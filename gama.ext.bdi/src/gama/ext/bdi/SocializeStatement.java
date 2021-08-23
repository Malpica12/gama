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


@symbol(name = SocializeStatement.SOCIALIZE, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the identifier of the socialize statement")),
		@facet(name = SocializeStatement.LIKING, type = IType.FLOAT, optional = true, doc = @doc("the appreciation value of the created social link")),
		@facet(name = SocializeStatement.DOMINANCE, type = IType.FLOAT, optional = true, doc = @doc("the dominance value of the created social link")),
		@facet(name = SocializeStatement.SOLIDARITY, type = IType.FLOAT, optional = true, doc = @doc("the solidarity value of the created social link")),
		@facet(name = SocializeStatement.FAMILIARITY, type = IType.FLOAT, optional = true, doc = @doc("the familiarity value of the created social link")),
		@facet(name = SocializeStatement.TRUST, type = IType.FLOAT, optional = true, doc = @doc("the trust value of the created social link")),
		@facet(name = SocializeStatement.AGENT, type = IType.AGENT, optional = true, doc = @doc("the agent value of the created social link")),
		@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("A boolean value to socialize only with a certain condition"))
}, omissible = IKeyword.NAME)
@doc(value = "enables to directly add a social link from a perceived agent.", examples = {
		@example("socialize;") })

public class SocializeStatement extends AbstractStatement{
	public static final String SOCIALIZE = "socialize";
	public static final String LIKING = "liking";
	public static final String DOMINANCE = "dominance";
	public static final String SOLIDARITY = "solidarity";
	public static final String FAMILIARITY = "familiarity";
	public static final String TRUST = "trust";
	public static final String AGENT = "agent";
	
	final IExpression name;
	final IExpression appreciation;
	final IExpression dominance;
	final IExpression when;
	final IExpression solidarity;
	final IExpression familiarity;
	final IExpression trust;
	final IExpression agent;
	
	public SocializeStatement(IDescription desc) {
		super(desc);
		name = getFacet(IKeyword.NAME);
		appreciation = getFacet(SocializeStatement.LIKING);
		dominance = getFacet(SocializeStatement.DOMINANCE);
		when = getFacet(IKeyword.WHEN);
		solidarity = getFacet(SocializeStatement.SOLIDARITY);
		familiarity = getFacet(SocializeStatement.FAMILIARITY);
		trust = getFacet(SocializeStatement.TRUST);
		agent = getFacet(SocializeStatement.AGENT);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		if (when == null || Cast.asBool(scope, when.value(scope))) {
			final IAgent[] stack = scope.getAgentsStack();
			final IAgent mySelfAgent = stack[stack.length - 2];
			IScope scopeMySelf = null;
			if (mySelfAgent != null) {
				scopeMySelf = mySelfAgent.getScope().copy("in SocializeStatement");
				scopeMySelf.push(mySelfAgent);
			}
			if(!scope.getAgent().equals(mySelfAgent)){
				SocialLink tempSocial = new SocialLink(scope.getAgent());
				if(!SimpleBdiArchitecture.hasSocialLink(scopeMySelf, tempSocial)){
					if (appreciation != null) {
						tempSocial.setLiking(Cast.asFloat(scopeMySelf, appreciation.value(scopeMySelf)));;
					}
					if (dominance != null){
						tempSocial.setDominance(Cast.asFloat(scopeMySelf, dominance.value(scopeMySelf)));
					}
					if (solidarity != null){
						tempSocial.setSolidarity(Cast.asFloat(scopeMySelf, solidarity.value(scopeMySelf)));
					}
					if (familiarity != null){
						tempSocial.setFamiliarity(Cast.asFloat(scopeMySelf, familiarity.value(scopeMySelf)));
					}
					if (trust != null){
						tempSocial.setTrust(Cast.asFloat(scopeMySelf, trust.value(scopeMySelf)));
					}
					if (agent != null){
						tempSocial.setAgent((IAgent)agent.value(scopeMySelf));
					}
					SimpleBdiArchitecture.addSocialLink(scopeMySelf, tempSocial);
				} else{
					/*update le social link.*/
					tempSocial = SimpleBdiArchitecture.getSocialLink(scopeMySelf, tempSocial);
					SimpleBdiArchitecture.updateSocialLink(scopeMySelf, tempSocial);
				}
			}
			GAMA.releaseScope(scopeMySelf);
		}
		return null;
	}

}
