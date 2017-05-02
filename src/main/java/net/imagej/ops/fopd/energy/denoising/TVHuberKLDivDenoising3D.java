
package net.imagej.ops.fopd.energy.denoising;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.kldivergence.KLDivergence;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.tvhuber.TVHuber3D;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * A 3D denoising algorithm which uses TV (with Huber-Norm instead of L2-Norm)
 * as {@link Regularizer} and takes the L1-Norm as {@link CostFunction}.
 * 
 * Energy: E(u) = lambda * TV-Huber(u)_alpha + |u - f|_1, where u is the
 * denoised solution, lambda is the smoothness weight and f is the observed
 * image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
@Plugin(type = UnaryHybridCF.class)
public class TVHuberKLDivDenoising3D<T extends RealType<T>> extends AbstractDenoising<T> {

	@Parameter
	private double lambda;

	@Parameter
	private double alpha;

	@Override
	SolverState<T> getSolverState(RandomAccessibleInterval<T>[] input) {
		return new DefaultSolverState<T>(ops, input, 1);
	}

	@Override
	Regularizer<T> getRegularizer(final double numViews) {
		return new TVHuber3D<T>(ops, lambda, alpha, (1.0 / (6.0 + numViews)));
	}

	@Override
	CostFunction<T> getCostFunction(RandomAccessibleInterval<T>[] input, LinearOperator<T>[] ascentOperator,
			LinearOperator<T>[] descentOperator) {
		return new KLDivergence<T>(ops, input, ascentOperator, descentOperator, (1.0 / (6.0 + input.length)));
	}
}
