/*********************************************************************************************
 *
 * 'ClassicalSIREquations.java, in plugin ummisco.gaml.extensions.maths, is part of the source code of the GAMA modeling
 * and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ext.maths.ode.utils.classicalEquations.epidemiology;

import java.util.ArrayList;
import java.util.List;

import gama.ext.maths.ode.statements.SingleEquationStatement;
import gaml.compilation.GAML;
import gaml.descriptions.IDescription;
import gaml.descriptions.StatementDescription;
import gaml.expressions.IExpression;
import gaml.expressions.data.ListExpression;

// SIR equation is defined by
// diff(S,t) = (- beta * S * I / N);
// diff(I,t) = (beta * S * I / N) - (gamma * I);
// diff(R,t) = (gamma * I);
//
// It is called using
// equation eqSIR type: SIR vars: [S,I,R,t] params: [N,beta,gamma]

public class ClassicalSIREquations {
	private final IDescription parentDesc;

	public ClassicalSIREquations(final IDescription p) {
		parentDesc = p;
	}

	public IDescription getDescription() {
		return parentDesc;
	}

	public List<SingleEquationStatement> SIR(final ListExpression with_vars, final ListExpression with_params) {
		if (with_vars == null || with_params == null) return null;
		final ArrayList<SingleEquationStatement> cmd = new ArrayList<>();
		final IExpression[] v = with_vars.getElements();
		final IExpression[] p = with_params.getElements();

		final StatementDescription stm = new StatementDescription("=", getDescription(), false, null, null, null);

		final SingleEquationStatement eq1 = new SingleEquationStatement(stm);
		eq1.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[0].literalValue() + "," + v[3].literalValue() + ")", getDescription()));
		eq1.setExpression(
				GAML.getExpressionFactory().createExpr("(- " + p[1].literalValue() + " * " + v[0].literalValue() + " * "
						+ v[1].literalValue() + " / " + p[0].literalValue() + ")", getDescription()));
		// eq1.establishVar();
		cmd.add(eq1);

		final SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[1].literalValue() + "," + v[3].literalValue() + ")", getDescription()));
		eq2.setExpression(GAML.getExpressionFactory().createExpr(
				"(" + p[1].literalValue() + " * " + v[0].literalValue() + " * " + v[1].literalValue() + " / "
						+ p[0].literalValue() + ") - (" + p[2].literalValue() + " * " + v[1].literalValue() + ")",
				getDescription()));
		// eq2.establishVar();
		cmd.add(eq2);

		final SingleEquationStatement eq3 = new SingleEquationStatement(stm);
		eq3.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[2].literalValue() + "," + v[3].literalValue() + ")", getDescription()));
		eq3.setExpression(GAML.getExpressionFactory()
				.createExpr("(" + p[2].literalValue() + " * " + v[1].literalValue() + ")", getDescription()));
		// eq3.establishVar();
		cmd.add(eq3);
		return cmd;
	}

}