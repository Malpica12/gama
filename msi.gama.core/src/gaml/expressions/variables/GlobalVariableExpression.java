/*******************************************************************************************************
 *
 * msi.gaml.expressions.GlobalVariableExpression.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.expressions.variables;

import gama.common.interfaces.IKeyword;
import gama.common.preferences.GamaPreferences;
import gama.kernel.experiment.ITopLevelAgent;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.ICollector;
import gaml.compilation.GAML;
import gaml.descriptions.IDescription;
import gaml.descriptions.IVarDescriptionUser;
import gaml.descriptions.SpeciesDescription;
import gaml.descriptions.VariableDescription;
import gaml.expressions.IExpression;
import gaml.expressions.IVarExpression;
import gaml.types.IType;

public class GlobalVariableExpression extends VariableExpression implements IVarExpression.Agent {

	public static IExpression create(final String n, final IType<?> type, final boolean notModifiable,
			final IDescription world) {
		final VariableDescription v = ((SpeciesDescription) world).getAttribute(n);
		final IExpression exp = v.getFacetExpr(IKeyword.INIT);
		if (exp != null) {
			// AD Addition of a test on whether the variable is a function or not
			final boolean isConst = notModifiable && exp.isConst() && !v.isFunction();
			if (isConst && GamaPreferences.External.CONSTANT_OPTIMIZATION.getValue())
				return GAML.getExpressionFactory().createConst(exp.getConstValue(), type, n);
		}
		return new GlobalVariableExpression(n, type, notModifiable, world);
	}

	protected GlobalVariableExpression(final String n, final IType<?> type, final boolean notModifiable,
			final IDescription world) {
		super(n, type, notModifiable, world);
	}

	@Override
	public boolean isConst() {
		// Allow global variables to report that they are constant if they are noted so (except if they are containers).
		if (type.isContainer()) return false;
		VariableDescription vd = getDefinitionDescription().getSpeciesContext().getAttribute(name);
		if (vd == null || vd.isFunction()) return false;
		return isNotModifiable;
	}

	@Override
	public IExpression getOwner() {
		return this.getDefinitionDescription().getModelDescription().getVarExpr(IKeyword.WORLD_AGENT_NAME, false);
	}

	@Override
	public Object _value(final IScope scope) throws GamaRuntimeException {
		final String name = getName();
		// We first try in the 'normal' scope (so that regular global vars are still accessed by agents of micro-models,
		// see #2238)
		if (scope.hasAccessToGlobalVar(name))
			return scope.getGlobalVarValue(name);
		else {
			final IAgent microAgent = scope.getAgent();
			if (microAgent != null) {
				final IScope agentScope = microAgent.getScope();
				if (agentScope != null) {
					final ITopLevelAgent root = agentScope.getRoot();
					if (root != null) {
						final IScope globalScope = root.getScope();
						if (globalScope != null) return globalScope.getGlobalVarValue(getName());
					}
				}
			}
		}

		return null;
	}

	@Override
	public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
		if (isNotModifiable) return;
		if (scope.hasAccessToGlobalVar(name)) {
			scope.setGlobalVarValue(name, v);
		} else {
			final IAgent sc = scope.getAgent();
			if (sc != null) { sc.getScope().getRoot().getScope().setGlobalVarValue(name, v); }
		}
	}

	@Override
	public String getTitle() {
		final IDescription desc = getDefinitionDescription();
		final boolean isParameter =
				desc == null ? false : desc.getSpeciesContext().getAttribute(getName()).isParameter();
		return "global " + (isParameter ? "parameter" : isNotModifiable ? "constant" : "attribute") + " " + getName()
				+ " of type " + getGamlType().getTitle();
	}

	@Override
	public String getDocumentation() {
		final IDescription desc = getDefinitionDescription();
		String doc = null;
		String s = "Type " + type.getTitle();
		if (desc != null) {
			final VariableDescription var = desc.getSpeciesContext().getAttribute(name);
			if (var != null) { doc = var.getBuiltInDoc(); }
		} else
			return s;
		if (doc != null) { s += "<br>" + doc; }
		final String quality =
				(desc.isBuiltIn() ? "<br>Built In " : doc == null ? "<br>Defined in " : "<br>Redefined in ")
						+ desc.getTitle();

		return s + quality;
	}

	@Override
	public void collectUsedVarsOf(final SpeciesDescription species,
			final ICollector<IVarDescriptionUser> alreadyProcessed, final ICollector<VariableDescription> result) {
		if (alreadyProcessed.contains(this)) return;
		alreadyProcessed.add(this);
		final SpeciesDescription sd = this.getDefinitionDescription().getSpeciesContext();
		if (species.equals(sd)) { result.add(sd.getAttribute(getName())); }
	}

}
