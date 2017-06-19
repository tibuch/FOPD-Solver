/*-
 * #%L
 * An implementation of the first-order primal-dual solver proposed by Antonin Chamoblle and Thomas Pock.
 * Ref.: Chambolle, Antonin, and Thomas Pock. "A first-order primal-dual algorithm for convex problems with applications to imaging." Journal of Mathematical Imaging and Vision 40.1 (2011): 120-145.
 * %%
 * Copyright (C) 2017 Tim-Oliver Buchholz, University of Konstanz
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

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
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public abstract class AbstractOpTest {

	@Parameter
	protected Context context;

	@Parameter
	protected OpService ops;

	@Parameter
	protected OpMatchingService matcher;

	protected Img<DoubleType> img2D;

	protected Img<DoubleType> posNegImg2D;

	protected Img<DoubleType> kernel2D;

	protected Img<DoubleType> convolved2D;
	
	protected Img<DoubleType> img3D;

	protected Img<DoubleType> posNegImg3D;

	protected Img<DoubleType> kernel3D;

	protected Img<DoubleType> convolved3D;

	protected Context createContext() {
		return new Context(OpService.class, OpMatchingService.class,
			CacheService.class);
	}

	@Before
	public void setUp() {
		createContext().inject(this);
		img2D = ops.create().img(new int[] { 3, 3 });
		posNegImg2D = ops.create().img(new int[] { 3, 3 });

		RandomAccess<DoubleType> ra = img2D.randomAccess();
		RandomAccess<DoubleType> posNegRA = posNegImg2D.randomAccess();
		for (int x = 0; x < img2D.dimension(0); x++) {
			for (int y = 0; y < img2D.dimension(1); y++) {
				ra.setPosition(new int[] { x, y });
				if (x % 2 == 0) {
					ra.get().set(1.0);
				}
				posNegRA.setPosition(ra);
				posNegRA.get().set(ra.get().get() - 0.5);
			}
		}
		ra.setPosition(new int[] { 1, 1 });
		ra.get().set(1);
		posNegRA.setPosition(new int[] { 1, 1 });
		posNegRA.get().set(1);

		kernel2D = ops.create().img(new int[] { 3, 3 });
		ra = kernel2D.randomAccess();
		ra.setPosition(new int[] { 0, 0 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 1, 0 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 2, 0 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 0, 1 });
		ra.get().set(0.2);
		ra.setPosition(new int[] { 1, 1 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 2, 1 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 0, 2 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 1, 2 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 2, 2 });
		ra.get().set(0.1);

		convolved2D = ops.create().img(new int[] { 3, 3 });
		ra = convolved2D.randomAccess();
		ra.setPosition(new int[] { 0, 0 });
		ra.get().set(0.7);
		ra.setPosition(new int[] { 1, 0 });
		ra.get().set(0.8);
		ra.setPosition(new int[] { 2, 0 });
		ra.get().set(0.8);
		ra.setPosition(new int[] { 0, 1 });
		ra.get().set(0.8);
		ra.setPosition(new int[] { 1, 1 });
		ra.get().set(0.8);
		ra.setPosition(new int[] { 2, 1 });
		ra.get().set(0.8);
		ra.setPosition(new int[] { 0, 2 });
		ra.get().set(0.7);
		ra.setPosition(new int[] { 1, 2 });
		ra.get().set(0.8);
		ra.setPosition(new int[] { 2, 2 });
		ra.get().set(0.8);
		
		// setup 3D
		img3D = ops.create().img(new int[] { 3, 3, 3 });
		posNegImg3D = ops.create().img(new int[] { 3, 3, 3 });

		ra = img3D.randomAccess();
		posNegRA = posNegImg3D.randomAccess();
		for (int x = 0; x < img3D.dimension(0); x++) {
			for (int y = 0; y < img3D.dimension(1); y++) {
				for (int z = 0; z < img3D.dimension(2); z++) {
					ra.setPosition(new int[] { x, y, z });
					if (x % 2 == 0) {
						ra.get().set(1.0);
					}
					posNegRA.setPosition(ra);
					posNegRA.get().set(ra.get().get() - 0.5);
				}
			}
		}
		ra.setPosition(new int[] { 1, 1, 1 });
		ra.get().set(1);
		posNegRA.setPosition(new int[] { 1, 1, 1 });
		posNegRA.get().set(1);

		kernel3D = ops.create().img(new int[] { 3, 3, 3 });
		ra = kernel3D.randomAccess();
		ra.setPosition(new int[] { 0, 0, 0 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 0, 0 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 2, 0, 0 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 0, 1, 0 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 1, 0 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 2, 1, 0 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 0, 2, 0 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 2, 0 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 2, 2, 0 });
		ra.get().set(0.036);
		
		ra.setPosition(new int[] { 0, 0, 1 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 0, 1 });
		ra.get().set(0.064);
		ra.setPosition(new int[] { 2, 0, 1 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 0, 1, 1 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 1, 1 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 2, 1, 1 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 0, 2, 1 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 2, 1 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 2, 2, 1 });
		ra.get().set(0.036);
		
		ra.setPosition(new int[] { 0, 0, 2 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 0, 2 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 2, 0, 2 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 0, 1, 2 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 1, 2 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 2, 1, 2 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 0, 2, 2 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 1, 2, 2 });
		ra.get().set(0.036);
		ra.setPosition(new int[] { 2, 2, 2 });
		ra.get().set(0.036);

		convolved3D = ops.create().img(new int[] { 3, 3, 3 });
		ra = convolved3D.randomAccess();
		ra.setPosition(new int[] { 0, 0, 0 });
		ra.get().set(0.5);
		ra.setPosition(new int[] { 1, 0, 0 });
		ra.get().set(0.7);
		ra.setPosition(new int[] { 2, 0, 0 });
		ra.get().set(0.3);
		ra.setPosition(new int[] { 0, 1, 0 });
		ra.get().set(0.4);
		ra.setPosition(new int[] { 1, 1, 0 });
		ra.get().set(0.8);
		ra.setPosition(new int[] { 2, 1, 0 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 0, 2, 0 });
		ra.get().set(0.1);
		ra.setPosition(new int[] { 1, 2, 0 });
		ra.get().set(0.2);
		ra.setPosition(new int[] { 2, 2, 0 });
		ra.get().set(0.3);
		
		ra.setPosition(new int[] { 0, 0, 1 });
		ra.get().set(0.6);
		ra.setPosition(new int[] { 1, 0, 1 });
		ra.get().set(0.27);
		ra.setPosition(new int[] { 2, 0, 1 });
		ra.get().set(0.7);
		ra.setPosition(new int[] { 0, 1, 1 });
		ra.get().set(0.2);
		ra.setPosition(new int[] { 1, 1, 1 });
		ra.get().set(0.63);
		ra.setPosition(new int[] { 2, 1, 1 });
		ra.get().set(0.34);
		ra.setPosition(new int[] { 0, 2, 1 });
		ra.get().set(0.726);
		ra.setPosition(new int[] { 1, 2, 1 });
		ra.get().set(0.234);
		ra.setPosition(new int[] { 2, 2, 1 });
		ra.get().set(0.12);
		
		ra.setPosition(new int[] { 0, 0, 2 });
		ra.get().set(0.86);
		ra.setPosition(new int[] { 1, 0, 2 });
		ra.get().set(0.34);
		ra.setPosition(new int[] { 2, 0, 2 });
		ra.get().set(0.51);
		ra.setPosition(new int[] { 0, 1, 2 });
		ra.get().set(0.12);
		ra.setPosition(new int[] { 1, 1, 2 });
		ra.get().set(0.62);
		ra.setPosition(new int[] { 2, 1, 2 });
		ra.get().set(0.73);
		ra.setPosition(new int[] { 0, 2, 2 });
		ra.get().set(0.72);
		ra.setPosition(new int[] { 1, 2, 2 });
		ra.get().set(0.21);
		ra.setPosition(new int[] { 2, 2, 2 });
		ra.get().set(0.23);
		
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
