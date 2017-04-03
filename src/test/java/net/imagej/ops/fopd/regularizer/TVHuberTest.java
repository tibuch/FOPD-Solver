package net.imagej.ops.fopd.regularizer;

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
public class TVHuberTest extends AbstractOpTest {

	private static double[] expected = new double[] { -0.11904761904761904, 0.29582431434425593, -0.08838834764831843,
			0.0, -0.20743596669593747, 0.0, -0.11904761904761904, 0.3571428571428571, -0.11904761904761904 };

	@SuppressWarnings("unchecked")
	@Test
	public void tvHuberTest() {

		Img<DoubleType> result = ops.create().img(img);

		final TVHuber2DAscent<DoubleType> ascentTVHuber = ops.op(TVHuber2DAscent.class, RandomAccessibleInterval.class,
				DualVariables.class, 0.5, 0.1);
		final TVHuber2DDescent<DoubleType> descentTVHuber = ops.op(TVHuber2DDescent.class,
				RandomAccessibleInterval.class, DualVariables.class, 0.25);

		final DualVariables<DoubleType> p = new DualVariables<DoubleType>(ops.create().img(img), ops.create().img(img));

		ascentTVHuber.compute(p, img);
		descentTVHuber.compute(p, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("TV-Huber differs", expected[i++], c.next().get(), 0);
		}
	}
}
