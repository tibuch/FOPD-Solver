
package net.imagej.ops.fopd.energy.deconvolution;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.operator.FastConvolver;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.solver.DefaultSolver;
import net.imagej.ops.fopd.solver.Solver;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.special.function.AbstractBinaryFunctionOp;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.scijava.plugin.Parameter;

/**
 * Initialize the {@link LinearOperator} for the deconvolution problem. Actual
 * {@link Solver}s have to implement
 * {@link AbstractDeconvoltuion#getSolverState(RandomAccessibleInterval[])},
 * {@link AbstractDeconvoltuion#getRegularizer(double)} and
 * {@link AbstractDeconvoltuion#getCostFunction(RandomAccessibleInterval[], LinearOperator[], LinearOperator[])}
 * .
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public abstract class AbstractDeconvoltuion<T extends RealType<T>> extends
	AbstractBinaryFunctionOp<RandomAccessibleInterval<T>[], RandomAccessibleInterval<T>[], RandomAccessibleInterval<T>>
{

	@Parameter
	private int numIt;

	@Parameter
	protected OpService ops;

	public AbstractDeconvoltuion() {
		super();
	}

	@SuppressWarnings({ "unchecked" })
	public RandomAccessibleInterval<T> calculate(
		RandomAccessibleInterval<T>[] input,
		RandomAccessibleInterval<T>[] kernel)
	{

		if (input.length != kernel.length) {
			throw new IllegalArgumentException(
				"Number of input images differs from number of kernels.");
		}

		final Regularizer<T> tgv = getRegularizer(input.length);

		final LinearOperator<T>[] ascentConvolver =
			new LinearOperator[kernel.length];
		final LinearOperator<T>[] descentConvolver =
			new LinearOperator[kernel.length];

		for (int i = 0; i < kernel.length; i++) {
			ascentConvolver[i] = ops.op(FastConvolver.class, input[i],
				kernel[i]);
			if (kernel[i].numDimensions() == 2) {
				descentConvolver[i] = ops.op(FastConvolver.class, input[i], Views
					.invertAxis(Views.invertAxis(ops.copy().rai(kernel[i]), 0), 1));
			} else if (kernel[i].numDimensions() == 3) {
				descentConvolver[i] = ops.op(FastConvolver.class, input[i],
						Views.invertAxis(Views.invertAxis(Views.invertAxis(ops.copy().rai(kernel[i]), 0), 1), 2));
			}
		}

		final CostFunction<T> cf = getCostFunction(input, ascentConvolver,
			descentConvolver);
		final SolverState<T> state = getSolverState(input);

		final DefaultSolver<T> solver = ops.op(DefaultSolver.class, state, tgv,
			cf, numIt);
		solver.calculate(state);

		return state.getResultImage(0);
	}

	abstract SolverState<T> getSolverState(
		final RandomAccessibleInterval<T>[] input);

	abstract Regularizer<T> getRegularizer(final double numViews);

	abstract CostFunction<T> getCostFunction(
		final RandomAccessibleInterval<T>[] input,
		final LinearOperator<T>[] ascentOperator,
		final LinearOperator<T>[] descentOperator);

}
