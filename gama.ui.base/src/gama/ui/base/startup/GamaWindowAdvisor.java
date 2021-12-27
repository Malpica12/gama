/*******************************************************************************************************
 *
 * GamaWindowAdvisor.java, in gama.ui.base, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.base.startup;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.internal.ide.application.IDEWorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;

import gama.common.preferences.GamaPreferences;
import gama.core.dev.utils.FLAGS;
import gama.runtime.GAMA;
import gama.runtime.ISimulationStateProvider;
import gama.ui.base.utils.PerspectiveHelper;
import gama.ui.base.utils.ThemeHelper;

// TODO: Auto-generated Javadoc
/**
 * The Class GamaWindowAdvisor.
 */
public class GamaWindowAdvisor extends IDEWorkbenchWindowAdvisor {

	/**
	 * Creates the action bar advisor.
	 *
	 * @param configurer
	 *            the configurer
	 * @return the action bar advisor
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new GamaActionBarAdvisor(configurer);
	}

	/**
	 * Instantiates a new gama window advisor.
	 *
	 * @param adv
	 *            the adv
	 * @param configurer
	 *            the configurer
	 */
	public GamaWindowAdvisor(final GamaWorkbenchAdvisor adv, final IWorkbenchWindowConfigurer configurer) {
		super(adv, configurer);

		// Hack and workaround for the inability to find launcher icons...

		final Bundle bundle = Platform.getBundle("gama.core.application");

		final ImageDescriptor myImage =
				ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("branding_icons/icon256.png"), null));
		configurer.getWindow().getShell().setImage(myImage.createImage());
	}

	/**
	 * Pre window open.
	 */
	@Override
	public void preWindowOpen() {
		super.preWindowOpen();
		final IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		configurer.getWindow().addPerspectiveListener(new IPerspectiveListener() {

			@Override
			public void perspectiveChanged(final IWorkbenchPage page, final IPerspectiveDescriptor perspective,
					final String changeId) {}

			@Override
			public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
				if (PerspectiveHelper.isSimulationPerspective()) {
					// DEBUG.OUT("Running the perspective listener to automatically launch modeling");
					final IPerspectiveDescriptor desc = page.getPerspective();
					page.closePerspective(desc, false, false);
					PerspectiveHelper.openModelingPerspective(true, false);
				}
				configurer.getWindow().removePerspectiveListener(this);

			}
		});
		configurer.getWindow().addPageListener(new IPageListener() {

			@Override
			public void pageActivated(final IWorkbenchPage page) {
				configurer.getWindow().removePageListener(this);
				PerspectiveHelper.openModelingPerspective(true, false);
			}

			@Override
			public void pageClosed(final IWorkbenchPage page) {}

			@Override
			public void pageOpened(final IWorkbenchPage page) {}
		});
		// See #3187 -
		if (FLAGS.USE_OLD_TABS) {
			ThemeHelper.injectCSS(".MPartStack {\n" + " swt-tab-renderer: null;\n" + " swt-simple: true;\n" + "}");
		}
		configurer.setShowMenuBar(true);
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		configurer.setShowPerspectiveBar(false);
		configurer.setTitle(GAMA.VERSION);
		PlatformUI.getPreferenceStore().setValue(IWorkbenchPreferenceConstants.SHOW_MEMORY_MONITOR, true);
		Resource.setNonDisposeHandler(null);
	}

	/**
	 * Post window restore.
	 *
	 * @throws WorkbenchException
	 *             the workbench exception
	 */
	@Override
	public void postWindowRestore() throws WorkbenchException {}

	/**
	 * Post window create.
	 */
	@Override
	public void postWindowCreate() {
		final IWorkbenchWindow window = getWindowConfigurer().getWindow();
		window.getShell().setMaximized(GamaPreferences.Interface.CORE_SHOW_MAXIMIZED.getValue());
		if (FLAGS.USE_DELAYED_RESIZE) {
			window.getShell().addControlListener(new ControlAdapter() {

				@Override
				public void controlResized(final ControlEvent e) {
					window.getShell().requestLayout();
				}

			});
		}
	}

	/**
	 * Post window open.
	 */
	@Override
	public void postWindowOpen() {
		PerspectiveHelper.cleanPerspectives();
		GAMA.getGui().openWelcomePage(true);
		GAMA.getGui().updateExperimentState(null, ISimulationStateProvider.NONE);
	}

}
