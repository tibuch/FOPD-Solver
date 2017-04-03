package net.imagej.ops.fopd.regularizer;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.helper.DefaultForwardDifference;
import net.imagej.ops.fopd.helper.DefaultL1Projector;
import net.imagej.ops.fopd.helper.DefaultL2Norm;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.map.MapBinaryInplace1s.IIAndIIParallel;
import net.imagej.ops.map.MapIIAndIIInplaceParallel;
import net.imagej.ops.map.MapIIInplaceParallel;
import net.imagej.ops.map.MapUnaryComputers.IIToIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imagej.ops.special.inplace.Inplaces;
import net.imagej.ops.special.inplace.UnaryInplaceOp;
import net.imagej.options.OptionsMemoryAndThreads;
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
 *
 * @param <T>
 */
@Plugin(type = Ascent.class)
public class TVHuber2DAscent<T extends RealType<T>>
		extends AbstractUnaryHybridCF<DualVariables<T>, RandomAccessibleInterval<T>> implements Ascent<T> {

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
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> gradientX;

	/**
	 * The gradient computer in Y-direction.
	 */
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> gradientY;

	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	private IIAndIIParallel<T, T> inplaceMapper;

	private UnaryComputerOp<DualVariables, RandomAccessibleInterval> normComputer;

	private IIToIIParallel<T, T> mapperDivide;

	public RandomAccessibleInterval<T> createOutput(DualVariables<T> input) {

		return (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
	}

	@SuppressWarnings("unchecked")
	public void compute(DualVariables<T> input, RandomAccessibleInterval<T> output) {

		if (gradientX == null || gradientY == null || mapperAdd == null || norm == null) {
			init(input);
		}

		mapperAdd.compute(input.getDualVariable(0),
				(RandomAccessibleInterval<T>) Converters.convert(gradientX.calculate(output), new Converter<T, T>() {

					public void convert(T in, T out) {
						out.setReal(in.getRealDouble() * stepSize);
					}
				}, input.getType()), (IterableInterval<T>) input.getDualVariable(0));

		mapperAdd.compute(input.getDualVariable(1),
				(RandomAccessibleInterval<T>) Converters.convert(gradientY.calculate(output), new Converter<T, T>() {

					public void convert(T in, T out) {
						out.setReal(in.getRealDouble() * stepSize);
					}
				}, input.getType()), (IterableInterval<T>) input.getDualVariable(1));

		mapperDivide.compute((IterableInterval<T>)input.getDualVariable(0), (IterableInterval<T>)input.getDualVariable(0));
		mapperDivide.compute((IterableInterval<T>)input.getDualVariable(1), (IterableInterval<T>)input.getDualVariable(1));
		
		normComputer.compute(input, norm);

		inplaceMapper.mutate1((IterableInterval<T>) input.getDualVariable(0), (IterableInterval<T>) norm);
		inplaceMapper.mutate1((IterableInterval<T>) input.getDualVariable(1), (IterableInterval<T>) norm);
	}

	private void init(final DualVariables<T> input) {
		norm = (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
		gradientX = Functions.unary(ops, DefaultForwardDifference.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, 0,
				new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>());
		gradientY = Functions.unary(ops, DefaultForwardDifference.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, 1,
				new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>());

		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(Computers.binary(ops, Ops.Math.Add.class, input.getType(),
				input.getType(), input.getType()));
		
		final T divider = input.getType();
		divider.setReal(1 + alpha * stepSize);
		mapperDivide = (IIToIIParallel<T, T>) ops.op(Map.class, IterableInterval.class, IterableInterval.class,
				UnaryComputerOp.class);
		mapperDivide.setOp((UnaryComputerOp<T, T>) Computers.unary(ops, Ops.Math.Divide.class,
				input.getType(), divider));

		inplaceMapper = (IIAndIIParallel<T, T>) ops.op(Map.class, IterableInterval.class, IterableInterval.class,
				BinaryInplace1Op.class);

		normComputer = Computers.unary(ops, DefaultL2Norm.class, RandomAccessibleInterval.class, DualVariables.class);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) Inplaces.binary1(ops, DefaultL1Projector.class,
				input.getType(), input.getType(), lambda));

	}
}
