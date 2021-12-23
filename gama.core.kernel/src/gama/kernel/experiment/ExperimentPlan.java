/*******************************************************************************************************
 *
 * ExperimentPlan.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.kernel.experiment;

import static gama.common.interfaces.IKeyword.TEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;

import gama.common.interfaces.IGamlIssue;
import gama.common.interfaces.IKeyword;
import gama.common.preferences.GamaPreferences;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.facet;
import gama.core.dev.annotations.GamlAnnotations.facets;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.kernel.batch.BatchOutput;
import gama.kernel.batch.ExhaustiveSearch;
import gama.kernel.batch.IExploration;
import gama.kernel.experiment.ExperimentPlan.BatchValidator;
import gama.kernel.model.IModel;
import gama.kernel.simulation.SimulationAgent;
import gama.metamodel.agent.IAgent;
import gama.metamodel.population.GamaPopulation;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.topology.continuous.AmorphousTopology;
import gama.outputs.ExperimentOutputManager;
import gama.outputs.FileOutput;
import gama.outputs.IOutputManager;
import gama.outputs.LayoutStatement;
import gama.outputs.SimulationOutputManager;
import gama.runtime.ExecutionScope;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaMapFactory;
import gama.util.IList;
import gaml.compilation.IDescriptionValidator;
import gaml.compilation.ISymbol;
import gaml.compilation.annotations.validator;
import gaml.compilation.kernel.GamaMetaModel;
import gaml.descriptions.ExperimentDescription;
import gaml.descriptions.IDescription;
import gaml.expressions.IExpression;
import gaml.operators.Cast;
import gaml.species.GamlSpecies;
import gaml.species.ISpecies;
import gaml.types.IType;
import gaml.variables.IVariable;

// TODO: Auto-generated Javadoc
/**
 * Written by drogoul Modified on 28 mai 2011 Apr. 2013: Important modifications to enable running true experiment
 * agents
 *
 *
 * Dec 2015: ExperimentPlans now manage their own controller. They are entirely responsible for its life-cycle
 * (creation, disposal)
 *
 * @todo Description
 *
 */
@symbol (
		name = { IKeyword.EXPERIMENT },
		kind = ISymbolKind.EXPERIMENT,
		with_sequence = true,
		concept = { IConcept.EXPERIMENT })
@doc ("Declaration of a particular type of agent that can manage simulations. If the experiment directly imports a model using the 'model:' facet, this facet *must* be the first one after the name of the experiment")
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.LABEL,
				optional = false,
				doc = @doc ("identifier of the experiment")),
				@facet (
						name = IKeyword.TITLE,
						type = IType.LABEL,
						optional = false,
						doc = @doc (""),
						internal = true),
				@facet (
						name = IKeyword.BENCHMARK,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("If true, make GAMA record the number of invocations and running time of the statements and operators of the simulations launched in this experiment. The results are automatically saved in a csv file in a folder called 'benchmarks' when the experiment is closed"),
						internal = false),
				@facet (
						name = IKeyword.PARENT,
						type = IType.ID,
						optional = true,
						doc = @doc ("the parent experiment (in case of inheritance between experiments)")),
				@facet (
						name = IKeyword.SKILLS,
						type = IType.LIST,
						optional = true,
						doc = @doc (""),
						internal = true),
				@facet (
						name = IKeyword.CONTROL,
						type = IType.ID,
						optional = true,
						doc = @doc (""),
						internal = true),
				@facet (
						name = IKeyword.FREQUENCY,
						type = IType.INT,
						optional = true,
						internal = true,
						doc = @doc ("the execution frequence of the experiment (default value: 1). If frequency: 10, the experiment is executed only each 10 steps.")),
				@facet (
						name = IKeyword.SCHEDULES,
						type = IType.CONTAINER,
						of = IType.AGENT,
						optional = true,
						internal = true,
						doc = @doc ("A container of agents (a species, a dynamic list, or a combination of species and containers) , which represents which agents will be actually scheduled when the population is scheduled for execution. For instance, 'species a schedules: (10 among a)' will result in a population that schedules only 10 of its own agents every cycle. 'species b schedules: []' will prevent the agents of 'b' to be scheduled. Note that the scope of agents covered here can be larger than the population, which allows to build complex scheduling controls; for instance, defining 'global schedules: [] {...} species b schedules: []; species c schedules: b + world; ' allows to simulate a model where the agents of b are scheduled first, followed by the world, without even having to create an instance of c.")),
				@facet (
						name = IKeyword.KEEP_SEED,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Allows to keep the same seed between simulations. Mainly useful for batch experiments")),
				@facet (
						name = IKeyword.KEEP_SIMULATIONS,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("In the case of a batch experiment, specifies whether or not the simulations should be kept in memory for further analysis or immediately discarded with only their fitness kept in memory")),
				@facet (
						name = IKeyword.REPEAT,
						type = IType.INT,
						optional = true,
						doc = @doc ("In the case of a batch experiment, expresses hom many times the simulations must be repeated")),
				@facet (
						name = IKeyword.UNTIL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("In the case of a batch experiment, an expression that will be evaluated to know when a simulation should be terminated")),
				@facet (
						name = IKeyword.PARALLEL,
						type = { IType.BOOL, IType.INT },
						optional = true,
						doc = @doc ("When set to true, use multiple threads to run its simulations. Setting it to n will set the numbers of threads to use")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.LABEL,
						values = { IKeyword.BATCH, IKeyword.MEMORIZE, /* IKeyword.REMOTE, */IKeyword.GUI_,
								IKeyword.TEST, IKeyword.HEADLESS_UI },
						optional = false,
						doc = @doc ("the type of the experiment (either 'gui' or 'batch'")),
				@facet (
						name = IKeyword.VIRTUAL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("whether the experiment is virtual (cannot be instantiated, but only used as a parent, false by default)")),
				@facet (
						name = IKeyword.AUTORUN,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("whether this experiment should be run automatically when launched (false by default)")) },
		omissible = IKeyword.NAME)
