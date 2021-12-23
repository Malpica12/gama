/*******************************************************************************************************
 *
 * GeometryDrawer.java, in gama.display.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.display.opengl.scene.geometry;

import static gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;
import static gama.common.geometry.GeometryUtils.applyToInnerGeometries;
import static gama.common.geometry.GeometryUtils.getContourCoordinates;
import static gama.common.geometry.GeometryUtils.getHolesNumber;
import static gama.common.geometry.GeometryUtils.getTypeOf;
import static gama.common.geometry.GeometryUtils.getYNegatedCoordinates;

import java.awt.Color;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import com.jogamp.opengl.util.gl2.GLUT;

import gama.common.geometry.Envelope3D;
import gama.common.geometry.ICoordinates;
import gama.common.geometry.Rotation3D;
import gama.common.geometry.UnboundedCoordinateSequence;
import gama.common.geometry.Scaling3D.Heterogeneous;
import gama.display.opengl.OpenGL;
import gama.display.opengl.scene.ObjectDrawer;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.IShape;
import gama.metamodel.shape.IShape.Type;
import gama.util.GamaColor;
import gaml.types.GamaGeometryType;

/**
 *
 * The class GeometryDrawer. The main drawer, responsible for drawing JTS geometries, images and 3D files. This instance
 * (unique for a renderer) maintains a state (represented by _normal, _vertices, _quadvertices _tangent, _rot and
 * _scale) in order to limit the number of intermediary objects created. The downside is that methods needs to pay extra
 * attention in *not* spoiling the state (i.e. reusing an already set variable). All the methods that depend on the
 * correctness of the state are private (so that they are not called from outside)
 *
 * @author drogoul
 * @since 4 mai 2013
 * @revised january 2017
 *
 */
public class GeometryDrawer extends ObjectDrawer<GeometryObject> {

	/** The Constant DEFAULT_BORDER. */
	private static final GamaColor DEFAULT_BORDER = new GamaColor(Color.black);

	/** The normal. */
	final GamaPoint _normal = new GamaPoint();
	
	/** The center. */
	final GamaPoint _center = new GamaPoint();
	
	/** The tangent. */
	final GamaPoint _tangent = new GamaPoint();
	
	/** The rot. */
	final Rotation3D _rot = Rotation3D.identity();
	
	/** The scale. */
	final Heterogeneous _scale = new Heterogeneous(1, 1, 1);
	
	/** The quadvertices. */
	final ICoordinates _quadvertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(5, 3);
	
	/** The vertices. */
	final UnboundedCoordinateSequence _vertices = new UnboundedCoordinateSequence();

	/**
	 * Instantiates a new geometry drawer.
	 *
	 * @param gl the gl
	 */
	public GeometryDrawer(final OpenGL gl) {
		super(gl);
	}

	/**
	 * The inherited drawing method. Applies the rotation, translation and scaling declared in the draw statement,
	 * computes a number of properties attached to the geometry object, and calls the main drawing method
	 */
	@Override
	protected final void _draw(final GeometryObject object) {
		gl.pushMatrix();
		try {

			applyRotation(object);
			applyTranslation(object);
			applyScaling(object);
			// final boolean solid = object.isFilled() || gl.isTextured();
			final Color border = !object.isFilled() && object.getAttributes().getBorder() == null
					? object.getAttributes().getColor() : object.getAttributes().getBorder();
			final Geometry geometry = object.getObject();
			Double d = object.getAttributes().getDepth();
			final double height = d == null ? 0d : d;
			final IShape.Type type = object.getAttributes().getType();
			drawGeometry(geometry, /* solid, */ border, height, type);
		} finally {
			gl.popMatrix();
		}
	}

