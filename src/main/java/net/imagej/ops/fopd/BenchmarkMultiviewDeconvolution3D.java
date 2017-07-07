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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import net.imagej.ops.OpMatchingService;
import net.imagej.ops.OpService;
import net.imagej.ops.fopd.energy.deconvolution.Benchmark;
import net.imagej.ops.fopd.energy.deconvolution.TGVKLDivDeconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TGVL1Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TGVSquaredL2Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberKLDivDeconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberL1Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVHuberSquaredL2Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVKLDivDeconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVL1Deconvolution3D;
import net.imagej.ops.fopd.energy.deconvolution.TVSquaredL2Deconvolution3D;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.Context;
import org.scijava.cache.CacheService;
import org.scijava.plugin.Parameter;

/**
 * This is a benchmark for all multiview deconvolution variational models and
 * the Richardson-Lucy multiview deconvolution. Every algorithm is first
 * initialized (init), then warmed up for 10 iterations and then benchmarked for
 * 200 iterations.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class BenchmarkMultiviewDeconvolution3D {

	@Parameter
	private OpService ops;

	public static void main(String[] args) {
		BenchmarkMultiviewDeconvolution3D benchmark = new BenchmarkMultiviewDeconvolution3D();

		final Img<FloatType>[] convolvedImgs = benchmark.getConvolvedImgs();
		final Img<FloatType>[] kernels = benchmark.getKernels();

		String[] measurements = benchmark.benchmark(MultiViewRLDeconvolution.class, convolvedImgs, kernels, 0.1);
		System.out.println(
				"RL -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: " + measurements[2]);

		measurements = benchmark.benchmark(TVL1Deconvolution3D.class, convolvedImgs, kernels, 0.1);
		System.out.println("TVL1 -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);
		measurements = benchmark.benchmark(TVSquaredL2Deconvolution3D.class, convolvedImgs, kernels, 0.1);
		System.out.println("TVSqrL2 -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);
		measurements = benchmark.benchmark(TVKLDivDeconvolution3D.class, convolvedImgs, kernels, 0.1);
		System.out.println("TVKLDiv -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);

		measurements = benchmark.benchmark(TVHuberL1Deconvolution3D.class, convolvedImgs, kernels, 0.1, 0.05);
		System.out.println("TVHuberL1 -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);
		measurements = benchmark.benchmark(TVHuberSquaredL2Deconvolution3D.class, convolvedImgs, kernels, 0.1, 0.05);
		System.out.println("TVHuberSqrL2 -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);
		measurements = benchmark.benchmark(TVHuberKLDivDeconvolution3D.class, convolvedImgs, kernels, 0.1, 0.05);
		System.out.println("TVHuberKLDiv -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);

		measurements = benchmark.benchmark(TGVL1Deconvolution3D.class, convolvedImgs, kernels, 0.1, 0.2);
		System.out.println("TGVL1 -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);
		measurements = benchmark.benchmark(TGVSquaredL2Deconvolution3D.class, convolvedImgs, kernels, 0.1, 0.2);
		System.out.println("TGVSqrL2 -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);
		measurements = benchmark.benchmark(TGVKLDivDeconvolution3D.class, convolvedImgs, kernels, 0.1, 0.2);
		System.out.println("TGVKLDiv -> init: " + measurements[0] + ", warmup: " + measurements[1] + ", benchmark: "
				+ measurements[2]);

		System.exit(0);
	}

	public BenchmarkMultiviewDeconvolution3D() {
		Context context = new Context(OpService.class, OpMatchingService.class, CacheService.class);
		context.inject(this);
	}

	public String[] benchmark(final Class op, final Img<FloatType>[] convolvedImgs, final Img<FloatType>[] kernels,
			final double... args) {

		long initTime = System.currentTimeMillis();
		Benchmark<FloatType> solver = null;
		if (args.length == 1) {
			solver = (Benchmark<FloatType>) ops.op(op, convolvedImgs, kernels, 1, args[0]);
		} else if (args.length == 2) {
			solver = (Benchmark<FloatType>) ops.op(op, convolvedImgs, kernels, 1, args[0], args[1]);
		}
		solver.setNumIterations(1);
		solver.calculate();
		initTime = System.currentTimeMillis() - initTime;

		solver.setNumIterations(10);
		long warmupTime = System.currentTimeMillis();
		solver.calculate();
		warmupTime = System.currentTimeMillis() - warmupTime;

		solver.setNumIterations(200);
		long benchmarkTime = System.currentTimeMillis();
		solver.calculate();
		benchmarkTime = System.currentTimeMillis() - benchmarkTime;

		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SSS", Locale.GERMAN);
		String[] measurments = new String[3];

		c.setTimeInMillis(initTime);
		measurments[0] = sdf.format(c.getTimeInMillis());
		c.setTimeInMillis(warmupTime);
		measurments[1] = sdf.format(c.getTimeInMillis());
		c.setTimeInMillis(benchmarkTime);
		measurments[2] = sdf.format(c.getTimeInMillis());

		return measurments;
	}

	@SuppressWarnings("unchecked")
	private Img<FloatType>[] getConvolvedImgs() {
		return new Img[] { ops.create().img(new int[] { 150, 150, 150 }),
				ops.create().img(new int[] { 150, 150, 150 }) };
	}

	@SuppressWarnings({ "unchecked" })
	private Img<FloatType>[] getKernels() {
		return new Img[] { ops.create().img(new int[] { 21, 21, 51 }), ops.create().img(new int[] { 21, 51, 21 }) };
	}
}
