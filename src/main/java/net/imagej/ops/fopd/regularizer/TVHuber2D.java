package net.imagej.ops.fopd.regularizer;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * TV-Huber implementation for one 2D image.
 * 
 * Where the L2-Norm in TV is replace by the Huber-Norm.
 * 
 * Huber_alpha(u) = 1/2 * u^2, for |u| <= alpha 
 * 					alpha(|u| - 1/2 alpha), otherwise
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class TVHuber2D<T extends RealType<T>> extends AbstractRegularizer<T> {

	@SuppressWarnings("unchecked")
	public TVHuber2D(final OpService ops, final double lambda, final double alpha, final double descentStepSize) {
		this.ascent = ops.op(TVHuber2DAscent.class, RandomAccessibleInterval.class, DualVariables.class, lambda, alpha);
		this.descent = ops.op(TotalVariation2DDescent.class, RandomAccessibleInterval.class, DualVariables.class,
				descentStepSize);
	}
}
