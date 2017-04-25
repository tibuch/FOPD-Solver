
package net.imagej.ops.fopd.helper;

import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * L2-Norm Interface.
 * 
 * L2-Norm is defined by: l2(v) = sqrt(sum_i [v_i^2])
 * 
 * Ref: Weisstein, Eric W. "L^2-Norm." From MathWorld--A Wolfram Web Resource.
 * http://mathworld.wolfram.com/L2-Norm.html
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
public interface L2Norm<T extends RealType<T>>
		extends UnaryHybridCF<RandomAccessibleInterval<T>[], RandomAccessibleInterval<T>> {
	// NB: Marker Interface
}
