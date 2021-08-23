package gama.ext.bdi;

import java.util.List;

import gama.common.interfaces.IValue;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.getter;
import gama.core.dev.annotations.GamlAnnotations.variable;
import gama.core.dev.annotations.GamlAnnotations.vars;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.types.IType;
import gaml.types.Types;

@vars ({ @variable (
		name = "modality",
		type = IType.STRING,
		doc = @doc ("the modality of the mental state")),
		@variable (
				name = "predicate",
				type = PredicateType.id,
				doc = @doc ("the predicate about which is the mental state")),
		@variable (
				name = "mental_state",
				type = MentalStateType.id,
				doc = @doc ("the mental state about which is the mental state")),
		@variable (
				name = "emotion",
				type = EmotionType.id,
				doc = @doc ("the emotion about which is the mental state")),
		@variable (
				name = "owner",
				type = IType.AGENT,
				doc = @doc ("the owner of the mental state")),
		@variable (
				name = "strength",
				type = IType.FLOAT,
				doc = @doc ("the strength value related to the mental state")),
		@variable (
				name = "lifetime",
				type = IType.INT,
				doc = @doc ("the lifetime of the mental state")) })
public class MentalState implements IValue {

	String modality;
	Predicate predicate;
	Double strength;
	int lifetime = -1;
	boolean isUpdated = false;
	MentalState mental;
	Emotion emo;
	IAgent owner;
	List<MentalState> onHoldUntil;
	List<MentalState> subintentions;
	MentalState superIntention;

	@getter ("modality")
	public String getModality() {
		return modality;
	}

	@getter ("predicate")
	public Predicate getPredicate() {
		return predicate;
	}

	@getter ("mental_state")
	public MentalState getMentalState() {
		return mental;
	}

	@getter ("emotion")
	public Emotion getEmotion() {
		return emo;
	}

	@getter ("strength")
	public Double getStrength() {
		return strength;
	}

	@getter ("lifetime")
	public int getLifeTime() {
		return lifetime;
	}

	@getter ("owner")
	public IAgent getOwner() {
		return owner;
	}

	@getter ("subintentions")
	public List<MentalState> getSubintentions() {
		return subintentions;
	}

	@getter ("superIntention")
	public MentalState getSuperIntention() {
		return superIntention;
	}

	public List<MentalState> getOnHoldUntil() {
		return onHoldUntil;
	}

	public void setModality(final String mod) {
		this.modality = mod;
	}

	public void setPredicate(final Predicate pred) {
		this.predicate = pred;
	}

	public void setMentalState(final MentalState ment) {
		this.mental = ment;
	}

	public void setEmotion(final Emotion em) {
		this.emo = em;
	}

	public void setStrength(final Double stre) {
		this.strength = stre;
	}

	public void setLifeTime(final int life) {
		this.lifetime = life;
	}

	public void setOwner(final IAgent ag) {
		this.owner = ag;
	}

	public void setSubintentions(final List<MentalState> subintentions) {
		this.subintentions = subintentions;
	}

	public void setSuperIntention(final MentalState superPredicate) {
		this.superIntention = superPredicate;
	}

	public void setOnHoldUntil(final List<MentalState> onHoldUntil) {
		this.onHoldUntil = onHoldUntil;
	}

	public void updateLifetime() {
		if (this.lifetime > 0 && !this.isUpdated) {
			this.lifetime = this.lifetime - 1;
			this.isUpdated = true;
		}
	}

	public MentalState() {
		super();
		this.modality = "";
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.owner = null;
		this.emo = null;
	}

