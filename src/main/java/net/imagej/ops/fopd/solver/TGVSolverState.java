
package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.regularizer.tgv.solver.TGVMinimizer2DSolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Specific implementation of {@link SolverState} for TGVSolver. 
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz.
 * @param <T>
 */
public class TGVSolverState<T extends RealType<T>> extends
	AbstractSolverState<T>
{

	private TGVMinimizer2DSolverState<T> tgvState;

	public TGVSolverState(final OpService ops,
		final RandomAccessibleInterval<T>[] images, final int numResults)
	{
		super(ops, images, numResults);

		this.tgvState = new TGVMinimizer2DSolverState<T>(ops, images);
	}

	@Override
	public SolverState<T> getSubSolverState(int i) {
		if (i > 0) {
			throw new ArrayIndexOutOfBoundsException(
				"This solver only depends on one other sovler.");
		}
		return tgvState;
	}
}
