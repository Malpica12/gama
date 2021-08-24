/*********************************************************************************************
 *
 * 'InputParameter.java, in plugin gama.ui.base, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.kernel.experiment;

import java.util.List;

import gama.runtime.IScope;
import gama.util.GamaColor;
import gaml.types.GamaType;
import gaml.types.IType;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class InputParameter extends ParameterAdapter {

	private Object value;
	private final List among;
	private Comparable min, max;
	private Comparable step;

	public InputParameter(final String name, final Object value) {
		this(name, value, GamaType.of(value));
	}

	public InputParameter(final String name, final Object value, final IType type) {
		this(name, value, type, null);
	}

	public InputParameter(final String name, final Object value, final IType type, final List among) {
		super(name, type.id());
		this.value = value;
		this.among = among;
	}

	public InputParameter(final String name, final Object value, final Comparable min, final Comparable max) {
		this(name, value);
		this.min = min;
		this.max = max;
	}

	public InputParameter(final String name, final Object value, final Comparable min, final Comparable max,
			final Comparable step) {
		this(name, value);
		this.min = min;
		this.max = max;
		this.step = step;
	}

	public InputParameter(final String name, final String unit, final Object value, final Comparable min,
			final Comparable max, final Comparable step) {
		this(name, value, min, max);
		unitLabel = unit;
		this.step = step;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setValue(final IScope scope, final Object value) {
		this.value = value;
	}

	@Override
	public Comparable getMinValue(final IScope scope) {
		return min;
	}

	@Override
	public Comparable getMaxValue(final IScope scope) {
		return max;
	}

	@Override
	public List getAmongValue(final IScope scope) {
		return among;
	}

	@Override
	public Comparable getStepValue(final IScope scope) {
		return step;
	}

	@Override
	public Object value() {
		return value;
	}

	@Override
	public Object value(final IScope scope) {
		return value;
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public List<GamaColor> getColor(final IScope scope) {
		return null;
	}

	@Override
	public boolean acceptsSlider(final IScope scope) {
		return true;
	}

}