/*********************************************************************************************
 *
 * 'ReverseOperators.java, in plugin ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.ext.serialize.gaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import gama.common.interfaces.IKeyword;
import gama.common.util.FileUtils;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.no_test;
import gama.core.dev.annotations.GamlAnnotations.operator;
import gama.core.dev.utils.DEBUG;
import gama.ext.serialize.factory.StreamConverter;
import gama.ext.serialize.gamaType.converters.ConverterScope;
import gama.kernel.experiment.ExperimentAgent;
import gama.kernel.simulation.SimulationAgent;
import gama.metamodel.agent.IAgent;
import gama.metamodel.agent.SavedAgent;
import gama.runtime.IScope;

public class ReverseOperators {

	static {
		DEBUG.OFF();
	}

	@operator (
			value = "serialize")
	@doc (
			value = "It serializes any object, i.e.transforms it into a string.",
			see =  "serialize_agent")
	@no_test()
	public static String serialize(final IScope scope, final Object o) {
		DEBUG.OUT("**** Serialize Object ****");
		return StreamConverter.convertObjectToStream(scope, o);
	}

	@operator (
			value = "unserialize")
	@doc (
			value = "",
			deprecated = "Still in alpha version, do not use it.")
	public static Object unserialize(final IScope scope, final String s) {
		DEBUG.OUT("**** unSerialize Object ****");
		return StreamConverter.convertStreamToObject(scope, s);
	}

	@operator (
			value = "serialize_agent")
	@doc (
			value = "searializes an agent (i.e. transforms into a string value).",
			comment = "As a simulation is a particular agent, it can be used to serialize a simulation and save it.",
			see = "serialize")
	@no_test
	public static String serializeAgent(final IScope scope, final IAgent agent) {
		return StreamConverter.convertObjectToStream(scope, new SavedAgent(scope, agent));
	}

	@operator (
			value = "restore_simulation_from_file")
	@doc (
			value = "Restores a simulation from a saved simulation file.", 
			comment = "This operator should be used in a reflex of an experiment and it will remove the current simulation and replace it  by the new restored simulation",
			see = "restore_simulation")
	@no_test
	public static int unSerializeSimulationFromFile(final IScope scope, final GamaSavedSimulationFile file) {
		return unSerializeSimulationFromXML(scope, file.getBuffer().get(0));
	}

	@operator (
			value = "restore_simulation")
	@doc (
			value = "restores a simulation from a string value containing a serialized simulation.", 
			comment = "This operator should be used in a reflex of an experiment and it will remove the current simulation and replace it by the new restored simulation",
			see = "restore_simulation_from_file")
	@no_test
	public static int unSerializeSimulationFromXML(final IScope scope, final String simul) {
		final ConverterScope cScope = new ConverterScope(scope);
		final XStream xstream = StreamConverter.loadAndBuild(cScope);

		final SavedAgent saveAgt = (SavedAgent) xstream.fromXML(simul);
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgent = expAgt.getSimulation();

		simAgent.updateWith(scope, saveAgt);

		return 1;
	}

	// TODO : This should become a part of the save statement 
	@operator (
			value = "save_agent")
	@doc (
			value = "saves an agent in a file specified by its path",
			deprecated = "use the save statement instead.")
	@no_test
	public static int saveAgent(final IScope scope, final IAgent agent, final String pathname) {
		final String path = FileUtils.constructAbsoluteFilePath(scope, pathname, false);

		final String serializedAgent = serializeAgent(scope, agent);

		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgt = expAgt.getSimulation();
		final int savedCycle = simAgt.getClock().getCycle();
		final String savedModel = expAgt.getModel().getFilePath();
		final String savedExperiment = (String) expAgt.getSpecies().getFacet(IKeyword.NAME).value(scope);

		FileWriter fw = null;
		try {
			if (path.equals("")) { return -1; }
			
			final File f = new File(path);
			
			final File parent = f.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
									
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f);

			// Write the Metadata
			fw.write(savedModel + System.lineSeparator());
			fw.write(savedExperiment + System.lineSeparator());
			fw.write(savedCycle + System.lineSeparator());

			// Write the serializedAgent
			fw.write(serializedAgent);
			fw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@operator (
			value = "save_simulation")
	@doc (
			value = "saves the current simulation in a  given file",
			comment = "About to be deprecated, the save statement should be used instead.")
	@no_test
	public static int saveSimulation(final IScope scope, final String pathname) {
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgt = expAgt.getSimulation();

		return saveAgent(scope, simAgt, pathname);
	}

	// TODO to remove when possible
	@operator (
			value = "serialize_network")
	@doc (
			value = "[For network purpose] It serializes any object, i.e. transform it into a string.",
			deprecated = "Still in alpha version, do not use it.")
	public static String serializeNetwork(final IScope scope, final Object o) {
		DEBUG.OUT("**** Serialize Object ****");
		return StreamConverter.convertNetworkObjectToStream(scope, o);
	}

	@operator (
			value = "unserialize_network")
	@doc (
			value = "[For network purpose]",
			deprecated = "Still in alpha version, do not use it.")
	public static Object unserializeNetwork(final IScope scope, final String s) {
		DEBUG.OUT("**** unSerialize Object ****");
		return StreamConverter.convertNetworkStreamToObject(scope, s);
	}
	// END TODO

}