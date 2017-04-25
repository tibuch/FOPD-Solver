
package net.imagej.ops.fopd.costfunction.l1norm;

import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.costfunction.Abstract2DAscent;
import net.imagej.ops.fopd.helper.DefaultL1Projector;
import net.imagej.ops.fopd.helper.DefaultL2Norm;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.map.MapBinaryComputers.RAIAndRAIToIIParallel;
import net.imagej.ops.map.MapBinaryInplace1s.IIAndIIParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

/**
 * L1-Norm as costfunction of one 2D image: {@link Ascent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Ascent.class)
public class L1Norm2DAscent<T extends RealType<T>> extends Abstract2DAscent<T> {

	private RAIAndRAIToIIParallel<T, T, T> mapperSubtract;
	private RandomAccessibleInterval<T> diff;

	private RAIAndRAIToIIParallel<T, T, T> mapperAdd;

	private RandomAccessibleInterval<T> norm;

	@SuppressWarnings("rawtypes")
	private UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer;

	private IIAndIIParallel<T, T> inplaceMapper;

	@Override
	@SuppressWarnings("unchecked")
	public void doAscent(final SolverState<T> input, final int i) {
		final DualVariables<T> dualVariables = input.getCostFunctionDV();

		if (mapperSubtract == null || mapperAdd == null || diff == null ||
			norm == null || normComputer == null || inplaceMapper == null)
		{
			init(dualVariables);
		}

		mapperSubtract.compute(operator[i].calculate(input.getResultImage(0)),
			f[i], (IterableInterval<T>) diff);

		mapperAdd.compute(dualVariables.getDualVariable(i), diff,
			(IterableInterval<T>) dualVariables.getDualVariable(i));

		normComputer.compute(new RandomAccessibleInterval[] { dualVariables
			.getDualVariable(i) }, norm);
		inplaceMapper.mutate1((IterableInterval<T>) dualVariables
			.getDualVariable(i), (IterableInterval<T>) norm);
	}

	@SuppressWarnings("unchecked")
	private void init(final DualVariables<T> input) {

		final BinaryComputerOp<T, T, T> subtractComputer = Computers.binary(ops,
			Ops.Math.Subtract.class, input.getType(), input.getType(), input
				.getType());
		mapperSubtract = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperSubtract.setOp(subtractComputer);

		final BinaryComputerOp<T, T, T> addComputer = Computers.binary(ops,
			Ops.Math.Add.class, input.getType(), input.getType(), input
				.getType());
		mapperAdd = (RAIAndRAIToIIParallel<T, T, T>) ops.op(Map.class,
			IterableInterval.class, RandomAccessibleInterval.class,
			RandomAccessibleInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(addComputer);
		diff = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));

		norm = (RandomAccessibleInterval<T>) ops.create().img(input
			.getDualVariable(0));
		normComputer = Computers.unary(ops, DefaultL2Norm.class,
			RandomAccessibleInterval.class, RandomAccessibleInterval[].class);
		final BinaryInplace1Op<? super T, T, T> projector = Inplaces.binary1(
			ops, DefaultL1Projector.class, input.getType(), input.getType(), 1);
		inplaceMapper = (IIAndIIParallel<T, T>) ops.op(Map.class,
			IterableInterval.class, IterableInterval.class,
			BinaryInplace1Op.class);
		inplaceMapper.setOp((BinaryInplace1Op<T, T, T>) projector);
	}
}
