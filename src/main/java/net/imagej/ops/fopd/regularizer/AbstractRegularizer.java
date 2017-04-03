package net.imagej.ops.fopd.regularizer;

import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.Descent;
import net.imglib2.type.numeric.RealType;

/**
 * Abstract implementation of {@link Regularizer}.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
public class AbstractRegularizer<T extends RealType<T>> implements Regularizer<T> {

	protected Ascent<T> ascent;

	protected Descent<T> descent;

	public Ascent<T> getAscent() {
		return this.ascent;
	}

	public Descent<T> getDescent() {
		return this.descent;
	}
}
