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

package net.imagej.ops.fopd.costfunction.kldivergence;

import java.util.List;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.costfunction.AbstractCostFunction;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Kullback-Leibler-Divergence as costfunction. 
 * 
 * Ref.: https://de.wikipedia.org/wiki/Kullback-Leibler-Divergenz
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public class KLDivergence<T extends RealType<T>> extends
	AbstractCostFunction<T>
{

	@SuppressWarnings("unchecked")
	public KLDivergence(final OpService ops,
		final List<RandomAccessibleInterval<T>> input,
		final List<LinearOperator<T>> ascentOperator,
		final List<LinearOperator<T>> descentOperator,
		final double descentStepSize)
	{
		this.ascent = ops.op(KLDivergenceAscent.class, SolverState.class,
			input, ascentOperator);
		this.descent = ops.op(KLDivergenceDescent.class, SolverState.class,
			descentOperator, descentStepSize);
	}
}
