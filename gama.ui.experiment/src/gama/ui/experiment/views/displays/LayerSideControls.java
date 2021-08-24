/*********************************************************************************************
 *
 * 'LayerSideControls.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.experiment.views.displays;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import gama.common.geometry.ICoordinates;
import gama.common.interfaces.IKeyword;
import gama.common.interfaces.ItemList;
import gama.common.preferences.GamaPreferences;
import gama.common.ui.IDisplaySurface;
import gama.common.ui.ILayer;
import gama.metamodel.shape.GamaPoint;
import gama.outputs.LayeredDisplayData;
import gama.outputs.layers.AgentLayerStatement;
import gama.outputs.layers.GridLayer;
import gama.outputs.layers.ILayerStatement;
import gama.outputs.layers.ImageLayer;
import gama.outputs.layers.ImageLayerStatement;
import gama.outputs.layers.SpeciesLayerStatement;
import gama.outputs.layers.charts.ChartLayerStatement;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gama.ui.base.controls.ParameterExpandBar;
import gama.ui.base.controls.ParameterExpandItem;
import gama.ui.base.interfaces.EditorListener;
import gama.ui.base.parameters.BooleanEditor;
import gama.ui.base.parameters.ColorEditor;
import gama.ui.base.parameters.EditorFactory;
import gama.ui.base.parameters.EditorsGroup;
import gama.ui.base.parameters.FloatEditor;
import gama.ui.base.parameters.IntEditor;
import gama.ui.base.parameters.PointEditor;
import gama.ui.base.parameters.StringEditor;
import gama.ui.base.utils.WorkbenchHelper;
import gama.util.GamaColor;
import gama.util.GamaListFactory;
import gama.util.IList;
import gaml.expressions.IExpression;
import gaml.operators.Strings;
import gaml.types.Types;

/**
 * Class LayerSideControls.
 *
 * @author drogoul
 * @since 26 avr. 2014
 *
 */
public class LayerSideControls {

	public static void updateIfPaused(final ILayer layer, final IDisplaySurface container) {
		container.updateDisplay(true);
	}

