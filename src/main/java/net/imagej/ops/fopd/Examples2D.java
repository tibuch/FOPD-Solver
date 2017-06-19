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
import net.imagej.ops.fopd.energy.deconvolution.TGVKLDivDeconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TGVL1Deconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TGVSquaredL2Deconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberKLDivDeconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberL1Deconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberSquaredL2Deconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVKLDivDeconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVL1Deconvolution2D;
import net.imagej.ops.fopd.energy.deconvolution.TVSquaredL2Deconvolution2D;
import net.imagej.ops.fopd.energy.denoising.TGVKLDivDenoising2D;
import net.imagej.ops.fopd.energy.denoising.TGVL1Denoising2D;
import net.imagej.ops.fopd.energy.denoising.TGVSquaredL2Denoising2D;
import net.imagej.ops.fopd.energy.denoising.TVHuberKLDivDenoising2D;
import net.imagej.ops.fopd.energy.denoising.TVHuberL1Denoising2D;
import net.imagej.ops.fopd.energy.denoising.TVHuberSquaredL2Denoising2D;
import net.imagej.ops.fopd.energy.denoising.TVKLDivDenoising2D;
import net.imagej.ops.fopd.energy.denoising.TVL1Denoising2D;
import net.imagej.ops.fopd.energy.denoising.TVSquaredL2Denoising2D;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;

import ij.IJ;
import ij.ImageJ;

