/*********************************************************************************************
 *
 *
 * 'SimpleBdiArchitecture.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the GAMA
 * modeling and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ext.bdi;

import java.util.List;

import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.skill;
import gama.metamodel.agent.IAgent;
import gama.metamodel.population.IPopulation;
import gama.runtime.IScope;
import gama.runtime.concurrent.GamaExecutorService;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.descriptions.ConstantExpressionDescription;
import gaml.descriptions.IDescription;
import gaml.expressions.IExpression;
import gaml.operators.Cast;
import gaml.operators.Maths;
import gaml.statements.AbstractStatement;
import gaml.statements.IStatement;

@skill (
		name = SimpleBdiArchitectureParallel.PARALLEL_BDI,
		concept = { IConcept.BDI, IConcept.ARCHITECTURE })
@doc ("compute the bdi architecture in parallel")
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class SimpleBdiArchitectureParallel extends SimpleBdiArchitecture {

	public static final String PARALLEL_BDI = "parallel_bdi";
	IExpression parallel = ConstantExpressionDescription.TRUE_EXPR_DESCRIPTION;

	public class UpdateEmotions extends AbstractStatement {

		public UpdateEmotions(IDescription desc) {
			super(desc);
		}

		@Override
		protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
			// computeEmotions(scope);
			return null;
		}

	}

	public class UpdateSocialLinks extends AbstractStatement {

		public UpdateSocialLinks(IDescription desc) {
			super(desc);
		}

		@Override
		protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
			updateSocialLinks(scope);
			return null;
		}

	}

	public class UpdateEmotionsIntensity extends AbstractStatement {

		public UpdateEmotionsIntensity(IDescription desc) {
			super(desc);
		}

		@Override
		protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
			updateEmotionsIntensity(scope);
			return null;
		}

	}

	public class UpdateLifeTimePredicates extends AbstractStatement {

		public UpdateLifeTimePredicates(IDescription desc) {
			super(desc);
		}

		@Override
		protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
			updateLifeTimePredicates(scope);
			return null;
		}

	}

	@Override
	public void preStep(final IScope scope, IPopulation<? extends IAgent> gamaPopulation) {
		final IExpression schedule = gamaPopulation.getSpecies().getSchedule();
		final List<? extends IAgent> agents =
				schedule == null ? gamaPopulation : Cast.asList(scope, schedule.value(scope));

		GamaExecutorService.execute(scope, new UpdateLifeTimePredicates(null), agents, parallel);
		GamaExecutorService.execute(scope, new UpdateEmotionsIntensity(null), agents, parallel);

		if (_reflexes != null)
			for (final IStatement r : _reflexes) {
				if (!scope.interrupted()) {
					GamaExecutorService.execute(scope, r, agents, ConstantExpressionDescription.FALSE_EXPR_DESCRIPTION);
				}
			}

		if (_perceptionNumber > 0) {
			for (int i = 0; i < _perceptionNumber; i++) {
				if (!scope.interrupted()) {
					PerceiveStatement statement = _perceptions.get(i);
					IExpression par = statement.getParallel() == null ? parallel : statement.getParallel();
					GamaExecutorService.execute(scope, statement, agents, par);
				}
			}
		}
		if (_rulesNumber > 0) {
			for (int i = 0; i < _rulesNumber; i++) {
				RuleStatement statement = _rules.get(i);
				IExpression par = statement.getParallel() == null ? parallel : statement.getParallel();
				GamaExecutorService.execute(scope, statement, agents, par);
			}
		}

		if (_lawsNumber > 0) {
			for (int i = 0; i < _lawsNumber; i++) {
				LawStatement statement = _laws.get(i);
				IExpression par = statement.getParallel() == null ? parallel : statement.getParallel();
				GamaExecutorService.execute(scope, statement, agents, par);
			}
		}

		// GamaExecutorService.execute(scope, new UpdateEmotions(null), agents,parallel) ;
		GamaExecutorService.execute(scope, new UpdateSocialLinks(null), agents, parallel);
		if (_copingNumber > 0) {
			for (int i = 0; i < _copingNumber; i++) {
				CopingStatement statement = _coping.get(i);
				IExpression par = statement.getParallel() == null ? parallel : statement.getParallel();
				GamaExecutorService.execute(scope, statement, agents, par);
			}
		}
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		final Boolean use_personality = scope.hasArg(USE_PERSONALITY) ? scope.getBoolArg(USE_PERSONALITY)
				: (Boolean) scope.getAgent().getAttribute(USE_PERSONALITY);
		if (use_personality) {
			Double expressivity = (Double) scope.getAgent().getAttribute(EXTRAVERSION);
			Double neurotisme = (Double) scope.getAgent().getAttribute(NEUROTISM);
			Double conscience = (Double) scope.getAgent().getAttribute(CONSCIENTIOUSNESS);
			Double agreeableness = (Double) scope.getAgent().getAttribute(AGREEABLENESS);
			scope.getAgent().setAttribute(CHARISMA, expressivity);
			scope.getAgent().setAttribute(RECEPTIVITY, 1 - neurotisme);
			scope.getAgent().setAttribute(PERSISTENCE_COEFFICIENT_PLANS, Maths.sqrt(scope, conscience));
			scope.getAgent().setAttribute(PERSISTENCE_COEFFICIENT_INTENTIONS, Maths.sqrt(scope, conscience));
			scope.getAgent().setAttribute(OBEDIENCE, Maths.sqrt(scope, (conscience + agreeableness) * 0.5));
		}
		// return executePlans(scope);
		Object result = executePlans(scope);
		if (!scope.getAgent().dead()) {
			// Activer la violation des normes
			updateNormViolation(scope);
			// Mettre à jour le temps de vie des normes
			updateNormLifetime(scope);

			// Part that manage the lifetime of predicates
			// if(result!=null){
			// updateLifeTimePredicates(scope);
			// updateEmotionsIntensity(scope);
			// }
		}
		return result;
	}

}
