/*******************************************************************************************************
 *
 * GamaSpeciesType.java, in gama.core.kernel, is part of the source code of the
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
import gama.metamodel.agent.IAgent;
import gama.metamodel.population.IPopulationSet;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.expressions.IExpression;
import gaml.species.ISpecies;

/**
 * The type used for representing species objects (since they can be manipulated in a model)
 * 
 * Written by drogoul Modified on 1 aout 2010.
 *
 * @todo Description
 */
@type (
		name = IKeyword.SPECIES,
		id = IType.SPECIES,
		wraps = { ISpecies.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE, IConcept.SPECIES },
		doc = @doc ("Meta-type of the species present in the GAML language"))
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class GamaSpeciesType extends GamaContainerType<ISpecies> {

	@Override
	public ISpecies cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		// TODO Add a more general cast with list of agents to find a common
		// species.
		ISpecies species = obj == null ? getDefault()
				: obj instanceof ISpecies ? (ISpecies) obj : obj instanceof IAgent ? ((IAgent) obj).getSpecies()
						: obj instanceof String ? scope.getModel().getSpecies((String) obj) : getDefault();
		if (obj instanceof IPopulationSet) {
			species = ((IPopulationSet) obj).getSpecies();
		}
		return species;
	}

	@Override
	public ISpecies cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentType, final boolean copy) {

		final ISpecies result = cast(scope, obj, param, copy);
		if (result == null) {
			if (contentType.isAgentType()) { return scope.getModel().getSpecies(contentType.getName()); }
		}
		return result;
	}

	// TODO Verify that we dont need to declare the other cast method

	@Override
	public ISpecies getDefault() {
		return null;
	}

	@Override
	public IType getContentType() {
		return Types.get(AGENT);
	}

	@Override
	public IType getKeyType() {
		return Types.INT;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		final IType itemType = exp.getGamlType();
		if (itemType.isAgentType()) { return itemType; }
		switch (exp.getGamlType().id()) {
			case SPECIES:
				return itemType.getContentType();
			case IType.STRING:
				return Types.AGENT;
		}
		return exp.getGamlType();
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

}
