package net.imagej.ops.fopd;

import static org.junit.Assert.assertEquals;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Tests of the implemented solvers.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class SolverTest extends AbstractOpTest {

	final static double[] expectedTVL1Denoising = new double[] { 1.0, 0.7869002898651334, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
			1.0 };

	final static double[] expectedTVHuberL1Denoising = new double[] { 1.0, 0.7834267473541114, 1.0, 1.0, 1.0, 1.0, 1.0,
			1.0, 1.0 };

	final static double[] expectedTGVL1Denoising = new double[] { 1.0, 0.7480864124025571, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
			1.0 };

	final static double[] expectedTVL1Deconvolution = new double[] { 0.852645952127457, 0.9599103235969544, 1.0,
			0.9014054497424371, 1.0, 1.0, 0.8526459284847978, 0.9599102998930213, 1.0 };

	final static double[] expectedTVHuberL1Deconvolution = new double[] { 0.8449143152960898, 0.9578770115357464, 1.0,
			0.8949538345908679, 1.0, 1.0, 0.8449142943570016, 0.9578769702830028, 1.0 };

	@Test
	public void TVL1DenoisingTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(TVL1Denoising.class, img, 0.5, 10)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c.getDoublePosition(1) + "] differs.",
					expectedTVL1Denoising[i++], c.get().get(), 0);
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
					expectedTVHuberL1Denoising[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TGVL1DenoisingTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(TGVL1Denoising.class, img, 0.5, 1, 10)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c.getDoublePosition(1) + "] differs.",
					expectedTGVL1Denoising[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TVL1DeconvolutionTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(TVL1Deconvolution.class, convolved, kernel, 0.1, 10))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c.getDoublePosition(1) + "] differs.",
					expectedTVL1Deconvolution[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TVHuberL1DeconvolutionTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(TVHuberL1Deconvolution.class, convolved, kernel, 0.1,
				0.8, 10)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c.getDoublePosition(1) + "] differs.",
					expectedTVHuberL1Deconvolution[i++], c.get().get(), 0);
		}
	}
}
