package net.imagej.ops.fopd.costfunction;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.costfunction.deconvolution.L1Deconvolution2DAscent;
import net.imagej.ops.fopd.costfunction.deconvolution.L1Deconvolution2DDescent;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

import org.junit.Test;

/**
 * Deconvolution tests.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class DeconvolveTest extends AbstractOpTest {

	private static double[] expected = new double[] { -0.08500000089406967, -0.08999999612569809, -0.11500000208616257,
			-0.08749999850988388, -0.0924999937415123, -0.08500000089406967, -0.08500000089406967, -0.08999999612569809,
			-0.11500000208616257 };

	@SuppressWarnings("unchecked")
	@Test
	public void l1DeconvolutionTest() {

		final L1Deconvolution2DAscent<DoubleType> ascentL1Denoising = ops.op(L1Deconvolution2DAscent.class,
				SolverState.class, posNegImg, kernel);
		final L1Deconvolution2DDescent<DoubleType> descentL1Denoising = ops.op(L1Deconvolution2DDescent.class,
				SolverState.class, Views.invertAxis(Views.invertAxis(kernel, 0), 1), 0.25);

		final SolverState<DoubleType> state = new DefaultSolverState<DoubleType>(ops, ops.create().img(img));
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentL1Denoising.calculate(state);
		descentL1Denoising.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0)).cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L1 Deconvolution differs", expected[i++], c.next().get(), 0);
		}
	}
}
