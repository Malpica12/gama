/*******************************************************************************************************
 *
 * GamlEditorBindings.java, in gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.modeling.editor;

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

	/** The Constant MODIFIERS. */
	public static final int MODIFIERS = SWT.SHIFT + SWT.ALT;

	/**
	 * Install.
	 */
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
