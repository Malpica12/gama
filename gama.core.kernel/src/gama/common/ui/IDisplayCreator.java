/*******************************************************************************************************
 *
 * IDisplayCreator.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.common.ui;

import gama.common.interfaces.IGamlDescription;
import gama.common.interfaces.IGamlable;
import gama.common.interfaces.INamed;
import gama.outputs.IDisplayOutput;
import gama.outputs.display.NullDisplaySurface;

/**
 * The Interface IDisplayCreator.
 */
@FunctionalInterface
public interface IDisplayCreator {

	/**
	 * The Class DisplayDescription.
	 */
	public static class DisplayDescription implements IDisplayCreator, IGamlDescription {

		/** The original. */
		private final IDisplayCreator original;
		
		/** The plugin. */
		private final String name, plugin;

		/**
		 * Instantiates a new display description.
		 *
		 * @param original the original
		 * @param name the name
		 * @param plugin the plugin
		 */
		public DisplayDescription(final IDisplayCreator original, final String name, final String plugin) {
			this.original = original;
			this.name = name;
			this.plugin = plugin;
		}

		/**
		 * Method create()
		 * 
		 * @see gama.common.ui.IDisplayCreator#create(java.lang.Object[])
		 */
		@Override
		public IDisplaySurface create(final Object... args) {
			if (original != null) { return original.create(args); }
			return new NullDisplaySurface();
		}

		/**
		 * Creates the.
		 *
		 * @param output the output
		 * @param args the args
		 * @return the i display surface
		 */
		public IDisplaySurface create(final IDisplayOutput output, final Object... args) {
			final Object[] params = new Object[args.length + 1];
			params[0] = output;
			for (int i = 0; i < args.length; i++) {
				params[i + 1] = args[i];
			}
			return create(params);
		}

		/**
		 * Method getName()
		 * 
		 * @see gama.common.interfaces.INamed#getName()
		 */
		@Override
		public String getName() {
			return name;
		}

		/**
		 * Method setName()
		 * 
		 * @see gama.common.interfaces.INamed#setName(java.lang.String)
		 */
		@Override
		public void setName(final String newName) {}

		/**
		 * Method serialize()
		 * 
		 * @see gama.common.interfaces.IGamlable#serialize(boolean)
		 */
		@Override
		public String serialize(final boolean includingBuiltIn) {
			return getName();
		}

		/**
		 * Method getTitle()
		 * 
		 * @see gama.common.interfaces.IGamlDescription#getTitle()
		 */
		@Override
		public String getTitle() {
			return "Display supported by " + getName() + "";
		}

		/**
		 * Method getDocumentation()
		 * 
		 * @see gama.common.interfaces.IGamlDescription#getDocumentation()
		 */
		@Override
		public String getDocumentation() {
			return "";
		}

		/**
		 * Method getDefiningPlugin()
		 * 
		 * @see gama.common.interfaces.IGamlDescription#getDefiningPlugin()
		 */
		@Override
		public String getDefiningPlugin() {
			return plugin;
		}

		/**
		 * Method collectPlugins()
		 * 
		 * @see msi.gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
		 */
		// @Override
		// public void collectMetaInformation(final GamlProperties meta) {
		// meta.put(GamlProperties.PLUGINS, plugin);
		// }
	}

	/**
	 * Creates the.
	 *
	 * @param args the args
	 * @return the i display surface
	 */
	IDisplaySurface create(Object... args);

}