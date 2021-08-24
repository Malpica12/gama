package gama.display.opengl.renderer.helpers;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.swt.GLCanvas;

import gama.display.opengl.OpenGL;
import gama.display.opengl.renderer.IOpenGLRenderer;
import gama.display.opengl.view.SWTOpenGLDisplaySurface;
import gama.outputs.LayeredDisplayData;

public abstract class AbstractRendererHelper {

	public interface Pass extends AutoCloseable {

		@Override
		void close();

	}

	private final IOpenGLRenderer renderer;

	public AbstractRendererHelper(final IOpenGLRenderer renderer) {
		this.renderer = renderer;
	}

	public IOpenGLRenderer getRenderer() {
		return renderer;
	}

	protected LayeredDisplayData getData() {
		return renderer.getData();
	}

	protected GL2 getGL() {
		return renderer.getOpenGLHelper().getGL();
	}

	protected OpenGL getOpenGL() {
		return renderer.getOpenGLHelper();
	}

	protected GLCanvas getCanvas() {
		return renderer.getCanvas();
	}

	protected SWTOpenGLDisplaySurface getSurface() {
		return renderer.getSurface();
	}

	public double getMaxEnvDim() {
		return renderer.getMaxEnvDim();
	}

	public double getZNear() {
		return renderer.getData().getzNear();
	}

	public double getZFar() {
		return renderer.getData().getzFar();
	}

	public abstract void initialize();

}