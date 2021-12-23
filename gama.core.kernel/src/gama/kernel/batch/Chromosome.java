/*******************************************************************************************************
 *
 * Chromosome.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.kernel.batch;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import gama.kernel.experiment.IParameter;
import gama.kernel.experiment.ParametersSet;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.operators.Cast;
import gaml.types.IType;

// TODO: Auto-generated Javadoc
/**
 * The Class Chromosome.
 */
public class Chromosome implements Comparable<Chromosome> {

	/** The genes. */
	private Object[] genes;

	/** The phenotype. */
	private final String[] phenotype;

	/** The fitness. */
	private double fitness;

	/**
	 * Gets the genes.
	 *
	 * @return the genes
	 */
	public Object[] getGenes() {
		return genes;
	}

	/**
	 * Sets the genes.
	 *
	 * @param genes the new genes
	 */
	public void setGenes(final Object[] genes) {
		this.genes = genes;
	}

	/**
	 * Gets the fitness.
	 *
	 * @return the fitness
	 */
	public double getFitness() {
		return fitness;
	}

	/**
	 * Sets the fitness.
	 *
	 * @param fitness the new fitness
	 */
	public void setFitness(final double fitness) {
		this.fitness = fitness;
	}

	/**
	 * Instantiates a new chromosome.
	 *
	 * @param chromosome the chromosome
	 */
	public Chromosome(final Chromosome chromosome) {

		genes = new Object[chromosome.genes.length];
		phenotype = new String[chromosome.phenotype.length];

		for (int i = 0; i < genes.length; i++) {
			genes[i] = chromosome.genes[i];
			phenotype[i] = chromosome.phenotype[i];
		}

		fitness = chromosome.fitness;
	}

	/**
	 * Update.
	 *
	 * @param scope the scope
	 * @param solution the solution
	 */
	public void update(final IScope scope, final ParametersSet solution) {
		final int nb = this.getGenes().length;
		for (int i = 0; i < nb; i++) {
			final String var = getPhenotype()[i];
			genes[i] = Cast.asFloat(scope, solution.get(var));
		}
	}

	/**
	 * Instantiates a new chromosome.
	 *
	 * @param scope the scope
	 * @param variables the variables
	 * @param reInitVal the re init val
	 */
	public Chromosome(final IScope scope, final List<IParameter.Batch> variables, final boolean reInitVal) {
		genes = new Object[variables.size()];
		phenotype = new String[variables.size()];
		int cpt = 0;
		for (final IParameter.Batch var : variables) {
			if (reInitVal) {
				var.reinitRandomly(scope);
			}
			phenotype[cpt] = var.getName();
			if (var.getType().id() == IType.FLOAT) {
				genes[cpt] = Cast.asFloat(scope, var.value(scope));
			} else if (var.getType().id() == IType.INT) {
				genes[cpt] = Cast.asInt(scope, var.value(scope));
			} else {
				genes[cpt] = var.value(scope);
			}
			cpt++;
		}
	}

	/**
	 * Sets the gene.
	 *
	 * @param scope the scope
	 * @param var the var
	 * @param index the index
	 */
	public void setGene(final IScope scope, final IParameter.Batch var, final int index) {
		if (var.getType().id() == IType.FLOAT) {
			genes[index] = Cast.asFloat(scope, var.value(scope));
		} else if (var.getType().id() == IType.INT) {
			genes[index] = Cast.asInt(scope, var.value(scope));
		} else {
			genes[index] = 0;
		}
	}

	/**
	 * Convert to solution.
	 *
	 * @param scope the scope
	 * @param variables the variables
	 * @return the parameters set
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public ParametersSet convertToSolution(final IScope scope, final Collection<IParameter.Batch> variables)
			throws GamaRuntimeException {
		final ParametersSet sol = new ParametersSet(scope, variables, true);
		// TODO or false ???
		for (int i = 0; i < phenotype.length; i++) {
			sol.put(phenotype[i], genes[i]);
		}
		return sol;
	}

	/**
	 * Compare to.
	 *
	 * @param other the other
	 * @return the int
	 */
	@Override
	public int compareTo(final Chromosome other) {
		return Double.compare(this.fitness, other.fitness);
	}

	/**
	 * Gets the phenotype.
	 *
	 * @return the phenotype
	 */
	public String[] getPhenotype() {
		return phenotype;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(genes));
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if ((obj == null) || (getClass() != obj.getClass())) return false;
		final Chromosome other = (Chromosome) obj;
		if (!Arrays.equals(genes, other.genes)) return false;
		return true;
	}

}