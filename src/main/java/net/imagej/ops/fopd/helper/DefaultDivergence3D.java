
package net.imagej.ops.fopd.helper;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.imagej.ops.OpService;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.outofbounds.OutOfBoundsConstantValueFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import org.scijava.Priority;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;

/**
 * 3D implementation of {@link Divergence}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Divergence.class, description = "3D Divergence.",
	priority = Priority.HIGH_PRIORITY)
public class DefaultDivergence3D<T extends RealType<T>> extends
	AbstractUnaryHybridCF<RandomAccessibleInterval<T>[], RandomAccessibleInterval<T>>
	implements Divergence<T>
{

	@Parameter
	private OpService ops;
	
	@Parameter
	private ThreadService ts;

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

	/**
	 * {@link BackwardDifference} computer along third dimension.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> bdComputerZ;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(
		RandomAccessibleInterval<T>[] input)
	{
		return (RandomAccessibleInterval<T>) ops.create().img(input[0]);
	}

	@SuppressWarnings("unchecked")
	public void compute(final RandomAccessibleInterval<T>[] input,
		RandomAccessibleInterval<T> output)
	{
		if (bdComputerX == null || bdComputerY == null || bdComputerZ == null) {
			init(input);
		}

		long dimensionMax = Long.MIN_VALUE;
		int dimensionArgMax = -1;

		int nDim = input[0].numDimensions();
		for ( int d = 0; d < nDim; ++d )
		{
			final long size = input[0].dimension( d );
			if ( size > dimensionMax )
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
			input[0].min( mins );
			input[0].max( maxs );
			mins[ dimensionArgMax ] = currentMin;
			maxs[ dimensionArgMax ] = currentMax;
			final IntervalView< T > currentInterval = Views.interval( output, new FinalInterval( mins, maxs ) );
			
			futures.add(ts.run(new Runnable() {
				
				public void run() {
					add3(bdComputerX.calculate(input[0]), bdComputerY.calculate(input[1]),
							bdComputerZ.calculate(input[2]), currentInterval);
				}
			}));
			
		}
			final long currentMin = (numChunks - 1) * stepSize;
			final long currentMax = currentMin + stepSize - 1;
			final long[] mins = new long[ nDim ];
			final long[] maxs = new long[ nDim ];
			input[0].min( mins );
			input[0].max( maxs );
			mins[ dimensionArgMax ] = currentMin;
			maxs[ dimensionArgMax ] = currentMax;
			final IntervalView< T > currentInterval = Views.interval( output, new FinalInterval( mins, maxs ) );
		futures.add(ts.run(new Runnable() {
			
			public void run() {
				add3(bdComputerX.calculate(input[0]), bdComputerY.calculate(input[1]), bdComputerZ.calculate(input[2]),
						currentInterval);
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

	private void init(final RandomAccessibleInterval<T>[] input) {
		final T type = input[0].randomAccess().get().createVariable();
		bdComputerX = Functions.unary(ops, DefaultBackwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 0,
			new OutOfBoundsConstantValueFactory<T, RandomAccessibleInterval<T>>(
				type));
		bdComputerY = Functions.unary(ops, DefaultBackwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 1,
			new OutOfBoundsConstantValueFactory<T, RandomAccessibleInterval<T>>(
				type));
		bdComputerZ = Functions.unary(ops, DefaultBackwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 2,
			new OutOfBoundsConstantValueFactory<T, RandomAccessibleInterval<T>>(
				type));
	}

	private void add3(final RandomAccessible<T> source0,
		final RandomAccessible<T> source1, final RandomAccessible<T> source2,
		final RandomAccessibleInterval<T> output)
	{
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
			t.setReal(t1 + t2 + t3);

			// move to next pixel
			// check dimension 0 separately to avoid the loop over d in most
			// iterations
			if (result.getLongPosition(0) == max0) {
				if (n == 1) return;
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
						if (d == n - 1) return;
					}
					else {
						result.fwd(d);
						s1.fwd(d);
						s2.fwd(d);
						s3.fwd(d);
						break;
					}
			}
			else {
				result.fwd(0);
				s1.fwd(0);
				s2.fwd(0);
				s3.fwd(0);
			}
		}
	}
}
