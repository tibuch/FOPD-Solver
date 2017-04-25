
package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.helper.Default01Clipper;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.map.MapBinaryComputers.RAIAndIIToRAIParallel;
import net.imagej.ops.map.MapIIInplaceParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imagej.ops.special.inplace.Inplaces;
import net.imagej.ops.special.inplace.UnaryInplaceOp;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * This {@link Solver} is an implementation of the algorithm proposed by:
 * Chambolle, Antonin, and Thomas Pock.
 * "A first-order primal-dual algorithm for convex problems with applications to imaging."
 * Journal of Mathematical Imaging and Vision 40.1 (2011): 120-145.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Solver.class)
public class DefaultSolver<T extends RealType<T>>
		extends AbstractUnaryFunctionOp<SolverState<T>, RandomAccessibleInterval<T>> implements Solver<T> {

	@Parameter
	private Regularizer<T> regularizer;

	@Parameter
	private CostFunction<T> costfunction;

	@Parameter
	private int numIterations;

	@Parameter
	private OpService ops;

	/**
	 * CopyComputer to copy images.
	 */
	private UnaryComputerOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> copyComputer;

	/**
	 * Subtract computer.
	 */
	private RAIAndIIToRAIParallel<T, T, T> mapperSubtract;

	/**
	 * [0, 1]-Clipper.
	 */
	private MapIIInplaceParallel<T> clipperMapper;

	private Converter<T, T> converter;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> calculate(SolverState<T> input) {

		if (mapperSubtract == null || copyComputer == null || converter == null || clipperMapper == null) {
			initComputers(input);
		}

		// only needed because of a matcher bug.
		final RandomAccessibleInterval<T> tmp = (RandomAccessibleInterval<T>) ops.create()
				.img(input.getIntermediateResult(0));

		for (int i = 0; i < numIterations; i++) {

			regularizer.getAscent().calculate(input);
			costfunction.getAscent().calculate(input);

			copyComputer.compute(input.getIntermediateResult(0), input.getResultImage(0));

			regularizer.getDescent().calculate(input);
			costfunction.getDescent().calculate(input);

			// mapperSubtract.compute(2*u, uq, uq) does not work, because wrong
			// map is chosen later on.
			mapperSubtract.compute(
					Converters.convert(input.getIntermediateResult(0), converter, input.getRegularizerDV().getType()),
					(IterableInterval<T>) input.getResultImage(0), tmp);

			copyComputer.compute(tmp, input.getResultImage(0));
			clipperMapper.mutate((IterableInterval<T>) input.getResultImage(0));
		}
		return input.getResultImage(0);
	}

	@SuppressWarnings("unchecked")
	private void initComputers(final SolverState<T> input) {
		final T type = input.getType();

		mapperSubtract = (RAIAndIIToRAIParallel<T, T, T>) ops.op(Map.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, IterableInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(Computers.binary(ops, Ops.Math.Subtract.class, type, type, type));

		copyComputer = Computers.unary(ops, Ops.Copy.RAI.class, input.getResultImage(0), input.getResultImage(0));

		converter = new Converter<T, T>() {

			public void convert(T in, T output) {
				output.setReal(in.getRealDouble() * 2.0);
			}

		};

		clipperMapper = (MapIIInplaceParallel<T>) ops.op(Map.class, IterableInterval.class, UnaryInplaceOp.class);
		clipperMapper.setOp((UnaryInplaceOp<T, T>) Inplaces.unary(ops, Default01Clipper.class, type));
	}
}
