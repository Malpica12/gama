/*********************************************************************************************
 * 
 *
, * 'SimpleBdiPlanStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package gama.ext.bdi;

import gama.common.interfaces.IGamlIssue;
import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.facet;
import gama.core.dev.annotations.GamlAnnotations.facets;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.ext.bdi.SimpleBdiPlanStatement.SimpleBdiPlanValidator;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.compilation.IDescriptionValidator;
import gaml.compilation.annotations.validator;
import gaml.descriptions.IDescription;
import gaml.descriptions.SkillDescription;
import gaml.descriptions.SpeciesDescription;
import gaml.descriptions.StatementDescription;
import gaml.expressions.IExpression;
import gaml.operators.Cast;
import gaml.statements.AbstractStatementSequence;
import gaml.types.IType;

@symbol(name = { SimpleBdiArchitecture.PLAN }, kind = ISymbolKind.BEHAVIOR, with_sequence = true, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true),
		@facet(name = SimpleBdiArchitecture.FINISHEDWHEN, type = IType.BOOL, optional = true),
		@facet(name = SimpleBdiArchitecture.PRIORITY, type = IType.FLOAT, optional = true),
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true),
		@facet(name = SimpleBdiPlanStatement.INTENTION, type = PredicateType.id, optional = true),
		@facet(name = SimpleBdiPlanStatement.EMOTION, type = EmotionType.id, optional = true),
		@facet(name = SimpleBdiPlanStatement.THRESHOLD, type = IType.FLOAT, optional = true),
		@facet(name = SimpleBdiArchitecture.INSTANTANEAOUS, type = IType.BOOL, optional = true) }, omissible = IKeyword.NAME)
@validator(SimpleBdiPlanValidator.class)
@doc("define an action plan performed by an agent using the BDI engine")
public class SimpleBdiPlanStatement extends AbstractStatementSequence {

	public static class SimpleBdiPlanValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 * 
		 * @see gaml.compilation.IDescriptionValidator#validate(gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription description) {
			// Verify that the state is inside a species with fsm control
			final SpeciesDescription species = description.getSpeciesContext();
			final SkillDescription control = species.getControl();
			if (!SimpleBdiArchitecture.class.isAssignableFrom(control.getJavaBase())) {
				description.error("A plan can only be defined in a simple_bdi architecture species",
						IGamlIssue.WRONG_CONTEXT);
				return;
			}
		}
	}

	public static final String INTENTION = "intention";
	public static final String EMOTION = "emotion";
	public static final String THRESHOLD = "threshold";

	final IExpression _when;
	final IExpression _priority;
	final IExpression _executedwhen;
	final IExpression _instantaneous;
	final IExpression _intention;
	final IExpression _emotion;
	final IExpression _threshold;

	public IExpression getPriorityExpression() {
		return _priority;
	}

	public IExpression getContextExpression() {
		return _when;
	}

	public IExpression getExecutedExpression() {
		return _executedwhen;
	}

	public IExpression getInstantaneousExpression() {
		return _instantaneous;
	}

	public IExpression getIntentionExpression() {
		return _intention;
	}

	public IExpression getEmotionExpression() {
		return _emotion;
	}

	public IExpression getThreshold() {
		return _threshold;
	}

	public SimpleBdiPlanStatement(final IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		_priority = getFacet(SimpleBdiArchitecture.PRIORITY);
		_executedwhen = getFacet(SimpleBdiArchitecture.FINISHEDWHEN);
		_instantaneous = getFacet(SimpleBdiArchitecture.INSTANTANEAOUS);
		_intention = getFacet(SimpleBdiPlanStatement.INTENTION);
		_emotion = getFacet(SimpleBdiPlanStatement.EMOTION);
		_threshold = getFacet(SimpleBdiPlanStatement.THRESHOLD);
		setName(desc.getName());
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (_when == null || Cast.asBool(scope, _when.value(scope))) {
			return super.privateExecuteIn(scope);
		}
		return null;
	}

	public Double computePriority(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, _priority.value(scope));
	}
}
