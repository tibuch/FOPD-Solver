package net.imagej.ops.fopd.costfunction;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.costfunction.kldivergence.KLDivergence2DAscent;
import net.imagej.ops.fopd.costfunction.kldivergence.KLDivergence2DDescent;
import net.imagej.ops.fopd.costfunction.l1norm.L1Norm2DAscent;
import net.imagej.ops.fopd.costfunction.l1norm.L1Norm2DDescent;
import net.imagej.ops.fopd.operator.Identity;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * {@link CostFunction} tests.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class CostFunctionTests extends AbstractOpTest {

	private static double[] expectedL1Norm = new double[] { 0.125, 0.125, 0.125, 0.125, 0.0, 0.125, 0.125, 0.125,
			0.125 };

	private static double[] expectedKLDiv = new double[] { 0.07019410160110379, 0.125, 0.07019410160110379,
			0.07019410160110379, 0.0, 0.07019410160110379, 0.07019410160110379, 0.125, 0.07019410160110379 };

	@SuppressWarnings("unchecked")
	@Test
	public void l1NormTest() {

		final L1Norm2DAscent<DoubleType> ascentL1Denoising = ops.op(L1Norm2DAscent.class, SolverState.class, img,
				ops.op(Identity.class, img));
		final L1Norm2DDescent<DoubleType> descentL1Denoising = ops.op(L1Norm2DDescent.class, SolverState.class,
				ops.op(Identity.class, img), 0.25);

		final SolverState<DoubleType> state = new DefaultSolverState<DoubleType>(ops, ops.create().img(img));
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = posNegImg.cursor();

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
			assertEquals("Total Variation differs", expectedL1Norm[i++], c.next().get(), 0);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void klDivTest() {

		final KLDivergence2DAscent<DoubleType> ascentL1Denoising = ops.op(KLDivergence2DAscent.class, SolverState.class,
				img, ops.op(Identity.class, img));
		final AbstractCostFunction2DDescent<DoubleType> descentL1Denoising = ops.op(KLDivergence2DDescent.class,
				SolverState.class, ops.op(Identity.class, img), 0.25);

		final SolverState<DoubleType> state = new DefaultSolverState<DoubleType>(ops, ops.create().img(img));
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = posNegImg.cursor();

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
			assertEquals("Total Variation differs", expectedKLDiv[i++], c.next().get(), 0);
		}
	}
}
