
package net.imagej.ops.fopd.costfunction.squaredl2norm;

import net.imagej.ops.fopd.Descent;
import net.imagej.ops.fopd.costfunction.AbstractCostFunction2DDescent;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Plugin;

/**
 * L2-Norm as costfunction of one 2D image: {@link Descent} Step.
 * 
 * @author Tim-Oliver Buchholz, University of Konstanz
 * @param <T>
 */
@Plugin(type = Descent.class)
public class SquaredL2Norm2DDescent<T extends RealType<T>> extends
	AbstractCostFunction2DDescent<T>
{
	// nothing to do
}
