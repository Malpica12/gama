/*******************************************************************************************************
 *
 * StoppingCriterion.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.kernel.batch;

import java.util.Map;

/**
 * The Interface StoppingCriterion.
 */
public interface StoppingCriterion {

	/**
	 * Stop search process.
	 *
	 * @param parameters the parameters
	 * @return true, if successful
	 */
	public boolean stopSearchProcess(Map<String, Object> parameters);
}