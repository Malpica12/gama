/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.ExhaustiveSearch.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.kernel.batch;

import java.time.temporal.ChronoUnit;
import java.util.List;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.IConcept;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.example;
import gama.core.dev.annotations.GamlAnnotations.facet;
import gama.core.dev.annotations.GamlAnnotations.facets;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.core.dev.annotations.GamlAnnotations.usage;
import gama.kernel.experiment.IParameter;
import gama.kernel.experiment.ParametersSet;
import gama.metamodel.shape.GamaPoint;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.GamaDate;
import gaml.descriptions.IDescription;
import gaml.operators.Cast;
import gaml.types.GamaDateType;
import gaml.types.IType;

@symbol (
		name = { IKeyword.EXHAUSTIVE },
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH, IConcept.ALGORITHM })
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				internal = true,
				doc = @doc ("The name of the method. For internal use only")),
				@facet (
						name = IKeyword.MAXIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to maximize")),
				@facet (
						name = IKeyword.MINIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to minimize")),
				@facet (
						name = IKeyword.AGGREGATION,
						type = IType.LABEL,
						optional = true,
						values = { IKeyword.MIN, IKeyword.MAX },
						doc = @doc ("The aggregation method to use (either min or max)")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This is the standard batch method. The exhaustive mode is defined by default when there is no method element present in the batch section. It explores all the combination of parameter values in a sequential way. See [batch161 the batch dedicated page].",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the exhaustive statement uses `method exhaustive` instead of the expected `exhaustive name: id` : ",
				examples = { @example (
						value = "method exhaustive [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method exhaustive maximize: food_gathered;",
								isExecutable = false) }) })
public class ExhaustiveSearch extends ParamSpaceExploAlgorithm {

	public ExhaustiveSearch(final IDescription desc) {
		super(desc);
	}

	@Override
	public ParametersSet findBestSolution(final IScope scope) throws GamaRuntimeException {
		setBestFitness(null);
		testSolutions(scope, new ParametersSet(), 0);
		return getBestSolution();
	}

	private void testSolutions(final IScope scope, final ParametersSet sol, final int index)
			throws GamaRuntimeException {
		final List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		final ParametersSet solution = new ParametersSet(sol);
		if (variables.isEmpty()) {
			currentExperiment.launchSimulationsWithSolution(solution);
			return;
		}
		final IParameter.Batch var = variables.get(index);
		if (var.getAmongValue(scope) != null) {
			for (final Object val : var.getAmongValue(scope)) {
				solution.put(var.getName(), val);
				if (solution.size() == variables.size()) {
					currentExperiment.launchSimulationsWithSolution(solution);
				} else {
					testSolutions(scope, solution, index + 1);
				}
			}
		} else {
			switch (var.getType().id()) {
				case IType.INT:
					int intValue = Cast.asInt(scope, var.getMinValue(scope));
					int maxIntValue = Cast.asInt(scope, var.getMaxValue(scope));
					while (intValue <= maxIntValue) {
						solution.put(var.getName(), intValue);
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						intValue = intValue + Cast.asInt(scope, var.getStepValue(scope));
					}
					break;
				case IType.FLOAT:
					double floatValue = Cast.asFloat(scope, var.getMinValue(scope));
					double maxFloatValue = Cast.asFloat(scope, var.getMaxValue(scope));
					while (floatValue <= maxFloatValue) {
						solution.put(var.getName(), floatValue);
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						floatValue = floatValue + Cast.asFloat(scope, var.getStepValue(scope));
					}
					break;
				case IType.DATE:
					GamaDate dateValue = GamaDateType.staticCast(scope, var.getMinValue(scope), null, false);
					GamaDate maxDateValue = GamaDateType.staticCast(scope, var.getMaxValue(scope), null, false);
					while (dateValue.isSmallerThan(maxDateValue, false)) {
						solution.put(var.getName(), dateValue);
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						dateValue = dateValue.plus(Cast.asFloat(scope, var.getStepValue(scope)), ChronoUnit.SECONDS);
					}
					break;
				case IType.POINT:
					GamaPoint pointValue = Cast.asPoint(scope, var.getMinValue(scope));
					GamaPoint maxPointValue = Cast.asPoint(scope, var.getMaxValue(scope));
					while (pointValue.smallerThanOrEqualTo(maxPointValue)) {
						solution.put(var.getName(), pointValue);
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						pointValue = pointValue.plus(Cast.asPoint(scope, var.getStepValue(scope)));
					}
					break;
				default:
					double varValue = Cast.asFloat(scope, var.getMinValue(scope));
					while (varValue <= Cast.asFloat(scope, var.getMaxValue(scope))) {
						if (var.getType().id() == IType.INT) {
							solution.put(var.getName(), (int) varValue);
						} else if (var.getType().id() == IType.FLOAT) {
							solution.put(var.getName(), varValue);
						} else {
							continue;
						}
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						varValue = varValue + Cast.asFloat(scope, var.getStepValue(scope));
					}
			}

		}

	}
}