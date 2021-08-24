/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.layers.LayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.display.opengl.scene.layers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import com.google.common.collect.ImmutableList;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;

import gama.common.geometry.Scaling3D;
import gama.common.interfaces.IKeyword;
import gama.common.preferences.GamaPreferences;
import gama.common.ui.ILayer;
import gama.display.opengl.OpenGL;
import gama.display.opengl.renderer.IOpenGLRenderer;
import gama.display.opengl.scene.AbstractObject;
import gama.display.opengl.scene.geometry.GeometryObject;
import gama.display.opengl.scene.mesh.MeshObject;
import gama.display.opengl.scene.resources.ResourceObject;
import gama.display.opengl.scene.text.StringObject;
import gama.metamodel.agent.IAgent;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.IShape;
import gama.runtime.IScope;
import gama.util.GamaColor;
import gama.util.file.GamaGeometryFile;
import gama.util.matrix.IField;
import gaml.expressions.IExpression;
import gaml.expressions.units.PixelUnitExpression;
import gaml.operators.Cast;
import gaml.statements.draw.DrawingAttributes;
import gaml.statements.draw.MeshDrawingAttributes;
import gaml.statements.draw.ShapeDrawingAttributes;
import gaml.statements.draw.TextDrawingAttributes;
import gaml.types.GamaGeometryType;

