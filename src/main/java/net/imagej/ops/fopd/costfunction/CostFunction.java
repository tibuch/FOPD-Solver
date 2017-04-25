
package net.imagej.ops.fopd.costfunction;

import net.imagej.ops.fopd.Ascent;
import net.imagej.ops.fopd.Descent;
import net.imglib2.type.numeric.RealType;

/**
 * A {@link CostFunction} consists of an {@link Ascent}-step and a
 * {@link Descent}-step. In some sense the cost function describeds the image
 * formation process.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 */
public interface CostFunction<T extends RealType<T>> {

	public Ascent<T> getAscent();

	public Descent<T> getDescent();
}