	/**
	 * The main drawing method, which does not rely on the state so that it can be called from outside. Depending on the
	 * type of the geometry, it calls specialized sub-methods
	 *
	 * @param geom            the geometry to draw
	 * @param border            whether a border needs to be drawn around it (null means no)
	 * @param height            the height of special 3D geometries
	 * @param type            the type of the geometry
	 */
	public void drawGeometry(final Geometry geom, /* final boolean solid, */ final Color border, final double height,
			final IShape.Type type) {
		switch (type) {
			case SPHERE:
				drawSphere(geom, /* solid, */ height, border);
				break;
			case CONE:
				drawCone3D(geom, /* solid, */ height, border);
				break;
			case TEAPOT:
				drawTeapot(geom, /* solid, */height, border);
				break;
			case PYRAMID:
				drawPyramid(geom, /* solid, */ height, border);
				break;
			case CYLINDER:
				drawCylinder(geom, /* solid, */ height, border);
				break;
			case LINECYLINDER:
				drawLineCylinder(geom, /* solid, */ height, border);
				break;
			case CIRCLE:
				drawCircle(geom, /* solid, */ height, border);
				break;
			case CUBE:
			case BOX:
				drawCube(geom, /* solid, */ height, border);
				break;
			case POLYGON:
			case SQUARE:
			case POLYHEDRON:
			case GRIDLINE:
				if (geom instanceof Polygon) {
					if (height != 0) {
						drawPolyhedron((Polygon) geom, /* solid, */ height, border);
					} else {
						drawPolygon((Polygon) geom, /* solid, */ border, true, true);
					}
				}
				break;
			case LINESTRING:
				if (height != 0) {
					drawLineCylinder(geom, /* solid, */ height, border);
					break;
				}
				//$FALL-THROUGH$
			case LINEARRING:
			case PLAN:
			case POLYPLAN:
				drawPlan(geom, /* solid, */ height, border);
				break;
			case POINT:
				drawPoint(geom, /* solid, */ gl.getMaxEnvDim() / 800d, border);
				break;
			default:
				applyToInnerGeometries(geom, g -> {
					drawGeometry(g, /* solid, */ border, height, getTypeOf(g));
				});
		}
	}

	/**
	 * Draw polyhedron.
	 *
	 * @param polygon the polygon
	 * @param height the height
	 * @param border the border
	 */
	private void drawPolyhedron(final Polygon polygon, /* final boolean solid, */final double height,
			final Color border) {
		// final boolean hasHoles = getHolesNumber(polygon) > 0;
		// Draw bottom
		drawPolygon(polygon, /* solid, */ border, /* hasHoles ? border : null, */ true, true);
		_vertices.getNormal(true, height, _normal);
		try {
			gl.pushMatrix();
			gl.translateBy(_normal.x, _normal.y, _normal.z);
			// Draw top
			drawPolygon(polygon, /* solid, */ border, /* hasHoles ? border null, */ true, false);
		} finally {
			gl.popMatrix();
		}
		gl.enableAlternateTexture();
		// Draw faces
		_vertices.visit((pj, pk) -> {
			_quadvertices.setTo(pk.x, pk.y, pk.z, pk.x + _normal.x, pk.y + _normal.y, pk.z + _normal.z,
					pj.x + _normal.x, pj.y + _normal.y, pj.z + _normal.z, pj.x, pj.y, pj.z, pk.x, pk.y, pk.z);
			gl.drawSimpleShape(_quadvertices, 4, /* solid, */ true, true, border);
		});

	}

	/**
	 * Draw polygon.
	 *
	 * @param p the p
	 * @param border the border
	 * @param clockwise the clockwise
	 * @param computeVertices the compute vertices
	 */
	private void drawPolygon(final Polygon p, /* final boolean solid, */final Color border, final boolean clockwise,
			final boolean computeVertices) {
		if (computeVertices) { _vertices.setToYNegated(getContourCoordinates(p)); }
		if (!gl.isWireframe()) {
			gl.setNormal(_vertices, clockwise);
			final boolean hasHoles = getHolesNumber(p) > 0;
			final int size = _vertices.size();
			if (hasHoles || size > 5) {
				gl.drawPolygon(p, _vertices, clockwise);
			} else {
				gl.drawSimpleShape(_vertices, size - 1, /* solid, */clockwise, false, null);
			}
		}
		if (border != null) {
			gl.drawClosedLine(_vertices, border, -1);
			applyToInnerGeometries(p, ring -> gl.drawClosedLine(getYNegatedCoordinates(ring), border, -1));
		}
	}

	/**
	 * Draw plan.
	 *
	 * @param p the p
	 * @param height the height
	 * @param border the border
	 */
	private void drawPlan(final Geometry p, /* final boolean solid, */final double height, final Color border) {
		_vertices.setToYNegated(getContourCoordinates(p));
		if (height != 0) {
			_vertices.visit((pj, pk) -> {
				_quadvertices.setTo(pk.x, pk.y, pk.z, pk.x, pk.y, pk.z + height, pj.x, pj.y, pj.z + height, pj.x, pj.y,
						pj.z, pk.x, pk.y, pk.z);
				gl.drawSimpleShape(_quadvertices, 4, /* solid, */ true, true, border);
			});
		} else {
			gl.drawLine(_vertices, -1);
		}
	}

