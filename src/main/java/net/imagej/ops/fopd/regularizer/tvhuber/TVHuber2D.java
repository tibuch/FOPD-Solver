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

package net.imagej.ops.fopd.regularizer.tvhuber;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.regularizer.AbstractRegularizer;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation2DDescent;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.type.numeric.RealType;

/**
 * TV-Huber implementation for one 2D image. Where the L2-Norm in TV is replace
 * by the Huber-Norm. 
 * Huber_alpha(u) = 1/2 * u^2				, for |u| <= alpha 
 * 					alpha(|u| - 1/2 alpha)  , otherwise
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class TVHuber2D<T extends RealType<T>> extends AbstractRegularizer<T> {

	@SuppressWarnings("unchecked")
	public TVHuber2D(final OpService ops, final double lambda,
		final double alpha, final double descentStepSize)
	{
		this.ascent = ops.op(TVHuber2DAscent.class, SolverState.class, lambda,
			alpha);
		this.descent = ops.op(TotalVariation2DDescent.class, SolverState.class,
			descentStepSize);
	}
}
