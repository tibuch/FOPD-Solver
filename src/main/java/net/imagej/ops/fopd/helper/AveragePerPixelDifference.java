package net.imagej.ops.fopd.helper;

import net.imagej.ops.Op;
import net.imagej.ops.special.function.AbstractBinaryFunctionOp;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

/**
 * Computes the average difference per pixel of two {@link RandomAccessibleInterval}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
@Plugin(type = Op.class)
public class AveragePerPixelDifference<T extends RealType<T>>
		extends AbstractBinaryFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>, double[]> {

	public double[] calculate(RandomAccessibleInterval<T> input1, RandomAccessibleInterval<T> input2) {
		return averagePerPixel(input1, input2);
	}

	private double[] averagePerPixel(final RandomAccessibleInterval<T> source0,
			final RandomAccessibleInterval<T> source1) {
		double numPix = source0.dimension(1);
		for (int i = 1; i < source0.numDimensions(); i++) {
			numPix *= source0.dimension(i);
		}
		double minDiff = 0;
		double maxDiff = 0;
		double sum = 0;
		final int n = source0.numDimensions();

		final long[] min = new long[n];
		source0.min(min);
		final long[] max = new long[n];
		source0.max(max);
		final long[] shiftback = new long[n];
		for (int d = 0; d < n; ++d)
			shiftback[d] = min[d] - max[d];

		final RandomAccess<T> s1 = source0.randomAccess();
		final RandomAccess<T> s2 = source1.randomAccess();

		s1.setPosition(min);
		s2.setPosition(min);

		final long max0 = max[0];
		while (true) {
			// process pixel
			final double t1 = s1.get().getRealDouble();
			final double t2 = s2.get().getRealDouble();
			double diff = Math.abs(t1 - t2);
			sum += diff;
			minDiff = diff < minDiff ? diff : minDiff;
			maxDiff = diff > maxDiff ? diff : maxDiff;

			// move to next pixel
			// check dimension 0 separately to avoid the loop over d in most
			// iterations
			if (s1.getLongPosition(0) == max0) {
				if (n == 1)
					return new double[]{minDiff, sum / numPix, maxDiff};
				s1.move(shiftback[0], 0);
				s2.move(shiftback[0], 0);
				// now check the remaining dimensions
				for (int d = 1; d < n; ++d)
					if (s1.getLongPosition(d) == max[d]) {
						s1.move(shiftback[d], d);
						s2.move(shiftback[d], d);
						if (d == n - 1)
							return new double[]{minDiff, sum / numPix, maxDiff};
					} else {
						s1.fwd(d);
						s2.fwd(d);
						break;
					}
			} else {
				s1.fwd(0);
				s2.fwd(0);
			}
		}
	}
}
