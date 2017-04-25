
package net.imagej.ops.fopd.regularizer.tvhuber;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.regularizer.AbstractRegularizer;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation2DDescent;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.type.numeric.RealType;

/**
 * TV-Huber implementation for one 2D image. Where the L2-Norm in TV is replace
 * by the Huber-Norm. 
 * Huber_alpha(u) = 1/2 * u^2				, for |u| <= alpha 
 * 					alpha(|u| - 1/2 alpha)  , otherwise
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class TVHuber2D<T extends RealType<T>> extends AbstractRegularizer<T> {

	@SuppressWarnings("unchecked")
	public TVHuber2D(final OpService ops, final double lambda,
		final double alpha, final double descentStepSize)
	{
		this.ascent = ops.op(TVHuber2DAscent.class, SolverState.class, lambda,
			alpha);
		this.descent = ops.op(TotalVariation2DDescent.class, SolverState.class,
			descentStepSize);
	}
}
