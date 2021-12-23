/*******************************************************************************************************
 *
 * SyntaxErrorsView.java, in gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.modeling.views;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISources;
import org.eclipse.ui.internal.views.markers.ConfigureContentsDialogHandler;
import org.eclipse.ui.views.markers.MarkerSupportView;

import gama.common.preferences.GamaPreferences;
import gama.common.preferences.IPreferenceChangeListener.IPreferenceAfterChangeListener;
import gama.ui.base.commands.TestsRunner;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.toolbar.GamaToolbar2;
import gama.ui.base.toolbar.GamaToolbarFactory;
import gama.ui.base.toolbar.IToolbarDecoratedView;
import gama.ui.base.utils.WorkbenchHelper;

/**
 * The Class SyntaxErrorsView.
 */
public class SyntaxErrorsView extends MarkerSupportView implements IToolbarDecoratedView {

	/** The parent. */
	protected Composite parent;
	
	/** The toolbar. */
	protected GamaToolbar2 toolbar;

	/** The info action. */
	ToolItem warningAction, infoAction;
	
	/** The listener. */
	final BuildPreferenceChangeListener listener;

	/**
	 * Instantiates a new syntax errors view.
	 */
	public SyntaxErrorsView() {
		super("gama.ui.modeling.error.generator");
		listener = new BuildPreferenceChangeListener(this);
		GamaPreferences.Modeling.WARNINGS_ENABLED.addChangeListener(listener);
		GamaPreferences.Modeling.INFO_ENABLED.addChangeListener(listener);
	}

	@Override
	public void createPartControl(final Composite compo) {
		this.parent = GamaToolbarFactory.createToolbars(this, compo);
		super.createPartControl(parent);
	}

	@Override
	public void dispose() {
		super.dispose();
		GamaPreferences.Modeling.WARNINGS_ENABLED.removeChangeListener(listener);
		GamaPreferences.Modeling.INFO_ENABLED.removeChangeListener(listener);
	}

	/**
	 * The listener interface for receiving buildPreferenceChange events.
	 * The class that is interested in processing a buildPreferenceChange
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addBuildPreferenceChangeListener<code> method. When
	 * the buildPreferenceChange event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see BuildPreferenceChangeEvent
	 */
	public static class BuildPreferenceChangeListener implements IPreferenceAfterChangeListener<Boolean> {

		/** The view. */
		SyntaxErrorsView view;

		/**
		 * Instantiates a new builds the preference change listener.
		 *
		 * @param v the v
		 */
		BuildPreferenceChangeListener(final SyntaxErrorsView v) {
			view = v;
		}

		/**
		 * @see gama.common.preferences.IPreferenceChangeListener#afterValueChange(java.lang.Object)
		 */
		@Override
		public void afterValueChange(final Boolean newValue) {
			build();
			view.checkActions();
		}
	}

	/**
	 * Check actions.
	 */
	void checkActions() {
		if (warningAction != null) { warningAction.setSelection(GamaPreferences.Modeling.WARNINGS_ENABLED.getValue()); }
		if (infoAction != null) { infoAction.setSelection(GamaPreferences.Modeling.INFO_ENABLED.getValue()); }
	}

	@Override
	protected void setContentDescription(final String description) {
		toolbar.status((Image) null, description, e -> openFilterDialog(), IGamaColors.BLUE, SWT.LEFT);
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;

		warningAction = tb.check("build.warnings2", "", "Toggle display of warning markers", e -> {
			final boolean b = ((ToolItem) e.widget).getSelection();
			GamaPreferences.Modeling.WARNINGS_ENABLED.set(b).save();
		}, SWT.RIGHT);
		warningAction.setSelection(GamaPreferences.Modeling.WARNINGS_ENABLED.getValue());

		infoAction = tb.check("build.infos2", "", "Toggle display of information markers", e -> {
			final boolean b = ((ToolItem) e.widget).getSelection();
			GamaPreferences.Modeling.INFO_ENABLED.set(b).save();
		}, SWT.RIGHT);
		infoAction.setSelection(GamaPreferences.Modeling.INFO_ENABLED.getValue());

		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button("build.all2", "", "Clean and validate all projects", e -> {
			build();
		}, SWT.RIGHT);

		tb.button("test.run2", "", "Run all tests", e -> TestsRunner.start(), SWT.RIGHT);

	}

	/**
	 * Open filter dialog.
	 */
	void openFilterDialog() {
		final IEvaluationContext ec = new EvaluationContext(null, this);
		ec.addVariable(ISources.ACTIVE_PART_NAME, this);
		final ExecutionEvent ev = new ExecutionEvent(null, new HashMap<>(), this, ec);
		new ConfigureContentsDialogHandler().execute(ev);
	}

	/**
	 * Do build.
	 *
	 * @param monitor the monitor
	 */
	static private void doBuild(final IProgressMonitor monitor) {
		try {
			ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);

			// monitor.beginTask("Cleaning and building entire workspace", size);
			// for (final IProject p : projects) {
			// if (p.exists() && p.isAccessible()) {
			// monitor.subTask("Building " + p.getName());
			// p.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
			// monitor.worked(1);
			// }
			// }

		} catch (final CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds the.
	 */
	static void build() {

		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(WorkbenchHelper.getShell());
		dialog.setBlockOnOpen(false);
		dialog.setCancelable(false);
		dialog.setOpenOnRun(true);
		try {
			dialog.run(true, false, SyntaxErrorsView::doBuild);
		} catch (InvocationTargetException | InterruptedException e1) {
			e1.printStackTrace();
		}
	}
}