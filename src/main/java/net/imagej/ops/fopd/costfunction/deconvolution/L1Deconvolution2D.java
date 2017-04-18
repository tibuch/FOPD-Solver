package net.imagej.ops.fopd.costfunction.deconvolution;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.AbstractCostFunction;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Deconvolution with L1-Norm of one 2D image.
 * 
 * costfunction: |k*u - f|_1, where u is the solution, f is the noisy
 * observation, k the known kernel and * denotes the convolution operator.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public class L1Deconvolution2D<T extends RealType<T>> extends AbstractCostFunction<T> {

	@SuppressWarnings("unchecked")
	public L1Deconvolution2D(final OpService ops, final RandomAccessibleInterval<T> image,
			final RandomAccessibleInterval<T> kernel, final RandomAccessibleInterval<T> flippedKernel,
			final double descentStepSize) {
		this.ascent = ops.op(L1Deconvolution2DAscent.class, SolverState.class, image,
				kernel);
		this.descent = ops.op(L1Deconvolution2DDescent.class, SolverState.class,
				flippedKernel, descentStepSize);
	}
}
