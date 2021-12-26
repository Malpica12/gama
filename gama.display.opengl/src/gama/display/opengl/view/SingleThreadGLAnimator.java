/*******************************************************************************************************
 *
 * SingleThreadGLAnimator.java, in gama.display.opengl, is part of the source code of the GAMA modeling and simulation
 * platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.display.opengl.view;

import java.io.PrintStream;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.jogamp.opengl.GLAnimatorControl;
import com.jogamp.opengl.GLAutoDrawable;

import gama.common.preferences.GamaPreferences;
import gama.core.dev.utils.DEBUG;

// TODO: Auto-generated Javadoc
/**
 * Simple Animator (with target FPS).
 *
 * @author AqD (aqd@5star.com.tw)
 */
public class SingleThreadGLAnimator implements Runnable, GLAnimatorControl, GLAnimatorControl.UncaughtExceptionHandler {

	static {
		DEBUG.OFF();
	}

	/** The cap FPS. */
	protected boolean capFPS = GamaPreferences.Displays.OPENGL_CAP_FPS.getValue();

	/** The target FPS. */
	protected int targetFPS = GamaPreferences.Displays.OPENGL_FPS.getValue();

	/** The animator thread. */
	protected final Thread animatorThread;

	/** The drawable. */
	protected final GLAutoDrawable drawable;

	/** The stop requested. */
	protected volatile boolean stopRequested = false;

	/** The pause requested. */
	protected volatile boolean pauseRequested = false;

	/** The animating. */
	protected volatile boolean animating = false;

	/** The pause. */
	Semaphore pause = new Semaphore(0);

	/** The fps update frames interval. */
	private int fpsUpdateFramesInterval = 50;

	/** The fps total duration. */
	private long fpsStartTime, fpsLastUpdateTime, fpsLastPeriod, fpsTotalDuration;

	/** The fps total frames. */
	private int fpsTotalFrames;

	/** The fps total. */
	private float fpsLast, fpsTotal;

	/**
	 * Instantiates a new single thread GL animator.
	 *
	 * @param drawable
	 *            the drawable
	 */
	public SingleThreadGLAnimator(final GLAutoDrawable drawable) {
		GamaPreferences.Displays.OPENGL_FPS.onChange(newValue -> targetFPS = newValue);
		this.drawable = drawable;
		drawable.setAnimator(this);
		this.animatorThread = new Thread(this, "Animator thread");
	}

	/**
	 * Sets the update FPS frames.
	 *
	 * @param frames the frames
	 * @param out the out
	 */
	@Override
	public void setUpdateFPSFrames(final int frames, final PrintStream out) {
		fpsUpdateFramesInterval = frames;
	}

