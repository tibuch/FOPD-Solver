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

package net.imagej.ops.fopd.regularizer.tv;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.helper.DefaultForwardDifference;
import net.imagej.ops.fopd.helper.DefaultL1Projector;
import net.imagej.ops.fopd.helper.DefaultL2Norm;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.map.MapBinaryInplace1s.IIAndIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Total Variation of one 2D image: {@link Ascent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Ascent.class)
public class TotalVariation3DAscent<T extends RealType<T>> extends
	AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements
	Ascent<T>
{

	/**
	 * The OpService.
	 */
	@Parameter
	private OpService ops;

	@Parameter
	private double lambda;

	/**
	 * The ascent step-size.
	 */
	private final double stepSize = 0.5;

	/**
	 * The norm of the input
	 */
	private RandomAccessibleInterval<T> norm;

	/**
	 * The gradient computer in X-direction.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> gradientX;

	/**
	 * The gradient computer in Y-direction.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> gradientY;
	
	/**
	 * The gradient computer in Z-direction.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> gradientZ;

	private RAIAndRAIToIIParallel<T, T, T> mapper;

	private IIAndIIParallel<T, T> inplaceMapper;

	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer;

	private Converter<T, T> c1;

	private Converter<T, T> c2;

	private Converter<T, T> c3;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getRegularizerDV();

		if (gradientX == null || gradientY == null || mapper == null ||
			norm == null)
		{
			init(dualVariables);
		}

		mapper.compute(dualVariables.getDualVariable(0),
			(RandomAccessibleInterval<T>) Converters.convert(gradientX
				.calculate(input.getResultImage(0)), c1, input.getType()),
			(IterableInterval<T>) dualVariables.getDualVariable(0));

		mapper.compute(dualVariables.getDualVariable(1),
			(RandomAccessibleInterval<T>) Converters.convert(gradientY
				.calculate(input.getResultImage(0)), c2, input.getType()),
			(IterableInterval<T>) dualVariables.getDualVariable(1));
		
		mapper.compute(dualVariables.getDualVariable(2),
				(RandomAccessibleInterval<T>) Converters.convert(gradientZ
					.calculate(input.getResultImage(0)), c3, input.getType()),
				(IterableInterval<T>) dualVariables.getDualVariable(2));

		normComputer.compute(dualVariables.getAllDualVariables(), norm);

		inplaceMapper.mutate1((IterableInterval<T>) dualVariables
			.getDualVariable(0), (IterableInterval<T>) norm);
		inplaceMapper.mutate1((IterableInterval<T>) dualVariables
			.getDualVariable(1), (IterableInterval<T>) norm);
		inplaceMapper.mutate1((IterableInterval<T>) dualVariables
				.getDualVariable(2), (IterableInterval<T>) norm);

		return input;
	}

	@SuppressWarnings("unchecked")
	private void init(final DualVariables<T> input) {
		norm = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));
		gradientX = Functions.unary(ops, DefaultForwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>());
		gradientY = Functions.unary(ops, DefaultForwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 1,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>());
		gradientZ = Functions.unary(ops, DefaultForwardDifference.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, 2,
				new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>());

		c1 = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};

		c2 = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};

		c3 = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};
		
		final BinaryComputerOp<T, T, T> addComputer = Computers.binary(ops,
			Ops.Math.Add.class, input.getType(), input.getType(), input
				.getType());

		mapper = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapper.setOp(addComputer);

		inplaceMapper = (IIAndIIParallel<T, T>) ops.op(Map.class,
			IterableInterval.class, IterableInterval.class,
			BinaryInplace1Op.class);

		normComputer = Computers.unary(ops, DefaultL2Norm.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval[].class);
		final BinaryInplace1Op<? super T, T, T> projector = Inplaces.binary1(
			ops, DefaultL1Projector.class, input.getType(), input.getType(),
			lambda);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) projector);

	}
}
