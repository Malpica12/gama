/*******************************************************************************************************
 *
 * ValuedDisplayOutputFactory.java, in gama.core.kernel, is part of the source code of the GAMA modeling and simulation
 * platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.outputs;

import java.util.Collection;

import gama.kernel.experiment.ExperimentAgent;
import gama.kernel.experiment.IExperimentAgent;
import gama.kernel.model.GamlModelSpecies;
import gama.metamodel.agent.IAgent;
import gama.metamodel.agent.IMacroAgent;
import gama.metamodel.population.IPopulation;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.descriptions.SpeciesDescription;
import gaml.expressions.IExpression;
import gaml.species.ISpecies;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating ValuedDisplayOutput objects.
 */
public class ValuedDisplayOutputFactory {

	/**
	 * Browse.
	 *
	 * @param agents
	 *            the agents
	 */
	public static void browse(final Collection<? extends IAgent> agents) {
		IPopulation<? extends IAgent> pop = null;
		IMacroAgent root = null;
		if (agents instanceof IPopulation) {
			pop = (IPopulation<? extends IAgent>) agents;
			browse(pop.getHost(), pop.getSpecies());
		} else {
			for (final IAgent agent : agents) {
				final IPopulation<?> agentPop = agent.getPopulation();
				root = agentPop.getHost();
				if (root != null) { break; }
			}
			if (root == null) return;
			final IMacroAgent realRoot = findRootOf(root, agents);
			if (realRoot == null) {
				GamaRuntimeException.error("Impossible to find a common host agent for " + agents, root.getScope());
				return;
			}
			InspectDisplayOutput.browse(realRoot, agents, null).launch(realRoot.getScope());
		}
	}

	/**
	 * Find root of.
	 *
	 * @param root
	 *            the root
	 * @param agents
	 *            the agents
	 * @return the i macro agent
	 */
	private static IMacroAgent findRootOf(final IMacroAgent root, final Collection<? extends IAgent> agents) {
		if (agents instanceof IPopulation) return ((IPopulation<? extends IAgent>) agents).getHost();
		IMacroAgent result = null;
		for (final IAgent a : agents) {
			if (result == null) {
				result = a.getHost();
			} else if (a.getHost() != result) return null;
		}
		return result;

	}

	/**
	 * Browse.
	 *
	 * @param root
	 *            the root
	 * @param species
	 *            the species
	 */
	public static void browse(final IMacroAgent root, final ISpecies species) {
		if (root instanceof IExperimentAgent && species instanceof GamlModelSpecies) {
			// special case to be able to browse simulations, as their species is not contained in the experiment
			// species
			InspectDisplayOutput.browse(root, species, null).launch(root.getScope());
			return;
		}
		if (!root.getSpecies().getMicroSpecies().contains(species)) {
			if (root instanceof ExperimentAgent) {
				final IMacroAgent realRoot = ((ExperimentAgent) root).getSimulation();
				browse(realRoot, species);
			} else {
				GamaRuntimeException.error("Agent " + root + " has no access to populations of " + species.getName(),
						root.getScope());
			}
			return;
		}
		InspectDisplayOutput.browse(root, species, null).launch(root.getScope());
	}

	/**
	 * Browse.
	 *
	 * @param root            the root
	 * @param expr            the expr
	 * @param attributes the attributes
	 */
	public static void browse(final IMacroAgent root, final IExpression expr, final IExpression attributes) {
		final SpeciesDescription species = expr.getGamlType().isContainer()
				? expr.getGamlType().getContentType().getSpecies() : expr.getGamlType().getSpecies();
		if (species == null) {
			GamaRuntimeException.error("Expression '" + expr.serialize(true) + "' does not reference agents",
					root.getScope());
			return;
		}
		final ISpecies rootSpecies = root.getSpecies();
		if (rootSpecies.getMicroSpecies(species.getName()) == null) {
			if (root instanceof ExperimentAgent) {
				final IMacroAgent realRoot = ((ExperimentAgent) root).getSimulation();
				browse(realRoot, expr, attributes);
			} else {
				GamaRuntimeException.error("Agent " + root + " has no access to populations of " + species.getName(),
						root.getScope());
			}
			return;
		}
		InspectDisplayOutput.browse(root, expr, attributes).launch(root.getScope());
	}

	/**
	 * Browse simulations.
	 *
	 * @param host
	 *            the host
	 */
	public static void browseSimulations(final ExperimentAgent host) {
		InspectDisplayOutput.inspect(host, null).launch(host.getScope());
	}

}
