/*******************************************************************************************************
 *
 * Jacobian.java, in gama.ext.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.joints;

import org.jbox2d.common.Vec2;

/**
 * The Class Jacobian.
 */
public class Jacobian {
	
	/** The linear A. */
	public final Vec2 linearA = new Vec2();
	
	/** The angular A. */
	public float angularA;
	
	/** The angular B. */
	public float angularB;
}
