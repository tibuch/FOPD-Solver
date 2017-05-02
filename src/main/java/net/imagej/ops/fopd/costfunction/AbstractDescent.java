
package net.imagej.ops.fopd.costfunction;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;

/**
 * Implements the multi-view functionality. Every {@link CostFunction} has to
 * implement {@link AbstractDescent#doDescent(SolverState, int)} which then
 * will be called for each view.
 * 
 * @author Tim-Oliver Buchholz, Universtiy of Konstanz
 * @param <T>
 */
public abstract class AbstractDescent<T extends RealType<T>> extends
	AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements
	Descent<T>
{

	@Parameter
	protected LinearOperator<T>[] operator;
	@Parameter
	protected double stepSize;
	@Parameter
	protected OpService ops;

	public AbstractDescent() {
		super();
	}

	public SolverState<T> calculate(SolverState<T> input) {

		for (int i = 0; i < input.getNumViews(); i++) {
			doDescent(input, i);
		}
		return input;
	}

	public abstract void doDescent(final SolverState<T> input, final int i);

}
