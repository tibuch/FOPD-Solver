package net.imagej.ops.fopd.costfunction.deconvolution;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.filter.convolve.ConvolveFFTC;
import net.imagej.ops.filter.pad.PadInputFFTMethods;
import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.helper.DefaultL1Projector;
import net.imagej.ops.fopd.helper.DefaultL2Norm;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.map.MapBinaryInplace1s.IIAndIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.function.BinaryFunctionOp;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * L1-Deconvolution with known kernel of one 2D image: {@link Ascent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = Ascent.class)
public class L1Deconvolution2DAscent<T extends RealType<T>>
		extends AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements Ascent<T> {

	@Parameter
	private OpService ops;

	@Parameter
	private RandomAccessibleInterval<T> f;

	@Parameter
	private RandomAccessibleInterval<T> kernel;

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;
	private RandomAccessibleInterval<T> diff;

	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	private RandomAccessibleInterval<T> norm;

	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer;

	private IIAndIIParallel<T, T> inplaceMapper;

	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval, RandomAccessibleInterval<T>> convolver;

	private FinalDimensions paddedDims;

	@SuppressWarnings("rawtypes")
	private BinaryFunctionOp<RandomAccessibleInterval, Dimensions, RandomAccessibleInterval> padOp;

	private RandomAccessibleInterval<T> convolution;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getCostFunctionDV();
		
		if (mapperSubtract == null || mapperAdd == null || diff == null || norm == null || normComputer == null
				|| inplaceMapper == null || convolver == null) {
			init(dualVariables);
		}

		convolver.compute(padOp.calculate(input.getResultImage(0), paddedDims), convolution);

		mapperSubtract.compute(convolution, f, (IterableInterval<T>) diff);

		mapperAdd.compute(dualVariables.getDualVariable(0), diff, (IterableInterval<T>) dualVariables.getDualVariable(0));

		normComputer.compute(dualVariables.getAllDualVariables(), norm);
		inplaceMapper.mutate1((IterableInterval<T>) dualVariables.getDualVariable(0), (IterableInterval<T>) norm);
		
		return input;
	}

	@SuppressWarnings("unchecked")
	private void init(final DualVariables<T> input) {
		final long[] paddedSize = new long[2];
		paddedSize[0] = (int) input.getDualVariable(0).dimension(0) + (int) kernel.dimension(0) - 1;
		paddedSize[1] = (int) input.getDualVariable(0).dimension(1) + (int) kernel.dimension(1) - 1;

		paddedDims = new FinalDimensions(paddedSize);

		padOp = Functions.binary(ops, PadInputFFTMethods.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, Dimensions.class, true,
				new OutOfBoundsBorderFactory<T, RandomAccessibleInterval<T>>());

		final RandomAccessibleInterval<T> padKernel = ops.filter().padShiftFFTKernel(kernel, paddedDims);

		convolver = Computers.unary(ops, ConvolveFFTC.class, input.getDualVariable(0),
				padOp.calculate(input.getDualVariable(0), paddedDims), padKernel,
				ops.filter().createFFTOutput(paddedDims, new ComplexFloatType(), true), ops.filter().fft(padKernel),
				true, false);

		convolution = (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));

		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops, Ops.Math.Subtract.class,
				input.getType(), input.getType(), input.getType());
		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		final BinaryComputerOp<T, T, T> addComputer = Computers.binary(ops, Ops.Math.Add.class, input.getType(),
				input.getType(), input.getType());
		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(addComputer);
		diff = (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));

		norm = (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
		normComputer = Computers.unary(ops, DefaultL2Norm.class, RandomAccessibleInterval.class, RandomAccessibleInterval[].class);
		final BinaryInplace1Op<? super T, T, T> projector = Inplaces.binary1(ops, DefaultL1Projector.class,
				input.getType(), input.getType(), 1);
		inplaceMapper = (IIAndIIParallel<T, T>) ops.op(Map.class, IterableInterval.class, IterableInterval.class,
				BinaryInplace1Op.class);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) projector);
	}
}
