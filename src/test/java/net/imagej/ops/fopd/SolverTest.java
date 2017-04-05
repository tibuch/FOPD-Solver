package net.imagej.ops.fopd;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.imagej.ops.fopd.TVL1Denoising;
import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class SolverTest extends AbstractOpTest {

	final static double[] expectedTVL1Denoising = new double[] { 1.0, 0.7869002898651334, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
			1.0 };

	final static double[] expectedTVHuberL1Denoising = new double[] { 1.0, 0.7834267473541114, 1.0, 1.0, 1.0, 1.0, 1.0,
			1.0, 1.0 };

	@Test
	public void TVL1DenoisingTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(TVL1Denoising.class, img, 0.5, 10)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c.getDoublePosition(1) + "] differs.",
					expectedTVL1[i++], c.get().get(), 0);
		}
	}
	
	@Test
	public void TVHuberL1DenoisingTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(TVHuberL1Denoising.class, img, 0.5, 0.05, 10)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c.getDoublePosition(1) + "] differs.",
					expectedTVHuberL1[i++], c.get().get(), 0);
		}
	}
}
