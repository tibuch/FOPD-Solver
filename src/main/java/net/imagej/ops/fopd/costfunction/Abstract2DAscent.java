
package net.imagej.ops.fopd.costfunction;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;

/**
 * Implements the multi-view functionality. Every {@link CostFunction} has to
 * implement {@link Abstract2DAscent#doAscent(SolverState, int)} which then will
 * be called for each view.
 * 
 * @author Tim-Oliver Buchholz, Universtiy of Konstanz
 * @param <T>
 */
public abstract class Abstract2DAscent<T extends RealType<T>> extends
	AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements
	Ascent<T>
{

	@Parameter
	protected OpService ops;

	@Parameter
	protected RandomAccessibleInterval<T>[] f;

	@Parameter
	protected LinearOperator<T>[] operator;

	public SolverState<T> calculate(SolverState<T> input) {
		for (int i = 0; i < input.getNumViews(); i++) {
			doAscent(input, i);
		}
		return input;
	}

	public abstract void doAscent(final SolverState<T> input, final int i);
}
