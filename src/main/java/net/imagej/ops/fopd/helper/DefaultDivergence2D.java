package net.imagej.ops.fopd.helper;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * 2D implementation of {@link Divergence}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = Divergence.class, description = "2D Divergence.", priority = Priority.HIGH_PRIORITY)
public class DefaultDivergence2D<T extends RealType<T>>
		extends AbstractUnaryHybridCF<DualVariables<T>, RandomAccessibleInterval<T>> implements Divergence<T> {

	@Parameter
	private OpService ops;

	/**
	 * {@link BackwardDifference} computer along first dimension.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> bdComputerX;

	/**
	 * {@link BackwardDifference} computer along second dimension.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> bdComputerY;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(DualVariables<T> input) {
		return (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
	}

	@SuppressWarnings("unchecked")
	public void compute(DualVariables<T> input, RandomAccessibleInterval<T> output) {
		if (bdComputerX == null || bdComputerY == null) {
			init2();
		}
		add2(bdComputerX.calculate(input.getDualVariable(0)), bdComputerY.calculate(input.getDualVariable(1)), output);
	}

	private void init2() {
		bdComputerX = Functions.unary(ops, DefaultBackwardDifference.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, 0);
		bdComputerY = Functions.unary(ops, DefaultBackwardDifference.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, 1);
	}

	private void add2(final RandomAccessible<T> source0, final RandomAccessible<T> source1,
			final RandomAccessibleInterval<T> output) {
		final int n = source0.numDimensions();

		final long[] min = new long[n];
		output.min(min);
		final long[] max = new long[n];
		output.max(max);
		final long[] shiftback = new long[n];
		for (int d = 0; d < n; ++d)
			shiftback[d] = min[d] - max[d];

		final RandomAccess<T> result = output.randomAccess();
		final RandomAccess<T> s1 = source0.randomAccess();
		final RandomAccess<T> s2 = source1.randomAccess();

		result.setPosition(min);
		s1.setPosition(min);
		s2.setPosition(min);

		final long max0 = max[0];
		while (true) {
			// process pixel
			final T t = result.get();
			final double t1 = s1.get().getRealDouble();
			final double t2 = s2.get().getRealDouble();
			t.setReal(t1 + t2);

			// move to next pixel
			// check dimension 0 separately to avoid the loop over d in most
			// iterations
			if (result.getLongPosition(0) == max0) {
				if (n == 1)
					return;
				result.move(shiftback[0], 0);
				s1.move(shiftback[0], 0);
				s2.move(shiftback[0], 0);
				// now check the remaining dimensions
				for (int d = 1; d < n; ++d)
					if (result.getLongPosition(d) == max[d]) {
						result.move(shiftback[d], d);
						s1.move(shiftback[d], d);
						s2.move(shiftback[d], d);
						if (d == n - 1)
							return;
					} else {
						result.fwd(d);
						s1.fwd(d);
						s2.fwd(d);
						break;
					}
			} else {
				result.fwd(0);
				s1.fwd(0);
				s2.fwd(0);
			}
		}
	}
}
