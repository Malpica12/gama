/*******************************************************************************************************
 *
 * BodyDef.java, in gama.ext.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics;

import org.jbox2d.common.Vec2;

/**
 * A body definition holds all the data needed to construct a rigid body. You can safely re-use body
 * definitions. Shapes are added to a body after construction.
 * 
 * @author daniel
 */
public class BodyDef {

  /**
   * The body type: static, kinematic, or dynamic. Note: if a dynamic body would have zero mass, the
   * mass is set to one.
   */
  public BodyType type;

  /**
   * Use this to store application specific body data.
   */
  public Object userData;

  /**
   * The world position of the body. Avoid creating bodies at the origin since this can lead to many
   * overlapping shapes.
   */
  public Vec2 position;

  /**
   * The world angle of the body in radians.
   */
  public float angle;

  /**
   * The linear velocity of the body in world co-ordinates.
   */
  public Vec2 linearVelocity;

  /**
   * The angular velocity of the body.
   */
  public float angularVelocity;

  /**
   * Linear damping is use to reduce the linear velocity. The damping parameter can be larger than
   * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
   * large.
   */
  public float linearDamping;

  /**
   * Angular damping is use to reduce the angular velocity. The damping parameter can be larger than
   * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
   * large.
   */
  public float angularDamping;

  /**
   * Set this flag to false if this body should never fall asleep. Note that this increases CPU
   * usage.
   */
  public boolean allowSleep;

  /** Is this body initially sleeping?. */
  public boolean awake;

  /**
   * Should this body be prevented from rotating? Useful for characters.
   */
  public boolean fixedRotation;

  /**
   * Is this a fast moving body that should be prevented from tunneling through other moving bodies?
   * Note that all bodies are prevented from tunneling through kinematic and static bodies. This
   * setting is only considered on dynamic bodies.
   * 
   * @warning You should use this flag sparingly since it increases processing time.
   */
  public boolean bullet;

  /** Does this body start out active?. */
  public boolean active;

  /**
   * Experimental: scales the inertia tensor.
   */
  public float gravityScale;

  /**
   * Instantiates a new body def.
   */
  public BodyDef() {
    userData = null;
    position = new Vec2();
    angle = 0f;
    linearVelocity = new Vec2();
    angularVelocity = 0f;
    linearDamping = 0f;
    angularDamping = 0f;
    allowSleep = true;
    awake = true;
    fixedRotation = false;
    bullet = false;
    type = BodyType.STATIC;
    active = true;
    gravityScale = 1.0f;
  }

  /**
   * The body type: static, kinematic, or dynamic. Note: if a dynamic body would have zero mass, the
   * mass is set to one.
   *
   * @return the type
   */
  public BodyType getType() {
    return type;
  }

  /**
   * The body type: static, kinematic, or dynamic. Note: if a dynamic body would have zero mass, the
   * mass is set to one.
   *
   * @param type the new type
   */
  public void setType(BodyType type) {
    this.type = type;
  }

  /**
   * Use this to store application specific body data.
   *
   * @return the user data
   */
  public Object getUserData() {
    return userData;
  }

  /**
   * Use this to store application specific body data.
   *
   * @param userData the new user data
   */
  public void setUserData(Object userData) {
    this.userData = userData;
  }

  /**
   * The world position of the body. Avoid creating bodies at the origin since this can lead to many
   * overlapping shapes.
   *
   * @return the position
   */
  public Vec2 getPosition() {
    return position;
  }

  /**
   * The world position of the body. Avoid creating bodies at the origin since this can lead to many
   * overlapping shapes.
   *
   * @param position the new position
   */
  public void setPosition(Vec2 position) {
    this.position = position;
  }

  /**
   * The world angle of the body in radians.
   *
   * @return the angle
   */
  public float getAngle() {
    return angle;
  }

  /**
   * The world angle of the body in radians.
   *
   * @param angle the new angle
   */
  public void setAngle(float angle) {
    this.angle = angle;
  }

  /**
   * The linear velocity of the body in world co-ordinates.
   *
   * @return the linear velocity
   */
  public Vec2 getLinearVelocity() {
    return linearVelocity;
  }

