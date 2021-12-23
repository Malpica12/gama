/*******************************************************************************************************
 *
 * ConverterScope.java, in gama.ext.serialize, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.serialize.gamaType.converters;

import gama.kernel.simulation.SimulationAgent;
import gama.runtime.IScope;

/**
 * The Class ConverterScope.
 */
public class ConverterScope {
	
	/** The sim agt. */
	SimulationAgent simAgt;
	
	/** The scope. */
	IScope scope;
	
	/**
	 * Instantiates a new converter scope.
	 *
	 * @param s the s
	 */
	public ConverterScope(IScope s){
		scope = s;
		simAgt=null;
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public IScope getScope() { return scope; }
	
	/**
	 * Gets the simulation agent.
	 *
	 * @return the simulation agent
	 */
	public SimulationAgent getSimulationAgent() { return simAgt; }
	
	/**
	 * Sets the simulation agent.
	 *
	 * @param sim the new simulation agent
	 */
	public void setSimulationAgent(SimulationAgent sim){ simAgt = sim;}
}