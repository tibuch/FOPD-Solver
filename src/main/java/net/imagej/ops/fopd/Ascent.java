package net.imagej.ops.fopd;

import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.special.function.UnaryFunctionOp;
import net.imglib2.type.numeric.RealType;

/**
 * The {@link Ascent} interface is implemented by {@link Regularizer}s and
 * {@link CostFunction}s.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public interface Ascent<T extends RealType<T>>
		extends UnaryFunctionOp<SolverState<T>, SolverState<T>> {
	// NB: Marker Interface
}
