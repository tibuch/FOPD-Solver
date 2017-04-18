package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Specific implementation of {@link SolverState} for L1-TV-Denoising.
 * 
 * Energy: E(u) = lambda * TV(u) + |u - f|_1, where u is the denoised solution,
 * lambda is the smoothness weight and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz.
 *
 * @param <T>
 */
public class TVL1DenoisingSolverState<T extends RealType<T>> implements SolverState<T> {

	private DualVariables<T> regularizerDV;

	private DualVariables<T> costFunctionDV;

	private RandomAccessibleInterval<T> tmp;

	private RandomAccessibleInterval<T> result;

	private T type;

	@SuppressWarnings("unchecked")
	public TVL1DenoisingSolverState(final OpService ops, 
			final RandomAccessibleInterval<T> image) {

		this.regularizerDV = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(image),
				(RandomAccessibleInterval<T>) ops.create().img(image));
		this.costFunctionDV = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(image));

		this.tmp = (RandomAccessibleInterval<T>) ops.create().img(image);
		this.result = (RandomAccessibleInterval<T>) ops.create().img(image);
		this.type = image.randomAccess().get().createVariable();
	}
	
	public RandomAccessibleInterval<T> getResultImage(final int i) {
		if (i != 0) {
			throw new ArrayIndexOutOfBoundsException("Only one result available.");
		}
		return this.result;
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
		if (i != 0) {
			throw new ArrayIndexOutOfBoundsException("Only one intermediate result available.");
		}
		return this.tmp;
	}

	public SolverState<T> getSubSolverState(int i) {
		throw new NullPointerException("This denoising solver does not depend on another solver.");
	}

	public int numResultImages() {
		return 1;
	}

	public int numIntermediateResults() {
		return 1;
	}
}