	/**
	 * Reset FPS counter.
	 */
	@Override
	public void resetFPSCounter() {
		fpsStartTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime()); // overwrite startTime to real init one
		fpsLastUpdateTime = fpsStartTime;
		fpsLastPeriod = 0;
		fpsTotalFrames = 0;
		fpsLast = 0f;
		fpsTotal = 0f;
		fpsLastPeriod = 0;
		fpsTotalDuration = 0;
	}

	/**
	 * Gets the update FPS frames.
	 *
	 * @return the update FPS frames
	 */
	@Override
	public int getUpdateFPSFrames() { return fpsUpdateFramesInterval; }

	/**
	 * Gets the FPS start time.
	 *
	 * @return the FPS start time
	 */
	@Override
	public long getFPSStartTime() { return fpsStartTime; }

	/**
	 * Gets the last FPS update time.
	 *
	 * @return the last FPS update time
	 */
	@Override
	public long getLastFPSUpdateTime() { return fpsLastUpdateTime; }

	/**
	 * Gets the last FPS period.
	 *
	 * @return the last FPS period
	 */
	@Override
	public long getLastFPSPeriod() { return fpsLastPeriod; }

	/**
	 * Gets the last FPS.
	 *
	 * @return the last FPS
	 */
	@Override
	public float getLastFPS() { return fpsLast; }

	/**
	 * Gets the total FPS frames.
	 *
	 * @return the total FPS frames
	 */
	@Override
	public int getTotalFPSFrames() { return fpsTotalFrames; }

	/**
	 * Gets the total FPS duration.
	 *
	 * @return the total FPS duration
	 */
	@Override
	public long getTotalFPSDuration() { return fpsTotalDuration; }

	/**
	 * Gets the total FPS.
	 *
	 * @return the total FPS
	 */
	@Override
	public float getTotalFPS() { return fpsTotal; }

	/**
	 * Checks if is started.
	 *
	 * @return true, if is started
	 */
	@Override
	public boolean isStarted() { return this.animatorThread.isAlive(); }

	/**
	 * Checks if is animating.
	 *
	 * @return true, if is animating
	 */
	@Override
	public boolean isAnimating() { return this.animating && !pauseRequested; }

	/**
	 * Checks if is paused.
	 *
	 * @return true, if is paused
	 */
	@Override
	public boolean isPaused() { return isStarted() && pauseRequested; }

	/**
	 * Gets the thread.
	 *
	 * @return the thread
	 */
	@Override
	public Thread getThread() { return this.animatorThread; }

	/**
	 * Start.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean start() {
		this.stopRequested = false;
		this.pauseRequested = false;
		this.animatorThread.start();
		fpsStartTime = System.currentTimeMillis();
		return true;
	}

	/**
	 * Stop.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean stop() {
		this.stopRequested = true;
		try {
			pause.release();
			this.animatorThread.join();
		} catch (final InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.stopRequested = false;
		}
		return true;
	}

	/**
	 * Pause.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean pause() {
		pauseRequested = true;
		return true;
	}

	/**
	 * Resume.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean resume() {
		pause.release();
		return true;
	}

	/**
	 * Adds the.
	 *
	 * @param drawable the drawable
	 */
	@Override
	public void add(final GLAutoDrawable drawable) {}

	/**
	 * Removes the.
	 *
	 * @param drawable the drawable
	 */
	@Override
	public void remove(final GLAutoDrawable drawable) {}

	/**
	 * Increases total frame count and updates values if feature is enabled and update interval is reached.<br>
	 *
	 * Shall be called by actual FPSCounter implementing renderer, after display a new frame.
	 *
	 */
	public final void tickFPS() {
		fpsTotalFrames++;
		if (fpsUpdateFramesInterval > 0 && fpsTotalFrames % fpsUpdateFramesInterval == 0) {
			final long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
			fpsLastPeriod = now - fpsLastUpdateTime;
			fpsLastPeriod = Math.max(fpsLastPeriod, 1); // div 0
			fpsLast = fpsUpdateFramesInterval * 1000f / fpsLastPeriod;
			fpsTotalDuration = now - fpsStartTime;
			fpsTotalDuration = Math.max(fpsTotalDuration, 1); // div 0
			fpsTotal = fpsTotalFrames * 1000f / fpsTotalDuration;
			fpsLastUpdateTime = now;
			if (DEBUG.IS_ON()) {
				StringBuilder sb = new StringBuilder();
				String fpsLastS = String.valueOf(fpsLast);
				fpsLastS = fpsLastS.substring(0, fpsLastS.indexOf('.') + 2);
				String fpsTotalS = String.valueOf(fpsTotal);
				fpsTotalS = fpsTotalS.substring(0, fpsTotalS.indexOf('.') + 2);
				sb.append(fpsTotalDuration / 1000 + " s: " + fpsUpdateFramesInterval + " f / "
						+ fpsLastPeriod / fpsUpdateFramesInterval + " ms, " + fpsLastS + " fps, " + fpsLastPeriod
						+ " ms/f; " + "total: " + fpsTotalFrames + " f, " + fpsTotalS + " fps, "
						+ fpsTotalDuration / fpsTotalFrames + " ms/f");
				DEBUG.OUT(sb.toString());
			}
		}
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		while (!this.stopRequested) {
			if (pauseRequested) {
				try {
					pause.drainPermits();
					pause.acquire();
					pauseRequested = false;
				} catch (InterruptedException e1) {}
			}
			this.displayGL();

			if (capFPS) {
				final long frameDuration = 1000 / targetFPS;
				final long timeSleep = frameDuration - fpsLastPeriod;
				try {
					if (timeSleep >= 0) { Thread.sleep(timeSleep); }
				} catch (final InterruptedException e) {}
			}
			tickFPS();
		}

	}

	/**
	 * Display GL.
	 */
	protected void displayGL() {
		this.animating = true;
		try {
			if (drawable.isRealized()) { drawable.display(); }
		} catch (final RuntimeException ex) {
			DEBUG.ERR("Exception in OpenGL:" + ex.getMessage());
			ex.printStackTrace();
		} finally {
			this.animating = false;
		}
	}

	/**
	 * Gets the uncaught exception handler.
	 *
	 * @return the uncaught exception handler
	 */
	@Override
	public UncaughtExceptionHandler getUncaughtExceptionHandler() { return this; }

	/**
	 * Sets the uncaught exception handler.
	 *
	 * @param handler the new uncaught exception handler
	 */
	@Override
	public void setUncaughtExceptionHandler(final UncaughtExceptionHandler handler) {}

	/**
	 * Uncaught exception.
	 *
	 * @param animator the animator
	 * @param drawable the drawable
	 * @param cause the cause
	 */
	@Override
	public void uncaughtException(final GLAnimatorControl animator, final GLAutoDrawable drawable,
			final Throwable cause) {
		DEBUG.ERR("Uncaught exception in animator & drawable:");
		cause.printStackTrace();

	}
}