/*******************************************************************************************************
 *
 * TestModelsFolder.java, in gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.navigator.contents;

import gama.ui.base.utils.WorkbenchHelper;
import gaml.statements.test.CompoundSummary;

/**
 * The Class TestModelsFolder.
 */
public class TestModelsFolder extends TopLevelFolder {

	/**
	 * Instantiates a new test models folder.
	 *
	 * @param root the root
	 * @param name the name
	 */
	public TestModelsFolder(final NavigatorRoot root, final String name) {
		super(root, name, FOLDER_TEST, "navigator/folder.status.test", "Built-in tests", NEUTRAL,
				WorkbenchHelper.TEST_NATURE, Location.Tests);
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		final CompoundSummary<?, ?> summary = getManager().getTestsSummary();
		if (summary != null)
			sb.append(summary.getStringSummary());
		else {
			super.getSuffix(sb);
			sb.append(", not yet run");
		}

	}

}