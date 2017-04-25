
package net.imagej.ops.fopd.helper;

import net.imagej.ops.special.inplace.BinaryInplace1Op;
import net.imglib2.type.numeric.RealType;

/**
 * A {@link Projector} projects values back to a certain range. Mathematically
 * it is formulated by: p_lambda(x) = lambda*x/max(lambda, ||x||)
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public interface Projector<T extends RealType<T>> extends
	BinaryInplace1Op<T, T, T>
{
	// NB: Marker Interface
}
