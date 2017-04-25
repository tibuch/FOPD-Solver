
package net.imagej.ops.fopd.helper;

import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;

/**
 * Backward Difference Interface.
 * 
 * Backward difference is defined by: bd(i(x)) = i(x) - i(x-1)
 * 
 * Ref: Weisstein, Eric W. "Backward Difference." From MathWorld--A Wolfram Web
 * Resource. http://mathworld.wolfram.com/BackwardDifference.html
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public interface BackwardDifference<T extends Type<T>>
		extends UnaryHybridCF<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {
	// NB: Marker Interface
}
