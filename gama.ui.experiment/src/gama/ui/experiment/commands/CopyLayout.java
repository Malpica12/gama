/*******************************************************************************************************
 *
 * ummisco.gama.ui.commands.CopyLayout.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.experiment.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import gama.ui.base.utils.PerspectiveHelper;
import gama.ui.base.utils.WorkbenchHelper;
import gama.util.tree.GamaNode;
import gama.util.tree.GamaTree;

public class CopyLayout extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final GamaTree<String> tree =
				new LayoutTreeConverter().convertCurrentLayout(ArrangeDisplayViews.listDisplayViews());
		if (tree == null) { return this; }
		final GamaNode<String> firstSash = tree.getRoot().getChildren().get(0);
		firstSash.setWeight(null);
		final StringBuilder sb = new StringBuilder();
		sb.append(" layout " + firstSash);
		if (PerspectiveHelper.keepTabs() != null) {
			sb.append(" tabs:").append(PerspectiveHelper.keepTabs());
		}
		if (PerspectiveHelper.keepToolbars() != null) {
			sb.append(" toolbars:").append(PerspectiveHelper.keepToolbars());
		}
		if (PerspectiveHelper.keepControls() != null) {
			sb.append(" controls:").append(PerspectiveHelper.keepControls());
		}
		sb.append(" editors: ").append(WorkbenchHelper.getPage().isEditorAreaVisible()).append(";");
		WorkbenchHelper.copy(sb.toString());
		tree.dispose();
		return this;
	}

}
