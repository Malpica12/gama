/*******************************************************************************************************
 *
 * EGaml.java, in gama.core.lang, is part of the source code of the GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.lang;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;

import gama.common.interfaces.IKeyword;
import gama.core.lang.gaml.ActionRef;
import gama.core.lang.gaml.ArgumentDefinition;
import gama.core.lang.gaml.ArgumentPair;
import gama.core.lang.gaml.Array;
import gama.core.lang.gaml.BinaryOperator;
import gama.core.lang.gaml.Block;
import gama.core.lang.gaml.EquationRef;
import gama.core.lang.gaml.Expression;
import gama.core.lang.gaml.ExpressionList;
import gama.core.lang.gaml.Facet;
import gama.core.lang.gaml.Function;
import gama.core.lang.gaml.GamlDefinition;
import gama.core.lang.gaml.GamlFactory;
import gama.core.lang.gaml.GamlPackage;
import gama.core.lang.gaml.HeadlessExperiment;
import gama.core.lang.gaml.If;
import gama.core.lang.gaml.Model;
import gama.core.lang.gaml.Parameter;
import gama.core.lang.gaml.Point;
import gama.core.lang.gaml.S_Action;
import gama.core.lang.gaml.S_Assignment;
import gama.core.lang.gaml.S_Definition;
import gama.core.lang.gaml.S_DirectAssignment;
import gama.core.lang.gaml.S_Display;
import gama.core.lang.gaml.S_Equations;
import gama.core.lang.gaml.S_If;
import gama.core.lang.gaml.S_Reflex;
import gama.core.lang.gaml.SkillRef;
import gama.core.lang.gaml.Statement;
import gama.core.lang.gaml.StringLiteral;
import gama.core.lang.gaml.TerminalExpression;
import gama.core.lang.gaml.TypeRef;
import gama.core.lang.gaml.Unary;
import gama.core.lang.gaml.UnitName;
import gama.core.lang.gaml.VariableRef;
import gama.core.lang.gaml.impl.ActionArgumentsImpl;
import gama.core.lang.gaml.impl.BlockImpl;
import gama.core.lang.gaml.impl.ExpressionListImpl;
import gama.core.lang.gaml.impl.HeadlessExperimentImpl;
import gama.core.lang.gaml.impl.ModelImpl;
import gama.core.lang.gaml.impl.S_ActionImpl;
import gama.core.lang.gaml.impl.S_EquationsImpl;
import gama.core.lang.gaml.impl.S_IfImpl;
import gama.core.lang.gaml.impl.StatementImpl;
import gama.core.lang.gaml.util.GamlSwitch;
import gama.util.GamaMapFactory;
import gaml.compilation.GAML;
import gaml.compilation.IGamlEcoreUtils;
import gaml.compilation.ast.SyntacticFactory;

/**
 * The class EGaml.getInstance(). A stateless class, bunch of utilities to work with the various GAML statements and
 * expressions.
 *
 * @author drogoul
 * @since 2012
 *
 */
public class EGaml implements IGamlEcoreUtils {

	/** The Constant instance. */
	private static final EGaml instance = new EGaml();

	/**
	 * Gets the single instance of EGaml.
	 *
	 * @return single instance of EGaml
	 */
	public static EGaml getInstance() {
		return instance;
	}

	@Override
	public String getNameOf(final EObject o) {
		if (o instanceof S_Reflex) {
			String s = ((S_Reflex) o).getName();
			if (s == null) return IKeyword.INTERNAL + getKeyOf(o) + GAML.COMMAND_INDEX++;
		}
		if (o instanceof GamlDefinition) return ((GamlDefinition) o).getName();
		if (o instanceof S_Display) return ((S_Display) o).getName();
		if (o instanceof HeadlessExperiment) return ((HeadlessExperiment) o).getName();

		return null;
	}

