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

import static org.junit.Assert.assertEquals;

import net.imagej.ops.fopd.energy.deconvolution.TGVL1Deconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberL1Deconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVKLDivDeconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVL1Deconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVSquaredL2Deconvolution2D;
import net.imagej.ops.fopd.energy.denoising.TGVL1Denoising2D;
import net.imagej.ops.fopd.energy.denoising.TVHuberL1Denoising2D;
import net.imagej.ops.fopd.energy.denoising.TVL1Denoising2D;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Tests of the implemented solvers.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class SolverTest extends AbstractOpTest {

	final static double[] expectedTVL1Denoising = new double[] { 1.0,
		0.6505244424115337, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

	final static double[] expectedTVHuberL1Denoising = new double[] { 1.0,
		0.6444815657343546, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0 };

	final static double[] expectedTGVL1Denoising = new double[] { 1.0,
		0.61092099595629, 1.0, 1.0, 1.0, 1.0, 1.0, 0.9919244797347313, 1.0 };

	final static double[] expectedTVL1Deconvolution = new double[] {
		0.852645952127457, 0.9599103235969544, 1.0, 0.9014054497424371, 1.0,
		1.0, 0.8526459284847978, 0.9599102998930213, 1.0 };

	final static double[] expectedTVHuberL1Deconvolution = new double[] {
		0.8449143152960898, 0.9578770115357464, 1.0, 0.8949538345908679, 1.0,
		1.0, 0.8449142943570016, 0.9578769702830028, 1.0 };

	private static final double[] expectedTGVL1Deconvolution = new double[] {
		0.8439089252434915, 0.9610056122974837, 1.0, 0.8914287608473277, 1.0,
		1.0, 0.8445419552876334, 0.9618260754956498, 1.0 };

	private static final double[] expectedTVL1MultiViewDeconvolutino =
		new double[] { 0.4105700932017502, 0.5980660290731934,
			0.9498420512962649, 0.504989219586202, 0.6991254176998365,
			0.9600158205067111, 0.4172888561183441, 0.6045622279388823,
			0.9466757224895699 };

	private static final double[] expectedTVSquaredL2MultiViewDeconvolution =
		new double[] { 0.7592273723204381, 0.7623618244650691,
			0.7650725640119375, 0.7580971025767415, 0.7613931213358969,
			0.764063073007377, 0.7592273809033149, 0.7623618369559704,
			0.7650725708261379 };

	private static final double[] expectedTVKLDivMultiViewDeconvolution =
		new double[] { 0.7788525150845698, 0.7832699980854522,
			0.7866283572965034, 0.7760799537075452, 0.7811139240274552,
			0.7849561977296122, 0.7788525382058823, 0.7832700390325155,
			0.786628410267056 };

	@Test
	public void TVL1DenoisingTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TVL1Denoising2D.class, new RandomAccessibleInterval[] { img2D }, 10,
			0.5)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTVL1Denoising[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TVHuberL1DenoisingTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TVHuberL1Denoising2D.class, new RandomAccessibleInterval[] { img2D },
			10, 0.5, 0.05)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTVHuberL1Denoising[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TGVL1DenoisingTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TGVL1Denoising2D.class, new RandomAccessibleInterval[] { img2D }, 10,
			0.5, 1.0)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTGVL1Denoising[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TVL1DeconvolutionTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TVL1Deconvolution2D.class, new RandomAccessibleInterval[] {
				convolved2D }, new RandomAccessibleInterval[] { kernel2D }, 10,
			0.1)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTVL1Deconvolution[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TVHuberL1DeconvolutionTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TVHuberL1Deconvolution2D.class, new RandomAccessibleInterval[] {
				convolved2D }, new RandomAccessibleInterval[] { kernel2D }, 10, 0.1,
			0.8)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTVHuberL1Deconvolution[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TGVL1DeconvolutionTest() {

		@SuppressWarnings("unchecked")
		final Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TGVL1Deconvolution2D.class, new RandomAccessibleInterval[] {
				convolved2D }, new RandomAccessibleInterval[] { kernel2D }, 10, 0.5,
			1)).cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTGVL1Deconvolution[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TVL1MultiViewDeconvolutionTest() {

		@SuppressWarnings("unchecked")
		Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TVL1Deconvolution2D.class, new RandomAccessibleInterval[] { ops.copy()
				.img(convolved2D), ops.copy().img(convolved2D) },
			new RandomAccessibleInterval[] { kernel2D, kernel2D }, 10, 0.1))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTVL1MultiViewDeconvolutino[i++], c.get().get(), 0);
		}
	}

	@Test
	public void TVL2MultiViewDeconvolutionTest() {

		@SuppressWarnings("unchecked")
		Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TVSquaredL2Deconvolution2D.class, new RandomAccessibleInterval[] { ops
				.copy().img(convolved2D), ops.copy().img(convolved2D) },
			new RandomAccessibleInterval[] { kernel2D, kernel2D }, 10, 0.1))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTVSquaredL2MultiViewDeconvolution[i++], c.get().get(),
				0);
		}
	}

	@Test
	public void TVKLDivMultiViewDeconvolutionTest() {

		@SuppressWarnings("unchecked")
		Cursor<DoubleType> c = ((Img<DoubleType>) ops.run(
			TVKLDivDeconvolution2D.class, new RandomAccessibleInterval[] { ops
				.copy().img(convolved2D), ops.copy().img(convolved2D) },
			new RandomAccessibleInterval[] { kernel2D, kernel2D }, 10, 0.1))
				.cursor();
		int i = 0;
		while (c.hasNext()) {
			c.next();
			assertEquals("Pixel at [" + c.getDoublePosition(0) + "," + c
				.getDoublePosition(1) + "] differs.",
				expectedTVKLDivMultiViewDeconvolution[i++], c.get().get(), 0);
		}
	}
}
