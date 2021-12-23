/*******************************************************************************************************
 *
 * ModelRunner.java, in gama.ui.modeling, is part of the source code of the GAMA modeling and simulation platform
 * (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.modeling.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;

import com.google.inject.Injector;
import com.google.inject.Singleton;

import gama.core.dev.utils.DEBUG;
import gama.core.lang.validation.GamlModelBuilder;
import gama.kernel.experiment.IExperimentPlan;
import gama.kernel.experiment.ParametersSet;
import gama.kernel.experiment.TestAgent;
import gama.kernel.model.IModel;
import gama.runtime.GAMA;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.ui.base.interfaces.IModelRunner;
import gama.ui.base.utils.WorkbenchHelper;
import gama.ui.modeling.internal.ModelingActivator;
import gama.ui.navigator.contents.WrappedGamaFile;
import gaml.compilation.GamlCompilationError;
import gaml.descriptions.ModelDescription;
import gaml.statements.test.TestExperimentSummary;
import gaml.statements.test.WithTestSummary;

/**
 * The class ModelRunner.
 *
 * @author drogoul
 * @since 19 juin 2016
 *
 */
@Singleton
public class ModelRunner extends AbstractServiceFactory implements IModelRunner {

	/**
	 * Edits the model internal.
	 *
	 * @param eObject
	 *            the e object
	 */
	private void editModelInternal(final Object eObject) {
		if (eObject instanceof URI) {
			final URI uri = (URI) eObject;
			final Injector injector = ModelingActivator.getInstance().getInjector("gama.core.lang.Gaml");
			final IURIEditorOpener opener = injector.getInstance(IURIEditorOpener.class);
			opener.open(uri, true);
		} else if (eObject instanceof EObject) {
			editModelInternal(EcoreUtil.getURI((EObject) eObject));
		} else if (eObject instanceof String) {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IFile file = workspace.getRoot().getFile(new Path((String) eObject));
			editModelInternal(file);
		} else if (eObject instanceof IFile) {
			final IFile file = (IFile) eObject;
			if (!file.exists()) {
				DEBUG.LOG("File " + file.getFullPath().toString() + " does not exist in the workspace");
				return;
			}
			try {
				IDE.openEditor(WorkbenchHelper.getPage(), file);
			} catch (final PartInitException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void editModel(final Object eObject) {
		WorkbenchHelper.asyncRun(() -> editModelInternal(eObject));
	}

	@SuppressWarnings ("unchecked")
	@Override
	public List<TestExperimentSummary> runHeadlessTests(final Object object) {
		// final StringBuilder sb = new StringBuilder();
		final IModel model = findModel(object);
		if (model == null) return null;
		final List<String> testExpNames = ((ModelDescription) model.getDescription()).getExperimentNames().stream()
				.filter(e -> model.getExperiment(e).isTest()).collect(Collectors.toList());
		if (testExpNames.isEmpty()) return null;
		final List<TestExperimentSummary> result = new ArrayList<>();
		for (final String expName : testExpNames) {
			final IExperimentPlan exp = GAMA.addHeadlessExperiment(model, expName, new ParametersSet(), null);
			if (exp != null) {
				exp.setHeadless(true);
				final TestAgent agent = (TestAgent) exp.getAgent();
				exp.getController().getScheduler().paused = false;
				agent.step(agent.getScope());
				result.add(((WithTestSummary<TestExperimentSummary>) agent).getSummary());
				GAMA.closeExperiment(exp);
			}
		}
		return result;
	}

	/**
	 * Find model.
	 *
	 * @param object
	 *            the object
	 * @return the i model
	 */
	private IModel findModel(final Object object) {
		if (object instanceof IModel) return (IModel) object;
		if (object instanceof WrappedGamaFile) return findModel(((WrappedGamaFile) object).getResource());
		if (object instanceof IFile) {
			final IFile file = (IFile) object;
			try {
				if (file.findMaxProblemSeverity(IMarker.PROBLEM, true,
						IResource.DEPTH_ZERO) == IMarker.SEVERITY_ERROR) {
					GAMA.getGui().error("Model " + file.getFullPath() + " has errors and cannot be launched");
					return null;
				}
			} catch (final CoreException e) {
				e.printStackTrace();
			}
			final URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
			return findModel(uri);
		}
		if (object instanceof URI) {
			final URI uri = (URI) object;
			final List<GamlCompilationError> errors = new ArrayList<>();
			final IModel model = GamlModelBuilder.getDefaultInstance().compile(uri, errors);
			if (model == null) {
				GAMA.getGui().error("File " + uri.lastSegment() + " cannot be built because of " + errors.size()
						+ " compilation errors");
			}
			return model;
		}
		if (object instanceof IXtextDocument) {
			final IXtextDocument doc = (IXtextDocument) object;
			IModel model = null;
			try {
				model = doc.readOnly(state -> GamlModelBuilder.getDefaultInstance().compile(state.getURI(), null));
			} catch (final GamaRuntimeException ex) {
				GAMA.getGui()
						.error("Experiment cannot be instantiated because of the following error: " + ex.getMessage());
			}
			return model;

		}
		return null;
	}

	@Override
	public void runModel(final Object object, final String exp) {
		final IModel model = findModel(object);
		if (model == null) return;
		GAMA.runGuiExperiment(exp, model);
	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return this;
	}

}