	/**
	 * Draw cached geometry.
	 *
	 * @param type the type
	 * @param border the border
	 */
	private void drawCachedGeometry(final IShape.Type type, /* final boolean solid, */ final Color border) {
		gl.pushMatrix();
		gl.translateBy(_center);
		gl.rotateBy(_rot.rotateToHorizontal(_normal, _tangent, false).revertInPlace());
		gl.scaleBy(_scale);
		gl.drawCachedGeometry(type, /* solid, */ border);
		gl.popMatrix();

	}

	/**
	 * Draw point.
	 *
	 * @param point the point
	 * @param height the height
	 * @param border the border
	 */
	private void drawPoint(final Geometry point, /* final boolean solid, */ final double height, final Color border) {
		_center.setCoordinate(point.getCoordinate());
		_center.y *= -1;
		_scale.setTo(height);
		_rot.setToIdentity();
		drawCachedGeometry(Type.POINT, /* solid, */ border);
	}

	/**
	 * Draw cube.
	 *
	 * @param p the p
	 * @param height the height
	 * @param border the border
	 */
	private void drawCube(final Geometry p, /* final boolean solid, */ final double height, final Color border) {
		_vertices.setToYNegated(getContourCoordinates(p));
		_vertices.getNormal(true, 1, _normal);
		_vertices.getCenter(_center);
		_tangent.setLocation(_vertices.at(0)).subtract(_vertices.at(1));
		_scale.setTo(_tangent.norm(), _vertices.at(2).euclidianDistanceTo(_vertices.at(1)), height);
		drawCachedGeometry(Type.CUBE, /* solid, */ border);
	}

	/**
	 * Draw pyramid.
	 *
	 * @param p the p
	 * @param height the height
	 * @param border the border
	 */
	private void drawPyramid(final Geometry p, /* final boolean solid, */final double height, final Color border) {
		_vertices.setToYNegated(getContourCoordinates(p));
		_vertices.getNormal(true, 1, _normal);
		_vertices.getCenter(_center);
		_tangent.setLocation(_vertices.at(0)).subtract(_vertices.at(1));
		_scale.setTo(height);
		drawCachedGeometry(Type.PYRAMID, /* solid, */ border);
	}

	/**
	 * Draw sphere.
	 *
	 * @param p the p
	 * @param height the height
	 * @param border the border
	 */
	private void drawSphere(final Geometry p, /* final boolean solid, */ final double height, final Color border) {
		_vertices.setToYNegated(getContourCoordinates(p));
		_vertices.getNormal(true, 1, _normal);
		_vertices.getCenter(_center);
		_tangent.setLocation(_center).subtract(_vertices.at(0));
		_scale.setTo(height);
		drawCachedGeometry(Type.SPHERE, /* solid, */ border);
	}

	/**
	 * Draw circle.
	 *
	 * @param p the p
	 * @param height the height
	 * @param border the border
	 */
	private void drawCircle(final Geometry p, /* final boolean solid, */final double height, final Color border) {
		_vertices.setToYNegated(getContourCoordinates(p));
		_vertices.getNormal(true, 1, _normal);
		_vertices.getCenter(_center);
		_tangent.setLocation(_center).subtract(_vertices.at(0));
		_scale.setTo(height);
		drawCachedGeometry(Type.CIRCLE, /* solid, */border);
	}

	// public void drawRoundedRectangle(final GamaPoint pos, /*final boolean solid,*/final double width, final double
	// height,
	// final Color fill, final Color border) {
	// _center.setCoordinate(pos);
	// _scale.setTo(width, height, 1);
	// gl.setCurrentColor(fill);
	// drawCachedGeometry(Type.ROUNDED, solid, border);
	// }

	/**
	 * Draw cylinder.
	 *
	 * @param g the g
	 * @param height the height
	 * @param border the border
	 */
	private void drawCylinder(final Geometry g, /* final boolean solid, */ final double height, final Color border) {
		_vertices.setToYNegated(getContourCoordinates(g));
		final double radius = g instanceof Polygon ? _vertices.getLength() / (2 * Math.PI) : height;
		_vertices.getCenter(_center);
		_vertices.getNormal(true, 1, _normal);
		_tangent.setLocation(_center).subtract(_vertices.at(0));
		_scale.setTo(radius, radius, height);
		drawCachedGeometry(Type.CYLINDER, /* solid, */ border);
	}

