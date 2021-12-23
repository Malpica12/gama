/*******************************************************************************************************
 *
 * SwingControl.java, in gama.display.java2d, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.display.java2d.swing;

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Panel;

import javax.swing.JApplet;
import javax.swing.LayoutFocusTraversalPolicy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import gama.display.java2d.Java2DDisplaySurface;
import gama.display.java2d.WorkaroundForIssue2476;
import gama.ui.base.utils.PlatformHelper;
import gama.ui.base.utils.WorkbenchHelper;

/**
 * The Class SwingControl.
 */
public abstract class SwingControl extends Composite {

	static {
		// DEBUG.ON();
	}

	/** The applet. */
	JApplet applet;
	
	/** The frame. */
	Frame frame;
	
	/** The populated. */
	boolean populated = false;

	/**
	 * Instantiates a new swing control.
	 *
	 * @param parent the parent
	 * @param style the style
	 */
	public SwingControl(final Composite parent, final int style) {
		super(parent, style | ((style & SWT.BORDER) == 0 ? SWT.EMBEDDED : 0) | SWT.NO_BACKGROUND);
		setLayout(new FillLayout());
		addListener(SWT.Dispose, event -> EventQueue.invokeLater(() -> {
			try {
				frame.remove(applet);
			} catch (final Exception e) {}

		}));

	}

	@Override
	public void checkWidget() {}

	@Override
	public boolean isFocusControl() {
		boolean result = false;
		try {
			result = super.isFocusControl();
		} catch (final Exception e) {
			// Nothing. Eliminates annoying exceptions when closing Java2D displays.
		}
		return result;
	}

	/**
	 * Populate.
	 */
	protected void populate() {
		if (isDisposed()) return;
		if (!populated) {
			populated = true;
			frame = SWT_AWT.new_Frame(this);
			EventQueue.invokeLater(() -> {
				applet = new JApplet();
				if (PlatformHelper.isWindows()) { applet.setFocusTraversalPolicy(new LayoutFocusTraversalPolicy()); }
				frame.add(applet);
				final Java2DDisplaySurface surface = createSwingComponent();
				applet.add(surface);
				WorkaroundForIssue2476.installOn(applet, surface);
				WorkbenchHelper.asyncRun(() -> SwingControl.this.getParent().layout(true, true));
			});
		}
	}

	/**
	 * Creates the embedded Swing component. This method is called from the AWT event thread.
	 *
	 * @return a non-null Swing component
	 */
	protected abstract Java2DDisplaySurface createSwingComponent();

	@Override
	public void setBounds(final Rectangle rect) {
		setBounds(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Overridden to propagate the size to the embedded Swing component.
	 */
	@Override
	public void setBounds(final int x, final int y, final int width, final int height) {
		// DEBUG.OUT("-- Surface bounds set to " + x + " " + y + " | " + width + " " + height);
		populate();
		super.setBounds(x, y, width, height);
	}

	/**
	 * Gets the top level container.
	 *
	 * @return the top level container
	 */
	public Panel getTopLevelContainer() {
		return applet;
	}

}