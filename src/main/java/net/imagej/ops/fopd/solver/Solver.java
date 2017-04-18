package net.imagej.ops.fopd.solver;

import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public interface Solver<T extends RealType<T>> extends UnaryFunctionOp<SolverState<T>, RandomAccessibleInterval<T>> {
	// NB: Marker Interface
}
