package net.imagej.ops.fopd;

import net.imagej.ops.OpMatchingService;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.After;
import org.junit.Before;
import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;

/**
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public abstract class AbstractOpTest {

	@Parameter
	protected Context context;

	@Parameter
	protected OpService ops;

	@Parameter
	protected OpMatchingService matcher;

	protected Img<DoubleType> img;

	protected Img<DoubleType> posNegImg;

	protected Context createContext() {
		return new Context(OpService.class, OpMatchingService.class, CacheService.class);
	}

	@Before
	public void setUp() {
		createContext().inject(this);
		img = ops.create().img(new int[] { 3, 3 });
		posNegImg = ops.create().img(new int[] { 3, 3 });

		final RandomAccess<DoubleType> ra = img.randomAccess();
		final RandomAccess<DoubleType> posNegRA = posNegImg.randomAccess();
		for (int x = 0; x < img.dimension(0); x++) {
			for (int y = 0; y < img.dimension(1); y++) {
				ra.setPosition(new int[] { x, y });
				if (x % 2 == 0) {
					ra.get().set(1.0);
				}
				posNegRA.setPosition(ra);
				posNegRA.get().set(ra.get().get() - 0.5);
			}
		}
		ra.setPosition(new int[]{1,1});
		ra.get().set(1);
		posNegRA.setPosition(new int[]{1, 1});
		posNegRA.get().set(1);
	}

	@After
	public synchronized void cleanUp() {
		if (context != null) {
			context.dispose();
			context = null;
			ops = null;
			matcher = null;
		}
	}

}
