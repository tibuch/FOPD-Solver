
package net.imagej.ops.fopd.helper;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Test for {@link DefaultBackwardDifference}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class DivergenceTest extends AbstractOpTest {

	private static double[] expected2D = new double[] { -1.0, 3.0, -1.0, 0.0,
		-2.0, 0.0, -1.0, 3.0, -1.0 };
	
	private static double[] expected3D = new double[] { -1.0, 2.0, -1.0, -1.0,
			2.0, -1.0, -1.0, 2.0, -1.0, -1.0, 3.0, -1.0, 0.0, -2.0, 0.0, -1.0, 
			3.0, -1.0, -1.0, 2.0, -1.0, -1.0, 2.0, -1.0, -1.0, 2.0, -1.0 };

	@Test
	public void divergence2DTest() {

		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientX = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img2D, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));
		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientY = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img2D, 1,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((IterableInterval<DoubleType>) ops.run(
			DefaultDivergence2D.class, RandomAccessibleInterval.class,
			new RandomAccessibleInterval[] { gradientX, gradientY })).cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Divergence differs", expected2D[i++], c.next().get(),
				0);
		}
	}
	
	@Test
	public void divergence3DTest() {

		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientX = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img3D, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));
		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientY = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img3D, 1,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));
		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientZ = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img3D, 2,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((IterableInterval<DoubleType>) ops.run(
			DefaultDivergence2D.class, RandomAccessibleInterval.class,
			new RandomAccessibleInterval[] { gradientX, gradientY, gradientZ })).cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Divergence differs", expected3D[i++], c.next().get(),
				0);
		}
	}

}