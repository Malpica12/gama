/*********************************************************************************************
 *
 * 'AdamsBashforthSolver.java, in plugin ummisco.gaml.extensions.maths, is part of the source code of the GAMA modeling
 * and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ext.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegrator;

import gama.util.IList;
import gama.util.IMap;

public class AdamsBashforthSolver extends Solver {

	public AdamsBashforthSolver(final int nSteps, final double minStep, final double maxStep,
			final double scalAbsoluteTolerance, final double scalRelativeTolerance,
			final IMap<String, IList<Double>> integrated_val) {
		super((minStep + maxStep) / 2,
				new AdamsBashforthIntegrator(nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance),
				integrated_val);
	}

}