/*******************************************************************************************************
 *
 * ExperimentController.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.kernel.experiment;

import java.util.concurrent.ArrayBlockingQueue;

import gama.core.dev.utils.DEBUG;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.runtime.ISimulationStateProvider;
import gama.runtime.concurrent.GamaExecutorService;
import gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class ExperimentController.
 */
public class ExperimentController implements Runnable, IExperimentController {

	/** The experiment. */
	private final IExperimentPlan experiment;
	
	/** The disposing. */
	private boolean disposing;
	
	/** The commands. */
	protected volatile ArrayBlockingQueue<Integer> commands;
	
	/** The command thread. */
	public volatile Thread commandThread;
	
	/** The running. */
	protected volatile boolean running = true;
	
	/** The scheduler. */
	private final ExperimentScheduler scheduler;

	/**
	 * Instantiates a new experiment controller.
	 *
	 * @param experiment the experiment
	 */
	public ExperimentController(final IExperimentPlan experiment) {
		this.scheduler = new ExperimentScheduler(experiment);
		commands = new ArrayBlockingQueue<>(10);
		this.experiment = experiment;
	}

	/**
	 * Launch command thread.
	 */
	private void launchCommandThread() {
		if (commandThread != null) return;
		if (experiment.isHeadless()) {
			commandThread = null;
		} else {
			commandThread = new Thread(this, "Front end controller");
			commandThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
			commandThread.start();
		}

	}

	@Override
	public boolean isDisposing() {
		return disposing;
	}

	@Override
	public IExperimentPlan getExperiment() {
		return experiment;
	}

	@Override
	public void run() {
		while (running) {
			try {
				final Integer i = commands.take();
				if (i == null) throw new InterruptedException("Internal error. Please retry");
				processUserCommand(i);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		commandThread = null;
	}

	/**
	 * Offer.
	 *
	 * @param command the command
	 */
	public void offer(final int command) {
		if (isDisposing()) return;
		if (commandThread == null || !commandThread.isAlive()) {
			processUserCommand(command);
		} else {
			commands.offer(command);
		}
	}

	/**
	 * Process user command.
	 *
	 * @param command the command
	 */
	protected void processUserCommand(final int command) {
		final IScope scope = experiment.getExperimentScope();
		switch (command) {
			case IExperimentController._OPEN:

				experiment.getExperimentScope().getGui().updateExperimentState(scope,
						ISimulationStateProvider.NOTREADY);
				try {
					launchCommandThread();
					// Needs to run in the controller thread
					if (commandThread == null) {
						experiment.open();
					} else {
						new Thread(() -> experiment.open()).start();

					}
				} catch (final Exception e) {
					DEBUG.ERR("Error when opening the experiment: " + e.getMessage());
					closeExperiment(e);
				}
				break;
			case IExperimentController._START:
				try {
					scheduler.start();
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} finally {
					experiment.getExperimentScope().getGui().updateExperimentState(scope,
							ISimulationStateProvider.RUNNING);
				}
				break;
			case IExperimentController._PAUSE:
				experiment.getExperimentScope().getGui().updateExperimentState(scope, ISimulationStateProvider.PAUSED);
				scheduler.pause();
				break;
			case IExperimentController._STEP:
				experiment.getExperimentScope().getGui().updateExperimentState(scope, ISimulationStateProvider.PAUSED);
				scheduler.stepByStep();
				break;
			case IExperimentController._BACK:
				experiment.getExperimentScope().getGui().updateExperimentState(scope, ISimulationStateProvider.PAUSED);
				scheduler.stepBack();
				break;
			case IExperimentController._RELOAD:
				experiment.getExperimentScope().getGui().updateExperimentState(scope,
						ISimulationStateProvider.NOTREADY);
				try {
					final boolean wasRunning = !scheduler.paused && !experiment.isAutorun();
					scheduler.pause();
					GAMA.getGui().getStatus(scope).waitStatus("Reloading...");
					experiment.reload();
					if (wasRunning) {
						processUserCommand(IExperimentController._START);
					} else {
						experiment.getExperimentScope().getGui().getStatus(scope).informStatus("Experiment reloaded");
					}
				} catch (final GamaRuntimeException e) {
					closeExperiment(e);
				} catch (final Throwable e) {
					closeExperiment(GamaRuntimeException.create(e, experiment.getExperimentScope()));
				} finally {
					experiment.getExperimentScope().getGui().updateExperimentState(scope);
				}
				break;
		}
	}

	@Override
	public void userPause() {
		// TODO Should maybe be done directly (so as to pause immediately)
		offer(IExperimentController._PAUSE);
	}

	@Override
	public void directPause() {
		processUserCommand(IExperimentController._PAUSE);
	}

	@Override
	public void userStep() {
		if (experiment == null) return;
		offer(IExperimentController._STEP);
	}

	@Override
	public void stepBack() {
		if (experiment == null) return;
		offer(IExperimentController._BACK);
	}

	@Override
	public void userReload() {
		// TODO Should maybe be done directly (so as to reload immediately)
		if (experiment == null) return;
		// GAMA.getGui().openSimulationPerspective(null, null);
		offer(IExperimentController._RELOAD);
	}

	@Override
	public void directOpenExperiment() {
		processUserCommand(IExperimentController._OPEN);
	}

	@Override
	public void userStart() {
		offer(IExperimentController._START);
	}

	@Override
	public void userOpen() {
		offer(_OPEN);
	}

	@Override
	public void dispose() {
		if (experiment != null) {
			// DEBUG.OUT("Contoller.dipose BEGIN");
			final IScope scope = experiment.getExperimentScope();
			try {
				scheduler.pause();
				experiment.getExperimentScope().getGui().updateExperimentState(scope,
						ISimulationStateProvider.NOTREADY);
				experiment.getExperimentScope().getGui().closeDialogs(scope);
				// Dec 2015 This method is normally now called from
				// ExperimentPlan.dispose()
				// experiment.dispose();
				// experiment = null;
			} finally {
				running = false;
				scheduler.dispose();
				experiment.getExperimentScope().getGui().updateExperimentState(scope, ISimulationStateProvider.NONE);
				if (commandThread != null && commandThread.isAlive()) { commands.offer(-1); }
				// DEBUG.OUT("Contoller.dipose END");
			}
		}
	}

	@Override
	public void startPause() {
		if (experiment == null) {} else if (scheduler.paused) {
			userStart();
		} else {
			userPause();
		}
	}

	@Override
	public void close() {
		closeExperiment(null);
	}

	/**
	 * Close experiment.
	 *
	 * @param e the e
	 */
	public void closeExperiment(final Exception e) {
		disposing = true;
		// DEBUG.LOG("CloseExperiment : disposing = true");
		if (e != null) { GAMA.getGui().getStatus(getExperiment().getExperimentScope()).errorStatus(e.getMessage()); }

		experiment.dispose(); // will call own dispose() later
	}

	@Override
	public ExperimentScheduler getScheduler() {
		return scheduler;
	}

}
