
package net.imagej.ops.fopd.helper;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Test for {@link DefaultForwardDifference}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class ForwardDifferenceTest extends AbstractOpTest {

	private static double[] forwardDifferenceX2D = new double[] { -1.0, 1.0, 0.0,
		0.0, 0.0, 0.0, -1.0, 1.0, 0.0 };

	private static double[] forwardDifferenceY2D = new double[] { 0.0, 1.0, 0.0,
		0.0, -1.0, 0.0, 0.0, 0.0, 0.0 };

	private static double[] forwardDifferenceX3D = new double[] { -1.0, 1.0, 0.0, -1.0, 1.0, 0.0, -1.0, 1.0, 0.0, -1.0,
			1.0, 0.0, 0.0, 0.0, 0.0, -1.0, 1.0, 0.0, -1.0, 1.0, 0.0, -1.0, 1.0, 0.0, -1.0, 1.0, 0.0 };

	@Test
	public void forwardDifferenceXTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img2D, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("GradientX differs", forwardDifferenceX2D[i++], c.next()
				.get(), 0);
		}
	}

	@Test
	public void forwardDifferenceYTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img2D, 1,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("GradientY differs", forwardDifferenceY2D[i++], c.next()
				.get(), 0);
		}
	}
	
	@Test
	public void forwardDifferenceX3DTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img3D, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("GradientX differs", forwardDifferenceX3D[i++], c.next()
				.get(), 0);
		}
	}
}
