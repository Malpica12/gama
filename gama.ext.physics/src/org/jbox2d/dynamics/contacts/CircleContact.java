/*******************************************************************************************************
 *
 * CircleContact.java, in gama.ext.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics.contacts;

import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.pooling.IWorldPool;

/**
 * The Class CircleContact.
 */
public class CircleContact extends Contact {

  /**
   * Instantiates a new circle contact.
   *
   * @param argPool the arg pool
   */
  public CircleContact(IWorldPool argPool) {
    super(argPool);
  }

  /**
   * Inits the.
   *
   * @param fixtureA the fixture A
   * @param fixtureB the fixture B
   */
  public void init(Fixture fixtureA, Fixture fixtureB) {
    super.init(fixtureA, 0, fixtureB, 0);
    assert (m_fixtureA.getType() == ShapeType.CIRCLE);
    assert (m_fixtureB.getType() == ShapeType.CIRCLE);
  }

  @Override
  public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
    pool.getCollision().collideCircles(manifold, (CircleShape) m_fixtureA.getShape(), xfA,
        (CircleShape) m_fixtureB.getShape(), xfB);
  }
}
