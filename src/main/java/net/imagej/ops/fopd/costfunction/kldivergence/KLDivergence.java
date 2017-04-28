
package net.imagej.ops.fopd.costfunction.kldivergence;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.AbstractCostFunction;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Kullback-Leibler-Divergence as costfunction. 
 * 
 * Ref.: https://de.wikipedia.org/wiki/Kullback-Leibler-Divergenz
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public class KLDivergence<T extends RealType<T>> extends
	AbstractCostFunction<T>
{

	@SuppressWarnings("unchecked")
	public KLDivergence(final OpService ops,
		final RandomAccessibleInterval<T>[] image,
		final LinearOperator<T>[] operatorAscent,
		final LinearOperator<T>[] operatorDescent,
		final double descentStepSize)
	{
		this.ascent = ops.op(KLDivergenceAscent.class, SolverState.class,
			image, operatorAscent);
		this.descent = ops.op(KLDivergenceDescent.class, SolverState.class,
			operatorDescent, descentStepSize);
	}
}
