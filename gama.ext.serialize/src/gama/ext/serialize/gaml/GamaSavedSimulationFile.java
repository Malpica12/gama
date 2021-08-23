package gama.ext.serialize.gaml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import gama.common.geometry.Envelope3D;
import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.file;
import gama.kernel.experiment.ExperimentAgent;
import gama.kernel.simulation.SimulationAgent;
import gama.metamodel.agent.IAgent;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaListFactory;
import gama.util.IList;
import gama.util.file.GamaFile;
import gama.util.file.GamaFileMetaData;
import gaml.operators.Strings;
import gaml.statements.Facets;
import gaml.types.IContainerType;
import gaml.types.IType;
import gaml.types.Types;

@file (
		name = "saved_simulation",
		extensions = { "gsim", "gasim" },
		buffer_type = IType.LIST,
		buffer_content = IType.STRING,
		buffer_index = IType.INT,
		concept = { IConcept.FILE, IConcept.SAVE_FILE },
		doc = @doc ("Represents a saved simulation file. The internal contents is a string at index 0"))
// TODO : this type needs to be improved ....
@SuppressWarnings ({ "unchecked" })
public class GamaSavedSimulationFile extends GamaFile<IList<String>, String> {

	public static class SavedSimulationInfo extends GamaFileMetaData { // NO_UCD (unused code)

		public String savedModel;
		public String savedExperiment;
		public int savedCycle;

		public SavedSimulationInfo(final String fileName, final long modificationStamp) {
			super(modificationStamp);

			final File f = new File(fileName);
			final GamaSavedSimulationFile simulationFile =
					new GamaSavedSimulationFile(null, f.getAbsolutePath(), false);

			savedModel = simulationFile.getModelName();
			savedExperiment = simulationFile.getExperiment();
			savedCycle = simulationFile.getCycle();
		}

		public SavedSimulationInfo(final String propertyString) {
			super(propertyString);

			final String[] segments = split(propertyString);
			savedModel = segments[1];
			savedExperiment = segments[2];
			savedCycle = Integer.valueOf(segments[3]);
		}

		@Override
		public String getDocumentation() {
			final StringBuilder sb = new StringBuilder();
			sb.append("Model: ").append(savedModel).append(Strings.LN);
			sb.append("Experiment: ").append(savedExperiment).append(Strings.LN);
			sb.append("Cycle: ").append(savedCycle).append(Strings.LN);

			return sb.toString();
		}

		@Override
		public String getSuffix() {
			return "" + savedModel + " | " + "Experiment: " + savedExperiment + " | " + "Cycle: " + savedCycle;
		}

		@Override
		public void appendSuffix(final StringBuilder sb) {
			sb.append(savedModel).append(SUFFIX_DEL);
			sb.append("Experiment: ").append(savedExperiment).append(SUFFIX_DEL);
			sb.append("Cycle: ").append(savedCycle);
		}

		/**
		 * @return
		 */
		@Override
		public String toPropertyString() {
			return super.toPropertyString() + DELIMITER + savedModel + DELIMITER + savedExperiment + DELIMITER
					+ savedCycle;
		}
	}

	private int savedCycle;
	private String savedExperiment;
	private String savedModelPath;
	private String savedModelName;

	@doc ("Constructor for saved simulation files: read the metadata and content.")
	public GamaSavedSimulationFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		this(scope, pathName, true);
	}

	@doc ("Constructor for saved simulation files from a list of agents: it is used with aim of saving a simulation agent.")
	public GamaSavedSimulationFile(final IScope scope, final String pathName, final IList<IAgent> contents) {
		super(scope, pathName, null);
		final IAgent agent = contents.firstValue(scope);

		// Set first the metadata
		setMetadata(scope);

		// Set the buffer
		final String serializedAgent = ReverseOperators.serializeAgent(scope, agent);
		final IList<String> c = GamaListFactory.create();
		c.add(serializedAgent);

		setContents(c);
	}

	@doc ("Constructor for saved simulation files: read the metadata. If and only if the boolean operand is true, the content of the file is read.")
	public GamaSavedSimulationFile(final IScope scope, final String pathName, final boolean fillBuffer)
			throws GamaRuntimeException {
		super(scope, pathName);

		if (fillBuffer) {
			fillBuffer(scope);
		} else {
			metadataOnly(scope);
		}
	}

	private void setMetadata(final IScope scope) {
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgt = expAgt.getSimulation();
		savedCycle = simAgt.getClock().getCycle();
		savedModelPath = expAgt.getModel().getFilePath();
		savedExperiment = (String) expAgt.getSpecies().getFacet(IKeyword.NAME).value(scope);
		savedModelName = new File(savedModelPath).getName();
	}

	private void metadataOnly(final IScope scope) {
		try (BufferedReader in = new BufferedReader(new FileReader(getFile(scope)))) {
			readMetada(in);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	private void readMetada(final BufferedReader in) throws IOException {
		savedModelPath = in.readLine();
		savedExperiment = in.readLine();
		savedCycle = Integer.parseInt(in.readLine());

		savedModelName = new File(savedModelPath).getName();
	}

	@Override
	public IContainerType<?> getGamlType() {
		return Types.FILE.of(Types.INT, Types.STRING);
	}

	public String getModelPath() {
		return savedModelPath;
	}

	public String getModelName() {
		return savedModelName;
	}

	public String getExperiment() {
		return savedExperiment;
	}

	public int getCycle() {
		return savedCycle;
	}

	@Override
	public String _stringValue(final IScope scope) throws GamaRuntimeException {
		getContents(scope);
		final StringBuilder sb = new StringBuilder(getBuffer().length(scope) * 200);
		for (final String s : getBuffer().iterable(scope)) {
			sb.append(s).append(Strings.LN);
		}
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) { return; }
		try (BufferedReader in = new BufferedReader(new FileReader(getFile(scope)))) {
			final StringBuilder sb = new StringBuilder();

			// manage the metadata (and thus remove the first metadata lines)
			readMetada(in);

			// Continue with the core of the file
			String str = in.readLine();
			while (str != null) {
				sb.append(str);
				sb.append(System.lineSeparator());
				str = in.readLine();
			}
			final IList<String> contents = GamaListFactory.create(Types.STRING);
			contents.add(sb.toString());
			setBuffer(contents);
		} catch (final IOException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see msi.gama.util.GamaFile#flushBuffer()
	 */
	@Override
	protected void flushBuffer(final IScope scope, final Facets facets) throws GamaRuntimeException {
		if (getBuffer() != null && !getBuffer().isEmpty()) {
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(scope)))) {
				// Write the Metadata
				writer.append(savedModelPath).append(Strings.LN);
				writer.append(savedExperiment).append(Strings.LN);
				writer.append("" + savedCycle).append(Strings.LN);

				// Write the Buffer
				for (final String s : getBuffer()) {
					writer.append(s).append(Strings.LN);
				}
				writer.flush();
			} catch (final IOException e) {
				throw GamaRuntimeException.create(e, scope);
			}
		}

	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

}
