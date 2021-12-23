/*******************************************************************************************************
 *
 * IEventLayerDelegate.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.common.ui;

import java.util.Set;

import gama.outputs.layers.EventLayerStatement;
import gama.runtime.IScope;

/**
 * Class IEventLayerDelegate. Represents a delegate to the EventLayers that accepts other inputs than keyboard inputs
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public interface IEventLayerDelegate {

	/**
	 * Gets the events.
	 *
	 * @return the events
	 */
	Set<String> getEvents();
	
	/**
	 * Returns whether or not this delegate accepts the input source.
	 *
	 * @param scope the scope
	 * @param source the source
	 * @return true, if successful
	 */
	boolean acceptSource(IScope scope, Object source);

	/**
	 * Fills the list of maps with the initial values read from the source. Returns true if all the inits have been
	 * correctly filled
	 *
	 * @param scope the scope
	 * @param source the source
	 * @param statement the statement
	 * @return true, if successful
	 */

	boolean createFrom(IScope scope, Object source, EventLayerStatement statement);

}