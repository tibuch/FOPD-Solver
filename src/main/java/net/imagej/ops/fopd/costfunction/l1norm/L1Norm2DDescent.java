package net.imagej.ops.fopd.costfunction.l1norm;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * L1-Norm as costfunction of one 2D image: {@link Descent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = Descent.class)
public class L1Norm2DDescent<T extends RealType<T>>
		extends AbstractUnaryFunctionOp<SolverState<T>, SolverState<T>> implements Descent<T> {

	@Parameter
	private LinearOperator<T> operator;

	@Parameter
	private double stepSize;

	@Parameter
	private OpService ops;

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;

	private Converter<T, T> converter;

	@SuppressWarnings("unchecked")
	public SolverState<T> calculate(SolverState<T> input) {
		final DualVariables<T> dualVariables = input.getCostFunctionDV();

		if (mapperSubtract == null) {
			init(dualVariables);
		}

		mapperSubtract.compute(input.getIntermediateResult(0), Converters.convert(operator.calculate(dualVariables.getDualVariable(0)), converter, input.getType()),
				(IterableInterval<T>) input.getIntermediateResult(0));
		
		return input;
	}

	@SuppressWarnings("unchecked")
	private void init(DualVariables<T> input) {
		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops, Ops.Math.Subtract.class,
				input.getType(), input.getType(), input.getType());
		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		converter = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};
	}

}
