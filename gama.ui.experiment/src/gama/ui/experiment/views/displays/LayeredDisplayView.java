/*******************************************************************************************************
 *
 * LayeredDisplayView.java, in gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.experiment.views.displays;

import static gama.common.preferences.GamaPreferences.Displays.CORE_DISPLAY_BORDER;
import static gama.common.preferences.GamaPreferences.Runtime.CORE_SYNC;

import java.awt.Color;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;

import gama.common.ui.IDisplaySurface;
import gama.common.ui.IGamaView;
import gama.common.ui.ILayerManager;
import gama.core.dev.utils.DEBUG;
import gama.kernel.experiment.ITopLevelAgent;
import gama.outputs.IDisplayOutput;
import gama.outputs.LayeredDisplayOutput;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.ui.base.resources.GamaColors;
import gama.ui.base.resources.GamaIcons;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.toolbar.GamaToolbar2;
import gama.ui.base.toolbar.IToolbarDecoratedView;
import gama.ui.base.utils.WorkbenchHelper;
import gama.ui.base.views.GamaViewPart;

// TODO: Auto-generated Javadoc
/**
 * The Class LayeredDisplayView.
 */
public abstract class LayeredDisplayView extends GamaViewPart
		implements IToolbarDecoratedView.Pausable, IToolbarDecoratedView.Zoomable, IGamaView.Display {

	/** The real index. */
	protected int realIndex = -1;

	/** The form. */
	protected SashForm form;

	/** The surface composite. */
	public Composite surfaceComposite;

	/** The decorator. */
	public final LayeredDisplayDecorator decorator;

	/** The synchronizer. */
	public final LayeredDisplaySynchronizer synchronizer = new LayeredDisplaySynchronizer();

	/** The disposed. */
	protected volatile boolean disposed = false;

	/** The in init phase. */
	protected volatile boolean inInitPhase = true;

	/**
	 * Sets the index.
	 *
	 * @param index
	 *            the new index
	 */
	@Override
	public void setIndex(final int index) { realIndex = index; }

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	@Override
	public int getIndex() { return realIndex; }

	/**
	 * Instantiates a new layered display view.
	 */
	public LayeredDisplayView() {
		decorator = new LayeredDisplayDecorator(this);
	}

	/**
	 * Control to set full screen.
	 *
	 * @return the control
	 */
	public Control controlToSetFullScreen() {
		return form;
	}

	/**
	 * Gets the sash.
	 *
	 * @return the sash
	 */
	public SashForm getSash() { return form; }

	/**
	 * Contains point.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	@Override
	public boolean containsPoint(final int x, final int y) {
		if (super.containsPoint(x, y)) return true;
		final Point o = getSurfaceComposite().toDisplay(0, 0);
		final Point s = getSurfaceComposite().getSize();
		return new Rectangle(o.x, o.y, s.x, s.y).contains(x, y);
	}

	/**
	 * Inits the.
	 *
	 * @param site
	 *            the site
	 * @throws PartInitException
	 *             the part init exception
	 */
	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		if (getOutput() != null) { setPartName(getOutput().getName()); }
	}

	/**
	 * Adds the output.
	 *
	 * @param out
	 *            the out
	 */
	@Override
	public void addOutput(final IDisplayOutput out) {
		super.addOutput(out);
		if (out instanceof LayeredDisplayOutput) {
			((LayeredDisplayOutput) out).getData().addListener(decorator);
			final IScope scope = out.getScope();
			if (scope != null && scope.getSimulation() != null) {
				final ITopLevelAgent root = scope.getRoot();
				final Color color = root.getColor();
				this.setTitleImage(GamaIcons.createTempColorIcon(GamaColors.get(color)));
			}
		}

	}

	/**
	 * Checks if is open GL.
	 *
	 * @return true, if is open GL
	 */
	public boolean isOpenGL() {
		if (outputs.isEmpty()) return false;
		return getOutput().getData().isOpenGL();
	}

	/**
	 * Gets the display manager.
	 *
	 * @return the display manager
	 */
	public ILayerManager getDisplayManager() { return getDisplaySurface().getManager(); }

	/**
	 * Gets the surface composite.
	 *
	 * @return the surface composite
	 */
	public Composite getSurfaceComposite() { return surfaceComposite; }

	/**
	 * Own create part control.
	 *
	 * @param c
	 *            the c
	 */
	@Override
	public void ownCreatePartControl(final Composite c) {
		if (getOutput() == null) return;
		c.setLayout(emptyLayout());

		// First create the sashform

		form = new SashForm(c, SWT.HORIZONTAL);
		form.setLayoutData(fullData());
		form.setBackground(IGamaColors.WHITE.color());
		form.setSashWidth(8);
		decorator.createSidePanel(form);
		final Composite centralPanel = new Composite(form, CORE_DISPLAY_BORDER.getValue() ? SWT.BORDER : SWT.NONE);

		centralPanel.setLayout(emptyLayout());
		setParentComposite(new Composite(centralPanel, SWT.NONE) {

			@Override
			public boolean setFocus() {
				return forceFocus();
			}

		});

		getParentComposite().setLayoutData(fullData());
		getParentComposite().setLayout(emptyLayout());
		createSurfaceComposite(getParentComposite());
		surfaceComposite.setLayoutData(fullData());
		getOutput().setSynchronized(getOutput().isSynchronized() || CORE_SYNC.getValue());
		form.setMaximizedControl(centralPanel);
		decorator.createDecorations(form);
		c.layout();

	}

	/**
	 * Empty layout.
	 *
	 * @return the grid layout
	 */
	GridLayout emptyLayout() {
		final GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		return gl;
	}

	/**
	 * Full data.
	 *
	 * @return the grid data
	 */
	GridData fullData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}

	/**
	 * Sets the focus.
	 */
	@Override
	public void setFocus() {
		if (getParentComposite() != null && !getParentComposite().isDisposed()
				&& !getParentComposite().isFocusControl()) {
			getParentComposite().forceFocus();
		}
	}

	/**
	 * Creates the surface composite.
	 *
	 * @param parent
	 *            the parent
	 * @return the composite
	 */
	protected abstract Composite createSurfaceComposite(Composite parent);

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	@Override
	public LayeredDisplayOutput getOutput() { return (LayeredDisplayOutput) super.getOutput(); }

	/**
	 * Gets the display surface.
	 *
	 * @return the display surface
	 */
	@Override
	public IDisplaySurface getDisplaySurface() {
		final LayeredDisplayOutput out = getOutput();
		if (out != null) return out.getSurface();
		return null;
	}

	/**
	 * Widget disposed.
	 *
	 * @param e
	 *            the e
	 */
	@Override
	public void widgetDisposed(final DisposeEvent e) {
		if (disposed) return;
		final LayeredDisplayOutput output = getOutput();
		if (output != null) {
			output.getData().listeners.clear();
			final IDisplaySurface s = output.getSurface();
			if (isOpenGL() && s != null) {
				s.dispose();
				output.setSurface(null);
			}
		}

		disposed = true;
		if (surfaceComposite != null) {
			try {
				surfaceComposite.dispose();
			} catch (final RuntimeException ex) {

			}
		}
		synchronizer.authorizeViewUpdate();
		// }
		if (updateThread != null) { updateThread.interrupt(); }
		if (decorator != null) { decorator.dispose(); }
		super.widgetDisposed(e);
	}

	/**
	 * Pause changed.
	 */
	@Override
	public void pauseChanged() {
		decorator.updateOverlay();
	}

	/**
	 * Force overlay visibility.
	 *
	 * @return true, if successful
	 */
	public boolean forceOverlayVisibility() {
		return false;
	}

	/**
	 * Synchronize changed.
	 */
	@Override
	public void synchronizeChanged() {
		decorator.updateOverlay();
	}

	/**
	 * Zoom in.
	 */
	@Override
	public void zoomIn() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomIn(); }
	}

	/**
	 * Zoom out.
	 */
	@Override
	public void zoomOut() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomOut(); }
	}

	/**
	 * Zoom fit.
	 */
	@Override
	public void zoomFit() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomFit(); }
	}

	/**
	 * Gets the zoomable controls.
	 *
	 * @return the zoomable controls
	 */
	@Override
	public Control[] getZoomableControls() { return new Control[] { getParentComposite() }; }

	/**
	 * Creates the update job.
	 *
	 * @return the gama UI job
	 */
	@Override
	protected GamaUIJob createUpdateJob() {
		return new GamaUIJob() {

			@Override
			protected UpdatePriority jobPriority() {
				return UpdatePriority.HIGHEST;
			}

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {
				return Status.OK_STATUS;
			}
		};
	}

	/** The update thread. */
	final Thread updateThread = new Thread(() -> {
		final IDisplaySurface surface = getDisplaySurface();
		synchronizer.waitForSurfaceToBeRealized();
		while (!disposed && !surface.isDisposed()) {
			try {
				synchronizer.waitForViewUpdateAuthorisation();
				surface.updateDisplay(false);
				if (surface.getData().isAutosave()) { takeSnapshot(); }
				inInitPhase = false;
			} catch (Exception e) {
				DEBUG.OUT("Error when updating " + this.getTitle() + ": " + e.getMessage());
			}
		}
	});

	/**
	 * Update.
	 *
	 * @param out
	 *            the out
	 */
	@Override
	public void update(final IDisplayOutput out) {
		if (!updateThread.isAlive()) {
			synchronizer.setSurface(getDisplaySurface());
			updateThread.start();
		}
		synchronizer.authorizeViewUpdate();
		if (!inInitPhase && out.isSynchronized()) { synchronizer.waitForRenderingToBeFinished(); }
	}

	/**
	 * Zoom when scrolling.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}

	/**
	 * Removes the output.
	 *
	 * @param output
	 *            the output
	 */
	@Override
	public void removeOutput(final IDisplayOutput output) {
		if (output == null) return;
		if (output == getOutput() && isFullScreen()) { WorkbenchHelper.run(this::toggleFullScreen); }
		output.dispose();
		outputs.remove(output);
		if (outputs.isEmpty()) {
			synchronizer.authorizeViewUpdate();
			close(GAMA.getRuntimeScope());
		}
	}

	/**
	 * Checks if is full screen.
	 *
	 * @return true, if is full screen
	 */
	@Override
	public boolean isFullScreen() { return decorator.isFullScreen(); }

	/**
	 * Toggle side controls.
	 */
	@Override
	public void toggleSideControls() {
		decorator.toggleSideControls();
	}

	/**
	 * Toggle overlay.
	 */
	@Override
	public void toggleOverlay() {
		decorator.toggleOverlay();
	}

	/**
	 * Toggle full screen.
	 */
	@Override
	public void toggleFullScreen() {
		decorator.toggleFullScreen();
	}

	/**
	 * Creates the tool items.
	 *
	 * @param tb
	 *            the tb
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		decorator.createToolItems(tb);
	}

	/**
	 * Show overlay.
	 */
	@Override
	public void showOverlay() {
		decorator.overlay.setVisible(true);
	}

	/**
	 * Hide overlay.
	 */
	@Override
	public void hideOverlay() {
		decorator.overlay.setVisible(false);
	}

	/**
	 * A call indicating that fullscreen has been set on the display. Views might decide to do something or not. Default
	 * is to do nothing.
	 */
	public void fullScreenSet() {}

	/**
	 * Take snapshot.
	 */
	@Override
	public void takeSnapshot() {
		SnapshotMaker.getInstance().doSnapshot(getDisplaySurface(), WorkbenchHelper.displaySizeOf(surfaceComposite));
	}

}
