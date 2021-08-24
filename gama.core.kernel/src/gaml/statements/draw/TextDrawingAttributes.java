/*******************************************************************************************************
 *
 * TextDrawingAttributes.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.statements.draw;

import gama.common.geometry.AxisAngle;
import gama.common.geometry.Scaling3D;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.IShape.Type;
import gama.util.GamaColor;
import gama.util.GamaFont;

/**
 * The Class TextDrawingAttributes.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class TextDrawingAttributes extends DrawingAttributes implements Cloneable {

	/** The font. */
	private GamaFont font;
	
	/** The perspective. */
	public Boolean perspective;
	
	/** The anchor. */
	public GamaPoint anchor;
	
	/** The precision. */
	public Double precision;

	/**
	 * Instantiates a new text drawing attributes.
	 *
	 * @param size the size
	 * @param rotation the rotation
	 * @param location the location
	 * @param color the color
	 */
	public TextDrawingAttributes(final Scaling3D size, final AxisAngle rotation, final GamaPoint location,
			final GamaColor color) {
		super(size, rotation, location, color, null, null);
		setType(Type.POLYGON);
	}

	/**
	 * Sets the perspective.
	 *
	 * @param perspective the new perspective
	 */
	public void setPerspective(final Boolean perspective) {
		this.perspective = perspective;
	}

	@Override
	public GamaColor getColor() {
		if (isEmpty() && fill == null && border != null) return border;
		return super.getColor();
	}

	/**
	 * Sets the anchor.
	 *
	 * @param anchor the new anchor
	 */
	public void setAnchor(final GamaPoint anchor) {
		this.anchor = anchor;
	}

	@Override
	public GamaPoint getAnchor() {
		if (anchor == null) return super.getAnchor();
		return anchor;
	}

	/**
	 * Gets the font.
	 *
	 * @return the font
	 */
	public GamaFont getFont() {
		return font;
	}

	/**
	 * Sets the font.
	 *
	 * @param font the new font
	 */
	public void setFont(final GamaFont font) {
		this.font = font;
	}

	/**
	 * Checks if is perspective.
	 *
	 * @return true, if is perspective
	 */
	public boolean isPerspective() {
		return perspective == null ? true : perspective;
	}

	@Override
	public Double getDepth() {
		return depth == null ? 0d : depth;
	}

	/**
	 * Copy translated by.
	 *
	 * @param p the p
	 * @return the text drawing attributes
	 */
	public TextDrawingAttributes copyTranslatedBy(final GamaPoint p) {
		try {
			TextDrawingAttributes clone = (TextDrawingAttributes) this.clone();
			clone.setLocation(getLocation().plus(p));
			return clone;
		} catch (CloneNotSupportedException e) {}
		return new TextDrawingAttributes(getSize(), getRotation(), getLocation().plus(p), getColor());
	}

	/**
	 * Sets the precision.
	 *
	 * @param prec the new precision
	 */
	public void setPrecision(final Double prec) {
		precision = Math.min(1, Math.max(0, prec));
	}

	/**
	 * Gets the precision.
	 *
	 * @return the precision
	 */
	public Double getPrecision() {
		return precision;
	}

}