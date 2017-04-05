package net.imagej.ops.fopd;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.deconvolution.L1Deconvolution2D;
import net.imagej.ops.fopd.costfunction.denoising.L1Denoising2D;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.TotalVariation2D;
import net.imagej.ops.fopd.solver.DefaultSolver;
import net.imagej.ops.fopd.solver.TVL1Deconvolution2DSolverState;
import net.imagej.ops.special.hybrid.AbstractBinaryHybridCF;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

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
public class TVL1Deconvolution<T extends RealType<T>> extends
		AbstractBinaryHybridCF<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {

	@Parameter
	private OpService ops;

	@Parameter
	private double lambda;

	@Parameter
	private int numIt;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(RandomAccessibleInterval<T> image,
			RandomAccessibleInterval<T> kernel) {
		return (RandomAccessibleInterval<T>) ops.create().img(image);
	}

	@SuppressWarnings({ "unchecked" })
	public void compute(RandomAccessibleInterval<T> image, RandomAccessibleInterval<T> kernel,
			RandomAccessibleInterval<T> uq) {

		final TotalVariation2D<T> tv = new TotalVariation2D<T>(ops, lambda, 0.2);
		RandomAccessibleInterval<T> flippedKernel = ops.copy().rai(kernel);
		final L1Deconvolution2D<T> cf = new L1Deconvolution2D<T>(ops, image, kernel, Views.invertAxis(Views.invertAxis(flippedKernel, 0), 1), 0.2);

		final TVL1Deconvolution2DSolverState<T> state = new TVL1Deconvolution2DSolverState<T>(ops, numIt, lambda, image,
				kernel);

		final DefaultSolver<T> solver = ops.op(DefaultSolver.class, uq, state, tv, cf);
		solver.compute(state, uq);
	}
}
