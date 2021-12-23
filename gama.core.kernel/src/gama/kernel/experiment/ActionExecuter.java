/*******************************************************************************************************
 *
 * ActionExecuter.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.kernel.experiment;

import java.util.ArrayList;
import java.util.List;

import gama.runtime.IScope;
import gaml.statements.IExecutable;

/**
 * The Class ActionExecuter.
 */
public class ActionExecuter {

	/** The Constant BEGIN. */
	private static final int BEGIN = 0;
	
	/** The Constant END. */
	private static final int END = 1;
	
	/** The Constant DISPOSE. */
	private static final int DISPOSE = 2;
	
	/** The Constant ONE_SHOT. */
	private static final int ONE_SHOT = 3;

	/** The actions. */
	@SuppressWarnings ("unchecked") final List<IExecutable>[] actions = new List[4];
	
	/** The scope. */
	protected final IScope scope;

	/**
	 * Instantiates a new action executer.
	 *
	 * @param scope the scope
	 */
	public ActionExecuter(final IScope scope) {
		this.scope = scope.copy("of ActionExecuter");
	}

	/**
	 * Insert action.
	 *
	 * @param action the action
	 * @param type the type
	 * @return the i executable
	 */
	private IExecutable insertAction(final IExecutable action, final int type) {
		List<IExecutable> list = actions[type];
		if (list == null) {
			list = new ArrayList<>();
			actions[type] = list;
		}
		if (list.add(action)) { return action; }
		return null;
	}

	/**
	 * Insert dispose action.
	 *
	 * @param action the action
	 * @return the i executable
	 */
	public IExecutable insertDisposeAction(final IExecutable action) {
		return insertAction(action, DISPOSE);
	}

	/**
	 * Insert end action.
	 *
	 * @param action the action
	 * @return the i executable
	 */
	public IExecutable insertEndAction(final IExecutable action) {
		return insertAction(action, END);
	}

	/**
	 * Insert one shot action.
	 *
	 * @param action the action
	 * @return the i executable
	 */
	public IExecutable insertOneShotAction(final IExecutable action) {
		return insertAction(action, ONE_SHOT);
	}

	/**
	 * Execute end actions.
	 */
	public void executeEndActions() {
		if (scope.interrupted()) { return; }
		executeActions(END);
	}

	/**
	 * Execute dispose actions.
	 */
	public void executeDisposeActions() {
		executeActions(DISPOSE);
	}

	/**
	 * Execute one shot actions.
	 */
	public void executeOneShotActions() {
		if (scope.interrupted()) { return; }
		try {
			executeActions(ONE_SHOT);
		} finally {
			actions[ONE_SHOT] = null;
		}
	}

	/**
	 * Execute actions.
	 *
	 * @param type the type
	 */
	private void executeActions(final int type) {
		if (actions[type] == null) { return; }
		final int size = actions[type].size();
		if (size == 0) { return; }
		final IExecutable[] array = actions[type].toArray(new IExecutable[size]);
		for (final IExecutable action : array) {
			if (!scope.interrupted()) {
				action.executeOn(scope);
			}
		}
	}

	/**
	 * Execute one action.
	 *
	 * @param action the action
	 */
	public synchronized void executeOneAction(final IExecutable action) {
		final boolean paused = scope.isPaused();
		if (paused) {
			action.executeOn(scope);
		} else {
			insertOneShotAction(action);
		}
	}

	/**
	 * Execute begin actions.
	 */
	public void executeBeginActions() {
		if (scope.interrupted()) { return; }
		executeActions(BEGIN);
	}

}