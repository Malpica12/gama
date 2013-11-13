/**
 * Created by drogoul, 24 janv. 2012
 * 
 */
package msi.gaml.compilation;

import gnu.trove.set.hash.THashSet;
import java.util.Set;
import msi.gama.common.util.GuiUtils;
import msi.gaml.types.Types;
import org.eclipse.core.runtime.*;

/**
 * The class GamaBundleLoader.
 * 
 * @author drogoul
 * @since 24 janv. 2012
 * 
 */
public class GamaBundleLoader {

	public static String CORE_PLUGIN = "msi.gama.core";
	public static String ADDITIONS = "gaml.additions.GamlAdditions";
	public static String EXTENSION = "gaml.grammar.addition";
	private static Set<String> plugins = new THashSet();

	public static void preBuildContributions() {
		final long start = System.currentTimeMillis();
		for ( IConfigurationElement e : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION) ) {
			plugins.add(e.getContributor().getName());
		}
		plugins.remove(CORE_PLUGIN);
		preBuild(CORE_PLUGIN);
		for ( String addition : plugins ) {
			preBuild(addition);
		}
		// CRUCIAL INITIALIZATIONS
		AbstractGamlAdditions.buildMetaModel();
		Types.init();
		GuiUtils.debug(">> GAMA total load time " + (System.currentTimeMillis() - start) + " ms.");
	}

	public static void preBuild(final String s) {
		final long start = System.currentTimeMillis();
		Class<IGamlAdditions> gamlAdditions = null;
		try {
			gamlAdditions = (Class<IGamlAdditions>) Platform.getBundle(s).loadClass(ADDITIONS);
		} catch (ClassNotFoundException e1) {
			GuiUtils.debug(">> Impossible to load additions from " + s);
			return;
		}
		IGamlAdditions add = null;
		try {
			add = gamlAdditions.newInstance();
		} catch (InstantiationException e) {
			GuiUtils.debug(">> Impossible to instantiate additions from " + s);
			return;
		} catch (IllegalAccessException e) {
			GuiUtils.debug(">> Impossible to access additions from " + s);
			return;
		}
		add.initialize();
		GuiUtils.debug(">> GAMA bundle loaded in " + (System.currentTimeMillis() - start) + "ms: \t" + s);

	}

}
