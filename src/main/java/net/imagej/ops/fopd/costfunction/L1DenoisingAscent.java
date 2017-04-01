package net.imagej.ops.fopd.costfunction;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.helper.DefaultL1Projector;
import net.imagej.ops.fopd.helper.DefaultL2Norm;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.map.MapBinaryInplace1s.IIAndIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.hybrid.AbstractUnaryHybridCF;
import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

@Plugin(type = Ascent.class)
public class L1DenoisingAscent<T extends RealType<T>>
		extends AbstractUnaryHybridCF<DualVariables<T>, RandomAccessibleInterval<T>> implements Ascent<T> {

	@Parameter
	private OpService ops;

	@Parameter
	private RandomAccessibleInterval<T> f;

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;
	private RandomAccessibleInterval<T> diff;

	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	private RandomAccessibleInterval<T> norm;

	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<DualVariables, RandomAccessibleInterval> normComputer;

	private IIAndIIParallel<T, T> inplaceMapper;

	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<T> createOutput(DualVariables<T> input) {
		return (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
	}

	@SuppressWarnings("unchecked")
	public void compute(DualVariables<T> input, RandomAccessibleInterval<T> output) {
		if (mapperSubtract == null || mapperAdd == null || diff == null || norm == null || normComputer == null
				|| inplaceMapper == null) {
			init(input);
		}

		mapperSubtract.compute(output, f, (IterableInterval<T>) diff);

		mapperAdd.compute(input.getDualVariable(0), diff, (IterableInterval<T>) input.getDualVariable(0));

		normComputer.compute(input, norm);
		inplaceMapper.mutate1((IterableInterval<T>) input.getDualVariable(0), (IterableInterval<T>) norm);
	}

	@SuppressWarnings("unchecked")
	private void init(final DualVariables<T> input) {
		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops, Ops.Math.Subtract.class,
				input.getType(), input.getType(), input.getType());
		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		final BinaryComputerOp<T, T, T> addComputer = Computers.binary(ops, Ops.Math.Add.class, input.getType(),
				input.getType(), input.getType());
		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class, IterableInterval.class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(addComputer);
		diff = (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));

		norm = (RandomAccessibleInterval<T>) ops.create().img(input.getDualVariable(0));
		normComputer = Computers.unary(ops, DefaultL2Norm.class, RandomAccessibleInterval.class, DualVariables.class);
		final BinaryInplace1Op<? super T, T, T> projector = Inplaces.binary1(ops, DefaultL1Projector.class,
				input.getType(), input.getType(), 1);
		inplaceMapper = (IIAndIIParallel<T, T>) ops.op(Map.class, IterableInterval.class, IterableInterval.class,
				BinaryInplace1Op.class);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) projector);
	}
}
