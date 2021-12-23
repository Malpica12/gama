/*******************************************************************************************************
 *
 * GAMA.java, in gama.core.kernel, is part of the source code of the GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.runtime;

import static gama.core.dev.utils.DEBUG.PAD;
import static gama.core.dev.utils.DEBUG.TIMER_WITH_EXCEPTIONS;
import static java.lang.Thread.currentThread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import gama.common.interfaces.IBenchmarkable;
import gama.common.preferences.GamaPreferences;
import gama.common.ui.IGui;
import gama.common.ui.IStartupProgress;
import gama.common.util.PoolUtils;
import gama.common.util.RandomUtils;
import gama.core.dev.utils.DEBUG;
import gama.core.dev.utils.DEBUG.RunnableWithException;
import gama.kernel.experiment.ExperimentAgent;
import gama.kernel.experiment.ExperimentPlan;
import gama.kernel.experiment.IExperimentController;
import gama.kernel.experiment.IExperimentPlan;
import gama.kernel.experiment.IParameter;
import gama.kernel.experiment.ParametersSet;
import gama.kernel.model.IModel;
import gama.kernel.root.PlatformAgent;
import gama.kernel.simulation.SimulationAgent;
import gama.runtime.benchmark.Benchmark;
import gama.runtime.benchmark.StopWatch;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.runtime.exceptions.GamaRuntimeException.GamaRuntimeFileException;
import gaml.compilation.ISymbol;
import gaml.compilation.kernel.GamaMetaModel;

/**
 * Written by drogoul Modified on 23 nov. 2009
 *
 * In GUI Mode, for the moment, only one controller allowed at a time (controllers[0])
 *
 * @todo Description
 */
public class GAMA {

	static {
		DEBUG.ON();
	}

	/** The Constant VERSION_NUMBER. */
	public final static String VERSION_NUMBER = "1.8.2";

	/** The Constant VERSION. */
	public final static String VERSION = "GAMA " + VERSION_NUMBER;

	/** The Constant _WARNINGS. */
	public static final String _WARNINGS = "warnings";

	/** The agent. */
	private static volatile PlatformAgent agent;

	/** The benchmark agent. */
	private static Benchmark benchmarkAgent;

	/** The is in headless mode. */
	private static boolean isInHeadlessMode;

	/** The regular gui. */
	private static IGui regularGui;

	/** The headless gui. */
	private static IGui headlessGui = new HeadlessListener();

	/** The Constant controllers. */
	// hqnghi: add several controllers to have multi-thread experiments
	private final static List<IExperimentController> controllers = new CopyOnWriteArrayList<>();

	/**
	 * Gets the controllers.
	 *
	 * @return the controllers
	 */
	public static List<IExperimentController> getControllers() {
		return controllers;
	}

	/**
	 * Gets the frontmost controller.
	 *
	 * @return the frontmost controller
	 */
	public static IExperimentController getFrontmostController() {
		return controllers.isEmpty() ? null : controllers.get(0);
	}

	/**
	 * New control architecture.
	 *
	 * @param id
	 *            the id
	 * @param model
	 *            the model
	 */

	/**
	 * Create a GUI experiment that replaces the current one (if any)
	 *
	 * @param id
	 * @param model
	 */
	public static void runGuiExperiment(final String id, final IModel model) {
		// DEBUG.OUT("Launching experiment " + id + " of model " + model.getFilePath());
		final IExperimentPlan newExperiment = model.getExperiment(id);
		if (newExperiment == null) // DEBUG.OUT("No experiment " + id + " in model " + model.getFilePath());
			return;
		IExperimentController controller = getFrontmostController();
		if (controller != null) {
			final IExperimentPlan existingExperiment = controller.getExperiment();
			if (existingExperiment != null) {
				controller.getScheduler().pause();
				if (!getGui().confirmClose(existingExperiment)) return;
			}
		}
		controller = newExperiment.getController();
		if (controllers.size() > 0) { closeAllExperiments(false, false); }

		if (getGui().openSimulationPerspective(model, id)) {
			controllers.add(controller);
			startBenchmark(newExperiment);
			controller.userOpen();
		} else {
			// we are unable to launch the perspective.
			DEBUG.ERR("Unable to launch simulation perspective for experiment " + id + " of model "
					+ model.getFilePath());
		}
	}

	// /**
	// * Add a sub-experiment to the current GUI experiment
	// *
	// * @param id
	// * @param model
	// */
	// public static void addGuiExperiment(final IExperimentPlan experiment) {
	//
	// }

