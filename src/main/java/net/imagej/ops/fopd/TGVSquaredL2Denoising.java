package net.imagej.ops.fopd;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.squaredl2norm.SquaredL2Norm2D;
import net.imagej.ops.fopd.operator.Identity;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.tgv.TGV2D;
import net.imagej.ops.fopd.solver.DefaultSolver;
import net.imagej.ops.fopd.solver.TGVSolverState;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * A 2D denoising algorithm which uses TV as {@link Regularizer} and takes the
 * L1-Norm as {@link CostFunction}.
 * 
 * Energy: E(u) = lambda * TV(u) + |u - f|_1, where u is the denoised solution,
 * lambda is the smoothness weight and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
@Plugin(type = UnaryHybridCF.class)
public class TGVSquaredL2Denoising<T extends RealType<T>>
		extends AbstractUnaryFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {

	@Parameter
	private OpService ops;

	@Parameter
	private double alpha;

	@Parameter
	private double beta;

	@Parameter
	private int numIt;

	@SuppressWarnings({ "unchecked" })
	public RandomAccessibleInterval<T> calculate(RandomAccessibleInterval<T> input) {

		final TGV2D<T> tgv = new TGV2D<T>(ops, alpha, beta, 0.25);
		final SquaredL2Norm2D<T> cf = new SquaredL2Norm2D<T>(ops, input, ops.op(Identity.class, input),
				ops.op(Identity.class, input), 0.25);

		final TGVSolverState<T> state = new TGVSolverState<T>(ops, input);

		final DefaultSolver<T> solver = ops.op(DefaultSolver.class, state, tgv, cf, numIt);
		return solver.calculate(state);
	}
}
