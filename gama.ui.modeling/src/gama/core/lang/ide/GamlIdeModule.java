/*******************************************************************************************************
 *
 * GamlIdeModule.java, in gama.ui.modeling, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.lang.ide;

import org.eclipse.xtext.ide.editor.contentassist.IPrefixMatcher;
import org.eclipse.xtext.ide.editor.contentassist.IProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.AntlrProposalConflictHelper;
import org.eclipse.xtext.ide.editor.contentassist.antlr.IContentAssistParser;

import com.google.inject.Binder;

import gama.core.lang.ide.contentassist.antlr.GamlParser;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GamlIdeModule extends AbstractGamlIdeModule {

	/**
	 * Configure.
	 *
	 * @param binder
	 *            the binder
	 * @see org.eclipse.xtext.service.AbstractGenericModule#configure(com.google.inject.Binder)
	 */
	@Override
	public void configure(final Binder binder) {
		super.configure(binder);
		configureContentAssistLexer(binder);
		binder.bind(IContentAssistParser.class).to(GamlParser.class);
		binder.bind(IProposalConflictHelper.class).to(AntlrProposalConflictHelper.class);
		binder.bind(IPrefixMatcher.class).to(IPrefixMatcher.IgnoreCase.class);
	}
}
