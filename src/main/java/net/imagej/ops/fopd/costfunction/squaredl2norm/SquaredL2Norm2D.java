package net.imagej.ops.fopd.costfunction.squaredl2norm;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.AbstractCostFunction;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Squared L2-Norm as costfunction. 
 * 
 * Ref.: http://mathworld.wolfram.com/L2-Norm.html
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public class SquaredL2Norm2D<T extends RealType<T>> extends AbstractCostFunction<T> {

	@SuppressWarnings("unchecked")
	public SquaredL2Norm2D(final OpService ops, final RandomAccessibleInterval<T> image,
			final LinearOperator<T> operatorAscent, final LinearOperator<T> operatorDescent,
			final double descentStepSize) {
		this.ascent = ops.op(SquaredL2Norm2DAscent.class, SolverState.class, image, operatorAscent);
		this.descent = ops.op(SquaredL2Norm2DDescent.class, SolverState.class, operatorDescent, descentStepSize);
	}
}
