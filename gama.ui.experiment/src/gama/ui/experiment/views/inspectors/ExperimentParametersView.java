/*******************************************************************************************************
 *
 * ExperimentParametersView.java, in gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.experiment.views.inspectors;

import static gama.common.preferences.GamaPreferences.Displays.CORE_DISPLAY_LAYOUT;
import static gama.common.preferences.GamaPreferences.Displays.LAYOUTS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import gama.common.preferences.GamaPreferences;
import gama.common.ui.IGamaView;
import gama.common.ui.IGui;
import gama.kernel.experiment.IExperimentDisplayable;
import gama.kernel.experiment.IExperimentPlan;
import gama.kernel.experiment.ParametersSet;
import gama.kernel.simulation.SimulationAgent;
import gama.runtime.GAMA;
import gama.ui.base.resources.GamaIcons;
import gama.ui.base.resources.IGamaIcons;
import gama.ui.base.utils.WorkbenchHelper;
import gama.ui.experiment.commands.ArrangeDisplayViews;
import gama.ui.experiment.parameters.EditorsList;
import gama.ui.experiment.parameters.ExperimentsParametersList;
import gaml.operators.IUnits;
import gama.ui.base.toolbar.GamaToolbar2;

/**
 * The Class ExperimentParametersView.
 */
public class ExperimentParametersView extends AttributesEditorsView<String> implements IGamaView.Parameters {

	/** The Constant ID. */
	public static final String ID = IGui.PARAMETER_VIEW_ID;
	
	/** The Constant REVERT. */
	public final static int REVERT = 0;
	
	/** The experiment. */
	private IExperimentPlan experiment;

	@Override
	public void ownCreatePartControl(final Composite view) {
		final Composite intermediate = new Composite(view, SWT.NONE);
		// intermediate.setBackground(view.getBackground());
		final GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 5;
		intermediate.setLayout(parentLayout);
		// view.pack();
		// view.layout();
		setParentComposite(intermediate);
	}

	@Override
	public void addItem(final IExperimentPlan exp) {
		if (exp != null) {
			experiment = exp;
			if (!exp.hasParametersOrUserCommands()) return;
			reset();
			final List<IExperimentDisplayable> params = new ArrayList<>(exp.getParameters().values());
			params.addAll(exp.getExplorableParameters().values());
			params.addAll(exp.getUserCommands());
			Collections.sort(params);
			editors = new ExperimentsParametersList(exp.getAgent().getScope(), params);
			final String expInfo = "Model " + experiment.getModel().getDescription().getTitle() + " / "
					+ StringUtils.capitalize(experiment.getDescription().getTitle());
			this.setPartName(expInfo);
			displayItems();
		} else {
			experiment = null;
		}
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.button(GamaIcons.create(IGamaIcons.ACTION_REVERT).getCode(), "Revert parameter values",
				"Revert parameters to their initial values", e -> {
					final EditorsList<?> eds = editors;
					if (eds != null) { eds.revertToDefaultValue(); }
				}, SWT.RIGHT);
		tb.button("menu.add2", "Add simulation",
				"Add a new simulation (with the current parameters) to this experiment", e -> {
					final SimulationAgent sim =
							GAMA.getExperiment().getAgent().createSimulation(new ParametersSet(), true);
					if (sim == null) return;
					WorkbenchHelper.runInUI("", 0, m -> {
						if ("None".equals(CORE_DISPLAY_LAYOUT.getValue())) {
							ArrangeDisplayViews.execute(IUnits.split);
						} else {
							ArrangeDisplayViews.execute(LAYOUTS.indexOf(CORE_DISPLAY_LAYOUT.getValue()));
						}
					});
				}, SWT.RIGHT);

	}

	@Override
	public boolean addItem(final String object) {
		createItem(getParentComposite(), object, GamaPreferences.Runtime.CORE_EXPAND_PARAMS.getValue(), null);
		return true;
	}

	/**
	 * Gets the experiment.
	 *
	 * @return the experiment
	 */
	public IExperimentPlan getExperiment() {
		return experiment;
	}

	@Override
	public void stopDisplayingTooltips() {
		toolbar.wipe(SWT.LEFT, true);
	}

	@Override
	protected GamaUIJob createUpdateJob() {
		return null;
	}

	@Override
	protected boolean needsOutput() {
		return false;
	}

	/**
	 * Method handleMenu()
	 *
	 * @see gama.common.interfaces.ItemList#handleMenu(java.lang.Object, int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final String data, final int x, final int y) {
		return null;
	}

}
