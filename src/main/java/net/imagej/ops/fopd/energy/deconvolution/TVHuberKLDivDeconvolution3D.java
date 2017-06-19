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

package net.imagej.ops.fopd.energy.deconvolution;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.kldivergence.KLDivergence;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber3D;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * A 3D deconvolved algorithm which uses TV-Huber as {@link Regularizer} and
 * takes the L1-Norm as {@link CostFunction}.
 * 
 * Energy: E(u) = lambda * TV_Huber(u) + |u*k - f|_1, where u is the deconvolved
 * solution, lambda is the smoothness weight, k is the known kernel, * is the
 * convolution operator and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
@Plugin(type = UnaryHybridCF.class)
public class TVHuberKLDivDeconvolution3D<T extends RealType<T>> extends AbstractDeconvoltuion<T> {

	@Parameter
	private double lambda;

	@Parameter
	private double alpha;

	@Override
	SolverState<T> getSolverState(RandomAccessibleInterval<T>[] input) {
		return new DefaultSolverState<T>(ops, input, 1);
	}

	@Override
	Regularizer<T> getRegularizer(final double numViews) {
		return new TVHuber3D<T>(ops, lambda, alpha, (1.0 / (6.0 + numViews)));
	}

	@Override
	CostFunction<T> getCostFunction(RandomAccessibleInterval<T>[] input, LinearOperator<T>[] ascentConvolver,
			LinearOperator<T>[] descentConvolver) {
		return new KLDivergence<T>(ops, input, ascentConvolver, descentConvolver, (1.0 / (6.0 + input.length)));
	}
}
