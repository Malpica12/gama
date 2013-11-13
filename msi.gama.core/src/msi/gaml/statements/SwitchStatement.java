/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.statements;

import static msi.gama.common.interfaces.IKeyword.MATCH;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.SwitchStatement.SwitchValidator;
import msi.gaml.types.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * IfPrototype.
 * 
 * @author drogoul 14 nov. 07
 */
@symbol(name = IKeyword.SWITCH, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets(value = { @facet(name = IKeyword.VALUE, type = IType.NONE, optional = false) }, omissible = IKeyword.VALUE)
@validator(SwitchValidator.class)
public class SwitchStatement extends AbstractStatementSequence {

	

	public static Predicate allMatches = new Predicate<IDescription>() {

		@Override
		public boolean apply(final IDescription input) {
			return input.getKeyword().equals(MATCH);
		}
	};

	public static class SwitchValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {

			// FIXME This assertion only verifies the case of "match" (not match_one or match_between)
			List<IDescription> children = desc.getChildren();
			Iterable<IDescription> matches = Iterables.filter(children, allMatches);
			IExpression switchValue = desc.getFacets().getExpr(VALUE);
			if ( switchValue == null ) { return; }
			IType switchType = switchValue.getType();
			if ( switchType.equals(Types.NO_TYPE) ) { return; }
			for ( IDescription match : matches ) {
				IExpression value = match.getFacets().getExpr(VALUE);
				if ( value == null ) {
					continue;
				}
				IType matchType = value.getType();
				// AD : special case introduced for ints and floats (a warning is emitted)
				if ( Types.intFloatCase(matchType, switchType) ) {
					match.warning("The value " + value.toGaml() + " of type " + matchType +
						" is compared to a value of type " + switchType + ", which will never match ",
						IGamlIssue.SHOULD_CAST, IKeyword.VALUE, switchType.toString());
					continue;
				}

				if ( matchType.isTranslatableInto(switchType) ) {
					continue;
				}
				match.warning("The value " + value.toGaml() + " of type " + matchType +
					" is compared to a value of type " + switchType + ", which will never match ",
					IGamlIssue.SHOULD_CAST, IKeyword.VALUE, switchType.toString());
			}

		}

	}

	public MatchStatement[] matches;
	public MatchStatement defaultMatch;
	final IExpression value;

	/**
	 * The Constructor.
	 * 
	 * @param sim the sim
	 */
	public SwitchStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
		setName("switch" + value.toGaml());

	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		final List<MatchStatement> cases = new ArrayList();
		for ( final ISymbol c : commands ) {
			if ( c instanceof MatchStatement ) {
				if ( ((MatchStatement) c).getLiteral(IKeyword.KEYWORD).equals(IKeyword.DEFAULT) ) {
					defaultMatch = (MatchStatement) c;
				} else {
					cases.add((MatchStatement) c);
				}
			}
		}
		commands.removeAll(cases);
		commands.remove(defaultMatch);
		matches = cases.toArray(new MatchStatement[0]);
		super.setChildren(commands);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		boolean hasMatched = false;
		final Object switchValue = value.value(scope);
		Object lastResult = null;
		for ( int i = 0; i < matches.length; i++ ) {
			if ( scope.interrupted() ) { return lastResult; }
			if ( matches[i].matches(scope, switchValue) ) {
				lastResult = matches[i].executeOn(scope);
				hasMatched = true;
			}
		}
		if ( !hasMatched && defaultMatch != null ) { return defaultMatch.executeOn(scope); }
		return lastResult;
	}

	@Override
	public void leaveScope(final IScope scope) {
		// Clears any _loop_halted status
		scope.popLoop();
		super.leaveScope(scope);
	}
}