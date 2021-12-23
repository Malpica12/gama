/*******************************************************************************************************
 *
 * ThemeHelper.java, in gama.ui.base, is part of the source code of the GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.base.utils;

import static gama.common.preferences.GamaPreferences.create;
import static gama.common.preferences.GamaPreferences.Interface.APPEARANCE;
import static gama.common.preferences.GamaPreferences.Interface.NAME;
import static org.eclipse.swt.widgets.Display.isSystemDarkTheme;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.css.core.dom.ExtendedDocumentCSS;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.internal.theme.ThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.IThemeManager;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.stylesheets.StyleSheetList;

import gama.common.preferences.Pref;
import gama.core.dev.utils.DEBUG;
import gaml.types.IType;

// TODO: Auto-generated Javadoc
/**
 * The Class ThemeHelper.
 */
public class ThemeHelper {

	/** The Constant E4_DARK_THEME_ID. */
	public static final String E4_DARK_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_dark";

	/** The Constant E4_LIGHT_THEME_ID. */
	public static final String E4_LIGHT_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_default";

	/** The Constant E4_CLASSIC_THEME_ID. */
	public static final String E4_CLASSIC_THEME_ID = "org.eclipse.e4.ui.css.theme.e4_classic";

	/** The Constant THEME_ID_PREFERENCE. */
	public static final String THEME_ID_PREFERENCE = "themeid";

	/** The Constant THEME_ID. */
	public static final String THEME_ID = "cssTheme";

	/** The Constant THEME_FOLLOW_PROPERTY. */
	public static final String THEME_FOLLOW_PROPERTY = "org.eclipse.swt.display.useSystemTheme";

	/** The Constant ENABLED_THEME_KEY. */
	public static final String ENABLED_THEME_KEY = "themeEnabled";

	/** The Constant SWT_PREFERENCES. */
	public static final String SWT_PREFERENCES = "org.eclipse.e4.ui.workbench.renderers.swt";

	/** The Constant listeners. */
	private static final List<IThemeListener> listeners = new ArrayList<>();

	/** The engine. */
	private static IThemeEngine engine;

	/** The bundle. */
	private static Bundle bundle = Platform.getBundle("gama.ui.base");

	static {
		DEBUG.ON();
	}

	/** The Constant CORE_THEME_FOLLOW. */
	public static final Pref<Boolean> CORE_THEME_FOLLOW =
			create("pref_theme_follow", "Follow OS theme", followOSTheme(), IType.BOOL, false).in(NAME, APPEARANCE)
					.restartRequired().deactivates("pref_theme_light").onChange(yes -> {
						followOSTheme(yes);
						chooseThemeBasedOnPreferences();
					});

	/** The Constant CORE_THEME_LIGHT. */
	public static final Pref<Boolean> CORE_THEME_LIGHT =
			create("pref_theme_light", "Light theme", true, IType.BOOL, false).in(NAME, APPEARANCE).restartRequired()
					.onChange(v -> {
						chooseThemeBasedOnPreferences();
					});

	/**
	 * Chooses a light/dark theme based on the GAMA preferences and the actual theme.
	 *
	 * @return whether a change has been made
	 */
	private static boolean chooseThemeBasedOnPreferences() {
		return CORE_THEME_FOLLOW.getValue() && changeTo(!isSystemDarkTheme()) || changeTo(CORE_THEME_LIGHT.getValue());
	}

	/**
	 * Gets the context.
	 *
	 * @return the context
	 */
	private static IEclipseContext getContext() {
		return PlatformUI.getWorkbench().getService(IEclipseContext.class);
		// return ((Workbench) PlatformUI.getWorkbench()).getContext();
	}

	/**
	 * Follow OS theme.
	 *
	 * @return the boolean
	 */
	private static Boolean followOSTheme() {
		final var prefs = getSwtRendererPreferences();
		final var val = prefs.get(THEME_FOLLOW_PROPERTY, null);
		Boolean result;
		if (val != null) {
			result = Boolean.valueOf(val);
		} else {
			result = Boolean.valueOf(System.getProperty(THEME_FOLLOW_PROPERTY, "true"));
		}
		DEBUG.OUT("Follow OS Theme: " + result);
		return result;
	}

