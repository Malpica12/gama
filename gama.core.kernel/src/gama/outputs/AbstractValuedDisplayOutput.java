/*******************************************************************************************************
 *
 * msi.gama.outputs.AbstractValuedDisplayOutput.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.outputs;

import gama.common.interfaces.IKeyword;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.compilation.GAML;
import gaml.descriptions.IDescription;
import gaml.expressions.IExpression;

public abstract class AbstractValuedDisplayOutput extends AbstractDisplayOutput {

	protected String expressionText = "";
	protected IExpression value;
	protected Object lastValue = "";

	public AbstractValuedDisplayOutput(final IDescription desc) {
		super(desc);
		setValue(getFacet(IKeyword.VALUE));
		expressionText = getValue() == null ? "" : getValue().serialize(false);
	}

	public Object getLastValue() {
		return lastValue;
	}

	public IExpression getValue() {
		return value;
	}

	public String getExpressionText() {
		return expressionText == null ? "" : expressionText;
	}

	public boolean setNewExpressionText(final String string) {
		expressionText = string;
		setValue(GAML.compileExpression(string, getScope().getSimulation(), true));
		return getScope().step(this).passed();
	}

	public void setNewExpression(final IExpression expr) throws GamaRuntimeException {
		expressionText = expr == null ? "" : expr.serialize(false);
		setValue(expr);
		getScope().step(this);
	}

	protected void setValue(final IExpression value) {
		this.value = value;
	}

}
