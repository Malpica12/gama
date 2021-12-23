/*******************************************************************************************************
 *
 * PluginsModelsFolder.java, in gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.navigator.contents;

import gama.ui.base.utils.WorkbenchHelper;

/**
 * The Class PluginsModelsFolder.
 */
public class PluginsModelsFolder extends TopLevelFolder {

	/**
	 * Instantiates a new plugins models folder.
	 *
	 * @param root the root
	 * @param name the name
	 */
	public PluginsModelsFolder(final NavigatorRoot root, final String name) {
		super(root, name, FOLDER_PLUGIN, "navigator/folder.status.plugin", "Models present in GAMA plugins", WARNING,
				WorkbenchHelper.PLUGIN_NATURE, Location.Plugins);
	}

}