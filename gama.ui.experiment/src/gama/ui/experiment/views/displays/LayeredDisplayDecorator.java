/*
 *
 */
package gama.ui.experiment.views.displays;

/*******************************************************************************************************
 *
 * LayeredDisplayDecorator.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

import static gama.ui.base.bindings.GamaKeyBindings.COMMAND;
import static gama.ui.base.bindings.GamaKeyBindings.format;
import static gama.ui.base.resources.IGamaIcons.DISPLAY_TOOLBAR_SNAPSHOT;
import static gama.ui.experiment.controls.SimulationSpeedContributionItem.create;
import static gama.ui.experiment.controls.SimulationSpeedContributionItem.totalWidth;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import gama.common.interfaces.IDisposable;
import gama.common.preferences.GamaPreferences;
import gama.common.ui.IDisplaySurface;
import gama.common.ui.IGui;
import gama.core.dev.utils.DEBUG;
import gama.outputs.LayeredDisplayData.Changes;
import gama.outputs.LayeredDisplayData.DisplayDataListener;
import gama.runtime.GAMA;
import gama.runtime.PlatformHelper;
import gama.ui.base.bindings.GamaKeyBindings;
import gama.ui.base.dialogs.Dialogs;
import gama.ui.base.resources.GamaColors;
import gama.ui.base.resources.GamaIcons;
import gama.ui.base.resources.IGamaIcons;
import gama.ui.base.toolbar.GamaCommand;
import gama.ui.base.toolbar.GamaToolbar2;
import gama.ui.base.toolbar.GamaToolbarFactory;
import gama.ui.base.utils.PerspectiveHelper;
import gama.ui.base.utils.ViewsHelper;
import gama.ui.base.utils.WorkbenchHelper;
import gama.ui.base.views.InteractiveConsoleView;

// TODO: Auto-generated Javadoc
/**
 * The Class LayeredDisplayDecorator.
 */
public class LayeredDisplayDecorator implements DisplayDataListener {

	static {
		DEBUG.ON();
	}

	/** The key and mouse listener. */
	protected IDisposable keyAndMouseListener;

	/** The menu manager. */
	protected DisplaySurfaceMenu menuManager;

	/** The view. */
	public final LayeredDisplayView view;

	/** The fs. */
	ToolItem fs = null;

	/** The normal parent of full screen control. */
	protected Composite normalParentOfFullScreenControl, normalParentOfToolbar;

	/** The side control weights. */
	int[] sideControlWeights = { 30, 70 };

	/** The full screen shell. */
	protected Shell fullScreenShell;

	/** The side panel. */
	protected Composite sidePanel;

	/** The overlay. */
	public DisplayOverlay overlay;

	/** The toolbar. */
	public GamaToolbar2 toolbar;

	/** The interactive console visible. */
	boolean isOverlayTemporaryVisible, sideControlsVisible, interactiveConsoleVisible;

	/** The perspective listener. */
	protected IPerspectiveListener perspectiveListener;

	/** The relaunch experiment. */
	GamaCommand toggleSideControls, toggleOverlay, takeSnapshot, toggleFullScreen, toggleInteractiveConsole,
			runExperiment, stepExperiment, closeExperiment, relaunchExperiment;

	/**
	 * Instantiates a new layered display decorator.
	 *
	 * @param view
	 *            the view
	 */
	LayeredDisplayDecorator(final LayeredDisplayView view) {
		this.view = view;
		createCommands();
		final IPartService ps = ((IWorkbenchPart) view).getSite().getService(IPartService.class);
		ps.addPartListener(overlayListener);

	}

