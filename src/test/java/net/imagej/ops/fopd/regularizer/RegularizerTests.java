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

package net.imagej.ops.fopd.regularizer;

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.AbstractOpTest;
import net.imagej.ops.fopd.regularizer.tgv.TGV2DAscent;
import net.imagej.ops.fopd.regularizer.tgv.TGV2DDescent;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation2DAscent;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation2DDescent;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation3DAscent;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation3DDescent;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber2DAscent;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber2DDescent;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber3DAscent;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber3DDescent;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.fopd.solver.TGV3DSolverState;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * {@link Regularizer} tests.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class RegularizerTests extends AbstractOpTest {

	private static double[] expectedTV2D = new double[] { -0.025,
		0.06035533905932738, -0.017677669529663688, 0.0, -0.04267766952966369,
		0.0, -0.025, 0.07500000000000001, -0.025 };
	
	private static double[] expectedTV3D = new double[] { -0.1, 0.1, 0.0, -0.1,
			0.07071067811865475, 0.0, -0.1, 0.1, 0.0, -0.1, 0.07071067811865475, 
			0.0, 0.0, 0.0, 0.0, -0.1, 0.1, 0.0, -0.1, 0.1, 0.0, -0.1, 0.1, 0.0, 
			-0.1, 0.1, 0.0 };

	private static double[] expectedTVHuber2D = new double[] {
		-0.11904761904761904, 0.29582431434425593, -0.08838834764831843, 0.0,
		-0.20743596669593747, 0.0, -0.11904761904761904, 0.3571428571428571,
		-0.11904761904761904 };
	
	private static double[] expectedTVHuber3D = new double[] { -0.11904761904761904, 
			0.23809523809523808, -0.11904761904761904, -0.11904761904761904, 0.29582431434425593,
			-0.08838834764831843, -0.11904761904761904, 0.23809523809523808, -0.11904761904761904,
			-0.11904761904761904, 0.29582431434425593, -0.08838834764831843, 0.0, -0.35355339059327373,
			0.0, -0.11904761904761904, 0.3264835857435565, -0.11904761904761904,
			-0.11904761904761904, 0.23809523809523808, -0.11904761904761904, -0.11904761904761904,
			0.3264835857435565, -0.11904761904761904, -0.11904761904761904, 0.23809523809523808, 
			-0.11904761904761904 };

	private static double[] expectedTGV2D = new double[] { -0.08333333333333333,
		0.25, -0.08333333333333333, 0.0, -0.16666666666666666, 0.0,
		-0.08333333333333333, 0.25, -0.08333333333333333 };
	
	private static double[] expectedTGV3D = new double[] { -0.08333333333333333, 0.16666666666666666,
			-0.08333333333333333, -0.08333333333333333, 0.16666666666666666, -0.08333333333333333, 
			-0.08333333333333333, 0.16666666666666666, -0.08333333333333333, -0.08333333333333333, 
			0.25, -0.08333333333333333, 0.0, -0.16666666666666666, 0.0, -0.08333333333333333, 0.25,
			-0.08333333333333333, -0.08333333333333333, 0.16666666666666666, -0.08333333333333333,
			-0.08333333333333333, 0.16666666666666666, -0.08333333333333333, -0.08333333333333333, 
			0.16666666666666666, -0.08333333333333333 };

	@SuppressWarnings("unchecked")
	@Test
	public void tv2DTest() {

		final TotalVariation2DAscent<DoubleType> ascentTV = ops.op(
			TotalVariation2DAscent.class, SolverState.class, 0.1);
		final TotalVariation2DDescent<DoubleType> descentTV = ops.op(
			TotalVariation2DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state =
			new DefaultSolverState<DoubleType>(ops,
				new RandomAccessibleInterval[] { ops.create().img(img2D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img2D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}
		ascentTV.calculate(state);
		descentTV.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Total Variation 2D differs", expectedTV2D[i++], c.next()
				.get(), 0);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void tv3DTest() {

		final TotalVariation3DAscent<DoubleType> ascentTV = ops.op(
			TotalVariation3DAscent.class, SolverState.class, 0.1);
		final TotalVariation3DDescent<DoubleType> descentTV = ops.op(
			TotalVariation3DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state =
			new DefaultSolverState<DoubleType>(ops,
				new RandomAccessibleInterval[] { ops.create().img(img3D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img3D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}
		ascentTV.calculate(state);
		descentTV.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getRegularizerDV().getDualVariable(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("Total Variation 3D differs", expectedTV3D[i++], c.next()
				.get(), 0);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void tvHuber2DTest() {

		final TVHuber2DAscent<DoubleType> ascentTVHuber = ops.op(
			TVHuber2DAscent.class, SolverState.class, 0.5, 0.1);
		final TVHuber2DDescent<DoubleType> descentTVHuber = ops.op(
			TVHuber2DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state =
			new DefaultSolverState<DoubleType>(ops,
				new RandomAccessibleInterval[] { ops.create().img(img2D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img2D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentTVHuber.calculate(state);
		descentTVHuber.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("TV-Huber differs", expectedTVHuber2D[i++], c.next()
				.get(), 0);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void tvHuber3DTest() {

		final TVHuber3DAscent<DoubleType> ascentTVHuber = ops.op(
			TVHuber3DAscent.class, SolverState.class, 0.5, 0.1);
		final TVHuber3DDescent<DoubleType> descentTVHuber = ops.op(
			TVHuber3DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state =
			new DefaultSolverState<DoubleType>(ops,
				new RandomAccessibleInterval[] { ops.create().img(img3D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img3D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentTVHuber.calculate(state);
		descentTVHuber.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("TV-Huber differs", expectedTVHuber3D[i++], c.next()
				.get(), 0);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void tgv2DTest() {

		final TGV2DAscent<DoubleType> ascentTVHuber = ops.op(TGV2DAscent.class,
			SolverState.class, 0.5, 0.1);
		final TGV2DDescent<DoubleType> descentTVHuber = ops.op(
			TGV2DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state = new TGV3DSolverState<DoubleType>(
			ops, new RandomAccessibleInterval[] { ops.create().img(img2D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img2D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentTVHuber.calculate(state);
		descentTVHuber.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("TGV 2D differs", expectedTGV2D[i++], c.next().get(),
				0);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void tgv3DTest() {

		final TGV2DAscent<DoubleType> ascentTVHuber = ops.op(TGV2DAscent.class,
			SolverState.class, 0.5, 0.1);
		final TGV2DDescent<DoubleType> descentTVHuber = ops.op(
			TGV2DDescent.class, SolverState.class, 0.25);

		final SolverState<DoubleType> state = new TGV3DSolverState<DoubleType>(
			ops, new RandomAccessibleInterval[] { ops.create().img(img3D) }, 1);
		RandomAccess<DoubleType> ra = state.getResultImage(0).randomAccess();
		Cursor<DoubleType> c = img3D.cursor();

		while (c.hasNext()) {
			c.next();
			ra.setPosition(c);
			ra.get().set(c.get().get());
		}

		ascentTVHuber.calculate(state);
		descentTVHuber.calculate(state);

		c = ((IterableInterval<DoubleType>) state.getIntermediateResult(0))
			.cursor();
		int i = 0;
		while (c.hasNext()) {
			assertEquals("TGV 3D differs", expectedTGV3D[i++], c.next().get(),
				0);
		}
	}
}
