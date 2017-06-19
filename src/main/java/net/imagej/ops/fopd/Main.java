package net.imagej.ops.fopd;

import java.io.IOException;

import net.imagej.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class Main {

	public static <T extends RealType<T>> void main(String[] args) throws IOException {
		ImageJ ij = new ImageJ();
		ij.ui().showUI();
		Img<T> img = (Img<T>)ij.io().open("/home/tibuch/git-repos/FOPD-Solver/src/main/resources/net/imagej/ops/fopd/2D_noisy_v0.tif");
		ij.ui().show(img);
//		ij.command().run(WrapCommand.class, true, "img", img);
//		ij.command().run(TVL1Denoising2D.class, true, "in", new Img[]{img});
	}

}