	/**
	 * Creates the commands.
	 */
	private void createCommands() {
		toggleSideControls = new GamaCommand("display.layers2", "Toggle side controls " + format(COMMAND, 'L'),
				e -> toggleSideControls());
		toggleOverlay =
				new GamaCommand("display.overlay2", "Toggle overlay " + format(COMMAND, 'O'), e -> toggleOverlay());
		takeSnapshot =
				new GamaCommand(DISPLAY_TOOLBAR_SNAPSHOT, "Take a snapshot", "	", e -> SnapshotMaker.getInstance()
						.doSnapshot(view.getDisplaySurface(), WorkbenchHelper.displaySizeOf(view.surfaceComposite)));
		toggleFullScreen = new GamaCommand("display.fullscreen2", "Toggle fullscreen ESC", e -> toggleFullScreen());
		toggleInteractiveConsole = new GamaCommand("display.presentation2",
				"Toggle interactive console " + format(COMMAND, 'K'), e -> toggleInteractiveConsole());
		runExperiment = new GamaCommand(IGamaIcons.MENU_RUN_ACTION,
				"Run or pause experiment " + GamaKeyBindings.PLAY_STRING, e -> {
					final Item item = (Item) e.widget;
					if (!GAMA.isPaused()) {
						item.setImage(GamaIcons.create(IGamaIcons.MENU_RUN_ACTION).image());
					} else {
						item.setImage(GamaIcons.create("menu.pause4").image());
					}
					GAMA.startPauseFrontmostExperiment();

				});
		stepExperiment = new GamaCommand("menu.step4", "Step experiment " + GamaKeyBindings.STEP_STRING,
				e -> GAMA.stepFrontmostExperiment());
		closeExperiment = new GamaCommand("toolbar.stop2", "Closes experiment " + GamaKeyBindings.QUIT_STRING,
				e -> new Thread(() -> GAMA.closeAllExperiments(true, false)).start());
		relaunchExperiment = new GamaCommand("menu.reload4", "Reload experiment" + GamaKeyBindings.RELOAD_STRING,
				e -> GAMA.reloadFrontmostExperiment());
	}

	/** The overlay listener. */
	private final IPartListener2 overlayListener = new IPartListener2() {

		private boolean ok(final IWorkbenchPartReference partRef) {
			return partRef.getPart(false) == view && view.surfaceComposite != null
					&& !view.surfaceComposite.isDisposed() && !view.isFullScreen();
		}

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {

			if (ok(partRef)) {
				DEBUG.OUT("Part Activated:" + partRef.getTitle());
				WorkbenchHelper.asyncRun(() -> {
					if (overlay != null) { overlay.display(); }
					view.showCanvas();
				});
			}
		}

		@Override
		public void partClosed(final IWorkbenchPartReference partRef) {
			if (ok(partRef) && overlay != null) { overlay.close(); }
		}

		@Override
		public void partDeactivated(final IWorkbenchPartReference partRef) {

			if (ok(partRef) && !view.surfaceComposite.isVisible()) {
				DEBUG.OUT("Part Deactivated:" + partRef.getTitle());
				WorkbenchHelper.asyncRun(() -> {
					if (overlay != null) { overlay.hide(); }
					view.hideCanvas();
				});
			}
		}

		@Override
		public void partHidden(final IWorkbenchPartReference partRef) {
			// This event is wrongly sent when tabs are not displayed for the views

			if (ok(partRef) && !view.surfaceComposite.isVisible()) {
				DEBUG.OUT("Part hidden:" + partRef.getTitle());
				WorkbenchHelper.asyncRun(() -> {
					if (overlay != null) { overlay.hide(); }
					view.hideCanvas();
				});
			}
		}

		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {

			if (ok(partRef)) {
				DEBUG.OUT("Part Visible:" + partRef.getTitle());
				WorkbenchHelper.asyncRun(() -> {
					if (overlay != null) { overlay.display(); }
					view.showCanvas();
				});
			}
		}

	};

