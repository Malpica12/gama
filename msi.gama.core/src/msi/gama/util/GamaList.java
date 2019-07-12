/*******************************************************************************************************
 *
 * msi.gama.util.GamaList.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaIntegerType;
import msi.gaml.types.GamaType;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import one.util.streamex.StreamEx;

/**
 * Written by drogoul Modified on 21 nov. 2008
 *
 * @todo Description
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaList<E> extends ArrayList<E> implements IList<E> {

	private IContainerType type;

	@Override
	public IContainerType<?> getGamlType() {
		return type;
	}

	@Override
	public StreamEx<E> stream(final IScope scope) {
		return StreamEx.<E> of((ArrayList<E>) this);
	}

	protected GamaList(final int capacity, final IType contentType) {
		super(capacity);
		this.type = Types.LIST.of(contentType);
	}

	@Override
	public IList<E> listValue(final IScope scope, final IType contentsType, final boolean copy) {
		if (!GamaType.requiresCasting(contentsType, getGamlType().getContentType())) {
			if (copy) { return this.cloneWithContentType(contentsType); }
			return this;
		}
		final GamaList clone = this.cloneWithContentType(contentsType);
		final int n = size();
		for (int i = 0; i < n; i++) {
			clone.setValueAtIndex(scope, i, get(i));
		}
		return clone;
	}

	@Override
	public void addValue(final IScope scope, final E object) {
		super.add(buildValue(scope, object));
	}

	@Override
	public void addValueAtIndex(final IScope scope, final Object index, final E object) {
		super.add(buildIndex(scope, index), buildValue(scope, object));
	}

	@Override
	public void setValueAtIndex(final IScope scope, final Object index, final E value) {
		super.set(buildIndex(scope, index), buildValue(scope, value));
	}

	@Override
	public void addValues(final IScope scope, final IContainer values) {
		super.addAll(buildValues(scope, values));
	}

	@Override
	public void setAllValues(final IScope scope, final E value) {
		final E element = buildValue(scope, value);
		for (int i = 0, n = size(); i < n; i++) {
			super.set(i, element);
		}
	}

	@Override
	public void removeValue(final IScope scope, final Object value) {
		remove(value);
	}

	@Override
	public void removeIndex(final IScope scope, final Object index) {
		if (index instanceof Integer) {
			remove(((Integer) index).intValue());
		}
	}

	@Override
	public void removeValues(final IScope scope, final IContainer values) {
		if (values instanceof Collection) {
			removeAll((Collection) values);
		} else {
			removeAll(values.listValue(scope, Types.NO_TYPE, false));
		}
	}

	@Override
	public void removeAllOccurrencesOfValue(final IScope scope, final Object value) {
		for (final Iterator iterator = iterator(); iterator.hasNext();) {
			final Object obj = iterator.next();
			if (obj.equals(value)) {
				iterator.remove();
			}
		}
	}

	@Override
	public E firstValue(final IScope scope) {
		if (size() == 0) { return null; }
		return get(0);
	}

	@Override
	public E lastValue(final IScope scope) {
		if (size() == 0) { return null; }
		return get(size() - 1);
	}

	@Override
	public E get(final IScope scope, final Integer index) {
		return get(index.intValue());
	}

	@Override
	public int length(final IScope scope) {
		return size();
	}

	@Override
	public IContainer<Integer, E> reverse(final IScope scope) {
		final IList list = copy(scope);
		Collections.reverse(list);
		return list;
	}

	private GamaList cloneWithContentType(final IType contentType) {
		final GamaList clone = (GamaList) super.clone();
		clone.type = Types.LIST.of(contentType);
		return clone;
	}

	@Override
	public IList<E> copy(final IScope scope) {
		return cloneWithContentType(type.getContentType());
		// return GamaListFactory.create(scope, type.getContentType(), this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.interfaces.IGamaContainer#checkBounds(java.lang.Object)
	 */
	@Override
	public boolean checkBounds(final IScope scope, final Object object, final boolean forAdding) {
		if (object instanceof Integer) {
			final Integer index = (Integer) object;
			final int size = size();
			final boolean upper = forAdding ? index <= size : index < size;
			return index >= 0 && upper;
		} else if (object instanceof IContainer) {
			for (final Object o : ((IContainer) object).iterable(scope)) {
				if (!checkBounds(scope, o, forAdding)) { return false; }
			}
		}
		return false;
	}

	@Override
	public E anyValue(final IScope scope) {
		if (isEmpty()) { return null; }
		final int i = scope.getRandom().between(0, size() - 1);
		return get(i);
	}

	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return contains(o);
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return isEmpty();
	}

	@Override
	public Iterable<? extends E> iterable(final IScope scope) {
		return this;
	}

	@Override
	public E getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		if (indices == null || indices.isEmpty()) { return null; }
		return get(scope, Cast.asInt(scope, indices.get(0)));
		// We do not consider the case where multiple indices are used. Maybe
		// could be used in the
		// future to return a list of values ?
	}

	/**
	 * Method removeIndexes()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#removeIndexes(msi.gama.runtime.IScope, msi.gama.util.IContainer)
	 */
	@Override
	public void removeIndexes(final IScope scope, final IContainer<?, ?> index) {
		final IList<Integer> l = (IList<Integer>) index.listValue(scope, Types.INT, false);
		Collections.sort(l, Collections.reverseOrder());
		for (final Integer i : l) {
			remove(i.intValue());
		}
	}

	/**
	 * Method buildValue()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValue(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public E buildValue(final IScope scope, final Object object) {
		final IType ct = type.getContentType();
		return (E) ct.cast(scope, object, null, false);
	}

	/**
	 * Method buildValues()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildValues(msi.gama.runtime.IScope, msi.gama.util.IContainer,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public IList<E> buildValues(final IScope scope, final IContainer objects) {
		return (IList<E>) type.cast(scope, objects, null, false);
	}

	/**
	 * Method buildIndex()
	 *
	 * @see msi.gama.util.IContainer.Modifiable#buildIndex(msi.gama.runtime.IScope, java.lang.Object,
	 *      msi.gaml.types.IContainerType)
	 */
	@Override
	public Integer buildIndex(final IScope scope, final Object object) {
		return GamaIntegerType.staticCast(scope, object, null, false);
	}

	@Override
	public IContainer<?, Integer> buildIndexes(final IScope scope, final IContainer value) {
		final IList<Integer> result = GamaListFactory.create(Types.INT);
		for (final Object o : value.iterable(scope)) {
			result.add(buildIndex(scope, o));
		}
		return result;
	}

}
