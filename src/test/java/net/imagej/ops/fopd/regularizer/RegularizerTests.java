package net.imagej.ops.fopd.regularizer;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.regularizer.tgv.TGV2DAscent;
import net.imagej.ops.fopd.regularizer.tgv.TGV2DDescent;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation2DAscent;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation2DDescent;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber2DAscent;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber2DDescent;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.fopd.solver.TGVSolverState;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * {@link Regularizer} tests.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class RegularizerTests extends AbstractOpTest {

	private static double[] expectedTV = new double[] { -0.025, 0.06035533905932738, -0.017677669529663688, 0.0,
			-0.04267766952966369, 0.0, -0.025, 0.07500000000000001, -0.025 };

	private static double[] expectedTVHuber = new double[] { -0.11904761904761904, 0.29582431434425593, -0.08838834764831843,
			0.0, -0.20743596669593747, 0.0, -0.11904761904761904, 0.3571428571428571, -0.11904761904761904 };
	
	private static double[] expectedTGV = new double[] { -0.08333333333333333, 0.25, -0.08333333333333333, 0.0,
			-0.16666666666666666, 0.0, -0.08333333333333333, 0.25, -0.08333333333333333 };

	@SuppressWarnings("unchecked")
	@Test
	public void tvTest() {

		final TotalVariation2DAscent<DoubleType> ascentTV = ops.op(TotalVariation2DAscent.class, SolverState.class,
				0.1);
		final TotalVariation2DDescent<DoubleType> descentTV = ops.op(TotalVariation2DDescent.class, SolverState.class,
				0.25);

		final SolverState<DoubleType> state = new DefaultSolverState<DoubleType>(ops, ops.create().img(img));
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}
		ascentTV.calculate(state);
		descentTV.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0)).cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Total Variation differs", expectedTV[i++], c.next().get(), 0);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void tvHuberTest() {

		final TVHuber2DAscent<DoubleType> ascentTVHuber = ops.op(TVHuber2DAscent.class, SolverState.class, 0.5, 0.1);
		final TVHuber2DDescent<DoubleType> descentTVHuber = ops.op(TVHuber2DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state = new DefaultSolverState<DoubleType>(ops, ops.create().img(img));
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentTVHuber.calculate(state);
		descentTVHuber.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0)).cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("TV-Huber differs", expectedTVHuber[i++], c.next().get(), 0);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void tgvTest() {

		final TGV2DAscent<DoubleType> ascentTVHuber = ops.op(TGV2DAscent.class, SolverState.class, 0.5, 0.1);
		final TGV2DDescent<DoubleType> descentTVHuber = ops.op(TGV2DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state = new TGVSolverState<DoubleType>(ops, ops.create().img(img));
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentTVHuber.calculate(state);
		descentTVHuber.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0)).cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("TV-Huber differs", expectedTGV[i++], c.next().get(), 0);
		}
	}
}
