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

package net.imagej.ops.fopd.regularizer.tgv.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.helper.DefaultDivergence2D;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Total Generalized Variation of one 2D image: {@link Descent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Descent.class)
public class TGVMinimizer2DDescent<T extends RealType<T>> extends
	AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements
	Descent<T>
{

	/**
	 * Descent-stepSize, this depends on the {@link CostFunction}.
	 */
	@Parameter
	private double stepSize;

	/**
	 * The {@link OpService}.
	 */
	@Parameter
	private OpService ops;

	/**
	 * Holds the sum of the divergence and the image which should be smoothed.
	 */
	private RandomAccessibleInterval<T> sum;

	/**
	 * Divergence computer.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryFunctionOp<RandomAccessibleInterval[], RandomAccessibleInterval> divComputer;

	/**
	 * Mapped add computer.
	 */
	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	/**
	 * Converter multiplying by stepSize.
	 */
	private Converter<T, T> converter;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getSubSolverState(0)
			.getRegularizerDV();

		if (mapperAdd == null || divComputer == null) {
			init(dualVariables);
		}

		mapperAdd.compute(divComputer.calculate(new RandomAccessibleInterval[] {
			dualVariables.getDualVariable(0), dualVariables.getDualVariable(
				1) }), input.getRegularizerDV().getDualVariable(0),
			(IterableInterval<T>) sum);

		mapperAdd.compute(input.getSubSolverState(0).getIntermediateResult(0),
			Converters.convert(sum, converter, input.getType()),
			(IterableInterval<T>) input.getSubSolverState(0)
				.getIntermediateResult(0));

		mapperAdd.compute(divComputer.calculate(new RandomAccessibleInterval[] {
			dualVariables.getDualVariable(1), dualVariables.getDualVariable(
				2) }), input.getRegularizerDV().getDualVariable(1),
			(IterableInterval<T>) sum);

		mapperAdd.compute(input.getSubSolverState(0).getIntermediateResult(1),
			Converters.convert(sum, converter, input.getType()),
			(IterableInterval<T>) input.getSubSolverState(0)
				.getIntermediateResult(1));

		return input;
	}

	@SuppressWarnings("unchecked")
	private void init(final DualVariables<T> input) {
		divComputer = Functions.unary(ops, DefaultDivergence2D.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval[].class);
		converter = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};
		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(Computers.binary(ops, Ops.Math.Add.class, input
			.getType(), input.getType(), input.getType()));

		sum = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));
	}
}
