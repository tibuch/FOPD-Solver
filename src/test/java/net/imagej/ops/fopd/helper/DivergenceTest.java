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

package net.imagej.ops.fopd.helper;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsBorderFactory;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Test for {@link DefaultBackwardDifference}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class DivergenceTest extends AbstractOpTest {

	private static double[] expected2D = new double[] { -1.0, 3.0, -1.0, 0.0,
		-2.0, 0.0, -1.0, 3.0, -1.0 };
	
	private static double[] expected3D = new double[] { -1.0, 2.0, -1.0, -1.0,
			2.0, -1.0, -1.0, 2.0, -1.0, -1.0, 3.0, -1.0, 0.0, -2.0, 0.0, -1.0, 
			3.0, -1.0, -1.0, 2.0, -1.0, -1.0, 2.0, -1.0, -1.0, 2.0, -1.0 };

	@Test
	public void divergence2DTest() {

		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientX = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img2D, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));
		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientY = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img2D, 1,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((IterableInterval<DoubleType>) ops.run(
			DefaultDivergence2D.class, RandomAccessibleInterval.class,
			new RandomAccessibleInterval[] { gradientX, gradientY })).cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Divergence differs", expected2D[i++], c.next().get(),
				0);
		}
	}
	
	@Test
	public void divergence3DTest() {

		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientX = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img3D, 0,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));
		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientY = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img3D, 1,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));
		@SuppressWarnings("unchecked")
		final Img<DoubleType> gradientZ = ((Img<DoubleType>) ops.run(
			DefaultForwardDifference.class, img3D, 2,
			new OutOfBoundsBorderFactory<DoubleType, RandomAccessibleInterval<DoubleType>>()));

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((IterableInterval<DoubleType>) ops.run(
			DefaultDivergence2D.class, RandomAccessibleInterval.class,
			new RandomAccessibleInterval[] { gradientX, gradientY, gradientZ })).cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Divergence differs", expected3D[i++], c.next().get(),
				0);
		}
	}

}
