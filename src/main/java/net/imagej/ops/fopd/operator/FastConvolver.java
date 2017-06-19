/*-
 * #%L
 * An implementation of the first-order primal-dual solver proposed by Antonin Chamoblle and Thomas Pock.
 * Ref.: Chambolle, Antonin, and Thomas Pock. "A first-order primal-dual algorithm for convex problems with applications to imaging." Journal of Mathematical Imaging and Vision 40.1 (2011): 120-145.
 * %%
 * Copyright (C) 2017 Tim-Oliver Buchholz, University of Konstanz
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
 * @param <T>
 */
@Plugin(type = LinearOperator.class)
public class FastConvolver<T extends RealType<T>> extends
	AbstractUnaryFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>>
	implements LinearOperator<T>
{

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

	public RandomAccessibleInterval<T> calculate(
		RandomAccessibleInterval<T> input)
	{
		if (paddedDims == null || padOp == null || convolver == null ||
			convolution == null)
		{
			init(input);
		}
		convolver.compute(padOp.calculate(input, paddedDims), convolution);
		return convolution;
	}

	@SuppressWarnings("unchecked")
	private void init(RandomAccessibleInterval<T> input) {
		final int ndim = input.numDimensions();
		final long[] paddedSize = new long[ndim];
		
		for (int i = 0; i < ndim; i++) {
			paddedSize[i] = (int) input.dimension(i) + (int) kernel.dimension(i) - 1;
		}

		paddedDims = new FinalDimensions(paddedSize);

		padOp = Functions.binary(ops, PadInputFFTMethods.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class,
			Dimensions.class, true,
			new OutOfBoundsBorderFactory<T, RandomAccessibleInterval<T>>());

		final RandomAccessibleInterval<T> padKernel = ops.filter()
			.padShiftFFTKernel(kernel, paddedDims);

		convolver = Computers.unary(ops, ConvolveFFTC.class, input, padOp
			.calculate(input, paddedDims), padKernel, ops.filter()
				.createFFTOutput(paddedDims, new ComplexFloatType(), true), ops
					.filter().fft(padKernel), true, false);

		convolution = (RandomAccessibleInterval<T>) ops.create().img(input);
	}
}
