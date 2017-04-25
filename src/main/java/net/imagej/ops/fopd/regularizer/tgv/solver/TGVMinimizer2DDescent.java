
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