	/**
	 * Draw line cylinder.
	 *
	 * @param g the g
	 * @param radius the radius
	 * @param border the border
	 */
	private void drawLineCylinder(final Geometry g, /* final boolean solid, */ final double radius,
			final Color border) {
		_vertices.setToYNegated(getContourCoordinates(g));
		for (int i = 0, n = _vertices.size(); i < n - 1; i++) {
			final GamaPoint v1 = _vertices.at(i);
			final GamaPoint v2 = _vertices.at(i + 1);
			// draw first sphere
			_center.setLocation(v1);
			_normal.setLocation(v2);
			_normal.subtract(v1);
			final double height = _normal.norm();
			_tangent.setLocation(_normal.orthogonal());
			_normal.normalize();
			if (i > 0) {
				_scale.setTo(radius);
				drawCachedGeometry(Type.SPHERE, /* solid, */ border);
			}
			// draw tube
			_scale.setTo(radius, radius, height);
			drawCachedGeometry(Type.CYLINDER, /* solid, */border);

		}

	}

	/**
	 * Draw cone 3 D.
	 *
	 * @param p the p
	 * @param height the height
	 * @param border the border
	 */
	private void drawCone3D(final Geometry p, /* final boolean solid, */final double height, final Color border) {
		_vertices.setToYNegated(getContourCoordinates(p));
		final double radius = p instanceof Polygon ? _vertices.getLength() / (2 * Math.PI) : _vertices.getLength();
		_vertices.getCenter(_center);
		_vertices.getNormal(true, 1, _normal);
		_tangent.setLocation(_center).subtract(_vertices.at(0));
		_rot.rotateToHorizontal(_normal, _tangent, false).revertInPlace();
		_scale.setTo(radius, radius, height);
		drawCachedGeometry(Type.CONE, /* solid, */ border);
	}

	/**
	 * Draw teapot.
	 *
	 * @param p the p
	 * @param height the height
	 * @param border the border
	 */
	private void drawTeapot(final Geometry p, /* final boolean solid, */final double height, final Color border) {
		_vertices.setToYNegated(getContourCoordinates(p));
		try {
			gl.pushMatrix();
			_vertices.getCenter(_center);
			gl.translateBy(_center);
			gl.rotateBy(90, 1.0, 0.0, 0.0);
			final GLUT glut = gl.getGlut();
			if (!gl.isWireframe()) {
				glut.glutSolidTeapot(height);
				if (border != null) {
					gl.setCurrentColor(border);
					glut.glutWireTeapot(height);
				}
			} else {
				glut.glutWireTeapot(height);
			}
		} finally {
			gl.popMatrix();
		}
	}

	/**
	 * Helper method that draws the ROI box in the world.
	 *
	 * @param envelope            the size of the ROI box
	 */
	public void drawROIHelper(final Envelope3D envelope) {
		if (envelope == null) return;
		final Polygon polygon = envelope.yNegated().toGeometry();
		gl.setCurrentColor(0, 0.5, 0, 0.15);
		gl.setZIncrement(0);
		boolean old = gl.isWireframe();
		gl.setObjectWireframe(false);
		drawPolyhedron(polygon, /* true, */ envelope.getMaxZ(), DEFAULT_BORDER);
		gl.setObjectWireframe(old);
	}

	/**
	 * Helper method that draws the sphere used to indicate a user rotation.
	 *
	 * @param target the target
	 * @param distance            the distance used as a reference (between the camera and its target)
	 * @param height the height
	 */
	public void drawRotationHelper(final GamaPoint target, final double distance, final double height) {
		gl.setZIncrement(0);
		gl.setCurrentColor(Color.gray, 0.3);
		final GamaPoint position = target.yNegated().add(0, 0, -height);
		final Geometry point = GamaGeometryType.buildCircle(height, position).getInnerGeometry();
		boolean old = gl.isWireframe();
		gl.setObjectWireframe(false);
		drawSphere(point, /* true, */ height, DEFAULT_BORDER);
		gl.setObjectWireframe(old);
	}

	@Override
	public void dispose() {}

}