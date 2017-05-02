
package net.imagej.ops.fopd.regularizer.tgv.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.regularizer.AbstractRegularizer;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.type.numeric.RealType;

/**
 * Total Generalized Variation as minimization problem for 3D images. TGV(u),
 * where u is the image which needs smoothing.
 * 
 * Ref: Bredies, Kristian, Karl Kunisch, and Thomas Pock.
 * "Total generalized variation." SIAM Journal on Imaging Sciences 3.3 (2010):
 * 492-526.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class TGVMinimizer3D<T extends RealType<T>> extends AbstractRegularizer<T> {

	@SuppressWarnings("unchecked")
	public TGVMinimizer3D(final OpService ops, final double beta, final double descentStepSize) {
		this.ascent = ops.op(TGVMinimizer3DAscent.class, SolverState.class, beta);
		this.descent = ops.op(TGVMinimizer3DDescent.class, SolverState.class, descentStepSize);
	}
}
