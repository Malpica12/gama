/*********************************************************************************************
 *
 * 'GamaColors.java, in plugin gama.ui.base, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.base.resources;

import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import gama.ui.base.utils.WorkbenchHelper;
import gama.util.GamaColor;

/**
 * Class GamaIcons.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
public class GamaColors {

	public static class GamaUIColor {

		Color active, inactive, darker, gray, lighter, reverse;

		public GamaUIColor(final Color c) {
			active = c;
		}

		public GamaUIColor validate() {
			return this;
		}

		@Override
		public String toString() {
			return active.getRed() + ", " + active.getGreen() + ", " + active.getBlue();
		}

		public boolean isDark() {
			return GamaColors.isDark(active);
		}

		public GamaUIColor(final Color c, final Color i) {
			active = c;
			inactive = i;
		}

		public Color color() {
			return active;
		}

		public Color inactive() {
			if (inactive == null) {
				inactive = computeInactive(active);
			}
			return inactive;
		}

		public Color darker() {
			if (darker == null) {
				darker = computeDarker(active);
			}
			return darker;
		}

		public Color lighter() {
			if (lighter == null) {
				lighter = computeLighter(active);
			}
			return lighter;
		}

		public RGB getRGB() {
			return active.getRGB();
		}
	}

	static HashMap<RGB, GamaUIColor> colors = new HashMap<>();

	static Color computeInactive(final Color c) {
		final var data = c.getRGB();
		final var hsb = data.getHSB();
		final var newHsb = new float[3];
		newHsb[0] = hsb[0];
		newHsb[1] = hsb[1] / 2;
		newHsb[2] = Math.min(1.0f, hsb[2] + 0.2f);
		final var newData = new RGB(newHsb[0], newHsb[1], newHsb[2]);
		return getColor(newData.red, newData.green, newData.blue);
	}

	static Color computeDarker(final Color c) {
		final var data = c.getRGB();
		final var hsb = data.getHSB();
		final var newHsb = new float[3];
		newHsb[0] = hsb[0];
		newHsb[1] = hsb[1];
		newHsb[2] = Math.max(0.0f, hsb[2] - 0.1f);
		final var newData = new RGB(newHsb[0], newHsb[1], newHsb[2]);
		return getColor(newData.red, newData.green, newData.blue);
	}

	static Color computeReverse(final Color c) {
		final var data = c.getRGB();
		return getColor(255 - data.red, 255 - data.green, 255 - data.blue);
	}

	static Color computeLighter(final Color c) {
		final var data = c.getRGB();
		final var hsb = data.getHSB();
		final var newHsb = new float[3];
		newHsb[0] = hsb[0];
		newHsb[1] = hsb[1];
		newHsb[2] = Math.min(1f, hsb[2] + 0.2f);
		final var newData = new RGB(newHsb[0], newHsb[1], newHsb[2]);
		return getColor(newData.red, newData.green, newData.blue);
	}

	static Color computeGray(final Color c) {
		final var data = c.getRGB();
		final var hsb = data.getHSB();
		final var newHsb = new float[3];
		newHsb[0] = hsb[0];
		newHsb[1] = 0.0f;
		newHsb[2] = hsb[2];
		final var newData = new RGB(newHsb[0], newHsb[1], newHsb[2]);
		return getColor(newData.red, newData.green, newData.blue);
	}

	private static Color getColor(final int r, final int g, final int b) {
		return new Color(WorkbenchHelper.getDisplay(), r, g, b);
	}

	public static GamaUIColor get(final java.awt.Color color) {
		if (color == null) { return null; }
		return get(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static GamaUIColor get(final RGB rgb) {
		if (rgb == null) { return null; }
		var c = colors.get(rgb);
		if (c == null) {
			final var cc = getColor(rgb.red, rgb.green, rgb.blue);
			c = new GamaUIColor(cc);
			colors.put(rgb, c);
		}
		return c;
	}

	public static GamaUIColor get(final Color color) {
		if (color == null) { return null; }
		return get(color.getRGB());
	}

	public static GamaUIColor get(final int r, final int g, final int b) {
		final var r1 = r < 0 ? 0 : r > 255 ? 255 : r;
		final var g1 = g < 0 ? 0 : g > 255 ? 255 : g;
		final var b1 = b < 0 ? 0 : b > 255 ? 255 : b;
		final var rgb = new RGB(r1, g1, b1);
		return get(rgb);
	}

	public static Color system(final int c) {
		return WorkbenchHelper.getDisplay().getSystemColor(c);
	}

	public static GamaUIColor get(final int... c) {
		if (c.length >= 3) {
			return get(c[0], c[1], c[2]);
		} else {
			final var rgb = c[0];
			final var red = rgb >> 16 & 0xFF;
			final var green = rgb >> 8 & 0xFF;
			final var blue = rgb & 0xFF;
			return get(red, green, blue);
		}
	}

	/**
	 * Get the color of the icon passed in parameter (supposing it's mono-colored)
	 *
	 * @param create
	 * @return
	 */
	public static GamaUIColor get(final GamaIcon icon) {
		final var image = icon.image();
		final var data = image.getImageData();
		final var palette = data.palette;
		final var pixelValue = data.getPixel(0, 0);
		return get(palette.getRGB(pixelValue));
	}

	/**
	 * @param background
	 * @return
	 */
	public static boolean isDark(final Color color) {
		return luminanceOf(color) < 130;
	}

	public static int luminanceOf(final Color color) {
		return (int) (0.299 * color.getRed() * color.getRed() / 255 + 0.587 * color.getGreen() * color.getGreen() / 255
				+ 0.114 * color.getBlue() * color.getBlue() / 255); // http://alienryderflex.com/hsp.html
	}

	public static java.awt.Color toAwtColor(final Color color) {
		return new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static GamaColor toGamaColor(final Color color) {
		return new GamaColor(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static GamaColor toGamaColor(final RGB color) {
		if (color == null) { return GamaColor.getInt(0); }
		return new GamaColor(color.red, color.green, color.blue);
	}

	/**
	 * @param background
	 * @return
	 */
	public static GamaUIColor getTextColorForBackground(final Color background) {
		return isDark(background) ? IGamaColors.WHITE : IGamaColors.BLACK;
	}

	public static GamaUIColor getTextColorForBackground(final GamaUIColor background) {
		return getTextColorForBackground(background.color());
	}

}
