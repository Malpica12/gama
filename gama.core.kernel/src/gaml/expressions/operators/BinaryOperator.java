/*******************************************************************************************************
 *
 * msi.gaml.expressions.BinaryOperator.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.expressions.operators;

import gama.common.interfaces.IKeyword;
import gama.common.preferences.GamaPreferences;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.compilation.GAML;
import gaml.compilation.GamaGetter;
import gaml.descriptions.IDescription;
import gaml.descriptions.OperatorProto;
import gaml.expressions.IExpression;
import gaml.expressions.IVarExpression;
import gaml.expressions.IVarExpression.Agent;
import gaml.expressions.variables.VariableExpression;
import gaml.operators.Cast;

/**
 * The Class BinaryOperator.
 */
public class BinaryOperator extends AbstractNAryOperator {

	public static IExpression create(final OperatorProto proto, final IDescription context,
			final IExpression... child) {
		final BinaryOperator u = new BinaryOperator(proto, context, child);
		if (u.isConst() && GamaPreferences.External.CONSTANT_OPTIMIZATION.getValue()) {
			return GAML.getExpressionFactory().createConst(u.getConstValue(), u.getGamlType(), u.serialize(false));
		}
		return u;
	}

	public BinaryOperator(final OperatorProto proto, final IDescription context, final IExpression... args) {
		super(proto, args);
		prototype.verifyExpectedTypes(context, exprs[1].getGamlType());
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		final String name = getName();
		if (name.equals("internal_at")) {
			// '[' and ']' included
			sb.append(exprs[0].serialize(includingBuiltIn)).append(exprs[1].serialize(includingBuiltIn));
		} else if (OperatorProto.binaries.contains(name)) {
			parenthesize(sb, exprs[0]);
			sb.append(' ').append(name).append(' ');
			parenthesize(sb, exprs[1]);
		} else if (name.equals(IKeyword.AS)) {
			// Special case for the "as" operator
			sb.append(exprs[1].serialize(false)).append("(").append(exprs[0].serialize(includingBuiltIn)).append(")");
		} else {
			sb.append(name);
			parenthesize(sb, exprs[0], exprs[1]);
		}
		return sb.toString();
	}

	@Override
	public boolean shouldBeParenthesized() {
		final String s = getName();
		if (s.equals(".") || s.equals(":")) { return false; }
		return OperatorProto.binaries.contains(getName());
	}

	@Override
	public Object _value(final IScope scope) throws GamaRuntimeException {
		Object leftVal = null, rightVal = null;
		try {
			leftVal = prototype.lazy[0] ? exprs[0] : exprs[0].value(scope);
			rightVal = prototype.lazy[1] ? exprs[1] : exprs[1].value(scope);
			return ((GamaGetter.Binary) prototype.helper).get(scope, leftVal, rightVal);
		} catch (final GamaRuntimeException ge) {
			throw ge;
		} catch (final Throwable ex) {
			final GamaRuntimeException e1 = GamaRuntimeException.create(ex, scope);
			e1.addContext("when applying the " + literalValue() + " operator on " + Cast.toGaml(leftVal) + " and "
					+ Cast.toGaml(rightVal));
			throw e1;
		}
	}

	@Override
	public BinaryOperator copy() {
		return new BinaryOperator(prototype, null, exprs);
	}

	public static class BinaryVarOperator extends BinaryOperator implements IVarExpression.Agent {

		IDescription definitionDescription;

		public BinaryVarOperator(final OperatorProto proto, final IDescription context, final IExpression target,
				final IVarExpression var) {
			super(proto, context, target, var);
			definitionDescription = context;
		}

		@Override
		public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
			final IAgent agent = Cast.asAgent(scope, exprs[0].value(scope));
			if (agent == null || agent.dead()) { return; }
			scope.setAgentVarValue(agent, exprs[1].literalValue(), v);
		}

		@Override
		public IExpression getOwner() {
			return exprs[0];
		}

		@Override
		public VariableExpression getVar() {
			return (VariableExpression) exprs[1];
		}

		@Override
		public IDescription getDefinitionDescription() {
			return definitionDescription;
		}

		@Override
		public boolean isNotModifiable() {
			return ((IVarExpression) exprs[1]).isNotModifiable();
		}

		@Override
		public String serialize(final boolean includingBuiltIn) {
			final StringBuilder sb = new StringBuilder();
			parenthesize(sb, exprs[0]);
			sb.append('.');
			sb.append(exprs[1].serialize(includingBuiltIn));
			return sb.toString();
		}

		@Override
		public boolean isContextIndependant() {
			return false;
		}

		@Override
		public BinaryVarOperator copy() {
			return new BinaryVarOperator(prototype, null, exprs[0], (IVarExpression) exprs[1]);
		}
	}

}
