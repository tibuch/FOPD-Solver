package net.imagej.ops.fopd;

import net.imagej.ops.OpMatchingService;
import net.imagej.ops.OpService;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;

import ij.IJ;

/**
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class Examples {

	@Parameter
	private OpService ops;

	public static void main(String[] args) {
		Examples ex = new Examples();
		final int numIts = 100;
		// Denoising
//		ImageJFunctions.show(ex.getNoisyImg());
//		// L1 costfunction
//		ImageJFunctions.show(ex.denoisingL1TV2D(ex.getNoisyImg(), numIts));
//		ImageJFunctions.show(ex.denoisingL1TVHuber2D(ex.getNoisyImg(), numIts));
//		ImageJFunctions.show(ex.denoisingL1TGV2D(ex.getNoisyImg(), numIts));
//		// KL-Div costfunction
//		ImageJFunctions.show(ex.denoisingKLDivTV2D(ex.getNoisyImg(), numIts));
//		ImageJFunctions.show(ex.denoisingKLDivTVHuber2D(ex.getNoisyImg(), numIts));
//		ImageJFunctions.show(ex.denoisingKLDivTGV2D(ex.getNoisyImg(), numIts));
		// squared L2 costfunction
//		ImageJFunctions.show(ex.denoisingSquaredL2TV2D(ex.getNoisyImg(), numIts));
//		ImageJFunctions.show(ex.denoisingSquaredL2TVHuber2D(ex.getNoisyImg(), numIts));
//		ImageJFunctions.show(ex.denoisingSquaredL2TGV2D(ex.getNoisyImg(), numIts));
		
		// Deconvolution
		ImageJFunctions.show(ex.getConvolvedImg());
//		// L1 costfunction
//		ImageJFunctions.show(ex.deconvolutionL1TV2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
//		ImageJFunctions.show(ex.deconvolutionL1TVHuber2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
//		ImageJFunctions.show(ex.deconvolutionL1TGV2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
//		// KL-Div costfunction
//		ImageJFunctions.show(ex.deconvolutionKLDivTV2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
//		ImageJFunctions.show(ex.deconvolutionKLDivTVHuber2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
//		ImageJFunctions.show(ex.deconvolutionKLDivTGV2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
//		// squared L2 costfunction
		ImageJFunctions.show(ex.deconvolutionSquaredL2TV2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
		ImageJFunctions.show(ex.deconvolutionSquaredL2TVHuber2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
		ImageJFunctions.show(ex.deconvolutionSquaredL2TGV2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
	}

	public Examples() {
		Context context = new Context(OpService.class, OpMatchingService.class, CacheService.class);
		context.inject(this);
	}

	private Img<FloatType> getNoisyImg() {
		return ImagePlusAdapter.wrap(IJ.openImage(this.getClass().getResource("2D_noise.tif").getPath()));
	}

	private Img<FloatType> getConvolvedImg() {
		return ImagePlusAdapter.wrap(IJ.openImage(this.getClass().getResource("2D_convolved.tif").getPath()));
	}

	private Img<FloatType> getKernel() {
		return ImagePlusAdapter.wrap(IJ.openImage(this.getClass().getResource("2D_kernel.tif").getPath()));
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TV2D(final Img<FloatType> img, final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVL1Denoising.class, img, 2, numIts);
		t = System.currentTimeMillis() - t;
		System.out
				.println("TVL1-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TVHuber2D(final Img<FloatType> img, final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVHuberL1Denoising.class, img, 2, 0.02, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TVHuberL1-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TGV2D(final Img<FloatType> img, final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TGVL1Denoising.class, img, 1.0, 2.0, numIts);
		t = System.currentTimeMillis() - t;
		System.out
				.println("TGVL1-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TV2D(final Img<FloatType> img, final Img<FloatType> kernel,
			final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVL1Deconvolution.class, img, kernel, 0.08, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TVL1-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TVHuber2D(final Img<FloatType> img, final Img<FloatType> kernel,
			final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVHuberL1Deconvolution.class, img, kernel, 0.1, 0.05,
				numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TVHuberL1-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionL1TGV2D(final Img<FloatType> img, final Img<FloatType> kernel,
			final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TGVL1Deconvolution.class, img, kernel, 0.1, 0.2, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TGVL1-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	// ================================= Kullback-Leibler-Divergence

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTV2D(final Img<FloatType> img, final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVKLDivDenoising.class, img, 0.5, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TVKLDiv-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTVHuber2D(final Img<FloatType> img, final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVHuberKLDivDenoising.class, img, 0.5, 0.02, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TVHuberKLDiv-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingKLDivTGV2D(final Img<FloatType> img, final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TGVKLDivDenoising.class, img, 0.5, 2.0, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TGVKLDiv-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTV2D(final Img<FloatType> img, final Img<FloatType> kernel,
			final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVKLDivDeconvolution.class, img, kernel, 0.008, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TVKLDiv-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTVHuber2D(final Img<FloatType> img, final Img<FloatType> kernel,
			final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVHuberKLDivDeconvolution.class, img, kernel, 0.01, 0.05,
				numIts);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberKLDiv-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: "
				+ t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> deconvolutionKLDivTGV2D(final Img<FloatType> img, final Img<FloatType> kernel,
			final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TGVKLDivDeconvolution.class, img, kernel, 0.01, 0.02,
				numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TGVKLDiv-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}
	
	// ================================= Squared-L2-Norm

		@SuppressWarnings("unchecked")
		private Img<FloatType> denoisingSquaredL2TV2D(final Img<FloatType> img, final int numIts) {

			long t = System.currentTimeMillis();
			final Img<FloatType> result = (Img<FloatType>) ops.run(TVSquaredL2Denoising.class, img, 0.1, numIts);
			t = System.currentTimeMillis() - t;
			System.out.println(
					"TVKLDiv-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
			System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
			return result;
		}

		@SuppressWarnings("unchecked")
		private Img<FloatType> denoisingSquaredL2TVHuber2D(final Img<FloatType> img, final int numIts) {

			long t = System.currentTimeMillis();
			final Img<FloatType> result = (Img<FloatType>) ops.run(TVHuberSquaredL2Denoising.class, img, 0.1, 0.05, numIts);
			t = System.currentTimeMillis() - t;
			System.out.println(
					"TVHuberSquaredL2-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
			System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
			return result;
		}

		@SuppressWarnings("unchecked")
		private Img<FloatType> denoisingSquaredL2TGV2D(final Img<FloatType> img, final int numIts) {

			long t = System.currentTimeMillis();
			final Img<FloatType> result = (Img<FloatType>) ops.run(TGVSquaredL2Denoising.class, img, 0.05, 0.1, numIts);
			t = System.currentTimeMillis() - t;
			System.out.println(
					"TGVSquaredL2-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
			System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
			return result;
		}

		@SuppressWarnings("unchecked")
		private Img<FloatType> deconvolutionSquaredL2TV2D(final Img<FloatType> img, final Img<FloatType> kernel,
				final int numIts) {

			long t = System.currentTimeMillis();
			final Img<FloatType> result = (Img<FloatType>) ops.run(TVSquaredL2Deconvolution.class, img, kernel, 0.008, numIts);
			t = System.currentTimeMillis() - t;
			System.out.println(
					"TVSquaredL2-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
			System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
			return result;
		}

		@SuppressWarnings("unchecked")
		private Img<FloatType> deconvolutionSquaredL2TVHuber2D(final Img<FloatType> img, final Img<FloatType> kernel,
				final int numIts) {

			long t = System.currentTimeMillis();
			final Img<FloatType> result = (Img<FloatType>) ops.run(TVHuberSquaredL2Deconvolution.class, img, kernel, 0.01, 0.05,
					numIts);
			t = System.currentTimeMillis() - t;
			System.out.println("TVHuberSquaredL2-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: "
					+ t / 1000.0 + "sec");
			System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
			return result;
		}

		@SuppressWarnings("unchecked")
		private Img<FloatType> deconvolutionSquaredL2TGV2D(final Img<FloatType> img, final Img<FloatType> kernel,
				final int numIts) {

			long t = System.currentTimeMillis();
			final Img<FloatType> result = (Img<FloatType>) ops.run(TGVSquaredL2Deconvolution.class, img, kernel, 0.01, 0.02,
					numIts);
			t = System.currentTimeMillis() - t;
			System.out.println(
					"TGVSquaredL2-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
			System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
			return result;
		}
}
