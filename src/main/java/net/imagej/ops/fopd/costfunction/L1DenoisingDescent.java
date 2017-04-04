package net.imagej.ops.fopd.costfunction;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * L1-Denoising of one 2D image: {@link Descent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = Descent.class)
public class L1DenoisingDescent<T extends RealType<T>>
		extends AbstractUnaryHybridCF<DualVariables<T>, RandomAccessibleInterval<T>> implements Descent<T> {

	@Parameter
	private double stepSize;

	@Parameter
	private OpService ops;

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;

	private Converter<T, T> converter;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(DualVariables<T> input) {
		return (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
	}

	@SuppressWarnings("unchecked")
	public void compute(DualVariables<T> input, RandomAccessibleInterval<T> output) {

		if (mapperSubtract == null) {
			init(input);
		}

		mapperSubtract.compute(output, Converters.convert(input.getDualVariable(0), converter, input.getType()),
				(IterableInterval<T>) output);
	}

	@SuppressWarnings("unchecked")
	private void init(DualVariables<T> input) {
		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops, Ops.Math.Subtract.class,
				input.getType(), input.getType(), input.getType());
		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		converter = new Converter<T, T>() {

			public void convert(T input, T output) {
				output.setReal(input.getRealDouble() * stepSize);
			}
		};
	}

}
