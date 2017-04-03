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

	private int numIterations;

	private double lambda;

	private RandomAccessibleInterval<T> image;

	private DualVariables<T> regularizerDV;

	private DualVariables<T> costFunctionDV;

	private RandomAccessibleInterval<T> tmp;

	@SuppressWarnings("unchecked")
	public TVL1DenoisingSolverState(final OpService ops, final int numIterations, final double lambda,
			final RandomAccessibleInterval<T> image) {
		this.numIterations = numIterations;
		this.lambda = lambda;
		this.image = image;

		this.regularizerDV = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(image),
				(RandomAccessibleInterval<T>) ops.create().img(image));
		this.costFunctionDV = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(image));

		this.tmp = (RandomAccessibleInterval<T>) ops.create().img(image);
	}

	public int getNumIterations() {
		return this.numIterations;
	}

	public double getLambda() {
		return this.lambda;
	}

	public RandomAccessibleInterval<T> getImage() {
		return this.image;
	}

	public DualVariables<T> getRegularizerDV() {
		return this.regularizerDV;
	}

	public DualVariables<T> getCostFunctionDV() {
		return this.costFunctionDV;
	}

	public T getType() {
		return image.randomAccess().get().createVariable();
	}

	public RandomAccessibleInterval<T> getIntermediateResult() {
		return this.tmp;
	}
}
