package net.imagej.ops.fopd.regularizer;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation2DAscent;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation2DDescent;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Total Variation test.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class TotalVariationTest extends AbstractOpTest {

	private static double[] expected = new double[] { -0.025, 0.06035533905932738, -0.017677669529663688, 0.0,
			-0.04267766952966369, 0.0, -0.025, 0.07500000000000001, -0.025 };

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
			assertEquals("Total Variation differs", expected[i++], c.next().get(), 0);
		}
	}
}
