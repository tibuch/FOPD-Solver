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
		ImageJFunctions.show(ex.getNoisyImg());
		ImageJFunctions.show(ex.denoisingL1TV2D(ex.getNoisyImg(), numIts));
		ImageJFunctions.show(ex.denoisingL1TVHuber2D(ex.getNoisyImg(), numIts));
		ImageJFunctions.show(ex.getConvolvedImg());
		ImageJFunctions.show(ex.deconvolutionL1TV2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
		ImageJFunctions.show(ex.deconvolutionL1TVHuber2D(ex.getConvolvedImg(), ex.getKernel(), numIts));
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
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVHuberL1Deconvolution.class, img, kernel, 0.1, 0.05, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println(
				"TVHuberL1-Deconvolution [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t / 1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double) t / numIts + "millisec");
		return result;
	}
}