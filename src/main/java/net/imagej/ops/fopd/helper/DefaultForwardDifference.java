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

package net.imagej.ops.fopd.helper;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.imagej.ops.OpService;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;

/**
 * Implementation of {@link ForwardDifference} for the given dimension d.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = ForwardDifference.class,
	description = "Forward difference for dimension d.",
	priority = Priority.HIGH_PRIORITY)
public class DefaultForwardDifference<T extends RealType<T>> extends
	AbstractUnaryHybridCF<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>>
	implements ForwardDifference<T>
{

	@Parameter
	private int dimension;

	@Parameter
	private OutOfBoundsFactory<T, RandomAccessibleInterval<T>> fac;

	@Parameter
	private OpService ops;

	@Parameter
	private ThreadService ts;
	
	private FinalInterval interval;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(
		RandomAccessibleInterval<T> input)
	{
		return (RandomAccessibleInterval<T>) ops.create().img(input);
	}

	public void compute(final RandomAccessibleInterval<T> input,
		final RandomAccessibleInterval<T> output)
	{

		if (interval == null) {
			init(input);
		}

		long dimensionMax = Long.MIN_VALUE;
		int dimensionArgMax = -1;

		int nDim = input.numDimensions();
		for ( int d = 0; d < nDim; ++d )
		{
			final long size = input.dimension( d );
			if ( d != dimension && size > dimensionMax )
			{
				dimensionMax = size;
				dimensionArgMax = d;
			}
		}

		final long stepSize = Math.max( dimensionMax / Runtime.getRuntime().availableProcessors(), 1 );
		final int numChunks = (int) (dimensionMax / stepSize);

		final ArrayList<Future<?>> futures = new ArrayList<Future<?>>();

			for (int i = 0 ; i < numChunks - 1; i++)
		{
			final long currentMin = i * stepSize;
			final long currentMax = currentMin + stepSize - 1;
			final long[] mins = new long[ nDim ];
			final long[] maxs = new long[ nDim ];
			input.min( mins );
			input.max( maxs );
			mins[ dimensionArgMax ] = currentMin;
			maxs[ dimensionArgMax ] = currentMax;
			final IntervalView< T > currentInterval = Views.interval( output, new FinalInterval( mins, maxs ) );
			
			futures.add(ts.run(new Runnable() {
				
				public void run() {
					gradientForwardDifference(Views.interval(Views.extend(input, fac),
							interval), currentInterval, dimension);
				}
			}));
			
		}
			final long currentMin = (numChunks - 1) * stepSize;
			final long currentMax = currentMin + stepSize - 1;
			final long[] mins = new long[ nDim ];
			final long[] maxs = new long[ nDim ];
			input.min( mins );
			input.max( maxs );
			mins[ dimensionArgMax ] = currentMin;
			maxs[ dimensionArgMax ] = currentMax;
			final IntervalView< T > currentInterval = Views.interval( output, new FinalInterval( mins, maxs ) );
		futures.add(ts.run(new Runnable() {
			
			public void run() {
				gradientForwardDifference(Views.interval(Views.extend(input, fac),
						interval), currentInterval, dimension);
			}
		}));
		
		
		for (final Future<?> future : futures) {
			try {
				future.get();
			}
			catch (final InterruptedException exc) {
				throw new RuntimeException(exc);
			}
			catch (final ExecutionException exc) {
				throw new RuntimeException(exc);
			}
		}
	}

	/**
	 * Since the actual object are not available during initialization, the
	 * subtractComputer and shift are created during the first call of
	 * {@link DefaultForwardDifference#compute(RandomAccessibleInterval, RandomAccessibleInterval)}
	 * .
	 * 
	 * @param input the input-object
	 */
	private void init(final RandomAccessibleInterval<T> input) {
		final long[] min = new long[input.numDimensions()];
		final long[] max = new long[input.numDimensions()];
		input.min(min);
		input.max(max);

		min[dimension] += 1;

		interval = new FinalInterval(min, max);
	}

	/**
	 * Compute the {@link ForwardDifference} of source in a particular
	 * dimension. Note: This implementation is based on
	 * https://github.com/imglib/imglib2-algorithm/blob/
	 * f44ed9e1781d43222b7fc4bb3ccf8b0a837c1b56/src/main/java/net/imglib2/
	 * algorithm/gradient/PartialDerivative.java
	 * 
	 * @param source source image, has to provide valid data in the interval of
	 *            the gradient image plus a one pixel border in dimension.
	 * @param gradient output image
	 * @param dim along which dimension the partial derivatives are computed
	 */
	private void gradientForwardDifference(final RandomAccessible<T> source,
		final RandomAccessibleInterval<T> gradient, final int dim)
	{
		final int n = gradient.numDimensions();

		final long[] min = new long[n];
		gradient.min(min);
		final long[] max = new long[n];
		gradient.max(max);
		final long[] shiftback = new long[n];
		for (int d = 0; d < n; ++d)
			shiftback[d] = min[d] - max[d];

		final RandomAccess<T> result = gradient.randomAccess();
		final RandomAccess<T> front = source.randomAccess(Intervals.translate(
			gradient, -1, dim));
		final RandomAccess<T> current = source.randomAccess(gradient);

		result.setPosition(min);
		front.setPosition(min);
		front.fwd(dim);
		current.setPosition(min);

		final long max0 = max[0];
		while (true) {
			// process pixel
			final T t = result.get();
			t.set(front.get());
			t.sub(current.get());

			// move to next pixel
			// check dimension 0 separately to avoid the loop over d in most
			// iterations
			if (result.getLongPosition(0) == max0) {
				if (n == 1) return;
				result.move(shiftback[0], 0);
				front.move(shiftback[0], 0);
				current.move(shiftback[0], 0);
				// now check the remaining dimensions
				for (int d = 1; d < n; ++d)
					if (result.getLongPosition(d) == max[d]) {
						result.move(shiftback[d], d);
						front.move(shiftback[d], d);
						current.move(shiftback[d], d);
						if (d == n - 1) return;
					}
					else {
						result.fwd(d);
						front.fwd(d);
						current.fwd(d);
						break;
					}
			}
			else {
				result.fwd(0);
				front.fwd(0);
				current.fwd(0);
			}
		}
	}
}
