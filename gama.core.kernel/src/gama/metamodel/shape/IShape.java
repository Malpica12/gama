/*******************************************************************************************************
 *
 * msi.gama.metamodel.shape.IShape.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.metamodel.shape;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

import gama.common.geometry.Envelope3D;
import gama.common.interfaces.IAttributed;
import gama.common.interfaces.ILocated;
import gama.common.interfaces.IValue;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.getter;
import gama.core.dev.annotations.GamlAnnotations.variable;
import gama.core.dev.annotations.GamlAnnotations.vars;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.util.GamaMapFactory;
import gama.util.IList;
import gama.util.IMap;
import gaml.types.IType;

/**
 * Interface for objects that can be provided with a geometry (or which can be translated to a GamaGeometry)
 *
 * @author Alexis Drogoul
 * @since 16 avr. 2011
 * @modified November 2011 to include isPoint(), getInnerGeometry() and getEnvelope()
 *
 */
@vars ({ @variable (
		name = "area",
		type = IType.FLOAT,
		doc = { @doc ("Returns the total area of this geometry") }),
		@variable (
				name = "volume",
				type = IType.FLOAT,
				doc = { @doc ("Returns the total volume of this geometry") }),
		@variable (
				name = "centroid",
				type = IType.POINT,
				doc = { @doc ("Returns the centroid of this geometry") }),
		@variable (
				name = "width",
				type = IType.FLOAT,
				doc = { @doc ("Returns the width (length on the x-axis) of the rectangular envelope of this  geometry") }),
		@variable (
				name = "attributes",
				type = IType.MAP,
				index = IType.STRING,
				doc = { @doc ("Returns the attributes kept by this geometry (the ones shared with the agent)") }),
		@variable (
				name = "depth",
				type = IType.FLOAT,
				doc = { @doc ("Returns the depth (length on the z-axis) of the rectangular envelope of this geometry") }),
		@variable (
				name = "height",
				type = IType.FLOAT,
				doc = { @doc ("Returns the height (length on the y-axis) of the rectangular envelope of this geometry") }),
		@variable (
				name = "points",
				type = IType.LIST,
				of = IType.POINT,
				doc = { @doc ("Returns the list of points that delimit this geometry. A point will return a list with itself") }),
		@variable (
				name = "envelope",
				type = IType.GEOMETRY,
				doc = { @doc ("Returns the envelope of this geometry (the smallest rectangle that contains the geometry)") }),
		@variable (
				name = "geometries",
				type = IType.LIST,
				of = IType.GEOMETRY,
				doc = { @doc ("Returns the list of geometries that compose this geometry, or a list containing the geometry itself if it is simple") }),
		@variable (
				name = "multiple",
				type = IType.BOOL,
				doc = { @doc ("Returns whether this geometry is composed of multiple geometries or not") }),
		@variable (
				name = "perimeter",
				type = IType.FLOAT,
				doc = { @doc ("Returns the length of the contour of this geometry") }),
		@variable (
				name = "holes",
				type = IType.LIST,
				of = IType.GEOMETRY,
				doc = { @doc ("Returns the list of holes inside this geometry as a list of geometries, and an emptly list if this geometry is solid") }),
		@variable (
				name = "contour",
				type = IType.GEOMETRY,
				doc = { @doc ("Returns the polyline representing the contour of this geometry") }) })
public interface IShape extends ILocated, IValue, IAttributed {
	Map<String, Type> JTS_TYPES = GamaMapFactory.createUnordered();
	Set<Type> THREED_TYPES = new HashSet<>();

	enum Type {
		BOX("3D"),
		CIRCLE("3D"),
		CONE("3D"),
		CUBE("3D"),
		SQUARE("3D"),
		ROUNDED(""),
		CYLINDER("3D"),
		GRIDLINE(""),
		LINEARRING("LinearRing"),
		LINESTRING("LineString"),
		MULTILINESTRING("MultiLineString"),
		MULTIPOINT("MultiPoint"),
		MULTIPOLYGON("MultiPolygon"),
		NULL(""),
		PLAN("3D"),
		POINT("Point"),
		POLYGON("Polygon"),
		POLYHEDRON("3D"),
		POLYPLAN("3D"),
		PYRAMID("3D"),
		SPHERE("3D"),
		TEAPOT("3D"),
		LINECYLINDER("3D"),
		THREED_FILE("");

		Type(final String name) {
			if (name.isEmpty()) return;
			if ("3D".equals(name)) {
				THREED_TYPES.add(this);
			} else {
				JTS_TYPES.put(name, this);
			}
		}
	}

	WKTWriter SHAPE_WRITER = new WKTWriter();

	@Override
	IShape copy(IScope scope);

	boolean covers(IShape g);
	
	boolean partiallyOverlaps(IShape g);

	boolean touches(IShape g);

	boolean crosses(IShape g);

	void dispose();

	double euclidianDistanceTo(GamaPoint g);

	double euclidianDistanceTo(IShape g);

	IAgent getAgent();

	Envelope3D getEnvelope();

	/**
	 * Returns the geometrical type of this shape. May be computed dynamically (from the JTS inner geometry) or stored
	 * somewhere (in the attributes of the shape, using TYPE_ATTRIBUTE)
	 *
	 * @param g
	 * @return
	 */
	IShape.Type getGeometricalType();

	IShape getGeometry();

	Geometry getInnerGeometry();

	boolean intersects(IShape g);

	boolean isLine();

	boolean isPoint();

	void setAgent(IAgent agent);

	void setGeometry(IShape g);

	void setInnerGeometry(Geometry intersection);

	void setDepth(double depth);

	@Override
	@getter ("attributes")
	IMap<String, Object> getOrCreateAttributes();

	@getter ("multiple")
	boolean isMultiple();

	@getter ("area")
	Double getArea();

	@getter ("volume")
	Double getVolume();

	@getter ("perimeter")
	double getPerimeter();

	@getter ("holes")
	IList<GamaShape> getHoles();

	@getter ("centroid")
	GamaPoint getCentroid();

	@getter ("contour")
	GamaShape getExteriorRing(IScope scope);

	@getter ("width")
	Double getWidth();

	@getter ("height")
	Double getHeight();

	@getter ("depth")
	Double getDepth();

	@getter ("envelope")
	GamaShape getGeometricEnvelope();

	@getter ("points")
	IList<GamaPoint> getPoints();

	@getter ("geometries")
	IList<? extends IShape> getGeometries();

	/**
	 * Copy only the attributes that support defining the shape
	 *
	 * @param other
	 */
	default void copyShapeAttributesFrom(final IShape other) {
		final Double d = other.getDepth();
		if (d != null) { setDepth(d); }
		final Type t = other.getGeometricalType();
		if (THREED_TYPES.contains(t)) { setGeometricalType(t); }
	}

	void setGeometricalType(Type t);

}