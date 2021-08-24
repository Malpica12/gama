/*********************************************************************************************
 *
 * 'LayeredDisplayView.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
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
import gama.kernel.experiment.ITopLevelAgent;
import gama.outputs.IDisplayOutput;
import gama.outputs.LayeredDisplayOutput;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.ui.base.resources.GamaColors;
import gama.ui.base.resources.GamaIcons;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.utils.WorkbenchHelper;
import gama.ui.base.views.GamaViewPart;
import gama.ui.base.toolbar.GamaToolbar2;
import gama.ui.base.toolbar.IToolbarDecoratedView;

public abstract class LayeredDisplayView extends GamaViewPart
		implements IToolbarDecoratedView.Pausable, IToolbarDecoratedView.Zoomable, IGamaView.Display {

	protected int realIndex = -1;
	protected SashForm form;
	public Composite surfaceComposite;
	public final LayeredDisplayDecorator decorator;
	public final LayeredDisplaySynchronizer synchronizer = new LayeredDisplaySynchronizer();
	protected volatile boolean disposed = false;
	protected volatile boolean inInitPhase = true;

	@Override
	public void setIndex(final int index) {
		realIndex = index;
	}

	@Override
	public int getIndex() {
		return realIndex;
	}

	public LayeredDisplayView() {
		decorator = new LayeredDisplayDecorator(this);
	}

	public Control controlToSetFullScreen() {
		return form;
	}

	public SashForm getSash() {
		return form;
	}

	@Override
	public boolean containsPoint(final int x, final int y) {
		if (super.containsPoint(x, y)) return true;
		final Point o = getSurfaceComposite().toDisplay(0, 0);
		final Point s = getSurfaceComposite().getSize();
		return new Rectangle(o.x, o.y, s.x, s.y).contains(x, y);
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		if (getOutput() != null) { setPartName(getOutput().getName()); }
	}

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

	public boolean isOpenGL() {
		if (outputs.isEmpty()) return false;
		return getOutput().getData().isOpenGL();
	}

	public ILayerManager getDisplayManager() {
		return getDisplaySurface().getManager();
	}

	public Composite getSurfaceComposite() {
		return surfaceComposite;
	}

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

	GridLayout emptyLayout() {
		final GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		gl.verticalSpacing = 0;
		return gl;
	}

	GridData fullData() {
		return new GridData(SWT.FILL, SWT.FILL, true, true);
	}

	@Override
	public void setFocus() {
		if (getParentComposite() != null && !getParentComposite().isDisposed()
				&& !getParentComposite().isFocusControl()) {
			getParentComposite().forceFocus();
		}
	}

	protected abstract Composite createSurfaceComposite(Composite parent);

	@Override
	public LayeredDisplayOutput getOutput() {
		return (LayeredDisplayOutput) super.getOutput();
	}

	@Override
	public IDisplaySurface getDisplaySurface() {
		final LayeredDisplayOutput out = getOutput();
		if (out != null) return out.getSurface();
		return null;
	}

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

	@Override
	public void pauseChanged() {
		decorator.updateOverlay();
	}

	public boolean forceOverlayVisibility() {
		return false;
	}

	@Override
	public void synchronizeChanged() {
		decorator.updateOverlay();
	}

	@Override
	public void zoomIn() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomIn(); }
	}

	@Override
	public void zoomOut() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomOut(); }
	}

	@Override
	public void zoomFit() {
		if (getDisplaySurface() != null) { getDisplaySurface().zoomFit(); }
	}

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { getParentComposite() };
	}

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

	final Thread updateThread = new Thread(() -> {
		final IDisplaySurface surface = getDisplaySurface();
		synchronizer.waitForSurfaceToBeRealized();
		while (!disposed && !surface.isDisposed()) {
			synchronizer.waitForViewUpdateAuthorisation();
			surface.updateDisplay(false);
			if (surface.getData().isAutosave()) { takeSnapshot(); }
			inInitPhase = false;
		}
	});

	@Override
	public void update(final IDisplayOutput out) {
		if (!updateThread.isAlive()) {
			synchronizer.setSurface(getDisplaySurface());
			updateThread.start();
		}
		synchronizer.authorizeViewUpdate();
		if (!inInitPhase && out.isSynchronized()) { synchronizer.waitForRenderingToBeFinished(); }
	}

	@Override
	public boolean zoomWhenScrolling() {
		return true;
	}

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

	@Override
	public boolean isFullScreen() {
		return decorator.isFullScreen();
	}

	@Override
	public void toggleSideControls() {
		decorator.toggleSideControls();
	}

	@Override
	public void toggleOverlay() {
		decorator.toggleOverlay();
	}

	@Override
	public void toggleFullScreen() {
		decorator.toggleFullScreen();
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		decorator.createToolItems(tb);
	}

	@Override
	public void showToolbar() {
		toolbar.show();
	}

	@Override
	public void hideToolbar() {
		toolbar.hide();
	}

	@Override
	public void showOverlay() {
		decorator.overlay.setVisible(true);
	}

	@Override
	public void hideOverlay() {
		decorator.overlay.setVisible(false);
	}

	/**
	 * A call indicating that fullscreen has been set on the display. Views might decide to do something or not. Default
	 * is to do nothing.
	 */
	public void fullScreenSet() {}

	@Override
	public void takeSnapshot() {
		SnapshotMaker.getInstance().doSnapshot(getDisplaySurface(), WorkbenchHelper.displaySizeOf(surfaceComposite));
	}

}