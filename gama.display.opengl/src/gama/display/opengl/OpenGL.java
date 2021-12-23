/*******************************************************************************************************
 *
 * OpenGL.java, in gama.display.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.display.opengl;

import static com.jogamp.opengl.glu.GLU.gluTessBeginContour;
import static com.jogamp.opengl.glu.GLU.gluTessBeginPolygon;
import static com.jogamp.opengl.glu.GLU.gluTessEndContour;
import static com.jogamp.opengl.glu.GLU.gluTessEndPolygon;
import static gama.common.geometry.GeometryUtils.applyToInnerGeometries;
import static gama.common.geometry.GeometryUtils.getContourCoordinates;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

import gama.common.geometry.Envelope3D;
import gama.common.geometry.ICoordinates;
import gama.common.geometry.Rotation3D;
import gama.common.geometry.Scaling3D;
import gama.common.geometry.UnboundedCoordinateSequence;
import gama.common.geometry.ICoordinates.VertexVisitor;
import gama.common.preferences.GamaPreferences;
import gama.core.dev.utils.DEBUG;
import gama.display.opengl.renderer.IOpenGLRenderer;
import gama.display.opengl.renderer.caches.GeometryCache;
import gama.display.opengl.renderer.caches.ITextureCache;
import gama.display.opengl.renderer.caches.TextureCache2;
import gama.display.opengl.renderer.caches.GeometryCache.BuiltInGeometry;
import gama.display.opengl.renderer.helpers.AbstractRendererHelper;
import gama.display.opengl.renderer.helpers.KeystoneHelper;
import gama.display.opengl.renderer.helpers.PickingHelper;
import gama.display.opengl.scene.AbstractObject;
import gama.display.opengl.scene.ObjectDrawer;
import gama.display.opengl.scene.geometry.GeometryDrawer;
import gama.display.opengl.scene.mesh.MeshDrawer;
import gama.display.opengl.scene.resources.ResourceDrawer;
import gama.display.opengl.scene.text.TextDrawer;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.IShape;
import gama.ui.base.utils.PlatformHelper;
import gama.util.file.GamaGeometryFile;
import gama.util.file.GamaImageFile;
import gaml.operators.Maths;
import gaml.statements.draw.DrawingAttributes;
import jogamp.opengl.glu.tessellator.GLUtessellatorImpl;

/**
 * A class that represents an intermediate state between the rendering and the opengl state. It captures all the
 * commands sent to opengl to either record them and ouput VBOs or send them immediately (in immediate mode). Only the
 * immediate mode is implemented now. This class also manages the different caches (textures, geometries, envelopes,
 * text renderers)
 *
 * @author drogoul
 *
 */
public class OpenGL extends AbstractRendererHelper implements ITesselator {

	static {
		DEBUG.ON();
		GamaPreferences.Displays.DRAW_ROTATE_HELPER.onChange(v -> SHOULD_DRAW_ROTATION_SPHERE = v);
	}

	/** The should draw rotation sphere. */
	private static boolean SHOULD_DRAW_ROTATION_SPHERE = GamaPreferences.Displays.DRAW_ROTATE_HELPER.getValue();

	/** The Constant NO_TEXTURE. */
	public static final int NO_TEXTURE = Integer.MAX_VALUE;
	
	/** The Constant NO_ANISOTROPY. */
	public static final float NO_ANISOTROPY = -1f;

	/** The geometry drawer. */
	// Special drawers
	private final GeometryDrawer geometryDrawer;
	
	/** The string drawer. */
	private final TextDrawer stringDrawer;
	
	/** The field drawer. */
	private final MeshDrawer fieldDrawer;
	
	/** The resource drawer. */
	private final ResourceDrawer resourceDrawer;

	/** The viewport. */
	// Matrices of the display
	final int[] viewport = new int[4];
	
	/** The mvmatrix. */
	final double mvmatrix[] = new double[16];
	
	/** The projmatrix. */
	final double projmatrix[] = new double[16];

	/** The gl. */
	// The real openGL context
	private GL2 gl;
	
	/** The glut. */
	private final GLUT glut;
	
	/** The glu. */
	private final GLU glu;
	
	/** The view height. */
	private int viewWidth, viewHeight;
	
	/** The picking state. */
	private final PickingHelper pickingState;

	/** The texture cache. */
	// Textures
	private final ITextureCache textureCache = new TextureCache2(this);
	
	/** The texture envelope. */
	private final Envelope3D textureEnvelope = Envelope3D.create();
	
	/** The current texture rotation. */
	private final Rotation3D currentTextureRotation = Rotation3D.identity();
	
	/** The textured. */
	private boolean textured;
	
	/** The primary texture. */
	private int primaryTexture = NO_TEXTURE;
	
	/** The alternate texture. */
	private int alternateTexture = NO_TEXTURE;
	
	/** The anisotropic level. */
	private float anisotropicLevel = NO_ANISOTROPY;

	/** The current color. */
	// Colors
	private Color currentColor;
	
	/** The current object alpha. */
	private double currentObjectAlpha = 1d;
	
	/** The lighted. */
	private boolean lighted;

	/** The in raster text mode. */
	// Text
	private boolean inRasterTextMode;
	// protected final FontCache fontCache = new FontCache();

	/** The geometry cache. */
	// Geometries
	protected final GeometryCache geometryCache;
	
	/** The display is wireframe. */
	protected boolean displayIsWireframe;
	
	/** The object is wireframe. */
	protected boolean objectIsWireframe;
	
	/** The tobj. */
	final GLUtessellatorImpl tobj = (GLUtessellatorImpl) GLU.gluNewTess();
	
	/** The gl tesselator drawer. */
	final VertexVisitor glTesselatorDrawer;

	/** The ratios. */
	// World
	final GamaPoint ratios = new GamaPoint();
	
	/** The roi envelope. */
	Envelope3D roiEnvelope;
	
	/** The rotation mode. */
	private boolean rotationMode;
	
	/** The is ROI sticky. */
	private boolean isROISticky;

	/** The current normal. */
	// Working objects
	final GamaPoint currentNormal = new GamaPoint();
	
