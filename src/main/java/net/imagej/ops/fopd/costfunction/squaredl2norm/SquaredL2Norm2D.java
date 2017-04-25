
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
 * @param <T>
 */
public class SquaredL2Norm2D<T extends RealType<T>> extends
	AbstractCostFunction<T>
{

	@SuppressWarnings("unchecked")
	public SquaredL2Norm2D(final OpService ops,
		final RandomAccessibleInterval<T>[] input,
		final LinearOperator<T>[] ascentConvolver,
		final LinearOperator<T>[] descentConvolver,
		final double descentStepSize)
	{
		this.ascent = ops.op(SquaredL2Norm2DAscent.class, SolverState.class,
			input, ascentConvolver);
		this.descent = ops.op(SquaredL2Norm2DDescent.class, SolverState.class,
			descentConvolver, descentStepSize);
	}
}
