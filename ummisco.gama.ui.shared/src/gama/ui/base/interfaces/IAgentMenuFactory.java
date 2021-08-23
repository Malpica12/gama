/*********************************************************************************************
 *
 * 'IAgentMenuFactory.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.ui.base.interfaces;

import java.util.Collection;

import org.eclipse.swt.widgets.Menu;

import gama.metamodel.agent.IAgent;
import gama.ui.base.menus.MenuAction;

public interface IAgentMenuFactory {

	void fillPopulationSubMenu(final Menu menu, final Collection<? extends IAgent> species,
			final MenuAction... actions);
}