package gama.ext.serialize.gamaType.reference;

import java.util.ArrayList;

import gama.ext.serialize.gamaType.reduced.GamaPairReducer;
import gama.kernel.simulation.SimulationAgent;
import gama.util.GamaPair;
import gama.util.IReference;
import gaml.types.Types;

public class ReferencePair extends GamaPair<Object, Object> implements IReference {

	ArrayList<AgentAttribute> agtAttr;

	GamaPairReducer pairReducer;

	public ReferencePair(final GamaPairReducer p) {
		super(null, null, Types.NO_TYPE, Types.NO_TYPE);
		agtAttr = new ArrayList<>();
		pairReducer = p;
	}

	public GamaPairReducer getPairReducer() {
		return pairReducer;
	}

	@Override
	public Object constructReferencedObject(final SimulationAgent sim) {
		pairReducer.unreferenceReducer(sim);
		return pairReducer.constructObject();
	}

	@Override
	public ArrayList<AgentAttribute> getAgentAttributes() {
		return agtAttr;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		} else {
			return false;
		}
	}
}
