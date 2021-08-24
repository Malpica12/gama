/*******************************************************************************************************
 *
 * msi.gama.common.interfaces.IGamaView.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.common.ui;

import java.util.Collections;
import java.util.List;

import gama.kernel.experiment.IExperimentPlan;
import gama.kernel.experiment.ITopLevelAgent;
import gama.kernel.simulation.SimulationAgent;
import gama.outputs.IDisplayOutput;
import gama.outputs.LayeredDisplayOutput;
import gama.runtime.IScope;
import gama.util.GamaColor;
import gaml.architecture.user.UserPanelStatement;
import gaml.statements.test.CompoundSummary;

/**
 * An abstract representation of the 'views', in a UI sense, that are used to display outputs or present information to
 * the user. A view can display one or several outputs (for instance, several monitors)
 *
 * @author drogoul
 */
public interface IGamaView {

	void update(IDisplayOutput output);

	void addOutput(IDisplayOutput output);

	void removeOutput(IDisplayOutput putput);

	IDisplayOutput getOutput();

	void close(IScope scope);

	void changePartNameWithSimulation(SimulationAgent agent);

	void reset();

	String getPartName();

	void setName(String name);

	void updateToolbarState();

	public interface Test {
		void addTestResult(final CompoundSummary<?, ?> summary);

		void startNewTestSequence(boolean all);

		void displayProgress(int number, int total);

		void finishTestSequence();

	}

	public interface Display extends IGamaView {

		boolean containsPoint(int x, int y);

		IDisplaySurface getDisplaySurface();

		void toggleFullScreen();

		boolean isFullScreen();

		void toggleSideControls();

		void toggleOverlay();

		void showOverlay();

		void hideOverlay();

		void hideToolbar();

		void showToolbar();

		@Override
		LayeredDisplayOutput getOutput();

		int getIndex();

		void setIndex(int i);

		default List<String> getCameraNames() {
			return Collections.EMPTY_LIST;
		}

		void takeSnapshot();
	}

	public interface Error {

		void displayErrors();

	}

	public interface Html {
		void setUrl(String url);
	}

	public interface Parameters {
		void addItem(IExperimentPlan exp);

		void updateItemValues();
	}

	public interface Console {

		void append(String msg, ITopLevelAgent root, GamaColor color);

	}

	public interface User {
		void initFor(final IScope scope, final UserPanelStatement panel);
	}

}