	/**
	 * Follow OS theme.
	 *
	 * @param follow
	 *            the follow
	 */
	private static void followOSTheme(final Boolean follow) {
		Display.getDefault().setData(THEME_FOLLOW_PROPERTY, follow);
		System.setProperty(THEME_FOLLOW_PROPERTY, follow.toString());
		// We create a new preference
		getSwtRendererPreferences().putBoolean(THEME_FOLLOW_PROPERTY, follow);
		try {
			getSwtRendererPreferences().flush();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if is dark.
	 *
	 * @return true, if is dark
	 */
	public static boolean isDark() {
		// DEBUG.OUT("Asks for isDark(): ", false);
		String id;
		final var themeEngine = getContext().get(IThemeEngine.class);
		if (themeEngine == null) {
			id = (String) getContext().get(THEME_ID);
			if (id == null) {
				// Still no trace of a theme, let's look at preferences
				final var prefs = getThemeEclipsePreferences();
				id = prefs.get(THEME_ID_PREFERENCE, null);
			}

		} else {
			final var theme = themeEngine.getActiveTheme();
			id = theme == null ? null : theme.getId();
		}
		// DEBUG.OUT(" " + (id != null && id.contains("dark")) + " and OS is dark = " + isSystemDarkTheme());
		return id != null && id.contains("dark");
	}

	/**
	 * Install.
	 */
	public static void install() {
		// if ( !PlatformUI.isWorkbenchRunning() ) { return; }
		// We transfer the preference to the system property (to be read by Eclipse)
		System.setProperty(THEME_FOLLOW_PROPERTY, followOSTheme().toString());
		final var eventBroker = PlatformUI.getWorkbench().getService(IEventBroker.class);
		if (eventBroker != null) {
			final var themeChangedHandler = new WorkbenchThemeChangedHandler();
			eventBroker.subscribe(UIEvents.UILifeCycle.THEME_CHANGED, themeChangedHandler);
			eventBroker.subscribe(IThemeEngine.Events.THEME_CHANGED, themeChangedHandler);
		}
		chooseThemeBasedOnPreferences();
	}

	/**
	 * Gets the theme eclipse preferences.
	 *
	 * @return the theme eclipse preferences
	 */
	private static IEclipsePreferences getThemeEclipsePreferences() {
		return InstanceScope.INSTANCE.getNode("org.eclipse.e4.ui.css.swt.theme");
	}

	/**
	 * Gets the swt renderer preferences.
	 *
	 * @return the swt renderer preferences
	 */
	private static IEclipsePreferences getSwtRendererPreferences() {
		return InstanceScope.INSTANCE.getNode("org.eclipse.e4.ui.workbench.renderers.swt"); //$NON-NLS-1$
	}

	/**
	 * Changes to a light or dark theme depending on the value of the argument.
	 *
	 * @param light
	 *            whether to choose a light (true) or dark (false) theme
	 * @return whether a change has been necessary
	 */
	private static boolean changeTo(final boolean light) {
		// OS.setTheme(!light);
		return changeTo(light ? E4_LIGHT_THEME_ID : E4_DARK_THEME_ID);
	}

	/**
	 * Changes the current theme in both the theme engine and the preferences (so that they can stick).
	 *
	 * @param id
	 *            the identifier of the theme
	 * @return true, if successful
	 */
	private static boolean changeTo(final String id) {
		// even early in the cycle
		getContext().set(THEME_ID, id);
		getThemeEclipsePreferences().put(THEME_ID_PREFERENCE, id);
		try {
			getThemeEclipsePreferences().flush();
		} catch (final BackingStoreException e) {
			e.printStackTrace();
		}
		final var theme = getEngine().getActiveTheme();
		if (theme != null && theme.getId().startsWith(id)) return false;
		getEngine().setTheme(id, true);
		return true;
	}

	/**
	 * Adds the listener.
	 *
	 * @param l
	 *            the l
	 */
	public static void addListener(final IThemeListener l) {
		if (!listeners.contains(l)) { listeners.add(l); }
	}

	/**
	 * Removes the listener.
	 *
	 * @param l
	 *            the l
	 */
	public static void removeListener(final IThemeListener l) {
		listeners.remove(l);
	}

	/**
	 * Gets the engine.
	 *
	 * @return the engine
	 */
	public static IThemeEngine getEngine() {
		if (engine == null) { engine = getThemeEngine(); }
		return engine;
	}

	/**
	 * Gets the theme engine.
	 *
	 * @return the theme engine
	 */
	private static ThemeEngine getThemeEngine() {
		BundleContext context = bundle.getBundleContext();
		ServiceReference ref = context.getServiceReference(IThemeManager.class.getName());
		IThemeManager manager = (IThemeManager) context.getService(ref);
		return (ThemeEngine) manager.getEngineForDisplay(PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null
				? Display.getCurrent() : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay());
	}

	/**
	 * Inject CSS.
	 *
	 * @param cssText
	 *            the css text
	 */
	public static void injectCSS(final String cssText) {
		StringBuilder sb = new StringBuilder();
		// FIXME: expose these new protocols: resetCurrentTheme() and
		// getCSSEngines()
		getThemeEngine().resetCurrentTheme();

		int count = 0;
		for (CSSEngine engine : getThemeEngine().getCSSEngines()) {
			if (count++ > 0) { sb.append("\n\n"); }
			sb.append("Engine[").append(engine.getClass().getSimpleName()).append("]");
			ExtendedDocumentCSS doc = (ExtendedDocumentCSS) engine.getDocumentCSS();
			List<StyleSheet> sheets = new ArrayList<>();
			StyleSheetList list = doc.getStyleSheets();
			for (int i = 0; i < list.getLength(); i++) {
				sheets.add(list.item(i));
			}

			try {
				Reader reader = new StringReader(cssText);
				sheets.add(0, engine.parseStyleSheet(reader));
				doc.removeAllStyleSheets();
				for (StyleSheet sheet : sheets) {
					doc.addStyleSheet(sheet);
				}
				engine.reapply();

			} catch (CSSParseException e) {
				sb.append("\nError: line ").append(e.getLineNumber()).append(" col ").append(e.getColumnNumber())
						.append(": ").append(e.getLocalizedMessage());
			} catch (IOException e) {
				sb.append("\nError: ").append(e.getLocalizedMessage());
			}
		}
	}

	/**
	 * The Class WorkbenchThemeChangedHandler.
	 */
	public static class WorkbenchThemeChangedHandler implements EventHandler {

		/**
		 * Handle event.
		 *
		 * @param event
		 *            the event
		 */
		@Override
		public void handleEvent(final Event event) {
			final var theme = getTheme(event);
			// System.out.println("PROPERTY " + THEME_FOLLOW_PROPERTY + " = " +
			// System.getProperty(THEME_FOLLOW_PROPERTY));
			// System.out.println("THEME = " + theme);
			if (theme == null) return;
			final var isDark = theme.getId().startsWith(E4_DARK_THEME_ID);
			listeners.forEach(l -> l.themeChanged(!isDark));
		}

		/**
		 * Gets the theme.
		 *
		 * @param event
		 *            the event
		 * @return the theme
		 */
		protected ITheme getTheme(final Event event) {
			var theme = (ITheme) event.getProperty(IThemeEngine.Events.THEME);
			if (theme == null) { theme = getEngine().getActiveTheme(); }
			return theme;
		}
	}

	/**
	 * The listener interface for receiving ITheme events. The class that is interested in processing a ITheme event
	 * implements this interface, and the object created with that class is registered with a component using the
	 * component's <code>addIThemeListener<code> method. When the ITheme event occurs, that object's appropriate method
	 * is invoked.
	 *
	 * @see IThemeEvent
	 */
	public interface IThemeListener {

		/**
		 * Theme changed.
		 *
		 * @param light
		 *            the light
		 */
		void themeChanged(boolean light);
	}

}
