
package net.imagej.ops.fopd.regularizer.tvhuber;

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
import net.imagej.ops.map.MapUnaryComputers.IIToIIParallel;
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
 * TV-Huber of one 2D image: {@link Ascent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Ascent.class)
public class TVHuber2DAscent<T extends RealType<T>> extends
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

	@Parameter
	private double alpha;

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

	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	private IIAndIIParallel<T, T> inplaceMapper;

	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer;

	private IIToIIParallel<T, T> mapperDivide;

	private Converter<T, T> c1;

	private Converter<T, T> c2;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getRegularizerDV();

		if (gradientX == null || gradientY == null || mapperAdd == null ||
			norm == null)
		{
			init(dualVariables);
		}

		mapperAdd.compute(dualVariables.getDualVariable(0),
			(RandomAccessibleInterval<T>) Converters.convert(gradientX
				.calculate(input.getResultImage(0)), c1, input.getType()),
			(IterableInterval<T>) dualVariables.getDualVariable(0));

		mapperAdd.compute(dualVariables.getDualVariable(1),
			(RandomAccessibleInterval<T>) Converters.convert(gradientY
				.calculate(input.getResultImage(0)), c2, input.getType()),
			(IterableInterval<T>) dualVariables.getDualVariable(1));

		mapperDivide.compute((IterableInterval<T>) dualVariables
			.getDualVariable(0), (IterableInterval<T>) dualVariables
				.getDualVariable(0));
		mapperDivide.compute((IterableInterval<T>) dualVariables
			.getDualVariable(1), (IterableInterval<T>) dualVariables
				.getDualVariable(1));

		normComputer.compute(dualVariables.getAllDualVariables(), norm);

		inplaceMapper.mutate1((IterableInterval<T>) dualVariables
			.getDualVariable(0), (IterableInterval<T>) norm);
		inplaceMapper.mutate1((IterableInterval<T>) dualVariables
			.getDualVariable(1), (IterableInterval<T>) norm);

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

		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(Computers.binary(ops, Ops.Math.Add.class, input
			.getType(), input.getType(), input.getType()));

		final T divider = input.getType();
		divider.setReal(1 + alpha * stepSize);
		mapperDivide = (IIToIIParallel<T, T>) ops.op(Map.class,
			IterableInterval.class, IterableInterval.class,
			UnaryComputerOp.class);
		mapperDivide.setOp(Computers.unary(ops, Ops.Math.Divide.class, input
			.getType(), divider));

		inplaceMapper = (IIAndIIParallel<T, T>) ops.op(Map.class,
			IterableInterval.class, IterableInterval.class,
			BinaryInplace1Op.class);

		normComputer = Computers.unary(ops, DefaultL2Norm.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval[].class);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) Inplaces.binary1(ops,
			DefaultL1Projector.class, input.getType(), input.getType(),
			lambda));

	}
}
