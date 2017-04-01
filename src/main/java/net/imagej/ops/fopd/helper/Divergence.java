package net.imagej.ops.fopd.helper;

import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Divergence Interface.
 * 
 * Divergence is defined by: div(pX, pY) = (d pX)/dx + (d pY)/dy where pX is
 * computed using {@link ForwardDifference} and (d pX)/dx is computed using
 * {@link BackwardDifference}.
 * 
 * Ref: Weisstein, Eric W. "Divergence." From MathWorld--A Wolfram Web Resource.
 * http://mathworld.wolfram.com/Divergence.html
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public interface Divergence<T extends RealType<T>>
		extends UnaryHybridCF<DualVariables<T>, RandomAccessibleInterval<T>> {
	// NB: Marker Interface
}
