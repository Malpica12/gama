/*******************************************************************************************************
 *
 * Particle.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.kernel.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gama.kernel.experiment.BatchAgent;
import gama.kernel.experiment.IParameter;
import gama.kernel.experiment.ParametersSet;
import gama.metamodel.shape.GamaPoint;
import gama.runtime.IScope;
import gaml.operators.Cast;

// TODO: Auto-generated Javadoc
/**
 * Represents a particle from the Particle Swarm Optimization algorithm.
 */
class Particle {

	/** The position. */
	private final ParametersSet position;        // Current position.

	/** The velocity. */
	private ParametersSet velocity;

	/** The best position. */
	private ParametersSet bestPosition;    // Personal best solution.

	/** The best eval. */
	private double bestEval;        // Personal best value.

	/** The tested solutions. */
	protected HashMap<ParametersSet, Double> testedSolutions;

	/** The current experiment. */
	BatchAgent currentExperiment;

	/** The parameters. */
	final Map<String, GamaPoint> parameters;

	/** The algo. */
	ParamSpaceExploAlgorithm algo;

	/** The current val. */
	double currentVal;

	/**
	 * Construct a Particle with a random starting position.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @param algorithm the algorithm
	 * @param testedSolutionsMap the tested solutions map
	 */
	Particle (final IScope scope, final BatchAgent agent, final ParamSpaceExploAlgorithm algorithm, final HashMap<ParametersSet, Double> testedSolutionsMap) {
		currentExperiment = agent;
		algo = algorithm;
		this.testedSolutions = testedSolutionsMap;
		bestEval = algo.isMaximize() ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
		final List<IParameter.Batch> v = agent.getParametersToExplore();
		parameters = new HashMap<>();
		for (IParameter p : v ) {
			GamaPoint minMax = new GamaPoint(
					p.getMinValue(scope) != null ? Cast.asFloat(scope,p.getMinValue(scope)) : Double.NEGATIVE_INFINITY,
							p.getMaxValue(scope) != null ? Cast.asFloat(scope,p.getMaxValue(scope)) : Double.POSITIVE_INFINITY);
			parameters.put(p.getName(),minMax);
		}
		position = new ParametersSet(scope, v, true);
		velocity = new ParametersSet(scope, v, true);
		for (String key : velocity.keySet()) {
			velocity.put(key, Cast.asFloat(scope, velocity.get(key))-  Cast.asFloat(scope, position.get(key)) );
		}
		bestPosition = new ParametersSet(position);
	}

	/**
	 * The evaluation of the current position.
	 * @return      the evaluation
	 */
	public double eval () {
		Double fitness = testedSolutions.get(position);
		if (fitness == null) {
			fitness = currentExperiment.launchSimulationsWithSolution(position);
			testedSolutions.put(position, fitness);
		}
		return fitness;
	}

	/**
	 * Update the personal best if the current evaluation is better.
	 */
	void updatePersonalBest () {
		if (algo.isMaximize() && currentVal > bestEval
				|| !algo.isMaximize() && currentVal < bestEval) {
			bestEval = currentVal;
			bestPosition = new ParametersSet(position);
		}
	}

	/**
	 * Get the position of the particle.
	 * @return  the x position
	 */
	ParametersSet getPosition () {
		return position;
	}

	/**
	 * Getthe velocity of the particle.
	 * @return  the velocity
	 */
	ParametersSet getVelocity () {
		return velocity;
	}

	/**
	 * Get  the personal best solution.
	 * @return  the best position
	 */
	ParametersSet getBestPosition() {
		return bestPosition;
	}

	/**
	 * Get the value of the personal best solution.
	 * @return  the evaluation
	 */
	double getBestEval () {
		return bestEval;
	}

	/**
	 * Update the position of a particle by adding its velocity to its position.
	 *
	 * @param scope the scope
	 */
	void updatePosition (final IScope scope) {

		for (String key : position.keySet()) {
			GamaPoint p = parameters.get(key);
			double val =  Cast.asFloat(scope, position.get(key)) + Cast.asFloat(scope, velocity.get(key));
			val = Math.min(Math.max(val, Cast.asFloat(scope, p.x)), p.y);
			position.put(key, val );
		}
	}

	/**
	 * Set the velocity of the particle.
	 * @param velocity  the new velocity
	 */
	void setVelocity (final ParametersSet velocity) {
		this.velocity = velocity;
	}


}