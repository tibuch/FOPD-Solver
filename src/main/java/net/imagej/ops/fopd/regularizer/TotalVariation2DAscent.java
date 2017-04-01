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
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@Plugin(type = Ascent.class)
public class TotalVariation2DAscent<T extends RealType<T>>
		extends AbstractUnaryHybridCF<DualVariables<T>, RandomAccessibleInterval<T>> implements Ascent<T> {

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
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> gradientX;

	/**
	 * The gradient computer in Y-direction.
	 */
	private UnaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval> gradientY;

	private RAIAndRAIToIIParallel<T, T, T> mapper;
	
	private IIAndIIParallel<T,T> inplaceMapper;

	private UnaryComputerOp<DualVariables, RandomAccessibleInterval> normComputer;

	public RandomAccessibleInterval<T> createOutput(DualVariables<T> input) {
		
		return (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
	}

	@SuppressWarnings("unchecked")
	public void compute(DualVariables<T> input, RandomAccessibleInterval<T> output) {

		if (gradientX == null || gradientY == null || mapper == null || norm == null) {
			init(input);
		}

		mapper.compute(input.getDualVariable(0), (RandomAccessibleInterval<T>) Converters
				.convert(gradientX.calculate(output), new Converter<T, T>() {

					public void convert(T in, T out) {
						out.setReal(in.getRealDouble() * stepSize);
					}
				}, input.getType()), (IterableInterval<T>) input.getDualVariable(0));

		mapper.compute(input.getDualVariable(1), (RandomAccessibleInterval<T>) Converters
				.convert(gradientY.calculate(output), new Converter<T, T>() {

					public void convert(T in, T out) {
						out.setReal(in.getRealDouble() * stepSize);
					}
				}, input.getType()), (IterableInterval<T>) input.getDualVariable(1));

		normComputer.compute(input, norm);
		
		inplaceMapper.mutate1((IterableInterval<T>)input.getDualVariable(0), (IterableInterval<T>)norm);
		inplaceMapper.mutate1((IterableInterval<T>)input.getDualVariable(1), (IterableInterval<T>)norm);
	}

	private void init(final DualVariables<T> input) {
		norm = (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
		gradientX = Functions.unary(ops, DefaultForwardDifference.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, 0);
		gradientY = Functions.unary(ops, DefaultForwardDifference.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, 1);

		final BinaryComputerOp<T, T, T> addComputer = Computers.binary(ops, Ops.Math.Add.class, input.getType(), input.getType(), input.getType());

		mapper = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapper.setOp(addComputer);
		
		inplaceMapper = (IIAndIIParallel<T,T>) ops.op(Map.class, IterableInterval.class, IterableInterval.class, BinaryInplace1Op.class);
		
		normComputer = Computers.unary(ops, DefaultL2Norm.class, RandomAccessibleInterval.class, DualVariables.class);
		final BinaryInplace1Op<? super T, T, T> projector = Inplaces.binary1(ops, DefaultL1Projector.class, input.getType(), input.getType(), lambda);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) projector);


	}
}
