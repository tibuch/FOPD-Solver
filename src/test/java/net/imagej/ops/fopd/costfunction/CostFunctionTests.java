
package net.imagej.ops.fopd.costfunction;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.costfunction.kldivergence.KLDivergenceAscent;
import net.imagej.ops.fopd.costfunction.kldivergence.KLDivergenceDescent;
import net.imagej.ops.fopd.costfunction.l1norm.L1NormAscent;
import net.imagej.ops.fopd.costfunction.l1norm.L1NormDescent;
import net.imagej.ops.fopd.costfunction.squaredl2norm.SquaredL2NormAscent;
import net.imagej.ops.fopd.costfunction.squaredl2norm.SquaredL2NormDescent;
import net.imagej.ops.fopd.operator.Identity;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * {@link CostFunction} tests.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class CostFunctionTests extends AbstractOpTest {

	private static double[] expectedL1Norm = new double[] { 0.125, 0.125, 0.125,
		0.125, 0.0, 0.125, 0.125, 0.125, 0.125 };

	private static double[] expectedKLDiv = new double[] { 0.07019410160110379,
		0.125, 0.07019410160110379, 0.07019410160110379, 0.0,
		0.07019410160110379, 0.07019410160110379, 0.125, 0.07019410160110379 };

	private static double[] expectedSquaredL2Norm = new double[] { 0.0625,
		0.0625, 0.0625, 0.0625, 0.0, 0.0625, 0.0625, 0.0625, 0.0625 };

	@SuppressWarnings("unchecked")
	@Test
	public void l1NormTest() {

		final L1NormAscent<DoubleType> ascentL1Denoising = ops.op(
			L1NormAscent.class, SolverState.class, img2D, ops.op(Identity.class,
				img2D));
		final L1NormDescent<DoubleType> descentL1Denoising = ops.op(
			L1NormDescent.class, SolverState.class, ops.op(Identity.class,
				img2D), 0.25);

		final SolverState<DoubleType> state =
			new DefaultSolverState<DoubleType>(ops,
				new RandomAccessibleInterval[] { ops.create().img(img2D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = posNegImg2D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentL1Denoising.calculate(state);
		descentL1Denoising.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L1-Norm differs", expectedL1Norm[i++], c.next().get(),
				0);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void klDivTest() {

		final KLDivergenceAscent<DoubleType> ascentL1Denoising = ops.op(
			KLDivergenceAscent.class, SolverState.class, img2D, ops.op(
				Identity.class, img2D));
		final KLDivergenceDescent<DoubleType> descentL1Denoising = ops.op(
			KLDivergenceDescent.class, SolverState.class, ops.op(
				Identity.class, img2D), 0.25);

		final SolverState<DoubleType> state =
			new DefaultSolverState<DoubleType>(ops,
				new RandomAccessibleInterval[] { ops.create().img(img2D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = posNegImg2D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentL1Denoising.calculate(state);
		descentL1Denoising.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Kullback-Leibler-Divergence differs",
				expectedKLDiv[i++], c.next().get(), 0);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void squaredL2NormTest() {

		final SquaredL2NormAscent<DoubleType> ascentL1Denoising = ops.op(
			SquaredL2NormAscent.class, SolverState.class, img2D, ops.op(
				Identity.class, img2D));
		final SquaredL2NormDescent<DoubleType> descentL1Denoising = ops.op(
			SquaredL2NormDescent.class, SolverState.class, ops.op(
				Identity.class, img2D), 0.25);

		final SolverState<DoubleType> state =
			new DefaultSolverState<DoubleType>(ops,
				new RandomAccessibleInterval[] { ops.create().img(img2D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = posNegImg2D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentL1Denoising.calculate(state);
		descentL1Denoising.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Squared L2-Norm differs", expectedSquaredL2Norm[i++],
				c.next().get(), 0);
		}
	}
}
