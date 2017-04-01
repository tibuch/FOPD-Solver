package net.imagej.ops.fopd.helper;

import net.imagej.ops.special.inplace.UnaryInplaceOp;
import net.imglib2.type.numeric.RealType;

/**
 * A {@link MinMaxClipper} sets values > (<) max (min) to max (min).
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public interface MinMaxClipper<T extends RealType<T>> extends UnaryInplaceOp<T, T> {
	// NB: Marker Interface
}
