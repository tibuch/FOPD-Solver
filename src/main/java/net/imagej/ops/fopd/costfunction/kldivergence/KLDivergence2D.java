package net.imagej.ops.fopd.costfunction.kldivergence;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.AbstractCostFunction;
import net.imagej.ops.fopd.operator.LinearOperator;
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
public class KLDivergence2D<T extends RealType<T>> extends AbstractCostFunction<T> {

	@SuppressWarnings("unchecked")
	public KLDivergence2D(final OpService ops, final RandomAccessibleInterval<T> image,
			final LinearOperator<T> operatorAscent, final LinearOperator<T> operatorDescent,
			final double descentStepSize) {
		this.ascent = ops.op(KLDivergence2DAscent.class, SolverState.class, image, operatorAscent);
		this.descent = ops.op(KLDivergence2DDescent.class, SolverState.class, operatorDescent, descentStepSize);
	}
}