	/**
	 * Open experiment from gaml file.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public static void openExperimentFromGamlFile(final IExperimentPlan experiment) {
		experiment.getController().directOpenExperiment();
	}

	/**
	 * Add an experiment.
	 *
	 * @param model
	 *            the model
	 * @param expName
	 *            the exp name
	 * @param params
	 *            the params
	 * @param seed
	 *            the seed
	 * @return the i experiment plan
	 */
	public static synchronized IExperimentPlan addHeadlessExperiment(final IModel model, final String expName,
			final ParametersSet params, final Double seed) {

		final ExperimentPlan currentExperiment = (ExperimentPlan) model.getExperiment(expName);

		if (currentExperiment == null) throw GamaRuntimeException
				.error("Experiment " + expName + " does not exist. Please check its name.", getRuntimeScope());
		currentExperiment.setHeadless(true);
		for (final Map.Entry<String, Object> entry : params.entrySet()) {

			final IParameter.Batch v = currentExperiment.getParameterByTitle(entry.getKey());
			if (v != null) {
				currentExperiment.setParameterValueByTitle(currentExperiment.getExperimentScope(), entry.getKey(),
						entry.getValue());
			} else {
				currentExperiment.setParameterValue(currentExperiment.getExperimentScope(), entry.getKey(),
						entry.getValue());
			}

		}
		currentExperiment.open(seed);
		controllers.add(currentExperiment.getController());
		return currentExperiment;

	}

	// public static void closeFrontmostExperiment() {
	// final IExperimentController controller = getFrontmostController();
	// if (controller == null || controller.getExperiment() == null) { return; }
	// controller.close();
	// controllers.remove(controller);
	// }

	/**
	 * Close experiment.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public static void closeExperiment(final IExperimentPlan experiment) {
		if (experiment == null) return;
		closeController(experiment.getController());
	}

	/**
	 * Close all experiments.
	 *
	 * @param andOpenModelingPerspective
	 *            the and open modeling perspective
	 * @param immediately
	 *            the immediately
	 */
	public static void closeAllExperiments(final boolean andOpenModelingPerspective, final boolean immediately) {
		for (final IExperimentController controller : new ArrayList<>(controllers)) {
			closeController(controller);
		}
		getGui().closeSimulationViews(null, andOpenModelingPerspective, immediately);
		PoolUtils.WriteStats();

	}

	/**
	 * Close controller.
	 *
	 * @param controller
	 *            the controller
	 */
	private static void closeController(final IExperimentController controller) {
		if (controller == null) return;
		stopBenchmark(controller.getExperiment());
		controller.close();
		controllers.remove(controller);
	}

	/**
	 * Access to experiments and their components.
	 *
	 * @return the simulation
	 */

