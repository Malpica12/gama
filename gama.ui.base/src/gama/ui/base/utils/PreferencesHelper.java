/*******************************************************************************************************
 *
 * PreferencesHelper.java, in gama.ui.base, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.base.utils;

import static gama.common.preferences.GamaPreferences.create;
import static gama.common.preferences.GamaPreferences.Interface.APPEARANCE;
import static gama.common.preferences.GamaPreferences.Interface.MENUS;
import static gama.common.preferences.GamaPreferences.Interface.NAME;
import static gama.common.ui.IGui.NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID;
import static gama.ui.base.menus.GamaColorMenu.SORT_NAMES;
import static gama.ui.base.menus.GamaColorMenu.byBrightness;
import static gama.ui.base.menus.GamaColorMenu.byLuminescence;
import static gama.ui.base.menus.GamaColorMenu.byName;
import static gama.ui.base.menus.GamaColorMenu.byRGB;
import static gama.ui.base.menus.GamaColorMenu.colorComp;
import static gama.ui.base.resources.GamaColors.toGamaColor;
import static gama.ui.base.resources.IGamaColors.WARNING;
import static gama.util.GamaColor.getNamed;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

import gama.common.preferences.GamaPreferences;
import gama.common.preferences.Pref;
import gama.core.dev.utils.DEBUG;
import gama.runtime.MemoryUtils;
import gama.ui.base.menus.GamaColorMenu;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.views.GamaPreferencesView;
import gama.util.GamaColor;
import gaml.types.IType;

/**
 * The Class PreferencesHelper.
 */
public class PreferencesHelper {

	/** The Constant CORE_EDITORS_HIGHLIGHT. */
	public static final Pref<Boolean> CORE_EDITORS_HIGHLIGHT =
			create("pref_editors_highligth", "Highlight in yellow the title of value editors when they change", true,
					IType.BOOL, true).in(NAME, APPEARANCE);

	/** The Constant SHAPEFILE_VIEWER_FILL. */
	public static final Pref<GamaColor> SHAPEFILE_VIEWER_FILL = create("pref_shapefile_background_color",
			"Shapefile viewer fill color", () -> getNamed("lightgray"), IType.COLOR, false).in(NAME, APPEARANCE);

	/** The Constant SHAPEFILE_VIEWER_LINE_COLOR. */
	public static final Pref<GamaColor> SHAPEFILE_VIEWER_LINE_COLOR = create("pref_shapefile_line_color",
			"Shapefile viewer line color", () -> getNamed("black"), IType.COLOR, false).in(NAME, APPEARANCE);

