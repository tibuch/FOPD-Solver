
package net.imagej.ops.fopd.energy.denoising;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import net.imagej.ops.fopd.costfunction.CostFunction;
import net.imagej.ops.fopd.costfunction.l1norm.L1Norm;
import net.imagej.ops.fopd.operator.LinearOperator;
import net.imagej.ops.fopd.regularizer.Regularizer;
import net.imagej.ops.fopd.regularizer.tv.TotalVariation3D;
import net.imagej.ops.fopd.solver.DefaultSolverState;
import net.imagej.ops.fopd.solver.SolverState;
import net.imagej.ops.special.hybrid.UnaryHybridCF;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

/**
 * A 3D denoising algorithm which uses TV as {@link Regularizer} and takes the
 * L1-Norm as {@link CostFunction}.
 * 
 * Energy: E(u) = lambda * TV(u) + |u - f|_1, where u is the denoised solution,
 * lambda is the smoothness weight and f is the observed image.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
@Plugin(type = UnaryHybridCF.class)
public class TVL1Denoising3D<T extends RealType<T>> extends AbstractDenoising<T> {

	@Parameter
	private double lambda;

	@Override
	SolverState<T> getSolverState(RandomAccessibleInterval<T>[] input) {
		return new DefaultSolverState<T>(ops, input, 1);
	}

	@Override
	Regularizer<T> getRegularizer(final double numViews) {
		return new TotalVariation3D<T>(ops, lambda, (1.0 / (6.0 + numViews)));
	}

	@Override
	CostFunction<T> getCostFunction(RandomAccessibleInterval<T>[] input, LinearOperator<T>[] ascentOperator,
			LinearOperator<T>[] descentOperator) {
		return new L1Norm<T>(ops, input, ascentOperator, descentOperator, (1.0 / (6.0 + input.length)));
	}
}