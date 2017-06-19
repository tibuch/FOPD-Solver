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

package net.imagej.ops.fopd.costfunction;

import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;

/**
 * Descent-step is (currently) for all {@link CostFunction}s the same.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public class AbstractCostFunctionDescent<T extends RealType<T>> extends
	AbstractDescent<T>
{

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;
	private Converter<T, T> converter;

	@SuppressWarnings("unchecked")
	@Override
	public void doDescent(SolverState<T> input, int i) {
		final DualVariables<T> dualVariables = input.getCostFunctionDV();

		if (mapperSubtract == null) {
			init(dualVariables);
		}

		mapperSubtract.compute(input.getIntermediateResult(0), Converters
			.convert(operator[i].calculate(dualVariables.getDualVariable(i)),
				converter, input.getType()), (IterableInterval<T>) input
					.getIntermediateResult(0));
	}

	@SuppressWarnings("unchecked")
	private void init(DualVariables<T> input) {
		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops,
			Ops.Math.Subtract.class, input.getType(), input.getType(), input
				.getType());
		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		converter = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};
	}

}
