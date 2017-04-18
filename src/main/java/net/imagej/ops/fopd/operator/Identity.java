package net.imagej.ops.fopd.operator;

import net.imagej.ops.special.function.AbstractUnaryFunctionOp;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

/**
 * Identity operator. Returns the input.F
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = LinearOperator.class, description = "Denoising Operator.")
public class Identity<T extends RealType<T>> extends
		AbstractUnaryFunctionOp<RandomAccessibleInterval<T>, RandomAccessibleInterval<T>> implements LinearOperator<T> {

	public RandomAccessibleInterval<T> calculate(RandomAccessibleInterval<T> input) {
		return input;
	}
}
