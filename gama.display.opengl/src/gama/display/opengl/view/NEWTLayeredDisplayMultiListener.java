/*
 *
 */
package gama.display.opengl.view;

import java.util.function.Supplier;

import com.jogamp.newt.Window;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowUpdateEvent;

import gama.common.interfaces.IDisposable;
import gama.common.ui.IDisplaySurface;
import gama.core.dev.utils.DEBUG;
import gama.runtime.PlatformHelper;
import gama.ui.experiment.views.displays.LayeredDisplayDecorator;
import gama.ui.experiment.views.displays.LayeredDisplayMultiListener;

// TODO: Auto-generated Javadoc
/**
 * A listener for NEWT events.
 *
 * @see NEWTLayeredDisplayMultiEvent
 */
public class NEWTLayeredDisplayMultiListener implements MouseListener, KeyListener, WindowListener, IDisposable {

	static {
		DEBUG.OFF();
	}

	/** The delegate. */
	final LayeredDisplayMultiListener delegate;

	/** The control. */
	final Window control;

	/** The ok. */
	final Supplier<Boolean> ok;

	/**
	 * Instantiates a new NEWT layered display multi listener.
	 *
	 * @param deco
	 *            the deco
	 * @param surface
	 *            the surface
	 * @param window
	 *            the window
	 */
	public NEWTLayeredDisplayMultiListener(final LayeredDisplayDecorator deco, final IDisplaySurface surface,
			final Window window) {

		delegate = new LayeredDisplayMultiListener(surface, deco);
		control = window;

		ok = () -> {
			final boolean viewOk = deco.view != null && !deco.view.disposed;
			if (!viewOk) return false;
			final boolean controlOk = control != null /* && !control.isDisposed() */;
			if (!controlOk) return false;

			// if (!Objects.equals(WorkbenchHelper.getActivePart(), deco.view)) {
			// WorkbenchHelper.getPage().activate(deco.view);
			// }
			return surface != null && !surface.isDisposed();
		};

		control.addKeyListener(this);
		control.addMouseListener(this);
		control.addWindowListener(this);
	}

	/**
	 * Dispose.
	 */
	@Override
	public void dispose() {
		control.removeKeyListener(this);
		control.removeMouseListener(this);
		control.removeWindowListener(this);
	}

	/**
	 * Key pressed.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyPressed(final KeyEvent e) {
		DEBUG.OUT("Key pressed: " + e);
		if (!ok.get()) return;
		delegate.keyPressed(e.getKeyChar());
	}

	/**
	 * Key released.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void keyReleased(final KeyEvent e) {
		DEBUG.OUT("Key released: " + e);
		if (!ok.get()) return;
		delegate.keyReleased(e.getKeyCode(),
				PlatformHelper.isMac() ? e.isMetaDown() : e.isControlDown() /* ?? GamaKeyBindings.ctrl(e) */);
	}

	/**
	 * Checks for modifiers.
	 *
	 * @param e
	 *            the e
	 * @return true, if successful
	 */
	private boolean hasModifiers(final MouseEvent e) {
		return e.isAltDown() || e.isAltGraphDown() || e.isControlDown() || e.isMetaDown() || e.isShiftDown();
	}

	/**
	 * Mouse entered.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseEntered(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.mouseEnter(e.getX(), e.getY(), hasModifiers(e), e.getButton());
	}

	/**
	 * Mouse exited.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseExited(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.mouseExit(e.getX(), e.getY(), hasModifiers(e), e.getButton());
	}

	/**
	 * Mouse moved.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseMoved(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.mouseMove(e.getX(), e.getY(), hasModifiers(e));
	}

	/**
	 * Mouse pressed.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mousePressed(final MouseEvent e) {
		if (!ok.get()) return;
		DEBUG.OUT("Mouse pressed with button " + e.getButton() + " modifiers " + e.getModifiersString(null));
		delegate.mouseDown(e.getX(), e.getY(), e.getButton(), hasModifiers(e));
	}

	/**
	 * Mouse released.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseReleased(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.mouseUp(e.getX(), e.getY(), e.getButton(), hasModifiers(e));
	}

	/**
	 * Mouse dragged.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseDragged(final MouseEvent e) {
		if (!ok.get()) return;
		delegate.dragDetected(e.getX(), e.getY());
	}

	/**
	 * Window resized.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void windowResized(final WindowEvent e) {}

	/**
	 * Window moved.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void windowMoved(final WindowEvent e) {}

	/**
	 * Window destroy notify.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void windowDestroyNotify(final WindowEvent e) {}

	/**
	 * Window destroyed.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void windowDestroyed(final WindowEvent e) {}

	/**
	 * Window gained focus.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void windowGainedFocus(final WindowEvent e) {
		if (!ok.get()) return;
		delegate.focusGained();
	}

	/**
	 * Window lost focus.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void windowLostFocus(final WindowEvent e) {
		if (!ok.get()) return;
		delegate.focusLost();

	}

	/**
	 * Window repaint.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void windowRepaint(final WindowUpdateEvent e) {}

	/**
	 * Mouse clicked.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseClicked(final MouseEvent e) {
		this.mouseReleased(e);
	}

	/**
	 * Mouse wheel moved.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void mouseWheelMoved(final MouseEvent e) {
		this.mouseMoved(e);
	}

}