	public static SimulationAgent getSimulation() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) return null;
		return controller.getExperiment().getCurrentSimulation();
	}

	/**
	 * Gets the experiment.
	 *
	 * @return the experiment
	 */
	public static IExperimentPlan getExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null) return null;
		return controller.getExperiment();
	}

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public static IModel getModel() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null)
			return GamaMetaModel.INSTANCE.getAbstractModelSpecies();
		return controller.getExperiment().getModel();
	}

	/**
	 * Exception and life-cycle related utilities.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @param shouldStopSimulation
	 *            the should stop simulation
	 * @return true, if successful
	 */

	public static boolean reportError(final IScope scope, final GamaRuntimeException g,
			final boolean shouldStopSimulation) {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null || controller.isDisposing()
				|| controller.getExperiment().getAgent() == null)
			return false;
		DEBUG.LOG("report error : " + g.getMessage());
		// Returns whether or not to continue
		if (!(g instanceof GamaRuntimeFileException) && scope != null && !scope.reportErrors()) {
			// AD: we still throw exceptions related to files (Issue #1281)
			g.printStackTrace();
			return true;
		}
		if (scope != null && scope.getGui() != null) { scope.getGui().runtimeError(scope, g); }
		g.setReported();

		final boolean isError = !g.isWarning() || controller.getExperiment().getAgent().getWarningsAsErrors();
		final boolean shouldStop =
				isError && shouldStopSimulation && GamaPreferences.Runtime.CORE_REVEAL_AND_STOP.getValue();
		return !shouldStop;
	}

	/**
	 * Report and throw if needed.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 * @param shouldStopSimulation
	 *            the should stop simulation
	 */
	public static void reportAndThrowIfNeeded(final IScope scope, final GamaRuntimeException g,
			final boolean shouldStopSimulation) {

		if (getExperiment() == null && !(g instanceof GamaRuntimeFileException) && scope != null
				&& !scope.reportErrors()) {
			// AD: we still throw exceptions related to files (Issue #1281)
			g.printStackTrace();
			return;
		}

		DEBUG.LOG("reportAndThrowIfNeeded : " + g.getMessage());
		if (scope != null && scope.getAgent() != null) {
			final String name = scope.getAgent().getName();
			if (!g.getAgentsNames().contains(name)) { g.addAgent(name); }
		}
		if (scope != null) { scope.setCurrentError(g); }
		final boolean isInTryMode = scope != null && scope.isInTryMode();
		if (isInTryMode) throw g;
		final boolean shouldStop = !reportError(scope, g, shouldStopSimulation);
		if (shouldStop) {
			if (isInHeadLessMode()) throw g;
			pauseFrontmostExperiment();
			throw g;
		}
	}

	/**
	 * Start pause frontmost experiment.
	 */
	public static void startPauseFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			controller.startPause();
		}
	}

	/**
	 * Step frontmost experiment.
	 */
	public static void stepFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			controller.userStep();
		}
	}

	/**
	 * Step back frontmost experiment.
	 */
	public static void stepBackFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			controller.stepBack();
		}
	}

	/**
	 * Pause frontmost experiment.
	 */
	public static void pauseFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			// Dont block display threads (see #
			if (getGui().isInDisplayThread()) {
				controller.userPause();
			} else {
				controller.directPause();
			}
		}
	}

	/**
	 * Resume frontmost experiment.
	 */
	public static void resumeFrontmostExperiment() {
		for (final IExperimentController controller : controllers) {
			controller.userStart();
		}
	}

	/**
	 * Reload frontmost experiment.
	 */
	public static void reloadFrontmostExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller != null) { controller.userReload(); }
	}

	/**
	 * Start frontmost experiment.
	 */
	public static void startFrontmostExperiment() {
		final IExperimentController controller = getFrontmostController();
		if (controller != null) { controller.userStart(); }
	}

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */
	public static boolean isPaused() {
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) return true;
		return controller.getScheduler().paused;

	}

	/**
	 * Scoping utilities.
	 *
	 * @param scope
	 *            the scope
	 */

	public static void releaseScope(final IScope scope) {
		if (scope != null) { scope.clear(); }
	}

	/**
	 * Copy runtime scope.
	 *
	 * @param additionalName
	 *            the additional name
	 * @return the i scope
	 */
	private static IScope copyRuntimeScope(final String additionalName) {
		final IScope scope = getRuntimeScope();
		if (scope != null) return scope.copy(additionalName);
		return null;
	}

	/**
	 * Gets the runtime scope.
	 *
	 * @return the runtime scope
	 */
	public static IScope getRuntimeScope() {
		// // If GAMA has not yet been loaded, we return null
		// if (!GamaBundleLoader.LOADED) return null;
		final IExperimentController controller = getFrontmostController();
		if (controller == null || controller.getExperiment() == null) return getPlatformAgent().getScope();
		final ExperimentAgent a = controller.getExperiment().getAgent();
		if (a == null || a.dead()) return controller.getExperiment().getExperimentScope();
		final SimulationAgent s = a.getSimulation();
		if (s == null || s.dead()) return a.getScope();
		return s.getScope();
	}

	/**
	 * Gets the current random.
	 *
	 * @return the current random
	 */
	public static RandomUtils getCurrentRandom() {
		final IScope scope = getRuntimeScope();
		if (scope == null) return new RandomUtils();
		return scope.getRandom();
	}

	/**
	 * The Interface InScope.
	 *
	 * @param <T>
	 *            the generic type
	 */
	public interface InScope<T> {

		/**
		 * The Class Void.
		 */
		public abstract static class Void implements InScope<Object> {

			@Override
			public Object run(final IScope scope) {
				process(scope);
				return null;
			}

			/**
			 * Process.
			 *
			 * @param scope
			 *            the scope
			 */
			public abstract void process(IScope scope);
		}

		/**
		 * Run.
		 *
		 * @param scope
		 *            the scope
		 * @return the t
		 */
		T run(IScope scope);
	}

	/**
	 * Run.
	 *
	 * @param <T>
	 *            the generic type
	 * @param r
	 *            the r
	 * @return the t
	 */
	public static <T> T run(final InScope<T> r) {
		try (IScope scope = copyRuntimeScope(" in temporary scope block")) {
			return r.run(scope);
		}
	}

	/**
	 * Allows to update all outputs after running an experiment.
	 *
	 * @param r
	 *            the r
	 */
	public static final void runAndUpdateAll(final Runnable r) {
		r.run();
		// SimulationAgent sim = getSimulation();
		// if(sim.isPaused(sim.getScope()))
		getExperiment().refreshAllOutputs();
	}

	/**
	 * Gets the gui.
	 *
	 * @return the gui
	 */
	public static IGui getGui() {
		// either a headless listener or a fully configured gui
		if (isInHeadlessMode || regularGui == null) return headlessGui;
		return regularGui;
	}

	/**
	 * Gets the headless gui.
	 *
	 * @return the headless gui
	 */
	public static IGui getHeadlessGui() {
		return headlessGui;
	}

	/**
	 * Gets the regular gui.
	 *
	 * @return the regular gui
	 */
	public static IGui getRegularGui() {
		return regularGui;
	}

	/**
	 * Sets the headless gui.
	 *
	 * @param g
	 *            the new headless gui
	 */
	public static void setHeadlessGui(final IGui g) {
		headlessGui = g;
	}

	/**
	 * Sets the regular gui.
	 *
	 * @param g
	 *            the new regular gui
	 */
	public static void setRegularGui(final IGui g) {
		regularGui = g;
	}

	/**
	 * Checks if is in head less mode.
	 *
	 * @return true, if is in head less mode
	 */
	public static boolean isInHeadLessMode() {
		return isInHeadlessMode;
	}

	/**
	 * Sets the head less mode.
	 *
	 * @return the i gui
	 */
	public static IGui setHeadLessMode() {
		isInHeadlessMode = true;
		final IGui gui = new HeadlessListener();
		setHeadlessGui(gui);
		return gui;
	}

	/**
	 * Relaunch frontmost experiment.
	 */
	public static void relaunchFrontmostExperiment() {
		// Needs to be done: recompile the model and runs the previous
		// experiment if any

	}

	/**
	 * Access to the one and only 'gama' agent.
	 *
	 * @return the platform agent, or creates it if it doesn't exist
	 */
	public static PlatformAgent getPlatformAgent() {
		if (agent == null) { agent = new PlatformAgent(); }
		return agent;
	}

	/**
	 * Benchmarking utilities.
	 *
	 * @param scope
	 *            the scope
	 * @param symbol
	 *            the symbol
	 * @return the stop watch
	 */
	public static StopWatch benchmark(final IScope scope, final Object symbol) {
		if (benchmarkAgent == null || symbol == null || scope == null) return StopWatch.NULL;
		if (symbol instanceof IBenchmarkable) return benchmarkAgent.record(scope, (IBenchmarkable) symbol);
		if (symbol instanceof ISymbol) return benchmarkAgent.record(scope, ((ISymbol) symbol).getDescription());
		return StopWatch.NULL;
	}

	/**
	 * Start benchmark.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public static void startBenchmark(final IExperimentPlan experiment) {
		if (experiment.shouldBeBenchmarked()) { benchmarkAgent = new Benchmark(experiment); }
	}

	/**
	 * Stop benchmark.
	 *
	 * @param experiment
	 *            the experiment
	 */
	public static void stopBenchmark(final IExperimentPlan experiment) {
		if (benchmarkAgent != null) { benchmarkAgent.saveAndDispose(experiment); }
		benchmarkAgent = null;
	}

	// Startup

	/** The monitor. */
	private static IStartupProgress monitor;

	/**
	 * Sets the startup monitor.
	 *
	 * @param splash
	 *            the new startup monitor
	 */
	public static void setStartupProgressListener(final IStartupProgress splash) {
		monitor = splash;
	}

	/**
	 * Initialize at startup.
	 *
	 * @param <T>
	 *            the generic type
	 * @param title
	 *            the title
	 * @param runnable
	 *            the runnable
	 * @throws T
	 *             the t
	 */
	public static <T extends Throwable> void initializeAtStartup(final String title,
			final RunnableWithException<T> runnable) throws T {
		TIMER_WITH_EXCEPTIONS(
				PAD("> GAMA: " + title, 45, ' ') + PAD("[" + currentThread().getName() + "]", 20, '_') + " in ",
				runnable);
		if (monitor != null) { monitor.add(title); }

	}

}