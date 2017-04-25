
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
