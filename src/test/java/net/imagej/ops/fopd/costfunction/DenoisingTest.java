package net.imagej.ops.fopd.costfunction;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.costfunction.denoising.L1DenoisingAscent;
import net.imagej.ops.fopd.costfunction.denoising.L1DenoisingDescent;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Denoising tests.
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

		final L1DenoisingAscent<DoubleType> ascentL1Denoising = ops.op(L1DenoisingAscent.class, SolverState.class,
				posNegImg);
		final L1DenoisingDescent<DoubleType> descentL1Denoising = ops.op(L1DenoisingDescent.class, SolverState.class,
				0.25);

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
			assertEquals("Total Variation differs", expected[i++], c.next().get(), 0);
		}
	}
}
