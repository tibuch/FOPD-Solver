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

import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * DualVariables holds as many dual variables as needed.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public class DualVariables<T extends RealType<T>> {

	private final RandomAccessibleInterval<T>[] dualVariables;

	private final int numVariables;

	private final T type;

	@SuppressWarnings("unchecked")
	public DualVariables(final OpService ops,
		final RandomAccessibleInterval<T> img, final int numDualVariables)
	{
		dualVariables = new RandomAccessibleInterval[numDualVariables];
		for (int i = 0; i < numDualVariables; i++) {
			this.dualVariables[i] = (RandomAccessibleInterval<T>) ops.create()
				.img(img);
		}
		this.numVariables = dualVariables.length;
		this.type = dualVariables[0].randomAccess().get().createVariable();
	}

	public RandomAccessibleInterval<T> getDualVariable(final int i) {
		if (i <= numVariables) {
			return dualVariables[i];
		}
		throw new ArrayIndexOutOfBoundsException("Only " + numVariables +
			" dual variables are available. Dual variable " + i +
			" was requested.");
	}

	public T getType() {
		return this.type.copy();
	}

	public int getNumDualVariables() {
		return this.numVariables;
	}

	public RandomAccessibleInterval<T>[] getAllDualVariables() {
		return this.dualVariables;
	}
}