	public Composite fill(final Composite parent, final LayeredDisplayView view) {

		final Composite column = new Composite(parent, SWT.NONE);
		// column.setBackground(IGamaColors.WHITE.color());
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		column.setLayoutData(data);
		final GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		column.setLayout(layout);

		final Composite viewersComposite = new Composite(parent, SWT.None);
		// viewersComposite.setBackground(IGamaColors.WHITE.color());
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewersComposite.setLayoutData(data);
		viewersComposite.setLayout(new GridLayout(1, true));

		final ParameterExpandBar propertiesViewer =
				new ParameterExpandBar(viewersComposite, SWT.V_SCROLL, false, false, false, false, null);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		propertiesViewer.setLayoutData(data);
		propertiesViewer.setSpacing(5);
		final Label sep = new Label(viewersComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		sep.setLayoutData(data);
		final ItemList<ILayer> list = view.getDisplayManager();
		final ParameterExpandBar layerViewer =
				new ParameterExpandBar(viewersComposite, SWT.V_SCROLL, false, false, true, true, list);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		layerViewer.setLayoutData(data);
		layerViewer.setSpacing(5);
		// Fill the 2 viewers
		fillGeneralParameters(propertiesViewer, view);
		if (view.isOpenGL()) {
			fillCameraParameters(propertiesViewer, view);
			fillOpenGLParameters(propertiesViewer, view);
			fillKeystoneParameters(propertiesViewer, view);
		}

		for (final ILayer layer : list.getItems()) {
			fillLayerParameters(layerViewer, layer, view);
		}

		viewersComposite.layout();
		layerViewer.addListener(SWT.Collapse, e -> viewersComposite.redraw());
		layerViewer.addListener(SWT.Expand, e -> viewersComposite.redraw());
		propertiesViewer.addListener(SWT.Collapse, e -> viewersComposite.redraw());
		propertiesViewer.addListener(SWT.Expand, e -> viewersComposite.redraw());
		parent.layout();
		return viewersComposite;

	}

	private void fillOpenGLParameters(final ParameterExpandBar viewer, final LayeredDisplayView view) {
		final EditorsGroup contents = createContentsComposite(viewer);
		final IDisplaySurface ds = view.getDisplaySurface();
		final IScope scope = ds.getScope();
		final LayeredDisplayData data = ds.getData();
		EditorFactory.create(scope, contents, "View as wireframe", data.isWireframe(),
				(EditorListener<Boolean>) val -> {
					ds.runAndUpdate(() -> {
						data.setWireframe(val);
					});

				});
		EditorFactory.create(scope, contents, "Split layers", data.isLayerSplitted(), (EditorListener<Boolean>) val -> {
			ds.runAndUpdate(() -> {
				data.setLayerSplitted(val);
			});

		});
		EditorFactory.create(scope, contents, "Split distance", data.getSplitDistance(), 0d, 1d, .001d, false, true,
				(EditorListener<Double>) val -> {
					// ds.run(() -> {
					data.setSplitDistance(val / 3);
					// });

				});
		createItem(viewer, "OpenGL", null, contents);
	}

	PointEditor cameraPos, cameraTarget, cameraOrientation;
	StringEditor preset;
	IntEditor zoom;
	FloatEditor rotate;

	private void fillCameraParameters(final ParameterExpandBar viewer, final LayeredDisplayView view) {
		final EditorsGroup contents = createContentsComposite(viewer);
		final IDisplaySurface ds = view.getDisplaySurface();
		final IScope scope = ds.getScope();
		final LayeredDisplayData data = ds.getData();

		EditorFactory.create(scope, contents, "FreeFly Camera", !data.isArcBallCamera(),
				(EditorListener<Boolean>) val -> {
					ds.runAndUpdate(() -> {
						data.setArcBallCamera(!val);
					});

				});
		final boolean cameraLocked = data.cameraInteractionDisabled();
		EditorFactory.create(scope, contents, "Lock camera:", cameraLocked, (EditorListener<Boolean>) newValue -> {
			preset.setActive(!newValue);
			cameraPos.setActive(!newValue);
			cameraTarget.setActive(!newValue);
			cameraOrientation.setActive(!newValue);
			zoom.setActive(!newValue);
			data.disableCameraInteractions(newValue);
		});

		preset = EditorFactory.choose(scope, contents, "Preset camera:", "Choose...", view.getCameraNames(),
				(EditorListener<String>) newValue -> {
					if (newValue.isEmpty()) return;
					data.setPresetCamera(newValue);
					ds.updateDisplay(true);
				});

		cameraPos = EditorFactory.create(scope, contents, "Position:", data.getCameraPos(),
				(EditorListener<GamaPoint>) newValue -> {
					data.setCameraPos(newValue);
					ds.updateDisplay(true);
				});
		cameraTarget = EditorFactory.create(scope, contents, "Target:", data.getCameraTarget(),
				(EditorListener<GamaPoint>) newValue -> {
					data.setCameraLookPos(newValue);
					ds.updateDisplay(true);
				});
		cameraOrientation = EditorFactory.create(scope, contents, "Orientation:", data.getCameraOrientation(),
				(EditorListener<GamaPoint>) newValue -> {
					data.setCameraOrientation(newValue);
					ds.updateDisplay(true);
				});
		preset.setActive(!cameraLocked);
		cameraPos.setActive(!cameraLocked);
		cameraTarget.setActive(!cameraLocked);
		cameraOrientation.setActive(!cameraLocked);
		zoom.setActive(!cameraLocked);
		data.addListener((p, v) -> {
			switch (p) {
				case CAMERA_POS:
					cameraPos.getParam().setValue(scope, data.getCameraPos());
					cameraPos.updateWithValueOfParameter();
					copyCameraAndKeystoneDefinition(scope, data);
					break;
				case CAMERA_TARGET:
					cameraTarget.getParam().setValue(scope, data.getCameraTarget());
					cameraTarget.updateWithValueOfParameter();
					copyCameraAndKeystoneDefinition(scope, data);
					break;
				case CAMERA_ORIENTATION:
					cameraOrientation.getParam().setValue(scope, data.getCameraOrientation());
					cameraOrientation.updateWithValueOfParameter();
					copyCameraAndKeystoneDefinition(scope, data);
					break;
				case CAMERA_PRESET:
					preset.getParam().setValue(scope, "Choose...");
					preset.updateWithValueOfParameter();
					copyCameraAndKeystoneDefinition(scope, data);
					break;

				default:
					;
			}
		});
		final Label l = new Label(contents, SWT.None);
		l.setText("");
		final Button copy = new Button(contents, SWT.PUSH);
		copy.setText("Copy as facets");
		copy.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		copy.setToolTipText(
				"Copy the definition of the camera properties to the clipboard in a format suitable for pasting them in the definition of a display in GAML");
		copy.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String text = cameraDefinitionToCopy();
				WorkbenchHelper.copy(text);
			}

		});
		createItem(viewer, "Camera", null, contents);

	}

	private void fillKeystoneParameters(final ParameterExpandBar viewer, final LayeredDisplayView view) {
		final EditorsGroup contents = createContentsComposite(viewer);
		final IDisplaySurface ds = view.getDisplaySurface();
		final IScope scope = ds.getScope();
		final LayeredDisplayData data = ds.getData();

		final PointEditor[] point = new PointEditor[4];
		final ICoordinates points = data.getKeystone();
		int i = 0;
		for (@SuppressWarnings ("unused") final GamaPoint p : points) {
			final int j = i;
			i++;
			point[j] = EditorFactory.create(scope, contents, "Point " + j + ":", data.getKeystone().at(j).clone(),
					(EditorListener<GamaPoint>) newValue -> {
						data.getKeystone().at(j).setLocation(newValue);
						data.setKeystone(data.getKeystone());
						ds.updateDisplay(true);
					});
		}

		data.addListener((p, v) -> {
			switch (p) {
				case KEYSTONE:
					for (int k = 0; k < 4; k++) {
						point[k].getParam().setValue(scope, data.getKeystone().at(k));
						point[k].updateWithValueOfParameter();
					}
					data.setAntialias(data.isKeystoneDefined());
					copyCameraAndKeystoneDefinition(scope, data);
					break;
				default:
					;
			}

		});
		final Label l = new Label(contents, SWT.None);
		l.setText("");
		final Button copy = new Button(contents, SWT.PUSH);
		copy.setText("Copy as facet");
		copy.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
		copy.setToolTipText(
				"Copy the definition of the keystone values to the clipboard in a format suitable for pasting them in the definition of a display in GAML");
		copy.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String text = keystoneDefinitionToCopy(scope, data);
				WorkbenchHelper.copy(text);
			}

		});
		createItem(viewer, "Keystone", null, contents);
	}

	private void fillGeneralParameters(final ParameterExpandBar viewer, final LayeredDisplayView view) {
		final EditorsGroup contents = createContentsComposite(viewer);
		final IDisplaySurface ds = view.getDisplaySurface();
		final IScope scope = ds.getScope();
		final LayeredDisplayData data = ds.getData();
		final BooleanEditor antialias = EditorFactory.create(scope, contents, "Antialias:", data.isAntialias(),
				(EditorListener<Boolean>) newValue -> {
					data.setAntialias(newValue);
					ds.updateDisplay(true);
				});

		final ColorEditor background = EditorFactory.create(scope, contents, "Background:", data.getBackgroundColor(),
				(EditorListener<Color>) newValue -> {
					data.setBackgroundColor(new GamaColor(newValue));
					ds.updateDisplay(true);
				});
		EditorFactory.create(scope, contents, "Highlight:", data.getHighlightColor(),
				(EditorListener<Color>) newValue -> {
					data.setHighlightColor(new GamaColor(newValue));
					ds.updateDisplay(true);
				});
		zoom = EditorFactory.create(scope, contents, "Zoom (%):", "", (int) (data.getZoomLevel() * 100), 0, null, 1,
				(EditorListener<Integer>) newValue -> {
					data.setZoomLevel(newValue.doubleValue() / 100d, true, false);
					ds.updateDisplay(true);
				});

		if (view.isOpenGL()) {
			rotate = EditorFactory.create(scope, contents, "Z-axis rotation:", data.getCurrentRotationAboutZ(), null,
					null, 0.1, false, (EditorListener<Double>) newValue -> {
						data.setZRotationAngle(newValue);
						// ds.updateDisplay(true);
					});
			EditorFactory.create(scope, contents, "Continuous rotation", data.isContinuousRotationOn(),
					(EditorListener<Boolean>) val -> {
						ds.runAndUpdate(() -> {
							data.setContinuousRotation(val);

						});

					});
		}
		createItem(viewer, "General", null, contents);

		ds.getData().addListener((p, v) -> {
			switch (p) {
				case ZOOM:
					zoom.getParam().setValue(scope, (int) (data.getZoomLevel() * 100));
					zoom.updateWithValueOfParameter();
					break;
				case BACKGROUND:
					background.getParam().setValue(scope, data.getBackgroundColor());
					background.updateWithValueOfParameter();
					break;
				case ROTATION:
					if (rotate != null) {
						rotate.getParam().setValue(scope, (double) v);
						rotate.updateWithValueOfParameter();
					}
					break;
				case ANTIALIAS:
					antialias.getParam().setValue(scope, data.isAntialias());
					antialias.updateWithValueOfParameter();
					break;
				default:
					;

			}

		});

	}

	public static void createItem(final ParameterExpandBar viewer, final String name, final Object data,
			final Composite contents) {
		final ParameterExpandItem i = new ParameterExpandItem(viewer, data, SWT.None, null);
		i.setText(name);
		contents.pack(true);
		contents.layout();
		i.setControl(contents);
		i.setHeight(contents.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		i.setExpanded(false);
	}

	public static EditorsGroup createContentsComposite(final ParameterExpandBar viewer) {

		// contents.setBackground(IGamaColors.WHITE.color());
		// final GridLayout layout = new GridLayout(2, false);
		// layout.verticalSpacing = 0;
		// contents.setLayout(layout);
		return new EditorsGroup(viewer, SWT.NONE);
	}

	private void fillLayerParameters(final ParameterExpandBar viewer, final ILayer layer,
			final LayeredDisplayView view) {
		if (layer.isControllable()) {
			final EditorsGroup contents = createContentsComposite(viewer);
			fill(contents, layer, view.getDisplaySurface());
			createItem(viewer, "Layer " + layer.getName(), layer, contents);
		}
	}

	public void fill(final EditorsGroup compo, final ILayer layer, final IDisplaySurface container) {

		final ILayerStatement definition = layer.getDefinition();

		EditorFactory.create(container.getScope(), compo, "Transparency:",
				layer.getData().getTransparency(container.getScope()), 0.0, 1.0, 0.1, false, newValue -> {
					layer.getData().setTransparency(newValue);
					updateIfPaused(layer, container);
				});
		EditorFactory.create(container.getScope(), compo, "Position:", layer.getData().getPosition(),
				(EditorListener<GamaPoint>) newValue -> {
					layer.getData().setPosition(newValue);
					updateIfPaused(layer, container);
				});
		EditorFactory.create(container.getScope(), compo, "Size:", layer.getData().getSize(),
				(EditorListener<GamaPoint>) newValue -> {
					layer.getData().setSize(newValue);
					updateIfPaused(layer, container);
				});

		switch (definition.getType(container.getOutput())) {

			case GRID: {
				EditorFactory.create(container.getScope(), compo, "Draw grid:",
						((GridLayer) layer).getData().drawLines(), (EditorListener<Boolean>) newValue -> {
							((GridLayer) layer).getData().setDrawLines(newValue);
							updateIfPaused(layer, container);
						});
				break;
			}
			case AGENTS: {
				IExpression expr = null;
				if (definition instanceof AgentLayerStatement) {
					expr = ((AgentLayerStatement) definition).getFacet(IKeyword.VALUE);
				}
				if (expr != null) {
					EditorFactory.createExpression(container.getScope(), compo, "Agents:", expr, newValue -> {
						((AgentLayerStatement) definition).setAgentsExpr(newValue);
						updateIfPaused(layer, container);
					}, Types.LIST);
				}
				break;
			}
			case SPECIES: {
				EditorFactory.choose(container.getScope(), compo, "Aspect:",
						((SpeciesLayerStatement) definition).getAspectName(),
						((SpeciesLayerStatement) definition).getAspects(), newValue -> {
							((SpeciesLayerStatement) definition).setAspect(newValue);
							updateIfPaused(layer, container);
						});
				break;
			}
			case IMAGE: {
				if (definition instanceof ImageLayerStatement) {
					EditorFactory.create(container.getScope(), compo, "Image:",
							((ImageLayer) layer).getImageFileName(GAMA.getRuntimeScope()), false, newValue -> {
								((ImageLayer) layer).setImageFileName(GAMA.getRuntimeScope(), newValue);
								updateIfPaused(layer, container);
							});
				}
				break;

			}

			case CHART: {
				final Button b = new Button(compo, SWT.PUSH);
				b.setText("Properties");
				b.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
				b.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						// FIXME Editor not working for the moment
						final Point p = b.toDisplay(b.getLocation());
						p.y = p.y + 30;
						final SWTChartEditor editor = new SWTChartEditor(WorkbenchHelper.getDisplay(),
								((ChartLayerStatement) definition).getChart(), p);
						editor.open();
						updateIfPaused(layer, container);
					}

				});
				final Button save = new Button(compo, SWT.PUSH);
				final boolean enabled = ((ChartLayerStatement) definition).getDataSet().keepsHistory();
				save.setText(enabled ? "Save..." : "No history");
				save.setLayoutData(new GridData(SWT.END, SWT.FILL, false, false));
				save.setToolTipText(
						"Save the chart data as a CSV file when memorization of values is enabled in the preferences or via the 'memorize:' facet");
				save.setEnabled(enabled);
				if (enabled) {
					save.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(final SelectionEvent e) {
							((ChartLayerStatement) definition).saveHistory();
						}

					});
				}
				break;
			}
			default:
				break;

		}

	}

	private void copyCameraAndKeystoneDefinition(final IScope scope, final LayeredDisplayData data) {
		if (!GamaPreferences.Displays.OPENGL_CLIPBOARD_CAM.getValue()) return;
		final String toCopy = cameraDefinitionToCopy() + " " + Strings.LN
				+ (data.isKeystoneDefined() ? keystoneDefinitionToCopy(scope, data) : "");
		WorkbenchHelper.copy(toCopy);
	}

	private String cameraDefinitionToCopy() {
		StringBuilder text = new StringBuilder(IKeyword.CAMERA_LOCATION).append(": ").append(
				new GamaPoint(cameraPos.getCurrentValue()).yNegated().withPrecision(4).serialize(false));
		text.append(" ").append(IKeyword.CAMERA_TARGET).append(": ")
				.append(new GamaPoint(cameraTarget.getCurrentValue()).yNegated().withPrecision(4)
						.serialize(false));
		text.append(" ").append(IKeyword.CAMERA_ORIENTATION).append(": ").append(
				new GamaPoint(cameraOrientation.getCurrentValue()).withPrecision(4).serialize(false));
		return text.toString();
	}

	private String keystoneDefinitionToCopy(final IScope scope, final LayeredDisplayData data) {
		final IList<GamaPoint> pp = GamaListFactory.create(scope, Types.POINT, data.getKeystone().toCoordinateArray());
		return IKeyword.KEYSTONE + ": " + pp.serialize(false);
	}
}