	public MentalState(final String mod) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.owner = null;
		this.emo = null;
	}

	public MentalState(final String mod, final Predicate pred) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = 1.0;
		this.owner = null;
		this.emo = null;
	}

	public MentalState(final String mod, final MentalState ment) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = 1.0;
		this.owner = null;
		this.emo = null;
	}

	public MentalState(final String mod, final Emotion em) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.owner = null;
		this.emo = em;
	}

	public MentalState(final String mod, final Predicate pred, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = 1.0;
		this.owner = ag;
		this.emo = null;
	}

	public MentalState(final String mod, final MentalState ment, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = 1.0;
		this.owner = ag;
		this.emo = null;
	}

	public MentalState(final String mod, final Emotion em, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.owner = ag;
		this.emo = em;
	}

	public MentalState(final String mod, final Predicate pred, final Double stre) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = stre;
		this.emo = null;
	}

	public MentalState(final String mod, final MentalState ment, final Double stre) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = stre;
		this.emo = null;
	}

	public MentalState(final String mod, final Emotion em, final Double stre) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = stre;
		this.emo = em;
	}

	public MentalState(final String mod, final Predicate pred, final int life) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.lifetime = life;
		this.strength = 1.0;
		this.emo = null;
	}

	public MentalState(final String mod, final MentalState ment, final int life) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.lifetime = life;
		this.strength = 1.0;
		this.emo = null;
	}

	public MentalState(final String mod, final Emotion em, final int life) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.lifetime = life;
		this.strength = 1.0;
		this.emo = em;
	}

	public MentalState(final String mod, final Predicate pred, final Double stre, final int life) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = stre;
		this.lifetime = life;
		this.emo = null;
	}

	public MentalState(final String mod, final MentalState ment, final Double stre, final int life) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = stre;
		this.lifetime = life;
		this.emo = null;
	}

	public MentalState(final String mod, final Emotion em, final Double stre, final int life) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = stre;
		this.lifetime = life;
		this.emo = em;
	}

	public MentalState(final String mod, final Predicate pred, final Double stre, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = stre;
		this.owner = ag;
		this.emo = null;
	}

	public MentalState(final String mod, final MentalState ment, final Double stre, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = stre;
		this.owner = ag;
		this.emo = null;
	}

	public MentalState(final String mod, final Emotion em, final Double stre, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = stre;
		this.owner = ag;
		this.emo = em;
	}

	public MentalState(final String mod, final Predicate pred, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = 1.0;
		this.lifetime = life;
		this.owner = ag;
		this.emo = null;
	}

	public MentalState(final String mod, final MentalState ment, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = 1.0;
		this.lifetime = life;
		this.owner = ag;
		this.emo = null;
	}

	public MentalState(final String mod, final Emotion em, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = 1.0;
		this.lifetime = life;
		this.owner = ag;
		this.emo = em;
	}

	public MentalState(final String mod, final Predicate pred, final Double stre, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = pred;
		this.mental = null;
		this.strength = stre;
		this.lifetime = life;
		this.owner = ag;
		this.emo = null;
	}

	public MentalState(final String mod, final MentalState ment, final Double stre, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = ment;
		this.strength = stre;
		this.lifetime = life;
		this.owner = ag;
		this.emo = null;
	}

	public MentalState(final String mod, final Emotion em, final Double stre, final int life, final IAgent ag) {
		super();
		this.modality = mod;
		this.predicate = null;
		this.mental = null;
		this.strength = stre;
		this.lifetime = life;
		this.owner = ag;
		this.emo = em;
	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return modality + "(" + (predicate == null ? "" : predicate) + (mental == null ? "" : mental)
				+ (emo == null ? "" : emo) + "," + (owner == null ? "" : owner) + "," + strength + "," + lifetime + ")";
	}

	@Override
	public IType<?> getGamlType() {
		return Types.get(MentalStateType.id);
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return modality + "(" + (predicate == null ? "" : predicate) + (mental == null ? "" : mental)
				+ (emo == null ? "" : emo) + "," + (owner == null ? "" : owner) + "," + strength + "," + lifetime + ")";
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		final MentalState tempMental = new MentalState(modality);
		tempMental.setLifeTime(lifetime);
		tempMental.setStrength(strength);
		tempMental.setOwner(owner);
		if (predicate != null) {
			tempMental.setPredicate(predicate);
			return tempMental;
		} else if (mental != null) {
			tempMental.setMentalState(mental);
			return tempMental;
		} else if (emo != null) {
			tempMental.setEmotion(emo);
			return tempMental;
		}
		return tempMental;
	}

	@Override
	public int hashCode() {
		// final int prime = 31;
		final int result = 1;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final MentalState other = (MentalState) obj;
		// if(other.getModality()!=this.modality){return false;}
		if (this.predicate == null && other.getPredicate() != null) { return false; }
		if (this.predicate != null && other.getPredicate() == null) { return false; }
		if (this.predicate != null && other.getPredicate() != null) {
			if (!other.getPredicate().equals(this.predicate)) { return false; }
		}
		if (this.mental == null && other.getMentalState() != null) { return false; }
		if (this.mental != null && other.getMentalState() == null) { return false; }
		if (this.mental != null && other.getMentalState() != null) {
			if (!other.getMentalState().equals(this.mental)) { return false; }
		}
		if (this.emo == null && other.getEmotion() != null) { return false; }
		if (this.emo != null && other.getEmotion() == null) { return false; }
		if (this.emo != null && other.getEmotion() != null) {
			if (!other.getEmotion().equals(this.emo)) { return false; }
		}
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
		// } else if (superIntention.partialEquality(other.superIntention)) {
		// return false;
		// }
		if (this.owner != null && other.getOwner() != null) {
			if (!other.getOwner().equals(this.owner)) { return false; }
		}
		// if(other.getStrength()!=this.strength){return false;}
		return true;
	}

	// private boolean partialEquality(final Object obj) {
	// // You don't test the sub-intentions. Used when testing the equality of
	// // the super-intention
	// if (this == obj) { return true; }
	// if (obj == null) { return false; }
	// if (getClass() != obj.getClass()) { return false; }
	// final MentalState other = (MentalState) obj;
	// // if(other.getModality()!=this.modality){return false;}
	// if (this.predicate == null && other.getPredicate() != null) { return false; }
	// if (this.predicate != null && other.getPredicate() == null) { return false; }
	// if (this.predicate != null && other.getPredicate() != null) {
	// if (!other.getPredicate().equals(this.predicate)) { return false; }
	// }
	// if (this.mental == null && other.getMentalState() != null) { return false; }
	// if (this.mental != null && other.getMentalState() == null) { return false; }
	// if (this.mental != null && other.getMentalState() != null) {
	// if (!other.getMentalState().equals(this.mental)) { return false; }
	// }
	// if (this.emo == null && other.getEmotion() != null) { return false; }
	// if (this.emo != null && other.getEmotion() == null) { return false; }
	// if (this.emo != null && other.getEmotion() != null) {
	// if (!other.getEmotion().equals(this.emo)) { return false; }
	// }
	// if (superIntention == null) {
	// if (other.superIntention != null) { return false; }
	// } else if (superIntention.partialEquality(other.superIntention)) { return false; }
	// if (this.owner != null && other.getOwner() != null) {
	// if (!other.getOwner().equals(this.owner)) { return false; }
	// }
	// // if(other.getStrength()!=this.strength){return false;}
	// return true;
	// }

}
