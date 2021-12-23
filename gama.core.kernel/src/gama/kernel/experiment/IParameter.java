/*******************************************************************************************************
 *
 * IParameter.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.kernel.experiment;

import java.util.List;
import java.util.Set;

import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaColor;
import gaml.types.IType;

/**
 * Written by drogoul Modified on 4 juin 2010.
 *
 * @todo Description
 */
public interface IParameter extends IExperimentDisplayable {

	/**
	 * The listener interface for receiving parameterChange events.
	 * The class that is interested in processing a parameterChange
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addParameterChangeListener<code> method. When
	 * the parameterChange event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see ParameterChangeEvent
	 */
	public interface ParameterChangeListener {
		
		/**
		 * Changed.
		 *
		 * @param scope the scope
		 * @param newValue the new value
		 */
		void changed(IScope scope, Object newValue);
	}

	/** The empty strings. */
	String[] EMPTY_STRINGS = {};

	/**
	 * Sets the value.
	 *
	 * @param scope the scope
	 * @param value the value
	 */
	void setValue(IScope scope, Object value);

	/**
	 * Value.
	 *
	 * @param scope the scope
	 * @return the object
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	Object value(IScope scope) throws GamaRuntimeException;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	@SuppressWarnings ("rawtypes")
	IType getType();

	/**
	 * Serialize.
	 *
	 * @param includingBuiltIn the including built in
	 * @return the string
	 */
	@Override
	String serialize(boolean includingBuiltIn);

	/**
	 * Gets the initial value.
	 *
	 * @param scope the scope
	 * @return the initial value
	 */
	Object getInitialValue(IScope scope);

	/**
	 * Gets the min value.
	 *
	 * @param scope the scope
	 * @return the min value
	 */
	Object getMinValue(IScope scope);

	/**
	 * Gets the max value.
	 *
	 * @param scope the scope
	 * @return the max value
	 */
	Object getMaxValue(IScope scope);

	/**
	 * Gets the among value.
	 *
	 * @param scope the scope
	 * @return the among value
	 */
	@SuppressWarnings ("rawtypes")
	List getAmongValue(IScope scope);

	/**
	 * Checks if is editable.
	 *
	 * @return true, if is editable
	 */
	boolean isEditable();

	/**
	 * Gets the color.
	 *
	 * @param scope the scope
	 * @return the color
	 */
	List<GamaColor> getColor(final IScope scope);

	/**
	 * Accepts slider.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 */
	boolean acceptsSlider(IScope scope);

	/**
	 * Gets the step value.
	 *
	 * @param scope the scope
	 * @return the step value
	 */
	Comparable getStepValue(IScope scope);

	/**
	 * Checks if is defined.
	 *
	 * @return true, if is defined
	 */
	boolean isDefined();

	/**
	 * Gets the enablement.
	 *
	 * @return the enablement
	 */
	default String[] getEnablement() {
		return EMPTY_STRINGS;
	}

	/**
	 * Gets the disablement.
	 *
	 * @return the disablement
	 */
	default String[] getDisablement() {
		return EMPTY_STRINGS;
	}

	/**
	 * Adds the changed listener.
	 *
	 * @param listener the listener
	 */
	default void addChangedListener(final ParameterChangeListener listener) {
		// Nothing to do by default
	}

	/**
	 * The Interface Batch.
	 */
	public interface Batch extends IParameter {

		/**
		 * Value.
		 *
		 * @return the object
		 */
		Object value();

		/**
		 * Sets the category.
		 *
		 * @param cat the new category
		 */
		void setCategory(String cat);

		/**
		 * Reinit randomly.
		 *
		 * @param scope the scope
		 */
		void reinitRandomly(IScope scope);

		/**
		 * Neighbor values.
		 *
		 * @param scope the scope
		 * @return the sets the
		 * @throws GamaRuntimeException the gama runtime exception
		 */
		Set<Object> neighborValues(IScope scope) throws GamaRuntimeException;

		/**
		 * Sets the editable.
		 *
		 * @param b the new editable
		 */
		void setEditable(boolean b);

		/**
		 * Can be explored.
		 *
		 * @return true, if successful
		 */
		boolean canBeExplored();

	}

	/**
	 * Sets the defined.
	 *
	 * @param b the new defined
	 */
	void setDefined(boolean b);

}