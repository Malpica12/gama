/*********************************************************************************************
 *
 * 'SimpleSlider.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.base.controls;

import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;
import static org.eclipse.jface.layout.GridLayoutFactory.fillDefaults;
import static org.eclipse.swt.SWT.BEGINNING;
import static org.eclipse.swt.SWT.DOUBLE_BUFFERED;
import static org.eclipse.swt.SWT.FILL;
import static org.eclipse.swt.SWT.NO_BACKGROUND;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import gama.core.dev.utils.DEBUG;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.resources.GamaColors.GamaUIColor;

public class SimpleSlider extends Composite implements IPopupProvider {

	static {
		DEBUG.OFF();
	}
	final static public int THUMB_WIDTH = 6;
	final static public int THUMB_HEIGHT = 13;
	final static public int PANEL_HEIGHT = 3;
	final Composite parent;
	final Thumb thumb;

	final Panel leftRegion, rightRegion;
	boolean mouseDown = false;
	private int sliderHeight;
	private Double step = null;

	private IToolTipProvider toolTipInterperter;
	private final List<IPositionChangeListener> positionChangedListeners = new ArrayList<>();
	/**
	 * stores the previous position that was sent out to the position changed listeners
	 */
	double previousPosition = 0;

	GamaUIColor popupColor = IGamaColors.GRAY_LABEL;
	Popup2 popup = null;
	private boolean notify = true;
	private final IPositionChangeListener popupListener = (slider, position) -> popup.display();

	public SimpleSlider(final Composite parent, final Color leftColor, final Color rightColor, final Color thumbColor,
			final boolean withPopup) {
		super(parent, SWT.DOUBLE_BUFFERED);
		this.parent = parent;
		fillDefaults().numColumns(3).spacing(0, 0).applyTo(this);
		leftRegion = new Panel(this, leftColor);
		leftRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(e.x - THUMB_WIDTH / 2);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		leftRegion.addMouseMoveListener(e -> {
			if (mouseDown) { moveThumbHorizontally(e.x - THUMB_WIDTH / 2); }
		});
		thumb = new Thumb(this, thumbColor);
		thumb.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(leftRegion.getBounds().width + e.x - THUMB_WIDTH / 2);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		thumb.addMouseMoveListener(e -> {
			if (mouseDown) { moveThumbHorizontally(leftRegion.getBounds().width + e.x - THUMB_WIDTH / 2); }
		});

		rightRegion = new Panel(this, rightColor, true);
		rightRegion.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				mouseDown = true;
				moveThumbHorizontally(leftRegion.getBounds().width + thumb.getBounds().width / 2 + e.x);
			}

			@Override
			public void mouseUp(final MouseEvent e) {
				mouseDown = false;
			}
		});
		rightRegion.addMouseMoveListener(e -> {
			if (mouseDown) {
				moveThumbHorizontally(leftRegion.getBounds().width + thumb.getBounds().width / 2 + e.x);

			}
		});

		addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(final ControlEvent e) {
				updateSlider(previousPosition, false);
			}
		});

		addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				thumb.setFocus();
			}
		});

		addTraverseListener(e -> e.doit = true);
		if (withPopup) {
			addPositionChangeListener(popupListener);
			popup = new Popup2(this, leftRegion, thumb, rightRegion);
		} else {
			popup = null;
		}
		// if (DEBUG.IS_ON()) {
		// addPositionChangeListener((slider, position) -> DEBUG.OUT("Position changed to : " + position));
		// }
	}

	public void addPositionChangeListener(final IPositionChangeListener listener) {
		synchronized (positionChangedListeners) {
			if (!positionChangedListeners.contains(listener)) { positionChangedListeners.add(listener); }
		}
	}

	/**
	 *
	 * @return the position of the slider in the form of a percentage. Note the range is from 0 to 1
	 */
	public double getCurrentPosition() {
		return previousPosition;
	}

	private void updatePositionListeners(final double perc) {
		if (!notify) return;
		if (Math.abs(perc - previousPosition) > 0.000001) {
			final Iterator<IPositionChangeListener> iter = positionChangedListeners.iterator();
			while (iter.hasNext()) {
				iter.next().positionChanged(SimpleSlider.this, perc);
			}
		}
	}

	void moveThumbHorizontally(final int x) {
		final int width = getClientArea().width - THUMB_WIDTH;
		int pos = x < 0 ? 0 : x > width ? width : x;
		double percentage = pos / (double) width;
		if (step != null) { percentage = Math.round(percentage / step) * step; }
		pos = (int) (percentage * width);
		thumb.setFocus();
		leftRegion.updatePosition(pos);
		layout();
		updatePositionListeners(percentage);
		previousPosition = percentage;
	}

	/**
	 * Method to update current position of the slider
	 *
	 * @param percentage
	 *            between 0 and 1 (i.e 0% to 100%)
	 */
	public void updateSlider(final double p, final boolean n) {
		double percentage = p;
		if (step != null) { percentage = Math.round(percentage / step) * step; }
		this.notify = n;
		if (percentage < 0) {
			percentage = 0;
		} else if (percentage > 1) { percentage = 1; }
		final int usefulWidth = getClientArea().width - THUMB_WIDTH;
		final int width = (int) Math.round(usefulWidth * percentage);
		moveThumbHorizontally(width);
		previousPosition = percentage;
		this.notify = true;
	}

	/**
	 *
	 * @param toolTipInterperter
	 */
	public void setTooltipInterperter(final IToolTipProvider toolTipInterperter) {
		this.toolTipInterperter = toolTipInterperter;
	}

	@Override
	public void setBackground(final Color color) {
		thumb.setBackground(color);
		rightRegion.setBackground(color);
		leftRegion.setBackground(color);
		super.setBackground(color);
	}

	public void setLeftBackground(final Color color) {
		leftRegion.setBackground(color);
	}

	public void setRightBackground(final Color color) {
		rightRegion.setBackground(color);
	}

	public void setPopupBackground(final GamaUIColor color) {
		popupColor = color;
	}

	@Override
	public void setToolTipText(final String string) {
		super.setToolTipText(string);
		thumb.setToolTipText(string);
		rightRegion.setToolTipText(string);
		leftRegion.setToolTipText(string);
	}

	/**
	 * @see gama.ui.base.controls.IPopupProvider#getPopupText()
	 */
	@Override
	public PopupText getPopupText() {
		final double value = getCurrentPosition();
		final String text =
				toolTipInterperter == null ? String.valueOf(value) : toolTipInterperter.getToolTipText(value);
		return PopupText.with(popupColor, text);
	}

	@Override
	public Point getAbsoluteOrigin() {
		return leftRegion.toDisplay(new Point(leftRegion.getLocation().x, -sliderHeight));
	}

	@Override
	public Shell getControllingShell() {
		return leftRegion.getShell();
	}

	public void specifyHeight(final int heightsize) {
		sliderHeight = heightsize;
	}

	public void setStep(final Double realStep) {
		if (realStep != null && realStep > 0d) { step = realStep; }
	}

	public class Thumb extends Canvas implements PaintListener {

		final Color color;

		public Thumb(final Composite parent, final Color thumbColor) {
			super(parent, NO_BACKGROUND);
			color = thumbColor;
			addPaintListener(this);
			swtDefaults().hint(THUMB_WIDTH, THUMB_HEIGHT).minSize(THUMB_WIDTH, THUMB_HEIGHT).align(BEGINNING, SWT.FILL)
					.grab(false, true).applyTo(this);
		}

		@Override
		public boolean forceFocus() {
			return true;
		}

		@Override
		public Point computeSize(final int w, final int h) {
			return new Point(THUMB_WIDTH, THUMB_HEIGHT);
		}

		@Override
		public void paintControl(final PaintEvent e) {
			final GC gc = e.gc;
			// DEBUG.OUT("Thumb bounds " + getBounds() + " client area: " + getClientArea() + " gc clipping: "
			// + gc.getClipping());
			final Rectangle r = gc.getClipping();
			gc.setBackground(getParent().getBackground());
			gc.fillRectangle(r);
			gc.setBackground(color);
			gc.fillRoundRectangle(0, (r.height - THUMB_HEIGHT) / 2 + 1, THUMB_WIDTH, THUMB_HEIGHT, 3, 3);
		}
	}

	private class Panel extends Canvas implements PaintListener {

		private final GridData gd;
		private final Color color;

		public Panel(final Composite parent, final Color color) {
			this(parent, color, false);
		}

		public Panel(final Composite parent, final Color color, final boolean last) {
			super(parent, DOUBLE_BUFFERED | NO_BACKGROUND);
			gd = swtDefaults().minSize(0, PANEL_HEIGHT).align(last ? FILL : BEGINNING, BEGINNING).grab(last, false)
					.create();
			this.color = color;
			setLayoutData(gd);
			addPaintListener(this);
		}

		void updatePosition(final int value) {
			gd.minimumWidth = value;
			gd.widthHint = value;
		}

		/**
		 * Method paintControl()
		 *
		 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
		 */
		@Override
		public void paintControl(final PaintEvent e) {
			final GC gc = e.gc;
			// DEBUG.OUT("Panel bounds " + getBounds() + " client area: " + getClientArea() + " gc clipping: "
			// + gc.getClipping() + " parent bounds " + parent.getBounds() + " parent size " + parent.getSize());
			final Rectangle r = gc.getClipping();
			gc.setBackground(getParent().getBackground());
			gc.fillRectangle(r);
			gc.setBackground(color);
			gc.fillRoundRectangle(r.x, (int) ((double) r.height / 2 - 1d), r.width, PANEL_HEIGHT, 3, 3);
		}

	}

}
