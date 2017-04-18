package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Specific implementation of {@link SolverState} for L1-TVHuber-Denoising.
 * 
 * Energy: E(u) = lambda * TV-Huber(u)_alpha + |u - f|_1, where u is the
 * denoised solution, lambda is the smoothness weight and f is the observed
 * image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz.
 *
 * @param <T>
 */
public class DefaultSolverState<T extends RealType<T>> extends AbstractSolverState<T> {

	public DefaultSolverState(final OpService ops, final RandomAccessibleInterval<T> image) {
		super(ops, image);
	}
}
