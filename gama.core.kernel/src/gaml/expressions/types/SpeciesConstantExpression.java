/*******************************************************************************************************
 *
 * SpeciesConstantExpression.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.expressions.types;

import gama.kernel.model.IModel;
import gama.metamodel.agent.IAgent;
import gama.metamodel.population.IPopulation;
import gama.runtime.IScope;
import gama.util.ICollector;
import gaml.descriptions.IVarDescriptionUser;
import gaml.descriptions.ModelDescription;
import gaml.descriptions.SpeciesDescription;
import gaml.descriptions.VariableDescription;
import gaml.expressions.ConstantExpression;
import gaml.types.IType;

/**
 * The Class SpeciesConstantExpression.
 */
@SuppressWarnings ({ "rawtypes" })
public class SpeciesConstantExpression extends ConstantExpression {

	/**
	 * Instantiates a new species constant expression.
	 *
	 * @param string the string
	 * @param t the t
	 */
	public SpeciesConstantExpression(final String string, final IType t) {
		super(string, t);
	}

	@Override
	public Object _value(final IScope scope) {
		final IAgent a = scope.getAgent();
		if (a != null) {
			// hqnghi if main description contains micro-description then
			// species comes from micro-model
			final IModel m = scope.getModel();
			final ModelDescription micro = this.getGamlType().getContentType().getSpecies().getModelDescription();
			final ModelDescription main = m == null ? null : (ModelDescription) scope.getModel().getDescription();
			final Boolean fromMicroModel = main == null || main.getMicroModel(micro.getAlias()) != null;
			if (!fromMicroModel) {
				final IPopulation pop = a.getPopulationFor((String) value);
				if (pop != null) { return pop.getSpecies(); }
				return scope.getModel().getSpecies((String) value);
			} else {
				final IPopulation pop = scope.getRoot().getExternMicroPopulationFor(micro.getAlias() + "." + value);
				if (pop != null) { return pop.getSpecies(); }
				return scope.getModel().getSpecies((String) value, this.getGamlType().getContentType().getSpecies());
			}
			// end-hqnghi
		}
		return null;
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		// if ( computed ) { return super.serialize(includingBuiltIn); }
		return (String) value;
	}

	@Override
	public String getDocumentation() {
		return getGamlType().getContentType().getSpecies().getDocumentationWithoutMeta();
	}

	/**
	 * Method collectPlugins()
	 *
	 * @see gama.common.interfaces.IGamlDescription#collectPlugins(java.util.Set)
	 */
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// final SpeciesDescription sd = getGamlType().getContentType().getSpecies();
	// if (sd != null) {
	// meta.put(GamlProperties.PLUGINS, sd.getDefiningPlugin());
	// if (sd.isBuiltIn()) {
	// meta.put(GamlProperties.SPECIES, (String) value);
	// }
	// }
	// }

	@Override
	public boolean isContextIndependant() {
		return false;
	}

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) { return; }
		alreadyProcessed.add(this);
		if (species.hasAttribute(value.toString())) {
			result.add(species.getAttribute(value.toString()));
		}
	}

}