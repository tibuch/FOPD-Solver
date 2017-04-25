
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
 * Test for {@link DefaultBackwardDifference}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class BackwardDifferenceTest extends AbstractOpTest {

	private static double[] backwardDifferenceX = new double[] { 0.0, -1.0, 1.0,
		0.0, 0.0, 0.0, 0.0, -1.0, 1.0 };

	private static double[] backwardDifferenceY = new double[] { 0.0, 0.0, 0.0,
		0.0, 1.0, 0.0, 0.0, -1.0, 0.0 };

	@Test
	public void backwardDifferenceXTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			DefaultBackwardDifference.class, img, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("GradientX differs", backwardDifferenceX[i++], c.next()
				.get(), 0);
		}
	}

	@Test
	public void backwardDifferenceYTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			DefaultBackwardDifference.class, img, 1,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("GradientY differs", backwardDifferenceY[i++], c.next()
				.get(), 0);
		}
	}
}
