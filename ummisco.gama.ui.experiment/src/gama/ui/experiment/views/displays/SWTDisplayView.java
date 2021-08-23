/*********************************************************************************************
 *
 * 'SWTLayeredDisplayView.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.experiment.views.displays;

import org.eclipse.swt.widgets.Control;

import gama.runtime.IScope;
import gama.ui.base.utils.WorkbenchHelper;

/**
 * Class OpenGLLayeredDisplayView.
 *
 * @author drogoul
 * @since 25 mars 2015
 *
 */
public abstract class SWTDisplayView extends LayeredDisplayView {

	@Override
	public Control[] getZoomableControls() {
		return new Control[] { surfaceComposite };
	}

	@Override
	public void setFocus() {
		if (surfaceComposite != null && !surfaceComposite.isDisposed() && !surfaceComposite.isFocusControl()) {
			surfaceComposite.forceFocus();
		}
	}

	@Override
	public void close(final IScope scope) {

		WorkbenchHelper.asyncRun(() -> {
			try {
				if (getDisplaySurface() != null) {
					getDisplaySurface().dispose();
				}
				if (getSite() != null && getSite().getPage() != null) {
					getSite().getPage().hideView(SWTDisplayView.this);
				}
			} catch (final Exception e) {}
		});

	}

}
