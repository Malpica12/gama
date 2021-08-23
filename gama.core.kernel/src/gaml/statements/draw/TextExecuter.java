/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.TextExecuter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.statements.draw;

import java.awt.geom.Rectangle2D;

import gama.common.geometry.Scaling3D;
import gama.common.ui.IGraphics;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.expressions.IExpression;
import gaml.operators.Cast;

class TextExecuter extends DrawExecuter {

	private final String constText;

	TextExecuter(final IExpression item) throws GamaRuntimeException {
		super(item);
		constText = item.isConst() ? Cast.asString(null, item.getConstValue()) : null;
	}

	@Override
	Rectangle2D executeOn(final IScope scope, final IGraphics g, final DrawingData data) throws GamaRuntimeException {
		final String info = constText == null ? Cast.asString(scope, item.value(scope)) : constText;
		if (info == null || info.length() == 0) return null;
		final TextDrawingAttributes attributes = computeAttributes(scope, data);
		return g.drawString(info, attributes);
	}

	TextDrawingAttributes computeAttributes(final IScope scope, final DrawingData data) {
		final TextDrawingAttributes attributes = new TextDrawingAttributes(Scaling3D.of(data.size.get()),
				data.rotation.get(), data.getLocation(), data.color.get());
		// We push the location of the agent if none has been provided
		if (attributes.getLocation() == null) {
			attributes.setLocation(scope.getAgent().getLocation().clone());
		}
		attributes.setFont(data.font.get());
		attributes.setAnchor(data.getAnchor());
		attributes.setBorder(data.border.get());
		attributes.setEmpty(data.empty.get());
		attributes.setHeight(data.depth.get());
		attributes.setPerspective(data.perspective.get());
		attributes.setTextures(data.texture.get());
		attributes.setLineWidth(data.lineWidth.get());
		attributes.setPrecision(data.precision.get());
		return attributes;
	}
}