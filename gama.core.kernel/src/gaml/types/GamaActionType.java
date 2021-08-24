/*******************************************************************************************************
 *
 * GamaActionType.java, in gama.core.kernel, is part of the source code of the
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
import gaml.descriptions.IDescription;

/**
 * The Class GamaActionType.
 */
@type (
		name = IKeyword.ACTION,
		id = IType.ACTION,
		wraps = { IDescription.class },
		kind = ISymbolKind.Variable.REGULAR,
		doc = { @doc ("The type of the variables that denote an action or an aspect of a species") },
		concept = { IConcept.TYPE, IConcept.ACTION, IConcept.SPECIES })
public class GamaActionType extends GamaType<IDescription> {

	@Override
	public boolean canCastToConst() {
		return false;
	}

	@Override
	public IDescription cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		if (obj == null)
			return null;
		if (obj instanceof IDescription)
			return (IDescription) obj;
		if (obj instanceof String) {
			final String name = (String) obj;
			final IDescription action = scope.getAgent().getSpecies().getDescription().getAction(name);
			if (action != null)
				return action;
			return scope.getAgent().getSpecies().getDescription().getAspect(name);
		}
		return null;
	}

	@Override
	public IDescription getDefault() {
		// TODO Auto-generated method stub
		return null;
	}

}
