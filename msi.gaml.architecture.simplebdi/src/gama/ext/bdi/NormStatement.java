package gama.ext.bdi;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.facet;
import gama.core.dev.annotations.GamlAnnotations.facets;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.descriptions.IDescription;
import gaml.expressions.IExpression;
import gaml.operators.Cast;
import gaml.statements.AbstractStatementSequence;
import gaml.types.IType;

@symbol(name = { NormStatement.NORM }, kind = ISymbolKind.BEHAVIOR, with_sequence = true, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("the boolean condition when the norm is active")),
		@facet(name = SimpleBdiArchitecture.FINISHEDWHEN, type = IType.BOOL, optional = true, doc = @doc("the boolean condition when the norm is finished")),
		@facet(name = SimpleBdiArchitecture.PRIORITY, type = IType.FLOAT, optional = true, doc = @doc("the priority value of the norm")),
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the name of the norm")),
		@facet(name = NormStatement.INTENTION, type = PredicateType.id, optional = true, doc = @doc("the intention triggering the norm")),
		@facet(name = NormStatement.OBLIGATION, type = PredicateType.id, optional = true, doc = @doc("the obligation triggering of the norm")),
		@facet(name = NormStatement.THRESHOLD, type = IType.FLOAT, optional = true, doc = @doc("the threshold to trigger the norm")),
		@facet(name = NormStatement.LIFETIME, type = IType.INT, optional = true, doc = @doc("the lifetime of the norm")),
		@facet(name = SimpleBdiArchitecture.INSTANTANEAOUS, type = IType.BOOL, optional = true, doc = @doc("indicates if the norm is instananeous")) }, omissible = IKeyword.NAME)
@doc("a norm indicates what action the agent has to do in a certain context and with and obedience value higher than the threshold")

public class NormStatement extends AbstractStatementSequence{

	public static final String NORM = "norm";
	public static final String INTENTION = "intention";
	public static final String OBLIGATION = "obligation";
	public static final String THRESHOLD = "threshold";
	public static final String LIFETIME = "lifetime";
	
	final IExpression _when;
	final IExpression _priority;
	final IExpression _executedwhen;
	final IExpression _instantaneous;
	final IExpression _intention;
	final IExpression _obligation;
	final IExpression _threshold;
	final IExpression _lifetime;
	
	public IExpression getContextExpression() {
		return _when;
	}
	
	public IExpression getExecutedExpression() {
		return _executedwhen;
	}
	
	public IExpression getIntentionExpression() {
		return _intention;
	}
	
	public IExpression getObligationExpression() {
		return _obligation;
	}
	
	public IExpression getInstantaneousExpression() {
		return _instantaneous;
	}
	
	public IExpression getThreshold() {
		return _threshold;
	}
	
	public IExpression getPriorityExpression() {
		return _priority;
	}
	
	public NormStatement(IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		_priority = getFacet(SimpleBdiArchitecture.PRIORITY);
		_executedwhen = getFacet(SimpleBdiArchitecture.FINISHEDWHEN);
		_instantaneous = getFacet(SimpleBdiArchitecture.INSTANTANEAOUS);
		_intention = getFacet(NormStatement.INTENTION);
		_obligation = getFacet(NormStatement.OBLIGATION);
		_threshold = getFacet(NormStatement.THRESHOLD);
		_lifetime = getFacet(NormStatement.LIFETIME);
		setName(desc.getName());
	}

	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (_when == null || Cast.asBool(scope, _when.value(scope))) {
			return super.privateExecuteIn(scope);
		}
		return null;
	}
	
}
