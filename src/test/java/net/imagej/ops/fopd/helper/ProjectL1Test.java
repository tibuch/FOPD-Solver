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
import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imagej.ops.special.inplace.Inplaces;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Tests of L1-back-projection.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class ProjectL1Test extends AbstractOpTest {

	private static double[] expected01 = new double[] { 0.07071067811865475,
		-0.07071067811865475, 0.07071067811865475, 0.07071067811865475,
		0.07071067811865475, 0.07071067811865475, 0.07071067811865475,
		-0.07071067811865475, 0.07071067811865475 };
	private static double[] expected05 = new double[] { 0.35355339059327373,
		-0.35355339059327373, 0.35355339059327373, 0.35355339059327373,
		0.35355339059327373, 0.35355339059327373, 0.35355339059327373,
		-0.35355339059327373, 0.35355339059327373 };

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void projectL1Test01() {
		final Img<DoubleType> result = ops.copy().img(posNegImg2D);

		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer =
			Computers.unary(ops, DefaultL2Norm.class,
				RandomAccessibleInterval.class,
				RandomAccessibleInterval[].class);

		final Img<DoubleType> norm = ops.create().img(posNegImg2D);
		normComputer.compute(new RandomAccessibleInterval[] { ops.copy().img(
			posNegImg2D), ops.copy().img(posNegImg2D) }, norm);

		final BinaryInplace1Op<? super DoubleType, DoubleType, DoubleType> projector =
			Inplaces.binary1(ops, DefaultL1Projector.class, DoubleType.class,
				DoubleType.class, 0.1);

		ops.map((IterableInterval<DoubleType>) result,
			(IterableInterval<DoubleType>) norm, projector);
		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm1D differs", expected01[i++], c.next().get(),
				0);
		}

	}

	@SuppressWarnings({ "rawtypes" })
	@Test
	public void projectL1Test05() {
		final Img<DoubleType> result = ops.copy().img(posNegImg2D);

		final UnaryComputerOp<RandomAccessibleInterval[], RandomAccessibleInterval> normComputer =
			Computers.unary(ops, DefaultL2Norm.class,
				RandomAccessibleInterval.class,
				RandomAccessibleInterval[].class);

		final Img<DoubleType> norm = ops.create().img(posNegImg2D);
		normComputer.compute(new RandomAccessibleInterval[] { ops.copy().img(
			posNegImg2D), ops.copy().img(posNegImg2D) }, norm);

		final BinaryInplace1Op<? super DoubleType, DoubleType, DoubleType> projector =
			Inplaces.binary1(ops, DefaultL1Projector.class, DoubleType.class,
				DoubleType.class, 0.5);

		ops.map((IterableInterval<DoubleType>) result,
			(IterableInterval<DoubleType>) norm, projector);
		final Cursor<DoubleType> c = result.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("L2Norm1D differs", expected05[i++], c.next().get(),
				0);
		}
	}
}
