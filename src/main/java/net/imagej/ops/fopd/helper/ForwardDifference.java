package net.imagej.ops.fopd.helper;

import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;

/**
 * Forward Difference Interface.
 * 
 * Forward difference is defined by: fd(i(x)) = i(x+1) - i(x)
 * 
 * Ref: Weisstein, Eric W. "Forward Difference." From MathWorld--A Wolfram Web
 * Resource. http://mathworld.wolfram.com/ForwardDifference.html
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public interface ForwardDifference<T extends Type<T>>
		extends UnaryHybridCF<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {
	// NB: Marker Interface
}
