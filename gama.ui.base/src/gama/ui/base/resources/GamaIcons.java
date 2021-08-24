/*********************************************************************************************
 *
 * 'GamaIcons.java, in plugin gama.ui.base, is part of the source code of the GAMA modeling and simulation
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
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

import gama.ui.base.interfaces.IIconProvider;
import gama.ui.base.resources.GamaColors.GamaUIColor;
import gama.ui.base.utils.WorkbenchHelper;

/**
 * Class GamaIcons.
 *
 * @author drogoul
 * @since 12 sept. 2013
 *
 */
public class GamaIcons implements IIconProvider {

	public static final String PLUGIN_ID = "gama.ui.base";

	static private GamaIcons instance = new GamaIcons();

	public static GamaIcons getInstance() {
		return instance;
	}

	static public final String DEFAULT_PATH = "/icons/";
	static final String SIZER_PREFIX = "sizer_";
	static final String COLOR_PREFIX = "color_";

	Map<String, GamaIcon> iconCache = new HashMap<>();
	Map<String, Image> imageCache = new HashMap<>();

	GamaIcon getIcon(final String name) {
		return iconCache.get(name);
	}

	Image putImageInCache(final String name, final Image image) {
		imageCache.put(name, image);
		return image;

	}

	void putIconInCache(final String name, final GamaIcon icon) {
		iconCache.put(name, icon);
	}

	Image getImageInCache(final String code) {
		return imageCache.get(code);
	}

	public static GamaIcon createSizer(final Color color, final int width, final int height) {
		final String name = SIZER_PREFIX + width + "x" + height + color.hashCode();
		GamaIcon sizer = getInstance().getIcon(name);
		if (sizer == null) {
			final Image sizerImage = new Image(WorkbenchHelper.getDisplay(), width, height);
			final GC gc = new GC(sizerImage);
			gc.setBackground(color);
			gc.fillRectangle(0, 0, width, height);
			gc.dispose();
			sizer = new GamaIcon(name);
			getInstance().putImageInCache(name, sizerImage);
			getInstance().putIconInCache(name, sizer);
		}
		return sizer;
	}

	public static GamaIcon create(final String s) {
		return create(s, PLUGIN_ID);
	}

	public static GamaIcon create(final String code, final String plugin) {
		return create(code, code, plugin);
	}

	public static GamaIcon create(final String code, final String path, final String plugin) {
		GamaIcon result = getInstance().getIcon(code);
		if (result == null) {
			result = new GamaIcon(code, path, plugin);
			getInstance().putIconInCache(code, result);
		}
		return result;
	}

	public static GamaIcon createColorIcon(final String s, final GamaUIColor gcolor, final int width,
			final int height) {
		final String name = COLOR_PREFIX + s;
		GamaIcon icon = getInstance().getIcon(s);
		if (icon == null) {
			// Color color = gcolor.color();
			// RGB c = new RGB(color.getRed(), color.getGreen(),
			// color.getBlue());
			final Image image = new Image(WorkbenchHelper.getDisplay(), width, height);
			final GC gc = new GC(image);
			gc.setAntialias(SWT.ON);
			gc.setBackground(gcolor.color());
			gc.fillRoundRectangle(0, 0, width, height, width / 3, height / 3);
			gc.dispose();
			final ImageData data = image.getImageData();
			data.transparentPixel = data.palette.getPixel(new RGB(255, 255, 255));
			icon = new GamaIcon(name);
			getInstance().putImageInCache(name, new Image(WorkbenchHelper.getDisplay(), data));
			image.dispose();
			getInstance().putIconInCache(name, icon);
		}
		return icon;
	}

	/**
	 * Creates an icon that needs to be disposed afterwards
	 *
	 * @param gcolor
	 * @param width
	 * @param height
	 * @return
	 */
	public static Image createTempColorIcon(final GamaUIColor gcolor) {
		final String name = "color" + gcolor.getRGB().toString();
		final GamaIcon icon = getInstance().getIcon(name);
		if (icon != null) return icon.image();
		// Color color = gcolor.color();
		final GamaIcon blank = create("display.color2");
		final Image image = new Image(WorkbenchHelper.getDisplay(), blank.image().getImageData());
		final GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		gc.setBackground(gcolor.color());
		gc.fillRoundRectangle(6, 6, 12, 12, 4, 4);
		if (!gcolor.isDark()) {
			gc.setForeground(IGamaColors.BLACK.color());
			gc.drawRoundRectangle(6, 6, 12, 12, 4, 4);
		}
		gc.dispose();
		getInstance().putImageInCache(name, image);
		getInstance().putIconInCache(name, new GamaIcon(name));
		return image;
	}

	public static Image createTempRoundColorIcon(final GamaUIColor gcolor) {
		final String name = "roundcolor" + gcolor.getRGB().toString();
		final GamaIcon icon = getInstance().getIcon(name);
		if (icon != null) return icon.image();
		// Color color = gcolor.color();
		final GamaIcon blank = create("display.color3");
		final Image image = new Image(WorkbenchHelper.getDisplay(), blank.image().getImageData());
		final GC gc = new GC(image);
		gc.setAntialias(SWT.ON);
		gc.setBackground(gcolor.color());
		gc.fillOval(6, 6, 12, 12);
		if (!gcolor.isDark()) {
			gc.setForeground(IGamaColors.BLACK.color());
			gc.drawOval(6, 6, 12, 12);
		}
		gc.dispose();
		getInstance().putImageInCache(name, image);
		getInstance().putIconInCache(name, new GamaIcon(name));
		return image;
	}

	@Override
	public ImageDescriptor desc(final String name) {
		final GamaIcon icon = create(name);
		return icon.descriptor();
	}

	@Override
	public Image image(final String name) {
		final GamaIcon icon = create(name);
		return icon.image();
	}

}
