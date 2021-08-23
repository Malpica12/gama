/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.filter.IAgentFilter.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.metamodel.topology.filter;

import java.util.Collection;

import gama.metamodel.agent.IAgent;
import gama.metamodel.population.IPopulation;
import gama.metamodel.shape.IShape;
import gama.runtime.IScope;
import gama.util.IContainer;
import gaml.species.ISpecies;

public interface IAgentFilter {

	boolean hasAgentList();

	ISpecies getSpecies();

	IPopulation<? extends IAgent> getPopulation(IScope scope);

	IContainer<?, ? extends IAgent> getAgents(IScope scope);

	boolean accept(IScope scope, IShape source, IShape a);

	void filter(IScope scope, IShape source, Collection<? extends IShape> results);

}