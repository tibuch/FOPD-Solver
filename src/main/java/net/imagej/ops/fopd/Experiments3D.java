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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

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
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;

import ij.IJ;

/**
 * Experiments computing the results of multiview deconvolution of two 3D views
 * for every available variational deconvolution model and Richardson-Lucy
 * multiview deconvolution.
 * 
 * The datasets have to be generated with the Matlab scripts, ground truth, psf0
 * and psf90 provided in src/main/resources/net/imagej/ops/fopd/TestData.tag.gz.
 * 
 * Matlab functions 'createDataWithPoissonNoise( groundtruth, psf0, psf90, snr,
 * sliceFactor )' and 'createDataWithGaussianNoise(groundtruth, psf0, psf90,
 * mean, sigma, sliceFactor )' generate the datasets. 
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class Experiments3D {

	private static double lambda;
	@Parameter
	private OpService ops;

	private String[] pathsGaussianNNInterpolation = new String[] {
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_1/sample_factor_1/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_1/sample_factor_2/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_1/sample_factor_4/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_1/sample_factor_8/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_01/sample_factor_1/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_01/sample_factor_2/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_01/sample_factor_4/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_01/sample_factor_8/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_001/sample_factor_1/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_001/sample_factor_2/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_001/sample_factor_4/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/gaussianNoise/sigma_0_001/sample_factor_8/", };

	private String[] pathsPoissonNNInterpolation = new String[] {
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_10/sample_factor_1/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_10/sample_factor_2/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_10/sample_factor_4/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_10/sample_factor_8/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_100/sample_factor_1/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_100/sample_factor_2/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_100/sample_factor_4/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_100/sample_factor_8/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_1000/sample_factor_1/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_1000/sample_factor_2/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_1000/sample_factor_4/",
			"/home/tibuch/Documents/MasterThesis_tibuch/data/nn_interpolation/poissonNoise/SNR_1000/sample_factor_8/", };

	public static void main(String[] args) throws FileNotFoundException {
		Experiments3D ex = new Experiments3D();
		final int numIts = 100;
		double[] rlLambda = new double[] { 0.01, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0 };
		Img<FloatType>[] views;
		Img<FloatType>[] psfs;
		Img<FloatType> result;
		long time;
		for (int i = 0; i < rlLambda.length; i++) {
			lambda = rlLambda[i];
			// provide the locations of the datasets [dataset = {view0, psf0,
			// view90, psf90}]
			ImageLoader loader = ex.getLoader(ex.pathsPoissonNNInterpolation);
			while (loader.hasNext()) {
				System.gc();
				loader.fwd();
				views = loader.getViews();
				psfs = loader.getPSFs();

				loader.setEnergy("RL");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(MultiViewRLDeconvolution.class, views, psfs, numIts, lambda);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TGVL1");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TGVL1Deconvolution3D.class, views, psfs, numIts, lambda,
						2 * lambda);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TVL1");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TVL1Deconvolution3D.class, views, psfs, numIts, lambda);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TVHuberL1");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TVHuberL1Deconvolution3D.class, views, psfs, numIts, lambda, 0.05);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TGVKLDiv");

				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TGVKLDivDeconvolution3D.class, views, psfs, numIts, lambda,
						2 * lambda);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TVKLDiv");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TVKLDivDeconvolution3D.class, views, psfs, numIts, lambda);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TVHuberKLDiv");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TVHuberKLDivDeconvolution3D.class, views, psfs, numIts, lambda,
						0.05);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TGVL2Sqr");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TGVSquaredL2Deconvolution3D.class, views, psfs, numIts, lambda,
						2 * lambda);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TVL2Sqr");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TVSquaredL2Deconvolution3D.class, views, psfs, numIts, lambda);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());

				loader.setEnergy("TVHuberL2Sqr");
				time = System.currentTimeMillis();
				loader.createDirs();
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getResultPath())), true));
				result = (Img<FloatType>) ex.ops.run(TVHuberSquaredL2Deconvolution3D.class, views, psfs, numIts, lambda,
						0.05);
				time = System.currentTimeMillis() - time;
				System.setOut(
						new PrintStream(new BufferedOutputStream(new FileOutputStream(loader.getTimingPath())), true));
				System.out.println(time);
				IJ.saveAsTiff(ImageJFunctions.wrap(result, "result"), loader.getImagePath());
			}
		}
		System.exit(0);
	}

	public Experiments3D() {
		Context context = new Context(OpService.class, OpMatchingService.class, CacheService.class);
		context.inject(this);
	}

	public ImageLoader getLoader(final String[] paths) {
		return new ImageLoader(paths);
	}

	private class ImageLoader {

		private String[] paths;
		private int current = -1;
		private String e;

		public ImageLoader(final String[] paths) {
			this.paths = paths;
		}

		public boolean hasNext() {
			return current < paths.length - 1;
		}

		public void fwd() {
			this.current++;
		}

		public Img<FloatType>[] getViews() {
			return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.paths[current] + "aligned_view_0.tif")),
					ImagePlusAdapter.wrap(IJ.openImage(this.paths[current] + "aligned_view_90.tif")) };
		}

		public Img<FloatType>[] getPSFs() {
			return new Img[] { ImagePlusAdapter.wrap(IJ.openImage(this.paths[current] + "aligned_view_psf_0.tif")),
					ImagePlusAdapter.wrap(IJ.openImage(this.paths[current] + "aligned_view_psf_90.tif")) };
		}

		public void setEnergy(final String e) {
			this.e = e;
		}

		public ArrayList<String> getPSFFileList() {
			ArrayList<String> l = new ArrayList<String>();
			l.add(this.paths[current] + "aligned_view_psf_0.tif");
			l.add(this.paths[current] + "aligned_view_psf_90.tif");
			return l;
		}

		public void createDirs() {
			File f = new File(this.paths[current].replace("data", "results/lambda_" + lambda) + e + "/");
			f.mkdirs();
		}

		public String getResultPath() {
			return this.paths[current].replace("data", "results/lambda_" + lambda) + e + "/statistic.txt";
		}

		public String getTimingPath() {
			return this.paths[current].replace("data", "results/lambda_" + lambda) + e + "/timing.txt";
		}

		public String getImagePath() {
			return this.paths[current].replace("data", "results/lambda_" + lambda) + e + "/deconvolution.tif";
		}

		public String getSourceDir() {
			return this.paths[current];
		}

		public String getResultDir() {
			return this.paths[current].replace("data", "results/lambda_" + lambda);
		}

		public void setCurrent(final int c) {
			this.current = c;
		}
	}
}
