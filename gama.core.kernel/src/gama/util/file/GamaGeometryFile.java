/*******************************************************************************************************
 *
 * msi.gama.util.file.GamaGeometryFile.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.util.file;

import gama.common.geometry.AxisAngle;
import gama.common.geometry.Envelope3D;
import gama.metamodel.shape.IShape;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IList;
import gaml.types.IContainerType;
import gaml.types.Types;

/**
 * Class GamaGeometryFile. An abstract class that supports loading and saving geometries in specific subclasses. The
 * buffer is a GamaList of points (GamaPoint) from which the GamaGeometry can be constructed (using
 * geometry(file("..."));)
 *
 * @author drogoul
 * @since 30 déc. 2013
 *
 */
public abstract class GamaGeometryFile extends GamaFile<IList<IShape>, IShape> {

	protected IShape geometry;

	public GamaGeometryFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	public GamaGeometryFile(final IScope scope, final String pathName, final boolean b) {
		super(scope, pathName, b);
	}

	@Override
	public IContainerType<?> getGamlType() {
		return Types.FILE.of(Types.INT, Types.GEOMETRY);
	}

	/**
	 * Method computeEnvelope()
	 *
	 * @see gama.util.file.IGamaFile#computeEnvelope(gama.runtime.IScope)
	 */
	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return getGeometry(scope).getEnvelope();
	}

	public IShape getGeometry(final IScope scope) {
		fillBuffer(scope);
		if (geometry == null) { geometry = buildGeometry(scope); }
		return geometry;
	}

	protected abstract IShape buildGeometry(IScope scope);

	@Override
	public void invalidateContents() {
		super.invalidateContents();
		geometry = null;
	}

	public AxisAngle getInitRotation() {
		return null;
	}

	public boolean is2D() {
		return true;
	}

}