/*******************************************************************************************************
 *
 * IExperimentPlan.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.kernel.experiment;

import java.util.Map;

import gama.kernel.batch.IExploration;
import gama.kernel.experiment.IParameter.Batch;
import gama.kernel.model.IModel;
import gama.kernel.simulation.SimulationAgent;
import gama.outputs.FileOutput;
import gama.outputs.IOutputManager;
import gama.runtime.IScope;
import gaml.descriptions.ExperimentDescription;
import gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 31 mai 2011.
 *
 * @todo Description
 */
public interface IExperimentPlan extends ISpecies {

	/** The batch category name. */
	String BATCH_CATEGORY_NAME = "Exploration method";
	
	/** The test category name. */
	String TEST_CATEGORY_NAME = "Configuration of tests";
	
	/** The explorable category name. */
	String EXPLORABLE_CATEGORY_NAME = "Parameters to explore";
	
	/** The system category prefix. */
	String SYSTEM_CATEGORY_PREFIX = "Random number generation";

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	IModel getModel();

	/**
	 * Sets the model.
	 *
	 * @param model the new model
	 */
	void setModel(final IModel model);

	/**
	 * Gets the original simulation outputs.
	 *
	 * @return the original simulation outputs
	 */
	IOutputManager getOriginalSimulationOutputs();

	/**
	 * Refresh all outputs.
	 */
	void refreshAllOutputs();

	/**
	 * Pause all outputs.
	 */
	void pauseAllOutputs();

	/**
	 * Resume all outputs.
	 */
	void resumeAllOutputs();

	/**
	 * Synchronize all outputs.
	 */
	void synchronizeAllOutputs();

	/**
	 * Un synchronize all outputs.
	 */
	void unSynchronizeAllOutputs();

	/**
	 * Close all outputs.
	 */
	void closeAllOutputs();

	/**
	 * Gets the experiment outputs.
	 *
	 * @return the experiment outputs
	 */
	IOutputManager getExperimentOutputs();

	/**
	 * Checks if is gui.
	 *
	 * @return true, if is gui
	 */
	boolean isGui();

	/**
	 * Checks for parameter.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	boolean hasParameter(String name);

	/**
	 * Gets the agent.
	 *
	 * @return the agent
	 */
	ExperimentAgent getAgent();

	/**
	 * Gets the experiment scope.
	 *
	 * @return the experiment scope
	 */
	IScope getExperimentScope();

	/**
	 * Open.
	 */
	void open();

	/**
	 * Reload.
	 */
	void reload();

	/**
	 * Gets the current simulation.
	 *
	 * @return the current simulation
	 */
	SimulationAgent getCurrentSimulation();

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	Map<String, IParameter> getParameters();

	/**
	 * Gets the exploration algorithm.
	 *
	 * @return the exploration algorithm
	 */
	IExploration getExplorationAlgorithm();

	/**
	 * Gets the log.
	 *
	 * @return the log
	 */
	FileOutput getLog();

	/**
	 * Checks if is batch.
	 *
	 * @return true, if is batch
	 */
	boolean isBatch();

	/**
	 * Checks if is memorize.
	 *
	 * @return true, if is memorize
	 */
	boolean isMemorize();

	/**
	 * Gets the explorable parameters.
	 *
	 * @return the explorable parameters
	 */
	Map<String, Batch> getExplorableParameters();

	/**
	 * Gets the controller.
	 *
	 * @return the controller
	 */
	IExperimentController getController();

	/**
	 * Checks if is headless.
	 *
	 * @return true, if is headless
	 */
	boolean isHeadless();

	/**
	 * Sets the headless.
	 *
	 * @param headless the new headless
	 */
	void setHeadless(boolean headless);

	/**
	 * Gets the experiment type.
	 *
	 * @return the experiment type
	 */
	String getExperimentType();

	/**
	 * Keeps seed.
	 *
	 * @return true, if successful
	 */
	boolean keepsSeed();

	/**
	 * Keeps simulations.
	 *
	 * @return true, if successful
	 */
	boolean keepsSimulations();

	/**
	 * Checks for parameters or user commands.
	 *
	 * @return true, if successful
	 */
	boolean hasParametersOrUserCommands();

	/**
	 * Recompute and refresh all outputs.
	 */
	void recomputeAndRefreshAllOutputs();

	/**
	 * Gets the active output managers.
	 *
	 * @return the active output managers
	 */
	Iterable<IOutputManager> getActiveOutputManagers();

	/**
	 * Checks if is autorun.
	 *
	 * @return true, if is autorun
	 */
	boolean isAutorun();

	/**
	 * Checks if is test.
	 *
	 * @return true, if is test
	 */
	boolean isTest();

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Override
	ExperimentDescription getDescription();

	/**
	 * Should be benchmarked.
	 *
	 * @return true, if successful
	 */
	boolean shouldBeBenchmarked();

}