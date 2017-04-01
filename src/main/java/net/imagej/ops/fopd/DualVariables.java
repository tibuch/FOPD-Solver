package net.imagej.ops.fopd;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * DualVariables holds as many dual variables as needed.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public class DualVariables<T extends RealType<T>> {

	private final RandomAccessibleInterval<T>[] dualVariables;

	private final int numVariables;

	private final T type;

	public DualVariables(final RandomAccessibleInterval<T>... dualVariables) {
		this.dualVariables = dualVariables;
		this.numVariables = dualVariables.length;
		this.type = dualVariables[0].randomAccess().get().copy();
		this.type.setZero();
	}

	public RandomAccessibleInterval<T> getDualVariable(final int i) {
		if (i <= numVariables) {
			return dualVariables[i];
		} else {
			throw new ArrayIndexOutOfBoundsException(
					"Only " + numVariables + " dual variables are available. Dual variable " + i + " was requested.");
		}
	}

	public T getType() {
		return this.type.copy();
	}

	public int getNumDualVariables() {
		return this.numVariables;
	}

	public RandomAccessibleInterval<T>[] getAllDUalVariables() {
		return this.dualVariables;
	}
}
