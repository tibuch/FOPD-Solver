package net.imagej.ops.fopd.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.regularizer.tgv.solver.TGVMinimizer2DSolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * Specific implementation of {@link SolverState} for L1-TV-Denoising.
 * 
 * Energy: E(u) = lambda * TV(u) + |u - f|_1, where u is the denoised solution,
 * lambda is the smoothness weight and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz.
 *
 * @param <T>
 */
public class TGVSolverState<T extends RealType<T>> extends AbstractSolverState<T> {

	private TGVMinimizer2DSolverState<T> tgvState;

	public TGVSolverState(final OpService ops, final RandomAccessibleInterval<T> image) {
		super(ops, image);

		this.tgvState = new TGVMinimizer2DSolverState<T>(ops, image);
	}

	@Override
	public SolverState<T> getSubSolverState(int i) {
		if (i > 0) {
			throw new ArrayIndexOutOfBoundsException("This solver only depends on one other sovler.");
		}
		return tgvState;
	}
}
