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
import net.imagej.ops.fopd.energy.denoising.TVHuberL1Denoising2D;
import net.imagej.ops.fopd.energy.denoising.TVHuberL1Denoising3D;
import net.imagej.ops.fopd.energy.denoising.TVHuberSquaredL2Denoising3D;
import net.imagej.ops.fopd.energy.denoising.TVKLDivDenoising3D;
import net.imagej.ops.fopd.energy.denoising.TVL1Denoising3D;
import net.imagej.ops.fopd.energy.denoising.TVSquaredL2Denoising3D;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;

/**
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class CryoProofOfConcept {

	@Parameter
	private OpService ops;
	private ImageJ ij;

	public static void main(String[] args) {
		CryoProofOfConcept ex = new CryoProofOfConcept();
		ex.showIJ();
//		ex.denoiseSingle();
		ex.multiViewDenoise();
	}

	public CryoProofOfConcept() {
		Context context = new Context(OpService.class, OpMatchingService.class,
			CacheService.class);
		context.inject(this);
	}

	public void showIJ() {
		ij = new ImageJ();
	}


	@SuppressWarnings("unchecked")
	private Img<FloatType>[] getNoisyImgs() {
		Img[] imgs = new Img[] { 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/01.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/02.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/03.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/04.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/05.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/06.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/07.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/08.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/09.tif")), 
				ImagePlusAdapter.wrap(IJ.openImage("/home/tibuch/CRY-EM_frames/normalizedSlices/10.tif")) };
		
		
		return imgs;
	}
	
	private void denoiseSingle() {
		TVHuberL1Denoising2D denoiser = (TVHuberL1Denoising2D) ops.op(TVHuberL1Denoising2D.class, getNoisyImgs(), 100, 0.8, 0.01);
		for (int i = 0; i < getNoisyImgs().length; i++) {
			ImageJFunctions.show(denoiser.calculate(new RandomAccessibleInterval[]{getNoisyImgs()[i]}));
		}
	}
	
	private void multiViewDenoise() {
		TVHuberL1Denoising2D denoiser = (TVHuberL1Denoising2D) ops.op(TVHuberL1Denoising2D.class, getNoisyImgs(), 500, 0.9, 0.05);
		ImageJFunctions.show(denoiser.calculate(getNoisyImgs()));
	}
}
