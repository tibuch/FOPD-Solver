
package net.imagej.ops.fopd.regularizer.tgv;

import org.scijava.plugin.Plugin;

import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.regularizer.tv.AbstractTV3DDescent;
import net.imglib2.type.numeric.RealType;

/**
 * Abstract Total Variation of one 3D image: {@link Descent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Descent.class)
public class TGV3DDescent<T extends RealType<T>> extends AbstractTV3DDescent<T> {
	// Nothing to do
}
