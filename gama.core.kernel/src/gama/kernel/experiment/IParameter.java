/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.IParameter.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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
 * Written by drogoul Modified on 4 juin 2010
 *
 * @todo Description
 *
 */
public interface IParameter extends IExperimentDisplayable {

	public interface ParameterChangeListener {
		void changed(IScope scope, Object newValue);
	}

	String[] EMPTY_STRINGS = {};

	void setValue(IScope scope, Object value);

	Object value(IScope scope) throws GamaRuntimeException;

	@SuppressWarnings ("rawtypes")
	IType getType();

	@Override
	String serialize(boolean includingBuiltIn);

	Object getInitialValue(IScope scope);

	Object getMinValue(IScope scope);

	Object getMaxValue(IScope scope);

	@SuppressWarnings ("rawtypes")
	List getAmongValue(IScope scope);

	boolean isEditable();

	List<GamaColor> getColor(final IScope scope);

	boolean acceptsSlider(IScope scope);

	Comparable getStepValue(IScope scope);

	boolean isDefined();

	default String[] getEnablement() {
		return EMPTY_STRINGS;
	}

	default String[] getDisablement() {
		return EMPTY_STRINGS;
	}

	default void addChangedListener(final ParameterChangeListener listener) {
		// Nothing to do by default
	}

	public interface Batch extends IParameter {

		Object value();

		void setCategory(String cat);

		void reinitRandomly(IScope scope);

		Set<Object> neighborValues(IScope scope) throws GamaRuntimeException;

		void setEditable(boolean b);

		boolean canBeExplored();

	}

	/**
	 * @param b
	 */
	void setDefined(boolean b);

}