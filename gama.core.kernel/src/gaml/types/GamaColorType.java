/*******************************************************************************************************
 *
 * GamaColorType.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.types;

import java.awt.Color;
import java.util.List;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.type;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaColor;
import gama.util.IContainer;
import gaml.operators.Cast;

/**
 * Written by drogoul Modified on 1 ao�t 2010.
 *
 * @todo Description
 */
@type (
		name = IKeyword.RGB,
		id = IType.COLOR,
		wraps = { GamaColor.class, Color.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE, IConcept.COLOR },
		doc = @doc ("The type rgb represents colors in GAML, with their three red, green, blue components and, optionally, a fourth alpha component "))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaColorType extends GamaType<GamaColor> {

	@Override
	public GamaColor cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	/**
	 * Static cast.
	 *
	 * @param scope the scope
	 * @param obj the obj
	 * @param param the param
	 * @param copy the copy
	 * @return the gama color
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public static GamaColor staticCast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		// param can contain the alpha value
		if (obj == null) { return null; }
		if (obj instanceof GamaColor) {
			final GamaColor col = (GamaColor) obj;
			if (param instanceof Integer) {
				return new GamaColor(col.getRed(), col.getGreen(), col.getBlue(), (Integer) param);
			} else if (param instanceof Double) {
				return new GamaColor(col.getRed(), col.getGreen(), col.getBlue(), (Double) param);
			} else {
				return (GamaColor) obj;
			}
		}
		if (obj instanceof List) {
			final List l = (List) obj;
			final int size = l.size();
			if (size == 0) { return new GamaColor(Color.black); }
			if (size == 1 || size == 2) {
				return staticCast(scope, ((List) obj).get(0), param, copy);
			} else if (size == 3) {
				return new GamaColor(Cast.asInt(scope, l.get(0)), Cast.asInt(scope, l.get(1)),
						Cast.asInt(scope, l.get(2)), 255);
			} else if (size >= 4) { return new GamaColor(Cast.asInt(scope, l.get(0)), Cast.asInt(scope, l.get(1)),
					Cast.asInt(scope, l.get(2)), Cast.asInt(scope, l.get(3))); }
			/* To allow constructions like rgb [255,255,255] */
		}
		if (obj instanceof IContainer) { return staticCast(scope,
				((IContainer) obj).listValue(scope, Types.NO_TYPE, false), param, copy); }
		if (obj instanceof String) {
			final String s = ((String) obj).toLowerCase();
			GamaColor c = GamaColor.colors.get(s);
			if (c == null) {
				try {
					c = new GamaColor(Color.decode(s));
				} catch (final NumberFormatException e) {
					final GamaRuntimeException ex =
							GamaRuntimeException.error("'" + s + "' is not a valid color name", scope);
					throw ex;
				}
				GamaColor.colors.put(s, c);
			}
			if (param == null) {
				return c;
			} else if (param instanceof Integer) {
				return new GamaColor(c, (Integer) param);
			} else if (param instanceof Double) { return new GamaColor(c, (Double) param); }
		}
		if (obj instanceof Boolean) { return (Boolean) obj ? new GamaColor(Color.black) : new GamaColor(Color.white); }
		final int i = Cast.asInt(scope, obj);
		final GamaColor gc = GamaColor.getInt((255 & 0xFF) << 24 | i & 0xFFFFFF << 0);
		if (param instanceof Integer) {
			return new GamaColor(gc, (Integer) param);
		} else if (param instanceof Double) { return new GamaColor(gc, (Double) param); }
		return gc;
	}

	@Override
	public GamaColor getDefault() {
		return null; // new GamaColor(Color.black);
	}

	@Override
	public IType getContentType() {
		return Types.get(INT);
	}

	@Override
	public IType getKeyType() {
		return Types.get(INT);
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	public boolean isCompoundType() {
		return true;
	}

}
