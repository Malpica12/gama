/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.ITopology.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.metamodel.topology;

import java.util.Collection;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import gama.common.geometry.Envelope3D;
import gama.common.interfaces.IKeyword;
import gama.common.interfaces.IValue;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.getter;
import gama.core.dev.annotations.GamlAnnotations.variable;
import gama.core.dev.annotations.GamlAnnotations.vars;
import gama.metamodel.agent.IAgent;
import gama.metamodel.population.IPopulation;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.IShape;
import gama.metamodel.topology.continuous.RootTopology;
import gama.metamodel.topology.filter.IAgentFilter;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IContainer;
import gama.util.IList;
import gama.util.path.GamaSpatialPath;
import gaml.types.IType;

/**
 * Written by drogoul Modified on 4 juil. 2011
 *
 * @todo Description
 *
 */
@vars ({ @variable (
		name = IKeyword.ENVIRONMENT,
		type = IType.GEOMETRY,
		doc = { @doc ("Returns the environment of this topology, either an agent or a geometry, which defines its boundaries") }),
		@variable (
				name = IKeyword.PLACES,
				type = IType.CONTAINER,
				of = IType.GEOMETRY,
				doc = { @doc ("Returns the list of discrete places that compose this topology (e.g. the list of cells for a grid topology). The continuous topologies will return a singleton list with their environment") }),
		// Could be replaced by "geometries"
		/*
		 * Normally not necessary as it is inherited from GamaGeometry @var(name = GamaPath.POINTS, type = IType.LIST,
		 * of = IType.POINT)
		 */
})
public interface ITopology extends IValue {
	
	enum SpatialRelation {
		OVERLAP,
		COVER,
		INSIDE,
		TOUCH,
		CROSS,
		PARTIALLY_OVERLAP
	}

	ISpatialIndex getSpatialIndex();

	void initialize(IScope scope, IPopulation<? extends IAgent> pop) throws GamaRuntimeException;

	void updateAgent(Envelope3D previous, IAgent agent);

	void removeAgent(final IAgent agent);

	List<Geometry> listToroidalGeometries(final Geometry geom);

	Collection<IAgent> getAgentClosestTo(IScope scope, final IShape source, IAgentFilter filter, int number);

	IAgent getAgentClosestTo(IScope scope, final IShape source, IAgentFilter filter);

	IAgent getAgentFarthestTo(IScope scope, final IShape source, IAgentFilter filter);

	Collection<IAgent> getNeighborsOf(IScope scope, final IShape source, final Double distance, IAgentFilter filter)
			throws GamaRuntimeException;

	Collection<IAgent> getAgentsIn(IScope scope, final IShape source, final IAgentFilter f, SpatialRelation relation);

	

	boolean isTorus();

	boolean isContinuous();

	/**
	 * @throws GamaRuntimeException
	 *             Distance between two geometries in this topology.
	 *
	 * @param source
	 *            the source geometry (cannot be null)
	 * @param target
	 *            the target (cannot be null)
	 * @return a double representing the distance between the two geometries, or Double.MAX_VALUE if either one of them
	 *         is not reachable from this topology
	 */
	Double distanceBetween(IScope scope, final IShape source, final IShape target);

	Double distanceBetween(IScope scope, final GamaPoint source, final GamaPoint target);

	GamaSpatialPath pathBetween(IScope scope, final IShape source, final IShape target) throws GamaRuntimeException;

	GamaSpatialPath pathBetween(IScope scope, final GamaPoint source, final GamaPoint target)
			throws GamaRuntimeException;

	/**
	 * Return the location corresponding to a displacement from source, with an angle (in degrees) given by direction
	 * and a given distance. The returned point is a valid local location of this topology or null.
	 *
	 * @param source
	 *            the point from which the destination is computed
	 * @param direction
	 *            an angle in degrees (between 0 and 360 -- other values will be normalized
	 * @param distance
	 *            the distance that should separate the actual location and the destination
	 * @param nullIfOutside
	 *            tells wether to return the destination point or null if the destination is outside the topology
	 * @return a point or null if no random locations are available
	 */
	GamaPoint getDestination(final GamaPoint source, final double direction, final double distance,
			boolean nullIfOutside);

	/**
	 * Return the location corresponding to a displacement from source, with an angle (in degrees) given by direction
	 * and a given distance. The returned point is a valid local location of this topology or null.
	 *
	 * @param source
	 *            the point from which the destination is computed
	 * @param direction
	 *            an angle in degrees (between 0 and 360 -- other values will be normalized
	 * @param distance
	 *            the distance that should separate the actual location and the destination
	 * @param nullIfOutside
	 *            tells wether to return the destination point or null if the destination is outside the topology
	 * @return a point or null if no random locations are available
	 */
	GamaPoint getDestination3D(final GamaPoint source, final double heading, final double pitch, final double distance,
			boolean nullIfOutside);

	/**
	 * Return a random location inside the bounds of the environment's shape. The returned point is a valid local
	 * location of this topology.
	 *
	 * @return a point, or null if no random locations are available
	 */
	GamaPoint getRandomLocation(IScope scope);

	/**
	 * Return the collection of places (IGeometry) defined by this topology. For continuous topologies, it is a GamaList
	 * with their environment. For discrete topologies, it can be any of the container supporting the inclusion of
	 * geometries (GamaList, GamaSpatialGraph, GamaMap, GamaSpatialMatrix)
	 *
	 * @return an instance of IGamaContainer, which geometries can be iterated.
	 */
	@getter (IKeyword.PLACES)
	IContainer<?, IShape> getPlaces();

	/**
	 * Return the environment of this topology (i.e. the IGeometry that defines its boundaries).
	 *
	 * @return an instance of IGeometry.
	 */
	@getter (IKeyword.ENVIRONMENT)
	IShape getEnvironment();

	/**
	 * Normalizes a location so that the returned location is inside the bounds of the topology. The returned point is a
	 * valid local location of this topology.
	 *
	 * @param p
	 *            the location to normalize
	 * @param nullIfOutside
	 *            tells whether to return null or to coerce p if p is outside the bounds of the topology
	 * @return a valid point or null if nullIfOutside is true and the point is outside
	 */
	GamaPoint normalizeLocation(final GamaPoint p, boolean nullIfOutside);

	/**
	 * @throws GamaRuntimeException
	 *             Called by a population to tell this topology that the shape of its host has changed. If the
	 *             environment of the topology depends on the shape of the host, the topology can choose to adapt in
	 *             consequence.
	 *
	 * @param pop
	 *            the population to which this topology is attached.
	 */
	// public abstract void shapeChanged(IPopulation pop);

	double getWidth();

	double getHeight();

	void dispose();

	boolean isValidLocation(IScope scope, GamaPoint p);

	boolean isValidGeometry(IScope scope, IShape g);

	/**
	 * @param scope
	 *            TODO
	 * @throws GamaRuntimeException
	 * @param source
	 * @param target
	 * @return the direction or null if one these two geometries are invalid in this topology
	 */
	Double directionInDegreesTo(IScope scope, IShape source, IShape target);

	IList<GamaSpatialPath> KpathsBetween(IScope scope, IShape source, IShape target, int k);

	IList<GamaSpatialPath> KpathsBetween(IScope scope, GamaPoint source, GamaPoint target, int k);

	void setRoot(IScope scope, RootTopology rt);

}