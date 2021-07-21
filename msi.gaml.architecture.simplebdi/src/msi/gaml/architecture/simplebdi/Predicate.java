/*********************************************************************************************
 *
 *
 * 'Predicate.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.architecture.simplebdi;

import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

@vars ({ @variable (
		name = "name",
		type = IType.STRING,
		doc = @doc ("the name of the predicate")),
		@variable (
				name = "is_true",
				type = IType.BOOL,
				doc = @doc ("the truth value of the predicate")),
		@variable (
				name = "values",
				type = IType.MAP,
				doc = @doc ("the values attached to the predicate")),
		@variable (
				name = "date",
				type = IType.FLOAT,
				doc = @doc ("the date of the predicate")),
		@variable (
				name = "subintentions",
				type = IType.LIST,
				doc = @doc ("the subintentions of the predicate")),
		@variable (
				name = "on_hold_until",
				type = IType.NONE,
				doc = @doc ("the list of intention that must be fullfiled before resuming to an intention related to this predicate")),
		@variable (
				name = "super_intention",
				type = IType.NONE,
				doc = @doc ("the super-intention of the predicate")),
		@variable (
				name = "agentCause",
				type = IType.AGENT,
				doc = @doc ("the agent causing the predicate")) })
public class Predicate implements IValue {

	String name;
	Map<String, Object> values;
	Double date;
	List<MentalState> onHoldUntil;
	List<MentalState> subintentions;
	MentalState superIntention;
	IAgent agentCause;
	boolean everyPossibleValues = false;
	boolean is_true = true;
//	int lifetime = -1;
	boolean isUpdated = false;
	private boolean noAgentCause = true;

	@getter ("name")
	public String getName() {
		return name;
	}

	@getter ("values")
	public Map<String, Object> getValues() {
		return values;
	}

	@getter ("is_true")
	public Boolean getIs_True() {
		return is_true;
	}

	@getter ("date")
	public Double getDate() {
		return date;
	}

	@getter ("subintentions")
	public List<MentalState> getSubintentions() {
		return subintentions;
	}

	@getter ("superIntention")
	public MentalState getSuperIntention() {
		return superIntention;
	}

	@getter ("agentCause")
	public IAgent getAgentCause() {
		return agentCause;
	}

	public List<MentalState> getOnHoldUntil() {
		return onHoldUntil;
	}

//	public int getLifetime() {
//		return lifetime;
//	}

	public void setSuperIntention(final MentalState superPredicate) {
		this.superIntention = superPredicate;
	}

	public void setOnHoldUntil(final List<MentalState> onHoldUntil) {
		this.onHoldUntil = onHoldUntil;
	}

	public void setValues(final Map<String, Object> values) {
		this.values = values;
		everyPossibleValues = values == null;
	}

	public void setIs_True(final Boolean ist) {
		this.is_true = ist;
	}

	public void setDate(final Double date) {
		this.date = date;
	}

	public void setSubintentions(final List<MentalState> subintentions) {
		this.subintentions = subintentions;
	}

//	public void setLifetime(final int lifetime) {
//		this.lifetime = lifetime;
//	}

	public void setAgentCause(final IAgent ag) {
		this.agentCause = ag;
		this.noAgentCause = false;
	}

	public Predicate() {
		super();
		this.name = "";
		everyPossibleValues = true;
		this.agentCause = null;
	}

	public Predicate(final String name) {
		super();
		this.name = name;
		everyPossibleValues = true;
		this.agentCause = null;
	}

	public Predicate(final String name, final boolean ist) {
		super();
		this.name = name;
		everyPossibleValues = true;
		is_true = ist;
		this.agentCause = null;
	}


//	public Predicate(final String name, final int lifetime) {
//		super();
//		this.name = name;
//		everyPossibleValues = true;
//		this.lifetime = lifetime;
//		this.agentCause = null;
//	}

	public Predicate(final String name, final Map<String, Object> values) {
		super();
		this.name = name;
		this.values = values;
		everyPossibleValues = values == null;
		this.agentCause = null;
	}

	public Predicate(final String name, final IAgent ag) {
		super();
		this.name = name;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
		everyPossibleValues = true;
	}

	public Predicate(final String name, final Map<String, Object> values, final Boolean truth) {
		super();
		this.name = name;
		this.values = values;
		this.is_true = truth;
		everyPossibleValues = values == null;
		this.agentCause = null;
	}
//
//	public Predicate(final String name, final Map<String, Object> values, final int lifetime) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.lifetime = lifetime;
//		everyPossibleValues = values == null;
//		this.agentCause = null;
//	}


	public Predicate(final String name, final Map<String, Object> values, final IAgent ag) {
		super();
		this.name = name;
		this.values = values;
		everyPossibleValues = values == null;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
	}
//
//	public Predicate(final String name, final Map<String, Object> values, final int lifetime, final Boolean truth) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.lifetime = lifetime;
//		this.is_true = truth;
//		everyPossibleValues = values == null;
//	}

	public Predicate(final String name, final Map<String, Object> values, final Boolean truth, final IAgent ag) {
		super();
		this.name = name;
		this.values = values;
		this.is_true = truth;
		everyPossibleValues = values == null;
		this.agentCause = ag;
		this.noAgentCause = ag == null;
	}
//
//	public Predicate(final String name, final Map<String, Object> values, final int lifetime, final IAgent ag) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.lifetime = lifetime;
//		everyPossibleValues = values == null;
//		this.agentCause = ag;
//		this.noAgentCause = ag == null;
//	}

//	public Predicate(final String name, final Map<String, Object> values, final int lifetime, final Boolean truth,
//			final IAgent ag) {
//		super();
//		this.name = name;
//		this.values = values;
//		this.lifetime = lifetime;
//		this.is_true = truth;
//		everyPossibleValues = values == null;
//		this.agentCause = ag;
//		this.noAgentCause = ag == null;
//	}

	public void setName(final String name) {
		this.name = name;

	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return "predicate(" + name + (values == null ? "" : "," + values) + (agentCause == null ? "" : "," + agentCause)
				+ "," + is_true +")";
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "predicate(" + name + (values == null ? "" : "," + values) + (agentCause == null ? "" : "," + agentCause)
				+ "," + is_true +")";
	}

	@Override
	public Predicate copy(final IScope scope) throws GamaRuntimeException {
		return new Predicate(name, values == null ? null : ((GamaMap<String, Object>) values).copy(scope));
	}

	public Predicate copy() throws GamaRuntimeException {
		if (values != null && agentCause != null) {
			return new Predicate(name,((GamaMap<String, Object>) values).copy(GAMA.getRuntimeScope()), is_true, agentCause);
		}
		if (values != null) { return new Predicate(name, ((GamaMap<String, Object>) values).copy(GAMA.getRuntimeScope())); }
		return new Predicate(name);
	}

//	public void updateLifetime() {
//		if (this.lifetime > 0 && !this.isUpdated) {
//			this.lifetime = this.lifetime - 1;
//			this.isUpdated = true;
//		}
//	}

	public boolean isSimilarName(final Predicate other) {
		if (this == other) { return true; }
		if (other == null) { return false; }
		if (name == null) {
			if (other.name != null) { return false; }
		} else if (!name.equals(other.name)) { return false; }
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (values == null ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) { return false; }
		} else if (!name.equals(other.name)) { return false; }

		// if (subintentions == null) {
		// if (other.subintentions != null && !other.subintentions.isEmpty()) {
		// return false;
		// }
		// } else if (!subintentions.equals(other.subintentions)) {
		// return false;
		// }
		// if (superIntention == null) {
		// if (other.superIntention != null) {
		// return false;
		// }
		// } else if (superIntention.getPredicate() == null) {
		// if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
		// return false;
		// }
		// } else if (other.superIntention!=null &&
		// !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
		// return false;
		// }
		if (is_true != other.is_true) { return false; }
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) { return true; }
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; } } else //
		 */ if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty()) {
			final Set<String> keys = values.keySet();
			keys.retainAll(other.values.keySet());
			for (final String k : keys) {
				if(this.values.get(k)==null && other.values.get(k)!=null) {return false;}
				if (!values.get(k).equals(other.values.get(k))) { return false; }
			}
			return true;
		}
		// if (values != null && other.values != null && !values.equals(other.values)) {
		// return false;
		// }

		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) { return false; }

		return true;
	}

	// private boolean partialEquality(final Object obj) {
	// // You don't test the sub-intentions. Used when testing the equality of
	// // the super-intention
	// if (this == obj) {
	// return true;
	// }
	// if (obj == null) {
	// return false;
	// }
	// if (getClass() != obj.getClass()) {
	// return false;
	// }
	// final Predicate other = (Predicate) obj;
	// if (name == null) {
	// if (other.name != null) {
	// return false;
	// }
	// } else if (!name.equals(other.name)) {
	// return false;
	// }
	//// if (superIntention == null) {
	//// if (other.superIntention != null) {
	//// return false;
	//// }
	//// } else if (superIntention.getPredicate() == null) {
	//// if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
	//// return false;
	//// }
	//// } else if (other.superIntention!=null &&
	// !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
	//// return false;
	//// }
	// if (is_true != other.is_true) {
	// return false;
	// }
	// // if(lifetime!=-1 || other.lifetime!=1){
	// // if(lifetime!=other.lifetime){return false;}
	// // }
	// if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) {
	// return true;
	// }
	// /*
	// * if ( values == null ) { if ( other.values != null ) { return false; }
	// * } else
	// */
	// if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty())
	// {
	// Set<String> keys = values.keySet();
	// keys.retainAll(other.values.keySet());
	// for (String k : keys) {
	// if (!values.get(k).equals(other.values.get(k)))
	// return false;
	// }
	// return true;
	// }
	//// if (values != null && other.values != null && !values.equals(other.values)) {
	//// return false;
	//// }
	// /*
	// * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
	// */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
	// return false;
	// }
	//
	// return true;
	// }

	public boolean equalsIntentionPlan(final Object obj) {
		// Only test case where the parameter is not null
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) { return false; }
		} else if (!name.equals(other.name)) { return false; }
		// if (subintentions != null) {
		// if (!subintentions.equals(other.subintentions)) {
		// return false;
		// }
		// }
		// if (superIntention == null) {
		// if (other.superIntention!=null && other.superIntention != null) {
		// return false;
		// }
		// } else if (superIntention.getPredicate() != null) {
		// if (!superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
		// return false;
		// }
		// }
		if (is_true != other.is_true) { return false; }
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) { return true; }
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; } } else
		 */
		if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty()) {
			final Set<String> keys = values.keySet();
			keys.retainAll(other.values.keySet());
			for (final String k : keys) {
				if(this.values.get(k)==null && other.values.get(k)!=null) {return false;}
				if (!values.get(k).equals(other.values.get(k))) { return false; }
			}
			return true;
		}
		// if (values != null && other.values != null && !values.equals(other.values)) {
		// return false;
		// }
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) { return false; }

		return true;
	}

	public boolean equalsButNotTruth(final Object obj) {
		// return true if the predicates are equals but one is true and not the
		// other
		// Doesn't check the lifetime value
		// Used in emotions
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) { return false; }
		} else if (!name.equals(other.name)) { return false; }
		// if (subintentions == null) {
		// if (other.subintentions != null && !other.subintentions.isEmpty()) {
		// return false;
		// }
		// } else if (!subintentions.equals(other.subintentions)) {
		// return false;
		// }
		// if (superIntention == null) {
		// if (other.superIntention != null) {
		// return false;
		// }
		// } else if (superIntention.getPredicate() == null) {
		// if (other.superIntention != null) {
		// return false;
		// }
		// } else if (other.superIntention!=null &&
		// !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
		// return false;
		// }
		if (is_true != other.is_true) {
			if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) { return true; }
			/*
			 * if ( values == null ) { if ( other.values != null ) { return false; } } else
			 */
			if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty()) {
				final Set<String> keys = values.keySet();
				keys.retainAll(other.values.keySet());
				for (final String k : keys) {
					if(this.values.get(k)==null && other.values.get(k)!=null) {return false;}
					if (!values.get(k).equals(other.values.get(k))) { return false; }
				}
				return true;
			}
			// if (values != null && other.values != null && !values.equals(other.values)) {
			// return false;
			// }
			/*
			 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
			 */
			// if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
			// return false;
			// }

			return true;
		} else {
			return false;
		}
	}

	public boolean equalsEmotions(final Object obj) {
		// Ne teste pas l'agent cause.
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Predicate other = (Predicate) obj;
		if (name == null) {
			if (other.name != null) { return false; }
		} else if (!name.equals(other.name)) { return false; }

		// if (subintentions == null) {
		// if (other.subintentions != null && !other.subintentions.isEmpty()) {
		// return false;
		// }
		// } else if (!subintentions.equals(other.subintentions)) {
		// return false;
		// }
		// if (superIntention == null) {
		// if (other.superIntention != null) {
		// return false;
		// }
		// } else if (superIntention.getPredicate() == null) {
		// if (other.superIntention!=null && other.superIntention.getPredicate() != null) {
		// return false;
		// }
		// } else if (other.superIntention!=null &&
		// !superIntention.getPredicate().partialEquality(other.superIntention.getPredicate())) {
		// return false;
		// }
		if (is_true != other.is_true) { return false; }
		// if(lifetime!=-1 || other.lifetime!=1){
		// if(lifetime!=other.lifetime){return false;}
		// }
		if (everyPossibleValues && noAgentCause || other.everyPossibleValues && other.noAgentCause) { return true; }
		/*
		 * if ( values == null ) { if ( other.values != null ) { return false; } } else
		 */
		if (values != null && other.values != null && !values.isEmpty() && !other.values.isEmpty()) {
			final Set<String> keys = values.keySet();
			keys.retainAll(other.values.keySet());
			for (final String k : keys) {
				if(this.values.get(k)==null && other.values.get(k)!=null) {return false;}
				if (!values.get(k).equals(other.values.get(k))) { return false; }
			}
			return true;
		}
		// if (values != null && other.values != null && !values.equals(other.values)) {
		// return false;
		// }
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */
		// if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) {
		// return false;
		// }

		return true;
	}

	/**
	 * Method getType()
	 *
	 * @see msi.gama.common.interfaces.ITyped#getGamlType()
	 */
	@Override
	public IType<?> getGamlType() {
		return Types.get(PredicateType.id);
	}

}
