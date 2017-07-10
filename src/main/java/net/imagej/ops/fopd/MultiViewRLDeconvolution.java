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

import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops;
import net.imagej.ops.Ops.Map;
import net.imagej.ops.fopd.helper.AveragePerPixelDifference;
import net.imagej.ops.fopd.helper.Default01Clipper;
import net.imagej.ops.fopd.operator.FastConvolver;
import net.imagej.ops.map.MapBinaryComputers.RAIAndIIToRAIParallel;
import net.imagej.ops.map.MapIIInplaceParallel;
import net.imagej.ops.special.computer.BinaryComputerOp;
import net.imagej.ops.special.computer.Computers;
import net.imagej.ops.special.computer.UnaryComputerOp;
import net.imagej.ops.special.function.AbstractBinaryFunctionOp;
import net.imagej.ops.special.function.BinaryFunctionOp;
import net.imagej.ops.special.function.Functions;
import net.imagej.ops.special.inplace.Inplaces;
import net.imagej.ops.special.inplace.UnaryInplaceOp;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * This is a ImageJ-Ops implementation of the Richardson-Lucy multiview
 * deconvolution from https://github.com/fiji/SPIM_Registration/blob/
 * 01f72ea988cffd1f46146794a89a4e6317dfe87c/src/main/java/mpicbg/spim/
 * postprocessing/deconvolution/LucyRichardsonMultiViewDeconvolution.java
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
@Plugin(type = Op.class)
public class MultiViewRLDeconvolution<T extends RealType<T>>
		extends AbstractBinaryFunctionOp<RandomAccessibleInterval<T>[], RandomAccessibleInterval<T>[], Img<T>> {

	@Parameter
	private int numIts;

	@Parameter
	private double lambda;

	@Parameter
	private OpService ops;

	private RandomAccessibleInterval<T> tmp;

	private FastConvolver<T>[] conv;

	private FastConvolver<T>[] convFlipped;

	private Img<T> u;

	private double w;

	private RAIAndIIToRAIParallel<T, T, T> mapperDivide;

	private RAIAndIIToRAIParallel<T, T, T> mapperMul;

	private Converter converter;

	private Img<T> oldU;

	private RAIAndIIToRAIParallel<T, T, T> mapperAdd;

	private UnaryComputerOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> copyComputer;

	private BinaryFunctionOp<RandomAccessibleInterval, RandomAccessibleInterval, double[]> avgDifference;

	private MapIIInplaceParallel<T> clipperMapper;

	private Img<T> zero;

	private RAIAndIIToRAIParallel<T, T, T> mapperSub;

	private Converter<T, T> avgConverter;

	@SuppressWarnings("unchecked")
	public Img<T> calculate(final RandomAccessibleInterval<T>[] input, final RandomAccessibleInterval<T>[] kernel) {

		init(input, kernel);
		double[] statistic = new double[3];

		for (int i = 0; i < numIts; i++) {
			copyComputer.compute(u, oldU);
			for (int j = 0; j < input.length; j++) {
				mapperDivide.compute(input[j], (IterableInterval<T>) conv[j].calculate(u), tmp);
				mapperMul.compute(u, (IterableInterval<T>) convFlipped[j].calculate(tmp), tmp);
				mapperSub.compute(Converters.convert(tmp, converter, tmp.randomAccess().get()), u, tmp);
				mapperAdd.compute(Converters.convert(tmp, avgConverter, tmp.randomAccess().get()), u, tmp);
				copyComputer.compute(tmp, u);
			}
			statistic = avgDifference.calculate(u, oldU);
			System.out.println(statistic[0] + ", " + statistic[1] + ", " + statistic[2] + ";");
		}

		return u;
	}

	@SuppressWarnings("unchecked")
	private void init(final RandomAccessibleInterval<T>[] input, final RandomAccessibleInterval<T>[] kernel) {
		zero = (Img<T>) ops.create().img(input[0]);
		tmp = (RandomAccessibleInterval<T>) ops.create().img(input[0]);
		conv = new FastConvolver[input.length];
		convFlipped = new FastConvolver[input.length];
		RandomAccess<T>[] vc = new RandomAccess[input.length];

		for (int i = 0; i < input.length; i++) {
			conv[i] = ops.op(FastConvolver.class, input[i], kernel[i]);
			if (kernel[i].numDimensions() == 2) {
				convFlipped[i] = ops.op(FastConvolver.class, input[i],
						Views.invertAxis(Views.invertAxis(ops.copy().rai(kernel[i]), 0), 1));
			} else if (kernel[i].numDimensions() == 3) {
				convFlipped[i] = ops.op(FastConvolver.class, input[i],
						Views.invertAxis(Views.invertAxis(Views.invertAxis(ops.copy().rai(kernel[i]), 0), 1), 2));
			}

			vc[i] = input[i].randomAccess();
		}

		u = (Img<T>) ops.create().img(input[0]);
		oldU = (Img<T>) ops.create().img(input[0]);
		Cursor<T> cu = u.cursor();

		double val = 0;
		w = 1 / (double) input.length;
		while (cu.hasNext()) {
			cu.next();
			val = 0;
			for (int i = 0; i < input.length; i++) {
				vc[i].setPosition(cu);
				val += w * vc[i].get().getRealDouble();
			}
			cu.get().setReal(val);
		}

		T type = input[0].randomAccess().get().createVariable();
		mapperDivide = (RAIAndIIToRAIParallel<T, T, T>) ops.op(Map.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, IterableInterval.class, BinaryComputerOp.class);
		mapperDivide.setOp(Computers.binary(ops, Ops.Math.Divide.class, type, type, type));

		mapperMul = (RAIAndIIToRAIParallel<T, T, T>) ops.op(Map.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, IterableInterval.class, BinaryComputerOp.class);
		mapperMul.setOp(Computers.binary(ops, Ops.Math.Multiply.class, type, type, type));

		mapperAdd = (RAIAndIIToRAIParallel<T, T, T>) ops.op(Map.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, IterableInterval.class, BinaryComputerOp.class);
		mapperAdd.setOp(Computers.binary(ops, Ops.Math.Add.class, type, type, type));
		
		mapperSub = (RAIAndIIToRAIParallel<T, T, T>) ops.op(Map.class, RandomAccessibleInterval.class,
				RandomAccessibleInterval.class, IterableInterval.class, BinaryComputerOp.class);
		mapperSub.setOp(Computers.binary(ops, Ops.Math.Subtract.class, type, type, type));

		converter = new Converter<T, T>() {

			@SuppressWarnings("hiding")
			@Override
			public void convert(T input, T output) {
				double val = input.getRealDouble();
				double nextValue = 0.0001;
				if (val > 0) {
					nextValue = (Math.sqrt(1 + 2.0 * lambda * val) - 1.0) / lambda;
				} 
				if (Double.isNaN(nextValue))
					nextValue = 0.0001;
				else
					nextValue = Math.max(0.0001, nextValue);
				
				output.setReal(nextValue);
				
			}
		};
		
		avgConverter = new Converter<T, T>() {

			@SuppressWarnings("hiding")
			@Override
			public void convert(T in, T out) {
				out.setReal(in.getRealDouble() * (1/(double)input.length));
				
			}
		};

		copyComputer = Computers.unary(ops, Ops.Copy.RAI.class, input[0], input[0]);

		avgDifference = Functions.binary(ops, AveragePerPixelDifference.class, double[].class,
				RandomAccessibleInterval.class, RandomAccessibleInterval.class);

		clipperMapper = (MapIIInplaceParallel<T>) ops.op(Map.class, IterableInterval.class, UnaryInplaceOp.class);
		clipperMapper.setOp((UnaryInplaceOp<T, T>) Inplaces.unary(ops, Default01Clipper.class, type));
	}
}
