package net.imagej.ops.fopd.operator;

import net.imagej.ops.OpService;
import net.imagej.ops.filter.convolve.ConvolveFFTC;
import net.imagej.ops.filter.pad.PadInputFFTMethods;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.function.BinaryFunctionOp;
import net.imagej.ops.special.function.Functions;
import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Fast convolver which holds an initiated computer with a set kernel.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanzf
 *
 * @param <T>
 */
@Plugin(type = LinearOperator.class)
public class FastConvolver<T extends RealType<T>> extends
		AbstractUnaryFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> implements LinearOperator<T> {

	@Parameter
	private RandomAccessibleInterval<T> kernel;

	@Parameter
	private OpService ops;

	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval, RandomAccessibleInterval<T>> convolver;

	private RandomAccessibleInterval<T> convolution;

	@SuppressWarnings("rawtypes")
	private BinaryFunctionOp<RandomAccessibleInterval, Dimensions, RandomAccessibleInterval> padOp;

	private FinalDimensions paddedDims;

	public RandomAccessibleInterval<T> calculate(RandomAccessibleInterval<T> input) {
		if (paddedDims == null || padOp == null || convolver == null || convolution == null) {
			init(input);
		}
		convolver.compute(padOp.calculate(input, paddedDims), convolution);
		return convolution;
	}

	@SuppressWarnings("unchecked")
	private void init(RandomAccessibleInterval<T> input) {
		final long[] paddedSize = new long[2];
		paddedSize[0] = (int) input.dimension(0) + (int) kernel.dimension(0) - 1;
		paddedSize[1] = (int) input.dimension(1) + (int) kernel.dimension(1) - 1;

		paddedDims = new FinalDimensions(paddedSize);

		padOp = Functions.binary(ops, PadInputFFTMethods.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, Dimensions.class, true,
				new OutOfBoundsBorderFactory<T, RandomAccessibleInterval<T>>());

		final RandomAccessibleInterval<T> padKernel = ops.filter().padShiftFFTKernel(kernel, paddedDims);

		convolver = Computers.unary(ops, ConvolveFFTC.class, input, padOp.calculate(input, paddedDims), padKernel,
				ops.filter().createFFTOutput(paddedDims, new ComplexFloatType(), true), ops.filter().fft(padKernel),
				true, false);

		convolution = (RandomAccessibleInterval<T>) ops.create().img(input);
	}
}
