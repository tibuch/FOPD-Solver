package net.imagej.ops.fopd;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.deconvolution.L1Deconvolution2D;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber2D;
import net.imagej.ops.fopd.solver.DefaultSolver;
import net.imagej.ops.fopd.solver.TVL1Deconvolution2DSolverState;
import net.imagej.ops.special.function.AbstractBinaryFunctionOp;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * A 2D deconvolved algorithm which uses TV-Huber as {@link Regularizer} and
 * takes the L1-Norm as {@link CostFunction}.
 * 
 * Energy: E(u) = lambda * TV_Huber(u) + |u*k - f|_1, where u is the deconvolved
 * solution, lambda is the smoothness weight, k is the known kernel, * is the
 * convolution operator and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
@Plugin(type = UnaryHybridCF.class)
public class TVHuberL1Deconvolution<T extends RealType<T>> extends
		AbstractBinaryFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {

	@Parameter
	private OpService ops;

	@Parameter
	private double lambda;

	@Parameter
	private double alpha;

	@Parameter
	private int numIt;

	@SuppressWarnings({ "unchecked" })
	public RandomAccessibleInterval<T> calculate(RandomAccessibleInterval<T> image,
			RandomAccessibleInterval<T> kernel) {

		final TVHuber2D<T> tv = new TVHuber2D<T>(ops, lambda, alpha, 0.2);
		RandomAccessibleInterval<T> flippedKernel = ops.copy().rai(kernel);
		final L1Deconvolution2D<T> cf = new L1Deconvolution2D<T>(ops, image, kernel,
				Views.invertAxis(Views.invertAxis(flippedKernel, 0), 1), 0.2);

		final TVL1Deconvolution2DSolverState<T> state = new TVL1Deconvolution2DSolverState<T>(ops, image);

		final DefaultSolver<T> solver = ops.op(DefaultSolver.class, state, tv, cf, numIt);
		solver.calculate(state);

		return state.getResultImage(0);
	}
}