	/**
	 * Toggle full screen.
	 */
	public void toggleFullScreen() {
		if (isFullScreen()) {
			fs.setImage(GamaIcons.create("display.fullscreen2").image());
			if (interactiveConsoleVisible) { toggleInteractiveConsole(); }
			// Toolbar
			if (!toolbar.isDisposed()) {
				toolbar.wipe(SWT.LEFT, true);
				toolbar.setParent(normalParentOfToolbar);
				normalParentOfToolbar.layout(true, true);
			}
			view.getSash().setParent(normalParentOfFullScreenControl);
			createOverlay();
			normalParentOfFullScreenControl.layout(true, true);
			destroyFullScreenShell();
		} else {
			fs.setImage(GamaIcons.create("display.fullscreen3").image());
			fullScreenShell = createFullScreenShell();
			normalParentOfFullScreenControl = view.getSash().getParent();
			view.getSash().setParent(fullScreenShell);
			fullScreenShell.layout(true, true);
			fullScreenShell.setVisible(true);
			createOverlay();
			// Toolbar
			if (!toolbar.isDisposed()) {
				toolbar.wipe(SWT.LEFT, true);
				addFullscreenToolbarCommands();
				normalParentOfToolbar = toolbar.getParent();
				toolbar.setParent(fullScreenShell);
			}
		}
		toolbar.refresh(true);
		toolbar.getParent().layout(true, true);
		if (overlay.isVisible()) {
			WorkbenchHelper.runInUI("Overlay", 50, m -> {
				toggleOverlay();
				toggleOverlay();
			});
		}
		view.focusCanvas();
	}

	/**
	 * Toggle toolbar.
	 */
	public void toggleToolbar() {
		// If in fullscreen the hierarchy is simplified
		if (isFullScreen()) {
			boolean visible = toolbar.isVisible();
			toolbar.setVisible(!visible);
			((GridData) toolbar.getLayoutData()).exclude = visible;
		} else if (toolbar.isVisible()) {
			toolbar.hide();
		} else {
			toolbar.show();
		}
		toolbar.getParent().layout(true, true);
	}

