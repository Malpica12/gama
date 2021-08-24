/*******************************************************************************************************
 *
 * Shape.java, in gama.ext.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.collision.shapes;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

/**
 * A shape is used for collision detection. You can create a shape however you like. Shapes used for
 * simulation in World are created automatically when a Fixture is created. Shapes may encapsulate a
 * one or more child shapes.
 */
public abstract class Shape {

  /** The m type. */
  public final ShapeType m_type;
  
  /** The m radius. */
  public float m_radius;

  /**
   * Instantiates a new shape.
   *
   * @param type the type
   */
  public Shape(ShapeType type) {
    this.m_type = type;
  }

  /**
   * Get the type of this shape. You can use this to down cast to the concrete shape.
   * 
   * @return the shape type.
   */
  public ShapeType getType() {
    return m_type;
  }

  /**
   * The radius of the underlying shape. This can refer to different things depending on the shape
   * implementation
   *
   * @return the radius
   */
  public float getRadius() {
    return m_radius;
  }

  /**
   * Sets the radius of the underlying shape. This can refer to different things depending on the
   * implementation
   *
   * @param radius the new radius
   */
  public void setRadius(float radius) {
    this.m_radius = radius;
  }

  /**
   * Get the number of child primitives.
   *
   * @return the child count
   */
  public abstract int getChildCount();

  /**
   * Test a point for containment in this shape. This only works for convex shapes.
   *
   * @param xf the shape world transform.
   * @param p a point in world coordinates.
   * @return true, if successful
   */
  public abstract boolean testPoint(final Transform xf, final Vec2 p);

  /**
   * Cast a ray against a child shape.
   *
   * @param output the output
   * @param input the input
   * @param transform the transform
   * @param childIndex the child index
   * @return if hit
   */
  public abstract boolean raycast(RayCastOutput output, RayCastInput input, Transform transform,
      int childIndex);


  /**
   * Given a transform, compute the associated axis aligned bounding box for a child shape.
   *
   * @param aabb the aabb
   * @param xf the xf
   * @param childIndex the child index
   */
  public abstract void computeAABB(final AABB aabb, final Transform xf, int childIndex);

  /**
   * Compute the mass properties of this shape using its dimensions and density. The inertia tensor
   * is computed about the local origin.
   * 
   * @param massData returns the mass data for this shape.
   * @param density the density in kilograms per meter squared.
   */
  public abstract void computeMass(final MassData massData, final float density);

  /**
   * Compute the distance from the current shape to the specified point. This only works for convex
   * shapes.
   *
   * @param xf the shape world transform.
   * @param p a point in world coordinates.
   * @param childIndex the child index
   * @param normalOut returns the direction in which the distance increases.
   * @return distance returns the distance from the current shape.
   */
  public abstract float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut);

  public abstract Shape clone();
}