	/**
	 * Gets the exprs out of an expression list
	 *
	 * @param o
	 *            the o
	 * @return the exprs of
	 */
	@Override
	public List<Expression> getExprsOf(final EObject o) {
		if (o instanceof ExpressionList && ((ExpressionListImpl) o).eIsSet(GamlPackage.EXPRESSION_LIST__EXPRS))
			return ((ExpressionList) o).getExprs();
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the args out of the arguments of an action
	 *
	 * @param args
	 *            the args
	 * @return the args of
	 */
	@Override
	public List<ArgumentDefinition> getArgsOf(final EObject args) {
		if (args == null) return Collections.EMPTY_LIST;
		if (args instanceof ActionArgumentsImpl
				&& ((ActionArgumentsImpl) args).eIsSet(GamlPackage.ACTION_ARGUMENTS__ARGS))
			return ((ActionArgumentsImpl) args).getArgs();
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the facets of a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets of
	 */
	@Override
	public List<Facet> getFacetsOf(final EObject s) {
		if (s instanceof StatementImpl) {
			if (((StatementImpl) s).eIsSet(GamlPackage.STATEMENT__FACETS)) return ((StatementImpl) s).getFacets();
		} else if (s instanceof HeadlessExperimentImpl
				&& ((HeadlessExperimentImpl) s).eIsSet(GamlPackage.HEADLESS_EXPERIMENT__FACETS))
			return ((HeadlessExperimentImpl) s).getFacets();
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the facets map of a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets map of
	 */
	@Override
	public Map<String, Facet> getFacetsMapOf(final EObject s) {
		final List<? extends EObject> list = getFacetsOf(s);
		if (list.isEmpty()) return Collections.EMPTY_MAP;
		final Map<String, Facet> map = GamaMapFactory.create();
		for (final EObject f : list) {
			if (f instanceof Facet) { map.put(getKeyOf(f), (Facet) f); }
		}
		return map;
	}

	/**
	 * Tells if a facet is present in a statement
	 *
	 * @param s
	 *            the s
	 * @return the facets map of
	 */
	@Override
	public boolean hasFacet(final EObject s, final String facet) {
		final List<? extends EObject> list = getFacetsOf(s);
		if (list.isEmpty()) return false;
		for (final EObject f : list) {
			if (f instanceof Facet) {
				final String name = getKeyOf(f);
				if (facet.equals(name)) return true;
			}
		}
		return false;
	}

	/**
	 * Get one particular facet of a statement
	 *
	 * @param s
	 * @return
	 */
	@Override
	public Expression getExpressionAtKey(final EObject s, final String name) {
		if (s == null || name == null) return null;
		if ("value".equals(name) && s instanceof S_DirectAssignment) return ((S_DirectAssignment) s).getValue();
		final List<Facet> list = getFacetsOf(s);

		for (final Facet f : list) {
			final String key = getKeyOf(f);
			if (s instanceof Statement && ("value".equals(name) || "init".equals(name))) {
				if ("<-".equals(key)) return f.getExpr();
			} else if (s instanceof S_Assignment && "item".equals(name) && (key.contains("<") || key.contains(">")))
				return f.getExpr();
			if (name.equals(key)) return f.getExpr();
		}
		return null;
	}

	@Override
	public Expression getExprOf(final EObject s) {
		if (s instanceof Expression) return (Expression) s;
		if (s instanceof Statement) return ((Statement) s).getExpr();
		return null;
	}

	/** The children switch. */
	private final GamlSwitch<Boolean> childrenSwitch = new GamlSwitch<>() {

		@Override
		public Boolean caseModel(final Model object) {
			return ((ModelImpl) object).eIsSet(GamlPackage.MODEL__BLOCK);
		}

		@Override
		public Boolean caseS_Action(final S_Action object) {
			if (((S_ActionImpl) object).eIsSet(GamlPackage.SACTION__ARGS)) return true;
			return caseStatement(object);
		}

		@Override
		public Boolean caseBlock(final Block object) {
			return ((BlockImpl) object).eIsSet(GamlPackage.BLOCK__STATEMENTS);
		}

		@Override
		public Boolean caseStatement(final Statement object) {
			return ((StatementImpl) object).eIsSet(GamlPackage.STATEMENT__BLOCK) || hasFacet(object, IKeyword.VIRTUAL)
			// && ((StatementImpl) object).getBlock().getFunction() == null
			;
		}

		@Override
		public Boolean caseHeadlessExperiment(final HeadlessExperiment object) {
			return ((HeadlessExperimentImpl) object).eIsSet(GamlPackage.HEADLESS_EXPERIMENT__BLOCK)
			// && object.getBlock().getFunction() == null
			;
		}

		@Override
		public Boolean caseS_Equations(final S_Equations object) {
			return ((S_EquationsImpl) object).eIsSet(GamlPackage.SEQUATIONS__EQUATIONS);
		}

		@Override
		public Boolean caseS_If(final S_If object) {
			return caseStatement(object) || ((S_IfImpl) object).eIsSet(GamlPackage.SIF__ELSE);
		}

		@Override
		public Boolean defaultCase(final EObject object) {
			return false;
		}

	};

	/**
	 * Checks for children.
	 *
	 * @param obj
	 *            the obj
	 * @return true, if successful
	 */
	@Override
	public boolean hasChildren(final EObject obj) {
		return childrenSwitch.doSwitch(obj);
	}

	/**
	 * Gets the statements of a block
	 *
	 * @param block
	 *            the block
	 * @return the statements of
	 */
	@Override
	public List<Statement> getStatementsOf(final EObject block) {

		if (block instanceof BlockImpl) {
			if (((BlockImpl) block).eIsSet(GamlPackage.BLOCK__STATEMENTS)) return ((BlockImpl) block).getStatements();
		} else if (block instanceof Model) return getStatementsOf(((Model) block).getBlock());
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the equations of a systems of equations
	 *
	 * @param stm
	 *            the stm
	 * @return the equations of
	 */
	@Override
	public List<S_Assignment> getEquationsOf(final EObject stm) {
		if (stm instanceof S_EquationsImpl && ((S_EquationsImpl) stm).eIsSet(GamlPackage.SEQUATIONS__EQUATIONS))
			return ((S_EquationsImpl) stm).getEquations();
		return Collections.EMPTY_LIST;
	}

	/**
	 * Gets the key of an eObject
	 *
	 * @param f
	 *            the f
	 * @return the key of
	 */
	@Override
	public String getKeyOf(final EObject f) {
		if (f == null) return null;
		return getKeyOf(f, f.eClass());
	}

	/**
	 * Gets the key of an eObject in a given eClass
	 *
	 * @param object
	 *            the object
	 * @param clazz
	 *            the clazz
	 * @return the key of
	 */
	@Override
	public String getKeyOf(final EObject object, final EClass clazz) {
		String s;
		final int id = clazz.getClassifierID();
		switch (id) {
			case GamlPackage.UNARY:
				return ((Unary) object).getOp();
			case GamlPackage.BINARY_OPERATOR:
				return ((BinaryOperator) object).getOp();
			case GamlPackage.ARGUMENT_PAIR:
				s = ((ArgumentPair) object).getOp();
				return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
			case GamlPackage.PARAMETER:
				final Parameter p = (Parameter) object;
				s = getKeyOf(p.getLeft());
				if (s == null) { s = p.getBuiltInFacetKey(); }
				return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
			case GamlPackage.MODEL:
				return IKeyword.MODEL;
			case GamlPackage.STATEMENT:
				s = ((Statement) object).getKey();
				if (s == null && object instanceof S_Definition) {
					final TypeRef type = (TypeRef) ((S_Definition) object).getTkey();
					if (type != null) return getKeyOf(type);
				}
				return s;
			case GamlPackage.FACET:
				s = ((Facet) object).getKey();
				return s.endsWith(":") ? s.substring(0, s.length() - 1) : s;
			case GamlPackage.FUNCTION:
				final Function ff = (Function) object;
				return getKeyOf(ff.getLeft());
			case GamlPackage.TYPE_REF:
				s = getNameOfRef(object);
				if (s.contains("<")) {
					s = s.split("<")[0];
					// Special case for the 'species<xxx>' case
					if ("species".equals(s)) { s = SyntacticFactory.SPECIES_VAR; }
				}
				return s;
			case GamlPackage.IF:
				return "?";
			case GamlPackage.VARIABLE_REF:
			case GamlPackage.UNIT_NAME:
			case GamlPackage.ACTION_REF:
			case GamlPackage.SKILL_REF:
			case GamlPackage.EQUATION_REF:
				return getNameOfRef(object);
			case GamlPackage.INT_LITERAL:
			case GamlPackage.STRING_LITERAL:
			case GamlPackage.DOUBLE_LITERAL:
				// case GamlPackage.COLOR_LITERAL:
			case GamlPackage.RESERVED_LITERAL:
			case GamlPackage.BOOLEAN_LITERAL:
			case GamlPackage.TERMINAL_EXPRESSION:
				return ((TerminalExpression) object).getOp();
			default:
				final List<EClass> eSuperTypes = clazz.getESuperTypes();
				return eSuperTypes.isEmpty() ? null : getKeyOf(object, eSuperTypes.get(0));
		}
	}

	/**
	 * Gets the name of the ref represented by this eObject
	 *
	 * @param o
	 *            the o
	 * @return the name of ref
	 */
	@Override
	public String getNameOfRef(final EObject o) {
		final ICompositeNode n = NodeModelUtils.getNode(o);
		if (n != null) return NodeModelUtils.getTokenText(n);
		if (o instanceof VariableRef) return ((VariableRef) o).getRef().getName();
		if (o instanceof UnitName) return ((UnitName) o).getRef().getName();
		if (o instanceof ActionRef)
			return ((ActionRef) o).getRef().getName();
		else if (o instanceof SkillRef)
			return ((SkillRef) o).getRef().getName();
		else if (o instanceof EquationRef)
			return ((EquationRef) o).getRef().getName();
		else if (o instanceof TypeRef)
			return ((TypeRef) o).getRef().getName();
		else
			return "";
	}

	/**
	 * Gets the factory for building eObjects.
	 *
	 * @return the factory
	 */
	public GamlFactory getFactory() {
		return (GamlFactory) GamlPackage.eINSTANCE.getEFactoryInstance();
	}

	/**
	 * Save an eObject into a string
	 *
	 * @param expr
	 *            the expr
	 * @return the string
	 */

	@Override
	public String toString(final EObject expr) {
		if (expr == null) return null;
		if (expr instanceof Statement) return getNameOf(expr);
		if (expr instanceof Facet) return ((Facet) expr).getName();

		if (!(expr instanceof Expression)) return expr.toString();
		final StringBuilder serializer = new StringBuilder(100);
		serializer.setLength(0);
		serialize(serializer, (Expression) expr);
		return serializer.toString();
	}

	/**
	 * Serialize an expression.
	 *
	 * @param serializer
	 *            a string builder to which the expression should be appended
	 * @param expr
	 *            the expr
	 */
	private void serialize(final StringBuilder serializer, final Expression expr) {
		if (expr == null) {} else if (expr instanceof If) {
			serializer.append("(");
			serialize(serializer, ((If) expr).getLeft());
			serializer.append(")").append(((If) expr).getOp()).append("(");
			serialize(serializer, ((If) expr).getRight());
			serializer.append(")").append(":");
			serialize(serializer, ((If) expr).getIfFalse());
		} else if (expr instanceof StringLiteral) {
			serializer.append(((StringLiteral) expr).getOp());
		} else if (expr instanceof TerminalExpression) {
			serializer.append(((TerminalExpression) expr).getOp());
		} else if (expr instanceof Point) {
			serializer.append("{").append("(");
			serialize(serializer, ((Point) expr).getLeft());
			serializer.append(")").append(((Point) expr).getOp()).append("(");
			serialize(serializer, ((Point) expr).getRight());
			serializer.append(")");
			if (((Point) expr).getZ() != null) {
				serializer.append(',').append("(");
				serialize(serializer, ((Point) expr).getZ());
				serializer.append(")");
			}
			serializer.append("}");
		} else if (expr instanceof Array) {
			array(serializer, ((Array) expr).getExprs().getExprs(), false);
		} else if (expr instanceof VariableRef || expr instanceof TypeRef || expr instanceof SkillRef
				|| expr instanceof ActionRef || expr instanceof UnitName) {
			serializer.append(getKeyOf(expr));
		} else if (expr instanceof Unary) {
			serializer.append(((Unary) expr).getOp()).append("(");
			serialize(serializer, ((Unary) expr).getRight());
			serializer.append(")");
		} else if (expr instanceof Function) {
			function(serializer, (Function) expr);
		}
		// else if ( expr instanceof FunctionRef ) {
		// function((FunctionRef) expr);
		// }
		else {
			// serializer.append("(");
			// serialize(serializer, expr.getLeft());
			// serializer.append(")").append(expr.getOp()).append("(");
			// serialize(serializer, expr.getRight());
			// serializer.append(")");
		}
	}

	/**
	 * Serializes a function.
	 *
	 * @param serializer
	 *            a string builder to which the function should be appended
	 * @param expr
	 *            the expr
	 */
	private void function(final StringBuilder serializer, final Function expr) {
		final List<? extends EObject> args = getExprsOf(expr.getRight());
		final String opName = getKeyOf(expr.getLeft());
		switch (args.size()) {
			case 1:
				serializer.append(opName).append("(");
				serialize(serializer, (Expression) args.get(0));
				serializer.append(")");
				break;
			case 2:
				serializer.append("(");
				serialize(serializer, (Expression) args.get(0));
				serializer.append(")").append(opName).append("(");
				serialize(serializer, (Expression) args.get(1));
				serializer.append(")");
				break;
			default:
				serializer.append(opName);
				serializer.append("(");
				array(serializer, args, true);
				serializer.append(")");
		}
	}

	/**
	 * Serializes a list of arguments.
	 *
	 * @param serializer
	 *            a string builder to which the args should be appended
	 * @param args
	 *            the args
	 * @param arguments
	 *            the arguments
	 */
	private void array(final StringBuilder serializer, final List<? extends EObject> args, final boolean arguments) {
		// if arguments is true, parses the list to transform it into a map of
		// args
		// (starting at 1); Experimental right now
		// serializer.append("[");
		final int size = args.size();
		for (int i = 0; i < size; i++) {
			final Expression e = (Expression) args.get(i);
			if (arguments) { serializer.append("arg").append(i).append("::"); }
			serialize(serializer, e);
			if (i < size - 1) { serializer.append(","); }
		}
		// serializer.append("]");
	}

	/**
	 * Gets the statement equal to or including this eObject
	 *
	 * @param o
	 *            the o
	 * @return the statement
	 */
	@Override
	public Statement getStatement(final EObject o) {
		if (o instanceof Statement) return (Statement) o;
		if (o instanceof TypeRef && o.eContainer() instanceof S_Definition
				&& ((S_Definition) o.eContainer()).getTkey() == o)
			return (Statement) o.eContainer();
		return null;

	}

	/**
	 * Checks if this statement includes a batch definition
	 *
	 * @param e
	 *            the e
	 * @return true, if is batch
	 */
	@Override
	public boolean isBatch(final EObject e) {
		if (e instanceof StatementImpl) {
			if (!((StatementImpl) e).eIsSet(GamlPackage.STATEMENT__FACETS)) return false;
			for (final Facet f : ((Statement) e).getFacets()) {
				if (IKeyword.TYPE.equals(getKeyOf(f))) {
					final String type = EGaml.getInstance().getKeyOf(f.getExpr());
					if (IKeyword.BATCH.equals(type) || IKeyword.TEST.equals(type)) return true;
				}
			}
		} else if (e instanceof HeadlessExperimentImpl) {
			if (!((HeadlessExperimentImpl) e).eIsSet(GamlPackage.HEADLESS_EXPERIMENT__FACETS)) return false;
			for (final Facet f : ((HeadlessExperimentImpl) e).getFacets()) {
				if (IKeyword.TYPE.equals(getKeyOf(f))) {
					final String type = EGaml.getInstance().getKeyOf(f.getExpr());
					if (IKeyword.BATCH.equals(type)) return true;
				}
			}
		}
		return false;

	}

}
