/*******************************************************************************************************
 *
 * GamaGridFile.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.util.file;

import static gama.common.geometry.Envelope3D.of;
import static gama.runtime.GAMA.reportError;
import static gama.runtime.exceptions.GamaRuntimeException.error;
import static gama.runtime.exceptions.GamaRuntimeException.warning;
import static org.geotools.util.factory.Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM;

import java.awt.image.BandedSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferDouble;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridCoverageWriter;
import org.geotools.data.DataSourceException;
import org.geotools.data.PrjFileReader;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.arcgrid.ArcGridWriter;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.GeoTiffWriter;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.util.factory.Hints;
import org.locationtech.jts.geom.Envelope;
import org.opengis.geometry.DirectPosition;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import gama.common.geometry.Envelope3D;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.example;
import gama.core.dev.annotations.GamlAnnotations.file;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.GamaShape;
import gama.metamodel.shape.IShape;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaListFactory;
import gama.util.IList;
import gama.util.matrix.GamaField;
import gaml.statements.Facets;
import gaml.types.GamaGeometryType;
import gaml.types.IType;
import gaml.types.Types;

/**
 * The Class GamaGridFile.
 */
@file (
		name = "grid",
		extensions = { "asc", "tif" },
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.GRID, IConcept.ASC, IConcept.TIF, IConcept.FILE },
		doc = @doc ("Represents .asc or .tif files that contain grid descriptions"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaGridFile extends GamaGisFile implements IFieldMatrixProvider {

	/**
	 * The Class Records.
	 */
	class Records {
		
		/** The x. */
		double x[];
		
		/** The y. */
		double y[];
		
		/** The bands. */
		final List<double[]> bands = new ArrayList<>();

		/**
		 * Fill.
		 *
		 * @param i the i
		 * @param bands2 the bands 2
		 */
		public void fill(final int i, final IList<Double> bands2) {
			for (double[] tab : bands) {
				bands2.add(tab[i]);
			}
		}
	}

	/** The coverage. */
	GridCoverage2D coverage;
	
	/** The num cols. */
	public int nbBands, numRows, numCols;
	
	/** The geom. */
	IShape geom;
	
	/** The no data. */
	Number noData = -9999;
	
	/** The genv. */
	GeneralEnvelope genv;
	
	/** The records. */
	Records records;

	/**
	 * Instantiates a new gama grid file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a asc file or a tif (geotif) file",
			examples = { @example (
					value = "file f <- grid_file(\"file.asc\");",
					isExecutable = false) })

	public GamaGridFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	/**
	 * Instantiates a new gama grid file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param asMatrix the as matrix
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a asc file or a tif (geotif) file, but without converting it into shapes. Only a matrix of float values is created",
			examples = { @example (
					value = "file f <- grid_file(\"file.asc\", false);",
					isExecutable = false) })

	public GamaGridFile(final IScope scope, final String pathName, final boolean asMatrix) throws GamaRuntimeException {
		super(scope, pathName, (Integer) null);
	}

	/**
	 * Instantiates a new gama grid file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param code the code
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a asc file or a tif (geotif) file specifying the coordinates system code, as an int (epsg code)",
			examples = { @example (
					value = "file f <- grid_file(\"file.asc\", 32648);",
					isExecutable = false) })
	public GamaGridFile(final IScope scope, final String pathName, final Integer code) throws GamaRuntimeException {
		super(scope, pathName, code);
	}

	/**
	 * Instantiates a new gama grid file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param code the code
	 */
	@doc (
			value = "This file constructor allows to read a asc file or a tif (geotif) file specifying the coordinates system code (epg,...,), as a string ",
			examples = { @example (
					value = "file f <- grid_file(\"file.asc\",\"EPSG:32648\");",
					isExecutable = false) })
	public GamaGridFile(final IScope scope, final String pathName, final String code) {
		super(scope, pathName, code);
	}

	/**
	 * Instantiates a new gama grid file.
	 *
	 * @param scope the scope
	 * @param pathName the path name
	 * @param field the field
	 */
	@doc (
			value = "This allows to build a writable grid file from the values of a field",
			examples = { @example (
					value = "file f <- grid_file(\"file.tif\",my_field); save f;",
					isExecutable = false) })
	public GamaGridFile(final IScope scope, final String pathName, final GamaField field) {
		super(scope, pathName, false);
		setWritable(scope, true);
		createCoverage(scope, field);
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// No attributes
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Creates the coverage.
	 *
	 * @param scope the scope
	 */
	private void createCoverage(final IScope scope) {
		if (coverage == null) {
			final File gridFile = getFile(scope);
			gridFile.setReadable(true);
			InputStream fis = null;
			try {
				fis = new FileInputStream(gridFile);
			} catch (FileNotFoundException e1) {}
			try {
				privateCreateCoverage(scope, fis);
			} catch (final Exception e) {
				String name = getName(scope);
				if (isTiff(scope)) throw error("The format of " + name + " seems incorrect: " + e.getMessage(), scope);
				// A problem appeared, likely related to the wrong format of the file (see Issue 412)
				reportError(scope, warning("Format of " + name + " seems incorrect. Trying to read it anyway.", scope),
						false);
				fis = fixFileHeader(scope);
				try {
					privateCreateCoverage(scope, fis);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Creates the coverage.
	 *
	 * @param scope the scope
	 * @param field the field
	 */
	private void createCoverage(final IScope scope, final GamaField field) {
		double[] data = field.getMatrix();
		DataBuffer buffer = new DataBufferDouble(data, data.length);
		SampleModel sample = new BandedSampleModel(DataBuffer.TYPE_DOUBLE, field.numCols, field.numRows,
				field.getBandsNumber(scope));
		WritableRaster raster = Raster.createWritableRaster(sample, buffer, null);
		Envelope2D envelope =
				new Envelope2D(getCRS(scope), 0, 0, scope.getSimulation().getWidth(), scope.getSimulation().getHeight());
		GridCoverageFactory factory = CoverageFactoryFinder.getGridCoverageFactory(null);
		GridCoverage2D cov = factory.create(getName(scope), raster, envelope);
		coverage = cov;
	}
	
	/**
	 * Gets the crs.
	 *
	 * @param scope the scope
	 * @return the crs
	 */
	protected CoordinateReferenceSystem getCRS(final IScope scope) {
		final boolean nullProjection = scope.getSimulation().getProjectionFactory().getWorld() == null;
		CoordinateReferenceSystem crs = null;
		try {
			crs = nullProjection ? CRS.decode("EPSG:3857")
						: scope.getSimulation().getProjectionFactory().getWorld().getTargetCRS(scope);
		} catch (NoSuchAuthorityCodeException e) {
			e.printStackTrace();
		} catch (FactoryException e) {
			e.printStackTrace();
		}
		return crs;
	}

	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		if (!writable) return;
		if (coverage == null) return;
		try {
			final File f = getFile(scope);
			f.setWritable(true);
			AbstractGridCoverageWriter writer;
			if (isTiff(scope)) {
				writer = new GeoTiffWriter(f);
			} else {
				writer = new ArcGridWriter(f);
			}
			writer.write(coverage, (GeneralParameterValue[]) null);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Private create coverage.
	 *
	 * @param scope the scope
	 * @param fis the fis
	 * @throws DataSourceException the data source exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void privateCreateCoverage(final IScope scope, final InputStream fis)
			throws DataSourceException, IOException {
		AbstractGridCoverage2DReader store = null;
		try {
			// Necessary to compute it here, because it needs to be passed to the Hints
			final CoordinateReferenceSystem crs = getExistingCRS(scope);
			if (isTiff(scope)) {
				store = crs == null ? new GeoTiffReader(getFile(scope))
						: new GeoTiffReader(getFile(scope), new Hints(DEFAULT_COORDINATE_REFERENCE_SYSTEM, crs));
				noData = ((GeoTiffReader) store).getMetadata().getNoData();
			} else {
				if (crs == null) {
					store = new ArcGridReader(fis);
				} else {
					store = new ArcGridReader(fis, new Hints(DEFAULT_COORDINATE_REFERENCE_SYSTEM, crs));
				}
			}
			genv = store.getOriginalEnvelope();
			final Envelope3D env =
					of(genv.getMinimum(0), genv.getMaximum(0), genv.getMinimum(1), genv.getMaximum(1), 0, 0);
			computeProjection(scope, env);
			numRows = store.getOriginalGridRange().getHigh(1) + 1;
			numCols = store.getOriginalGridRange().getHigh(0) + 1;
			coverage = store.read(null);
		} finally {
			if (store != null) { store.dispose(); }
			scope.getGui().getStatus(scope).endSubStatus("Opening file " + getName(scope));
		}
	}

	/**
	 * Fix file header.
	 *
	 * @param scope the scope
	 * @return the input stream
	 */
	private InputStream fixFileHeader(final IScope scope) {
		final StringBuilder text = new StringBuilder();
		final String NL = System.getProperty("line.separator");

		try (Scanner scanner = new Scanner(getFile(scope))) {
			// final int cpt = 0;
			while (scanner.hasNextLine()) {
				final String line = scanner.nextLine();
				if (line.contains("dx")) {
					text.append(line.replace("dx", "cellsize") + NL);
				} else {
					text.append(line + NL);
				}
			}
		} catch (final FileNotFoundException e2) {
			throw error("The format of " + getName(scope) + " is not correct. Error: " + e2.getMessage(), scope);
		}

		text.append(NL);
		// fis = new StringBufferInputStream(text.toString());
		return new StringBufferInputStream(text.toString());
	}

	/**
	 * Read.
	 *
	 * @param scope the scope
	 * @param readAll the read all
	 * @param createGeometries the create geometries
	 */
	void read(final IScope scope, final boolean readAll, final boolean createGeometries) {

		try {
			scope.getGui().getStatus(scope).beginSubStatus("Reading file " + getName(scope));

			final Envelope envP = gis.getProjectedEnvelope();
			final double cellHeight = envP.getHeight() / numRows;
			final double cellWidth = envP.getWidth() / numCols;
			final IList<IShape> shapes = GamaListFactory.create(Types.GEOMETRY);
			final double originX = envP.getMinX();
			final double originY = envP.getMinY();
			final double maxY = envP.getMaxY();
			final double maxX = envP.getMaxX();
			shapes.add(new GamaPoint(originX, originY));
			shapes.add(new GamaPoint(maxX, originY));
			shapes.add(new GamaPoint(maxX, maxY));
			shapes.add(new GamaPoint(originX, maxY));
			shapes.add(shapes.get(0));
			geom = GamaGeometryType.buildPolygon(shapes);
			if (!readAll) return;

			final double cmx = cellWidth / 2;
			final double cmy = cellHeight / 2;
			final double cellHeightP = genv.getSpan(1) / numRows;
			final double cellWidthP = genv.getSpan(0) / numCols;
			final double originXP = genv.getMinimum(0);
			final double maxYP = genv.getMaximum(1);
			final double cmxP = cellWidthP / 2;
			final double cmyP = cellHeightP / 2;
			if (records == null) {
				records = new Records();
				records.x = new double[numRows * numCols]; // x
				records.y = new double[numRows * numCols]; // y
				records.bands.add(new double[numRows * numCols]); // data
				for (int i = 0, n = numRows * numCols; i < n; i++) {
					scope.getGui().getStatus(scope).setSubStatusCompletion(i / (double) n);

					final int yy = i / numCols;
					final int xx = i - yy * numCols;

					records.x[i] = originX + xx * cellWidth + cmx;
					records.y[i] = maxY - (yy * cellHeight + cmy);

					double[] vd =
							coverage.evaluate((DirectPosition) new DirectPosition2D(originXP + xx * cellWidthP + cmxP,
									maxYP - (yy * cellHeightP + cmyP)), (double[]) null);
					nbBands = vd.length;
					if (i == 0 && vd.length > 1) {
						for (int j = 0; j < vd.length - 1; j++) {
							records.bands.add(new double[numRows * numCols]);
						}
					}
					for (int j = 0; j < vd.length; j++) {
						records.bands.get(j)[i] = vd[j];
					}

					// else if (byteValues) {
					// final byte[] bv = (byte[]) vals;
					// if (i == 0) { nbBands = bv.length; }
					// if (bv.length == 1) {
					// final double v = Double.valueOf(((byte[]) vals)[0]);
					// rect.setAttribute("grid_value", v);
					// } else if (bv.length == 3) {
					// final int red = bv[0] < 0 ? 256 + bv[0] : bv[0];
					// final int green = bv[0] < 0 ? 256 + bv[1] : bv[1];
					// final int blue = bv[0] < 0 ? 256 + bv[2] : bv[2];
					// rect.setAttribute("grid_value", (red + green + blue) / 3.0);
					// }
					// rect.setAttribute("bands", GamaListFactory.create(scope, Types.FLOAT, bv));
					// }

				}
				if (createGeometries) {
					System.out.println("Building geometries !");
					for (int i = 0, n = numRows * numCols; i < n; i++) {

						setBuffer(GamaListFactory.<IShape> create(Types.GEOMETRY));
						final GamaPoint p = new GamaPoint(records.x[i], records.y[i]);
						GamaShape rect = (GamaShape) GamaGeometryType.buildRectangle(cellWidth, cellHeight, p);
						if (gis == null) {
							rect = new GamaShape(rect.getInnerGeometry());
						} else {
							rect = new GamaShape(gis.transform(rect.getInnerGeometry()));
						}
						IList<Double> bands = GamaListFactory.create(scope, Types.FLOAT);
						records.fill(i, bands);
						rect.setAttribute("grid_value", bands.get(0));
						rect.setAttribute("bands", bands);
						((IList) getBuffer()).add(rect);
					}
				}
			}
		} catch (final Exception e) {
			throw error("The format of " + getName(scope) + " is not correct. Error: " + e.getMessage(), scope);
		} finally {
			scope.getGui().getStatus(scope).endSubStatus("Reading file " + getName(scope));
		}

	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		if (gis == null) { createCoverage(scope); }
		return gis.getProjectedEnvelope();
		// OLD : see what it changes to not do it
		// fillBuffer(scope);
		// return gis.getProjectedEnvelope();
	}

	@Override
	protected void fillBuffer(final IScope scope) {
		if (getBuffer() != null) return;
		createCoverage(scope);
		read(scope, true, true);
	}

	/**
	 * Gets the nb rows.
	 *
	 * @param scope the scope
	 * @return the nb rows
	 */
	public int getNbRows(final IScope scope) {
		createCoverage(scope);
		return numRows;
	}

	/**
	 * Checks if is tiff.
	 *
	 * @param scope the scope
	 * @return true, if is tiff
	 */
	public boolean isTiff(final IScope scope) {
		return getExtension(scope).startsWith("tif");
	}

	@Override
	public IShape getGeometry(final IScope scope) {
		createCoverage(scope);
		read(scope, false, false);
		return geom;
	}

	@Override
	protected CoordinateReferenceSystem getOwnCRS(final IScope scope) {
		final File source = getFile(scope);
		// check to see if there is a projection file
		// getting name for the prj file
		final String sourceAsString;
		sourceAsString = source.getAbsolutePath();
		final int index = sourceAsString.lastIndexOf('.');
		final StringBuffer prjFileName;
		if (index == -1) {
			prjFileName = new StringBuffer(sourceAsString);
		} else {
			prjFileName = new StringBuffer(sourceAsString.substring(0, index));
		}
		prjFileName.append(".prj");

		// does it exist?
		final File prjFile = new File(prjFileName.toString());
		if (prjFile.exists()) {
			// it exists then we have to read it
			PrjFileReader projReader = null;
			try (FileInputStream fip = new FileInputStream(prjFile); final FileChannel channel = fip.getChannel();) {
				projReader = new PrjFileReader(channel);
				return projReader.getCoordinateReferenceSystem();
			} catch (final FileNotFoundException e) {
				// warn about the error but proceed, it is not fatal
				// we have at least the default crs to use
				return null;
			} catch (final IOException e) {
				// warn about the error but proceed, it is not fatal
				// we have at least the default crs to use
				return null;
			} catch (final FactoryException e) {
				// warn about the error but proceed, it is not fatal
				// we have at least the default crs to use
				return null;
			} finally {
				if (projReader != null) {
					try {
						projReader.close();
					} catch (final IOException e) {
						// warn about the error but proceed, it is not fatal
						// we have at least the default crs to use
						return null;
					}
				}
			}
		} else if (isTiff(scope)) {
			try {
				final GeoTiffReader store = new GeoTiffReader(getFile(scope));
				return store.getCoordinateReferenceSystem();
			} catch (final DataSourceException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public void invalidateContents() {
		super.invalidateContents();
		if (coverage != null) { coverage.dispose(true); }
		coverage = null;
	}

	/**
	 * Value of.
	 *
	 * @param scope the scope
	 * @param loc the loc
	 * @return the double
	 */
	public Double valueOf(final IScope scope, final GamaPoint loc) {
		return valueOf(scope, loc.getX(), loc.getY());
	}

	/**
	 * Value of.
	 *
	 * @param scope the scope
	 * @param x the x
	 * @param y the y
	 * @return the double
	 */
	public Double valueOf(final IScope scope, final double x, final double y) {
		if (getBuffer() == null) { fillBuffer(scope); }
		Object vals = null;
		try {
			vals = coverage.evaluate(new DirectPosition2D(x, y));
		} catch (final Exception e) {
			vals = noData.doubleValue();
		}
		final boolean doubleValues = vals instanceof double[];
		final boolean intValues = vals instanceof int[];
		final boolean byteValues = vals instanceof byte[];
		final boolean longValues = vals instanceof long[];
		final boolean floatValues = vals instanceof float[];
		Double val = null;
		if (doubleValues) {
			final double[] vd = (double[]) vals;
			val = vd[0];
		} else if (intValues) {
			final int[] vi = (int[]) vals;
			val = Double.valueOf(vi[0]);
		} else if (longValues) {
			final long[] vi = (long[]) vals;
			val = Double.valueOf(vi[0]);
		} else if (floatValues) {
			final float[] vi = (float[]) vals;
			val = Double.valueOf(vi[0]);
		} else if (byteValues) {
			final byte[] bv = (byte[]) vals;
			if (bv.length == 3) {
				final int red = bv[0] < 0 ? 256 + bv[0] : bv[0];
				final int green = bv[0] < 0 ? 256 + bv[1] : bv[1];
				final int blue = bv[0] < 0 ? 256 + bv[2] : bv[2];
				val = (red + green + blue) / 3.0;
			} else {
				val = Double.valueOf(((byte[]) vals)[0]);
			}
		}
		return val;
	}

	@Override
	public int length(final IScope scope) {
		createCoverage(scope);
		return numRows * numCols;
	}

	@Override
	protected SimpleFeatureCollection getFeatureCollection(final IScope scope) {
		return null;
	}

	@Override
	public double getNoData(final IScope scope) {
		return noData.doubleValue();
	}

	@Override
	public int getRows(final IScope scope) {
		createCoverage(scope);
		return numRows;
	}

	@Override
	public int getCols(final IScope scope) {
		createCoverage(scope);
		return numCols;
	}

	@Override
	public int getBandsNumber(final IScope scope) {
		createCoverage(scope);
		return nbBands;
	}

	@Override
	public double[] getBand(final IScope scope, final int index) {
		createCoverage(scope);
		read(scope, true, false);
		return records.bands.get(index);
	}

}
