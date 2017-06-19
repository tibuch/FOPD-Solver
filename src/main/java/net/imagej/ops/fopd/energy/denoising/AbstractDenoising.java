/*-
 * #%L
 * An implementation of the first-order primal-dual solver proposed by Antonin Chamoblle and Thomas Pock.
 * Ref.: Chambolle, Antonin, and Thomas Pock. "A first-order primal-dual algorithm for convex problems with applications to imaging." Journal of Mathematical Imaging and Vision 40.1 (2011): 120-145.
 * %%
 * Copyright (C) 2017 Tim-Oliver Buchholz, University of Konstanz
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.ops.fopd.energy.denoising;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.operator.Identity;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.solver.DefaultSolver;
import net.imagej.ops.fopd.solver.Solver;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;

/**
 * Initialize the {@link LinearOperator} for the denoising problem. Actual
 * {@link Solver}s have to implement
 * {@link AbstractDenoising#getSolverState(RandomAccessibleInterval[])},
 * {@link AbstractDenoising#getRegularizer(double)} and
 * {@link AbstractDenoising#getCostFunction(RandomAccessibleInterval[], LinearOperator[], LinearOperator[])}
 * .
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public abstract class AbstractDenoising<T extends RealType<T>> extends
	AbstractUnaryFunctionOp<RandomAccessibleInterval<T>[], RandomAccessibleInterval<T>>
{

	@Parameter
	protected int numIt;

	@Parameter
	protected OpService ops;

	public AbstractDenoising() {
		super();
	}

	@SuppressWarnings({ "unchecked" })
	public RandomAccessibleInterval<T> calculate(
		RandomAccessibleInterval<T>[] input)
	{

		final Regularizer<T> tgv = getRegularizer(input.length);

		final LinearOperator<T>[] ascentOperator =
			new LinearOperator[input.length];
		final LinearOperator<T>[] descentOperator =
			new LinearOperator[input.length];

		for (int i = 0; i < input.length; i++) {
			ascentOperator[i] = ops.op(Identity.class, input[i]);
			descentOperator[i] = ops.op(Identity.class, input[i]);
		}

		final CostFunction<T> cf = getCostFunction(input, ascentOperator,
			descentOperator);
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
