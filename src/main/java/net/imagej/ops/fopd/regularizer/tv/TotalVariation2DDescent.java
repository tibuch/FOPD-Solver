package net.imagej.ops.fopd.regularizer.tv;

import net.imagej.ops.fopd.Descent;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

/**
 * Total Variation of one 2D image: {@link Descent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = Descent.class)
public class TotalVariation2DDescent<T extends RealType<T>> extends AbstractTV2DDescent<T> {
	// nothing to do
}
