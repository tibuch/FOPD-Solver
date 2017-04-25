
package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Default implementation of {@link SolverState}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz.
 * @param <T>
 */
public class DefaultSolverState<T extends RealType<T>> extends
	AbstractSolverState<T>
{

	public DefaultSolverState(final OpService ops,
		final RandomAccessibleInterval<T>[] images, final int numResults)
	{
		super(ops, images, numResults);
	}
}
