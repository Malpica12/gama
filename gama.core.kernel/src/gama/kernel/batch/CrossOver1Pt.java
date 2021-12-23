/*******************************************************************************************************
 *
 * CrossOver1Pt.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.kernel.batch;

import java.util.HashSet;
import java.util.Set;

import gama.runtime.IScope;

/**
 * The Class CrossOver1Pt.
 */
public class CrossOver1Pt implements CrossOver {

	/**
	 * Instantiates a new cross over 1 pt.
	 */
	public CrossOver1Pt() {}

	@Override
	public Set<Chromosome> crossOver(final IScope scope, final Chromosome parent1, final Chromosome parent2) {
		final Set<Chromosome> children = new HashSet<>();
		final int nbGenes = parent2.getGenes().length;
		if (nbGenes == 1) { return children; }
		int cutPt = 0;
		if (nbGenes > 2) {
			cutPt = scope.getRandom().between(0, nbGenes - 2);
		}
		final Chromosome child1 = new Chromosome(parent1);
		final Chromosome child2 = new Chromosome(parent2);
		for (int i = 0; i < cutPt; i++) {
			final Object val1 = child1.getGenes()[i];
			child1.getGenes()[i] = child2.getGenes()[i];
			child2.getGenes()[i] = val1;
		}
		children.add(child1);
		children.add(child2);
		return children;
	}

}