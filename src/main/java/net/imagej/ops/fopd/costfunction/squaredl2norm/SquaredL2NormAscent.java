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

package net.imagej.ops.fopd.costfunction.squaredl2norm;

import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.costfunction.AbstractAscent;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.map.MapUnaryComputers.IIToIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

/**
 * L2-Norm as costfunction of one 2D image: {@link Ascent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Ascent.class)
public class SquaredL2NormAscent<T extends RealType<T>> extends
	AbstractAscent<T>
{

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;

	private RandomAccessibleInterval<T> diff;

	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	private IIToIIParallel<T, T> inplaceMapper;

	@Override
	@SuppressWarnings("unchecked")
	public void doAscent(final SolverState<T> input, final int i) {
		final DualVariables<T> dualVariables = input.getCostFunctionDV();

		if (mapperSubtract == null || mapperAdd == null || diff == null ||
			inplaceMapper == null)
		{
			init(dualVariables);
		}

		mapperSubtract.compute(operator[i].calculate(input.getResultImage(0)),
			f[i], (IterableInterval<T>) diff);

		mapperAdd.compute(dualVariables.getDualVariable(i), diff,
			(IterableInterval<T>) dualVariables.getDualVariable(i));

		inplaceMapper.compute((IterableInterval<T>) dualVariables
			.getDualVariable(i), (IterableInterval<T>) dualVariables
				.getDualVariable(i));
	}

	@SuppressWarnings("unchecked")
	private void init(final DualVariables<T> input) {

		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops,
			Ops.Math.Subtract.class, input.getType(), input.getType(), input
				.getType());
		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		final BinaryComputerOp<T, T, T> addComputer = Computers.binary(ops,
			Ops.Math.Add.class, input.getType(), input.getType(), input
				.getType());
		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(addComputer);
		diff = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));

		T divider = input.getType();
		divider.setReal(2.0);
		inplaceMapper = (IIToIIParallel<T, T>) ops.op(Map.class,
			IterableInterval.class, IterableInterval.class,
			UnaryComputerOp.class);
		inplaceMapper.setOp(Computers.unary(ops, Ops.Math.Divide.class, input
			.getType(), divider));
	}
}
