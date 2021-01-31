/*********************************************************************************************
 *
 *
 * 'MoleExperiment.java', in plugin 'msi.gama.headless', is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.headless.core;

import msi.gama.headless.common.DataType;
import msi.gama.headless.job.ExperimentJob.OutputType;
import msi.gama.headless.job.ListenedVariable;
import msi.gama.kernel.model.IModel;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.FileOutput;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public class RichExperiment extends Experiment implements IRichExperiment {
	public RichExperiment(final IModel mdl) {
		super(mdl);
	}

	@Override
	public OutputType getTypeOf(final String name) {
		if (currentExperiment == null) { return OutputType.OUTPUT; }
		if (currentExperiment.hasVar(name)) { return OutputType.EXPERIMENT_ATTRIBUTE; }
		if (currentExperiment.getModel().getSpecies().hasVar(name)) { return OutputType.SIMULATION_ATTRIBUTE; }
		return OutputType.OUTPUT;
	}

	@Override
	public RichOutput getRichOutput(final ListenedVariable v) {
		final String parameterName = v.getName();
		if (currentSimulation.dead()) { return null; }
		final IOutput output =
				((AbstractOutputManager) currentSimulation.getOutputManager()).getOutputWithOriginalName(parameterName);
		if (output == null) {
			throw GamaRuntimeException.error("Output unresolved", currentExperiment.getExperimentScope());
		} 
		if (output instanceof LayeredDisplayOutput) {
			if(((LayeredDisplayOutput) output).getSurface().getWidth()!=v.width) {
				((LayeredDisplayOutput) output).getSurface().setSize(v.width, v.height);
				((LayeredDisplayOutput) output).getSurface().outputReloaded();
			}
		}
		output.update();

		Object val = null;
		DataType tpe = null;

		if (output instanceof MonitorOutput) {
			// ((SimulationAgent)
			// this.currentExperiment.getAgent().getSimulation()).getOutputManager().getOutputWithName(parameterName)
			val = ((MonitorOutput) output).getLastValue();
			if (val instanceof Integer) {
				tpe = DataType.INT;
			} else if (val instanceof Double) {
				tpe = DataType.INT;
			} else if (val instanceof String) {
				tpe = DataType.STRING;
			} else {
				tpe = DataType.UNDEFINED;
			}

		} else if (output instanceof LayeredDisplayOutput) {
			val = ((LayeredDisplayOutput) output).getImage(v.width, v.height);
			tpe = DataType.DISPLAY2D;
		} else if (output instanceof FileOutput) {
			val = ((FileOutput) output).getFile();
			tpe = DataType.DISPLAY2D;
		}
		return new RichOutput(parameterName, this.currentStep, val, tpe);
	}

	@Override
	public void dispose() {
		GAMA.closeExperiment(currentExperiment);
	}
}
