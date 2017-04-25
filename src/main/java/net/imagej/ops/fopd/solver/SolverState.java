
package net.imagej.ops.fopd.solver;

import net.imagej.ops.fopd.DualVariables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Holds all variables needed by {@link Solver}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public interface SolverState<T extends RealType<T>> {

	/**
	 * @return the regularizer dual variables
	 */
	DualVariables<T> getRegularizerDV();

	/**
	 * @return the cost function dual variables
	 */
	DualVariables<T> getCostFunctionDV();

	/**
	 * @return an instance of the image type
	 */
	T getType();

	/**
	 * @return the intermediate result of the solver
	 */
	RandomAccessibleInterval<T> getIntermediateResult(final int i);

	SolverState<T> getSubSolverState(final int i);

	RandomAccessibleInterval<T> getResultImage(final int i);

	int getNumViews();

	int numResultImages();

	int numIntermediateResults();
}
