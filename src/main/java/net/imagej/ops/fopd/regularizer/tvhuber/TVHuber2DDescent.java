package net.imagej.ops.fopd.regularizer.tvhuber;

import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.regularizer.tv.AbstractTV2DDescent;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

/**
 * TV-Huber of one 2D image: {@link Descent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 *
 * @param <T>
 */
@Plugin(type = Descent.class)
public class TVHuber2DDescent<T extends RealType<T>> extends AbstractTV2DDescent<T> {
	// nothing to do
}
