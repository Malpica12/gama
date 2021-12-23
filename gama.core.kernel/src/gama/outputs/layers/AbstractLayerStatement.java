/*******************************************************************************************************
 *
 * AbstractLayerStatement.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.outputs.layers;

import com.google.common.primitives.Ints;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.outputs.IDisplayOutput;
import gama.outputs.LayeredDisplayData;
import gama.outputs.LayeredDisplayOutput;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.compilation.ISymbol;
import gaml.compilation.Symbol;
import gaml.descriptions.IDescription;
import gaml.expressions.IExpression;

// TODO: Auto-generated Javadoc
/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * GAML statement to define the properties of a layer in a display
 *
 * @todo Description
 *
 */
@inside (
		symbols = IKeyword.DISPLAY)
public abstract class AbstractLayerStatement extends Symbol implements ILayerStatement {

	/** The output. */
	LayeredDisplayOutput output;

	/**
	 * Checks if is to create.
	 *
	 * @return true, if is to create
	 */
	public boolean isToCreate() { return true; }

	/**
	 * Instantiates a new abstract layer statement.
	 *
	 * @param desc the desc
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public AbstractLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setName(desc.getName());
	}

	/**
	 * Gets the refresh facet.
	 *
	 * @return the refresh facet
	 */
	@Override
	public IExpression getRefreshFacet() {
		return getFacet(IKeyword.REFRESH);
	}

	/**
	 * Compare to.
	 *
	 * @param o the o
	 * @return the int
	 */
	@Override
	public int compareTo(final ILayerStatement o) {
		return Ints.compare(getOrder(), o.getOrder());
	}

	/**
	 * Inits the.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 */
	@Override
	public final boolean init(final IScope scope) {
		return _init(scope);
	}

	/**
	 * Inits the.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 */
	protected abstract boolean _init(IScope scope);

	/**
	 * Sets the display output.
	 *
	 * @param out the new display output
	 */
	@Override
	public void setDisplayOutput(final IDisplayOutput out) {
		output = (LayeredDisplayOutput) out;
	}

	/**
	 * Gets the display output.
	 *
	 * @return the display output
	 */
	public LayeredDisplayOutput getDisplayOutput() {
		return output;
	}

	/**
	 * Gets the layered display data.
	 *
	 * @return the layered display data
	 */
	public LayeredDisplayData getLayeredDisplayData() {
		if (output == null) return null;
		return output.getData();
	}

	/**
	 * Step.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	@Override
	public final boolean step(final IScope scope) throws GamaRuntimeException {
		if (!scope.interrupted()) return _step(scope);
		return false;
	}

	/**
	 * Step.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 */
	protected abstract boolean _step(IScope scope);

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

}