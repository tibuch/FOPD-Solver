package net.imagej.ops.fopd.regularizer;

import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.Descent;
import net.imglib2.type.numeric.RealType;

/**
 * A {@link Regularizer} enforces a smoothness constraint on the solution.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public interface Regularizer<T extends RealType<T>> {
	public Ascent<T> getAscent();
	
	public Descent<T> getDescent();
}
