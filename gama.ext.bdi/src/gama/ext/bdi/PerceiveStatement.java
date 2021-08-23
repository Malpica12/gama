/*********************************************************************************************
 * 
 *
 * 'PerceiveStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the GAMA
 * modeling and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package gama.ext.bdi;

import java.util.Iterator;

import gama.common.interfaces.IKeyword;
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
import gama.metamodel.shape.GamaShape;
import gama.metamodel.shape.IShape;
import gama.runtime.ExecutionResult;
import gama.runtime.IScope;
import gama.runtime.concurrent.GamaExecutorService;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaListFactory;
import gama.util.IContainer;
import gama.util.IList;
import gaml.compilation.ISymbol;
import gaml.descriptions.IDescription;
import gaml.expressions.IExpression;
import gaml.operators.Cast;
import gaml.statements.AbstractStatementSequence;
import gaml.statements.RemoteSequence;
import gaml.types.IType;
import gaml.types.Types;

@symbol (
		name = { PerceiveStatement.PERCEIVE },
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		remote_context = true,
		concept = { IConcept.BDI })
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = true,
				doc = @doc ("the name of the perception")),
				@facet (
						name = IKeyword.AS,
						type = IType.SPECIES,
						optional = true,
						doc = @doc ("an expression that evaluates to a species")),
				@facet (
						name = IKeyword.WHEN,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("a boolean to tell when does the perceive is active")),
				@facet (
						name = IKeyword.PARALLEL,
						type = { IType.BOOL, IType.INT },
						optional = true,
						doc = @doc ("setting this facet to 'true' will allow 'perceive' to use concurrency with a parallel_bdi architecture; setting it to an integer will set the threshold under which they will be run sequentially (the default is initially 20, but can be fixed in the preferences). This facet is true by default.")),

				@facet (
						name = IKeyword.IN,
						type = { IType.FLOAT, IType.GEOMETRY },
						optional = true,
						doc = @doc ("a float or a geometry. If it is a float, it's a radius of a detection area. If it is a geometry, it is the area of detection of others species.")),
				@facet (
						name = PerceiveStatement.EMOTION,
						type = EmotionType.id,
						optional = true,
						doc = @doc ("The emotion needed to do the perception")),
				@facet (
						name = PerceiveStatement.THRESHOLD,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("Threshold linked to the emotion.")),
				@facet (
						name = IKeyword.TARGET,
						type = { IType.CONTAINER, /* IType.POINT, */
								IType.AGENT },
						of = IType.AGENT,
						optional = false,
						doc = @doc ("the list of the agent you want to perceive")) },
		omissible = IKeyword.NAME)
@doc (
		value = "Allow the agent, with a bdi architecture, to perceive others agents",
		usages = { @usage (
				value = "the basic syntax to perceive agents inside a circle of perception",
				examples = { @example (
						value = "perceive name_of-perception target: the_agents_you_want_to_perceive in: a_distance when: a_certain_condition {",
						isExecutable = false),
						@example (
								value = "Here you are in the context of the perceived agents. To refer to the agent who does the perception, use myself.",
								isExecutable = false),
						@example (
								value = "If you want to make an action (such as adding a belief for example), use ask myself{ do the_action}",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }) })

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class PerceiveStatement extends AbstractStatementSequence {

	public static final String PERCEIVE = "perceive";
	public static final String EMOTION = "emotion";
	public static final String THRESHOLD = "threshold";

	private RemoteSequence sequence = null;

	final IExpression _when;
	final IExpression _in;
	final IExpression emotion;
	final IExpression threshold;
	final IExpression parallel;
	private final IExpression target = getFacet(IKeyword.TARGET);
	// AD Dangerous as it may still contain a value after the execution. Better
	// to use temp arrays
	// private final Object[] result = new Object[1];

	public IExpression getWhen() {
		return _when;
	}

	public IExpression getIn() {
		return _in;
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> com) {
		sequence = new RemoteSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	public void leaveScope(final IScope scope) {
		scope.popLoop();
		super.leaveScope(scope);
	}

	public PerceiveStatement(final IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		if (hasFacet(IKeyword.IN)) {
			_in = getFacet(IKeyword.IN);
		} else {
			_in = null;
		}
		if (hasFacet(IKeyword.NAME)) {
			setName(getLiteral(IKeyword.NAME));
		}
		emotion = getFacet(PerceiveStatement.EMOTION);
		threshold = getFacet(PerceiveStatement.THRESHOLD);
		parallel = getFacet(IKeyword.PARALLEL);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		if (_when == null || Cast.asBool(scope, _when.value(scope))) {
			final Object obj = target.value(scope);
			Object inArg = null;
			final IAgent ag = scope.getAgent();
			if (_in != null) {
				inArg = _in.value(scope);
			}
			if (emotion == null || SimpleBdiArchitecture.hasEmotion(scope, (Emotion) emotion.value(scope))) {
				if (threshold == null || emotion != null && threshold != null && SimpleBdiArchitecture.getEmotion(scope,
						(Emotion) emotion.value(scope)).intensity >= (Double) threshold.value(scope)) {
					if (inArg instanceof Float || inArg instanceof Integer || inArg instanceof Double) {
						IList temp = GamaListFactory.create();
						final double dist = Cast.asFloat(scope, inArg);
						if (obj instanceof IContainer) {
							temp = gaml.operators.Spatial.Queries.at_distance(scope, (IContainer) obj,
									Cast.asFloat(scope, inArg));
						} else if (obj instanceof IAgent) {
							if (ag.euclidianDistanceTo((IAgent) obj) <= dist) {
								temp.add(obj);
							}
						}
						GamaExecutorService.execute(scope, sequence, temp.listValue(scope, Types.AGENT, false), null);
						return this;

					} else if (inArg instanceof gaml.types.GamaGeometryType || inArg instanceof GamaShape) {
						IList temp = GamaListFactory.create();
						final IShape geom = Cast.asGeometry(scope, inArg);
						if (obj instanceof IContainer) {
							temp = gaml.operators.Spatial.Queries.overlapping(scope, (IContainer) obj,
									Cast.asGeometry(scope, inArg));
						} else if (obj instanceof IAgent) {
							if (geom.intersects((IShape) obj)) {
								temp.add(obj);
							}
						}
						GamaExecutorService.execute(scope, sequence, temp.listValue(scope, Types.AGENT, false), null);
						return this;
					} else {
						ExecutionResult result = null;
						final Iterator<IAgent> runners =
								obj instanceof IContainer ? ((IContainer) obj).iterable(scope).iterator()
										: obj instanceof IAgent ? transformAgentToList((IAgent) obj, scope) : null;
						if (runners != null) {
							while (runners.hasNext()
									&& (result = scope.execute(sequence, runners.next(), null)).passed()) {}
						}
						if (result != null) { return result.getValue(); }
					}
				}
			}
		}

		return null;

	}

	Iterator<IAgent> transformAgentToList(final IAgent temp, final IScope scope) {
		final IList<IAgent> tempList = GamaListFactory.create();
		tempList.add(temp);
		return ((IContainer) tempList).iterable(scope).iterator();
	}

	public IExpression getParallel() {
		return parallel;
	}

}
