package net.imagej.ops.fopd.regularizer.tv;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.DualVariables;
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
 * Abstract Total Variation of one 2D image: {@link Descent} Step.
 * 
 * Note: Is the same for TV and TV-Huber.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = Descent.class)
public abstract class AbstractTV2DDescent<T extends RealType<T>>
		extends AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements Descent<T> {

	@Parameter
	private double stepSize;

	@Parameter
	private OpService ops;
	@SuppressWarnings("rawtypes")
	private UnaryFunctionOp<RandomAccessibleInterval[], RandomAccessibleInterval> divComputer;

	private BinaryComputerOp<T, T, T> addComputer;

	private RAIAndRAIToIIParallel<T, T, T> mapper;

	private Converter<T, T> converter;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getRegularizerDV();

		if (mapper == null || divComputer == null) {
			init(dualVariables);
		}

		mapper.compute(input.getIntermediateResult(0),
				Converters.convert(divComputer.calculate(dualVariables.getAllDualVariables()), converter, input.getType()),
				(IterableInterval<T>) input.getIntermediateResult(0));
		return input;
	}

	@SuppressWarnings("unchecked")
	private void init(final DualVariables<T> input) {
		divComputer = Functions.unary(ops, DefaultDivergence2D.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval[].class);
		converter = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};
		addComputer = Computers.binary(ops, Ops.Math.Add.class, input.getType(), input.getType(), input.getType());
		mapper = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapper.setOp(addComputer);

	}
}
