
package net.imagej.ops.fopd.regularizer.tv;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.regularizer.AbstractRegularizer;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.type.numeric.RealType;

/**
 * Total Variation implementation for one 2D image. TV(u), where u is the image
 * which needs smoothing.
 * 
 * Ref: Rowland, Todd. "Total Variation." From MathWorld--A Wolfram Web
 * Resource, created by Eric W. Weisstein.
 * http://mathworld.wolfram.com/TotalVariation.html
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class TotalVariation2D<T extends RealType<T>> extends AbstractRegularizer<T> {

	@SuppressWarnings("unchecked")
	public TotalVariation2D(final OpService ops, final double lambda, final double descentStepSize) {
		this.ascent = ops.op(TotalVariation2DAscent.class, SolverState.class, lambda);
		this.descent = ops.op(TotalVariation2DDescent.class, SolverState.class, descentStepSize);
	}
}
