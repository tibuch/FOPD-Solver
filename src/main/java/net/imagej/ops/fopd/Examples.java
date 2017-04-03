package net.imagej.ops.fopd;

import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;

import ij.IJ;
import net.imagej.ops.OpMatchingService;
import net.imagej.ops.OpService;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

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
		Img<FloatType> img = ex.getImg(); 

		ImageJFunctions.show(img);
		ImageJFunctions.show(ex.denoisingL1TV2D(img, numIts));
		ImageJFunctions.show(ex.denoisingL1TVHuber2D(img, numIts));
	}

	public Examples() {
		Context context = new Context(OpService.class, OpMatchingService.class, CacheService.class);
		context.inject(this);
	}
	
	private Img<FloatType> getImg() {
		return ImagePlusAdapter.wrap(IJ.openImage(this.getClass().getResource("2D_noise.tif").getPath()));
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TV2D(final Img<FloatType> img, final int numIts) {
		
		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVL1Denoising.class, img, 2, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println("TVL1-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t/1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double)t/numIts + "millisec");
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private Img<FloatType> denoisingL1TVHuber2D(final Img<FloatType> img, final int numIts) {

		long t = System.currentTimeMillis();
		final Img<FloatType> result = (Img<FloatType>) ops.run(TVHuberL1Denoising.class, img, 2, 0.02, numIts);
		t = System.currentTimeMillis() - t;
		System.out.println("TVHuberL1-Denoising [" + img.dimension(0) + ", " + img.dimension(1) + "]: " + t/1000.0 + "sec");
		System.out.println("Time/Iteration: " + (double)t/numIts + "millisec");
		return result;
	}
}
