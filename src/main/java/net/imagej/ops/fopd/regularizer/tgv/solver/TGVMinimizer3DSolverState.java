
package net.imagej.ops.fopd.regularizer.tgv.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.solver.AbstractSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * {@link SolverState} for {@link TGVMinimizer2D}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public class TGVMinimizer3DSolverState<T extends RealType<T>> extends
	AbstractSolverState<T>
{

	public TGVMinimizer3DSolverState(final OpService ops,
		final RandomAccessibleInterval<T>[] images)
	{
		super(ops, images, 3);
		regularizerDV = new DualVariables<T>(ops, images[0], 6);
	}

	@Override
	public RandomAccessibleInterval<T> getIntermediateResult(int i) {
		if (i > 1) {
			throw new ArrayIndexOutOfBoundsException(
				"This solver only has two intermediate results.");
		}
		return intermediateResults[i];
	}

	@Override
	public RandomAccessibleInterval<T> getResultImage(int i) {
		if (i > 1) {
			throw new ArrayIndexOutOfBoundsException(
				"This solver only has two results.");
		}
		return results[i];
	}
}
