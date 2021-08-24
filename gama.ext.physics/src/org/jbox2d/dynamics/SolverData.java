/*******************************************************************************************************
 *
 * SolverData.java, in gama.ext.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics;

import org.jbox2d.dynamics.contacts.Position;
import org.jbox2d.dynamics.contacts.Velocity;

/**
 * The Class SolverData.
 */
public class SolverData {
  
  /** The step. */
  public TimeStep step;
  
  /** The positions. */
  public Position[] positions;
  
  /** The velocities. */
  public Velocity[] velocities;
}
