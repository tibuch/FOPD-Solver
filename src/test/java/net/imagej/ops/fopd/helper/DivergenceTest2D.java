package net.imagej.ops.fopd.helper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.DualVariables;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * Test for {@link DefaultBackwardDifference}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class DivergenceTest2D extends AbstractOpTest {

	private static double[] expected = new double[] { 0.0, 2.0, -1.0, 0.0, -2.0, 0.0, 0.0, 3.0, -1.0 };

	@Test
	public void divergenceTest() {

		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientX = ((Img<DoubleType>) ops.run(DefaultForwardDifference.class, img, 0));
		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientY = ((Img<DoubleType>) ops.run(DefaultForwardDifference.class, img, 1));

		@SuppressWarnings("unchecked")
		DualVariables<DoubleType> d = new DualVariables<DoubleType>(gradientX, gradientY);

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((IterableInterval<DoubleType>) ops.run(DefaultDivergence2D.class, d)).cursor();
		int i = 0;
		while (c.hasNext()) {
			 assertEquals("Divergence differs", expected[i++], c.next().get(), 0);
		}
	}

}
