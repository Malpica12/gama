/*********************************************************************************************
 *
 * 'AddMonitorHandler.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.ui.experiment.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import gama.runtime.GAMA;
import gama.ui.experiment.views.inspectors.MonitorView;

public class AddMonitorHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		MonitorView.createNewMonitor(GAMA.getRuntimeScope());
		return null;
	}

}