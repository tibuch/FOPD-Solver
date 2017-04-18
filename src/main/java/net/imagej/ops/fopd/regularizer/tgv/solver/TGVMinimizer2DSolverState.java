package net.imagej.ops.fopd.regularizer.tgv.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * {@link SolverState} for {@link TGVMinimizer2D}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public class TGVMinimizer2DSolverState<T extends RealType<T>> implements SolverState<T> {

	private RandomAccessibleInterval<T>[] intermediateResults;

	private RandomAccessibleInterval<T>[] results;

	private DualVariables<T> regularizerDV;

	private T type;

	@SuppressWarnings("unchecked")
	public TGVMinimizer2DSolverState(final OpService ops, final RandomAccessibleInterval<T> image) {

		regularizerDV = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(image),
				(RandomAccessibleInterval<T>) ops.create().img(image),
				(RandomAccessibleInterval<T>) ops.create().img(image));

		this.type = image.randomAccess().get().createVariable();

		intermediateResults = new RandomAccessibleInterval[2];
		intermediateResults[0] = (RandomAccessibleInterval<T>) ops.create().img(image);
		intermediateResults[1] = (RandomAccessibleInterval<T>) ops.create().img(image);

		this.results = new RandomAccessibleInterval[2];
		results[0] = (RandomAccessibleInterval<T>) ops.create().img(image);
		results[1] = (RandomAccessibleInterval<T>) ops.create().img(image);
	}

	public DualVariables<T> getRegularizerDV() {
		return regularizerDV;
	}

	public DualVariables<T> getCostFunctionDV() {
		throw new NullPointerException("This solver has no cost-function.");
	}

	public T getType() {
		return this.type;
	}

	public RandomAccessibleInterval<T> getIntermediateResult(int i) {
		if (i > 1) {
			throw new ArrayIndexOutOfBoundsException("This solver only has two intermediate results.");
		}
		return intermediateResults[i];
	}

	public SolverState<T> getSubSolverState(int i) {
		throw new NullPointerException("There is no sub-solver available.");
	}

	public RandomAccessibleInterval<T> getResultImage(int i) {
		if (i > 1) {
			throw new ArrayIndexOutOfBoundsException("This solver only has two results.");
		}
		return results[i];
	}

	public int numResultImages() {
		return results.length;
	}

	public int numIntermediateResults() {
		return intermediateResults.length;
	}

}
