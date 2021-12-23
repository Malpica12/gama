/*******************************************************************************************************
 *
 * ExperimentScheduler.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.kernel.experiment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import gama.common.interfaces.IStepable;
import gama.core.dev.utils.DEBUG;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.runtime.concurrent.GamaExecutorService;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaMapFactory;

/**
 * The Class ExperimentScheduler.
 */
public class ExperimentScheduler implements Runnable {

	/** The alive. */
	public volatile boolean alive = true;
	// Flag indicating that the experiment is set to pause (it should be alive
	/** The paused. */
	// unless the application is shutting down)
	public volatile boolean paused = true;
	
	/** The to step. */
	/* The stepables that need to be stepped */
	private final Map<IStepable, IScope> toStep = GamaMapFactory.create();
	
	/** The to stop. */
	private volatile Set<IStepable> toStop = new HashSet<>();
	
	/** The execution thread. */
	private Thread executionThread;
	
	/** The lock. */
	volatile Semaphore lock = new Semaphore(1);
	
	/** The experiment. */
	final IExperimentPlan experiment;

	/**
	 * Instantiates a new experiment scheduler.
	 *
	 * @param experiment the experiment
	 */
	ExperimentScheduler(final IExperimentPlan experiment) {
		this.experiment = experiment;
		if (!experiment.isHeadless()) {
			executionThread = new Thread(null, this, "Front end scheduler");
			executionThread.setUncaughtExceptionHandler(GamaExecutorService.EXCEPTION_HANDLER);
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			startThread();
		} else {
			executionThread = null;
		}
	}

	/**
	 * Start thread.
	 */
	private void startThread() {
		if (executionThread == null) {
			step();
		} else if (!executionThread.isAlive()) {
			try {
				executionThread.start();
			} catch (final Throwable e) {
				e.printStackTrace();
				final GamaRuntimeException ee = GamaRuntimeException.create(e, experiment.getExperimentScope());
				ee.addContext("Error in front end scheduler. Reloading thread, but it would be safer to reload GAMA");
				DEBUG.LOG(ee.getMessage());
				executionThread = new Thread(null, this, "Front end scheduler");
				executionThread.start();
			}
		}
	}

	/**
	 * Step.
	 */
	public void step() {
		if (!experiment.isHeadless() && paused) {
			try {
				lock.acquire();
			} catch (final InterruptedException e) {
				alive = false;
				return;
			}
		}
		try {
			// synchronized (toStep) {
			toStep.forEach((stepable, scope) -> {
				if (!scope.step(stepable).passed()) { toStop.add(stepable); }
			});
			// }
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Clean.
	 */
	private void clean() {
		if (toStop.isEmpty()) return;
		synchronized (toStop) {
			for (final IStepable s : toStop) {
				final IScope scope = toStep.get(s);
				if (scope != null && !scope.interrupted()) { scope.setInterrupted(); }
				toStep.remove(s);
			}
			if (toStep.isEmpty()) { this.pause(); }
			toStop.clear();
		}
	}

	@Override
	public void run() {
		while (alive) {
			step();
			clean();
		}
	}

	// public void setUserHold(final boolean hold) {
	// on_user_hold = hold;
	// }

	/**
	 * Step by step.
	 */
	public void stepByStep() {
		paused = true;
		lock.release();
		startThread();
	}

	/**
	 * Step back.
	 */
	// TODO : c'est moche .....
	public void stepBack() {
		paused = true;
		// lock.release();
		experiment.getAgent().backward(experiment.getExperimentScope());// ?? scopes[0]);
	}

	/**
	 * Start.
	 */
	public void start() {
		paused = false;
		lock.release();
		startThread();
	}

	/**
	 * Pause.
	 */
	public void pause() {
		paused = true;
	}

	/**
	 * Schedule.
	 *
	 * @param stepable the stepable
	 * @param scope the scope
	 */
	public void schedule(final IStepable stepable, final IScope scope) {
		if (toStep.containsKey(stepable)) { toStep.remove(stepable); }
		toStep.put(stepable, scope);
		// We first init the stepable before it is scheduled
		// DEBUG.OUT("ExperimentScheduler.schedule " + stepable);
		try {
			if (!scope.init(stepable).passed()) { toStop.add(stepable); }
		} catch (final Throwable e) {
			if (scope != null && scope.interrupted()) {
				toStop.add(stepable);
			} else {
				if (!(e instanceof GamaRuntimeException)) {
					GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
				}
			}
		}

	}

	/**
	 * Wipe.
	 */
	public synchronized void wipe() {
		synchronized (toStop) {
			toStop.clear();
		}
		synchronized (toStep) {
			toStep.clear();
		}
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		alive = false;
		wipe();
		lock.release();
	}

}