/**
 * Class LayerObject.
 *
 * @author drogoul
 * @since 3 mars 2014
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class LayerObject {

	final static GamaPoint NULL_OFFSET = new GamaPoint();
	final static GamaPoint NULL_SCALE = new GamaPoint(1, 1, 1);

	GamaPoint offset = new GamaPoint(NULL_OFFSET);
	GamaPoint scale = new GamaPoint(NULL_SCALE);
	protected Double alpha = 1d;
	public final ILayer layer;
	volatile boolean isInvalid;
	volatile boolean locked;
	boolean isAnimated;
	protected final IOpenGLRenderer renderer;
	protected final LinkedList<List<AbstractObject<?, ?>>> traces;
	protected List<AbstractObject<?, ?>> currentList;
	protected Integer openGLListIndex;
	protected boolean isFading;

	public LayerObject(final IOpenGLRenderer renderer2, final ILayer layer) {
		this.renderer = renderer2;
		this.layer = layer;
		computeOffset();
		computeScale();
		currentList = newCurrentList();
		if (layer != null && layer.getData().getTrace() != null || renderer.useShader()) {
			traces = new LinkedList();
			traces.add(currentList);
		} else {
			traces = null;
		}
	}

	public void computeScale() {
		double zScale = layer.getData().getSize().getZ();
		if (zScale <= 0) { zScale = 1; }
		scale.setLocation(renderer.getLayerWidth() / renderer.getWidth(),
				renderer.getLayerHeight() / renderer.getHeight(), zScale);

	}

	public void computeOffset() {
		final IScope scope = renderer.getSurface().getScope();
		final IExpression expr = layer.getDefinition().getFacet(IKeyword.POSITION);

		if (expr != null) {
			final boolean containsPixels = expr.findAny(e -> e instanceof PixelUnitExpression);
			offset.setLocation(Cast.asPoint(scope, expr.value(scope)));
			if (Math.abs(offset.x) <= 1 && !containsPixels) { offset.x *= renderer.getEnvWidth(); }
			if (offset.x < 0) { offset.x = renderer.getEnvWidth() - offset.x; }
			if (Math.abs(offset.y) <= 1 && !containsPixels) { offset.y *= renderer.getEnvHeight(); }
			if (offset.y < 0) { offset.y = renderer.getEnvHeight() - offset.y; }

		}
		increaseZ();
	}

	protected void increaseZ() {
		double currentZLayer = renderer.getMaxEnvDim() * layer.getData().getPosition().getZ();
		currentZLayer += layer.getData().getAddedElevation() * renderer.getMaxEnvDim();
		offset.z = currentZLayer;
	}

	public boolean isLightInteraction() {
		return true;
	}

	protected List newCurrentList() {
		return /* Collections.synchronizedList( */new ArrayList()/* ) */;
	}

	protected boolean isPickable() {
		return layer == null ? false : layer.getData().isSelectable();
	}

	public void draw(final OpenGL gl) {
		if (isInvalid()) return;
		drawWithoutShader(gl);
	}

	private void drawWithoutShader(final OpenGL gl) {
		prepareDrawing(gl);
		try {
			final boolean picking = renderer.getPickingHelper().isPicking() && isPickable();
			doDrawing(gl, picking);
		} finally {
			stopDrawing(gl);
		}

	}

	protected void doDrawing(final OpenGL gl, final boolean picking) {
		if (picking) {
			gl.runWithNames(() -> drawAllObjects(gl, true));
		} else if (isAnimated) {
			drawAllObjects(gl, false);
		} else {
			if (openGLListIndex == null) { openGLListIndex = gl.compileAsList(() -> drawAllObjects(gl, false)); }
			gl.drawList(openGLListIndex);
		}
	}

	protected void prepareDrawing(final OpenGL gl) {
		gl.getGL().glEnable(GL.GL_DEPTH_TEST);
		gl.push(GLMatrixFunc.GL_MODELVIEW);
		final GamaPoint nonNullOffset = getOffset();
		gl.translateBy(nonNullOffset.x, -nonNullOffset.y, nonNullOffset.z);
		final GamaPoint nonNullScale = getScale();
		gl.scaleBy(nonNullScale.x, nonNullScale.y, nonNullScale.z);
	}

	protected void stopDrawing(final OpenGL gl) {
		gl.pop(GLMatrixFunc.GL_MODELVIEW);
	}

	protected void drawAllObjects(final OpenGL gl, final boolean picking) {
		if (traces != null) {
			double delta = 0;
			if (isFading) {
				final int size = traces.size();
				delta = size == 0 ? 0 : 1d / size;
			}
			double alpha = 0d;
			for (final List<AbstractObject<?, ?>> list : traces) {
				alpha = delta == 0d ? this.alpha : this.alpha * (alpha + delta);
				drawObjects(gl, list, alpha, picking);
			}
		} else {
			drawObjects(gl, currentList, alpha, picking);
		}
	}

	protected void drawObjects(final OpenGL gl, final List<AbstractObject<?, ?>> list, final double alpha,
			final boolean picking) {
		final ImmutableList<AbstractObject> l = ImmutableList.copyOf(list);
		gl.setCurrentObjectAlpha(alpha);
		for (final AbstractObject object : l) {
			object.draw(gl, gl.getDrawerFor(object.type), picking);
		}
	}

	public boolean isStatic() {
		if (layer == null) return true;
		return !layer.getData().isDynamic();
	}

	public void setAlpha(final Double a) {
		alpha = a;
	}

	public GamaPoint getOffset() {
		return offset == null ? NULL_OFFSET : offset;
	}

	public void setOffset(final GamaPoint offset) {
		if (offset != null) {
			this.offset = new GamaPoint(offset);
		} else {
			this.offset = null;
		}
	}

	public GamaPoint getScale() {
		return scale == null ? NULL_SCALE : scale;
	}

	public void setScale(final GamaPoint scale) {
		this.scale = new GamaPoint(scale);
	}

	public void addString(final String string, final TextDrawingAttributes attributes) {
		currentList.add(new StringObject(string, attributes));
	}

	public void addFile(final GamaGeometryFile file, final DrawingAttributes attributes) {
		currentList.add(new ResourceObject(file, attributes));
	}

	public void addImage(final Object o, final DrawingAttributes attributes) {
		// If no dimensions have been defined, then the image is considered as wide and tall as the environment
		Scaling3D size = attributes.getSize();
		if (size == null) {
			size = Scaling3D.of(renderer.getEnvWidth(), renderer.getEnvHeight(), 0);
			attributes.setSize(size);
		}
		final GamaPoint loc = attributes.getLocation();
		final GamaPoint newLoc = loc == null ? size.toGamaPoint().dividedBy(2) : loc;
		// We build a rectangle that will serve as a "support" for the image (which will become its texture)
		final Geometry geometry =
				GamaGeometryType.buildRectangle(size.getX(), size.getY(), new GamaPoint()).getInnerGeometry();

		attributes.setLocation(newLoc);
		attributes.setTexture(o);
		attributes.setSynthetic(true);
		addGeometry(geometry, attributes);
	}

	public void addField(final IField fieldValues, final MeshDrawingAttributes attributes) {
		currentList.add(new MeshObject(fieldValues, attributes));
	}

	public void addGeometry(final Geometry geometry, final DrawingAttributes attributes) {
		isAnimated = attributes.isAnimated();
		currentList.add(new GeometryObject(geometry, attributes));
	}

	protected int getTrace() {
		if (layer == null) return 0;
		final Integer trace = layer.getData().getTrace();
		return trace == null ? 0 : trace;
	}

	protected boolean getFading() {
		if (layer == null) return false;
		final Boolean fading = layer.getData().getFading();
		return fading == null ? false : fading;
	}

	public void clear(final OpenGL gl) {

		if (traces != null) {
			final int sizeLimit = getTrace();
			isFading = getFading();
			final int size = traces.size();
			for (int i = 0, n = size - sizeLimit; i < n; i++) {
				traces.poll();
			}
			currentList = newCurrentList();
			traces.offer(currentList);
		} else {
			currentList.clear();
		}
		final Integer index = openGLListIndex;
		if (index != null) {
			gl.deleteList(index);
			openGLListIndex = null;
		}

	}

	public boolean isInvalid() {
		return isInvalid;
	}

	public void invalidate() {
		isInvalid = true;
	}

	public boolean hasTrace() {
		return getTrace() > 0;
	}

	public boolean isLocked() {
		return locked;
	}

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

	public boolean isOverlay() {
		return false;
	}

	public int numberOfTraces() {
		return traces == null ? 1 : traces.size();
	}

	public boolean canSplit() {
		return true;
	}

	protected void addSyntheticObject(final List<AbstractObject<?, ?>> list, final IShape shape, final GamaColor color,
			final IShape.Type type, final boolean empty) {
		final DrawingAttributes att = new ShapeDrawingAttributes(shape, (IAgent) null, color, color, type,
				GamaPreferences.Displays.CORE_LINE_WIDTH.getValue(), null);
		att.setEmpty(empty);
		att.setHeight(shape.getDepth());
		att.setLighting(false);
		list.add(new GeometryObject(shape.getInnerGeometry(), att));
	}

	public void forceRedraw(final OpenGL gl) {
		if (layer == null) return;
		if (openGLListIndex != null) {
			gl.deleteList(openGLListIndex);
			openGLListIndex = null;
		}
		// layer.draw(renderer.getSurface().getScope(), renderer);

	}

}