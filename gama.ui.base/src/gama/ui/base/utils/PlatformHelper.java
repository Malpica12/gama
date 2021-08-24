/*********************************************************************************************
 *
 * 'PlatformHelper.java, in plugin gama.ui.base, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
/*******************************************************************************
 * Copyright (c) 2007-2008 SAS Institute Inc., ILOG S.A. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: SAS Institute Inc. - initial API and implementation ILOG S.A. - initial API and implementation IBM
 * Corporation - Java/SWT versioning code (from org.eclipse.swt.internal.Library)
 *******************************************************************************/
package gama.ui.base.utils;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.DPIUtil;

public class PlatformHelper {

	private static String platformString = SWT.getPlatform();
	private static boolean isWindows = "win32".equals(platformString);
	private static boolean isMac = "cocoa".equals(platformString) || "carbon".equals(platformString);
	private static boolean isLinux = "gtk".equals(platformString);
	private static boolean isHiDPI = DPIUtil.getDeviceZoom() > 100;

	private static Boolean isDeveloper;

	private PlatformHelper() {}

	public static boolean isHiDPI() {
		return isHiDPI;
	}

	public static boolean isWindows() {
		return isWindows;
	}

	public static boolean isLinux() {
		return isLinux;
	}

	public static boolean isMac() {
		return isMac;
	}

	public static boolean isDeveloper() { // NO_UCD (unused code)
		if (isDeveloper == null) {
			isDeveloper = Platform.getInstallLocation() == null
					|| Platform.getInstallLocation().getURL().getPath().contains("org.eclipse.pde.core");
		}
		return isDeveloper;
	}

	public static int getDeviceZoom() {
		return DPIUtil.getDeviceZoom();
	}

	/**
	 * Returns SWT auto scaled-up value {@code v}, compatible with {@link DPIUtil#autoScaleUp(int)}
	 * <p>
	 * We need to keep track of SWT's implementation in this regard!
	 * </p>
	 */
	public static int autoScaleUp(final int v) {
		// Temp !
		// if (true) return v;
		final int deviceZoom = DPIUtil.getDeviceZoom();
		if (100 == deviceZoom || DPIUtil.useCairoAutoScale()) return v;
		final float scaleFactor = deviceZoom / 100f;
		return Math.round(v * scaleFactor);
	}

	public static double autoScaleUp(final double v) {
		final int deviceZoom = DPIUtil.getDeviceZoom();
		if (100 == deviceZoom || DPIUtil.useCairoAutoScale()) return v;
		final double scaleFactor = deviceZoom / 100d;
		return v * scaleFactor;
	}

	/**
	 * Returns SWT auto scaled-down value {@code v}, compatible with {@link DPIUtil#autoScaleDown(int)}
	 * <p>
	 * We need to keep track of SWT's implementation in this regard!
	 * </p>
	 */
	public static int autoScaleDown(final int v) {
		// Temp !
		// if (true) return v;
		final int deviceZoom = DPIUtil.getDeviceZoom();
		if (100 == deviceZoom || DPIUtil.useCairoAutoScale()) return v;
		final float scaleFactor = deviceZoom / 100f;
		return Math.round(v / scaleFactor);
	}

	public static double autoScaleDown(final double v) {
		// Temp !
		// if (true) return v;
		final int deviceZoom = DPIUtil.getDeviceZoom();
		if (100 == deviceZoom || DPIUtil.useCairoAutoScale()) return v;
		final double scaleFactor = deviceZoom / 100d;
		return v / scaleFactor;
	}

	/**
	 * The JAVA version
	 */
	public static final int JAVA_VERSION; // NO_UCD (unused code)
	static {
		JAVA_VERSION = parseVersion(System.getProperty("java.version")); //$NON-NLS-1$
	}

	static int parseVersion(final String version) {
		if (version == null) return 0;
		int major = 0, minor = 0, micro = 0;
		final int length = version.length();
		int index = 0, start = 0;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) { major = Integer.parseInt(version.substring(start, index)); }
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) { minor = Integer.parseInt(version.substring(start, index)); }
		} catch (final NumberFormatException e) {}
		start = ++index;
		while (index < length && Character.isDigit(version.charAt(index))) {
			index++;
		}
		try {
			if (start < length) { micro = Integer.parseInt(version.substring(start, index)); }
		} catch (final NumberFormatException e) {}
		return javaVersion(major, minor, micro);
	}

	/**
	 * Returns the Java version number as an integer.
	 *
	 * @param major
	 * @param minor
	 * @param micro
	 * @return the version
	 */
	public static int javaVersion(final int major, final int minor, final int micro) {
		return (major << 16) + (minor << 8) + micro;
	}

}