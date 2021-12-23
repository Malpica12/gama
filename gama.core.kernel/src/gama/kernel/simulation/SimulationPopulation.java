/*******************************************************************************************************
 *
 * SimulationPopulation.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.kernel.simulation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gama.common.interfaces.IKeyword;
import gama.kernel.experiment.ExperimentAgent;
import gama.kernel.experiment.ExperimentPlan;
import gama.metamodel.agent.IAgent;
import gama.metamodel.agent.SavedAgent;
import gama.metamodel.population.GamaPopulation;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.topology.continuous.AmorphousTopology;
import gama.runtime.IScope;
import gama.runtime.concurrent.GamaExecutorService;
import gama.runtime.concurrent.GamaExecutorService.Caller;
import gama.runtime.concurrent.SimulationRunner;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaListFactory;
import gama.util.IList;
import gaml.compilation.IAgentConstructor;
import gaml.species.ISpecies;
import gaml.statements.RemoteSequence;
import gaml.variables.IVariable;

// TODO: Auto-generated Javadoc
/**
 * The Class SimulationPopulation.
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class SimulationPopulation extends GamaPopulation<SimulationAgent> {

	/** The current simulation. */
	private SimulationAgent currentSimulation;

	/** The runner. */
	private final SimulationRunner runner;

	/**
	 * Instantiates a new simulation population.
	 *
	 * @param agent the agent
	 * @param species the species
	 */
	public SimulationPopulation(final ExperimentAgent agent, final ISpecies species) {
		super(agent, species);
		runner = SimulationRunner.of(this);
	}

	/**
	 * Gets the max number of concurrent simulations.
	 *
	 * @return the max number of concurrent simulations
	 */
	public int getMaxNumberOfConcurrentSimulations() {
		return GamaExecutorService.getParallelism(getHost().getScope(), getSpecies().getConcurrency(),
				Caller.SIMULATION);
	}

	/**
	 * Method fireAgentRemoved().
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @see gama.metamodel.population.GamaPopulation#fireAgentRemoved(gama.metamodel.agent.IAgent)
	 */
	@Override
	protected void fireAgentRemoved(final IScope scope, final IAgent agent) {
		super.fireAgentRemoved(scope, agent);
		runner.remove((SimulationAgent) agent);
	}

	/**
	 * Initialize for.
	 *
	 * @param scope the scope
	 */
	@Override
	public void initializeFor(final IScope scope) {
		super.initializeFor(scope);
		this.currentAgentIndex = 0;
	}

	/**
	 * Dispose.
	 */
	@Override
	public void dispose() {
		runner.dispose();
		currentSimulation = null;
		super.dispose();
	}

	/**
	 * Iterable.
	 *
	 * @param scope the scope
	 * @return the iterable
	 */
	@Override
	public Iterable<SimulationAgent> iterable(final IScope scope) {
		return (Iterable<SimulationAgent>) getAgents(scope);
	}

	/**
	 * Creates the agents.
	 *
	 * @param scope the scope
	 * @param number the number
	 * @param initialValues the initial values
	 * @param isRestored the is restored
	 * @param toBeScheduled the to be scheduled
	 * @param sequence the sequence
	 * @return the i list
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@Override
	public IList<SimulationAgent> createAgents(final IScope scope, final int number,
			final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
					final boolean toBeScheduled, final RemoteSequence sequence) throws GamaRuntimeException {
		final IList<SimulationAgent> result = GamaListFactory.create(SimulationAgent.class);

		for (int i = 0; i < number; i++) {
			scope.getGui().getStatus(scope).waitStatus("Initializing simulation");
			// Model do not only rely on SimulationAgent
			final IAgentConstructor<SimulationAgent> constr = species.getDescription().getAgentConstructor();

			currentSimulation = constr.createOneAgent(this, currentAgentIndex++);
			currentSimulation.setScheduled(toBeScheduled);
			currentSimulation.setName("Simulation " + currentSimulation.getIndex());
			add(currentSimulation);
			currentSimulation.setOutputs(((ExperimentPlan) host.getSpecies()).getOriginalSimulationOutputs());
			if (scope.interrupted()) return null;
			initSimulation(scope, currentSimulation, initialValues, i, isRestored, toBeScheduled, sequence);
			if (toBeScheduled) { runner.add(currentSimulation); }
			result.add(currentSimulation);
		}
		// Linked to Issue #2430. Should not return this, but the newly created simulations
		// return this;
		return result;
	}

	/**
	 * Inits the simulation.
	 *
	 * @param scope the scope
	 * @param sim the sim
	 * @param initialValues the initial values
	 * @param index the index
	 * @param isRestored the is restored
	 * @param toBeScheduled the to be scheduled
	 * @param sequence the sequence
	 */
	private void initSimulation(final IScope scope, final SimulationAgent sim,
			final List<? extends Map<String, Object>> initialValues, final int index, final boolean isRestored,
					final boolean toBeScheduled, final RemoteSequence sequence) {
		scope.getGui().getStatus(scope).waitStatus("Instantiating agents");
		if (toBeScheduled) { sim.prepareGuiForSimulation(scope); }

		final Map<String, Object> firstInitValues = initialValues.isEmpty() ? null : initialValues.get(index);
		final Object firstValue =
				firstInitValues != null && !firstInitValues.isEmpty() ? firstInitValues.values().toArray()[0] : null;
		if (firstValue instanceof SavedAgent) {
			sim.updateWith(scope, (SavedAgent) firstValue);
		} else {
			createVariablesFor(sim.getScope(), Collections.singletonList(sim), Arrays.asList(firstInitValues));
		}

		if (toBeScheduled) {
			if (isRestored || firstValue instanceof SavedAgent) {
				sim.initOutputs();
			} else {
				sim.schedule(scope);
				if (sequence != null && !sequence.isEmpty()) { scope.execute(sequence, sim, null); }
			}
		}
	}

	/**
	 * Allow var init to be overriden by external init.
	 *
	 * @param var the var
	 * @return true, if successful
	 */
	@Override
	protected boolean allowVarInitToBeOverridenByExternalInit(final IVariable var) {
		switch (var.getName()) {
			case IKeyword.SEED:
			case IKeyword.RNG:
				return !var.hasFacet(IKeyword.INIT);
			default:
				return true;
		}
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	@Override
	public ExperimentAgent getHost() {
		return (ExperimentAgent) super.getHost();
	}

	/**
	 * Gets the agent.
	 *
	 * @param scope the scope
	 * @param value the value
	 * @return the agent
	 */
	@Override
	public SimulationAgent getAgent(final IScope scope, final GamaPoint value) {
		return get(null, 0);
	}

	/**
	 * Sets the host.
	 *
	 * @param agent the new host
	 */
	public void setHost(final ExperimentAgent agent) {
		host = agent;
	}

	/**
	 * Compute topology.
	 *
	 * @param scope the scope
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@Override
	public void computeTopology(final IScope scope) throws GamaRuntimeException {
		// Temporary topology set before the world gets a shape
		topology = new AmorphousTopology();
	}

	/**
	 * Step agents.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 */
	@Override
	protected boolean stepAgents(final IScope scope) {
		runner.step();
		return true;
	}

	/**
	 * This method can be called by the batch experiments to temporarily stop (unschedule) a simulation.
	 *
	 * @param sim the sim
	 */
	public void unscheduleSimulation(final SimulationAgent sim) {
		runner.remove(sim);
	}

	/**
	 * Gets the number of active threads.
	 *
	 * @return the number of active threads
	 */
	public int getNumberOfActiveThreads() {
		return runner.getActiveThreads();
	}

	/**
	 * Checks for scheduled simulations.
	 *
	 * @return true, if successful
	 */
	public boolean hasScheduledSimulations() {
		return runner.hasSimulations();
	}

	/**
	 * Last simulation created.
	 *
	 * @return the simulation agent
	 */
	public SimulationAgent lastSimulationCreated() {
		return currentSimulation;
	}

}