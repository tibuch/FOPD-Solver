package net.imagej.ops.fopd.costfunction;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.DualVariables;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class DenoisingTest extends AbstractOpTest {

	private static double[] expected = new double[] { -0.125, -0.125, -0.125, -0.125, 0.0, -0.125, -0.125, -0.125,
			-0.125 };

	@SuppressWarnings("unchecked")
	@Test
	public void l1DenoisingTest() {

		Img<DoubleType> result = ops.create().img(img);

		final L1DenoisingAscent<DoubleType> ascentL1Denoising = ops.op(L1DenoisingAscent.class,
				RandomAccessibleInterval.class, DualVariables.class, posNegImg);
		final L1DenoisingDescent<DoubleType> descentL1Denoising = ops.op(L1DenoisingDescent.class,
				RandomAccessibleInterval.class, DualVariables.class, 0.25);

		final DualVariables<DoubleType> q = new DualVariables<DoubleType>(ops.create().img(img));

		ascentL1Denoising.compute(q, img);
		descentL1Denoising.compute(q, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Total Variation differs", expected[i++], c.next().get(), 0);
		}
	}
}
