package net.imagej.ops.fopd.costfunction;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.costfunction.deconvolution.L1Deconvolution2DAscent;
import net.imagej.ops.fopd.costfunction.deconvolution.L1Deconvolution2DDescent;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

import org.junit.Test;

public class DeconvolveTest extends AbstractOpTest {

	private static double[] expected = new double[] { -0.08500000089406967, -0.08999999612569809, -0.11500000208616257,
			-0.08749999850988388, -0.0924999937415123, -0.08500000089406967, -0.08500000089406967, -0.08999999612569809,
			-0.11500000208616257 };

	@SuppressWarnings("unchecked")
	@Test
	public void l1DeconvolutionTest() {

		Img<DoubleType> result = ops.create().img(img);

		final L1Deconvolution2DAscent<DoubleType> ascentL1Denoising = ops.op(L1Deconvolution2DAscent.class,
				RandomAccessibleInterval.class, DualVariables.class, posNegImg, kernel);
		final L1Deconvolution2DDescent<DoubleType> descentL1Denoising = ops.op(L1Deconvolution2DDescent.class,
				RandomAccessibleInterval.class, DualVariables.class, Views.invertAxis(Views.invertAxis(kernel, 0), 1),
				0.25);

		final DualVariables<DoubleType> q = new DualVariables<DoubleType>(ops.create().img(img));

		ascentL1Denoising.compute(q, img);
		descentL1Denoising.compute(q, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L1 Deconvolution differs", expected[i++], c.next().get(), 0);
		}
	}
}
