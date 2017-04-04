package net.imagej.ops.fopd.helper;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops.Stats.Sum;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.ops.special.hybrid.BinaryHybridCF;
import net.imagej.ops.transform.project.DefaultProjectParallel;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Implementation of {@link L2Norm}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = BinaryHybridCF.class)
public class DefaultL2Norm<T extends RealType<T>>
		extends AbstractUnaryHybridCF<DualVariables<T>, RandomAccessibleInterval<T>> implements L2Norm<T> {

	@Parameter
	private OpService ops;

	private DefaultProjectParallel<T, T> projector;

	private Converter<T, T> squareConverter;

	private Converter<T, T> sqrtConverter;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(final DualVariables<T> input) {
		return (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
	}

	public void compute(final DualVariables<T> input, final RandomAccessibleInterval<T> output) {
		int numDualVariables = input.getNumDualVariables();

		if (numDualVariables == 1) {
			norm1D(input.getDualVariable(0), output);
		} else if (numDualVariables == 2) {
			norm2D(input.getDualVariable(0), input.getDualVariable(1), output);
		} else if (numDualVariables == 3) {
			norm3D(input.getDualVariable(0), input.getDualVariable(1), input.getDualVariable(2), output);
		} else {
			normND(input, output);
		}
	}

	@SuppressWarnings("unchecked")
	private void normND(DualVariables<T> input, final RandomAccessibleInterval<T> output) {
		final RandomAccessibleInterval<T> stack = Views.stack(input.getAllDUalVariables());

		if (projector == null) {
			init(stack, (IterableInterval<T>) output, input.getNumDualVariables() - 1);
		}

		projector.compute(Converters.convert(stack, squareConverter, input.getType()), (IterableInterval<T>) output);

		Converters.convert(output, sqrtConverter, input.getType());
	}

	@SuppressWarnings("unchecked")
	private void init(final RandomAccessibleInterval<T> stack, final IterableInterval<T> output, final int d) {
		projector = ops.op(DefaultProjectParallel.class, IterableInterval.class, RandomAccessibleInterval.class,
				Computers.unary(ops, Sum.class, DoubleType.class, RandomAccessibleInterval.class), d);

		squareConverter = new Converter<T, T>() {

			public void convert(T input, T output) {
				final double value = input.getRealDouble();
				output.setReal(value * value);
			}

		};

		sqrtConverter = new Converter<T, T>() {

			public void convert(T input, T output) {
				final double value = input.getRealDouble();
				output.setReal(Math.sqrt(value));
			}

		};
	}

	private void norm1D(final RandomAccessible<T> source0, final RandomAccessibleInterval<T> norm) {
		final int n = source0.numDimensions();

		final long[] min = new long[n];
		norm.min(min);
		final long[] max = new long[n];
		norm.max(max);
		final long[] shiftback = new long[n];
		for (int d = 0; d < n; ++d)
			shiftback[d] = min[d] - max[d];

		final RandomAccess<T> result = norm.randomAccess();
		final RandomAccess<T> s1 = source0.randomAccess();

		result.setPosition(min);
		s1.setPosition(min);

		final long max0 = max[0];
		while (true) {
			// process pixel
			final T t = result.get();
			t.setReal(Math.abs(s1.get().getRealDouble()));

			// move to next pixel
			// check dimension 0 separately to avoid the loop over d in most
			// iterations
			if (result.getLongPosition(0) == max0) {
				if (n == 1)
					return;
				result.move(shiftback[0], 0);
				s1.move(shiftback[0], 0);
				// now check the remaining dimensions
				for (int d = 1; d < n; ++d)
					if (result.getLongPosition(d) == max[d]) {
						result.move(shiftback[d], d);
						s1.move(shiftback[d], d);
						if (d == n - 1)
							return;
					} else {
						result.fwd(d);
						s1.fwd(d);
						break;
					}
			} else {
				result.fwd(0);
				s1.fwd(0);
			}
		}
	}

	private void norm2D(final RandomAccessible<T> source0, final RandomAccessible<T> source1,
			final RandomAccessibleInterval<T> norm) {
		final int n = source0.numDimensions();

		final long[] min = new long[n];
		norm.min(min);
		final long[] max = new long[n];
		norm.max(max);
		final long[] shiftback = new long[n];
		for (int d = 0; d < n; ++d)
			shiftback[d] = min[d] - max[d];

		final RandomAccess<T> result = norm.randomAccess();
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
			t.setReal(Math.sqrt(t1 * t1 + t2 * t2));

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

	private void norm3D(final RandomAccessible<T> source0, final RandomAccessible<T> source1,
			final RandomAccessible<T> source2, final RandomAccessibleInterval<T> norm) {
		final int n = source0.numDimensions();

		final long[] min = new long[n];
		norm.min(min);
		final long[] max = new long[n];
		norm.max(max);
		final long[] shiftback = new long[n];
		for (int d = 0; d < n; ++d)
			shiftback[d] = min[d] - max[d];

		final RandomAccess<T> result = norm.randomAccess();
		final RandomAccess<T> s1 = source0.randomAccess();
		final RandomAccess<T> s2 = source1.randomAccess();
		final RandomAccess<T> s3 = source2.randomAccess();

		result.setPosition(min);
		s1.setPosition(min);
		s2.setPosition(min);
		s3.setPosition(min);

		final long max0 = max[0];
		while (true) {
			// process pixel
			final T t = result.get();
			final double t1 = s1.get().getRealDouble();
			final double t2 = s2.get().getRealDouble();
			final double t3 = s3.get().getRealDouble();
			t.setReal(Math.sqrt(t1 * t1 + t2 * t2 + t3 * t3));

			// move to next pixel
			// check dimension 0 separately to avoid the loop over d in most
			// iterations
			if (result.getLongPosition(0) == max0) {
				if (n == 1)
					return;
				result.move(shiftback[0], 0);
				s1.move(shiftback[0], 0);
				s2.move(shiftback[0], 0);
				s3.move(shiftback[0], 0);
				// now check the remaining dimensions
				for (int d = 1; d < n; ++d)
					if (result.getLongPosition(d) == max[d]) {
						result.move(shiftback[d], d);
						s1.move(shiftback[d], d);
						s2.move(shiftback[d], d);
						s3.move(shiftback[d], d);
						if (d == n - 1)
							return;
					} else {
						result.fwd(d);
						s1.fwd(d);
						s2.fwd(d);
						s3.fwd(d);
						break;
					}
			} else {
				result.fwd(0);
				s1.fwd(0);
				s2.fwd(0);
				s3.fwd(0);
			}
		}
	}
}
