package net.imagej.ops.fopd;

import net.imagej.ops.Op;
import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imglib2.type.numeric.RealType;

/**
 * The {@link Descent} interface is implemented by {@link Regularizer}s and
 * {@link CostFunction}s.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public interface Descent<T extends RealType<T>> extends Op {
	// NB: Marker Interface
}
