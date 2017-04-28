
package net.imagej.ops.fopd.costfunction;

import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;

/**
 * Descent-step is (currently) for all {@link CostFunction}s the same.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public class AbstractCostFunctionDescent<T extends RealType<T>> extends
	AbstractDescent<T>
{

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;
	private Converter<T, T> converter;

	@SuppressWarnings("unchecked")
	@Override
	public void doDescent(SolverState<T> input, int i) {
		final DualVariables<T> dualVariables = input.getCostFunctionDV();

		if (mapperSubtract == null) {
			init(dualVariables);
		}

		mapperSubtract.compute(input.getIntermediateResult(0), Converters
			.convert(operator[i].calculate(dualVariables.getDualVariable(i)),
				converter, input.getType()), (IterableInterval<T>) input
					.getIntermediateResult(0));
	}

	@SuppressWarnings("unchecked")
	private void init(DualVariables<T> input) {
		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops,
			Ops.Math.Subtract.class, input.getType(), input.getType(), input
				.getType());
		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		converter = new Converter<T, T>() {

			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * stepSize);
			}
		};
	}

}
