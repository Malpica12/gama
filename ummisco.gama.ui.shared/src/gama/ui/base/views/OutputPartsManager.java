/*********************************************************************************************
 *
 * 'WorkaroundForIssue1353.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling
 * and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.ui.base.views;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import gama.common.ui.IGamaView;
import gama.outputs.IDisplayOutput;
import gama.ui.base.utils.WorkbenchHelper;

/**
 * Class OutputPartsManager.
 *
 * @author drogoul
 * @since 28 déc. 2015
 *
 */
public class OutputPartsManager {

	public static class PartListener implements IPartListener2 {

		@Override
		public void partActivated(final IWorkbenchPartReference partRef) {}

		@Override
		public void partClosed(final IWorkbenchPartReference partRef) {
			// DEBUG.LOG("Closed:" + partRef.getPartName());
			final IWorkbenchPart part = partRef.getPart(false);
			if (part instanceof IGamaView) {
				final IDisplayOutput output = ((IGamaView) part).getOutput();
				if (output != null) {
					output.setPaused(true);
					output.close();
				}
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPartReference partRef) {}

		@Override
		public void partOpened(final IWorkbenchPartReference partRef) {
			// DEBUG.LOG("Opened:" + partRef.getPartName());
			final IWorkbenchPart part = partRef.getPart(false);
			if (part instanceof IGamaView) {
				final IDisplayOutput output = ((IGamaView) part).getOutput();
				if (output != null) {
					if (!output.isOpen()) {
						output.open();
						output.setPaused(false);
					}
				}
			}

		}

		@Override
		public void partBroughtToTop(final IWorkbenchPartReference part) {}

		@Override
		public void partHidden(final IWorkbenchPartReference partRef) {}

		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {}

		@Override
		public void partInputChanged(final IWorkbenchPartReference partRef) {}
	}

	private final static PartListener listener = new PartListener();

	public static void install() {
		WorkbenchHelper.getPage().addPartListener(listener);

	}

}
