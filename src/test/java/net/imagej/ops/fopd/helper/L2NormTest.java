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
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * L2-Norm tests.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class L2NormTest extends AbstractOpTest {

	private static double[] expected1D = new double[] { 0.5, 0.5, 0.5, 0.5, 1.0,
		0.5, 0.5, 0.5, 0.5 };
	private static double[] expected2D = new double[] { 0.7071067811865476,
		0.7071067811865476, 0.7071067811865476, 0.7071067811865476,
		1.4142135623730951, 0.7071067811865476, 0.7071067811865476,
		0.7071067811865476, 0.7071067811865476 };
	private static double[] expected3D = new double[] { 0.8660254037844386,
		0.8660254037844386, 0.8660254037844386, 0.8660254037844386,
		1.7320508075688772, 0.8660254037844386, 0.8660254037844386,
		0.8660254037844386, 0.8660254037844386 };

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void L2Norm1DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer =
			Computers.unary(ops, DefaultL2Norm.class,
				RandomAccessibleInterval.class,
				RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg2D);
		normComputer.compute(new RandomAccessibleInterval[] { posNegImg2D },
			result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm1D differs", expected1D[i++], c.next().get(),
				0);
		}

	}

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void L2Norm2DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer =
			Computers.unary(ops, DefaultL2Norm.class,
				RandomAccessibleInterval.class,
				RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg2D);

		normComputer.compute(new RandomAccessibleInterval[] { posNegImg2D,
			posNegImg2D }, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm2D differs", expected2D[i++], c.next().get(),
				0);
		}

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void L2Norm3DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer =
			Computers.unary(ops, DefaultL2Norm.class,
				RandomAccessibleInterval.class,
				RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg2D);

		normComputer.compute(new RandomAccessibleInterval[] { posNegImg2D,
			posNegImg2D, posNegImg2D }, result);

		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm3D differs", expected3D[i++], c.next().get(),
				0);
		}
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void L2Norm4DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer =
			Computers.unary(ops, DefaultL2Norm.class,
				RandomAccessibleInterval.class,
				RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg2D);

		normComputer.compute(new RandomAccessibleInterval[] { posNegImg2D,
			posNegImg2D, posNegImg2D, posNegImg2D }, result);

		final RandomAccess<DoubleType> ra = result.randomAccess();
		for (int y = 0; y < result.dimension(1); y++) {
			for (int x = 0; x < result.dimension(0); x++) {
				ra.setPosition(new int[] { x, y });
				if (x == 1 && y == 1) {
					assertEquals("L2Norm 4D", 2, ra.get().get(), 0);
				} else {
					assertEquals("L2Norm 4D", 1, ra.get().get(), 0);
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void L2Norm9DTest() {
		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer =
			Computers.unary(ops, DefaultL2Norm.class,
				RandomAccessibleInterval.class,
				RandomAccessibleInterval[].class);

		final Img<DoubleType> result = ops.create().img(posNegImg2D);

		normComputer.compute(new RandomAccessibleInterval[] { posNegImg2D, posNegImg2D, posNegImg2D,
				posNegImg2D, posNegImg2D, posNegImg2D,
				posNegImg2D, posNegImg2D, posNegImg2D,}, result);

		final RandomAccess<DoubleType> ra = result.randomAccess();
		for (int y = 0; y < result.dimension(1); y++) {
			for (int x = 0; x < result.dimension(0); x++) {
				ra.setPosition(new int[] { x, y });
				if (x == 1 && y == 1) {
					assertEquals("L2Norm 9D", 3, ra.get().get(), 0);
				} else {
					assertEquals("L2Norm 9D", 1.5, ra.get().get(), 0);
				}
			}
		}
	}
}
