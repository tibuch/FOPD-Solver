package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

public class AbstractSolverState<T extends RealType<T>> implements SolverState<T> {

	protected DualVariables<T> regularizerDV;
	protected DualVariables<T> costFunctionDV;
	protected RandomAccessibleInterval<T>[] intermediateResults;
	protected RandomAccessibleInterval<T>[] results;
	protected T type;

	@SuppressWarnings("unchecked")
	public AbstractSolverState(final OpService ops, final RandomAccessibleInterval<T> image) {
		this.regularizerDV = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(image),
				(RandomAccessibleInterval<T>) ops.create().img(image));
		this.costFunctionDV = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(image));

		this.intermediateResults = new RandomAccessibleInterval[]{ ops.create().img(image) };
		this.results = new RandomAccessibleInterval[]{ ops.create().img(image) };
		this.type = image.randomAccess().get().createVariable();
	}

	public RandomAccessibleInterval<T> getResultImage(final int i) {
		if (i >= results.length) {
			throw new ArrayIndexOutOfBoundsException(
					"This solver has only " + results.length + " results.");
		}
		return this.results[0];
	}

	public DualVariables<T> getRegularizerDV() {
		return this.regularizerDV;
	}

	public DualVariables<T> getCostFunctionDV() {
		return this.costFunctionDV;
	}

	public T getType() {
		return this.type;
	}

	public RandomAccessibleInterval<T> getIntermediateResult(final int i) {
		if (i >= intermediateResults.length) {
			throw new ArrayIndexOutOfBoundsException(
					"This solver has only " + intermediateResults.length + " intermediate results.");
		}
		return this.intermediateResults[0];
	}

	public SolverState<T> getSubSolverState(int i) {
		throw new ArrayIndexOutOfBoundsException("This solver has no sub-solver.");
	}

	public int numResultImages() {
		return this.results.length;
	}

	public int numIntermediateResults() {
		return this.intermediateResults.length;
	}

}