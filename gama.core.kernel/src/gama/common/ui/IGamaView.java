/*******************************************************************************************************
 *
 * IGamaView.java, in gama.core.kernel, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

// TODO: Auto-generated Javadoc
/**
 * An abstract representation of the 'views', in a UI sense, that are used to display outputs or present information to
 * the user. A view can display one or several outputs (for instance, several monitors)
 *
 * @author drogoul
 */
public interface IGamaView {

	/**
	 * Hide toolbar.
	 */
	void hideToolbar();

	/**
	 * Show toolbar.
	 */
	void showToolbar();

	/**
	 * Update.
	 *
	 * @param output
	 *            the output
	 */
	void update(IDisplayOutput output);

	/**
	 * Adds the output.
	 *
	 * @param output
	 *            the output
	 */
	void addOutput(IDisplayOutput output);

	/**
	 * Removes the output.
	 *
	 * @param putput
	 *            the putput
	 */
	void removeOutput(IDisplayOutput putput);

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	IDisplayOutput getOutput();

	/**
	 * Close.
	 *
	 * @param scope
	 *            the scope
	 */
	void close(IScope scope);

	/**
	 * Change part name with simulation.
	 *
	 * @param agent
	 *            the agent
	 */
	void changePartNameWithSimulation(SimulationAgent agent);

	/**
	 * Reset.
	 */
	void reset();

	/**
	 * Gets the part name.
	 *
	 * @return the part name
	 */
	String getPartName();

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	void setName(String name);

	/**
	 * Update toolbar state.
	 */
	void updateToolbarState();

	/**
	 * The Interface Test.
	 */
	public interface Test {

		/**
		 * Adds the test result.
		 *
		 * @param summary
		 *            the summary
		 */
		void addTestResult(final CompoundSummary<?, ?> summary);

		/**
		 * Start new test sequence.
		 *
		 * @param all
		 *            the all
		 */
		void startNewTestSequence(boolean all);

		/**
		 * Display progress.
		 *
		 * @param number
		 *            the number
		 * @param total
		 *            the total
		 */
		void displayProgress(int number, int total);

		/**
		 * Finish test sequence.
		 */
		void finishTestSequence();

	}

	/**
	 * The Interface Display.
	 */
	public interface Display extends IGamaView {

		/**
		 * Contains point.
		 *
		 * @param x
		 *            the x
		 * @param y
		 *            the y
		 * @return true, if successful
		 */
		boolean containsPoint(int x, int y);

		/**
		 * Gets the display surface.
		 *
		 * @return the display surface
		 */
		IDisplaySurface getDisplaySurface();

		/**
		 * Toggle full screen.
		 */
		void toggleFullScreen();

		/**
		 * Checks if is full screen.
		 *
		 * @return true, if is full screen
		 */
		boolean isFullScreen();

		/**
		 * Toggle side controls.
		 */
		void toggleSideControls();

		/**
		 * Toggle overlay.
		 */
		void toggleOverlay();

		/**
		 * Show overlay.
		 */
		void showOverlay();

		/**
		 * Hide overlay.
		 */
		void hideOverlay();

		/**
		 * Gets the output.
		 *
		 * @return the output
		 */
		@Override
		LayeredDisplayOutput getOutput();

		/**
		 * Gets the index.
		 *
		 * @return the index
		 */
		int getIndex();

		/**
		 * Sets the index.
		 *
		 * @param i
		 *            the new index
		 */
		void setIndex(int i);

		/**
		 * Gets the camera names.
		 *
		 * @return the camera names
		 */
		default List<String> getCameraNames() { return Collections.EMPTY_LIST; }

		/**
		 * Take snapshot.
		 */
		void takeSnapshot();

		/**
		 * Hide canvas.
		 */
		default void hideCanvas() {}

		/**
		 * Show canvas.
		 */
		default void showCanvas() {}

		/**
		 * Focus canvas.
		 */
		default void focusCanvas() {}
	}

	/**
	 * The Interface Error.
	 */
	public interface Error {

		/**
		 * Display errors.
		 */
		void displayErrors();

	}

	/**
	 * The Interface Html.
	 */
	public interface Html {

		/**
		 * Sets the url.
		 *
		 * @param url
		 *            the new url
		 */
		void setUrl(String url);
	}

	/**
	 * The Interface Parameters.
	 */
	public interface Parameters {

		/**
		 * Adds the item.
		 *
		 * @param exp
		 *            the exp
		 */
		void addItem(IExperimentPlan exp);

		/**
		 * Update item values.
		 */
		void updateItemValues();
	}

	/**
	 * The Interface Console.
	 */
	public interface Console {

		/**
		 * Append.
		 *
		 * @param msg
		 *            the msg
		 * @param root
		 *            the root
		 * @param color
		 *            the color
		 */
		void append(String msg, ITopLevelAgent root, GamaColor color);

	}

	/**
	 * The Interface User.
	 */
	public interface User {

		/**
		 * Inits the for.
		 *
		 * @param scope
		 *            the scope
		 * @param panel
		 *            the panel
		 */
		void initFor(final IScope scope, final UserPanelStatement panel);
	}

}
