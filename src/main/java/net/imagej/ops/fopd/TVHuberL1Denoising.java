package net.imagej.ops.fopd;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.L1Denoising2D;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.TVHuber2D;
import net.imagej.ops.fopd.solver.DefaultSolver;
import net.imagej.ops.fopd.solver.TVHuberL1DenoisingSolverState;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * A 2D denoising algorithm which uses TV (with Huber-Norm instead of L2-Norm)
 * as {@link Regularizer} and takes the L1-Norm as {@link CostFunction}.
 * 
 * Energy: E(u) = lambda * TV-Huber(u)_alpha + |u - f|_1, where u is the denoised
 * solution, lambda is the smoothness weight and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
@Plugin(type = UnaryHybridCF.class)
public class TVHuberL1Denoising<T extends RealType<T>>
		extends AbstractUnaryHybridCF<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {

	@Parameter
	private OpService ops;

	@Parameter
	private double lambda;

	@Parameter
	private double alpha;

	@Parameter
	private int numIt;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(RandomAccessibleInterval<T> input) {
		return (RandomAccessibleInterval<T>) ops.create().img(input);
	}

	@SuppressWarnings({ "unchecked" })
	public void compute(RandomAccessibleInterval<T> input, RandomAccessibleInterval<T> uq) {

		final TVHuberL1DenoisingSolverState<T> state = new TVHuberL1DenoisingSolverState<T>(ops, numIt, lambda, alpha,
				input);

		final TVHuber2D<T> tv = new TVHuber2D<T>(ops, state.getLambda(), state.getAlpha(), 0.25);
		final L1Denoising2D<T> cf = new L1Denoising2D<T>(ops, input, 0.25);

		final DefaultSolver<T> solver = ops.op(DefaultSolver.class, uq, state, tv, cf);
		solver.compute(state, uq);
	}
}
