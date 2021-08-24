/*******************************************************************************************************
 *
 * TerminalMapBasedScope.java, in gama.core.lang, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.core.lang.scoping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

import gama.util.IMap;

/**
 * The Class TerminalMapBasedScope.
 */
public class TerminalMapBasedScope implements IScope {

	/** The elements. */
	private final IMap<QualifiedName, IEObjectDescription> elements;

	/**
	 * Instantiates a new terminal map based scope.
	 *
	 * @param elements the elements
	 */
	protected TerminalMapBasedScope(final IMap<QualifiedName, IEObjectDescription> elements) {
		this.elements = elements;
	}

	@Override
	public IEObjectDescription getSingleElement(final QualifiedName name) {
		return elements.get(name);
	}

	@Override
	public Iterable<IEObjectDescription> getAllElements() {
		return elements.values();
	}

	@Override
	public Iterable<IEObjectDescription> getElements(final QualifiedName name) {
		final IEObjectDescription result = elements.get(name);
		if (result == null) { return Collections.emptyList(); }
		return Collections.singleton(result);
	}

	@Override
	public IEObjectDescription getSingleElement(final EObject object) {
		final List<IEObjectDescription> list = getElements(object);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public List<IEObjectDescription> getElements(final EObject object) {
		final URI uri = EcoreUtil2.getPlatformResourceOrNormalizedURI(object);
		final IEObjectDescription[] result = new IEObjectDescription[1];
		elements.forEachPair((s, input) -> {
			if (input.getEObjectOrProxy() == object || uri.equals(input.getEObjectURI())) {
				result[0] = input;
				return false;
			}
			return true;
		});

		return result[0] == null ? Collections.EMPTY_LIST : Arrays.asList(result);
	}

}