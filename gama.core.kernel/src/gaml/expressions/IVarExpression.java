/*******************************************************************************************************
 *
 * IVarExpression.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.expressions;

import gama.runtime.IScope;
import gaml.descriptions.IDescription;
import gaml.expressions.variables.VariableExpression;

/**
 * VariableExpression.
 *
 * @author drogoul 4 sept. 07
 */
public interface IVarExpression extends IExpression {

	/**
	 * The Interface Agent.
	 */
	public interface Agent extends IVarExpression {

		/**
		 * Gets the definition description.
		 *
		 * @return the definition description
		 */
		IDescription getDefinitionDescription();
	}

	/** The global. */
	int GLOBAL = 0;
	
	/** The agent. */
	int AGENT = 1;
	
	/** The temp. */
	int TEMP = 2;
	
	/** The each. */
	int EACH = 3;
	
	/** The self. */
	int SELF = 4;
	
	/** The super. */
	int SUPER = 5;
	
	/** The myself. */
	int MYSELF = 6;
	// public static final int WORLD = 5;

	/**
	 * Sets the val.
	 *
	 * @param scope the scope
	 * @param v the v
	 * @param create the create
	 */
	void setVal(IScope scope, Object v, boolean create);

	/**
	 * Checks if is not modifiable.
	 *
	 * @return true, if is not modifiable
	 */
	boolean isNotModifiable();

	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	IExpression getOwner();

	/**
	 * Gets the var.
	 *
	 * @return the var
	 */
	VariableExpression getVar();

}