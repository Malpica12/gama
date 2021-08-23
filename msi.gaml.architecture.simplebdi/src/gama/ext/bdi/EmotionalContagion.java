package gama.ext.bdi;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.example;
import gama.core.dev.annotations.GamlAnnotations.facet;
import gama.core.dev.annotations.GamlAnnotations.facets;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.metamodel.agent.IAgent;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.descriptions.IDescription;
import gaml.expressions.IExpression;
import gaml.operators.Cast;
import gaml.statements.AbstractStatement;
import gaml.types.IType;

@symbol(name = EmotionalContagion.EMOTIONALCONTAGION, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the identifier of the emotional contagion")),
		@facet(name = EmotionalContagion.EMOTIONDETECTED, type = EmotionType.id, optional = false, doc = @doc("the emotion that will start the contagion")),
		@facet(name = EmotionalContagion.EMOTIONCREATED, type = EmotionType.id, optional = true, doc = @doc("the emotion that will be created with the contagion")),
		@facet(name = EmotionalContagion.CHARISMA, type = IType.FLOAT, optional = true, doc = @doc("The charisma value of the perceived agent (between 0 and 1)")),
		@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("A boolean value to get the emotion only with a certain condition")),
		@facet(name = EmotionalContagion.THRESHOLD, type = IType.FLOAT, optional = true, doc = @doc("The threshold value to make the contagion")),
		@facet(name = EmotionalContagion.DECAY, type = IType.FLOAT, optional = true, doc = @doc("The decay value of the emotion added to the agent")),
		@facet(name = EmotionalContagion.INTENSITY, type = IType.FLOAT, optional = true, doc = @doc("The intensity value of the emotion created to the agent")),
		@facet(name = EmotionalContagion.RECEPTIVITY, type = IType.FLOAT, optional = true, doc = @doc("The receptivity value of the current agent (between 0 and 1)")) },
	omissible = IKeyword.NAME)
@doc(value = "enables to make conscious or unconscious emotional contagion", examples = {
		@example("emotional_contagion emotion_detected:fearConfirmed;"),
		@example("emotional_contagion emotion_detected:fear emotion_created:fearConfirmed;"),
		@example("emotional_contagion emotion_detected:fear emotion_created:fearConfirmed charisma: 0.5 receptivity: 0.5;")})


public class EmotionalContagion extends AbstractStatement {

	public static final String EMOTIONALCONTAGION = "emotional_contagion";
	public static final String EMOTIONDETECTED = "emotion_detected";
	public static final String EMOTIONCREATED = "emotion_created";
	public static final String CHARISMA = "charisma";
	public static final String RECEPTIVITY = "receptivity";
	public static final String THRESHOLD = "threshold";
	public static final String DECAY = "decay";
	public static final String INTENSITY = "intensity";

	final IExpression nameExpr;
	final IExpression emotionDetected;
	final IExpression emotionCreated;
	final IExpression charisma;
	final IExpression when;
	final IExpression receptivity;
	final IExpression threshold;
	final IExpression decay;
	final IExpression intensity;
	
	public EmotionalContagion(IDescription desc) {
		super(desc);
		nameExpr = getFacet(IKeyword.NAME);
		emotionDetected = getFacet(EmotionalContagion.EMOTIONDETECTED);
		emotionCreated = getFacet(EmotionalContagion.EMOTIONCREATED);
		charisma = getFacet(EmotionalContagion.CHARISMA);
		when = getFacet(IKeyword.WHEN);
		receptivity = getFacet(EmotionalContagion.RECEPTIVITY);
		threshold = getFacet(EmotionalContagion.THRESHOLD);
		decay = getFacet(EmotionalContagion.DECAY);
		intensity = getFacet(EmotionalContagion.INTENSITY);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		final IAgent[] stack = scope.getAgentsStack();
		final IAgent mySelfAgent = stack[stack.length - 2];
		Double charismaValue = 1.0;
		Double receptivityValue = 1.0;
		Double thresholdValue = 0.25;
		IScope scopeMySelf = null;
		Double decayValue = 0.0;
		Double intensityValue = 0.0;
		if (mySelfAgent != null) {
			scopeMySelf = mySelfAgent.getScope().copy("of EmotionalContagion");
			scopeMySelf.push(mySelfAgent);
		}
		if (when == null || Cast.asBool(scopeMySelf, when.value(scopeMySelf))) {
			if (emotionDetected != null) {
				if (SimpleBdiArchitecture.hasEmotion(scope, (Emotion) emotionDetected.value(scope))) {
					if (charisma != null) {
						charismaValue = (Double) charisma.value(scope);
					}else{charismaValue = (Double) scope.getAgent().getAttribute(CHARISMA);}
					if (receptivity != null) {
						receptivityValue = (Double) receptivity.value(scopeMySelf);
					}else{receptivityValue = (Double) mySelfAgent.getAttribute(RECEPTIVITY);}
					if (threshold != null) {
						thresholdValue = (Double) threshold.value(scopeMySelf);
					}
					if(emotionCreated != null){
						if (charismaValue * receptivityValue >= thresholdValue) {
							final Emotion tempEmo = (Emotion) emotionCreated.value(scope);
							tempEmo.setAgentCause(scope.getAgent());
							if(decay!=null){
								decayValue = (Double) decay.value(scopeMySelf);
								if(decayValue>1.0){
									decayValue = 1.0;
								}
								if(decayValue<0.0){
									decayValue = 0.0;
								}
							} else {
								decayValue = SimpleBdiArchitecture.getEmotion(scope, (Emotion) emotionDetected.value(scope)).getDecay();
							}
							tempEmo.setDecay(decayValue);
							if(intensity!=null){
								intensityValue = (Double) intensity.value(scopeMySelf);
								if(intensityValue>1.0){
									intensityValue = 1.0;
								}
								if(intensityValue<0.0){
									intensityValue = 0.0;
								}
							}
							tempEmo.setIntensity(intensityValue);
							SimpleBdiArchitecture.addEmotion(scopeMySelf, tempEmo);
						}
					}else{
						if (charismaValue * receptivityValue >= thresholdValue) {
							final Emotion tempEmo = SimpleBdiArchitecture.getEmotion(scope, (Emotion) emotionDetected.value(scope));
							Emotion temp;
							if (!tempEmo.getNoIntensity()) {
								temp = new Emotion(tempEmo.getName(),
										tempEmo.getIntensity() * charismaValue * receptivityValue, tempEmo.getAbout(),
										tempEmo.getDecay());
							} else {
								temp = (Emotion) tempEmo.copy(scope);
							}
							temp.setAgentCause(scope.getAgent());
							if(decay!=null){
								decayValue = (Double) decay.value(scopeMySelf);
								if(decayValue>1.0){
									decayValue = 1.0;
								}
								if(decayValue<0.0){
									decayValue = 0.0;
								}
								temp.setDecay(decayValue);
							}
							SimpleBdiArchitecture.addEmotion(scopeMySelf, temp);
						}
					}
				}
			}
		}
		GAMA.releaseScope(scopeMySelf);
		return null;
	}

}
