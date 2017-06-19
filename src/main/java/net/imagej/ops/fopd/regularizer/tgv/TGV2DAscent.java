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

package net.imagej.ops.fopd.regularizer.tgv;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.helper.DefaultForwardDifference;
import net.imagej.ops.fopd.helper.DefaultL1Projector;
import net.imagej.ops.fopd.helper.DefaultL2Norm;
import net.imagej.ops.fopd.regularizer.tgv.solver.TGVMinimizer2D;
import net.imagej.ops.fopd.solver.RegularizerSolver;
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
 * Total Generalized Variation of one 2D image: {@link Ascent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Ascent.class)
public class TGV2DAscent<T extends RealType<T>> extends
	AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements
	Ascent<T>
{

	/**
	 * The OpService.
	 */
	@Parameter
	private OpService ops;

	/**
	 * Alpha weight.
	 */
	@Parameter
	private double alpha;

	/**
	 * Beta weight.
	 */
	@Parameter
	private double beta;

	/**
	 * The ascent step-size.
	 */
	private final double stepSize = 1 / 3.0;

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
	 * Holds difference between current gradientX(resultImage) and the first
	 * result of the {@link TGVMinimizer2D}.
	 */
	private IterableInterval<T> diff0;

	/**
	 * Holds difference between current gradientY(resultImage) and the second
	 * result of the {@link TGVMinimizer2D}.
	 */
	private IterableInterval<T> diff1;

	/**
	 * Solver of the {@link TGVMinimizer2D} which has to do one iteration.
	 */
	private RegularizerSolver<T> tgvSolver;

	/**
	 * Add mapper.
	 */
	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	/**
	 * Inplace mapper to project the dual-variable.
	 */
	private IIAndIIParallel<T, T> inplaceMapper;

	/**
	 * Norm computer.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer;

	/**
	 * Converter which multiplies by stepSize.
	 */
	private Converter<T, T> c1;

	/**
	 * Converter which multiplies by stepSize.
	 */
	private Converter<T, T> c2;

	/**
	 * Subtract mapper.
	 */
	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(final SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getRegularizerDV();

		if (gradientX == null || gradientY == null || mapperAdd == null ||
			norm == null)
		{
			init(input);
		}

		mapperSubtract.compute(gradientX.calculate(input.getResultImage(0)),
			input.getSubSolverState(0).getResultImage(0), diff0);

		mapperAdd.compute(dualVariables.getDualVariable(0), Converters.convert(
			(RandomAccessibleInterval<T>) diff0, c1, input.getType()),
			(IterableInterval<T>) dualVariables.getDualVariable(0));

		mapperSubtract.compute(gradientY.calculate(input.getResultImage(0)),
			input.getSubSolverState(0).getResultImage(1), diff1);

		mapperAdd.compute(dualVariables.getDualVariable(1), Converters.convert(
			(RandomAccessibleInterval<T>) diff1, c2, input.getType()),
			(IterableInterval<T>) dualVariables.getDualVariable(1));

		normComputer.compute(dualVariables.getAllDualVariables(), norm);

		inplaceMapper.mutate1((IterableInterval<T>) dualVariables
			.getDualVariable(0), (IterableInterval<T>) norm);
		inplaceMapper.mutate1((IterableInterval<T>) dualVariables
			.getDualVariable(1), (IterableInterval<T>) norm);

		tgvSolver.calculate(input);

		return input;
	}

	@SuppressWarnings("unchecked")
	private void init(final SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getRegularizerDV();
		norm = (RandomAccessibleInterval<T>) ops.create().img(dualVariables
			.getDualVariable(0));
		diff0 = (IterableInterval<T>) ops.create().img(dualVariables
			.getDualVariable(0));
		diff1 = (IterableInterval<T>) ops.create().img(dualVariables
			.getDualVariable(0));

		gradientX = Functions.unary(ops, DefaultForwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>());
		gradientY = Functions.unary(ops, DefaultForwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 1,
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

		final BinaryComputerOp<T, T, T> addComputer = Computers.binary(ops,
			Ops.Math.Add.class, input.getType(), input.getType(), input
				.getType());

		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(addComputer);

		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops,
			Ops.Math.Subtract.class, input.getType(), input.getType(), input
				.getType());

		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		inplaceMapper = (IIAndIIParallel<T, T>) ops.op(Map.class,
			IterableInterval.class, IterableInterval.class,
			BinaryInplace1Op.class);

		normComputer = Computers.unary(ops, DefaultL2Norm.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval[].class);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) Inplaces.binary1(ops,
			DefaultL1Projector.class, input.getType(), input.getType(), alpha));

		tgvSolver = ops.op(RegularizerSolver.class, input.getSubSolverState(0),
			new TGVMinimizer2D<T>(ops, beta, 1 / 5.0), 1);
	}
}
