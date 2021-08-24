/*******************************************************************************************************
 *
 * StatementFactory.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.factories;

import org.eclipse.emf.ecore.EObject;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.factory;
import gaml.descriptions.ActionDescription;
import gaml.descriptions.IDescription;
import gaml.descriptions.PrimitiveDescription;
import gaml.descriptions.StatementDescription;
import gaml.descriptions.StatementRemoteWithChildrenDescription;
import gaml.descriptions.StatementWithChildrenDescription;
import gaml.descriptions.SymbolProto;
import gaml.statements.Facets;

/**
 * Written by drogoul Modified on 8 févr. 2010
 *
 * @todo Description
 *
 */
@factory (
		handles = { ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.SINGLE_STATEMENT, ISymbolKind.BEHAVIOR,
				ISymbolKind.ACTION, ISymbolKind.LAYER, ISymbolKind.BATCH_METHOD, ISymbolKind.OUTPUT })
public class StatementFactory extends SymbolFactory implements IKeyword {

	/**
	 * Instantiates a new statement factory.
	 *
	 * @param handles the handles
	 */
	public StatementFactory(final int... handles) {
		super(handles);
	}

	@Override
	protected StatementDescription buildDescription(final String keyword, final Facets facets, final EObject element,
			final Iterable<IDescription> children, final IDescription enclosing, final SymbolProto proto) {
		if (proto.isPrimitive()) { return new PrimitiveDescription(enclosing, element, children, facets, null); }
		if (keyword.equals(ACTION)) { return new ActionDescription(keyword, enclosing, children, element, facets); }
		if (proto.hasSequence() && children != null) {
			if (proto.isRemoteContext()) {
				return new StatementRemoteWithChildrenDescription(keyword, enclosing, children, proto.hasArgs(),
						element, facets, null);
			}
			return new StatementWithChildrenDescription(keyword, enclosing, children, proto.hasArgs(), element, facets,
					null);
		}
		return new StatementDescription(keyword, enclosing, proto.hasArgs(), /* children, */ element, facets, null);
	}

}
