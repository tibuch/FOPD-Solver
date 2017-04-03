package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Specific implementation of {@link SolverState} for L1-TVHuber-Denoising.
 * 
 * Energy: E(u) = lambda * TV-Huber(u)_alpha + |u - f|_1, where u is the denoised solution,
 * lambda is the smoothness weight and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz.
 *
 * @param <T>
 */
public class TVHuberL1DenoisingSolverState<T extends RealType<T>> implements SolverState<T> {

	private int numIterations;

	private double lambda;
	
	private double alpha;

	private RandomAccessibleInterval<T> image;

	private DualVariables<T> regularizerDV;

	private DualVariables<T> costFunctionDV;

	private RandomAccessibleInterval<T> tmp;

	@SuppressWarnings("unchecked")
	public TVHuberL1DenoisingSolverState(final OpService ops, final int numIterations, final double lambda, final double alpha,
			final RandomAccessibleInterval<T> image) {
		this.numIterations = numIterations;
		this.lambda = lambda;
		this.alpha = alpha;
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
	
	public double getAlpha() {
		return this.alpha;
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