	/** The Constant ERROR_TEXT_COLOR. */
	public static final Pref<GamaColor> ERROR_TEXT_COLOR =
			create("pref_error_text_color", "Text color of errors", () -> toGamaColor(IGamaColors.ERROR.inactive()),
					IType.COLOR, true).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.ERRORS);

	/** The Constant WARNING_TEXT_COLOR. */
	public static final Pref<GamaColor> WARNING_TEXT_COLOR =
			create("pref_warning_text_color", "Text color of warnings", () -> toGamaColor(WARNING.inactive()),
					IType.COLOR, true).in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.ERRORS);

	/** The Constant IMAGE_VIEWER_BACKGROUND. */
	public static final Pref<GamaColor> IMAGE_VIEWER_BACKGROUND =
			create("pref_image_background_color", "Image viewer background color", () -> GamaColor.getNamed("white"),
					IType.COLOR, false).in(NAME, APPEARANCE);

	// public static final Pref<GamaFont> BASE_BUTTON_FONT = create("pref_button_font", "Font of buttons and dialogs",
	// () -> new GamaFont(getBaseFont(), SWT.BOLD, baseSize), IType.FONT, false).in(NAME, APPEARANCE)
	// .onChange(GamaFonts::setLabelFont);

	/** The color menu sort. */
	public static Pref<String> COLOR_MENU_SORT =
			create("pref_menu_colors_sort", "Sort colors menu by", "RGB value", IType.STRING, false).among(SORT_NAMES)
					.activates("menu.colors.reverse", "menu.colors.group").in(NAME, MENUS).onChange(pref -> {
						if (pref.equals(SORT_NAMES[0])) {
							colorComp = byRGB;
						} else if (pref.equals(SORT_NAMES[1])) {
							colorComp = byName;
						} else if (pref.equals(SORT_NAMES[2])) {
							colorComp = byBrightness;
						} else {
							colorComp = byLuminescence;
						}
					});
	
	/** The color menu reverse. */
	public static Pref<Boolean> COLOR_MENU_REVERSE =
			create("pref_menu_colors_reverse", "Reverse order", false, IType.BOOL, false).in(NAME, MENUS)
					.onChange(pref -> GamaColorMenu.setReverse(pref ? -1 : 1));
	
	/** The color menu group. */
	public static Pref<Boolean> COLOR_MENU_GROUP =
			create("pref_menu_colors_group", "Group colors", false, IType.BOOL, false).in(NAME, MENUS)
					.onChange(pref -> GamaColorMenu.breakdown = pref);
	
	/** The Constant NAVIGATOR_METADATA. */
	public static final Pref<Boolean> NAVIGATOR_METADATA =
			create("pref_navigator_display_metadata", "Display metadata in navigator", true, IType.BOOL, false)
					.in(NAME, APPEARANCE).onChange(newValue -> {
						final var mgr = getWorkbench().getDecoratorManager();
						try {
							mgr.setEnabled(NAVIGATOR_LIGHTWEIGHT_DECORATOR_ID, newValue);
						} catch (final CoreException e) {
							e.printStackTrace();
						}

					});

	/**
	 * Find ini file.
	 *
	 * @return the file
	 */
	public static File findIniFile() {
		final var path = Platform.getConfigurationLocation().getURL().getPath();
		DEBUG.OUT("Install location of GAMA is " + path);
		var dir = new File(path);
		var result = findIn(dir);
		if (result == null) {
			if (PlatformHelper.isMac()) {
				dir = new File(path + "Gama.app/Contents/MacOS");
				result = findIn(dir);
				if (result == null) {
					dir = new File(path + "Gama.app/Eclipse");
					result = findIn(dir);
				}
			} else {
				dir = dir.getParentFile();
				result = findIn(dir);
			}
		}
		return result;
	}

	/**
	 * Find in.
	 *
	 * @param path the path
	 * @return the file
	 */
	private static File findIn(final File path) {
		DEBUG.OUT("Looking for ini file in " + path);
		final var ini = new File(path.getAbsolutePath() + "/Gama.ini");
		return ini.exists() ? ini : null;
	}

	/**
	 * Initialize.
	 */
	public static void initialize() {
		final var ini = findIniFile();
		final var memory = readMaxMemoryInMegabytes(ini);
		final var text = ini == null || memory == 0
				? "The max. memory allocated needs to be set in Eclipse (developer version) or in Gama.ini file"
				: "Maximum memory allocated in Mb (requires to restart GAMA)";
		final var p = GamaPreferences
				.create("pref_memory_max", text, memory == 0 ? (int) MemoryUtils.availableMemory() : memory, 1, false)
				.in(GamaPreferences.Runtime.NAME, GamaPreferences.Runtime.MEMORY);
		if (memory == 0) { p.disabled(); }
		p.onChange(newValue -> {
			changeMaxMemory(ini, newValue);
			GamaPreferencesView.setRestartRequired();
		});

	}

	/**
	 * Read max memory in megabytes.
	 *
	 * @param ini the ini
	 * @return the int
	 */
	public static int readMaxMemoryInMegabytes(final File ini) {
		try {
			if (ini != null) {
				try (final var stream = new FileInputStream(ini);
						final var reader = new BufferedReader(new InputStreamReader(stream));) {
					var s = reader.readLine();
					while (s != null) {
						if (s.startsWith("-Xmx")) {
							final var last = s.charAt(s.length() - 1);
							var divider = 1000000D;
							var unit = false;
							switch (last) {
								case 'k':
								case 'K':
									unit = true;
									divider = 1000;
									break;
								case 'm':
								case 'M':
									unit = true;
									divider = 1;
									break;
								case 'g':
								case 'G':
									unit = true;
									divider = 0.001;
									break;
							}
							var trim = s;
							trim = trim.replace("-Xmx", "");
							if (unit) { trim = trim.substring(0, trim.length() - 1); }
							final var result = Integer.parseInt(trim);
							return (int) (result / divider);

						}
						s = reader.readLine();
					}
				}
			}
		} catch (final IOException e) {}
		return 0;

	}

	/**
	 * Change max memory.
	 *
	 * @param ini the ini
	 * @param memory the memory
	 */
	public static void changeMaxMemory(final File ini, final int memory) {
		final var mem = memory < 128 ? 128 : memory;
		try {
			final List<String> contents = new ArrayList<>();
			if (ini != null) {
				try (final var stream = new FileInputStream(ini);
						final var reader = new BufferedReader(new InputStreamReader(stream));) {
					var s = reader.readLine();
					while (s != null) {
						if (s.startsWith("-Xmx")) { s = "-Xmx" + mem + "m"; }
						contents.add(s);
						s = reader.readLine();
					}
				}
				try (final var os = new FileOutputStream(ini);
						final var writer = new BufferedWriter(new OutputStreamWriter(os));) {
					for (final String line : contents) {
						writer.write(line);
						writer.newLine();
					}
					writer.flush();
				}
			}
		} catch (final IOException e) {}

	}

}
