/*******************************************************************************************************
 *
 * DistanceOutput.java, in gama.ext.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.collision;

import org.jbox2d.common.Vec2;

/**
 * Output for Distance.
 * @author Daniel
 */
public class DistanceOutput {
	
	/**  Closest point on shapeA. */
	public final Vec2 pointA = new Vec2();
	
	/**  Closest point on shapeB. */
	public final Vec2 pointB = new Vec2();
	
	/** The distance. */
	public float distance;
	
	/**  number of gjk iterations used. */
	public int iterations;
}
