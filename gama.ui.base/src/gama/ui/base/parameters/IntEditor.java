/*********************************************************************************************
 *
 * 'IntEditor.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.base.parameters;

import gama.kernel.experiment.IParameter;
import gama.kernel.experiment.InputParameter;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.ui.base.interfaces.EditorListener;
import gaml.operators.Cast;
import gaml.types.IType;
import gaml.types.Types;

public class IntEditor extends NumberEditor<Integer> {

	IntEditor(final IScope scope, final IAgent agent, final IParameter param, final boolean canBeNull,
			final EditorListener<Integer> l) {
		super(scope, agent, param, l, canBeNull);
	}

	IntEditor(final IScope scope, final EditorsGroup parent, final String title, final String unit, final Integer value,
			final Integer min, final Integer max, final Integer step, final EditorListener<Integer> whenModified,
			final boolean canBeNull) {
		super(scope, new InputParameter(title, unit, value, min, max, step), whenModified, canBeNull);
		createControls(parent);
	}

	@Override
	protected Integer defaultStepValue() {
		return 1;
	}

	@Override
	protected Integer applyPlus() {
		if (currentValue == null) return 0;
		final Integer i = currentValue;
		return i + stepValue.intValue();
	}

	@Override
	protected Integer applyMinus() {
		if (currentValue == null) return 0;
		final Integer i = currentValue;
		return i - stepValue.intValue();
	}

	@Override
	protected boolean modifyValue(final Object val) throws GamaRuntimeException {
		final int i = Cast.asInt(getScope(), val);
		if (getMinValue() != null && i < Cast.asInt(getScope(), getMinValue()))
			throw GamaRuntimeException.error("Value " + i + " should be greater than " + getMinValue(), getScope());
		if (maxValue != null && i > Cast.asInt(getScope(), getMaxValue()))
			throw GamaRuntimeException.error("Value " + i + " should be smaller than " + maxValue, getScope());
		return super.modifyValue(i);
	}

	@Override
	protected void updateToolbar() {
		super.updateToolbar();
		editorToolbar.enable(PLUS,
				param.isDefined() && (getMaxValue() == null || applyPlus() < Cast.asInt(getScope(), getMaxValue())));
		editorToolbar.enable(MINUS,
				param.isDefined() && (getMinValue() == null || applyMinus() > Cast.asInt(getScope(), getMinValue())));
	}

	@Override
	protected Integer normalizeValues() throws GamaRuntimeException {
		final Integer valueToConsider = getOriginalValue() == null ? 0 : Cast.asInt(getScope(), getOriginalValue());
		currentValue = getOriginalValue() == null ? null : valueToConsider;
		minValue = getMinValue() == null ? null : Cast.asInt(getScope(), getMinValue());
		maxValue = getMaxValue() == null ? null : Cast.asInt(getScope(), getMaxValue());
		return valueToConsider;
	}

	@Override
	public IType<Integer> getExpectedType() {
		return Types.INT;
	}

}
