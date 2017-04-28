
package net.imagej.ops.fopd.costfunction.l1norm;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.AbstractCostFunction;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * L1-Norm as costfunction. 
 * 
 * Ref.: http://mathworld.wolfram.com/L1-Norm.html
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public class L1Norm<T extends RealType<T>> extends AbstractCostFunction<T> {

	@SuppressWarnings("unchecked")
	public L1Norm(final OpService ops,
		final RandomAccessibleInterval<T>[] input,
		final LinearOperator<T>[] operatorAscent,
		final LinearOperator<T>[] operatorDescent,
		final double descentStepSize)
	{
		this.ascent = ops.op(L1NormAscent.class, SolverState.class, input,
			operatorAscent);
		this.descent = ops.op(L1NormDescent.class, SolverState.class,
			operatorDescent, descentStepSize);
	}
}
