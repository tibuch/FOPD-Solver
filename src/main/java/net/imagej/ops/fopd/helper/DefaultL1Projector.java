
package net.imagej.ops.fopd.helper;

import net.imagej.ops.special.inplace.AbstractBinaryInplace1Op;
import net.imagej.ops.special.inplace.BinaryInplaceOp;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Implementation of {@link Projector}
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
@Plugin(type = BinaryInplaceOp.class)
public class DefaultL1Projector<T extends RealType<T>> extends
	AbstractBinaryInplace1Op<T, T> implements Projector<T>
{

	@Parameter
	private double lambda;

	public void mutate1(T p, T norm) {

		final double normValue = norm.getRealDouble();
		if (normValue > lambda) {
			p.setReal(lambda * p.getRealDouble() / normValue);
		}
	}
}