	/** The texture coords. */
	// final GamaPoint currentScale = new GamaPoint(1, 1, 1);
	final GamaPoint textureCoords = new GamaPoint();
	
	/** The working vertices. */
	final UnboundedCoordinateSequence workingVertices = new UnboundedCoordinateSequence();
	
	/** The saved Z translation. */
	private double currentZIncrement, currentZTranslation, savedZTranslation;
	
	/** The Z translation suspended. */
	private volatile boolean ZTranslationSuspended;
	
	/** The end scene. */
	// private final boolean useJTSTriangulation = !GamaPreferences.Displays.OPENGL_TRIANGULATOR.getValue();
	private final Pass endScene = this::endScene;

	/**
	 * Instantiates a new open GL.
	 *
	 * @param renderer the renderer
	 */
	public OpenGL(final IOpenGLRenderer renderer) {
		super(renderer);
		glut = new GLUT();
		glu = new GLU();
		pickingState = renderer.getPickingHelper();
		geometryCache = new GeometryCache(renderer);
		glTesselatorDrawer = (final double[] ordinates) -> {
			tobj.gluTessVertex(ordinates, 0, ordinates);
		};
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, this);
		GLU.gluTessProperty(tobj, GLU.GLU_TESS_TOLERANCE, 0.1);
		geometryDrawer = new GeometryDrawer(this);
		fieldDrawer = new MeshDrawer(this);
		stringDrawer = new TextDrawer(this);
		resourceDrawer = new ResourceDrawer(this);
	}

	/**
	 * Gets the drawer for.
	 *
	 * @param type the type
	 * @return the drawer for
	 */
	public ObjectDrawer<? extends AbstractObject<?, ?>> getDrawerFor(final AbstractObject.DrawerType type) {
		switch (type) {
			case STRING:
				return stringDrawer;
			case GEOMETRY:
				return geometryDrawer;
			case MESH:
				return fieldDrawer;
			case RESOURCE:
				return resourceDrawer;
		}
		return null;
	}

	/**
	 * Gets the geometry drawer.
	 *
	 * @return the geometry drawer
	 */
	public GeometryDrawer getGeometryDrawer() {
		return geometryDrawer;
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		stringDrawer.dispose();
		fieldDrawer.dispose();
		resourceDrawer.dispose();
		geometryDrawer.dispose();
		geometryCache.dispose();
		textureCache.dispose();
		gl = null;

	}

	@Override
	public GL2 getGL() {
		return gl;
	}

	/**
	 * Sets the gl2.
	 *
	 * @param gl2 the new gl2
	 */
	public void setGL2(final GL2 gl2) {
		this.gl = gl2;
		textureCache.initialize();
		if (anisotropicLevel == NO_ANISOTROPY && gl2.isExtensionAvailable("GL_EXT_texture_filter_anisotropic")) {
			final FloatBuffer aniso = Buffers.newDirectFloatBuffer(1);
			gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, aniso);
			anisotropicLevel = aniso.get();
			DEBUG.OUT("Anisotropic level: " + anisotropicLevel);
		}

	}

	/**
	 * Gets the glut.
	 *
	 * @return the glut
	 */
	public GLUT getGlut() {
		return glut;
	}

	/**
	 * Reshapes the GL world to comply with a new view size and computes the resulting ratios between pixels and world
	 * coordinates.
	 *
	 * @param newGL            the (possibly new) GL2 context
	 * @param width            the width of the view (in pixels)
	 * @param height            the height of the view (in pixels)
	 */
	public void reshape(final GL2 newGL, final int width, final int height) {
		setGL2(newGL);
		// newGL.glViewport(0, 0, width, height);
		viewWidth = width;
		viewHeight = height;
		resetMatrix(GLMatrixFunc.GL_MODELVIEW);
		resetMatrix(GLMatrixFunc.GL_PROJECTION);
		updatePerspective(newGL);

		final double[] pixelSize = new double[4];
		glu.gluProject(getWorldWidth(), 0, 0, mvmatrix, 0, projmatrix, 0, viewport, 0, pixelSize, 0);
		final double initialEnvWidth = pixelSize[0];
		final double initialEnvHeight = pixelSize[1];
		final double envWidthInPixels = 2 * pixelSize[0] - width;
		final double envHeightInPixels = 2 * pixelSize[1] - height;
		final double windowWidthInModelUnits = getWorldWidth() * width / envWidthInPixels;
		final double windowHeightInModelUnits = getWorldHeight() * height / envHeightInPixels;
		final double xRatio = width / windowWidthInModelUnits / getData().getZoomLevel();
		final double yRatio = height / windowHeightInModelUnits / getData().getZoomLevel();
		if (DEBUG.IS_ON()) {
			debugSizes(width, height, initialEnvWidth, initialEnvHeight, envWidthInPixels, envHeightInPixels,
					getData().getZoomLevel(), xRatio, yRatio);
		}
		ratios.setLocation(xRatio, yRatio, 0d);
	}

	/**
	 * Debug sizes.
	 *
	 * @param width the width
	 * @param height the height
	 * @param initialEnvWidth the initial env width
	 * @param initialEnvHeight the initial env height
	 * @param envWidth the env width
	 * @param envHeight the env height
	 * @param zoomLevel the zoom level
	 * @param xRatio the x ratio
	 * @param yRatio the y ratio
	 */
	@SuppressWarnings ("restriction")
	private void debugSizes(final int width, final int height, final double initialEnvWidth,
			final double initialEnvHeight, final double envWidth, final double envHeight, final double zoomLevel,
			final double xRatio, final double yRatio) {

		DEBUG.SECTION("RESHAPING TO " + width + "x" + height);
		DEBUG.OUT("Camera zoom level ", 35, zoomLevel);
		DEBUG.OUT("Size of env in units ", 35, getWorldWidth() + " | " + getWorldHeight());
		DEBUG.OUT("Ratio width/height in units ", 35, getWorldWidth() / getWorldHeight());
		DEBUG.OUT("Initial Size of env in pixels ", 35, initialEnvWidth + " | " + initialEnvHeight);
		DEBUG.OUT("Size of env in pixels ", 35, envWidth + " | " + envHeight);
		DEBUG.OUT("Ratio width/height in pixels ", 35, envWidth / envHeight);
		DEBUG.OUT("Window pixels/env pixels ", 35, width / envWidth + " | " + height / envHeight);
		DEBUG.OUT("Current XRatio pixels/env in units ", 35, xRatio + " | " + yRatio);
		DEBUG.OUT("Device Zoom =  " + PlatformHelper.getDeviceZoom());
		DEBUG.OUT("AutoScale down = ", false);
		DEBUG.OUT(" " + PlatformHelper.autoScaleDown(width) + " " + PlatformHelper.autoScaleDown(height));
		// DEBUG.OUT("Client area of window:" + getRenderer().getCanvas().getClientArea());
	}

	/**
	 * Update perspective.
	 *
	 * @param gl the gl
	 */
	public void updatePerspective(final GL2 gl) {
		final double height = getViewHeight();
		final double aspect = getViewWidth() / (height == 0d ? 1d : height);
		final double maxDim = getMaxEnvDim();
		double zNear = getZNear();
		if (zNear < 0.0) { zNear = maxDim / 100d; }
		double zFar = getZFar();
		if (zFar < 0.0) { zFar = maxDim * 100d; }

		if (!getData().isOrtho()) {
			try {
				double fW, fH;
				final double fovY = getData().getCameralens();
				if (aspect > 1.0) {
					fH = Math.tan(fovY / 360 * Math.PI) * zNear;
					fW = fH * aspect;
				} else {
					fW = Math.tan(fovY / 360 * Math.PI) * zNear;
					fH = fW / aspect;
				}

				gl.glFrustum(-fW, fW, -fH, fH, zNear, zFar);
			} catch (final BufferOverflowException e) {
				DEBUG.ERR("Buffer overflow exception");
			}
		} else {
			if (aspect >= 1.0) {
				gl.glOrtho(-maxDim * aspect, maxDim * aspect, -maxDim, maxDim, maxDim * 10, -maxDim * 10);
			} else {
				gl.glOrtho(-maxDim, maxDim, -maxDim / aspect, maxDim / aspect, maxDim, -maxDim);
			}
			gl.glTranslated(0d, 0d, maxDim * 0.05);
		}
		getRenderer().getCameraHelper().animate();
		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GLMatrixFunc.GL_PROJECTION_MATRIX, projmatrix, 0);
	}

	/**
	 * Gets the pixel width and height of world.
	 *
	 * @return the pixel width and height of world
	 */
	public double[] getPixelWidthAndHeightOfWorld() {
		final double[] coord = new double[4];
		glu.gluProject(getWorldWidth(), 0, 0, mvmatrix, 0, projmatrix, 0, viewport, 0, coord, 0);
		return coord;
	}

	/**
	 * Gets the world position from.
	 *
	 * @param mouse the mouse
	 * @return the world position from
	 */
	public GamaPoint getWorldPositionFrom(final GamaPoint mouse) {
		final GamaPoint camera = getData().getCameraPos();
		if (gl == null) return new GamaPoint();
		final double[] wcoord = new double[4];
		final double x = (int) mouse.x, y = viewport[3] - (int) mouse.y;
		glu.gluUnProject(x, y, 0.1, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		final GamaPoint v1 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);
		glu.gluUnProject(x, y, 0.9, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord, 0);
		final GamaPoint v2 = new GamaPoint(wcoord[0], wcoord[1], wcoord[2]);
		final GamaPoint v3 = v2.minus(v1).normalized();
		final double distance = camera.z / GamaPoint.dotProduct(new GamaPoint(0.0, 0.0, -1.0), v3);
		final GamaPoint worldCoordinates = camera.plus(v3.times(distance));
		return new GamaPoint(worldCoordinates.x, worldCoordinates.y);
	}

	/**
	 * Gets the view width.
	 *
	 * @return the view width
	 */
	public int getViewWidth() {
		return viewWidth;
	}

	/**
	 * Gets the view height.
	 *
	 * @return the view height
	 */
	public int getViewHeight() {
		return viewHeight;
	}

	/**
	 * Sets the z increment.
	 *
	 * @param z the new z increment
	 */
	public void setZIncrement(final double z) {
		currentZTranslation = 0;
		currentZIncrement = z;
	}

	/**
	 * Computes the translation in Z to enable z-fighting, using the current z increment, computed by ModelScene. The
	 * translations are cumulative
	 */
	public void translateByZIncrement() {
		if (!ZTranslationSuspended) { currentZTranslation += currentZIncrement; }
	}

	/**
	 * Suspend Z translation.
	 */
	public void suspendZTranslation() {
		ZTranslationSuspended = true;
		savedZTranslation = currentZTranslation;
		currentZTranslation = 0;
	}

	/**
	 * Resume Z translation.
	 */
	public void resumeZTranslation() {
		ZTranslationSuspended = false;
		currentZTranslation = savedZTranslation;
	}

	/**
	 * Gets the current Z translation.
	 *
	 * @return the current Z translation
	 */
	public double getCurrentZTranslation() {
		return currentZTranslation;
	}

	/**
	 * Gets the current Z increment.
	 *
	 * @return the current Z increment
	 */
	public double getCurrentZIncrement() {
		return currentZIncrement;
	}

	/**
	 * Returns the previous state.
	 *
	 * @param lighted the lighted
	 * @return true, if successful
	 */
	public boolean setLighting(final boolean lighted) {
		if (this.lighted == lighted) return lighted;
		if (lighted) {
			gl.glEnable(GLLightingFunc.GL_LIGHTING);
		} else {
			gl.glDisable(GLLightingFunc.GL_LIGHTING);
		}
		this.lighted = lighted;
		return !lighted;
	}

	/**
	 * Gets the lighting.
	 *
	 * @return the lighting
	 */
	public boolean getLighting() {
		return lighted;
	}

	/**
	 * Matrix mode.
	 *
	 * @param mode the mode
	 */
	public void matrixMode(final int mode) {
		gl.glMatrixMode(mode);
	}

	/**
	 * Push matrix.
	 */
	public void pushMatrix() {
		gl.glPushMatrix();
	}

	/**
	 * Pop matrix.
	 */
	public void popMatrix() {
		gl.glPopMatrix();
	}

	/**
	 * Reset matrix.
	 *
	 * @param mode the mode
	 */
	private void resetMatrix(final int mode) {
		matrixMode(mode);
		gl.glLoadIdentity();
	}

	/**
	 * Push identity.
	 *
	 * @param mode the mode
	 */
	public void pushIdentity(final int mode) {
		matrixMode(mode);
		pushMatrix();
		gl.glLoadIdentity();
	}

	/**
	 * Pop.
	 *
	 * @param mode the mode
	 */
	public void pop(final int mode) {
		matrixMode(mode);
		popMatrix();
	}

	/**
	 * Push.
	 *
	 * @param mode the mode
	 */
	public void push(final int mode) {
		matrixMode(mode);
		pushMatrix();
	}

	/**
	 * Enable.
	 *
	 * @param state the state
	 */
	public void enable(final int state) {
		if (!gl.glIsEnabled(state)) { gl.glEnableClientState(state); }
	}

	/**
	 * Disable.
	 *
	 * @param state the state
	 */
	public void disable(final int state) {
		if (gl.glIsEnabled(state)) { gl.glDisableClientState(state); }
	}

	@Override
	public void beginDrawing(final int style) {
		gl.glBegin(style);
	}

	@Override
	public void endDrawing() {
		gl.glEnd();
	}

	/**
	 * Translate by.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void translateBy(final double x, final double y, final double z) {
		gl.glTranslated(x, y, z);
	}

	/**
	 * Translate by.
	 *
	 * @param ordinates the ordinates
	 */
	public void translateBy(final double... ordinates) {
		switch (ordinates.length) {
			case 0:
				return;
			case 1:
				translateBy(ordinates[0], 0, 0);
				break;
			case 2:
				translateBy(ordinates[0], ordinates[1], 0);
				break;
			default:
				translateBy(ordinates[0], ordinates[1], ordinates[2]);
		}
	}

	/**
	 * Translate by.
	 *
	 * @param p the p
	 */
	public void translateBy(final GamaPoint p) {
		translateBy(p.x, p.y, p.z);
	}

	/**
	 * Rotate by.
	 *
	 * @param angle the angle
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void rotateBy(final double angle, final double x, final double y, final double z) {
		gl.glRotated(angle, x, y, z);
	}

	/**
	 * Rotate by.
	 *
	 * @param rotation the rotation
	 */
	public void rotateBy(final Rotation3D rotation) {
		final GamaPoint axis = rotation.getAxis();
		final double angle = rotation.getAngle() * Maths.toDeg;
		rotateBy(angle, axis.x, axis.y, axis.z);
	}

	/**
	 * Scale by.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void scaleBy(final double x, final double y, final double z) {
		// currentScale.setLocation(x, y, z);
		gl.glScaled(x, y, z);
	}

	/**
	 * Scale by.
	 *
	 * @param scaling the scaling
	 */
	public void scaleBy(final Scaling3D scaling) {
		scaleBy(scaling.getX(), scaling.getY(), scaling.getZ());
	}

	// DRAWING

	/**
	 * Draws an arbitrary shape using a set of vertices as input, computing the normal if necessary and drawing the
	 * contour if a border is present.
	 *
	 * @param yNegatedVertices            the set of vertices to draw
	 * @param number            the number of vertices to draw. Either 3 (a triangle), 4 (a quad) or -1 (a polygon)
	 * @param clockwise            whether to draw the shape in the clockwise direction (the vertices are always oriented clockwise)
	 * @param computeNormal            whether to compute the normal for this shape
	 * @param border            if not null, will be used to draw the contour
	 */
	public void drawSimpleShape(final ICoordinates yNegatedVertices, final int number, final boolean clockwise,
			final boolean computeNormal, final Color border) {
		if (!isWireframe()) {
			if (computeNormal) { setNormal(yNegatedVertices, clockwise); }
			final int style = number == 4 ? GL2ES3.GL_QUADS : number == -1 ? GL2.GL_POLYGON : GL.GL_TRIANGLES;
			drawVertices(style, yNegatedVertices, number, clockwise);
		}
		drawClosedLine(yNegatedVertices, border, -1);
	}

	/**
	 * Use whatever triangulator is available (JTS or GLU) to draw a polygon.
	 *
	 * @param p the p
	 * @param yNegatedVertices the y negated vertices
	 * @param clockwise the clockwise
	 */
	public void drawPolygon(final Polygon p, final ICoordinates yNegatedVertices, final boolean clockwise) {
		gluTessBeginPolygon(tobj, null);
		gluTessBeginContour(tobj);
		yNegatedVertices.visitClockwise(glTesselatorDrawer);
		gluTessEndContour(tobj);
		applyToInnerGeometries(p, geom -> {
			gluTessBeginContour(tobj);
			getContourCoordinates(geom).visitYNegatedCounterClockwise(glTesselatorDrawer);
			gluTessEndContour(tobj);
		});
		gluTessEndPolygon(tobj);
		// }
	}

	/**
	 * Draw closed line.
	 *
	 * @param yNegatedVertices the y negated vertices
	 * @param number the number
	 */
	public void drawClosedLine(final ICoordinates yNegatedVertices, final int number) {
		drawVertices(GL.GL_LINE_LOOP, yNegatedVertices, number, true);
	}

	/**
	 * Draw closed line.
	 *
	 * @param yNegatedVertices the y negated vertices
	 * @param color the color
	 * @param number the number
	 */
	public void drawClosedLine(final ICoordinates yNegatedVertices, final Color color, final int number) {
		if (color == null) return;
		final Color previous = swapCurrentColor(color);
		drawClosedLine(yNegatedVertices, number);
		setCurrentColor(previous);
	}

	/**
	 * Draw line.
	 *
	 * @param yNegatedVertices the y negated vertices
	 * @param number the number
	 */
	public void drawLine(final ICoordinates yNegatedVertices, final int number) {
		final boolean previous = this.setLighting(false);
		drawVertices(GL.GL_LINE_STRIP, yNegatedVertices, number, true);
		this.setLighting(previous);
	}

	/**
	 * Outputs a single vertex to OpenGL, applying the z-translation to it and computing the maximum z outputted so far.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void outputVertex(final double x, final double y, final double z) {
		gl.glVertex3d(x, y, z + currentZTranslation);
	}

	/**
	 * Output tex coord.
	 *
	 * @param u the u
	 * @param v the v
	 */
	public void outputTexCoord(final double u, final double v) {
		gl.glTexCoord2d(u, v);
	}

	/**
	 * Output normal.
	 *
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void outputNormal(final double x, final double y, final double z) {
		currentNormal.setLocation(x, y, z);
		gl.glNormal3d(x, y, z);
	}

	/**
	 * Draw vertex.
	 *
	 * @param coords the coords
	 * @param normal the normal
	 * @param tex the tex
	 */
	public void drawVertex(final GamaPoint coords, final GamaPoint normal, final GamaPoint tex) {
		if (normal != null) { outputNormal(normal.x, normal.y, normal.z); }
		if (tex != null) { gl.glTexCoord3d(tex.x, tex.y, tex.z); }
		outputVertex(coords.x, coords.y, coords.z);
	}

	@Override
	public void drawVertex(final int i, final double x, final double y, final double z) {
		if (isTextured()) {
			textureCoords.setLocation(x, y, z);
			currentTextureRotation.applyTo(textureCoords);
			final double u = 1 - (textureCoords.x - textureEnvelope.getMinX()) / textureEnvelope.getWidth();
			final double v = (textureCoords.y - textureEnvelope.getMinY()) / textureEnvelope.getHeight();
			outputTexCoord(u, v);
		}
		outputVertex(x, y, z);
	}

	/**
	 * Draw vertices.
	 *
	 * @param style the style
	 * @param yNegatedVertices the y negated vertices
	 * @param number the number
	 * @param clockwise the clockwise
	 */
	public void drawVertices(final int style, final ICoordinates yNegatedVertices, final int number,
			final boolean clockwise) {
		beginDrawing(style);
		yNegatedVertices.visit(this::drawVertex, number, clockwise);
		endDrawing();
	}

	/**
	 * Draw the vertices using the style provided and uses the double[] parameter to determine the texture coordinates
	 * associated with each vertex.
	 *
	 * @param style the style
	 * @param yNegatedVertices the y negated vertices
	 * @param number the number
	 * @param clockwise the clockwise
	 * @param texCoords the tex coords
	 */
	public void drawVertices(final int style, final ICoordinates yNegatedVertices, final int number,
			final boolean clockwise, final double[] texCoords) {
		beginDrawing(style);
		yNegatedVertices.visit((index, x, y, z) -> {
			outputTexCoord(texCoords[index * 2], texCoords[index * 2 + 1]);
			outputVertex(x, y, z);
		}, number, clockwise);
		endDrawing();
	}

	/**
	 * Replaces the current color by the parameter, sets the alpha of the parameter to be the one of the current color,
	 * and returns the ex-current color.
	 *
	 * @param color            a Color
	 * @return the previous current color
	 */
	public Color swapCurrentColor(final Color color) {
		final Color old = currentColor;
		setCurrentColor(color, old == null ? 1 : old.getAlpha() / 255d);
		return old;
	}

	/**
	 * Sets the normal.
	 *
	 * @param yNegatedVertices the y negated vertices
	 * @param clockwise the clockwise
	 * @return the gama point
	 */
	public GamaPoint setNormal(final ICoordinates yNegatedVertices, final boolean clockwise) {
		yNegatedVertices.getNormal(clockwise, 1, currentNormal);
		outputNormal(currentNormal.x, currentNormal.y, currentNormal.z);
		if (isTextured()) { computeTextureCoordinates(yNegatedVertices, clockwise); }
		return currentNormal;
	}

	/**
	 * Compute texture coordinates.
	 *
	 * @param yNegatedVertices the y negated vertices
	 * @param clockwise the clockwise
	 */
	private void computeTextureCoordinates(final ICoordinates yNegatedVertices, final boolean clockwise) {
		workingVertices.setTo(yNegatedVertices);
		currentTextureRotation.rotateToHorizontal(currentNormal, workingVertices.directionBetweenLastPointAndOrigin(),
				clockwise);
		workingVertices.applyRotation(currentTextureRotation);
		workingVertices.getEnvelopeInto(textureEnvelope);
	}

	/**
	 * Sets the current color.
	 *
	 * @param c the c
	 * @param alpha the alpha
	 */
	public void setCurrentColor(final Color c, final double alpha) {
		if (c == null) return;
		setCurrentColor(c.getRed() / 255d, c.getGreen() / 255d, c.getBlue() / 255d, c.getAlpha() / 255d * alpha);
	}

	/**
	 * Sets the current color.
	 *
	 * @param c the new current color
	 */
	public void setCurrentColor(final Color c) {
		setCurrentColor(c, currentObjectAlpha);
	}

	/**
	 * Sets the current color.
	 *
	 * @param red the red
	 * @param green the green
	 * @param blue the blue
	 * @param alpha the alpha
	 */
	public void setCurrentColor(final double red, final double green, final double blue, final double alpha) {
		currentColor = new Color((float) red, (float) green, (float) blue, (float) alpha);
		gl.glColor4d(red, green, blue, alpha);
	}

	/**
	 * Gets the current color.
	 *
	 * @return the current color
	 */
	public Color getCurrentColor() {
		return currentColor;
	}

	// LINE WIDTH

	/**
	 * Sets the line width.
	 *
	 * @param width the new line width
	 */
	public void setLineWidth(final double width) {
		gl.glLineWidth((float) width);
	}

	// ALPHA
	/**
	 * Between 0d (transparent) to 1d (opaque).
	 *
	 * @param alpha the new current object alpha
	 */
	public final void setCurrentObjectAlpha(final double alpha) {
		currentObjectAlpha = alpha;
	}

	/**
	 * Gets the current object alpha.
	 *
	 * @return the current object alpha
	 */
	public double getCurrentObjectAlpha() {
		return currentObjectAlpha;
	}

	// TEXTURES

	/**
	 * Sets the id of the textures to enable. If the first is equal to NO_TEXTURE, all textures are disabled. If the
	 * second is equal to NO_TEXTURE, then the first one is also bound to the second unit.
	 *
	 * @param t0 the t 0
	 * @param t1 the t 1
	 */
	public void setCurrentTextures(final int t0, final int t1) {
		primaryTexture = t0;
		alternateTexture = t1;
		textured = t0 != NO_TEXTURE;
		enablePrimaryTexture();
	}

	/**
	 * Bind texture.
	 *
	 * @param texture the texture
	 */
	public void bindTexture(final int texture) {
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		// Apply antialas to the texture based on the current preferences
		final boolean isAntiAlias = getData().isAntialias();
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, isAntiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, isAntiAlias ? GL.GL_LINEAR : GL.GL_NEAREST);
		if (isAntiAlias && anisotropicLevel > NO_ANISOTROPY) {
			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAX_ANISOTROPY_EXT, anisotropicLevel);
		}
	}

	/**
	 * Enable primary texture.
	 */
	public void enablePrimaryTexture() {
		if (primaryTexture == NO_TEXTURE) return;
		bindTexture(primaryTexture);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	/**
	 * Enable alternate texture.
	 */
	public void enableAlternateTexture() {
		if (alternateTexture == NO_TEXTURE) return;
		bindTexture(alternateTexture);
		gl.glEnable(GL.GL_TEXTURE_2D);
	}

	/**
	 * Disable textures.
	 */
	public void disableTextures() {
		gl.glDisable(GL.GL_TEXTURE_2D);
		textured = false;
	}

	/**
	 * Delete volatile textures.
	 */
	public void deleteVolatileTextures() {
		textureCache.deleteVolatileTextures();
	}

	/**
	 * Cache texture.
	 *
	 * @param file the file
	 */
	public void cacheTexture(final File file) {
		if (file == null) return;
		textureCache.processs(file);
	}

	/**
	 * Gets the texture id.
	 *
	 * @param file the file
	 * @param useCache the use cache
	 * @return the texture id
	 */
	public int getTextureId(final GamaImageFile file, final boolean useCache) {
		final Texture r = textureCache.getTexture(file.getFile(null), file.isAnimated(), useCache);
		if (r == null) return NO_TEXTURE;
		return r.getTextureObject();
	}

	/**
	 * Gets the texture id.
	 *
	 * @param img the img
	 * @return the texture id
	 */
	public int getTextureId(final BufferedImage img) {
		final Texture r = textureCache.getTexture(img);
		if (r == null) return NO_TEXTURE;
		return r.getTextureObject();
	}

	/**
	 * Gets the texture.
	 *
	 * @param file the file
	 * @param isAnimated the is animated
	 * @param useCache the use cache
	 * @return the texture
	 */
	public Texture getTexture(final File file, final boolean isAnimated, final boolean useCache) {
		return textureCache.getTexture(file, isAnimated, useCache);
	}

	// GEOMETRIES

	/**
	 * Cache geometry.
	 *
	 * @param object the object
	 */
	public void cacheGeometry(final GamaGeometryFile object) {
		geometryCache.process(object);
	}

	/**
	 * Gets the envelope for.
	 *
	 * @param obj the obj
	 * @return the envelope for
	 */
	public Envelope3D getEnvelopeFor(final Object obj) {
		if (obj instanceof GamaGeometryFile) return geometryCache.getEnvelope((GamaGeometryFile) obj);
		if (obj instanceof Geometry) return Envelope3D.of((Geometry) obj);
		return null;
	}

	// TEXT

	/**
	 * Draws one string in raster at the given coords and with the given font. Enters and exits raster mode before and
	 * after drawing the string
	 *
	 * @param s the s
	 * @param font            the font to draw with
	 * @param x the x
	 * @param y the y
	 * @param z the z
	 */
	public void rasterText(final String s, final int font, final double x, final double y, final double z) {
		beginRasterTextMode();
		final boolean previous = setLighting(false);
		gl.glRasterPos3d(x, y, z);
		glut.glutBitmapString(font, s);
		setLighting(previous);
		exitRasterTextMode();
	}

	/**
	 * Exit raster text mode.
	 */
	public void exitRasterTextMode() {
		gl.glEnable(GL.GL_BLEND);
		popMatrix();
		inRasterTextMode = false;
	}

	/**
	 * Begin raster text mode.
	 */
	public void beginRasterTextMode() {
		if (inRasterTextMode) return;
		pushMatrix();
		gl.glDisable(GL.GL_BLEND);
		inRasterTextMode = true;
	}

	/**
	 * Gets the world width.
	 *
	 * @return the world width
	 */
	public double getWorldWidth() {
		return getData().getEnvWidth();
	}

	/**
	 * Gets the world height.
	 *
	 * @return the world height
	 */
	public double getWorldHeight() {
		return getData().getEnvHeight();
	}

	/**
	 * Sets the display wireframe.
	 *
	 * @param wireframe the new display wireframe
	 */
	public void setDisplayWireframe(final boolean wireframe) {
		if (wireframe == displayIsWireframe) return;
		displayIsWireframe = wireframe;
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, isWireframe() ? GL2GL3.GL_LINE : GL2GL3.GL_FILL);
	}

	/**
	 * Sets the object wireframe.
	 *
	 * @param wireframe the new object wireframe
	 */
	public void setObjectWireframe(final boolean wireframe) {
		if (wireframe == objectIsWireframe) return;
		objectIsWireframe = wireframe;
		if (!displayIsWireframe) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, wireframe ? GL2GL3.GL_LINE : GL2GL3.GL_FILL);
		}
	}

	/**
	 * Checks if is wireframe.
	 *
	 * @return true, if is wireframe
	 */
	public boolean isWireframe() {
		return displayIsWireframe || objectIsWireframe;
	}

	// PICKING

	/**
	 * Run with names.
	 *
	 * @param r the r
	 */
	public void runWithNames(final Runnable r) {
		gl.glInitNames();
		gl.glPushName(0);
		r.run();
		gl.glPopName();
	}

	/**
	 * Register for selection.
	 *
	 * @param index the index
	 */
	public void registerForSelection(final int index) {
		gl.glLoadName(index);
	}

	/**
	 * Mark if selected.
	 *
	 * @param attributes the attributes
	 */
	public void markIfSelected(final DrawingAttributes attributes) {
		pickingState.tryPick(attributes);
	}

	// LISTS

	/**
	 * Compile as list.
	 *
	 * @param r the r
	 * @return the int
	 */
	public int compileAsList(final Runnable r) {
		final int index = gl.glGenLists(1);
		gl.glNewList(index, GL2.GL_COMPILE);
		r.run();
		gl.glEndList();
		return index;
	}

	/**
	 * Draw list.
	 *
	 * @param i the i
	 */
	public void drawList(final int i) {
		gl.glCallList(i);
	}

	/**
	 * Delete list.
	 *
	 * @param index the index
	 */
	public void deleteList(final Integer index) {
		gl.glDeleteLists(index, 1);
	}

	/**
	 * Draw cached geometry.
	 *
	 * @param file the file
	 * @param border the border
	 */
	public void drawCachedGeometry(final GamaGeometryFile file, final Color border) {
		if (file == null) return;
		final Integer index = geometryCache.get(file);
		if (index != null) {
			drawList(index);
			if (border != null || isWireframe()) {
				final Color old = swapCurrentColor(border);
				getGL().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
				try {
					drawList(index);
				} finally {
					setCurrentColor(old);
					getGL().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				}
			}
		}
	}

	/**
	 * Draw cached geometry.
	 *
	 * @param id the id
	 * @param border the border
	 */
	public void drawCachedGeometry(final IShape.Type id, /* final boolean solid, */ final Color border) {
		if (geometryCache == null || id == null) return;
		final BuiltInGeometry object = geometryCache.get(id);
		if (object != null) {
			if (!isWireframe()) { object.draw(this); }
			if (isWireframe() || border != null) {
				final Color old = swapCurrentColor(border != null ? border : getCurrentColor());
				getGL().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
				try {
					object.draw(this);
				} finally {
					setCurrentColor(old);
					getGL().glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				}
			}
		}
	}

	/**
	 * Initialize shape cache.
	 */
	public void initializeShapeCache() {
		textured = true;
		geometryCache.initialize(this);
		textured = false;
	}

	/**
	 * Checks if is textured.
	 *
	 * @return true, if is textured
	 */
	public boolean isTextured() {
		return textured && !isWireframe();
	}

	// COMPLEX SHAPES

	/**
	 * Begin object.
	 *
	 * @param object the object
	 */
	public void beginObject(final AbstractObject object) {
		// DEBUG.OUT("Object " + object + " begin and is " + (object.getAttributes().isEmpty() ? "empty" : "filled"));
		setObjectWireframe(object.getAttributes().isEmpty());
		setLineWidth(object.getAttributes().getLineWidth());
		setCurrentTextures(object.getPrimaryTexture(this), object.getAlternateTexture(this));
		setCurrentColor(object.getAttributes().getColor());
		if (object.isFilled() && !object.getAttributes().isSynthetic()) {
			gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_DECAL);
		}

	}

	/**
	 * End object.
	 *
	 * @param object the object
	 */
	public void endObject(final AbstractObject object) {
		disableTextures();
		translateByZIncrement();
		if (object.isFilled() && !object.getAttributes().isSynthetic()) {
			gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
		}
		// DEBUG.OUT("Object " + object + " ends and is " + (object.getAttributes().isEmpty() ? "empty" : "filled"));
		// setObjectWireframe(!object.getAttributes().isEmpty());
	}

	/**
	 * Begin scene.
	 *
	 * @return the pass
	 */
	public Pass beginScene() {
		setDisplayWireframe(getData().isWireframe());
		processUnloadedCacheObjects();
		final Color backgroundColor = getData().getBackgroundColor();
		gl.glClearColor(backgroundColor.getRed() / 255.0f, backgroundColor.getGreen() / 255.0f,
				backgroundColor.getBlue() / 255.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);
		gl.glClearDepth(1.0f);
		setLighting(getData().isLightOn());
		resetMatrix(GLMatrixFunc.GL_PROJECTION);
		updatePerspective(gl);
		resetMatrix(GLMatrixFunc.GL_MODELVIEW);
		rotateModel();
		return endScene;
	}

	/**
	 * Process unloaded cache objects.
	 */
	public void processUnloadedCacheObjects() {
		textureCache.processUnloaded();
		geometryCache.processUnloaded();
	}

	/**
	 * Checks if is continuous rotation active.
	 *
	 * @return true, if is continuous rotation active
	 */
	private boolean isContinuousRotationActive() {
		return getData().isContinuousRotationOn() && !getData().cameraInteractionDisabled();
	}

	/**
	 * Rotate model.
	 */
	public void rotateModel() {
		if (isContinuousRotationActive()) { getData().incrementZRotation(); }
		if (getData().getCurrentRotationAboutZ() != 0d) {
			final double env_width = getWorldWidth();
			final double env_height = getWorldHeight();
			translateBy(env_width / 2, -env_height / 2, 0d);
			rotateBy(getData().getCurrentRotationAboutZ(), 0, 0, 1);
			translateBy(-env_width / 2, +env_height / 2, 0d);
		}
	}

	/**
	 * End scene.
	 */
	public void endScene() {
		boolean drawFPS = getData().isShowfps();
		boolean drawRotation = rotationMode && SHOULD_DRAW_ROTATION_SPHERE;
		boolean drawROI = roiEnvelope != null;
		if (drawFPS || drawRotation || drawROI) {
			disableTextures();
			setLighting(false);
		}
		drawFPS(drawFPS);
		drawROI(drawROI);
		drawRotation(drawRotation);
		// gl.glFlush();
		gl.glFinish();
	}

	/**
	 * Initialize GL states.
	 *
	 * @param bg the bg
	 */
	public void initializeGLStates(final Color bg) {
		gl.glClearColor(bg.getRed() / 255.0f, bg.getGreen() / 255.0f, bg.getBlue() / 255.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT);

		// Putting the swap interval to 0 (instead of 1) seems to cure some of
		// the problems of resizing of views.
		gl.setSwapInterval(0);

		// Enable smooth shading, which blends colors nicely, and smoothes out
		// lighting.
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		// Enabling the depth buffer & the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do
		// Whether face culling is enabled or not
		if (GamaPreferences.Displays.ONLY_VISIBLE_FACES.getValue()) {
			gl.glEnable(GL.GL_CULL_FACE);
			gl.glCullFace(GL.GL_BACK);
		}
		// Turn on clockwise direction of vertices as an indication of "front" (important)
		gl.glFrontFace(GL.GL_CW);

		// Hints
		int hint = getData().isAntialias() ? GL.GL_NICEST : GL.GL_FASTEST;
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, hint);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, hint);
		gl.glHint(GL2ES1.GL_POINT_SMOOTH_HINT, hint);
		// gl.glHint(GL2GL3.GL_POLYGON_SMOOTH_HINT, hint);
		gl.glHint(GL2.GL_MULTISAMPLE_FILTER_HINT_NV, hint);
		// Enable texture 2D
		gl.glEnable(GL.GL_TEXTURE_2D);
		// Blending & alpha control
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTexEnvi(GL2ES1.GL_TEXTURE_ENV, GL2ES1.GL_TEXTURE_ENV_MODE, GL2ES1.GL_MODULATE);
		gl.glEnable(GL2ES1.GL_ALPHA_TEST);
		gl.glAlphaFunc(GL.GL_GREATER, 0.01f);
		// Disabling line smoothing to only rely on FSAA
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL2ES1.GL_POINT_SMOOTH);
		// gl.glEnable(GL2GL3.GL_POLYGON_SMOOTH);
		// Enabling forced normalization of normal vectors (important)
		gl.glEnable(GLLightingFunc.GL_NORMALIZE);
		// Enabling multi-sampling (necessary ?)
		// if (USE_MULTI_SAMPLE) {
		gl.glEnable(GL.GL_MULTISAMPLE);
		// Setting the default polygon mode
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		initializeShapeCache();

	}

	/**
	 * Gets the ratios.
	 *
	 * @return the ratios
	 */
	public GamaPoint getRatios() {
		return ratios;
	}

	/**
	 * DECORATIONS: ROI, Rotation, FPS.
	 *
	 * @param b the b
	 */

	public void isInRotationMode(final boolean b) {
		rotationMode = b;
	}

	/**
	 * Checks if is in rotation mode.
	 *
	 * @return true, if is in rotation mode
	 */
	public boolean isInRotationMode() {
		return rotationMode;
	}

	/**
	 * Draw FPS.
	 *
	 * @param doIt the do it
	 */
	public void drawFPS(final boolean doIt) {
		if (doIt) {
			setCurrentColor(Color.black);
			final int nb = (int) getCanvas().getAnimator().getLastFPS();
			final String s = nb == 0 ? "(computing FPS...)" : nb + " FPS";
			rasterText(s, GLUT.BITMAP_HELVETICA_12, -5, 5, 0);
		}
	}

	/**
	 * Draw ROI.
	 *
	 * @param doIt the do it
	 */
	public void drawROI(final boolean doIt) {
		if (doIt) { geometryDrawer.drawROIHelper(roiEnvelope); }
	}

	/**
	 * Size of rotation elements.
	 *
	 * @return the double
	 */
	public double sizeOfRotationElements() {
		return Math.min(getMaxEnvDim() / 4d, getData().getCameraPos().minus(getData().getCameraTarget()).norm() / 6d);
	}

	/**
	 * Draw rotation.
	 *
	 * @param doIt the do it
	 */
	public void drawRotation(final boolean doIt) {
		if (doIt) {
			final GamaPoint target = getData().getCameraTarget();
			final double distance = getData().getCameraPos().minus(target).norm();
			geometryDrawer.drawRotationHelper(target, distance, Math.min(getMaxEnvDim() / 4d, distance / 6d));
		}
	}

	/**
	 * Toogle ROI.
	 */
	public void toogleROI() {
		isROISticky = !isROISticky;
	}

	/**
	 * Checks if is sticky ROI.
	 *
	 * @return true, if is sticky ROI
	 */
	public boolean isStickyROI() {
		return isROISticky;
	}

	/**
	 * Gets the ROI envelope.
	 *
	 * @return the ROI envelope
	 */
	public Envelope3D getROIEnvelope() {
		return roiEnvelope;
	}

	/**
	 * Cancel ROI.
	 */
	public void cancelROI() {
		if (isROISticky) return;
		roiEnvelope = null;
	}

	/**
	 * Define ROI.
	 *
	 * @param mouseStart the mouse start
	 * @param mouseEnd the mouse end
	 */
	public void defineROI(final GamaPoint mouseStart, final GamaPoint mouseEnd) {
		final GamaPoint start = getWorldPositionFrom(mouseStart);
		final GamaPoint end = getWorldPositionFrom(mouseEnd);
		roiEnvelope = Envelope3D.of(start.x, end.x, start.y, end.y, 0, getMaxEnvDim() / 20d);
	}

	/**
	 * Mouse in ROI.
	 *
	 * @param mousePosition the mouse position
	 * @return true, if successful
	 */
	public boolean mouseInROI(final GamaPoint mousePosition) {
		final Envelope3D env = getROIEnvelope();
		if (env == null) return false;
		final GamaPoint p = getWorldPositionFrom(mousePosition);
		return env.contains(p);
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	/**
	 * Checks if is rendering keystone.
	 *
	 * @return true, if is rendering keystone
	 */
	public boolean isRenderingKeystone() {
		KeystoneHelper k = getRenderer().getKeystoneHelper();
		return k.isActive() || getRenderer().getData().isKeystoneDefined();
	}

}