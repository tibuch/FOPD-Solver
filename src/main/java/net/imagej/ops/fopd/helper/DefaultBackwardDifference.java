package net.imagej.ops.fopd.helper;

import net.imagej.ops.OpService;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Implementation of {@link BackwardDifference} for the given dimension d.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = BackwardDifference.class, description = "Backward difference for dimension d.", priority = Priority.HIGH_PRIORITY)
public class DefaultBackwardDifference<T extends RealType<T>>
		extends AbstractUnaryHybridCF<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>>
		implements BackwardDifference<T> {

	@Parameter
	private int dimension;
	
	@Parameter
	private OutOfBoundsFactory<T, RandomAccessibleInterval<T>> fac;

	@Parameter
	private OpService ops;
	
	/**
	 * Interval to extract the extended input.
	 */
	private FinalInterval interval;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(RandomAccessibleInterval<T> input) {
		return (RandomAccessibleInterval<T>) ops.create().img(input);
	}

	public void compute(RandomAccessibleInterval<T> input, RandomAccessibleInterval<T> output) {

		if (interval == null) {
			init(input);
		}

		gradientBackwardDifference(
				Views.interval(Views.extend(input, fac), interval), output,
				dimension);

	}
	
	/**
	 * Since the actual object are not available during initialization, the
	 * subtractComputer and shift are created during the first call of
	 * {@link DefaultBackwardDifference#compute(RandomAccessibleInterval, RandomAccessibleInterval)}
	 * .
	 * 
	 * @param input
	 *            the input-object
	 */
	private void init(final RandomAccessibleInterval<T> input) {
		final long[] min = new long[input.numDimensions()];
		final long[] max = new long[input.numDimensions()];
		input.min(min);
		input.max(max);

		min[dimension] -= 1;
		
		interval = new FinalInterval(min, max);
	}

	/**
	 * Compute the {@link BackwardDifference} of source in a particular
	 * dimension.
	 * 
	 * Note: This implementation is based on
	 * https://github.com/imglib/imglib2-algorithm/blob/f44ed9e1781d43222b7fc4bb3ccf8b0a837c1b56/src/main/java/net/imglib2/algorithm/gradient/PartialDerivative.java
	 * 
	 * @param source
	 *            source image, has to provide valid data in the interval of the
	 *            gradient image plus a one pixel border in dimension.
	 * @param gradient
	 *            output image
	 * @param dim
	 *            along which dimension the partial derivatives are computed
	 */
	private void gradientBackwardDifference(final RandomAccessible<T> source,
			final RandomAccessibleInterval<T> gradient, final int dim) {
		final int n = gradient.numDimensions();

		final long[] min = new long[n];
		gradient.min(min);
		final long[] max = new long[n];
		gradient.max(max);
		final long[] shiftback = new long[n];
		for (int d = 0; d < n; ++d)
			shiftback[d] = min[d] - max[d];

		final RandomAccess<T> result = gradient.randomAccess();
		final RandomAccess<T> back = source.randomAccess(Intervals.translate(gradient, 1, dim));
		final RandomAccess<T> current = source.randomAccess(gradient);

		result.setPosition(min);
		back.setPosition(min);
		back.bck(dim);
		current.setPosition(min);

		final long max0 = max[0];
		while (true) {
			// process pixel
			final T t = result.get();
			t.set(current.get());
			t.sub(back.get());

			// move to next pixel
			// check dimension 0 separately to avoid the loop over d in most
			// iterations
			if (result.getLongPosition(0) == max0) {
				if (n == 1)
					return;
				result.move(shiftback[0], 0);
				back.move(shiftback[0], 0);
				current.move(shiftback[0], 0);
				// now check the remaining dimensions
				for (int d = 1; d < n; ++d)
					if (result.getLongPosition(d) == max[d]) {
						result.move(shiftback[d], d);
						back.move(shiftback[d], d);
						current.move(shiftback[d], d);
						if (d == n - 1)
							return;
					} else {
						result.fwd(d);
						back.fwd(d);
						current.fwd(d);
						break;
					}
			} else {
				result.fwd(0);
				back.fwd(0);
				current.fwd(0);
			}
		}
	}
}
