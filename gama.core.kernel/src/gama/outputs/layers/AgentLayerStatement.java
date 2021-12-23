/*******************************************************************************************************
 *
 * AgentLayerStatement.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.outputs.layers;

import java.util.ArrayList;
import java.util.List;

import gama.common.interfaces.IGamlIssue;
import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.example;
import gama.core.dev.annotations.GamlAnnotations.facet;
import gama.core.dev.annotations.GamlAnnotations.facets;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.core.dev.annotations.GamlAnnotations.usage;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.outputs.LayeredDisplayOutput;
import gama.outputs.layers.AgentLayerStatement.AgentLayerValidator;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.compilation.IDescriptionValidator;
import gaml.compilation.ISymbol;
import gaml.compilation.annotations.validator;
import gaml.descriptions.IDescription;
import gaml.descriptions.IExpressionDescription;
import gaml.descriptions.SpeciesDescription;
import gaml.descriptions.StatementDescription;
import gaml.expressions.IExpression;
import gaml.factories.DescriptionFactory;
import gaml.operators.Cast;
import gaml.statements.AspectStatement;
import gaml.statements.IExecutable;
import gaml.statements.IStatement;
import gaml.types.IType;

// TODO: Auto-generated Javadoc
/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol (
		name = IKeyword.AGENTS,
		kind = ISymbolKind.LAYER,
		with_sequence = true,
		remote_context = true,
		concept = { IConcept.SPECIES, IConcept.DISPLAY })
@inside (
		symbols = IKeyword.DISPLAY)
@facets (
		value = { @facet (
				name = IKeyword.VALUE,
				type = IType.CONTAINER,
				of = IType.AGENT,
				optional = false,
				doc = @doc ("the set of agents to display")),
				@facet (
						name = IKeyword.TRACE,
						type = { IType.BOOL, IType.INT },
						optional = true,
						doc = @doc ("Allows to aggregate the visualization of agents at each timestep on the display. Default is false. If set to an int value, only the last n-th steps will be visualized. If set to true, no limit of timesteps is applied. ")),
				@facet (
						name = IKeyword.SELECTABLE,
						type = { IType.BOOL },
						optional = true,
						doc = @doc ("Indicates whether the agents present on this layer are selectable by the user. Default is true")),
				@facet (
						name = IKeyword.FADING,
						type = { IType.BOOL },
						optional = true,
						doc = @doc ("Used in conjunction with 'trace:', allows to apply a fading effect to the previous traces. Default is false")),
				@facet (
						name = IKeyword.POSITION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1 for x and y. The z-ordinate can only be defined between 0 and 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
				@facet (
						name = IKeyword.SIZE,
						type = IType.POINT,
						optional = true,
						doc = @doc ("extent of the layer in the screen from its position. Coordinates in [0,1[ are treated as percentages of the total surface, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Like in 'position', an elevation can be provided with the z coordinate, allowing to scale the layer in the 3 directions ")),
				@facet (
						name = IKeyword.TRANSPARENCY,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the transparency level of the layer (between 0 -- opaque -- and 1 -- fully transparent)")),
				@facet (
						name = IKeyword.VISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Defines whether this layer is visible or not")),
				@facet (
						name = IKeyword.NAME,
						type = IType.LABEL,
						optional = true,
						doc = @doc ("Human readable title of the layer")),
				@facet (
						name = IKeyword.FOCUS,
						type = IType.AGENT,
						optional = true,
						doc = @doc ("the agent on which the camera will be focused (it is dynamically computed)")),
				@facet (
						name = IKeyword.ASPECT,
						type = IType.ID,
						optional = true,
						doc = @doc ("the name of the aspect that should be used to display the species")),
				@facet (
						name = IKeyword.REFRESH,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("(openGL only) specify whether the display of the species is refreshed. (true by default, useful in case of agents that do not move)")) },
		omissible = IKeyword.NAME)
@validator (AgentLayerValidator.class)
@doc (
		value = "`" + IKeyword.AGENTS
		+ "` allows the modeler to display only the agents that fulfill a given condition.",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "display my_display {",
						isExecutable = false),
						@example (
								value = "   agents layer_name value: expression [additional options];",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }),
				@usage (
						value = "For instance, in a segregation model, `agents` will only display unhappy agents:",
						examples = { @example (
								value = "display Segregation {",
								isExecutable = false),
								@example (
										value = "   agents agentDisappear value: people as list where (each.is_happy = false) aspect: with_group_color;",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION, IKeyword.IMAGE,
				IKeyword.OVERLAY, IKeyword.POPULATION })
public class AgentLayerStatement extends AbstractLayerStatement {

	/**
	 * The Class AgentLayerValidator.
	 */
	public static class AgentLayerValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate().
		 *
		 * @param description the description
		 * @see gaml.compilation.IDescriptionValidator#validate(gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription description) {
			// Should be broken down in subclasses
			IExpressionDescription ed = description.getFacet(VALUE);
			SpeciesDescription target;
			if (ed == null || ed.getExpression() == null) return;
			target = ed.getExpression().getGamlType().getContentType().getSpecies();
			if (target == null) // Already caught by the type checking
				return;
			ed = description.getFacet(ASPECT);
			if (ed != null) {
				final String a = description.getLitteral(ASPECT);
				if (target.getAspect(a) != null) {
					ed.compileAsLabel();
				} else if (a != null && !a.equals(DEFAULT)) {
					description.error(a + " is not the name of an aspect of " + target.getName(),
							IGamlIssue.GENERAL, description.getFacet(ASPECT).getTarget());
				}

			}
		}

	}

	/** The agents expr. */
	private IExpression agentsExpr;

	/** The constant aspect name. */
	protected String constantAspectName = null;

	/** The aspect expr. */
	protected IExpression aspectExpr;

	/** The aspect. */
	private IExecutable aspect = null;

	/**
	 * Instantiates a new agent layer statement.
	 *
	 * @param desc the desc
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public AgentLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setAgentsExpr(getFacet(IKeyword.VALUE));
		if (name == null && agentsExpr != null) {
			setName(agentsExpr.serialize(false));
		}
		aspectExpr = getFacet(IKeyword.ASPECT);
		if (aspectExpr != null && aspectExpr.isConst()) {
			constantAspectName = aspectExpr.literalValue();
		}
	}

	/**
	 * Step.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 */
	@Override
	public boolean _step(final IScope scope) {
		return true;
	}

	/**
	 * Inits the.
	 *
	 * @param scope the scope
	 * @return true, if successful
	 */
	@Override
	public boolean _init(final IScope scope) {
		computeAspectName(scope);
		return true;
	}

	/**
	 * Gets the type.
	 *
	 * @param output the output
	 * @return the type
	 */
	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.AGENTS;
	}

	/**
	 * Compute aspect name.
	 *
	 * @param scope the scope
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void computeAspectName(final IScope scope) throws GamaRuntimeException {
		final String aspectName = constantAspectName == null
				? aspectExpr == null ? IKeyword.DEFAULT : Cast.asString(scope, aspectExpr.value(scope))
						: constantAspectName;
		setAspect(aspectName);
	}

	/**
	 * Sets the aspect.
	 *
	 * @param currentAspect the new aspect
	 */
	public void setAspect(final String currentAspect) {
		this.constantAspectName = currentAspect;
	}

	/**
	 * Gets the aspect name.
	 *
	 * @return the aspect name
	 */
	public String getAspectName() {
		return constantAspectName;
	}

	/**
	 * Sets the agents expr.
	 *
	 * @param setOfAgents the new agents expr
	 */
	public void setAgentsExpr(final IExpression setOfAgents) {
		this.agentsExpr = setOfAgents;
	}

	/**
	 * Gets the agents expr.
	 *
	 * @return the agents expr
	 */
	IExpression getAgentsExpr() {
		return agentsExpr;
	}

	/**
	 * Gets the aspect.
	 *
	 * @return the aspect
	 */
	public IExecutable getAspect() {
		return aspect;
	}

	/**
	 * Sets the children.
	 *
	 * @param commands the new children
	 */
	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		final List<IStatement> aspectStatements = new ArrayList<>();
		for (final ISymbol c : commands) {
			if (c instanceof IStatement) {
				aspectStatements.add((IStatement) c);
			}
		}
		if (!aspectStatements.isEmpty()) {
			constantAspectName = "inline";
			final IDescription d =
					DescriptionFactory.create(IKeyword.ASPECT, getDescription(), IKeyword.NAME, "inline");
			aspect = new AspectStatement(d);
			((AspectStatement) aspect).setChildren(aspectStatements);
		}
	}

}