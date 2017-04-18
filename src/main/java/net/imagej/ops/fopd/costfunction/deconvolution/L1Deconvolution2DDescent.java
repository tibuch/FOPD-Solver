package net.imagej.ops.fopd.costfunction.deconvolution;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.filter.convolve.ConvolveFFTC;
import net.imagej.ops.filter.pad.PadInputFFTMethods;
import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.function.BinaryFunctionOp;
import net.imagej.ops.special.function.Functions;
import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * L1-Deconvolution with known kernel of one 2D image: {@link Descent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = Descent.class)
public class L1Deconvolution2DDescent<T extends RealType<T>>
		extends AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements Descent<T> {

	@Parameter
	private RandomAccessibleInterval<T> flippedKernel;

	@Parameter
	private double stepSize;

	@Parameter
	private OpService ops;

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;

	private Converter<T, T> converter;

	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval, RandomAccessibleInterval<T>> convolver;

	@SuppressWarnings("rawtypes")
	private BinaryFunctionOp<RandomAccessibleInterval, Dimensions, RandomAccessibleInterval> padOp;

	private RandomAccessibleInterval<T> convolution;

	private FinalDimensions paddedDims;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getCostFunctionDV();

		if (mapperSubtract == null) {
			init(dualVariables);
		}

		convolver.compute(padOp.calculate(dualVariables.getDualVariable(0), paddedDims), convolution);

		mapperSubtract.compute(input.getIntermediateResult(0), Converters.convert(convolution, converter, input.getType()),
				(IterableInterval<T>) input.getIntermediateResult(0));
		
		return input;
	}

	@SuppressWarnings("unchecked")
	private void init(DualVariables<T> input) {
		final long[] paddedSize = new long[2];
		paddedSize[0] = (int) input.getDualVariable(0).dimension(0) + (int) flippedKernel.dimension(0) - 1;
		paddedSize[1] = (int) input.getDualVariable(0).dimension(1) + (int) flippedKernel.dimension(1) - 1;

		paddedDims = new FinalDimensions(paddedSize);

		padOp = Functions.binary(ops, PadInputFFTMethods.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, Dimensions.class, true,
				new OutOfBoundsBorderFactory<T, RandomAccessibleInterval<T>>());

		final RandomAccessibleInterval<T> padKernel = ops.filter().padShiftFFTKernel(flippedKernel, paddedDims);

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

		converter = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};
	}

}
