/*******************************************************************************************************
 *
 * ModelLibraryRunner.java, in gama.core.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.core.headless.batch.validation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;

import com.google.common.collect.Multimap;
import com.google.inject.Injector;

import gama.core.application.bundles.GamaBundleLoader;
import gama.core.dev.utils.DEBUG;
import gama.core.headless.batch.AbstractModelLibraryRunner;
import gama.core.headless.core.Experiment;
import gama.core.headless.core.HeadlessSimulationLoader;
import gama.core.lang.validation.GamlModelBuilder;
import gama.kernel.experiment.IExperimentPlan;
import gama.kernel.model.IModel;
import gaml.compilation.GamlCompilationError;
import gaml.descriptions.ModelDescription;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelLibraryRunner.
 */
public class ModelLibraryRunner extends AbstractModelLibraryRunner {

	/** The instance. */
	private static ModelLibraryRunner instance;

	/**
	 * Instantiates a new model library runner.
	 */
	private ModelLibraryRunner() {
		DEBUG.ON();
	}

	/**
	 * Start.
	 *
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public int start() throws IOException {
		final Injector injector = HeadlessSimulationLoader.getInjector();
		final GamlModelBuilder builder = createBuilder(injector);
		final int[] count = { 0 };
		final int[] code = { 0, 0 };
		final Multimap<Bundle, String> plugins = GamaBundleLoader.getPluginsWithModels();
		final List<URL> allURLs = new ArrayList<>();
		for (final Bundle bundle : plugins.keySet()) {
			for (final String entry : plugins.get(bundle)) {
				final Enumeration<URL> urls = bundle.findEntries(entry, "*", true);
				if (urls != null) {
					while (urls.hasMoreElements()) {
						final URL url = urls.nextElement();
						if (isModel(url)) {
							final URL resolvedFileURL = FileLocator.toFileURL(url);
							allURLs.add(resolvedFileURL);
						}
					}
				}
			}
		}
		builder.loadURLs(allURLs);
		// allURLs.forEach(u -> validate(builder, count, code, u));
		final Map<String, Exception> errors = new HashMap<>();
		allURLs.forEach(u -> validateAndRun(builder, errors, count, code, u, true, 1));

		DEBUG.OUT("" + count[0] + " GAMA models compiled in built-in library and plugins. " + code[0]
				+ " compilation errors found");

		DEBUG.SECTION("SUMMARY");

		errors.forEach((name, ex) -> DEBUG.OUT(name + " = " + ex.toString()));

		DEBUG.SECTION("SUMMARY");

		// code[1] = code[0];
		// code[0] = 0;
		// count[0] = 0;
		// final Multimap<Bundle, String> tests = GamaBundleLoader.getPluginsWithTests();
		// allURLs = new ArrayList<>();
		// for (final Bundle bundle : tests.keySet()) {
		// for (final String entry : tests.get(bundle)) {
		// final Enumeration<URL> urls = bundle.findEntries(entry, "*", true);
		// if (urls != null)
		// while (urls.hasMoreElements()) {
		// final URL url = urls.nextElement();
		// if (isModel(url)) {
		// final URL resolvedFileURL = FileLocator.toFileURL(url);
		// allURLs.add(resolvedFileURL);
		// }
		// }
		// }
		// }
		// builder.loadURLs(allURLs);
		//
		// allURLs.forEach(u -> validate(builder, count, code, u));
		//
		// DEBUG.OUT("" + count[0] + " GAMA tests compiled in built-in library and plugins. " + code[0]
		// + " compilation errors found");
		//
		// DEBUG.OUT(code[0] + code[1]);
		return code[0] + code[1];
	}

	// private void validate(GamlModelBuilder builder, final int[] countOfModelsValidated, final int[] returnCode,
	// final URL pathToModel) {
	// final List<GamlCompilationError> errors = new ArrayList<>();
	//// log("Compiling " + pathToModel.getFile());
	// builder.compile(pathToModel, errors);
	// countOfModelsValidated[0]++;
	// errors.stream().filter(e -> e.isError()).forEach(e -> {
	//// log("Error in " + e.getURI().lastSegment() + ": " + e);
	// DEBUG.OUT(
	// "Error in " + e.getURI() + ":\n " + e.toString() + " \n " + e.getStatement().toString() + "\n");
	// returnCode[0]++;
	// });
	// }

	/**
	 * Validate and run.
	 *
	 * @param builder the builder
	 * @param executionErrors the execution errors
	 * @param countOfModelsValidated the count of models validated
	 * @param returnCode the return code
	 * @param pathToModel the path to model
	 * @param expGUIOnly the exp GUI only
	 * @param nbCycles the nb cycles
	 */
	private void validateAndRun(final GamlModelBuilder builder, final Map<String, Exception> executionErrors,
			final int[] countOfModelsValidated, final int[] returnCode, final URL pathToModel, final boolean expGUIOnly,
			final int nbCycles) {
		if (pathToModel.toString().contains("Database")) return;
		DEBUG.PAD("", 80, '=');

		final List<GamlCompilationError> errors = new ArrayList<>();
		final IModel mdl = builder.compile(pathToModel, errors);

		countOfModelsValidated[0]++;
		errors.stream().filter(GamlCompilationError::isError).forEach(e -> {
			DEBUG.OUT("Error in " + e.getURI() + ":\n " + e.toString() + " \n " + e.getStatement().toString() + "\n");
			returnCode[0]++;
		});

		Experiment experiment = null;
		try {
			experiment = new Experiment(mdl);
		} catch (final Exception ex) {
			executionErrors.put(pathToModel.getPath() + "\n", ex);
		}

		for (final String expName : ((ModelDescription) mdl.getDescription()).getExperimentNames()) {
			final IExperimentPlan exp = mdl.getExperiment(expName);
			if (!exp.isBatch() || !expGUIOnly) {
				DEBUG.OUT("*********** Run experiment " + exp + " from model: " + mdl.getName());
				try {
					experiment.setup(expName, 0.1);
					for (int i = 0; i < nbCycles; i++) {
						experiment.step();
						DEBUG.OUT("****** Ap step()");
					}
				} catch (final gama.ext.libs.webb.WebbException ex1) {
					DEBUG.OUT("gama.ext.libs.webb.WebbException");
				} catch (final Exception ex) {
					ex.printStackTrace();
					executionErrors.put(pathToModel.getPath() + "\n" + expName, ex);
				}
			}
		}

	}

	/**
	 * Gets the single instance of ModelLibraryRunner.
	 *
	 * @return single instance of ModelLibraryRunner
	 */
	public static ModelLibraryRunner getInstance() {
		if (instance == null) { instance = new ModelLibraryRunner(); }
		return instance;
	}
}