	/**
	 * Adds the fullscreen toolbar commands.
	 */
	public void addFullscreenToolbarCommands() {
		toolbar.button(toggleSideControls, SWT.LEFT);
		toolbar.button(toggleOverlay, SWT.LEFT);
		toolbar.button(toggleInteractiveConsole, SWT.LEFT);
		toolbar.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.LEFT);
		final ToolItem item = toolbar.button(runExperiment, SWT.LEFT);
		if (GAMA.isPaused()) {
			item.setImage(GamaIcons.create(IGamaIcons.MENU_RUN_ACTION).image());
		} else {
			item.setImage(GamaIcons.create("menu.pause4").image());
		}
		toolbar.button(stepExperiment, SWT.LEFT);
		toolbar.control(create(toolbar.getToolbar(SWT.LEFT)), totalWidth(), SWT.LEFT);
		toolbar.button(relaunchExperiment, SWT.LEFT);
		toolbar.button(closeExperiment, SWT.LEFT);
	}

	/**
	 * Creates the overlay.
	 */
	public void createOverlay() {
		boolean wasVisible = false;
		if (overlay != null) {
			wasVisible = overlay.isVisible();
			overlay.dispose();
		}
		overlay = new DisplayOverlay(view, view.surfaceComposite, view.getOutput().getOverlayProvider());
		if (wasVisible) { overlay.setVisible(true); }

		if (overlay.isVisible()) {
			overlay.relocate();
			overlay.update();
		}
	}

	/**
	 * Creates the side panel.
	 *
	 * @param form
	 *            the form
	 */
	public void createSidePanel(final SashForm form) {
		sidePanel = new Composite(form, SWT.BORDER);
		final GridLayout layout = new GridLayout(1, true);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		sidePanel.setLayout(layout);
	}

	/**
	 * Creates the decorations.
	 *
	 * @param form
	 *            the form
	 */
	public void createDecorations(final SashForm form) {
		final LayerSideControls side = new LayerSideControls();
		side.fill(sidePanel, view);
		createOverlay();
		addPerspectiveListener();
		keyAndMouseListener = view.getMultiListener();
		menuManager = new DisplaySurfaceMenu(view.getDisplaySurface(), view.getParentComposite(), presentationMenu());
		final boolean tbVisible = view.getOutput().getData().isToolbarVisible();
		WorkbenchHelper.runInUI("Toolbar", 0, m -> {
			if (tbVisible) {
				toolbar.show();
			} else {
				toolbar.hide();
			}
		});
		if (view.getOutput().getData().fullScreen() > -1) {
			boolean toggle = true;
			if (GamaPreferences.Runtime.CORE_ASK_FULLSCREEN.getValue()) {
				toggle = Dialogs.question("Toggle fullscreen confirmation", "Do you want to go fullscreen ?");
			}
			if (toggle) { WorkbenchHelper.runInUI("Fullscreen", 100, m -> toggleFullScreen()); }
		}
	}

	/**
	 * Adds the perspective listener.
	 */
	private void addPerspectiveListener() {
		perspectiveListener = new IPerspectiveListener() {
			boolean previousState = false;

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
					final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if (PerspectiveHelper.PERSPECTIVE_MODELING_ID.equals(perspective.getId())) {
					if (view.getOutput() != null && view.getDisplaySurface() != null
							&& !GamaPreferences.Displays.CORE_DISPLAY_PERSPECTIVE.getValue()) {
						previousState = view.getOutput().isPaused();
						view.getOutput().setPaused(true);
					}
					// Seems necessary in addition to the IPartListener
					if (PlatformHelper.isMac()) {
						WorkbenchHelper.asyncRun(() -> {
							if (overlay != null) { overlay.hide(); }
							view.hideCanvas();
						});
					}
				} else {
					// Issue #2639
					if (PlatformHelper.isMac() && !view.isOpenGL()) {
						final IDisplaySurface ds = view.getDisplaySurface();
						if (ds != null) { ds.updateDisplay(true); }
					}
					if (!GamaPreferences.Displays.CORE_DISPLAY_PERSPECTIVE.getValue() && view.getOutput() != null
							&& view.getDisplaySurface() != null) {
						view.getOutput().setPaused(previousState);
					}
					// Seems necessary in addition to the IPartListener
					if (PlatformHelper.isMac()) {
						WorkbenchHelper.asyncRun(() -> {
							if (overlay != null) { overlay.display(); }
							view.showCanvas();
						});
					}
				}

			}
		};
		WorkbenchHelper.getWindow().addPerspectiveListener(perspectiveListener);
	}

	/**
	 * Checks if is full screen.
	 *
	 * @return true, if is full screen
	 */
	public boolean isFullScreen() { return fullScreenShell != null; }

	/**
	 * Creates the full screen shell.
	 *
	 * @return the shell
	 */
	private Shell createFullScreenShell() {
		final int monitorId = view.getOutput().getData().fullScreen();
		final Monitor[] monitors = WorkbenchHelper.getDisplay().getMonitors();
		int monitorId1 = Math.min(monitors.length - 1, Math.max(0, monitorId));
		final Rectangle bounds = monitors[monitorId1].getBounds();
		final Shell fullScreenShell = new Shell(WorkbenchHelper.getDisplay(), SWT.NO_TRIM | SWT.ON_TOP);
		fullScreenShell.setBounds(bounds);
		// For DEBUG purposes:
		// fullScreenShell.setBounds(new Rectangle(0, 0, bounds.width / 2, bounds.height / 2));
		fullScreenShell.setLayout(shellLayout());
		return fullScreenShell;
	}

	/**
	 * Shell layout.
	 *
	 * @return the layout used for the contents of the shell
	 */
	private GridLayout shellLayout() {
		final GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		final int margin = 0; // REDUCED_VIEW_TOOLBAR_HEIGHT.getValue() ? -1 : 0;
		layout.marginTop = margin;
		layout.marginBottom = margin;
		layout.marginHeight = margin;
		return layout;
	}

	/**
	 * Destroy full screen shell.
	 */
	private void destroyFullScreenShell() {
		if (fullScreenShell == null) return;
		fullScreenShell.close();
		fullScreenShell.dispose();
		fullScreenShell = null;
	}

	/** The display overlay. */
	protected Runnable displayOverlay = () -> {
		if (overlay == null) return;
		updateOverlay();
	};

	/**
	 * Update overlay.
	 */
	protected void updateOverlay() {
		if (overlay == null) return;
		if (view.forceOverlayVisibility()) {
			if (!overlay.isVisible()) {
				isOverlayTemporaryVisible = true;
				overlay.setVisible(true);
			}
		} else if (isOverlayTemporaryVisible) {
			isOverlayTemporaryVisible = false;
			overlay.setVisible(false);
		}
		if (overlay.isVisible()) { overlay.update(); }

	}

	/**
	 * Toggle overlay.
	 */
	public void toggleOverlay() {
		overlay.setVisible(!overlay.isVisible());
	}

	/**
	 * Toggle side controls.
	 */
	public void toggleSideControls() {
		SashForm display = view.getSash();
		if (sideControlsVisible) {
			sideControlWeights = display.getWeights();
			display.setMaximizedControl(view.getParentComposite()/* .getParent() */);
			display.layout(true, true);
			sideControlsVisible = false;
		} else {
			display.setWeights(sideControlWeights);
			display.setMaximizedControl(null);
			display.layout(true, true);
			sideControlsVisible = true;
		}
	}

	/**
	 * Toggle interactive console.
	 */
	public void toggleInteractiveConsole() {
		if (!sideControlsVisible) { toggleSideControls(); }
		final InteractiveConsoleView view =
				(InteractiveConsoleView) ViewsHelper.findView(IGui.INTERACTIVE_CONSOLE_VIEW_ID, null, true);
		if (view == null) return;
		if (interactiveConsoleVisible) {
			view.getControlToDisplayInFullScreen().setParent(view.getParentOfControlToDisplayFullScreen());
			view.getParentOfControlToDisplayFullScreen().layout();
			interactiveConsoleVisible = false;
		} else {
			view.getControlToDisplayInFullScreen().setParent(sidePanel);
			interactiveConsoleVisible = true;
		}
		sidePanel.layout(true, true);
	}

	/**
	 * Presentation menu.
	 *
	 * @return the menu manager
	 */
	private MenuManager presentationMenu() {
		final MenuManager mm = new MenuManager();

		mm.setMenuText("Presentation");
		mm.setImageDescriptor(GamaIcons.create("display.sidebar2").descriptor());
		mm.add(toggleSideControls.toAction());
		mm.add(toggleOverlay.toAction());
		mm.add(new Action("Toggle toolbar " + GamaKeyBindings.format(GamaKeyBindings.COMMAND, 'T'),
				GamaIcons.create("display.fullscreen.toolbar2").descriptor()) {

			@Override
			public boolean isEnabled() { return true; }

			@Override
			public void run() {
				toggleToolbar();
			}
		});
		return mm;
	}

	/**
	 * Creates the tool items.
	 *
	 * @param tb
	 *            the tb
	 */
	public void createToolItems(final GamaToolbar2 tb) {
		toolbar = tb;
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button(takeSnapshot, SWT.RIGHT);
		fs = tb.button(toggleFullScreen, SWT.RIGHT);
		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.menu(IGamaIcons.MENU_POPULATION, "Browse displayed agents by layers", "Browse through all displayed agents",
				trigger -> menuManager.buildToolbarMenu(trigger, (ToolItem) trigger.widget), SWT.RIGHT);
		tb.setBackgroundColor(GamaColors.get(view.getOutput().getData().getToolbarColor()).color());
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		// FIXME Remove the listeners
		try {
			WorkbenchHelper.getWindow().removePerspectiveListener(perspectiveListener);
			final IPartService ps = ((IWorkbenchPart) view).getSite().getService(IPartService.class);
			if (ps != null) { ps.removePartListener(overlayListener); }
		} catch (final Exception e) {

		}
		if (keyAndMouseListener != null) {
			keyAndMouseListener.dispose();
			keyAndMouseListener = null;
		}
		if (overlay != null) {
			overlay.close();
			overlay = null;
		}

		if (menuManager != null) {
			menuManager.disposeMenu();
			menuManager = null;
		}
		if (toolbar != null && !toolbar.isDisposed()) {
			toolbar.dispose();
			toolbar = null;
		}

		fs = null;
		normalParentOfToolbar = null;
		sidePanel = null;
		normalParentOfFullScreenControl = null;
		if (fullScreenShell != null && !fullScreenShell.isDisposed()) {
			fullScreenShell.dispose();
			fullScreenShell = null;
		}

	}

	/**
	 * Changed.
	 *
	 * @param changes
	 *            the changes
	 * @param value
	 *            the value
	 */
	@Override
	public void changed(final Changes changes, final Object value) {
		switch (changes) {
			case ZOOM:
				WorkbenchHelper.asyncRun(this::updateOverlay);
				break;
			default:
				break;
		}

	}

}