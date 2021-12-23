/*******************************************************************************************************
 *
 * MemoryUtils.java, in gama.core.kernel, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.runtime;

import static org.eclipse.core.runtime.Platform.getConfigurationLocation;

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

import gama.common.preferences.GamaPreferences;

// TODO: Auto-generated Javadoc
/**
 * All-purpose static-method container class.
 *
 * @author Sebastiano Vigna
 * @since 0.1
 */

public final class MemoryUtils {

	/**
	 * Instantiates a new memory utils.
	 */
	private MemoryUtils() {}

	/** A static reference to {@link Runtime#getRuntime()}. */
	public final static Runtime RUNTIME = Runtime.getRuntime();

	/**
	 * Returns true if less then a percentage of the available memory is free.
	 *
	 * @return true, if successful
	 */
	public static boolean memoryIsLow() {
		return availableMemory() * 10e8 < RUNTIME.totalMemory()
				* GamaPreferences.Runtime.CORE_MEMORY_PERCENTAGE.getValue();
	}

	/**
	 * Returns the amount of available memory (free memory plus never allocated memory).
	 *
	 * @return the amount of available memory, in megabytes.
	 */
	public static int availableMemory() {
		long bytes = RUNTIME.freeMemory() + RUNTIME.maxMemory() - RUNTIME.totalMemory();
		double result = bytes / 10e6;
		return (int) result;
	}

	/**
	 * Returns the amount of available memory (free memory plus never allocated memory).
	 *
	 * @return the amount of available memory, in megabytes.
	 */
	public static int maxMemory() {
		long bytes = RUNTIME.maxMemory();
		double result = bytes / 10e6;
		return (int) result;
	}

	/**
	 * Read max memory in megabytes.
	 *
	 * @param ini
	 *            the ini
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
	 * @param ini
	 *            the ini
	 * @param memory
	 *            the memory
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

	/**
	 * Find ini file.
	 *
	 * @return the file
	 */
	public static File findIniFile() {
		return findIt(new File(getConfigurationLocation().getURL().getPath()));
	}

	/**
	 * Find it.
	 *
	 * @param rootDir
	 *            the root dir
	 * @return the file
	 */
	public static File findIt(final File rootDir) {
		File[] files = rootDir.listFiles();
		List<File> directories = new ArrayList<>(files.length);
		for (File file : files) {
			if ("Gama.ini".equals(file.getName())) return file;
			if (file.isDirectory()) { directories.add(file); }
		}
		for (File directory : directories) {
			File file = findIt(directory);
			if (file != null) return file;
		}
		return null;
	}

}
