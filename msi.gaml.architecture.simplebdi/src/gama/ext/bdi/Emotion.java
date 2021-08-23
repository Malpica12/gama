package gama.ext.bdi;

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
		name = "name",
		type = IType.STRING,
		doc = @doc ("the name of the emotion")),
		@variable (
				name = "intensity",
				type = IType.FLOAT,
				doc = @doc ("the intensity of the emotion")),
		@variable (
				name = "about",
				type = PredicateType.id,
				doc = @doc ("the predicate about which is the emotion")),
		@variable (
				name = "decay",
				type = IType.FLOAT,
				doc = @doc ("the decay value of the emotion")),
		@variable (
				name = "agentCause",
				type = IType.AGENT,
				doc = @doc ("the agent causing the emotion")),
		@variable (
				name = "owner",
				type = IType.AGENT,
				doc = @doc ("the agent owning the emotion")) })
public class Emotion implements IValue {

	String name;
	Double intensity = -1.0;
	Predicate about;
	Double decay = 0.0;
	IAgent agentCause;
	IAgent owner;
	private boolean noAgentCause = true;
	private boolean noIntensity = true;
	private boolean noAbout = true;

	@getter ("name")
	public String getName() {
		return name;
	}

	@getter ("intensity")
	public Double getIntensity() {
		return intensity;
	}

	@getter ("about")
	public Predicate getAbout() {
		return about;
	}

	@getter ("decay")
	public Double getDecay() {
		return decay;
	}

	@getter ("agentCause")
	public IAgent getAgentCause() {
		return agentCause;
	}

	@getter ("owner")
	public IAgent getOwner() {
		return owner;
	}

	public boolean getNoIntensity() {
		return this.noIntensity;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setIntensity(final Double intens) {
		this.intensity = intens;
		this.noIntensity = false;
	}

	public void setAbout(final Predicate ab) {
		this.about = ab;
		this.noAbout = false;
	}

	public void setDecay(final Double de) {
		this.decay = de;
	}

	public void setAgentCause(final IAgent ag) {
		this.agentCause = ag;
		this.noAgentCause = false;
	}

	public void setOwner(final IAgent own) {
		this.owner = own;
	}

	public Emotion() {
		this.name = "";
		this.about = null;
		this.agentCause = null;
		this.owner = null;
	}

	public Emotion(final String name) {
		this.name = name;
		this.about = null;
		this.agentCause = null;
		this.owner = null;
	}

	public Emotion(final String name, final Double intensity2) {
		this.name = name;
		this.intensity = intensity2;
		this.about = null;
		this.agentCause = null;
		this.owner = null;
		this.noIntensity = false;
	}

	public Emotion(final String name, final Predicate ab) {
		this.name = name;
		this.about = ab;
		this.agentCause = null;
		this.owner = null;
		this.noAbout = false;
	}

	public Emotion(final String name, final IAgent ag) {
		this.name = name;
		this.about = null;
		this.agentCause = ag;
		this.owner = null;
		this.noAgentCause = ag == null;
	}

	public Emotion(final String name, final Double intens, final Double de) {
		this.name = name;
		this.intensity = intens;
		this.about = null;
		this.agentCause = null;
		this.owner = null;
		this.decay = de;
		this.noIntensity = false;
		this.noAbout = false;
	}

	public Emotion(final String name, final Double intens, final Predicate ab) {
		this.name = name;
		this.intensity = intens;
		this.about = ab;
		this.agentCause = null;
		this.owner = null;
		this.noIntensity = false;
		this.noAbout = false;
	}

	public Emotion(final String name, final Predicate ab, final IAgent ag) {
		this.name = name;
		this.about = ab;
		this.agentCause = ag;
		this.owner = null;
		this.noAbout = false;
		this.noAgentCause = ag == null;
	}

	public Emotion(final String name, final Double intens, final IAgent ag) {
		this.name = name;
		this.intensity = intens;
		this.about = null;
		this.agentCause = ag;
		this.owner = null;
		this.noIntensity = false;
		this.noAgentCause = ag == null;
	}

	public Emotion(final String name, final Double intens, final Predicate ab, final Double de) {
		this.name = name;
		this.intensity = intens;
		this.about = ab;
		this.agentCause = null;
		this.owner = null;
		this.decay = de;
		this.noIntensity = false;
		this.noAbout = false;
	}

	public Emotion(final String name, final Double intens, final Double de, final IAgent ag) {
		this.name = name;
		this.intensity = intens;
		this.about = null;
		this.agentCause = ag;
		this.owner = null;
		this.decay = de;
		this.noIntensity = false;
		this.noAgentCause = ag == null;
	}

	public Emotion(final String name, final Double intens, final Predicate ab, final IAgent ag) {
		this.name = name;
		this.intensity = intens;
		this.about = ab;
		this.agentCause = ag;
		this.owner = null;
		this.noIntensity = false;
		this.noAgentCause = ag == null;
		this.noAbout = false;
	}

	public Emotion(final String name, final Double intens, final Predicate ab, final Double de, final IAgent ag) {
		this.name = name;
		this.intensity = intens;
		this.about = ab;
		this.agentCause = ag;
		this.owner = null;
		this.decay = de;
		this.noIntensity = false;
		this.noAbout = false;
		this.noAgentCause = ag == null;
	}

	public void decayIntensity() {
		this.intensity = this.intensity - this.decay;
	}

	@Override
	public String toString() {
		return serialize(true);
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if (intensity > 0) {
			return "emotion(" + name + "," + intensity + "," + about + "," + decay + "," + agentCause + "," + owner
					+ ")";
		}
		return "emotion(" + name + "," + about + "," + decay + "," + agentCause + "," + owner + ")";
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return name;
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return new Emotion(name, intensity, about, decay, agentCause);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Emotion other = (Emotion) obj;
		if (name == null) {
			if (other.name != null) { return false; }
		} else if (!name.equals(other.name)) { return false; }
		if (noAbout && noAgentCause || other.noAbout && other.noAgentCause) { return true; }
		/*
		 * if(about==null){ if(other.about!=null){return false;} }else
		 */if (about != null && other.about != null && !about.equalsEmotions(other.about)) { return false; }
		/*
		 * if(agentCause==null){ if(other.agentCause!=null){return false;} }else
		 */
		/*
		 * if (agentCause != null && other.agentCause != null && !agentCause.equals(other.agentCause)) { return false; }
		 */
		if (owner != null && other.owner != null && !owner.equals(other.owner)) { return false; }
		return true;
	}

	@Override
	public IType<?> getGamlType() {
		return Types.get(EmotionType.id);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
