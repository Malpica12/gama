/*********************************************************************************************
 *
 * 'GamlEditorBindings.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.core.lang.ui.editor;

import org.eclipse.swt.SWT;

import gama.ui.base.bindings.GamaKeyBindings;
import gama.ui.base.bindings.GamaKeyBindings.PluggableBinding;
import gama.ui.base.interfaces.IGamlEditor;
import gama.ui.base.utils.WorkbenchHelper;

/**
 * The class GamlEditorBindings.
 *
 * @author drogoul
 * @since 10 nov. 2016
 *
 */
public class GamlEditorBindings {

	public static final int MODIFIERS = SWT.SHIFT + SWT.ALT;

	public static void install() {

		GamaKeyBindings.plug(new PluggableBinding(SWT.MOD1, 'g') {

			@Override
			public void run() {
				final IGamlEditor editor = WorkbenchHelper.getActiveEditor();
				if (!(editor instanceof GamlEditor)) { return; }
				((GamlEditor) editor).doSearch();
			}
		});
		// for (int i = 0; i < 9; i++) {
		// GamaKeyBindings.plug(newBinding(i));
		// }
	}

	// private static PluggableBinding newExperimentBinding(final int index) {
	// return new PluggableBinding('0' + index, MODIFIERS) {
	//
	// @Override
	// public void run() {
	//
	// final IEditorPart editor = WorkbenchHelper.getActiveEditor();
	// if (!(editor instanceof GamlEditor)) { return; }
	// ((GamlEditor) editor).runExperiment(index);
	//
	// }
	// };
	// }

}
