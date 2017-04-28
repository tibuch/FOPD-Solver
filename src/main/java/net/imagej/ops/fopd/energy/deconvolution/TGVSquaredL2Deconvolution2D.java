
package net.imagej.ops.fopd.energy.deconvolution;

import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.squaredl2norm.SquaredL2Norm;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.tgv.TGV2D;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.fopd.solver.TGVSolverState;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * A 2D deconvolution algorithm which uses TGV as {@link Regularizer} and takes
 * the L1-Norm as {@link CostFunction}.
 * 
 * Energy: E(u) = lambda * TGV(u) + |u - f|_1, where u is the deconvolved
 * solution, lambda is the smoothness weight, k is the known kernel, * is the
 * convolution operator and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
@Plugin(type = UnaryHybridCF.class)
public class TGVSquaredL2Deconvolution2D<T extends RealType<T>> extends AbstractDeconvoltuion<T> {

	@Parameter
	private double alpha;

	@Parameter
	private double beta;

	@Override
	SolverState<T> getSolverState(RandomAccessibleInterval<T>[] input) {
		return new TGVSolverState<T>(ops, input, 1);
	}

	@Override
	Regularizer<T> getRegularizer(final double numViews) {
		return new TGV2D<T>(ops, alpha, beta, (1.0 / (4.0 + numViews)));
	}

	@Override
	CostFunction<T> getCostFunction(RandomAccessibleInterval<T>[] input, LinearOperator<T>[] ascentConvolver,
			LinearOperator<T>[] descentConvolver) {
		return new SquaredL2Norm<T>(ops, input, ascentConvolver, descentConvolver, (1.0 / (4.0 + input.length)));
	}
}
