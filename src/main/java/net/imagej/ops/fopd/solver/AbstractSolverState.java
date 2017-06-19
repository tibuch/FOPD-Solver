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

package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Abstract implementation of {@link SolverState}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public class AbstractSolverState<T extends RealType<T>> implements SolverState<T> {

	protected DualVariables<T> regularizerDV;
	protected DualVariables<T> costFunctionDV;
	protected RandomAccessibleInterval<T>[] intermediateResults;
	protected RandomAccessibleInterval<T>[] results;
	protected T type;
	protected int numViews;

	@SuppressWarnings("unchecked")
	public AbstractSolverState(final OpService ops, final RandomAccessibleInterval<T>[] images, final int numResults) {
		this.regularizerDV = new DualVariables<T>(ops, images[0], numResults * images[0].numDimensions());
		this.costFunctionDV = new DualVariables<T>(ops, images[0], images.length);
		this.numViews = images.length;

		this.intermediateResults = new RandomAccessibleInterval[numResults];
		this.results = new RandomAccessibleInterval[numResults];

		for (int i = 0; i < numResults; i++) {
			intermediateResults[i] = (RandomAccessibleInterval<T>) ops.create().img(images[0]);
			results[i] = (RandomAccessibleInterval<T>) ops.create().img(images[0]);
		}

		this.type = images[0].randomAccess().get().createVariable();
	}

	public RandomAccessibleInterval<T> getResultImage(final int i) {
		if (i >= results.length) {
			throw new ArrayIndexOutOfBoundsException("This solver has only " + results.length + " results.");
		}
		return this.results[0];
	}

	public DualVariables<T> getRegularizerDV() {
		return this.regularizerDV;
	}

	public DualVariables<T> getCostFunctionDV() {
		return this.costFunctionDV;
	}

	public T getType() {
		return this.type;
	}

	public RandomAccessibleInterval<T> getIntermediateResult(final int i) {
		if (i >= intermediateResults.length) {
			throw new ArrayIndexOutOfBoundsException(
					"This solver has only " + intermediateResults.length + " intermediate results.");
		}
		return this.intermediateResults[0];
	}

	public SolverState<T> getSubSolverState(int i) {
		throw new ArrayIndexOutOfBoundsException("This solver has no sub-solver.");
	}

	public int getNumViews() {
		return this.numViews;
	}

	public int numResultImages() {
		return this.results.length;
	}

	public int numIntermediateResults() {
		return this.intermediateResults.length;
	}

}
