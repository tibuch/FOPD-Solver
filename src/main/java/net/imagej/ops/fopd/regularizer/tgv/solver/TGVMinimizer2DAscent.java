
package net.imagej.ops.fopd.regularizer.tgv.solver;

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
 * Total Generalized Variation of one 2D image: {@link Ascent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Ascent.class)
public class TGVMinimizer2DAscent<T extends RealType<T>> extends
	AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements
	Ascent<T>
{

	/**
	 * The OpService.
	 */
	@Parameter
	private OpService ops;

	/**
	 * Weight-factor.
	 */
	@Parameter
	private double beta;

	/**
	 * StepSize for the ascent-step.
	 */
	private final double stepSizeTGV = 1 / 2.0;

	/**
	 * Holds the gradient of the first image in x-direction.
	 */
	private RandomAccessibleInterval<T> g1xTGV;

	/**
	 * Holds the gradient of the first image in y-direction.
	 */
	private RandomAccessibleInterval<T> g1yTGV;

	/**
	 * Holds the gradient of the second image in x-direction.
	 */
	private RandomAccessibleInterval<T> g2xTGV;

	/**
	 * Holds the gradient of the second image in y-direction.
	 */
	private RandomAccessibleInterval<T> g2yTGV;

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
	 * Add computer.
	 */
	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	/**
	 * Norm computer.
	 */
	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer;

	/**
	 * Inplace mapper to project the dual variables back.
	 */
	private IIAndIIParallel<T, T> inplaceMapper;

	/**
	 * Converter multiplying by stepSizeTGV.
	 */
	private Converter<T, T> c1;

	/**
	 * Converter multiplying by 0.5*stepSizeTGV.
	 */
	private Converter<T, T> c2;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getSubSolverState(0)
			.getRegularizerDV();

		if (gradientX == null || gradientY == null || mapperAdd == null ||
			normComputer == null || norm == null || inplaceMapper == null)
		{
			init(input.getRegularizerDV());
		}

		g1xTGV = gradientX.calculate(input.getSubSolverState(0).getResultImage(
			0));
		g1yTGV = gradientY.calculate(input.getSubSolverState(0).getResultImage(
			0));

		g2xTGV = gradientX.calculate(input.getSubSolverState(0).getResultImage(
			1));
		g2yTGV = gradientY.calculate(input.getSubSolverState(0).getResultImage(
			1));

		mapperAdd.compute(dualVariables.getDualVariable(0), Converters.convert(
			g1xTGV, c1, input.getType()), (IterableInterval<T>) dualVariables
				.getDualVariable(0));
		mapperAdd.compute(dualVariables.getDualVariable(1), Converters.convert(
			g1yTGV, c2, input.getType()), (IterableInterval<T>) dualVariables
				.getDualVariable(1));
		mapperAdd.compute(dualVariables.getDualVariable(1), Converters.convert(
			g2xTGV, c2, input.getType()), (IterableInterval<T>) dualVariables
				.getDualVariable(1));
		mapperAdd.compute(dualVariables.getDualVariable(2), Converters.convert(
			g2yTGV, c1, input.getType()), (IterableInterval<T>) dualVariables
				.getDualVariable(2));

		normComputer.compute(new RandomAccessibleInterval[] { dualVariables
			.getDualVariable(0), dualVariables.getDualVariable(1), dualVariables
				.getDualVariable(1), dualVariables.getDualVariable(2) }, norm);

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
		g1xTGV = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));
		g1yTGV = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));
		g2xTGV = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));
		g2yTGV = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));
		gradientX = Functions.unary(ops, DefaultForwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>());
		gradientY = Functions.unary(ops, DefaultForwardDifference.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval.class, 1,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>());

		c1 = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSizeTGV);
			}
		};

		c2 = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSizeTGV * 0.5);
			}
		};

		final BinaryComputerOp<T, T, T> addComputer = Computers.binary(ops,
			Ops.Math.Add.class, input.getType(), input.getType(), input
				.getType());

		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(addComputer);

		inplaceMapper = (IIAndIIParallel<T, T>) ops.op(Map.class,
			IterableInterval.class, IterableInterval.class,
			BinaryInplace1Op.class);

		normComputer = Computers.unary(ops, DefaultL2Norm.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval[].class);
		final BinaryInplace1Op<? super T, T, T> projector = Inplaces.binary1(
			ops, DefaultL1Projector.class, input.getType(), input.getType(),
			beta);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) projector);

	}
}
