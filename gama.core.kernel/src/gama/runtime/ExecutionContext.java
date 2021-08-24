/*******************************************************************************************************
 *
 * ExecutionContext.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.runtime;

import java.util.Collections;
import java.util.Map;

import gama.common.util.PoolUtils;
import gama.util.GamaMapFactory;
import gaml.types.Types;

/**
 * The Class ExecutionContext.
 */
public class ExecutionContext implements IExecutionContext {

	/** The Constant POOL. */
	private static final PoolUtils.ObjectPool<ExecutionContext> POOL =
			PoolUtils.create("Execution Context", true, () -> new ExecutionContext(), null, null);

	/**
	 * Creates the.
	 *
	 * @param outer the outer
	 * @return the execution context
	 */
	public static ExecutionContext create(final IExecutionContext outer) {
		return create(outer.getScope(), outer);
	}

	/**
	 * Creates the.
	 *
	 * @param scope the scope
	 * @return the execution context
	 */
	public static ExecutionContext create(final IScope scope) {
		return create(scope, null);
	}

	/**
	 * Creates the.
	 *
	 * @param scope the scope
	 * @param outer the outer
	 * @return the execution context
	 */
	public static ExecutionContext create(final IScope scope, final IExecutionContext outer) {
		final ExecutionContext result = POOL.get();
		result.scope = scope;
		result.outer = outer;
		return result;
	}

	/** The local. */
	Map<String, Object> local;
	
	/** The outer. */
	IExecutionContext outer;
	
	/** The scope. */
	IScope scope;

	@Override
	public void dispose() {
		local = null;
		outer = null;
		scope = null;
		POOL.release(this);
	}

	@Override
	public IScope getScope() {
		return scope;
	}

	/**
	 * Instantiates a new execution context.
	 */
	ExecutionContext() {}

	@Override
	public final IExecutionContext getOuterContext() {
		return outer;
	}

	@Override
	public void setTempVar(final String name, final Object value) {
		if (local == null || !local.containsKey(name)) {
			if (outer != null) { outer.setTempVar(name, value); }
		} else {
			local.put(name, value);
		}

	}

	@Override
	public Object getTempVar(final String name) {
		if (local == null || !local.containsKey(name)) return outer == null ? null : outer.getTempVar(name);
		return local.get(name);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public ExecutionContext createCopy() {
		final ExecutionContext r = create(scope, outer);
		if (local != null) {
			r.local = GamaMapFactory.createWithoutCasting(Types.NO_TYPE, Types.NO_TYPE, local, false);
		}
		return r;
	}

	@Override
	public ExecutionContext createChildContext() {
		return create(this);
	}

	@Override
	public Map<? extends String, ? extends Object> getLocalVars() {
		return local == null ? Collections.EMPTY_MAP : local;
	}

	@Override
	public void clearLocalVars() {
		local = null;
	}

	@Override
	public void putLocalVar(final String varName, final Object val) {
		if (local == null) { local = GamaMapFactory.createUnordered(); }
		local.put(varName, val);
	}

	@Override
	public Object getLocalVar(final String string) {
		if (local == null) return null;
		return local.get(string);
	}

	@Override
	public boolean hasLocalVar(final String name) {
		if (local == null) return false;
		return local.containsKey(name);
	}

	@Override
	public void removeLocalVar(final String name) {
		if (local == null) return;
		local.remove(name);
	}

	@Override
	public String toString() {
		return "execution context " + local;
	}

}