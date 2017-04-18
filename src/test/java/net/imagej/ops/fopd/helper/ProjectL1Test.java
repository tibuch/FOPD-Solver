package net.imagej.ops.fopd.helper;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Tests of L1-back-projection.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class ProjectL1Test extends AbstractOpTest {

	private static double[] expected01 = new double[] { 0.07071067811865475, -0.07071067811865475, 0.07071067811865475,
			0.07071067811865475, 0.07071067811865475, 0.07071067811865475, 0.07071067811865475, -0.07071067811865475,
			0.07071067811865475 };
	private static double[] expected05 = new double[] { 0.35355339059327373, -0.35355339059327373, 0.35355339059327373,
			0.35355339059327373, 0.35355339059327373, 0.35355339059327373, 0.35355339059327373, -0.35355339059327373,
			0.35355339059327373 };

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void projectL1Test01() {
		final Img<DoubleType> result = ops.copy().img(posNegImg);

		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer = Computers.unary(ops,
				DefaultL2Norm.class, RandomAccessibleInterval.class, RandomAccessibleInterval[].class);

		final Img<DoubleType> norm = ops.create().img(posNegImg);
		DualVariables<DoubleType> input = new DualVariables<DoubleType>(ops.copy().img(posNegImg),
				ops.copy().img(posNegImg));
		normComputer.compute(input.getAllDualVariables(), norm);

		final BinaryInplace1Op<? super DoubleType, DoubleType, DoubleType> projector = Inplaces.binary1(ops,
				DefaultL1Projector.class, DoubleType.class, DoubleType.class, 0.1);

		ops.map((IterableInterval<DoubleType>) result, (IterableInterval<DoubleType>) norm, projector);
		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm1D differs", expected01[i++], c.next().get(), 0);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void projectL1Test05() {
		final Img<DoubleType> result = ops.copy().img(posNegImg);

		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer = Computers.unary(ops,
				DefaultL2Norm.class, RandomAccessibleInterval.class, RandomAccessibleInterval[].class);

		final Img<DoubleType> norm = ops.create().img(posNegImg);
		DualVariables<DoubleType> input = new DualVariables<DoubleType>(ops.copy().img(posNegImg),
				ops.copy().img(posNegImg));
		normComputer.compute(input.getAllDualVariables(), norm);

		final BinaryInplace1Op<? super DoubleType, DoubleType, DoubleType> projector = Inplaces.binary1(ops,
				DefaultL1Projector.class, DoubleType.class, DoubleType.class, 0.5);

		ops.map((IterableInterval<DoubleType>) result, (IterableInterval<DoubleType>) norm, projector);
		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm1D differs", expected05[i++], c.next().get(), 0);
		}
	}
}