  /**
   * The linear velocity of the body in world co-ordinates.
   *
   * @param linearVelocity the new linear velocity
   */
  public void setLinearVelocity(Vec2 linearVelocity) {
    this.linearVelocity = linearVelocity;
  }

  /**
   * The angular velocity of the body.
   *
   * @return the angular velocity
   */
  public float getAngularVelocity() {
    return angularVelocity;
  }

  /**
   * The angular velocity of the body.
   *
   * @param angularVelocity the new angular velocity
   */
  public void setAngularVelocity(float angularVelocity) {
    this.angularVelocity = angularVelocity;
  }

  /**
   * Linear damping is use to reduce the linear velocity. The damping parameter can be larger than
   * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
   * large.
   *
   * @return the linear damping
   */
  public float getLinearDamping() {
    return linearDamping;
  }

  /**
   * Linear damping is use to reduce the linear velocity. The damping parameter can be larger than
   * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
   * large.
   *
   * @param linearDamping the new linear damping
   */
  public void setLinearDamping(float linearDamping) {
    this.linearDamping = linearDamping;
  }

  /**
   * Angular damping is use to reduce the angular velocity. The damping parameter can be larger than
   * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
   * large.
   *
   * @return the angular damping
   */
  public float getAngularDamping() {
    return angularDamping;
  }

  /**
   * Angular damping is use to reduce the angular velocity. The damping parameter can be larger than
   * 1.0f but the damping effect becomes sensitive to the time step when the damping parameter is
   * large.
   *
   * @param angularDamping the new angular damping
   */
  public void setAngularDamping(float angularDamping) {
    this.angularDamping = angularDamping;
  }

  /**
   * Set this flag to false if this body should never fall asleep. Note that this increases CPU
   * usage.
   *
   * @return true, if is allow sleep
   */
  public boolean isAllowSleep() {
    return allowSleep;
  }

  /**
   * Set this flag to false if this body should never fall asleep. Note that this increases CPU
   * usage.
   *
   * @param allowSleep the new allow sleep
   */
  public void setAllowSleep(boolean allowSleep) {
    this.allowSleep = allowSleep;
  }

  /**
   * Is this body initially sleeping?.
   *
   * @return true, if is awake
   */
  public boolean isAwake() {
    return awake;
  }

  /**
   * Is this body initially sleeping?.
   *
   * @param awake the new awake
   */
  public void setAwake(boolean awake) {
    this.awake = awake;
  }

  /**
   * Should this body be prevented from rotating? Useful for characters.
   *
   * @return true, if is fixed rotation
   */
  public boolean isFixedRotation() {
    return fixedRotation;
  }

  /**
   * Should this body be prevented from rotating? Useful for characters.
   *
   * @param fixedRotation the new fixed rotation
   */
  public void setFixedRotation(boolean fixedRotation) {
    this.fixedRotation = fixedRotation;
  }

  /**
   * Is this a fast moving body that should be prevented from tunneling through other moving bodies?
   * Note that all bodies are prevented from tunneling through kinematic and static bodies. This
   * setting is only considered on dynamic bodies.
   *
   * @return true, if is bullet
   * @warning You should use this flag sparingly since it increases processing time.
   */
  public boolean isBullet() {
    return bullet;
  }

  /**
   * Is this a fast moving body that should be prevented from tunneling through other moving bodies?
   * Note that all bodies are prevented from tunneling through kinematic and static bodies. This
   * setting is only considered on dynamic bodies.
   *
   * @param bullet the new bullet
   * @warning You should use this flag sparingly since it increases processing time.
   */
  public void setBullet(boolean bullet) {
    this.bullet = bullet;
  }

  /**
   * Does this body start out active?.
   *
   * @return true, if is active
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Does this body start out active?.
   *
   * @param active the new active
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Experimental: scales the inertia tensor.
   *
   * @return the gravity scale
   */
  public float getGravityScale() {
    return gravityScale;
  }

  /**
   * Experimental: scales the inertia tensor.
   *
   * @param gravityScale the new gravity scale
   */
  public void setGravityScale(float gravityScale) {
    this.gravityScale = gravityScale;
  }
}