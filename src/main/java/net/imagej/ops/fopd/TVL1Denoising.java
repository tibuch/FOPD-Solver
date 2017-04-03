package net.imagej.ops.fopd;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.L1DenoisingAscent;
import net.imagej.ops.fopd.costfunction.L1DenoisingDescent;
import net.imagej.ops.fopd.helper.Default01Clipper;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.TotalVariation2DAscent;
import net.imagej.ops.fopd.regularizer.TotalVariation2DDescent;
import net.imagej.ops.map.MapBinaryComputers.RAIAndIIToRAIParallel;
import net.imagej.ops.map.MapIIInplaceParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imagej.ops.special.inplace.Inplaces;
import net.imagej.ops.special.inplace.UnaryInplaceOp;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;

/**
 * A 2D denoising algorithm which uses TV as {@link Regularizer} and takes the
 * L1-Norm as {@link CostFunction}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
@Plugin(type = UnaryHybridCF.class)
public class TVL1Denoising<T extends RealType<T>>
		extends AbstractUnaryHybridCF<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {

	@Parameter
	private OpService ops;

	@Parameter
	private double lambda;

	@Parameter
	private int numIt;

	private TotalVariation2DAscent<T> ascentTV;

	private TotalVariation2DDescent<T> descentTV;

	private L1DenoisingAscent<T> ascentL1Denoising;

	private L1DenoisingDescent<T> descentL1Denoising;

	private RAIAndIIToRAIParallel<T, T, T> mapperSubtract;

	private UnaryComputerOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> copyComputer;

	private MapIIInplaceParallel<T> inplaceMapper;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(RandomAccessibleInterval<T> input) {
		return (RandomAccessibleInterval<T>) ops.create().img(input);
	}

	@SuppressWarnings({ "unchecked" })
	public void compute(RandomAccessibleInterval<T> input, RandomAccessibleInterval<T> uq) {

		initComputers(input, uq);

		final DualVariables<T> p = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(input),
				(RandomAccessibleInterval<T>) ops.create().img(input));
		final DualVariables<T> q = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(input));

		final RandomAccessibleInterval<T> u = (RandomAccessibleInterval<T>) ops.create().img(uq);

		final RandomAccessibleInterval<T> tmp = (RandomAccessibleInterval<T>) ops.create().img(u);

		for (int i = 0; i < numIt; i++) {

			ascentTV.compute(p, uq);
			ascentL1Denoising.compute(q, uq);

			copyComputer.compute(u, uq);

			descentTV.compute(p, u);
			descentL1Denoising.compute(q, u);

			inplaceMapper.mutate((IterableInterval<T>) u);
			// mapperSubtract.compute(2*u, uq, uq) does not work, because wrong
			// map is chosen later on.
			mapperSubtract.compute(Converters.convert(u, new Converter<T, T>() {

				public void convert(T input, T output) {
					output.setReal(input.getRealDouble() * 2.0);
				}

			}, p.getType()), (IterableInterval<T>) uq, tmp);

			copyComputer.compute(tmp, uq);
		}
	}

	@SuppressWarnings("unchecked")
	private void initComputers(final RandomAccessibleInterval<T> input, final RandomAccessibleInterval<T> output) {

		final T type = output.randomAccess().get();

		ascentTV = ops.op(TotalVariation2DAscent.class, RandomAccessibleInterval.class, DualVariables.class, lambda);
		descentTV = ops.op(TotalVariation2DDescent.class, RandomAccessibleInterval.class, DualVariables.class, 0.25);
		ascentL1Denoising = ops.op(L1DenoisingAscent.class, RandomAccessibleInterval.class, DualVariables.class, input);
		descentL1Denoising = ops.op(L1DenoisingDescent.class, RandomAccessibleInterval.class, DualVariables.class,
				0.25);

		mapperSubtract = (RAIAndIIToRAIParallel<T, T, T>) ops.op(Map.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, IterableInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(Computers.binary(ops, Ops.Math.Subtract.class, type, type, type));

		copyComputer = Computers.unary(ops, Ops.Copy.RAI.class, (RandomAccessibleInterval<T>) input,
				(RandomAccessibleInterval<T>) output);

		inplaceMapper = (MapIIInplaceParallel<T>) ops.op(Map.class, IterableInterval.class, UnaryInplaceOp.class);
		inplaceMapper.setOp((UnaryInplaceOp<T, T>) Inplaces.unary(ops, Default01Clipper.class, type));
	}
}
