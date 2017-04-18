package net.imagej.ops.fopd;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.kldivergence.KLDivergence2D;
import net.imagej.ops.fopd.operator.Identity;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber2D;
import net.imagej.ops.fopd.solver.DefaultSolver;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * A 2D denoising algorithm which uses TV (with Huber-Norm instead of L2-Norm)
 * as {@link Regularizer} and takes the L1-Norm as {@link CostFunction}.
 * 
 * Energy: E(u) = lambda * TV-Huber(u)_alpha + |u - f|_1, where u is the
 * denoised solution, lambda is the smoothness weight and f is the observed
 * image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
@Plugin(type = UnaryHybridCF.class)
public class TVHuberKLDivDenoising<T extends RealType<T>>
		extends AbstractUnaryFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {

	@Parameter
	private OpService ops;

	@Parameter
	private double lambda;

	@Parameter
	private double alpha;

	@Parameter
	private int numIt;

	@SuppressWarnings({ "unchecked" })
	public RandomAccessibleInterval<T> calculate(RandomAccessibleInterval<T> input) {

		final DefaultSolverState<T> state = new DefaultSolverState<T>(ops,
				input);

		final TVHuber2D<T> tv = new TVHuber2D<T>(ops, lambda, alpha, 0.25);
		final KLDivergence2D<T> cf = new KLDivergence2D<T>(ops, input, ops.op(Identity.class, input), ops.op(Identity.class, input), 0.25);

		final DefaultSolver<T> solver = ops.op(DefaultSolver.class, state, tv, cf, numIt);
		return solver.calculate(state);
	}
}
