package net.imagej.ops.fopd.operator;

import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * A linear operator which can be used with a {@link CostFunction}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
public interface LinearOperator<T extends RealType<T>>
		extends UnaryFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> {
	// NB: Marker Interface
}
