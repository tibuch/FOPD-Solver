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

package net.imagej.ops.fopd.regularizer.tgv.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.solver.AbstractSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * {@link SolverState} for {@link TGVMinimizer2D}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public class TGVMinimizer2DSolverState<T extends RealType<T>> extends
	AbstractSolverState<T>
{

	public TGVMinimizer2DSolverState(final OpService ops,
		final RandomAccessibleInterval<T>[] images)
	{
		super(ops, images, 2);
		regularizerDV = new DualVariables<T>(ops, images[0], 3);
	}

	@Override
	public RandomAccessibleInterval<T> getIntermediateResult(int i) {
		if (i > 1) {
			throw new ArrayIndexOutOfBoundsException(
				"This solver only has two intermediate results.");
		}
		return intermediateResults[i];
	}

	@Override
	public RandomAccessibleInterval<T> getResultImage(int i) {
		if (i > 1) {
			throw new ArrayIndexOutOfBoundsException(
				"This solver only has two results.");
		}
		return results[i];
	}
}
