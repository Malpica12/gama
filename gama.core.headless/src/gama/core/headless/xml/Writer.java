/*********************************************************************************************
 * 
 * 
 * 'Writer.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package gama.core.headless.xml;

import gama.core.headless.core.*;
import gama.core.headless.job.ExperimentJob;
import gama.core.headless.job.ListenedVariable;

public interface Writer {

	public void writeSimulationHeader(ExperimentJob s);

	public void writeResultStep(long step, ListenedVariable[] vars);

	public void close();
}