@inside (
		kinds = { ISymbolKind.MODEL })
@validator (BatchValidator.class)
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class ExperimentPlan extends GamlSpecies implements IExperimentPlan {

	/**
	 * The Class BatchValidator.
	 */
	public static class BatchValidator implements IDescriptionValidator {

		/**
		 * Method validate().
		 *
		 * @param desc the desc
		 * @see gaml.compilation.IDescriptionValidator#validate(gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {
			final String type = desc.getLitteral(TYPE);
			// if (type.equals(MEMORIZE)) {
			// desc.warning("The memorize experiment is still in development. It should not be used.",
			// IGamlIssue.DEPRECATED);
			// }
			if (!BATCH.equals(type) && desc.getChildWithKeyword(METHOD) != null) {
				desc.error(type + " experiments cannot define exploration methods", IGamlIssue.CONFLICTING_FACETS,
						METHOD);
			}
			if (BATCH.equals(type) && !desc.hasFacet(UNTIL)) {
				desc.warning(
						"No stopping condition have been defined (facet 'until:'). This may result in an endless run of the "
								+ type + " experiment",
								IGamlIssue.MISSING_FACET, desc.getUnderlyingElement(), UNTIL, "true");
			}
		}
	}

	/** The controller. */
	protected IExperimentController controller;
	// An original copy of the simualtion outputs (which will be eventually
	/** The original simulation outputs. */
	// duplicated in all the simulations)
	protected SimulationOutputManager originalSimulationOutputs;

	/** The experiment outputs. */
	protected ExperimentOutputManager experimentOutputs;

	/** The parameters. */
	// private ItemList parametersEditors;
	protected final Map<String, IParameter> parameters = GamaMapFactory.create();

	/** The explorable parameters. */
	protected final Map<String, IParameter.Batch> explorableParameters = GamaMapFactory.create();

	/** The agent. */
	protected ExperimentAgent agent;

	/** The my scope. */
	protected final Scope myScope = new Scope("in ExperimentPlan");

	/** The model. */
	protected IModel model;

	/** The exploration. */
	protected IExploration exploration;

	/** The log. */
	private FileOutput log;

	/** The is headless. */
	private boolean isHeadless;

	/** The keep seed. */
	private final boolean keepSeed;

	/** The keep simulations. */
	private final boolean keepSimulations;

	/** The experiment type. */
	private final String experimentType;

	/** The autorun. */
	private final boolean autorun;

	/** The benchmarkable. */
	private final boolean benchmarkable;

	/**
	 * The Class ExperimentPopulation.
	 */
	public class ExperimentPopulation extends GamaPopulation<ExperimentAgent> {

		/**
		 * Instantiates a new experiment population.
		 *
		 * @param expr the expr
		 */
		public ExperimentPopulation(final ISpecies expr) {
			super(null, expr);
		}

		/**
		 * Creates the agents.
		 *
		 * @param scope the scope
		 * @param number the number
		 * @param initialValues the initial values
		 * @param isRestored the is restored
		 * @param toBeScheduled the to be scheduled
		 * @return the i list
		 * @throws GamaRuntimeException the gama runtime exception
		 */
		@Override
		public IList<ExperimentAgent> createAgents(final IScope scope, final int number,
				final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
						final boolean toBeScheduled) throws GamaRuntimeException {
			for (int i = 0; i < number; i++) {
				agent = GamaMetaModel.INSTANCE.createExperimentAgent(getExperimentType(), this, currentAgentIndex++);
				// agent.setIndex(currentAgentIndex++);
				add(agent);
				scope.push(agent);
				createVariables(scope, agent, initialValues.isEmpty() ? Collections.EMPTY_MAP : initialValues.get(i));
				scope.pop(agent);
			}
			return this;
		}

		/**
		 * Creates the variables.
		 *
		 * @param scope the scope
		 * @param a the a
		 * @param inits the inits
		 * @throws GamaRuntimeException the gama runtime exception
		 */
		public void createVariables(final IScope scope, final IAgent a, final Map<String, Object> inits)
				throws GamaRuntimeException {
			final Set<String> names = inits.keySet();
			for (final IVariable var : orderedVars) {

				String s = var.getName();
				var.initializeWith(scope, a, inits.get(s));
				names.remove(s);
			}
			for (final String s : names) {
				a.getScope().setAgentVarValue(a, s, inits.get(s));
			}

		}

		/**
		 * Step agents.
		 *
		 * @param scope the scope
		 * @return true, if successful
		 */
		@Override
		protected boolean stepAgents(final IScope scope) {
			return scope.step(agent).passed();
		}

		/**
		 * Gets the agent.
		 *
		 * @param scope the scope
		 * @param value the value
		 * @return the agent
		 */
		@Override
		public ExperimentAgent getAgent(final IScope scope, final GamaPoint value) {
			return agent;
		}

		/**
		 * Compute topology.
		 *
		 * @param scope the scope
		 * @throws GamaRuntimeException the gama runtime exception
		 */
		@Override
		public void computeTopology(final IScope scope) throws GamaRuntimeException {
			topology = new AmorphousTopology();
		}

	}

	/**
	 * Checks if is headless.
	 *
	 * @return true, if is headless
	 */
	@Override
	public boolean isHeadless() {
		return GAMA.isInHeadLessMode() || isHeadless;
	}

	/**
	 * Sets the headless.
	 *
	 * @param headless the new headless
	 */
	@Override
	public void setHeadless(final boolean headless) {
		isHeadless = headless;
	}

	/**
	 * Gets the agent.
	 *
	 * @return the agent
	 */
	@Override
	public ExperimentAgent getAgent() {
		return agent;
	}

	/**
	 * Instantiates a new experiment plan.
	 *
	 * @param description the description
	 */
	public ExperimentPlan(final IDescription description) {
		super(description);
		setName(description.getName());
		experimentType = description.getLitteral(IKeyword.TYPE);
		// final String type = description.getFacets().getLabel(IKeyword.TYPE);
		if (IKeyword.BATCH.equals(experimentType) || IKeyword.TEST.equals(experimentType)) {
			exploration = new ExhaustiveSearch(null);
		} else if (IKeyword.HEADLESS_UI.equals(experimentType)) { setHeadless(true); }
		final IExpression expr = getFacet(IKeyword.KEEP_SEED);
		if (expr != null && expr.isConst()) {
			keepSeed = Cast.asBool(myScope, expr.value(myScope));
		} else {
			keepSeed = false;
		}
		final IExpression ksExpr = getFacet(IKeyword.KEEP_SIMULATIONS);
		if (ksExpr != null && ksExpr.isConst()) {
			keepSimulations = Cast.asBool(myScope, ksExpr.value(myScope));
		} else {
			keepSimulations = true;
		}
		final IExpression ar = getFacet(IKeyword.AUTORUN);
		if (ar == null) {
			autorun = GamaPreferences.Runtime.CORE_AUTO_RUN.getValue();
		} else {
			autorun = Cast.asBool(myScope, ar.value(myScope));
		}
		final IExpression bm = getFacet(IKeyword.BENCHMARK);
		benchmarkable = bm != null && Cast.asBool(myScope, bm.value(myScope));
	}

	/**
	 * Checks if is autorun.
	 *
	 * @return true, if is autorun
	 */
	@Override
	public boolean isAutorun() {
		return autorun;
	}

	/**
	 * Keeps seed.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean keepsSeed() {
		return keepSeed;
	}

	/**
	 * Keeps simulations.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean keepsSimulations() {
		return keepSimulations;
	}

	/**
	 * Dispose.
	 */
	@Override
	public void dispose() {
		// DEBUG.LOG("ExperimentPlan.dipose BEGIN");
		// Dec 2015 Addition
		if (controller != null) { controller.dispose(); }
		if (agent != null) {
			agent.dispose();
			agent = null;
		}
		if (originalSimulationOutputs != null) {
			originalSimulationOutputs.dispose();
			originalSimulationOutputs = null;
		}
		if (experimentOutputs != null) {
			experimentOutputs.dispose();
			experimentOutputs = null;
		}
		parameters.clear();

		// FIXME Should be put somewhere around here, but probably not here
		// exactly.
		// ProjectionFactory.reset();

		super.dispose();
		// DEBUG.LOG("ExperimentPlan.dipose END");
	}

	/**
	 * Creates the agent.
	 *
	 * @param seed the seed
	 */
	public void createAgent(final Double seed) {
		final ExperimentPopulation pop = new ExperimentPopulation(this);
		final IScope scope = getExperimentScope();
		pop.initializeFor(scope);
		final List<Map<String, Object>> params =
				seed == null ? Collections.EMPTY_LIST : Arrays.asList(new HashMap<String, Object>() {
					{
						put(IKeyword.SEED, seed);
					}
				});
		agent = pop.createAgents(scope, 1, params, false, true).get(0);
		addDefaultParameters();
	}

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	/*
	 * public void createAgent() { final ExperimentPopulation pop = new ExperimentPopulation(this); final IScope scope =
	 * getExperimentScope(); pop.initializeFor(scope); agent = (ExperimentAgent) pop.createAgents(scope, 1,
	 * Collections.EMPTY_LIST, false, true).get(0); addDefaultParameters(); }
	 */
	@Override
	public IModel getModel() {
		return model;
	}

	/**
	 * Sets the model.
	 *
	 * @param model the new model
	 */
	@Override
	public void setModel(final IModel model) {
		this.model = model;
		if (!isBatch()) {
			// We look first in the experiment itself
			for (final IVariable v : getVars()) {
				if (v.isParameter()) {
					final ExperimentParameter p = new ExperimentParameter(myScope, v);
					final String parameterName = "(Experiment) " + p.getName();
					final boolean already = parameters.containsKey(parameterName);
					if (!already) { parameters.put(parameterName, p); }
				}
			}

			for (final IVariable v : model.getVars()) {
				if (v.isParameter()) {
					final IParameter p = new ExperimentParameter(myScope, v);
					final String parameterName = p.getName();
					final boolean already = parameters.containsKey(parameterName);
					if (!already) { parameters.put(parameterName, p); }
				}

			}
		}
	}

	/**
	 * Adds the default parameters.
	 */
	protected void addDefaultParameters() {
		for (final IParameter.Batch p : agent.getDefaultParameters()) {
			addParameter(p);
		}
	}

	/**
	 * Gets the experiment outputs.
	 *
	 * @return the experiment outputs
	 */
	@Override
	public final IOutputManager getExperimentOutputs() {
		return experimentOutputs;
	}

	/**
	 * Sets the children.
	 *
	 * @param children the new children
	 */
	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {
		super.setChildren(children);

		BatchOutput fileOutputDescription = null;
		LayoutStatement layout = null;
		for (final ISymbol s : children) {
			if (s instanceof LayoutStatement) {
				layout = (LayoutStatement) s;
			} else if (s instanceof IExploration) {
				exploration = (IExploration) s;
			} else if (s instanceof BatchOutput) {
				fileOutputDescription = (BatchOutput) s;
			} else if (s instanceof SimulationOutputManager) {
				if (originalSimulationOutputs != null) {
					originalSimulationOutputs.setChildren((SimulationOutputManager) s);
				} else {
					originalSimulationOutputs = (SimulationOutputManager) s;
				}
			} else if (s instanceof IParameter.Batch pb) {
				if (isBatch() && pb.canBeExplored()) {
					pb.setEditable(false);
					addExplorableParameter(pb);
					continue;
				}
				final IParameter p = (IParameter) s;
				final String parameterName = p.getName();
				final boolean already = parameters.containsKey(parameterName);
				if (!already) { parameters.put(parameterName, p); }
			} else if (s instanceof ExperimentOutputManager) {
				if (experimentOutputs != null) {
					experimentOutputs.setChildren((ExperimentOutputManager) s);
				} else {
					experimentOutputs = (ExperimentOutputManager) s;
				}
			}
		}
		if (originalSimulationOutputs == null) { originalSimulationOutputs = SimulationOutputManager.createEmpty(); }
		if (experimentOutputs == null) { experimentOutputs = ExperimentOutputManager.createEmpty(); }
		if (experimentOutputs.getLayout() == null) {
			if (layout != null) {
				experimentOutputs.setLayout(layout);
			} else if (originalSimulationOutputs.getLayout() != null) {
				experimentOutputs.setLayout(originalSimulationOutputs.getLayout());
			}
		}
		if (fileOutputDescription != null) { createOutput(fileOutputDescription); }

	}

	/**
	 * Creates the output.
	 *
	 * @param output the output
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	private void createOutput(final BatchOutput output) throws GamaRuntimeException {
		// TODO revoir tout ceci. Devrait plut�t �tre une commande
		if (output == null) return;
		IExpression data = output.getFacet(IKeyword.DATA);
		if (data == null) { data = exploration.getFitnessExpression(); }
		final String dataString = data == null ? "time" : data.serialize(false);
		log = new FileOutput(output.getLiteral(IKeyword.TO), dataString, new ArrayList(parameters.keySet()), this);
	}

	/**
	 * Open.
	 *
	 * @param seed the seed
	 */
	public synchronized void open(final Double seed) {

		createAgent(seed);
		myScope.getGui().prepareForExperiment(myScope, this);
		agent.schedule(agent.getScope());
		if (isHeadless()) {
			// Always auto start in headless mode
			this.getController().userStart();
		} else if (isBatch()) {
			agent.getScope().getGui().getStatus(agent.getScope())
			.informStatus(isTest() ? "Tests ready. Click run to begin." : " Batch ready. Click run to begin.");
			agent.getScope().getGui().updateExperimentState(agent.getScope());
		}
	}

	/**
	 * Open.
	 */
	@Override
	public synchronized void open() {
		Double seed = null;
		if (isHeadless()) {
			try {
				seed = this.getAgent().getSeed();
			} catch (Exception e) { // Catch no seed
				seed = null;
			}
		}
		open(seed);	}

	/**
	 * Reload.
	 */
	/*
	 * @Override public synchronized void open() {
	 *
	 * createAgent(); myScope.getGui().prepareForExperiment(myScope, this); agent.schedule(agent.getScope()); if
	 * (isBatch()) { agent.getScope().getGui().getStatus(agent.getScope()) .informStatus(isTest() ?
	 * "Tests ready. Click run to begin." : " Batch ready. Click run to begin.");
	 * agent.getScope().getGui().updateExperimentState(agent.getScope()); } }
	 */
	@Override
	public void reload() {
		// if (isBatch()) {
		agent.dispose();
		open();
		// } else {
		// agent.reset();
		// agent.getScope().getGui().getConsole(agent.getScope()).eraseConsole(false);
		// agent.init(agent.getScope());
		//
		// agent.getScope().getGui().updateParameterView(agent.getScope(), this);
		// }
	}

	/**
	 * Checks for parameters or user commands.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasParametersOrUserCommands() {
		return !parameters.isEmpty() || !explorableParameters.isEmpty() || !getUserCommands().isEmpty();
	}

	/**
	 * Checks if is batch.
	 *
	 * @return true, if is batch
	 */
	// @Override
	@Override
	public boolean isBatch() {
		return exploration != null;
	}

	/**
	 * Checks if is test.
	 *
	 * @return true, if is test
	 */
	@Override
	public boolean isTest() {
		return TEST.equals(getExperimentType());
	}

	/**
	 * Checks if is memorize.
	 *
	 * @return true, if is memorize
	 */
	@Override
	public boolean isMemorize() {
		return IKeyword.MEMORIZE.equals(getExperimentType());
	}

	/**
	 * Checks if is gui.
	 *
	 * @return true, if is gui
	 */
	@Override
	public boolean isGui() {
		return true;
	}

	/**
	 * Gets the experiment scope.
	 *
	 * @return the experiment scope
	 */
	@Override
	public IScope getExperimentScope() {
		return myScope;
	}

	/**
	 * Sets the parameter value.
	 *
	 * @param scope the scope
	 * @param name the name
	 * @param val the val
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	// @Override
	public void setParameterValue(final IScope scope, final String name, final Object val) throws GamaRuntimeException {
		checkGetParameter(name).setValue(scope, val);
	}

	/**
	 * Sets the parameter value by title.
	 *
	 * @param scope the scope
	 * @param name the name
	 * @param val the val
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public void setParameterValueByTitle(final IScope scope, final String name, final Object val)
			throws GamaRuntimeException {
		checkGetParameterByTitle(name).setValue(scope, val);
	}

	/**
	 * Gets the parameter value.
	 *
	 * @param parameterName the parameter name
	 * @return the parameter value
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	// @Override
	public Object getParameterValue(final String parameterName) throws GamaRuntimeException {
		return checkGetParameter(parameterName).value(myScope);
		// VERIFY THE USAGE OF SCOPE HERE
	}

	/**
	 * Checks for parameter.
	 *
	 * @param parameterName the parameter name
	 * @return true, if successful
	 */
	@Override
	public boolean hasParameter(final String parameterName) {
		return getParameter(parameterName) != null;
	}

	/**
	 * Gets the parameter by title.
	 *
	 * @param title the title
	 * @return the parameter by title
	 */
	public IParameter.Batch getParameterByTitle(final String title) {
		for (final IParameter p : parameters.values()) {
			if (p.getTitle().equals(title) && p instanceof IParameter.Batch) return (IParameter.Batch) p;
		}
		return null;
	}

	/**
	 * Gets the parameter.
	 *
	 * @param parameterName the parameter name
	 * @return the parameter
	 */
	public IParameter.Batch getParameter(final String parameterName) {
		final IParameter p = parameters.get(parameterName);
		if (p instanceof IParameter.Batch) return (IParameter.Batch) p;
		return null;
	}

	/**
	 * Adds the parameter.
	 *
	 * @param p the p
	 */
	public void addParameter(final IParameter p) {
		final String parameterName = p.getName();
		final IParameter already = parameters.get(parameterName);
		if (already != null) { p.setValue(myScope, already.getInitialValue(myScope)); }
		parameters.put(parameterName, p);
	}

	/**
	 * Check get parameter by title.
	 *
	 * @param parameterName the parameter name
	 * @return the i parameter. batch
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	protected IParameter.Batch checkGetParameterByTitle(final String parameterName) throws GamaRuntimeException {
		final IParameter.Batch v = getParameterByTitle(parameterName);
		if (v == null) throw GamaRuntimeException
		.error("No parameter named " + parameterName + " in experiment " + getName(), getExperimentScope());
		return v;
	}

	/**
	 * Check get parameter.
	 *
	 * @param parameterName the parameter name
	 * @return the i parameter. batch
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	protected IParameter.Batch checkGetParameter(final String parameterName) throws GamaRuntimeException {
		final IParameter.Batch v = getParameter(parameterName);
		if (v == null) throw GamaRuntimeException
		.error("No parameter named " + parameterName + " in experiment " + getName(), getExperimentScope());
		return v;
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	@Override
	public Map<String, IParameter> getParameters() {
		return parameters;
	}

	/**
	 * Gets the current simulation.
	 *
	 * @return the current simulation
	 */
	@Override
	public SimulationAgent getCurrentSimulation() {
		if (agent == null) return null;
		return agent.getSimulation();
	}

	/**
	 * A short-circuited scope that represents the scope of the experiment plan, before any agent is defined. If a
	 * simulation is available, it refers to it and gains access to its global scope. If not, it throws the appropriate
	 * runtime exceptions when a feature dependent on the existence of a simulation is accessed
	 *
	 * @author Alexis Drogoul
	 * @since November 2011
	 */
	private class Scope extends ExecutionScope {

		/**
		 * Instantiates a new scope.
		 *
		 * @param additionalName the additional name
		 */
		public Scope(final String additionalName) {
			super(null, additionalName);
		}

		/**
		 * Sets the global var value.
		 *
		 * @param name the name
		 * @param v the v
		 * @throws GamaRuntimeException the gama runtime exception
		 */
		@Override
		public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
			if (hasParameter(name)) {
				setParameterValue(this, name, v);
				GAMA.getGui().updateParameterView(getCurrentSimulation().getScope(), ExperimentPlan.this);
				return;
			}
			final SimulationAgent a = getCurrentSimulation();
			if (a != null) { a.setDirectVarValue(this, name, v); }
		}

		/**
		 * Gets the global var value.
		 *
		 * @param varName the var name
		 * @return the global var value
		 * @throws GamaRuntimeException the gama runtime exception
		 */
		@Override
		public Object getGlobalVarValue(final String varName) throws GamaRuntimeException {
			if (hasParameter(varName)) return getParameterValue(varName);
			final SimulationAgent a = getCurrentSimulation();
			if (a != null) return a.getDirectVarValue(this, varName);
			return null;
		}

	}

	/**
	 * Gets the exploration algorithm.
	 *
	 * @return the exploration algorithm
	 */
	@Override
	public IExploration getExplorationAlgorithm() {
		return exploration;
	}

	/**
	 * Gets the log.
	 *
	 * @return the log
	 */
	@Override
	public FileOutput getLog() {
		return log;
	}

	/**
	 * Adds the explorable parameter.
	 *
	 * @param p the p
	 */
	public void addExplorableParameter(final IParameter.Batch p) {
		p.setCategory(EXPLORABLE_CATEGORY_NAME);
		p.setUnitLabel(null);
		explorableParameters.put(p.getName(), p);
	}

	/**
	 * Gets the explorable parameters.
	 *
	 * @return the explorable parameters
	 */
	@Override
	public Map<String, IParameter.Batch> getExplorableParameters() {
		return explorableParameters;
	}

	/**
	 * Method getController().
	 *
	 * @return the controller
	 * @see gama.kernel.experiment.IExperimentPlan#getController()
	 */
	@Override
	public IExperimentController getController() {
		if (controller == null) { controller = new ExperimentController(this); }
		return controller;
	}

	/**
	 * Method refreshAllOutputs().
	 *
	 * @see gama.kernel.experiment.IExperimentPlan#refreshAllOutputs()
	 */
	@Override
	public void refreshAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.forceUpdateOutputs();
		}
	}

	/**
	 * Pause all outputs.
	 */
	@Override
	public void pauseAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.pause();
		}
	}

	/**
	 * Resume all outputs.
	 */
	@Override
	public void resumeAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.resume();
		}
	}

	/**
	 * Synchronize all outputs.
	 */
	@Override
	public void synchronizeAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.synchronize();
		}
	}

	/**
	 * Un synchronize all outputs.
	 */
	@Override
	public void unSynchronizeAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.unSynchronize();
		}
	}

	/**
	 * Close all outputs.
	 */
	@Override
	public void closeAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.close();
		}
	}

	/**
	 * Same as the previous one, but forces the outputs to do one step of computation (if some values have changed).
	 */
	@Override
	public void recomputeAndRefreshAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.step(getExperimentScope());
		}
	}

	/**
	 * Method getOriginalSimulationOutputs().
	 *
	 * @return the original simulation outputs
	 * @see gama.kernel.experiment.IExperimentPlan#getOriginalSimulationOutputs()
	 */
	@Override
	public IOutputManager getOriginalSimulationOutputs() {
		return originalSimulationOutputs;
	}

	/**
	 * Gets the experiment type.
	 *
	 * @return the experiment type
	 */
	@Override
	public String getExperimentType() {
		return experimentType;
	}

	/**
	 * Returns the output managers that are currently active. If no agent is defined, then an empty iterable is returned
	 *
	 * @return the active output managers
	 */

	@Override
	public Iterable<IOutputManager> getActiveOutputManagers() {
		if (agent == null) return Collections.EMPTY_LIST;
		return Iterables.concat(agent.getAllSimulationOutputs(), Arrays.asList(experimentOutputs));

	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@Override
	public ExperimentDescription getDescription() {
		return (ExperimentDescription) super.getDescription();
	}

	/**
	 * Should be benchmarked.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean shouldBeBenchmarked() {
		return benchmarkable;
	}

}