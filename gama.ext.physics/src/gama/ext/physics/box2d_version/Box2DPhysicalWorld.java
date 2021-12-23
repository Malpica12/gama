/*******************************************************************************************************
 *
 * Box2DPhysicalWorld.java, in gama.ext.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.physics.box2d_version;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import gama.ext.physics.common.AbstractPhysicalWorld;
import gama.ext.physics.common.IBody;
import gama.ext.physics.common.IShapeConverter;
import gama.ext.physics.gaml.PhysicalSimulationAgent;
import gama.metamodel.agent.IAgent;
import gama.metamodel.shape.GamaPoint;

/**
 * The Class Box2DPhysicalWorld.
 */
public class Box2DPhysicalWorld extends AbstractPhysicalWorld<World, Shape, Vec2> implements IBox2DPhysicalEntity {

	/**
	 * Instantiates a new box 2 D physical world.
	 *
	 * @param physicalSimulationAgent the physical simulation agent
	 */
	public Box2DPhysicalWorld(final PhysicalSimulationAgent physicalSimulationAgent) {
		super(physicalSimulationAgent);
	}

	@Override
	public void registerAgent(final IAgent agent) {
		Box2DBodyWrapper body = new Box2DBodyWrapper(agent, this);
	}

	@Override
	public void unregisterAgent(final IAgent agent) {
		Body body = (Body) agent.getAttribute(BODY);
		getWorld().destroyBody(body);

	}

	@Override
	public void setCCD(final boolean ccd) {}

	@Override
	public void setGravity(final GamaPoint gravity) {
		if (world != null) { world.setGravity(toVector(gravity)); }
	}

	@Override
	public void dispose() {
		if (world != null) {
			Body b = world.getBodyList();
			while (b != null) {
				world.destroyBody(b);
				b = b.getNext();
			}
			world = null;
		}
	}

	@Override
	public void updatePositionsAndRotations() {
		Body b = world.getBodyList();
		while (b != null) {
			IBody body = (IBody) b.getUserData();
			if (b.isActive()) { body.transferLocationAndRotationToAgent(); }
			b = b.getNext();
		}
	}

	@Override
	protected World createWorld() {
		GamaPoint p = simulation.getGravity(simulation.getScope());
		World result = new World(toVector(p));
		result.setAutoClearForces(true);
		result.setContactListener(contactListener);
		return result;
	}

	@Override
	protected IShapeConverter<Shape, Vec2> createShapeConverter() {
		return new Box2DShapeConverter();
	}

	@Override
	protected void updateAgentsShape() {
		// We update the agents
		for (IAgent a : updatableAgents) {
			unregisterAgent(a);
		}
		for (IAgent a : updatableAgents) {
			registerAgent(a);
		}
		updatableAgents.clear();
	}

	@Override
	protected void updateEngine(final Double timeStep, final int maxSubSteps) {
		getWorld().step(timeStep.floatValue(), maxSubSteps, maxSubSteps);
	}

}