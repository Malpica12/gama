/*******************************************************************************************************
 *
 * msi.gama.runtime.concurrent.ParallelAgentStepper.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.runtime.concurrent;

import java.util.Spliterator;

import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;

public class ParallelAgentStepper extends ParallelAgentRunner<Boolean> {

	public ParallelAgentStepper(final IScope scope, final Spliterator<IAgent> agents) {
		super(scope, agents);
	}

	@Override
	public Boolean executeOn(final IScope scope) throws GamaRuntimeException {
		final Boolean[] mutableBoolean = { Boolean.TRUE };
		agents.forEachRemaining(each -> {
			if (mutableBoolean[0].booleanValue()) {
				mutableBoolean[0] = Boolean.valueOf(scope.step(each).passed());
			}
		});
		return mutableBoolean[0];
	}

	@Override
	ParallelAgentRunner<Boolean> subTask(final Spliterator<IAgent> sub) {
		return new ParallelAgentStepper(originalScope, sub);
	}

}