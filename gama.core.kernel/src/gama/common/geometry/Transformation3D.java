/*******************************************************************************************************
 *
 * Transformation3D.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.common.geometry;

import org.locationtech.jts.geom.CoordinateFilter;

import gama.metamodel.shape.GamaPoint;

/**
 * The Interface Transformation3D.
 */
public interface Transformation3D extends CoordinateFilter {

	/**
	 * Apply to.
	 *
	 * @param vertex the vertex
	 */
	default void applyTo(final GamaPoint vertex) {
		filter(vertex);
	}
}