package net.imagej.ops.fopd.helper;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * L2-Norm tests.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class L2NormTest extends AbstractOpTest {

	private static double[] expected1D = new double[] { 0.5, 0.5, 0.5, 0.5, 1.0, 0.5, 0.5, 0.5, 0.5 };
	private static double[] expected2D = new double[] { 0.7071067811865476, 0.7071067811865476, 0.7071067811865476,
			0.7071067811865476, 1.4142135623730951, 0.7071067811865476, 0.7071067811865476, 0.7071067811865476,
			0.7071067811865476 };
	private static double[] expected3D = new double[] { 0.8660254037844386, 0.8660254037844386, 0.8660254037844386,
			0.8660254037844386, 1.7320508075688772, 0.8660254037844386, 0.8660254037844386, 0.8660254037844386,
			0.8660254037844386 };

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void L2Norm1DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer = Computers.unary(ops,
				DefaultL2Norm.class, RandomAccessibleInterval.class, RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg);
		normComputer.compute(new RandomAccessibleInterval[] { posNegImg }, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm1D differs", expected1D[i++], c.next().get(), 0);
		}

	}

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void L2Norm2DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer = Computers.unary(ops,
				DefaultL2Norm.class, RandomAccessibleInterval.class, RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg);

		normComputer.compute(new RandomAccessibleInterval[] { posNegImg, posNegImg }, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm2D differs", expected2D[i++], c.next().get(), 0);
		}

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void L2Norm3DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer = Computers.unary(ops,
				DefaultL2Norm.class, RandomAccessibleInterval.class, RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg);

		normComputer.compute(new RandomAccessibleInterval[] { posNegImg, posNegImg, posNegImg }, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm3D differs", expected3D[i++], c.next().get(), 0);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void L2Norm4DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer = Computers.unary(ops,
				DefaultL2Norm.class, RandomAccessibleInterval.class, RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg);

		normComputer.compute(new RandomAccessibleInterval[] { posNegImg, posNegImg, posNegImg, posNegImg }, result);

		final RandomAccess<DoubleType> ra = result.randomAccess();
		for (int y = 0; y < result.dimension(1); y++) {
			for (int x = 0; x < result.dimension(0); x++) {
				ra.setPosition(new int[] { x, y });
				assertEquals("L2Norm 2D", 1, ra.get().get(), 1);
			}
		}
	}
}
