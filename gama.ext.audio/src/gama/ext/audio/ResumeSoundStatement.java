/*******************************************************************************************************
 *
 * ResumeSoundStatement.java, in gama.ext.audio, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ext.audio;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.ext.audio.ResumeSoundStatement.ResumeSoundValidator;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.compilation.IDescriptionValidator;
import gaml.compilation.ISymbol;
import gaml.compilation.annotations.validator;
import gaml.descriptions.IDescription;
import gaml.statements.AbstractStatementSequence;

/**
 * The Class ResumeSoundStatement.
 */
@symbol (
		name = IKeyword.RESUME_SOUND,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		concept = { IConcept.SOUND })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER, ISymbolKind.OUTPUT })
@validator (ResumeSoundValidator.class)
@doc ("Allows to resume the sound output")
public class ResumeSoundStatement extends AbstractStatementSequence {

	/**
	 * The Class ResumeSoundValidator.
	 */
	public static class ResumeSoundValidator implements IDescriptionValidator<IDescription> {

		/**
		 * Method validate()
		 *
		 * @see gaml.compilation.IDescriptionValidator#validate(gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription cd) {

			// what to validate?
		}
	}

	/** The sequence. */
	private AbstractStatementSequence sequence = null;

	/**
	 * Instantiates a new resume sound statement.
	 *
	 * @param desc the desc
	 */
	public ResumeSoundStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence = new AbstractStatementSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final IAgent currentAgent = scope.getAgent();

		final GamaSoundPlayer soundPlayer = SoundPlayerBroker.getInstance().getSoundPlayer(currentAgent);
		soundPlayer.resume(scope);

		if (sequence != null) { scope.execute(sequence, currentAgent, null); }

		return null;
	}
}