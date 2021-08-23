/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.grid.IGrid.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.metamodel.topology.grid;

import java.util.List;
import java.util.Map;
import java.util.Set;

import gama.metamodel.agent.IAgent;
import gama.metamodel.population.IPopulation;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.IShape;
import gama.metamodel.topology.ISpatialIndex;
import gama.metamodel.topology.ITopology;
import gama.metamodel.topology.filter.IAgentFilter;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IList;
import gama.util.matrix.IMatrix;
import gama.util.path.GamaSpatialPath;
import gaml.expressions.IExpression;
import gaml.species.ISpecies;

/**
 * Interface IGrid.
 *
 * @author Alexis Drogoul
 * @since 13 mai 2013
 *
 */
public interface IGrid extends IMatrix<IShape>, ISpatialIndex, IDiffusionTarget {

	List<IAgent> getAgents();

	Boolean isHexagon();

	Boolean isHorizontalOrientation();

	void setCellSpecies(final IPopulation<? extends IAgent> pop);

	IAgent getAgentAt(final GamaPoint c);

	GamaSpatialPath computeShortestPathBetween(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final IList<IAgent> on) throws GamaRuntimeException;

	GamaSpatialPath computeShortestPathBetweenWeighted(final IScope scope, final IShape source, final IShape target,
			final ITopology topo, final Map<IAgent, Object> on) throws GamaRuntimeException;

	// public abstract Iterator<IAgent> getNeighborsOf(final IScope scope, final
	// GamaPoint shape, final Double
	// distance,
	// IAgentFilter filter);

	Set<IAgent> getNeighborsOf(final IScope scope, final IShape shape, final Double distance, IAgentFilter filter);

	int manhattanDistanceBetween(final IShape g1, final IShape g2);

	IShape getPlaceAt(final GamaPoint c);

	int[] getDisplayData();

	double[] getGridValue();

	/**
	 * Computes and returns a double array by applying the expression to each of the agents of the grid
	 *
	 * @param scope
	 *            the current scope
	 * @param expr
	 *            cannot be null
	 * @return a double array the size of the grid
	 */
	double[] getGridValueOf(IScope scope, IExpression expr);

	boolean isTorus();

	INeighborhood getNeighborhood();

	IShape getEnvironmentFrame();

	int getX(IShape geometry);

	int getY(IShape geometry);

	@Override
	void dispose();

	boolean usesIndiviualShapes();

	/**
	 * @return
	 */
	boolean usesNeighborsCache();

	String optimizer();

	/**
	 * @return
	 */
	ISpecies getCellSpecies();

}
