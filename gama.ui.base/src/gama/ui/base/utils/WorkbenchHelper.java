/*********************************************************************************************
 *
 * 'WorkbenchHelper.java, in plugin gama.ui.base, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.base.utils;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.progress.UIJob;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import gama.common.ui.IGamaView;
import gama.common.ui.IGui;
import gama.core.dev.utils.DEBUG;
import gama.ui.base.interfaces.IGamlEditor;
import gama.ui.base.workspace.WorkspaceModelsManager;
import one.util.streamex.StreamEx;

public class WorkbenchHelper {

	static final Object NULL = new Object();

	public final static LoadingCache<Class<?>, Object> SERVICES =
			CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, Object>() {

				@Override
				public Object load(final Class<?> key) throws Exception {
					final Object o = getWorkbench().getService(key);
					if (o == null) return NULL;
					return o;
				}
			});

	public final static String GAMA_NATURE = WorkspaceModelsManager.GAMA_NATURE; // NO_UCD (unused code)
	public final static String XTEXT_NATURE = WorkspaceModelsManager.XTEXT_NATURE; // NO_UCD (unused code)
	public final static String PLUGIN_NATURE = WorkspaceModelsManager.PLUGIN_NATURE;
	public final static String TEST_NATURE = WorkspaceModelsManager.TEST_NATURE;
	public final static String BUILTIN_NATURE = WorkspaceModelsManager.BUILTIN_NATURE;

	private static Clipboard CLIPBOARD;
	private final static Transfer[] TRANSFERS = { TextTransfer.getInstance() };

	public static boolean isDisplayThread() {
		Display d = getDisplay();
		if (d == null) { d = Display.getCurrent(); }
		if (d == null) return false;
		return d.getThread() == Thread.currentThread();
	}

	public static Clipboard getClipboard() {
		if (CLIPBOARD == null) { CLIPBOARD = new Clipboard(getDisplay()); }
		return CLIPBOARD;
	}

	public static void asyncRun(final Runnable r) {
		final Display d = getDisplay();
		if (d != null && !d.isDisposed()) {
			d.asyncExec(r);
		} else {
			r.run();
		}
	}

	public static void run(final Runnable r) {
		final Display d = getDisplay();
		if (d == null || d.isDisposed() || d.getThread() == Thread.currentThread()) {
			r.run();
		} else {
			d.syncExec(r);
		}
	}

	public static Display getDisplay() {
		return getWorkbench().getDisplay();
	}

	public static IWorkbenchPage getPage() {
		final IWorkbenchWindow w = getWindow();
		if (w == null) return null;
		return w.getActivePage();
	}

	public static Shell getShell() {

		return getDisplay().getActiveShell();
	}

	public static WorkbenchWindow getWindow() {
		WorkbenchWindow w = null;
		try {
			w = (WorkbenchWindow) getWorkbench().getActiveWorkbenchWindow();
		} catch (final Exception e) {
			DEBUG.ERR("SWT bug: Window not found ");
		}
		if (w == null) {
			final IWorkbenchWindow[] windows = getWorkbench().getWorkbenchWindows();
			if (windows != null && windows.length > 0) return (WorkbenchWindow) windows[0];
		}
		return w;
	}

	public static IGamlEditor getActiveEditor() {
		final IWorkbenchPage page = getPage();
		if (page != null) {
			final IEditorPart editor = page.getActiveEditor();
			if (editor instanceof IGamlEditor) return (IGamlEditor) editor;
		}
		return null;
	}

	public static IWorkbenchPart getActivePart() {
		final IWorkbenchPage page = getPage();
		if (page != null) return page.getActivePart();
		return null;
	}

	public static IWorkbench getWorkbench() {
		return PlatformUI.getWorkbench();
	}

	public static IGamaView.Display findDisplay(final String id) {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return null;
		final IViewReference ref = page.findViewReference(id);
		if (ref == null) return null;
		final IViewPart view = ref.getView(false);
		if (view instanceof IGamaView.Display) return (IGamaView.Display) view;
		return null;
	}

	public static boolean isDisplay(final String id) {
		if (!id.startsWith(IGui.GL_LAYER_VIEW_ID) && !id.startsWith(IGui.LAYER_VIEW_ID)) return false;
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return false;
		final IViewReference ref = page.findViewReference(id);
		return ref != null;
		// final IViewPart view = ref.getView(false);
		// if (view instanceof IGamaView.Display) { return (IGamaView.Display) view; }
		// return <
	}

	public static IViewPart findView(final String id, final String second, final boolean restore) {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return null;
		final IViewReference ref = page.findViewReference(id, second);
		if (ref == null) return null;
		return ref.getView(restore);
	}

	public static List<IGamaView.Display> getDisplayViews() {
		final IWorkbenchPage page = WorkbenchHelper.getPage();
		if (page == null) return Collections.EMPTY_LIST;
		return StreamEx.of(page.getViewReferences()).map(v -> v.getView(false)).select(IGamaView.Display.class)
				.toList();
	}

	public static void setWorkbenchWindowTitle(final String title) {
		asyncRun(() -> {
			if (WorkbenchHelper.getShell() != null) { WorkbenchHelper.getShell().setText(title); }
		});

	}

	public static void hideView(final String id) {

		run(() -> {
			final IWorkbenchPage activePage = getPage();
			if (activePage == null) return;
			final IWorkbenchPart part = activePage.findView(id);
			if (part != null && activePage.isPartVisible(part)) { activePage.hideView((IViewPart) part); }
		});

	}

	public static void hideView(final IViewPart gamaViewPart) {
		final IWorkbenchPage activePage = getPage();
		if (activePage == null) return;
		activePage.hideView(gamaViewPart);

	}

	@SuppressWarnings ("unchecked")
	public static <T> T getService(final Class<T> class1) {
		final Object o = SERVICES.getUnchecked(class1);
		if (o == NULL) {
			SERVICES.invalidate(class1);
			return null;
		}
		return (T) o;
	}

	public static void copy(final String o) {
		final Runnable r = () -> getClipboard().setContents(new String[] { o }, TRANSFERS);
		// if (isDisplayThread()) {
		// r.run();
		// } else {
		asyncRun(r);
		// }
	}

	/**
	 * @todo find a more robust way to find the view (maybe with the control ?)
	 * @return
	 */
	public static IViewPart findFrontmostGamaViewUnderMouse() {
		final IWorkbenchPage page = getPage();
		if (page == null) return null;
		final Point p = getDisplay().getCursorLocation();
		final List<IGamaView.Display> displays = StreamEx.of(page.getViewReferences()).map(r -> r.getView(false))
				.filter(part -> page.isPartVisible(part)).select(IGamaView.Display.class)
				.filter(display -> display.containsPoint(p.x, p.y)).toList();
		if (displays.isEmpty()) return null;
		if (displays.size() == 1) return (IViewPart) displays.get(0);
		for (final IGamaView.Display display : displays) {
			if (display.isFullScreen()) return (IViewPart) display;
		}
		// Strange: n views, none of them fullscreen, claiming to contain the mouse pointer...
		return (IViewPart) displays.get(0);
	}

	public static Shell obtainFullScreenShell(final int id) {
		final Monitor[] monitors = WorkbenchHelper.getDisplay().getMonitors();
		int monitorId = id;
		if (monitorId < 0) { monitorId = 0; }
		if (monitorId > monitors.length - 1) { monitorId = monitors.length - 1; }
		final Rectangle bounds = monitors[monitorId].getBounds();

		final Shell fullScreenShell = new Shell(WorkbenchHelper.getDisplay(), SWT.NO_TRIM | SWT.ON_TOP);
		fullScreenShell.setBounds(bounds);
		final FillLayout fl = new FillLayout();
		fl.marginHeight = 0;
		fl.marginWidth = 0;
		fl.spacing = 0;
		// final GridLayout gl = new GridLayout(1, true);
		// gl.horizontalSpacing = 0;
		// gl.marginHeight = 0;
		// gl.marginWidth = 0;
		// gl.verticalSpacing = 0;
		fullScreenShell.setLayout(fl);
		return fullScreenShell;
	}

	public static Rectangle displaySizeOf(final Control composite) {
		final Rectangle[] result = new Rectangle[1];
		run(() -> result[0] = getDisplay().map(composite, null, composite.getBounds()));
		return result[0];
	}

	public static boolean runCommand(final String string) throws ExecutionException {
		return runCommand(string, null);
	}

	public static boolean executeCommand(final String string) {
		try {
			return runCommand(string, null);
		} catch (final ExecutionException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean runCommand(final String string, final Event event) throws ExecutionException {
		final Command c = getCommand(string);
		final IHandlerService handlerService = getService(IHandlerService.class);
		final ExecutionEvent e = handlerService.createExecutionEvent(c, event);
		return runCommand(c, e);
	}

	public static boolean runCommand(final Command c, final ExecutionEvent event) throws ExecutionException {
		if (c.isEnabled()) {
			try {
				c.executeWithChecks(event);
				return true;
			} catch (NotDefinedException | NotEnabledException | NotHandledException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static Command getCommand(final String string) {
		final ICommandService service = getService(ICommandService.class);
		return service.getCommand(string);
	}

	public static void runInUI(final String title, final int scheduleTime, final Consumer<IProgressMonitor> run) {
		final UIJob job = new UIJob(title) {

			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor) {

				run.accept(monitor);
				return Status.OK_STATUS;
			}

		};
		job.schedule(scheduleTime);
	}

}
