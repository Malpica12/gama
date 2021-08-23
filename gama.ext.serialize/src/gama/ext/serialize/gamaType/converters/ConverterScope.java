/*********************************************************************************************
 *
 * 'ConverterScope.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.ext.serialize.gamaType.converters;

import gama.kernel.simulation.SimulationAgent;
import gama.runtime.IScope;

public class ConverterScope {
	SimulationAgent simAgt;
	IScope scope;
	
	public ConverterScope(IScope s){
		scope = s;
		simAgt=null;
	}

	public IScope getScope() { return scope; }
	public SimulationAgent getSimulationAgent() { return simAgt; }
	public void setSimulationAgent(SimulationAgent sim){ simAgt = sim;}
}
