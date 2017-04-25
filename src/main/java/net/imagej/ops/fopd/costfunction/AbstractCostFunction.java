
package net.imagej.ops.fopd.costfunction;

import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.Descent;
import net.imglib2.type.numeric.RealType;

/**
 * Abstract implementation of {@link CostFunction}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public abstract class AbstractCostFunction<T extends RealType<T>> implements
	CostFunction<T>
{

	protected Ascent<T> ascent;

	protected Descent<T> descent;

	public Ascent<T> getAscent() {
		return this.ascent;
	}

	public Descent<T> getDescent() {
		return this.descent;
	}
}
