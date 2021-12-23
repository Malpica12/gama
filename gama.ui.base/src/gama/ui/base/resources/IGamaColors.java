/*******************************************************************************************************
 *
 * IGamaColors.java, in gama.ui.base, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.base.resources;

import static gama.ui.base.resources.GamaColors.get;
import static gama.ui.base.resources.GamaColors.system;
import static gama.ui.base.resources.GamaIcons.create;
import static gama.ui.base.utils.ThemeHelper.isDark;

import org.eclipse.swt.SWT;

import gama.ui.base.resources.GamaColors.GamaUIColor;

/**
 * Class IGamaColors.
 *
 * @author drogoul
 * @since 24 nov. 2014
 *
 */
public interface IGamaColors {

	/** The blue. */
	GamaUIColor BLUE = isDark() ? GamaColors.get(get(create("palette/palette.blue2")).lighter())
			: get(create("palette/palette.blue2"));
	
	/** The error. */
	GamaUIColor ERROR = isDark() ? GamaColors.get(get(create("palette/palette.red2")).lighter())
			: get(create("palette/palette.red2"));
	
	/** The ok. */
	GamaUIColor OK = isDark() ? GamaColors.get(get(create("palette/palette.green2")).lighter())
			: get(create("palette/palette.green2"));
	
	/** The warning. */
	GamaUIColor WARNING = get(create("palette/palette.orange2"));
	
	/** The neutral. */
	GamaUIColor NEUTRAL = get(create("palette/palette.gray2"));
	
	/** The tooltip. */
	GamaUIColor TOOLTIP = get(create("palette/palette.yellow2"));
	
	/** The gray label. */
	GamaUIColor GRAY_LABEL = get(136, 136, 136);
	
	/** The gray. */
	GamaUIColor GRAY = new GamaUIColor(system(SWT.COLOR_GRAY));
	
	/** The light gray. */
	GamaUIColor LIGHT_GRAY = get(200, 200, 200);
	
	/** The very light gray. */
	GamaUIColor VERY_LIGHT_GRAY = get(245, 245, 245);
	
	/** The dark gray. */
	GamaUIColor DARK_GRAY = get(100, 100, 100);
	
	/** The very dark gray. */
	GamaUIColor VERY_DARK_GRAY = get(50, 50, 50);
	
	/** The white. */
	GamaUIColor WHITE = new GamaUIColor(system(SWT.COLOR_WHITE), system(SWT.COLOR_WHITE));
	
	/** The black. */
	GamaUIColor BLACK = new GamaUIColor(system(SWT.COLOR_BLACK));
	
	/** The parameters background. */
	GamaUIColor PARAMETERS_BACKGROUND = isDark() ? get(120, 120, 120) : get(255, 255, 255);
	
	/** The dark orange. */
	GamaUIColor DARK_ORANGE = get(225, 92, 15);

}