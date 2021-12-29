/*******************************************************************************************************
 *
 * GridLayer.java, in gama.core.kernel, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.outputs.layers;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import gama.common.ui.IDisplaySurface;
import gama.common.ui.IGraphics;
import gama.metamodel.agent.IAgent;
import gama.metamodel.shape.IShape;
import gama.runtime.IScope;
import gama.runtime.IScope.IGraphicsScope;
import gama.util.Collector;
import gama.util.GamaColor;
import gama.util.file.GamaImageFile;
import gama.util.matrix.GamaField;
import gama.util.matrix.IField;
import gaml.statements.draw.MeshDrawingAttributes;

/**
 * The Class GridLayer.
 */
public class GridLayer extends AbstractLayer {

	/**
	 * Instantiates a new grid layer.
	 *
	 * @param layer
	 *            the layer
	 */
	public GridLayer(final ILayerStatement layer) {
		super(layer);
	}

	@Override
	protected ILayerData createData() {
		return new GridLayerData(definition);
	}

	@Override
	public GridLayerData getData() { return (GridLayerData) super.getData(); }

	@Override
	public Rectangle2D focusOn(final IShape geometry, final IDisplaySurface s) {
		final IAgent a = geometry.getAgent();
		if (a == null || a.getSpecies() != getData().getGrid().getCellSpecies()) return null;
		return super.focusOn(a, s);
	}

	@Override
	public void reloadOn(final IDisplaySurface surface) {
		super.reloadOn(surface);
		getData().setImage(null);
	}

	@Override
	public void privateDraw(final IGraphicsScope scope, final IGraphics dg) {
		GamaColor lineColor = null;
		final GridLayerData data = getData();
		if (data.drawLines()) { lineColor = data.getLineColor(); }
		final double[] gridValueMatrix = data.getElevationMatrix(scope);
		final GamaImageFile textureFile = data.textureFile();
		final MeshDrawingAttributes attributes =
				new MeshDrawingAttributes(getName(), lineColor, gridValueMatrix == null);
		attributes.setGrayscaled(data.isGrayScaled());
		attributes.setEmpty(data.isWireframe());
		final BufferedImage image = data.getImage();
		if (textureFile != null) {
			attributes.setTextures(Arrays.asList(textureFile));
		} else if (image != null) {
			final int[] imageData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			System.arraycopy(data.getGrid().getDisplayData(), 0, imageData, 0, imageData.length);
			attributes.setTextures(Arrays.asList(image));
		}
		attributes.setTriangulated(data.isTriangulated());
		attributes.setWithText(data.isShowText());
		attributes.setCellSize(data.getCellSize());
		attributes.setBorder(lineColor);
		attributes.setXYDimension(data.getDimensions());
		attributes.setSmooth(data.isSmooth() ? 1 : 0);

		if (gridValueMatrix == null) {
			dg.drawImage(image, attributes);
		} else {
			dg.drawField(new GamaField(scope, (int) data.getDimensions().x, (int) data.getDimensions().y,
					gridValueMatrix, IField.NO_NO_DATA), attributes);
		}
	}

	@Override
	public Set<IAgent> collectAgentsAt(final int x, final int y, final IDisplaySurface g) {
		try (Collector.AsSet<IAgent> result = Collector.getSet()) {
			result.add(getData().getGrid().getAgentAt(getModelCoordinatesFrom(x, y, g)));
			return result.items();
		}
	}

	@Override
	public String getType() { return "Grid layer"; }

	@Override
	public Collection<IAgent> getAgentsForMenu(final IScope scope) {
		return getData().getGrid().getAgents();
	}

}
