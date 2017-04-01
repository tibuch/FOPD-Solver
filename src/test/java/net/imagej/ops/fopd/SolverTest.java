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

	final static double[] expectedTVL1 = new double[] { 1.0, 0.11740558411732949, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, };

	@Test
	public void TVL1DenoisingTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(TVL1Denoising.class, img, 0.5, 9)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c.getDoublePosition(1) + "] differs.",
					expectedTVL1[i++], c.get().get(), 0);
		}
	}
}
