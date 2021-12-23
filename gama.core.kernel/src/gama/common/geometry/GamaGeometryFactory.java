/*******************************************************************************************************
 *
 * GamaGeometryFactory.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.common.geometry;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;

import gama.metamodel.shape.GamaPoint;

/**
 * A factory for creating GamaGeometry objects.
 */
public class GamaGeometryFactory extends GeometryFactory {

	/** The Constant COORDINATES_FACTORY. */
	public static final GamaCoordinateSequenceFactory COORDINATES_FACTORY = new GamaCoordinateSequenceFactory();
	
	/** The Constant JTS_COORDINATES_FACTORY. */
	public static final CoordinateSequenceFactory JTS_COORDINATES_FACTORY = CoordinateArraySequenceFactory.instance();

	/**
	 * Instantiates a new gama geometry factory.
	 */
	public GamaGeometryFactory() {
		super(COORDINATES_FACTORY);
	}

	/**
	 * Checks if is ring.
	 *
	 * @param pts the pts
	 * @return true, if is ring
	 */
	public static boolean isRing(final Coordinate[] pts) {
		if (pts.length < 4) { return false; }
		if (!pts[0].equals(pts[pts.length - 1])) { return false; }
		return true;
	}

	/**
	 * Checks if is ring.
	 *
	 * @param pts the pts
	 * @return true, if is ring
	 */
	public static boolean isRing(final List<GamaPoint> pts) {
		final int size = pts.size();
		if (size < 4) { return false; }
		if (!pts.get(0).equals(pts.get(size - 1))) { return false; }
		return true;
	}

	// public static boolean isRing(final double[] array) {
	// final int size = array.length;
	// if (size < 12) { return false; }
	// if (array[0] != array[size - 3] || array[1] != array[size - 2] || array[2] != array[size - 1]) { return false; }
	// return true;
	// }

	/**
	 * Linear rings are created using a simple coordinate array, without enforcing any clockwiseness condition.
	 */
	@Override
	public LinearRing createLinearRing(final Coordinate[] coordinates) {
		Coordinate[] coords = coordinates;
		if (!isRing(coords)) {
			coords = (Coordinate[]) ArrayUtils.add(coords, coords[0]);
		}
		return createLinearRing(JTS_COORDINATES_FACTORY.create(coords));
	}

	/**
	 * Builds the rectangle.
	 *
	 * @param points the points
	 * @return the polygon
	 */
	public Polygon buildRectangle(final Coordinate[] points) {
		final CoordinateSequenceFactory fact = GamaGeometryFactory.COORDINATES_FACTORY;
		final CoordinateSequence cs = fact.create(points);
		final LinearRing geom = GeometryUtils.GEOMETRY_FACTORY.createLinearRing(cs);
		final Polygon p = GeometryUtils.GEOMETRY_FACTORY.createPolygon(geom, null);
		return p;
	}

	/**
	 * Polygons are created after ensuring that the coordinate sequence in them has been turned clockwise
	 */

	@Override
	public Polygon createPolygon(final LinearRing shell, final LinearRing[] holes) {
		final LinearRing shellClockwise = turnClockwise(shell);
		if (holes != null) {
			for (int i = 0; i < holes.length; i++) {
				holes[i] = turnClockwise(holes[i]);
			}
		}
		return super.createPolygon(shellClockwise, holes);
	}

	/**
	 * Turn clockwise.
	 *
	 * @param ring the ring
	 * @return the linear ring
	 */
	private LinearRing turnClockwise(final LinearRing ring) {
		if (ring == null || ring.isEmpty()) { return ring; }
		return createLinearRing(COORDINATES_FACTORY.create(ring.getCoordinateSequence()));
	}

	@Override
	public GamaCoordinateSequenceFactory getCoordinateSequenceFactory() {
		return COORDINATES_FACTORY;
	}

	/**
	 * Creates a new GamaGeometry object.
	 *
	 * @param coordinates the coordinates
	 * @param copyPoints the copy points
	 * @return the line string
	 */
	public LineString createLineString(final GamaPoint[] coordinates, final boolean copyPoints) {
		return createLineString(COORDINATES_FACTORY.create(coordinates, copyPoints));
	}

}