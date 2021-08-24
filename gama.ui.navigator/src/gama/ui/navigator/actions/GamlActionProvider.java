/*******************************************************************************************************
 *
 * GamlActionProvider.java, in gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.navigator.actions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

import gama.runtime.GAMA;
import gama.ui.navigator.contents.WrappedExperimentContent;
import gama.ui.navigator.contents.WrappedSyntacticContent;

/**
 * The Class GamlActionProvider.
 */
public class GamlActionProvider extends CommonActionProvider {

	/** The selection. */
	WrappedSyntacticContent selection;
	
	/** The reveal action. */
	SelectionListenerAction runAction, revealAction;

	/**
	 * Instantiates a new gaml action provider.
	 */
	public GamlActionProvider() {}

	@Override
	public void init(final ICommonActionExtensionSite aSite) {
		super.init(aSite);
		makeActions();
	}

	/**
	 * Make actions.
	 */
	private void makeActions() {
		runAction = new SelectionListenerAction("Run...") {
			@Override
			public void run() {
				selection.handleDoubleClick();
			}

		};
		runAction.setId("run.experiment");
		runAction.setEnabled(true);
		revealAction = new SelectionListenerAction("Reveal...") {
			@Override
			public void run() {
				GAMA.getGui().editModel(null, selection.getElement().getElement());
			}
		};
		revealAction.setId("reveal.item");
		revealAction.setEnabled(true);
	}

	@Override
	public void fillContextMenu(final IMenuManager menu) {
		super.fillContextMenu(menu);
		if (selection == null)
			return;
		menu.add(new Separator());
		if (selection instanceof WrappedExperimentContent) {
			menu.appendToGroup("group.copy", runAction);
		}
		menu.appendToGroup("group.copy", revealAction);
	}

	@Override
	public void updateActionBars() {
		final StructuredSelection s = (StructuredSelection) getContext().getSelection();
		if (s.isEmpty()) {
			selection = null;
			return;
		}
		final Object o = s.getFirstElement();
		if (!(o instanceof WrappedSyntacticContent)) {
			selection = null;
			return;
		}
		selection = (WrappedSyntacticContent) o;
		runAction.selectionChanged(s);
		revealAction.selectionChanged(s);
	}

}
