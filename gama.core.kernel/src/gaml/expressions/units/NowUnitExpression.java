/*******************************************************************************************************
 *
 * NowUnitExpression.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.expressions.units;

import java.time.LocalDateTime;

import gama.runtime.IScope;
import gama.util.GamaDate;
import gaml.types.Types;

/**
 * The Class NowUnitExpression.
 */
public class NowUnitExpression extends UnitConstantExpression {

	/**
	 * Instantiates a new now unit expression.
	 *
	 * @param name the name
	 * @param doc the doc
	 */
	public NowUnitExpression(final String name, final String doc) {
		super(1.0, Types.DATE, name, doc, null);
	}

	@Override
	public GamaDate _value(final IScope scope) {
		return GamaDate.of(LocalDateTime.now());
	}

	@Override
	public boolean isConst() {
		return false;
	}

}