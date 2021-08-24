/*******************************************************************************************************
 *
 * GamaContainerType.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.types;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.type;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IContainer;
import gama.util.IList;
import gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 11 nov. 2011
 * 
 * A generic type for containers. Tentative.
 *
 * @param <T> the generic type
 */
@type (
		name = IKeyword.CONTAINER,
		id = IType.CONTAINER,
		wraps = { IContainer.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.CONTAINER },
		doc = @doc ("Generic super-type of all the container types (list, graph, matrix, etc.)"))
public class GamaContainerType<T extends IContainer<?, ?>> extends GamaType<T> implements IContainerType<T> {

	@Override
	public T cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return cast(scope, obj, param, getKeyType(), getContentType(), copy);
		// return (T) (obj instanceof IContainer ? (IContainer) obj :
		// Types.get(LIST).cast(scope, obj, null,
		// Types.NO_TYPE, Types.NO_TYPE));
	}

	@Override
	public int getNumberOfParameters() {
		return 1;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public T cast(final IScope scope, final Object obj, final Object param, final IType<?> keyType,
			final IType<?> contentType, final boolean copy) throws GamaRuntimeException {
		// by default
		return (T) (obj instanceof IContainer ? (IContainer<?, ?>) obj
				: (IList<?>) Types.get(LIST).cast(scope, obj, null, copy));
	}

	@Override
	public T getDefault() {
		return null;
	}

	@Override
	public IContainerType<T> getGamlType() {
		return this;
	}

	@Override
	public boolean isContainer() {
		return true;
	}

	@Override
	public boolean isCompoundType() {
		return true;
	}

	@Override
	public boolean isFixedLength() {
		return false;
	}

	@Override
	public IType<?> contentsTypeIfCasting(final IExpression exp) {
		final IType<?> itemType = exp.getGamlType();
		if (itemType.isContainer() || itemType.isAgentType() || itemType.isCompoundType()) {
			return itemType.getContentType();
		}
		return itemType;
	}

	@Override
	public IContainerType<?> typeIfCasting(final IExpression exp) {
		return (IContainerType<?>) super.typeIfCasting(exp);
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

	@SuppressWarnings ("unchecked")
	@Override
	public IContainerType<?> of(final IType<?> sub1) {
		final IType<?> kt = getKeyType();
		IType<?> ct = sub1;
		if (ct == Types.NO_TYPE) {
			if (kt == Types.NO_TYPE) { return this; }
			ct = getContentType();
		}
		return ParametricType.createParametricType((IContainerType<IContainer<?, ?>>) this, kt, ct);

	}

	@SuppressWarnings ("unchecked")
	@Override
	public IContainerType<?> of(final IType<?> sub1, final IType<?> sub2) {
		IType<?> kt = sub1;
		IType<?> ct = sub2;
		if (ct == Types.NO_TYPE) {
			if (kt == Types.NO_TYPE) { return this; }
			ct = getContentType();
		}
		if (kt == Types.NO_TYPE) {
			kt = getKeyType();
		}
		return ParametricType.createParametricType((IContainerType<IContainer<?, ?>>) this, kt, ct);

	}

}