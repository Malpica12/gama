/*******************************************************************************************************
 *
 * AgentExecutionContext.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.runtime;

import gama.common.interfaces.IDisposable;
import gama.common.util.PoolUtils;
import gama.metamodel.agent.IAgent;

/**
 * The Class AgentExecutionContext.
 */
class AgentExecutionContext implements IDisposable {

	/** The Constant POOL. */
	private static final PoolUtils.ObjectPool<AgentExecutionContext> POOL =
			PoolUtils.create("Agent Execution Context", true, () -> new AgentExecutionContext(), null, null);

	/**
	 * Creates the.
	 *
	 * @param agent the agent
	 * @param outer the outer
	 * @return the agent execution context
	 */
	public static AgentExecutionContext create(final IAgent agent, final AgentExecutionContext outer) {
		final AgentExecutionContext result = POOL.get();
		result.agent = agent;
		result.outer = outer;
		return result;
	}

	/** The agent. */
	IAgent agent;
	
	/** The outer. */
	AgentExecutionContext outer;

	/**
	 * Instantiates a new agent execution context.
	 */
	private AgentExecutionContext() {}

	/**
	 * Gets the agent.
	 *
	 * @return the agent
	 */
	public IAgent getAgent() {
		return agent;
	}

	@Override
	public String toString() {
		return "context of " + agent;
	}

	/**
	 * Gets the outer context.
	 *
	 * @return the outer context
	 */
	public AgentExecutionContext getOuterContext() {
		return outer;
	}

	@Override
	public void dispose() {
		agent = null;
		outer = null;
		POOL.release(this);
	}

	/**
	 * Creates the copy.
	 *
	 * @return the agent execution context
	 */
	public AgentExecutionContext createCopy() {
		return create(agent, outer);
	}

}