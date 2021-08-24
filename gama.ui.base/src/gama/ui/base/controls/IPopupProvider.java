/*********************************************************************************************
 *
 * 'IPopupProvider.java, in plugin gama.ui.base, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.base.controls;

import java.util.LinkedHashMap;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import gama.ui.base.resources.GamaColors.GamaUIColor;

/**
 * The class IPopupProvider.
 *
 * @author drogoul
 * @since 19 janv. 2012
 *
 */
public interface IPopupProvider {

	public static class PopupText extends LinkedHashMap<String, GamaUIColor> {

		public static PopupText with(final GamaUIColor color, final String text) {
			final PopupText p = new PopupText();
			p.add(color, text);
			return p;
		}

		public void add(final GamaUIColor color, final String text) {
			put(text, color);
		}

	}

	PopupText getPopupText();

	Shell getControllingShell();

	Point getAbsoluteOrigin();

	default int getPopupWidth() {
		return 0;
	}

}
