package net.imagej.ops.fopd.helper;

import org.scijava.plugin.Plugin;

import net.imagej.ops.special.inplace.AbstractUnaryInplaceOp;
import net.imagej.ops.special.inplace.UnaryInplaceOp;
import net.imglib2.type.numeric.RealType;

/**
 * Implementation of {@link MinMaxClipper} which clips to 0 and 1.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 */
@Plugin(type = UnaryInplaceOp.class)
public class Default01Clipper<T extends RealType<T>> extends AbstractUnaryInplaceOp<T> implements MinMaxClipper<T> {

	public void mutate(T arg) {
		final double value = arg.getRealDouble();
		if (value < 0) {
			arg.setReal(0);
		} else if (value > 1) {
			arg.setReal(1);
		}
	}

}
