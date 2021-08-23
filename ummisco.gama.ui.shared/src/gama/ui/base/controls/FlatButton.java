/*********************************************************************************************
 *
 * 'FlatButton.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.base.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;

import gama.core.dev.utils.DEBUG;
import gama.ui.base.resources.GamaColors;
import gama.ui.base.resources.GamaIcons;
import gama.ui.base.resources.GamaColors.GamaUIColor;

public class FlatButton extends Canvas implements PaintListener, Listener {

	static {
		DEBUG.ON();
	}

	public static FlatButton create(final Composite comp, final int style) {
		return new FlatButton(comp, style);
	}

	public static FlatButton label(final Composite comp, final GamaUIColor color, final String text) {
		return button(comp, color, text).disabled();
	}

	public static FlatButton label(final Composite comp, final GamaUIColor color, final String text,
			final int forcedWidth) {
		return create(comp, SWT.None).setWidth(forcedWidth).setText(text).setColor(color);
	}

	public static FlatButton label(final Composite comp, final GamaUIColor color, final String text,
			final Image image) {
		return label(comp, color, text).setImage(image);
	}

	public static FlatButton button(final Composite comp, final GamaUIColor color, final String text) {
		return create(comp, SWT.None).setText(text).setColor(color);
	}

	public static FlatButton button(final Composite comp, final GamaUIColor color, final String text,
			final Image image) {
		return button(comp, color, text).setImage(image);
	}

	public static FlatButton menu(final Composite comp, final GamaUIColor color, final String text) {
		return button(comp, color, text).setImageStyle(IMAGE_RIGHT)
				.setImage(GamaIcons.create("small.dropdown").image());
	}

	private Image image;
	private String text;
	private RGB colorCode;
	private static final int innerMarginWidth = 5;
	// private static int DEFAULT_HEIGHT =
	// WorkbenchHelper.getDisplay().getSystemFont().getFontData()[0].getHeight() + innerMarginWidth;
	private int preferredHeight = -1; // DEFAULT_HEIGHT;
	private int preferredWidth = -1;
	private static final int imagePadding = 5;
	private boolean enabled = true;
	private boolean hovered = false;
	private boolean down = false;

	public static int IMAGE_LEFT = 0;
	public static int IMAGE_RIGHT = 1;
	private int imageStyle = IMAGE_LEFT;
	private int forcedWidth = -1;

	private FlatButton(final Composite parent, final int style) {
		super(parent, style | SWT.DOUBLE_BUFFERED);
		addPaintListener(this);
		addListeners();
	}

	@Override
	public void handleEvent(final Event e) {
		switch (e.type) {
			case SWT.MouseExit:
				doHover(false);
				break;
			case SWT.MouseMove:
				break;
			case SWT.MouseEnter:
			case SWT.MouseHover:
				doHover(true);
				e.doit = true;
				break;
			case SWT.MouseUp:
				if (e.button == 1 && getClientArea().contains(e.x, e.y)) { doButtonUp(); }
				break;
			case SWT.MouseDown:
				if (e.button == 1 && getClientArea().contains(e.x, e.y)) { doButtonDown(); }
				break;
			default:
				;
		}
	}

	/**
	 * SelectionListeners are notified when the button is clicked
	 *
	 * @param listener
	 */
	public void addSelectionListener(final SelectionListener listener) {
		if (listener == null) return;
		addListener(SWT.Selection, new TypedListener(listener));
	}

	public void doButtonDown() {
		if (!enabled) return;
		down = true;
		if (!isDisposed()) { redraw(); }
	}

	private void doButtonUp() {
		if (!enabled) return;
		final Event e = new Event();
		e.item = this;
		e.widget = this;
		e.type = SWT.Selection;
		notifyListeners(SWT.Selection, e);
		down = false;
		if (!isDisposed()) { redraw(); }
	}

	private void doHover(final boolean hover) {
		hovered = hover;
		if (!isDisposed()) { redraw(); }
	}

	@Override
	public void paintControl(final PaintEvent e) {
		final GC gc = e.gc;
		gc.setAntialias(SWT.ON);
		Font f = getFont();
		gc.setFont(f);
		int v_inset;
		if (preferredHeight < getBounds().height) {
			v_inset = (getBounds().height - preferredHeight) / 2;
		} else {
			v_inset = 0;
		}
		final Rectangle rect = new Rectangle(0, v_inset, preferredWidth, preferredHeight);
		setBackground(getParent().getBackground());
		final GamaUIColor color = GamaColors.get(colorCode);
		final Color background = hovered ? color.lighter() : color.color();
		final Color foreground = GamaColors.getTextColorForBackground(background).color();
		gc.setForeground(foreground);
		gc.setBackground(background);

		if (down) {
			gc.fillRoundRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, 8, 8);
		} else {
			gc.fillRoundRectangle(rect.x, rect.y, rect.width, rect.height, 8, 8);
		}

		int x = FlatButton.innerMarginWidth;
		int y_image = 0;
		final Image image = getImage();
		if (image != null) { y_image += (getBounds().height - image.getBounds().height) / 2; }
		int y_text = 0;
		final String text = newText();
		if (text != null) { y_text += (getBounds().height - gc.textExtent(text).y) / 2; }

		if (imageStyle == IMAGE_RIGHT) {
			gc.drawString(text, x, y_text);
			if (image != null) {
				x = rect.width - x - image.getBounds().width;
				drawImage(gc, x, y_image);
			}
		} else {
			x = drawImage(gc, x, y_image);
			gc.drawString(text, x, y_text);
		}
	}

	private int drawImage(final GC gc, final int x, final int y) {
		if (getImage() == null) return x;
		gc.drawImage(getImage(), x, y);
		return x + getImage().getBounds().width + imagePadding;
	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		int width = 0, height = 0;
		if (wHint != SWT.DEFAULT) {
			width = wHint;
		} else {
			width = preferredWidth;
		}
		if (hHint != SWT.DEFAULT) {
			height = hHint;
		} else {
			height = preferredHeight;
		}
		return new Point(width, height);
	}

	public String newText() {
		if (text == null) return null;
		final int parentWidth = getParent().getBounds().width;
		final int width = preferredWidth;
		if (parentWidth < width) {
			int imageWidth = 0;
			final Image image = getImage();
			if (image != null) {
				final Rectangle bounds = image.getBounds();
				if (imageStyle == IMAGE_LEFT) {
					imageWidth = bounds.width + imagePadding;
				} else {
					imageWidth = (bounds.width + imagePadding) * 2;
				}
			}
			final float r = (float) (parentWidth - imageWidth) / (float) width;
			final int nbChars = text.length();
			final int newNbChars = Math.max(0, (int) (nbChars * r));
			return text.substring(0, newNbChars / 2) + "..." + text.substring(nbChars - newNbChars / 2, nbChars);
		}
		return text;
	}

	/**
	 * This is an image that will be displayed to the side of the text inside the button (if any). By default the image
	 * will be to the left of the text; however, setImageStyle can be used to specify that it's either to the right or
	 * left. If there is no text, the image will be centered inside the button.
	 *
	 * @param image
	 */
	public FlatButton setImage(final Image image) {
		if (this.image == image) return this;
		this.image = image;
		computePreferredSize();
		redraw();
		return this;
	}

	private void computePreferredSize() {

		final Image image = getImage();
		if (image != null) {
			final Rectangle bounds = image.getBounds();
			if (imageStyle == IMAGE_LEFT) {
				preferredWidth = bounds.width + imagePadding;
			} else {
				preferredWidth = (bounds.width + imagePadding) * 2;
			}
			preferredHeight = bounds.height + imagePadding;
		}
		if (text != null) {
			final GC gc = new GC(this);
			gc.setFont(getFont());
			final Point extent = gc.textExtent(text + "...");
			gc.dispose();
			preferredWidth += extent.x + FlatButton.innerMarginWidth * 2;
			preferredHeight = Math.max(preferredHeight, extent.y + innerMarginWidth);
		}
		if (forcedWidth > 0) { preferredWidth = forcedWidth; }

		// DEBUG.OUT("Computing min height for button " + text + " = " + preferredHeight);

	}

	/**
	 * Set the style with which the side image is drawn, either IMAGE_LEFT or IMAGE_RIGHT (default is IMAGE_LEFT).
	 *
	 * @param imageStyle
	 */
	public FlatButton setImageStyle(final int imageStyle) {
		this.imageStyle = imageStyle;
		computePreferredSize(); // The inset is not the same
		redraw();
		return this;
	}

	public int getImageStyle() {
		return imageStyle;
	}

	public String getText() {
		return text;
	}

	public FlatButton setText(final String text) {
		if (text == null || text.equals(this.text)) return this;
		this.text = text;
		computePreferredSize();
		redraw();
		return this;
	}

	private void addListeners() {
		addListener(SWT.MouseDown, this);
		addListener(SWT.MouseExit, this);
		addListener(SWT.MouseEnter, this);
		addListener(SWT.MouseHover, this);
		addListener(SWT.MouseUp, this);
		addListener(SWT.MouseMove, this);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		final boolean oldSetting = this.enabled;
		this.enabled = enabled;
		if (oldSetting != enabled) {
			if (enabled) {
				addListeners();
			} else {
				removeListener(SWT.MouseDown, (Listener) this);
				removeListener(SWT.MouseExit, (Listener) this);
				removeListener(SWT.MouseEnter, (Listener) this);
				removeListener(SWT.MouseHover, (Listener) this);
				removeListener(SWT.MouseUp, (Listener) this);
				removeListener(SWT.MouseMove, (Listener) this);
			}
			redraw();
		}
	}

	public FlatButton disabled() {
		setEnabled(false);
		return this;
	}

	public FlatButton light() {
		return this;
	}

	public FlatButton small() {
		return this;
	}

	public FlatButton setColor(final GamaUIColor c) {
		final RGB oldColorCode = colorCode;
		final RGB newColorCode = c.getRGB();
		if (newColorCode.equals(oldColorCode)) return this;
		colorCode = c.getRGB();
		redraw();
		return this;
	}

	public int getHeight() {
		return preferredHeight;
	}

	public FlatButton setWidth(final int width) {
		forcedWidth = width;
		preferredWidth = width;
		return this;
	}

	public GamaUIColor getColor() {
		return GamaColors.get(colorCode);
	}

	public Image getImage() {
		return image;
	}

	/**
	 * Sent by the layout
	 */
	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		setWidth(width);
		super.setBounds(x, y, width, height);
	}

	@Override
	public void setBounds(final Rectangle rect) {
		setBounds(rect.x, rect.y, rect.width, rect.height);
	}

}