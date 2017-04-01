package net.imagej.ops.fopd.regularizer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.DualVariables;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

/**
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class TotalVariationTest extends AbstractOpTest {

	private static double[] expected = new double[] { 0.0, 0.04267766952966369, -0.017677669529663688, 0.0,
			-0.04267766952966369, 0.0, 0.0, 0.07500000000000001, -0.025 };

	@SuppressWarnings("unchecked")
	@Test
	public void tvTest() {

		Img<DoubleType> result = ops.create().img(img);

		final TotalVariation2DAscent<DoubleType> ascentTV = ops.op(TotalVariation2DAscent.class,
				RandomAccessibleInterval.class, DualVariables.class, 0.1);
		final TotalVariation2DDescent<DoubleType> descentTV = ops.op(TotalVariation2DDescent.class,
				RandomAccessibleInterval.class, DualVariables.class, 0.25);

		final DualVariables<DoubleType> p = new DualVariables<DoubleType>(ops.create().img(img), ops.create().img(img));

		ascentTV.compute(p, img);
		descentTV.compute(p, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Total Variation differs", expected[i++], c.next().get(), 0);
		}
	}
}
