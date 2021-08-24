/*******************************************************************************************************
 *
 * WorkaroundForIssue1594.java, in gama.display.java2d, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.display.java2d;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPartReference;

import gama.ui.base.utils.WorkbenchHelper;

/**
 * The Class WorkaroundForIssue1594.
 */
public class WorkaroundForIssue1594 {

	/**
	 * Install on.
	 *
	 * @param view the view
	 * @param parent the parent
	 * @param surfaceComposite the surface composite
	 * @param displaySurface the display surface
	 */
	public static void installOn(final AWTDisplayView view, final Composite parent, final Composite surfaceComposite,
			final Java2DDisplaySurface displaySurface) {
		// Install only on Windows
		if (!gama.ui.base.utils.PlatformHelper.isWindows()) { return; }
		final IPartService ps = view.getSite().getService(IPartService.class);
		ps.addPartListener(new IPartListener2() {

			@Override
			public void partActivated(final IWorkbenchPartReference partRef) {
				final IPartListener2 listener = this;
				// Fix for Issue #1594
				if (partRef.getPart(false).equals(view)) {
					// AD: Reworked to address Issue 535. It seems necessary to
					// read the size of the composite inside an SWT
					// thread and run the sizing inside an AWT thread
					WorkbenchHelper.asyncRun(() -> {
						if (parent.isDisposed()) { return; }

						final org.eclipse.swt.graphics.Rectangle r = parent.getBounds();
						java.awt.EventQueue.invokeLater(() -> {
							if (surfaceComposite == null) { return; }
							displaySurface.setBounds(r.x, r.y, r.width, r.height);
							WorkbenchHelper.asyncRun(() -> {
								view.getSash().setMaximizedControl(null);
								ps.removePartListener(listener);
								view.getSash().setMaximizedControl(parent.getParent());
							});
						});

					});
				}

			}

			@Override
			public void partClosed(final IWorkbenchPartReference partRef) {}

			@Override
			public void partDeactivated(final IWorkbenchPartReference partRef) {}

			@Override
			public void partOpened(final IWorkbenchPartReference partRef) {}

			@Override
			public void partBroughtToTop(final IWorkbenchPartReference part) {}

			@Override
			public void partHidden(final IWorkbenchPartReference partRef) {}

			@Override
			public void partVisible(final IWorkbenchPartReference partRef) {}

			@Override
			public void partInputChanged(final IWorkbenchPartReference partRef) {}
		});

	}

}
