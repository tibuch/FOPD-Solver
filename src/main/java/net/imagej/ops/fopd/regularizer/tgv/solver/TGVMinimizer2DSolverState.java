package net.imagej.ops.fopd.regularizer.tgv.solver;

import net.imagej.ops.OpService;
import net.imagej.ops.fopd.DualVariables;
import net.imagej.ops.fopd.solver.AbstractSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * {@link SolverState} for {@link TGVMinimizer2D}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public class TGVMinimizer2DSolverState<T extends RealType<T>> extends AbstractSolverState<T> {


	@SuppressWarnings("unchecked")
	public TGVMinimizer2DSolverState(final OpService ops, final RandomAccessibleInterval<T> image) {
		super(ops, image);
		regularizerDV = new DualVariables<T>((RandomAccessibleInterval<T>) ops.create().img(image),
				(RandomAccessibleInterval<T>) ops.create().img(image),
				(RandomAccessibleInterval<T>) ops.create().img(image));

		intermediateResults = new RandomAccessibleInterval[2];
		intermediateResults[0] = (RandomAccessibleInterval<T>) ops.create().img(image);
		intermediateResults[1] = (RandomAccessibleInterval<T>) ops.create().img(image);

		this.results = new RandomAccessibleInterval[2];
		results[0] = (RandomAccessibleInterval<T>) ops.create().img(image);
		results[1] = (RandomAccessibleInterval<T>) ops.create().img(image);
	}

	@Override
	public RandomAccessibleInterval<T> getIntermediateResult(int i) {
		if (i > 1) {
			throw new ArrayIndexOutOfBoundsException("This solver only has two intermediate results.");
		}
		return intermediateResults[i];
	}

	@Override
	public RandomAccessibleInterval<T> getResultImage(int i) {
		if (i > 1) {
			throw new ArrayIndexOutOfBoundsException("This solver only has two results.");
		}
		return results[i];
	}
}
