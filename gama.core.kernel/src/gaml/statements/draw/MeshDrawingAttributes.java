/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.FieldDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.statements.draw;

import java.awt.Color;

import gama.common.geometry.Scaling3D;
import gama.common.preferences.GamaPreferences;
import gama.metamodel.shape.GamaPoint;
import gama.util.GamaColor;
import gama.util.IList;
import gama.util.matrix.IField;
import gaml.operators.Colors.GamaGradient;
import gaml.operators.Colors.GamaPalette;
import gaml.operators.Colors.GamaScale;

public class MeshDrawingAttributes extends FileDrawingAttributes {

	public static final int TRIANGULATED = 32;
	public static final int GRAYSCALED = 64;
	public static final int WITH_TEXT = 128;
	public IMeshColorProvider color;
	public String speciesName;
	GamaPoint dimensions;
	GamaPoint cellSize;
	Double scale;
	double noData;
	int smooth;

	public MeshDrawingAttributes(final String name, final GamaColor border, final boolean isImage) {
		super(null, isImage);
		setBorder(border);
		speciesName = name;
	}

	public void setSpeciesName(final String name) {
		speciesName = name;
	}

	@SuppressWarnings ("unchecked")
	public void setColors(final Object colors) {
		if (colors instanceof GamaColor) {
			color = new ColorBasedMeshColorProvider((GamaColor) colors);
		} else if (colors instanceof GamaPalette) {
			color = new PaletteBasedMeshColorProvider((GamaPalette) colors);
		} else if (colors instanceof GamaScale) {
			color = new ScaleBasedMeshColorProvider((GamaScale) colors);
		} else if (colors instanceof GamaGradient) {
			color = new GradientBasedMeshColorProvider((GamaGradient) colors);
		} else if (colors instanceof IList) {
			if (((IList) colors).get(0) instanceof IField) {
				// We have bands
				color = new BandsBasedMeshColorProvider((IList<IField>) colors);
			} else {
				color = new ListBasedMeshColorProvider((IList<Color>) colors);
			}
		} else if (isGrayscaled()) {
			color = IMeshColorProvider.GRAYSCALE;
		} else {
			color = IMeshColorProvider.DEFAULT;
		}
	}

	// Rules are a bit different for the fill color for fields.

	public IMeshColorProvider getColorProvider() {
		if (isSet(SELECTED)) return new ColorBasedMeshColorProvider(SELECTED_COLOR);
		if (highlight != null) return new ColorBasedMeshColorProvider(highlight);
		if (isSet(EMPTY)) return null;
		return color;
	}

	@Override
	public GamaColor getColor() {
		if (isSet(SELECTED)) return SELECTED_COLOR;
		if (highlight != null) return highlight;
		if (isSet(EMPTY) || isSet(GRAYSCALED)) return null;
		if (textures != null) return TEXTURED_COLOR;
		return fill == null ? GamaPreferences.Displays.CORE_COLOR.getValue() : fill;
	}

	@Override
	public String getSpeciesName() {
		return speciesName;
	}

	public GamaPoint getXYDimension() {
		return dimensions;
	}

	public void setXYDimension(final GamaPoint dim) {
		dimensions = dim;
	}

	public void setCellSize(final GamaPoint p) {
		cellSize = p;
	}

	public GamaPoint getCellSize() {
		return cellSize;
	}

	public void setScale(final Double s) {
		scale = s;
	}

	/**
	 * Returns the z-scaling factor for this field
	 *
	 * @return
	 */
	public Double getScale() {
		if (scale == null) {
			Scaling3D size = getSize();
			return size == null ? 1d : size.getZ();
		}
		return scale;
	}

	/**
	 * A value > 1 to indicate a maximum; a value between 0 and 1 to indicate a scaling of the elevation values
	 *
	 * @return
	 */
	public double getZFactor() {
		return getSize().getZ();
	}

	public boolean isTriangulated() {
		return isSet(TRIANGULATED);
	}

	public boolean isGrayscaled() {
		return isSet(GRAYSCALED);
	}

	public boolean isWithText() {
		return isSet(WITH_TEXT);
	}

	public void setGrayscaled(final Boolean grayScaled2) {
		if (color == null) { color = new GrayscaleMeshColorProvider(); }
		setFlag(GRAYSCALED, grayScaled2);
	}

	public void setTriangulated(final Boolean triangulated2) {
		setFlag(TRIANGULATED, triangulated2);
	}

	public void setWithText(final Boolean showText) {
		setFlag(WITH_TEXT, showText);
	}

	public void setSmooth(final int smooth) {
		this.smooth = smooth;
	}

	public int getSmooth() {
		return smooth;
	}

	public void setNoData(final double noData) {
		this.noData = noData;
	}

	public double getNoDataValue() {
		return noData;
	}

	public void setTransparency(final Double transparency) {
		// TODO Auto-generated method stub

	}
}