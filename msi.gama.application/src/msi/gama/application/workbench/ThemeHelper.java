/*******************************************************************************************************
 *
 * ThemeHelper.java, in msi.gama.application, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application.workbench;

import static msi.gama.common.preferences.GamaPreferences.create;
import static msi.gama.common.preferences.GamaPreferences.Interface.APPEARANCE;
import static msi.gama.common.preferences.GamaPreferences.Interface.NAME;
import static org.eclipse.swt.widgets.Display.isSystemDarkTheme;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.css.swt.internal.theme.ThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.Workbench;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.event.EventHandler;
import org.osgi.service.prefs.BackingStoreException;

import msi.gama.common.preferences.Pref;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;

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
					.onChange(v -> { chooseThemeBasedOnPreferences(); });

	/**
	 * Chooses a light/dark theme based on the GAMA preferences and the actual theme
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
	private static IEclipseContext getContext() { return Workbench.getInstance().getContext(); }

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
		final var eventBroker = Workbench.getInstance().getService(IEventBroker.class);
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
		return InstanceScope.INSTANCE.getNode(FrameworkUtil.getBundle(ThemeEngine.class).getSymbolicName());
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
	 * Changes to a light or dark theme depending on the value of the argument
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
	 * Changes the current theme in both the theme engine and the preferences (so that they can stick)
	 *
	 * @param id
	 *            the identifier of the theme
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
		final var themeEngine = getContext().get(IThemeEngine.class);
		if (themeEngine == null) return true;
		final var theme = themeEngine.getActiveTheme();
		if (theme != null && theme.getId().startsWith(id)) return false;
		themeEngine.setTheme(id, true);
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
	 * The Class WorkbenchThemeChangedHandler.
	 */
	public static class WorkbenchThemeChangedHandler implements EventHandler {

		@Override
		public void handleEvent(final org.osgi.service.event.Event event) {
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
		protected ITheme getTheme(final org.osgi.service.event.Event event) {
			var theme = (ITheme) event.getProperty(IThemeEngine.Events.THEME);
			if (theme == null) {
				final var themeEngine = getContext().get(IThemeEngine.class);
				theme = themeEngine != null ? themeEngine.getActiveTheme() : null;
			}
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