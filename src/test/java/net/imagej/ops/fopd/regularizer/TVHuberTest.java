package net.imagej.ops.fopd.regularizer;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber2DAscent;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber2DDescent;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.fopd.solver.TVL1DenoisingSolverState;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Total Variation with Huber-Norm test.
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

		final TVHuber2DAscent<DoubleType> ascentTVHuber = ops.op(TVHuber2DAscent.class, SolverState.class, 0.5, 0.1);
		final TVHuber2DDescent<DoubleType> descentTVHuber = ops.op(TVHuber2DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state = new TVL1DenoisingSolverState<DoubleType>(ops, ops.create().img(img));
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
			assertEquals("TV-Huber differs", expected[i++], c.next().get(), 0);
		}
	}
}
