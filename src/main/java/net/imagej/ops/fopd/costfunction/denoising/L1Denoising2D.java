package net.imagej.ops.fopd.costfunction.denoising;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.AbstractCostFunction;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Denoising with L1-Norm for one 2D image.
 * 
 * costfunction: |u - f|_1, where u is the solution and f is the noisy
 * observation.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public class L1Denoising2D<T extends RealType<T>> extends AbstractCostFunction<T> {

	@SuppressWarnings("unchecked")
	public L1Denoising2D(final OpService ops, final RandomAccessibleInterval<T> image, final double descentStepSize) {
		this.ascent = ops.op(L1DenoisingAscent.class, SolverState.class, image);
		this.descent = ops.op(L1DenoisingDescent.class, SolverState.class,
				descentStepSize);
	}
}
