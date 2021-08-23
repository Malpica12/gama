package gama.ext.serialize.gamaType.reference;

import java.util.ArrayList;
import java.util.List;

import gama.kernel.simulation.SimulationAgent;
import gama.metamodel.agent.IAgent;
import gama.metamodel.population.IPopulation;

public class ReferenceToAgent {
	List<String> species;
	List<Integer> index;

	private ReferenceToAgent() {
		species = new ArrayList<>();
		index = new ArrayList<>();
	}

	public ReferenceToAgent(final IAgent agt) {
		this();
		if (agt != null) {
			species.add(agt.getSpeciesName());
			index.add(agt.getIndex());

			IAgent host = agt.getHost();

			while (host != null && !(host instanceof SimulationAgent)) {
				species.add(host.getSpeciesName());
				index.add(host.getIndex());
				host = host.getHost();
			}
		}
	}

	@Override
	public String toString() {
		String res = "";

		for (int i = 0; i < species.size(); i++) {
			res = "/" + species.get(i) + index.get(i);
		}
		return res;
	}

	public IAgent getReferencedAgent(final SimulationAgent sim) {

		IPopulation<? extends IAgent> pop = sim.getPopulationFor(species.get(species.size() - 1));
		IAgent referencedAgt = pop.getAgent(index.get(index.size() - 1));

		for (int i = index.size() - 2; i >= 0; i--) {
			pop = sim.getPopulationFor(species.get(i));
			referencedAgt = pop.get(index.get(i));
		}

		return referencedAgt;
	}

}
