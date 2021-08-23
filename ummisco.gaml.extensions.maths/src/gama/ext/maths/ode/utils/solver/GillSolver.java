/*********************************************************************************************
 *
 * 'GillSolver.java, in plugin ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.ext.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.GillIntegrator;

import gama.util.IList;
import gama.util.IMap;

public class GillSolver extends Solver {

	public GillSolver(final double step, final IMap<String, IList<Double>> integrated_val) {
		super(step, new GillIntegrator(step), integrated_val);
	}

}
