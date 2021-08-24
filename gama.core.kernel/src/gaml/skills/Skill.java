/*******************************************************************************************************
 *
 * Skill.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.skills;

import gama.common.interfaces.ISkill;
import gama.metamodel.agent.IAgent;
import gama.metamodel.topology.ITopology;
import gama.runtime.IScope;
import gaml.descriptions.SkillDescription;

/**
 * The Class Skill.
 */
public class Skill implements ISkill {

	/** The description. */
	protected SkillDescription description;

	/**
	 * Instantiates a new skill.
	 */
	protected Skill() {}

	@Override
	public void setName(final String newName) {}

	/**
	 * Sets the description.
	 *
	 * @param desc the new description
	 */
	public void setDescription(final SkillDescription desc) {
		description = desc;
	}

	@Override
	public String getDocumentation() {
		return description.getDocumentation();
	}

	@Override
	public SkillDescription getDescription() {
		return description;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return getName();
	}

	/**
	 * Gets the current agent.
	 *
	 * @param scope the scope
	 * @return the current agent
	 */
	protected IAgent getCurrentAgent(final IScope scope) {
		return scope.getAgent();
	}

	/**
	 * Gets the topology.
	 *
	 * @param agent the agent
	 * @return the topology
	 */
	protected ITopology getTopology(final IAgent agent) {
		return agent.getTopology();
	}

	@Override
	public String getTitle() {
		return description.getTitle();
	}

	@Override
	public String getDefiningPlugin() {
		return description.getDefiningPlugin();
	}

	@Override
	public String getName() {
		return description.getName();
	}

}