/**
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class Examples2D {

	@Parameter
	private OpService ops;
	private ImageJ ij;

	public static void main(String[] args) {
		Examples2D ex = new Examples2D();
		final int numIts = 100;
		ex.showIJ();
		// Denoising
		// ImageJFunctions.show(ex.getNoisyImgs()[0]);
		// ImageJFunctions.show(ex.getNoisyImgs()[1]);
		// L1 costfunction
		// ImageJFunctions.show(ex.denoisingL1TV2D(ex.getNoisyImgs(), numIts));
		// ImageJFunctions.show(ex.denoisingL1TV2D(ex.getNoisyImg0(), numIts));

		// ImageJFunctions.show(ex.denoisingL1TVHuber2D(ex.getNoisyImgs(),
		// numIts));
		// ImageJFunctions.show(ex.denoisingL1TVHuber2D(ex.getNoisyImg0(),
		// numIts));

		// ImageJFunctions.show(ex.denoisingL1TGV2D(ex.getNoisyImgs(), numIts));
		// ImageJFunctions.show(ex.denoisingL1TGV2D(ex.getNoisyImg0(), numIts));

		// KL-Div costfunction
		// ImageJFunctions.show(ex.denoisingKLDivTV2D(ex.getNoisyImgs(),
		// numIts));
		// ImageJFunctions.show(ex.denoisingKLDivTV2D(ex.getNoisyImg0(),
		// numIts));

		// ImageJFunctions.show(ex.denoisingKLDivTVHuber2D(ex.getNoisyImgs(),
		// numIts));
		// ImageJFunctions.show(ex.denoisingKLDivTVHuber2D(ex.getNoisyImg0(),
		// numIts));

		// ImageJFunctions.show(ex.denoisingKLDivTGV2D(ex.getNoisyImgs(),
		// numIts));
		// ImageJFunctions.show(ex.denoisingKLDivTGV2D(ex.getNoisyImg0(),
		// numIts));

		// squared L2 costfunction
		// ImageJFunctions.show(ex.denoisingSquaredL2TV2D(ex.getNoisyImgs(),
		// numIts));
		// ImageJFunctions.show(ex.denoisingSquaredL2TV2D(ex.getNoisyImg0(),
		// numIts));

		// ImageJFunctions.show(ex.denoisingSquaredL2TVHuber2D(ex.getNoisyImgs(),
		// numIts));
		// ImageJFunctions.show(ex.denoisingSquaredL2TVHuber2D(ex.getNoisyImg0(),
		// numIts));

		// ImageJFunctions.show(ex.denoisingSquaredL2TGV2D(ex.getNoisyImgs(),
		// numIts));
		// ImageJFunctions.show(ex.denoisingSquaredL2TGV2D(ex.getNoisyImg0(),
		// numIts));

		// Deconvolution
		ImageJFunctions.show(ex.getConvolvedImgs()[0]);
//		ImageJFunctions.show(ex.getConvolvedImgs()[1]);
		// L1 costfunction
//		ImageJFunctions.show(ex.deconvolutionL1TV2D(ex.getConvolvedImgs(), ex
//			.getKernels(), numIts));
		ImageJFunctions.show(ex.deconvolutionL1TV2D(ex.getConvolvedImg0(), ex
			.getKernel0(), numIts));
		
		ImageJFunctions.show(ex.rlDeconvolution(ex.getConvolvedImg0(), ex.getKernel0(), numIts));

		// ImageJFunctions.show(ex.deconvolutionL1TVHuber2D(ex.getConvolvedImgs(),
		// ex.getKernels(), numIts));
		// ImageJFunctions.show(ex.deconvolutionL1TVHuber2D(ex.getConvolvedImg0(),
		// ex.getKernel0(), numIts));
		//
		// ImageJFunctions.show(ex.deconvolutionL1TGV2D(ex.getConvolvedImgs(),
		// ex.getKernels(), numIts));
		// ImageJFunctions.show(ex.deconvolutionL1TGV2D(ex.getConvolvedImg0(),
		// ex.getKernel0(), numIts));
		//
		// // KL-Div costfunction
		// ImageJFunctions.show(ex.deconvolutionKLDivTV2D(ex.getConvolvedImgs(),
		// ex.getKernels(), numIts));
		// ImageJFunctions.show(ex.deconvolutionKLDivTV2D(ex.getConvolvedImg0(),
		// ex.getKernel0(), numIts));
		//
		// ImageJFunctions.show(ex.deconvolutionKLDivTVHuber2D(ex.getConvolvedImgs(),
		// ex.getKernels(), numIts));
		// ImageJFunctions.show(ex.deconvolutionKLDivTVHuber2D(ex.getConvolvedImg0(),
		// ex.getKernel0(), numIts));
		//
		// ImageJFunctions.show(ex.deconvolutionKLDivTGV2D(ex.getConvolvedImgs(),
		// ex.getKernels(), numIts));
		// ImageJFunctions.show(ex.deconvolutionKLDivTGV2D(ex.getConvolvedImg0(),
		// ex.getKernel0(), numIts));
		//
		// // squared L2 costfunction
		// ImageJFunctions.show(ex.deconvolutionSquaredL2TV2D(ex.getConvolvedImgs(),
		// ex.getKernels(), numIts));
		// ImageJFunctions.show(ex.deconvolutionSquaredL2TV2D(ex.getConvolvedImg0(),
		// ex.getKernel0(), numIts));
		//
		// ImageJFunctions.show(ex.deconvolutionSquaredL2TVHuber2D(ex.getConvolvedImgs(),
		// ex.getKernels(), numIts));
		// ImageJFunctions.show(ex.deconvolutionSquaredL2TVHuber2D(ex.getConvolvedImg0(),
		// ex.getKernel0(), numIts));
		//
		// ImageJFunctions.show(ex.deconvolutionSquaredL2TGV2D(ex.getConvolvedImgs(),
		// ex.getKernels(), numIts));
		// ImageJFunctions.show(ex.deconvolutionSquaredL2TGV2D(ex.getConvolvedImg0(),
		// ex.getKernel0(), numIts));
	}

	public Examples2D() {
		Context context = new Context(OpService.class, OpMatchingService.class,
			CacheService.class);
		context.inject(this);
	}

	public void showIJ() {
		ij = new ImageJ();
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType>[] getNoisyImg0() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("2D_noisy_v0.tif").getPath())) };
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType>[] getNoisyImgs() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("2D_noisy_v0.tif").getPath())), ImagePlusAdapter.wrap(
				IJ.openImage(this.getClass().getResource("2D_noisy_v1.tif")
					.getPath())) };
	}

	private Img<FloatType>[] getConvolvedImg0() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("2D_convolved_v0.tif").getPath())) };
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType>[] getConvolvedImgs() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("2D_convolved_v0.tif").getPath())), ImagePlusAdapter
				.wrap(IJ.openImage(this.getClass().getResource(
					"2D_convolved_v1.tif").getPath())) };
	}

	@SuppressWarnings({ "unchecked" })
	private Img<FloatType>[] getKernel0() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("2D_kernel_v0.tif").getPath())) };
	}

	@SuppressWarnings({ "unchecked" })
	private Img<FloatType>[] getKernels() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("2D_kernel_v0.tif").getPath())), ImagePlusAdapter.wrap(
				IJ.openImage(this.getClass().getResource("2D_kernel_v1.tif")
					.getPath())) };
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TV2D(final Img<FloatType>[] imgs,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVL1Denoising2D.class, imgs, numIts, 0.9);
		t = System.currentTimeMillis() - t;
		System.out.println("TVL1-Denoising [" + imgs[0].dimension(0) + ", " +
			imgs[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TVHuber2D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberL1Denoising2D.class, img, numIts, 0.9, 0.01);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberL1-Denoising [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TGV2D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVL1Denoising2D.class, img, numIts, 0.01, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVL1-Denoising [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TV2D(final Img<FloatType>[] img,
		final Img<FloatType> kernel[], final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVL1Deconvolution2D.class, img, kernel, numIts, 0.01);
		t = System.currentTimeMillis() - t;
		System.out.println("TVL1-Deconvolution [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TVHuber2D(final Img<FloatType>[] img,
		final Img<FloatType>[] kernel, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberL1Deconvolution2D.class, img, kernel, numIts, 0.1, 0.05);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberL1-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TGV2D(final Img<FloatType>[] img,
		final Img<FloatType>[] kernel, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVL1Deconvolution2D.class, img, kernel, numIts, 0.01, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVL1-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	// ================================= Kullback-Leibler-Divergence

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTV2D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVKLDivDenoising2D.class, img, numIts, 0.5);
		t = System.currentTimeMillis() - t;
		System.out.println("TVKLDiv-Denoising [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTVHuber2D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberKLDivDenoising2D.class, img, numIts, 0.5, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberKLDiv-Denoising [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTGV2D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVKLDivDenoising2D.class, img, numIts, 0.5, 2.0);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVKLDiv-Denoising [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTV2D(final Img<FloatType>[] img,
		final Img<FloatType>[] kernel, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVKLDivDeconvolution2D.class, img, kernel, numIts, 0.008);
		t = System.currentTimeMillis() - t;
		System.out.println("TVKLDiv-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTVHuber2D(
		final Img<FloatType>[] img, final Img<FloatType>[] kernel,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberKLDivDeconvolution2D.class, img, kernel, numIts, 0.01, 0.05);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberKLDiv-Deconvolution [" + img[0].dimension(
			0) + ", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTGV2D(final Img<FloatType>[] img,
		final Img<FloatType>[] kernel, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVKLDivDeconvolution2D.class, img, kernel, numIts, 0.01, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVKLDiv-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	// ================================= Squared-L2-Norm

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingSquaredL2TV2D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVSquaredL2Denoising2D.class, img, numIts, 0.1);
		t = System.currentTimeMillis() - t;
		System.out.println("TVKLDiv-Denoising [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingSquaredL2TVHuber2D(
		final Img<FloatType>[] img, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberSquaredL2Denoising2D.class, img, numIts, 0.1, 0.05);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberSquaredL2-Denoising [" + img[0].dimension(
			0) + ", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingSquaredL2TGV2D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVSquaredL2Denoising2D.class, img, numIts, 0.05, 0.1);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVSquaredL2-Denoising [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionSquaredL2TV2D(
		final Img<FloatType>[] img, final Img<FloatType>[] kernel,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVSquaredL2Deconvolution2D.class, img, kernel, numIts, 0.008);
		t = System.currentTimeMillis() - t;
		System.out.println("TVSquaredL2-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionSquaredL2TVHuber2D(
		final Img<FloatType>[] img, final Img<FloatType>[] kernel,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberSquaredL2Deconvolution2D.class, img, kernel, numIts, 0.01,
			0.05);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberSquaredL2-Deconvolution [" + img[0]
			.dimension(0) + ", " + img[0].dimension(1) + "]: " + t / 1000.0 +
			"sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionSquaredL2TGV2D(
		final Img<FloatType>[] img, final Img<FloatType>[] kernel,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVSquaredL2Deconvolution2D.class, img, kernel, numIts, 0.01, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVSquaredL2-Deconvolution [" + img[0].dimension(
			0) + ", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private Img<FloatType> rlDeconvolution(
		final Img<FloatType>[] imgs, final Img<FloatType>[] kernel,
		final int numIts)
	{
		long t = System.currentTimeMillis();
		Img result = (Img)ops.deconvolve().richardsonLucy(imgs[0], kernel[0], numIts);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVSquaredL2-Deconvolution [" + imgs[0].dimension(
			0) + ", " + imgs[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}
}
