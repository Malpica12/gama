/*******************************************************************************************************
 *
 * DrawingData.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.statements.draw;

import java.awt.Color;
import java.util.function.Function;

import gama.common.geometry.AxisAngle;
import gama.common.geometry.Rotation3D;
import gama.common.interfaces.IKeyword;
import gama.common.preferences.GamaPreferences;
import gama.metamodel.shape.GamaPoint;
import gama.util.GamaColor;
import gama.util.GamaFont;
import gama.util.GamaListFactory;
import gama.util.GamaMaterial;
import gama.util.GamaPair;
import gama.util.IList;
import gaml.expressions.IExpression;
import gaml.operators.Cast;
import gaml.operators.IUnits;
import gaml.types.GamaFontType;
import gaml.types.GamaListType;
import gaml.types.IType;
import gaml.types.Types;

/**
 * Class DrawingData. This class contains a number of attributes to help draw geometries, pictures, files and text.
 * These attributes are supplied either by the draw statement or by the layer
 *
 * @author drogoul
 * @since 28 janv. 2016
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DrawingData extends AttributeHolder {

	/** The Constant DEFAULT_BORDER_COLOR. */
	static final GamaColor DEFAULT_BORDER_COLOR = new GamaColor(Color.BLACK);

	/** The size. */
	final Attribute<GamaPoint> size;
	
	/** The depth. */
	final Attribute<Double> depth;
	
	/** The rotation. */
	final Attribute<AxisAngle> rotation;
	
	/** The location. */
	final Attribute<GamaPoint> location;
	
	/** The anchor. */
	final Attribute<GamaPoint> anchor;
	
	/** The empty. */
	final Attribute<Boolean> empty;
	
	/** The color. */
	final Attribute<GamaColor> border, color;
	
	/** The font. */
	final Attribute<GamaFont> font;
	
	/** The texture. */
	final Attribute<IList> texture;
	
	/** The material. */
	final Attribute<GamaMaterial> material;
	
	/** The perspective. */
	final Attribute<Boolean> perspective;
	
	/** The line width. */
	final Attribute<Double> lineWidth;
	
	/** The lighting. */
	final Attribute<Boolean> lighting;
	
	/** The precision. */
	final Attribute<Double> precision;

	/**
	 * Instantiates a new drawing data.
	 *
	 * @param symbol the symbol
	 */
	public DrawingData(final DrawStatement symbol) {
		super(symbol);
		final Function<IExpression, GamaPoint> constSizeCaster = (exp) -> {
			if (exp.getGamlType().isNumber()) {
				final double val = Cast.asFloat(null, exp.getConstValue());
				// We do not consider the z ordinate -- see Issue #1539
				return new GamaPoint(val, val, 0);
			} else
				return (GamaPoint) exp.getConstValue();
		};
		this.size = create(IKeyword.SIZE, (scope, exp) -> {
			if (exp.getGamlType().isNumber()) {
				final double val = Cast.asFloat(scope, exp.value(scope));
				// We do not consider the z ordinate -- see Issue #1539
				return new GamaPoint(val, val, 0);
			} else
				return (GamaPoint) exp.value(scope);
		}, Types.POINT, null, constSizeCaster);
		this.lighting = create(IKeyword.LIGHTED, Types.BOOL, true);
		this.depth = create(IKeyword.DEPTH, Types.FLOAT, null);
		this.precision = create("precision", Types.FLOAT, 0.01);
		final Function<IExpression, AxisAngle> constRotationCaster = (exp) -> {
			if (exp.getGamlType().getGamlType() == Types.PAIR) {
				final GamaPair currentRotation = Cast.asPair(null, exp.getConstValue(), true);
				return new AxisAngle(Cast.asPoint(null, currentRotation.value),
						Cast.asFloat(null, currentRotation.key));
			} else
				return new AxisAngle(Rotation3D.PLUS_K, Cast.asFloat(null, exp.getConstValue()));
		};
		this.rotation = create(IKeyword.ROTATE, (scope, exp) -> {
			if (exp.getGamlType().getGamlType() == Types.PAIR) {
				final GamaPair currentRotation = Cast.asPair(scope, exp.value(scope), true);
				return new AxisAngle(Cast.asPoint(scope, currentRotation.value),
						Cast.asFloat(scope, currentRotation.key));
			} else
				return new AxisAngle(Rotation3D.PLUS_K, Cast.asFloat(scope, exp.value(scope)));
		}, Types.NO_TYPE, null, constRotationCaster);
		this.anchor = create(IKeyword.ANCHOR, (scope, exp) -> {
			final GamaPoint p = Cast.asPoint(scope, exp.value(scope));
			p.x = Math.min(1d, Math.max(p.x, 0d));
			p.y = Math.min(1d, Math.max(p.y, 0d));
			return p;
		}, Types.POINT, IUnits.bottom_left, (e) -> Cast.asPoint(null, e.getConstValue()));
		this.location = create(IKeyword.AT, Types.POINT, null);
		this.empty = create(IKeyword.WIREFRAME, Types.BOOL, false);
		final Function<IExpression, GamaColor> constBorderCaster = (exp) -> {
			if (exp.getGamlType() == Types.BOOL) {
				final boolean hasBorder = (boolean) exp.getConstValue();
				if (hasBorder) return DEFAULT_BORDER_COLOR;
				return null;
			} else
				return (GamaColor) exp.getConstValue();
		};
		this.border = create(IKeyword.BORDER, (scope, exp) -> {
			if (exp.getGamlType() == Types.BOOL) {
				final boolean hasBorder = Cast.asBool(scope, exp.value(scope));
				if (hasBorder) return DEFAULT_BORDER_COLOR;
				return null;
			} else
				return (GamaColor) exp.value(scope);
		}, Types.COLOR, null, constBorderCaster);

		this.color = create(IKeyword.COLOR, (scope, exp) -> {
			switch (exp.getGamlType().id()) {
				case IType.COLOR:
					return Cast.asColor(scope, exp.value(scope));
				default:
					return null;
			}

		}, Types.COLOR, null, (e) -> {
			switch (e.getGamlType().id()) {
				case IType.COLOR:
					return Cast.asColor(null, e.getConstValue());
				default:
					return null;
			}
		});
		this.font = create(IKeyword.FONT, Types.FONT, GamaFontType.DEFAULT_DISPLAY_FONT.getValue());
		final Function<IExpression, IList> constTextureCaster = (exp) -> {
			if (exp.getGamlType().getGamlType() == Types.LIST)
				return GamaListType.staticCast(null, exp.getConstValue(), Types.STRING, false);
			else
				return GamaListFactory.wrap(Types.NO_TYPE, exp.getConstValue());
		};
		this.texture = create(IKeyword.TEXTURE, (scope, exp) -> {
			if (exp.getGamlType().getGamlType() == Types.LIST)
				return GamaListType.staticCast(scope, exp.value(scope), Types.STRING, false);
			else
				return GamaListFactory.wrap(Types.NO_TYPE, exp.value(scope));
		}, Types.LIST, null, constTextureCaster);
		this.material = create(IKeyword.MATERIAL, Types.MATERIAL, null);
		this.perspective = create(IKeyword.PERSPECTIVE, Types.BOOL, true);
		this.lineWidth = create(IKeyword.WIDTH, Types.FLOAT, GamaPreferences.Displays.CORE_LINE_WIDTH.getValue());

	}

	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public GamaPoint getLocation() {
		return location.get() == null ? null : location.get();
	}

	/**
	 * Gets the anchor.
	 *
	 * @return the anchor
	 */
	public GamaPoint getAnchor() {
		return anchor.get() == null ? null : anchor.get();
	}

}