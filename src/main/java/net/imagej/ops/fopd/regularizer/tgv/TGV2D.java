
package net.imagej.ops.fopd.regularizer.tgv;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.regularizer.AbstractRegularizer;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.type.numeric.RealType;

/**
 * Total Generalized Variation implementation for one 2D image. TGV(u), where u
 * is the image which needs smoothing.
 * 
 * Ref: Bredies, Kristian, Karl Kunisch, and Thomas Pock.
 * "Total generalized variation." SIAM Journal on Imaging Sciences 3.3 (2010):
 * 492-526.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class TGV2D<T extends RealType<T>> extends AbstractRegularizer<T> {

	@SuppressWarnings("unchecked")
	public TGV2D(final OpService ops, final double alpha, final double beta, final double descentStepSize) {
		this.ascent = ops.op(TGV2DAscent.class, SolverState.class, alpha, beta);
		this.descent = ops.op(TGV2DDescent.class, SolverState.class, descentStepSize);
	}
}
