
package net.imagej.ops.fopd;

import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;

import ij.IJ;
import ij.ImageJ;
import net.imagej.ops.OpMatchingService;
import net.imagej.ops.OpService;
import net.imagej.ops.fopd.energy.deconvolution.TGVKLDivDeconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TGVL1Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TGVSquaredL2Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberKLDivDeconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberL1Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberSquaredL2Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVKLDivDeconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVL1Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVSquaredL2Deconvolution3D;
import net.imagej.ops.fopd.energy.denoising.TGVKLDivDenoising3D;
import net.imagej.ops.fopd.energy.denoising.TGVL1Denoising3D;
import net.imagej.ops.fopd.energy.denoising.TGVSquaredL2Denoising3D;
import net.imagej.ops.fopd.energy.denoising.TVHuberKLDivDenoising3D;
import net.imagej.ops.fopd.energy.denoising.TVHuberL1Denoising3D;
import net.imagej.ops.fopd.energy.denoising.TVHuberSquaredL2Denoising3D;
import net.imagej.ops.fopd.energy.denoising.TVKLDivDenoising3D;
import net.imagej.ops.fopd.energy.denoising.TVL1Denoising3D;
import net.imagej.ops.fopd.energy.denoising.TVSquaredL2Denoising3D;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

/**
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class Examples3D {

	@Parameter
	private OpService ops;
	private ImageJ ij;

	public static void main(String[] args) {
		Examples3D ex = new Examples3D();
		final int numIts = 200;
		ex.showIJ();
		// Denoising
//		 ImageJFunctions.show(ex.getNoisyImgs()[0]);
//		 ImageJFunctions.show(ex.getNoisyImgs()[1]);
//		// L1 costfunction
//		 ImageJFunctions.show(ex.denoisingL1TV3D(ex.getNoisyImgs(), numIts));
//		 ImageJFunctions.show(ex.denoisingL1TV3D(ex.getNoisyImg0(), numIts));
//
//		 ImageJFunctions.show(ex.denoisingL1TVHuber3D(ex.getNoisyImgs(),
//		 numIts));
//		 ImageJFunctions.show(ex.denoisingL1TVHuber3D(ex.getNoisyImg0(),
//		 numIts));
//
//		 ImageJFunctions.show(ex.denoisingL1TGV3D(ex.getNoisyImgs(), numIts));
//		 ImageJFunctions.show(ex.denoisingL1TGV3D(ex.getNoisyImg0(), numIts));
//
//		// KL-Div costfunction
//		 ImageJFunctions.show(ex.denoisingKLDivTV3D(ex.getNoisyImgs(),
//		 numIts));
//		 ImageJFunctions.show(ex.denoisingKLDivTV3D(ex.getNoisyImg0(),
//		 numIts));
//
//		 ImageJFunctions.show(ex.denoisingKLDivTVHuber3D(ex.getNoisyImgs(),
//		 numIts));
//		 ImageJFunctions.show(ex.denoisingKLDivTVHuber3D(ex.getNoisyImg0(),
//		 numIts));
//
//		 ImageJFunctions.show(ex.denoisingKLDivTGV3D(ex.getNoisyImgs(),
//		 numIts));
//		 ImageJFunctions.show(ex.denoisingKLDivTGV3D(ex.getNoisyImg0(),
//		 numIts));
//
//		// squared L2 costfunction
//		 ImageJFunctions.show(ex.denoisingSquaredL2TV3D(ex.getNoisyImgs(),
//		 numIts));
//		 ImageJFunctions.show(ex.denoisingSquaredL2TV3D(ex.getNoisyImg0(),
//		 numIts));
//
//		 ImageJFunctions.show(ex.denoisingSquaredL2TVHuber3D(ex.getNoisyImgs(),
//		 numIts));
//		 ImageJFunctions.show(ex.denoisingSquaredL2TVHuber3D(ex.getNoisyImg0(),
//		 numIts));
//
//		 ImageJFunctions.show(ex.denoisingSquaredL2TGV3D(ex.getNoisyImgs(),
//		 numIts));
//		 ImageJFunctions.show(ex.denoisingSquaredL2TGV3D(ex.getNoisyImg0(),
//		 numIts));

		// Deconvolution
//		ImageJFunctions.show(ex.getConvolvedImg0()[0]);
//		ImageJFunctions.show(ex.getConvolvedImgs()[1]);
//		// L1 costfunction
		ImageJFunctions.show(ex.deconvolutionL1TV3D(ex.getConvolvedImgs(), ex
			.getKernels(), numIts));
//		ImageJFunctions.show(ex.deconvolutionL1TV3D(ex.getConvolvedImg0(), ex
//			.getKernel0(), numIts));
		
//		ImageJFunctions.show(ex.rlDeconvolution(ex.getConvolvedImg0(), ex.getKernel0(), numIts));
//
//		 ImageJFunctions.show(ex.deconvolutionL1TVHuber3D(ex.getConvolvedImgs(),
//		 ex.getKernels(), numIts));
//		 ImageJFunctions.show(ex.deconvolutionL1TVHuber3D(ex.getConvolvedImg0(),
//		 ex.getKernel0(), numIts));
//		
//		 ImageJFunctions.show(ex.deconvolutionL1TGV3D(ex.getConvolvedImgs(),
//		 ex.getKernels(), numIts));
//		 ImageJFunctions.show(ex.deconvolutionL1TGV3D(ex.getConvolvedImg0(),
//		 ex.getKernel0(), numIts));
//		
//		 // KL-Div costfunction
//		 ImageJFunctions.show(ex.deconvolutionKLDivTV3D(ex.getConvolvedImgs(),
//		 ex.getKernels(), numIts));
//		 ImageJFunctions.show(ex.deconvolutionKLDivTV3D(ex.getConvolvedImg0(),
//		 ex.getKernel0(), numIts));
//		
//		 ImageJFunctions.show(ex.deconvolutionKLDivTVHuber3D(ex.getConvolvedImgs(),
//		 ex.getKernels(), numIts));
//		 ImageJFunctions.show(ex.deconvolutionKLDivTVHuber3D(ex.getConvolvedImg0(),
//		 ex.getKernel0(), numIts));
//		
//		 ImageJFunctions.show(ex.deconvolutionKLDivTGV3D(ex.getConvolvedImgs(),
//		 ex.getKernels(), numIts));
//		 ImageJFunctions.show(ex.deconvolutionKLDivTGV3D(ex.getConvolvedImg0(),
//		 ex.getKernel0(), numIts));
//		
//		 // squared L2 costfunction
//		 ImageJFunctions.show(ex.deconvolutionSquaredL2TV3D(ex.getConvolvedImgs(),
//		 ex.getKernels(), numIts));
//		 ImageJFunctions.show(ex.deconvolutionSquaredL2TV3D(ex.getConvolvedImg0(),
//		 ex.getKernel0(), numIts));
//		
//		 ImageJFunctions.show(ex.deconvolutionSquaredL2TVHuber3D(ex.getConvolvedImgs(),
//		 ex.getKernels(), numIts));
//		 ImageJFunctions.show(ex.deconvolutionSquaredL2TVHuber3D(ex.getConvolvedImg0(),
//		 ex.getKernel0(), numIts));
//		
//		 ImageJFunctions.show(ex.deconvolutionSquaredL2TGV3D(ex.getConvolvedImgs(),
//		 ex.getKernels(), numIts));
//		 ImageJFunctions.show(ex.deconvolutionSquaredL2TGV3D(ex.getConvolvedImg0(),
//		 ex.getKernel0(), numIts));
	}

	public Examples3D() {
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
			.getResource("3D_noisy_v0.tif").getPath())) };
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType>[] getNoisyImgs() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("3D_noisy_v0.tif").getPath())), ImagePlusAdapter.wrap(
				IJ.openImage(this.getClass().getResource("3D_noisy_v1.tif")
					.getPath())) };
	}

	private Img<FloatType>[] getConvolvedImg0() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("3D_convolved_v0.tif").getPath())) };
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType>[] getConvolvedImgs() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("3D_convolved_v0.tif").getPath())), ImagePlusAdapter
				.wrap(IJ.openImage(this.getClass().getResource(
					"3D_convolved_v1.tif").getPath())) };
	}

	@SuppressWarnings({ "unchecked" })
	private Img<FloatType>[] getKernel0() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("psf0.tif").getPath())) };
	}
	
	@SuppressWarnings({ "unchecked" })
	private Img<FloatType>[] getKernels() {
		return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.getClass()
			.getResource("3D_kernel_v0.tif").getPath())), ImagePlusAdapter.wrap(
				IJ.openImage(this.getClass().getResource("3D_kernel_v1.tif")
					.getPath())) };
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TV3D(final Img<FloatType>[] imgs,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVL1Denoising3D.class, imgs, numIts, 0.1);
		t = System.currentTimeMillis() - t;
		System.out.println("TVL1-Denoising [" + imgs[0].dimension(0) + ", " +
			imgs[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TVHuber3D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberL1Denoising3D.class, img, numIts, 0.9, 0.01);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberL1-Denoising [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TGV3D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVL1Denoising3D.class, img, numIts, 0.01, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVL1-Denoising [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TV3D(final Img<FloatType>[] img,
		final Img<FloatType> kernel[], final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVL1Deconvolution3D.class, img, kernel, numIts, 0.1);
		t = System.currentTimeMillis() - t;
		System.out.println("TVL1-Deconvolution [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TVHuber3D(final Img<FloatType>[] img,
		final Img<FloatType>[] kernel, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberL1Deconvolution3D.class, img, kernel, numIts, 0.1, 0.05);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberL1-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TGV3D(final Img<FloatType>[] img,
		final Img<FloatType>[] kernel, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVL1Deconvolution3D.class, img, kernel, numIts, 0.01, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVL1-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	// ================================= Kullback-Leibler-Divergence

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTV3D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVKLDivDenoising3D.class, img, numIts, 0.5);
		t = System.currentTimeMillis() - t;
		System.out.println("TVKLDiv-Denoising [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTVHuber3D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberKLDivDenoising3D.class, img, numIts, 0.5, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberKLDiv-Denoising [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTGV3D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVKLDivDenoising3D.class, img, numIts, 0.5, 2.0);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVKLDiv-Denoising [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTV3D(final Img<FloatType>[] img,
		final Img<FloatType>[] kernel, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVKLDivDeconvolution3D.class, img, kernel, numIts, 0.008);
		t = System.currentTimeMillis() - t;
		System.out.println("TVKLDiv-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTVHuber3D(
		final Img<FloatType>[] img, final Img<FloatType>[] kernel,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberKLDivDeconvolution3D.class, img, kernel, numIts, 0.01, 0.05);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberKLDiv-Deconvolution [" + img[0].dimension(
			0) + ", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTGV3D(final Img<FloatType>[] img,
		final Img<FloatType>[] kernel, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVKLDivDeconvolution3D.class, img, kernel, numIts, 0.01, 0.02);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVKLDiv-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	// ================================= Squared-L2-Norm

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingSquaredL2TV3D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVSquaredL2Denoising3D.class, img, numIts, 0.1);
		t = System.currentTimeMillis() - t;
		System.out.println("TVKLDiv-Denoising [" + img[0].dimension(0) + ", " +
			img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingSquaredL2TVHuber3D(
		final Img<FloatType>[] img, final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberSquaredL2Denoising3D.class, img, numIts, 0.1, 0.05);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberSquaredL2-Denoising [" + img[0].dimension(
			0) + ", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingSquaredL2TGV3D(final Img<FloatType>[] img,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVSquaredL2Denoising3D.class, img, numIts, 0.05, 0.1);
		t = System.currentTimeMillis() - t;
		System.out.println("TGVSquaredL2-Denoising [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionSquaredL2TV3D(
		final Img<FloatType>[] img, final Img<FloatType>[] kernel,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVSquaredL2Deconvolution3D.class, img, kernel, numIts, 0.008);
		t = System.currentTimeMillis() - t;
		System.out.println("TVSquaredL2-Deconvolution [" + img[0].dimension(0) +
			", " + img[0].dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts +
			"millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionSquaredL2TVHuber3D(
		final Img<FloatType>[] img, final Img<FloatType>[] kernel,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TVHuberSquaredL2Deconvolution3D.class, img, kernel, numIts, 0.01,
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
	private Img<FloatType> deconvolutionSquaredL2TGV3D(
		final Img<FloatType>[] img, final Img<FloatType>[] kernel,
		final int numIts)
	{

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(
			TGVSquaredL2Deconvolution3D.class, img, kernel, numIts, 0.01, 0.02);
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
