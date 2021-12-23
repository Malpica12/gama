/*******************************************************************************************************
 *
 * IGraphEventProvider.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.util.graph;

import gama.runtime.IScope;

/**
 * The Interface IGraphEventProvider.
 */
public interface IGraphEventProvider {

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	public void addListener(IGraphEventListener listener);

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener
	 */
	public void removeListener(IGraphEventListener listener);

	/**
	 * Dispatch event.
	 *
	 * @param scope the scope
	 * @param event the event
	 */
	public void dispatchEvent(final IScope scope, GraphEvent event);

}