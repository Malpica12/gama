/*******************************************************************************************************
 *
 * msi.gaml.statements.HighlightStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.statements;

import gama.common.interfaces.IKeyword;
import gama.common.preferences.GamaPreferences;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.example;
import gama.core.dev.annotations.GamlAnnotations.facet;
import gama.core.dev.annotations.GamlAnnotations.facets;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.core.dev.annotations.GamlAnnotations.usage;
import gama.metamodel.agent.IAgent;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaColor;
import gaml.descriptions.IDescription;
import gaml.expressions.IExpression;
import gaml.operators.Cast;
import gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@symbol (
		name = IKeyword.HIGHLIGHT,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.DISPLAY, IConcept.COLOR })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets (
		value = { @facet (
				name = IKeyword.COLOR,
				type = IType.COLOR,
				doc = @doc ("An optional color to highlight the agent. Note that this color will become the default color for further higlight operations"),
				optional = true),
				@facet (
						name = IKeyword.VALUE,
						type = IType.AGENT,
						optional = false,
						doc = @doc ("The agent to hightlight")) },
		omissible = IKeyword.VALUE)
@doc (
		value = "Allows to highlight the agent passed in parameter in all available displays, optionaly setting a color. Passing 'nil' for the agent will remove the current highlight",
		usages = { @usage (
				value = "Highlighting an agent",
				examples = { @example ("highlight my_species(0) color: #blue;") }) })
public class HighlightStatement extends AbstractStatement {

	@Override
	public String getTrace(final IScope scope) {
		// We dont trace highlight statements
		return "";
	}

	final IExpression value;
	final IExpression color;

	public HighlightStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
		color = getFacet(IKeyword.COLOR);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = scope.getAgent();
		if (agent != null && !agent.dead()) {
			final IAgent o = Cast.asAgent(scope, value.value(scope));
			if (color != null) {
				final GamaColor c = Cast.asColor(scope, color.value(scope));
				if (c != null) {
					GamaPreferences.Displays.CORE_HIGHLIGHT.set(c);
				}
			}
			GAMA.getGui().setHighlightedAgent(o);
		}
		return value.value(scope);
	}
}
