/*******************************************************************************************************
 *
 * msi.gaml.statements.TraceStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.statements;

import gama.common.interfaces.IKeyword;
import gama.core.dev.*;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.runtime.IScope;
import gaml.descriptions.IDescription;

/**
 * Class TraceStatement.
 * 
 * @author drogoul
 * @since 23 févr. 2014
 * 
 */
@symbol(name = IKeyword.TRACE, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true,
concept = { IConcept.DISPLAY })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@doc(value="All the statements executed in the trace statement are displayed in the console.")
public class TraceStatement extends AbstractStatementSequence {

	/**
	 * @param desc
	 */
	public TraceStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void enterScope(final IScope scope) {
		super.enterScope(scope);
		scope.setTrace(true);
	}

	@Override
	public void leaveScope(final IScope scope) {
		scope.setTrace(false);
		super.leaveScope(scope);
	}
}