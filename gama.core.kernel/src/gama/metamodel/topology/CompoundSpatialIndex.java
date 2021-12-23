
/*******************************************************************************************************
 *
 * CompoundSpatialIndex.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.metamodel.topology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.locationtech.jts.geom.Envelope;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Ordering;

import gama.common.geometry.Envelope3D;
import gama.common.preferences.GamaPreferences;
import gama.metamodel.agent.IAgent;
import gama.metamodel.population.IPopulation;
import gama.metamodel.shape.IShape;
import gama.metamodel.topology.filter.IAgentFilter;
import gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import gama.runtime.IScope;
import gama.util.Collector;
import gama.util.ICollector;

/**
 * The Class CompoundSpatialIndex.
 */
public class CompoundSpatialIndex extends Object implements ISpatialIndex.Compound {

	/** The disposed. */
	boolean disposed = false;
	
	/** The spatial indexes. */
	private final Cache<IPopulation<? extends IAgent>, ISpatialIndex> spatialIndexes =
			CacheBuilder.newBuilder().expireAfterAccess(180, TimeUnit.SECONDS).build();
	
	/** The bounds. */
	private Envelope bounds;
	
	/** The parallel. */
	private boolean parallel;
	
	/** The steps. */
	final protected double[] steps;

	/**
	 * Instantiates a new compound spatial index.
	 *
	 * @param bounds the bounds
	 * @param parallel the parallel
	 */
	public CompoundSpatialIndex(final Envelope bounds, final boolean parallel) {
		this.bounds = bounds;
		this.parallel = parallel;
		final double biggest = Math.max(bounds.getWidth(), bounds.getHeight());
		steps = new double[] { biggest / 100, biggest / 50, biggest / 20, biggest / 10, biggest / 2, biggest,
				biggest * Math.sqrt(2) };
	}

	@Override
	public void insert(final IAgent agent) {
		if (disposed || agent == null) return;
		IPopulation<? extends IAgent> pop = agent.getPopulation();
		ISpatialIndex index = spatialIndexes.getIfPresent(pop);
		if (index == null && !GamaPreferences.External.QUADTREE_OPTIMIZATION.getValue()) { index = add(pop, false); }
		if (index != null) { index.insert(agent); }
	}

	@Override
	public void remove(final Envelope3D previous, final IAgent agent) {
		if (disposed || agent == null) return;
		ISpatialIndex index = spatialIndexes.getIfPresent(agent.getPopulation());
		if (index != null) { index.remove(previous, agent); }
	}

	@Override
	public IAgent firstAtDistance(final IScope scope, final IShape source, final double dist, final IAgentFilter f) {
		if (disposed) return null;
		ISpatialIndex index = add(scope, f);
		if (index == null) {
			try (final Collector.AsList<IAgent> shapes = Collector.getList()) {
				for (final double step : steps) {
					for (final ISpatialIndex si : spatialIndexes.asMap().values()) {
						final IAgent first = si.firstAtDistance(scope, source, step, f);
						if (first != null) { shapes.add(first); }
					}
					if (!shapes.isEmpty()) { break; }
				}
				if (shapes.items().size() == 1) return shapes.items().get(0);
				// Adresses Issue 722 by shuffling the returned list using GAMA random
				// procedure
				shapes.shuffleInPlaceWith(scope.getRandom());
				double min_dist = Double.MAX_VALUE;
				IAgent min_agent = null;
				for (final IAgent s : shapes) {
					final double dd = source.euclidianDistanceTo(s);
					if (dd < min_dist) {
						min_dist = dd;
						min_agent = s;
					}
				}
				return min_agent;
			}
		}
		for (final double step : steps) {
			IAgent first = index.firstAtDistance(scope, source, step, f);
			if (first != null) return first;
		}
		return null;
	}

	/**
	 * N first at distance in all spatial indexes.
	 *
	 * @param scope the scope
	 * @param source the source
	 * @param filter the filter
	 * @param number the number
	 * @param alreadyChosen the already chosen
	 * @return the collection
	 */
	private Collection<IAgent> nFirstAtDistanceInAllSpatialIndexes(final IScope scope, final IShape source,
			final IAgentFilter filter, final int number, final Collection<IAgent> alreadyChosen) {
		if (disposed) return null;
		final List<IAgent> shapes = new ArrayList<>(alreadyChosen);
		for (final double step : steps) {
			for (final ISpatialIndex si : spatialIndexes.asMap().values()) {
				final Collection<IAgent> firsts = si.firstAtDistance(scope, source, step, filter, number, shapes);
				shapes.addAll(firsts);
			}
			if (shapes.size() >= number) { break; }
		}

		if (shapes.size() <= number) return shapes;
		scope.getRandom().shuffleInPlace(shapes);
		final Ordering<IShape> ordering = Ordering.natural().onResultOf(input -> source.euclidianDistanceTo(input));
		return ordering.leastOf(shapes, number);
	}

	/**
	 * N first at distance in spatial index.
	 *
	 * @param scope the scope
	 * @param source the source
	 * @param filter the filter
	 * @param number the number
	 * @param alreadyChosen the already chosen
	 * @param index the index
	 * @return the collection
	 */
	private Collection<IAgent> nFirstAtDistanceInSpatialIndex(final IScope scope, final IShape source,
			final IAgentFilter filter, final int number, final Collection<IAgent> alreadyChosen,
			final ISpatialIndex index) {
		try (final ICollector<IAgent> closestEnt = Collector.getList()) {
			closestEnt.addAll(alreadyChosen);
			for (final double step : steps) {
				final Collection<IAgent> firsts =
						index.firstAtDistance(scope, source, step, filter, number - closestEnt.size(), closestEnt);
				if (firsts.isEmpty()) { continue; }
				closestEnt.addAll(firsts);
				if (closestEnt.size() == number) return closestEnt.items();
			}
			return closestEnt.items();
		}
	}

	@Override
	public Collection<IAgent> firstAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f, final int number, final Collection<IAgent> alreadyChosen) {
		if (disposed) return null;
		ISpatialIndex index = add(scope, f);
		if (index != null) return nFirstAtDistanceInSpatialIndex(scope, source, f, number, alreadyChosen, index);
		return nFirstAtDistanceInAllSpatialIndexes(scope, source, f, number, alreadyChosen);
	}

	@Override
	public Collection<IAgent> allInEnvelope(final IScope scope, final IShape source, final Envelope envelope,
			final IAgentFilter f, final boolean contained) {
		if (disposed) return Collections.EMPTY_LIST;
		ISpatialIndex index = add(scope, f);
		if (index != null) return index.allInEnvelope(scope, source, envelope, f, contained);
		try (final ICollector<IAgent> agents = Collector.getOrderedSet()) {
			for (final ISpatialIndex si : spatialIndexes.asMap().values()) {
				agents.addAll(si.allInEnvelope(scope, source, envelope, f, contained));
			}
			agents.shuffleInPlaceWith(scope.getRandom());
			return agents.items();
		}
	}

	@Override
	public Collection<IAgent> allAtDistance(final IScope scope, final IShape source, final double dist,
			final IAgentFilter f) {
		if (disposed) return Collections.EMPTY_LIST;
		ISpatialIndex index = add(scope, f);
		if (index != null) return index.allAtDistance(scope, source, dist, f);
		try (final ICollector<IAgent> agents = Collector.getOrderedSet()) {
			for (final ISpatialIndex si : spatialIndexes.asMap().values()) {
				agents.addAll(si.allAtDistance(scope, source, dist, f));
			}
			agents.shuffleInPlaceWith(scope.getRandom());
			return agents.items();
		}
	}

	@Override
	public void dispose() {
		if (disposed) return;
		disposed = true;
		spatialIndexes.invalidateAll();
	}

	/**
	 * Adds the.
	 *
	 * @param pop the pop
	 * @param insertAgents the insert agents
	 * @return the i spatial index
	 */
	private ISpatialIndex add(final IPopulation<? extends IAgent> pop, final boolean insertAgents) {
		if (disposed) return null;
		ISpatialIndex index = spatialIndexes.getIfPresent(pop);
		if (index == null) {
			if (pop.isGrid()) {
				index = ((GridPopulation) pop).getTopology().getPlaces();
			} else {
				index = GamaQuadTree.create(bounds, parallel);
			}
			spatialIndexes.put(pop, index);
			if (insertAgents) {
				for (final IAgent ag : pop) {
					index.insert(ag);
				}
			}
		}
		return index;
	}

	/**
	 * Adds the.
	 *
	 * @param scope the scope
	 * @param filter the filter
	 * @return the i spatial index
	 */
	private ISpatialIndex add(final IScope scope, final IAgentFilter filter) {
		if (filter == null) return null;
		IPopulation<? extends IAgent> pop = filter.getPopulation(scope);
		if (pop == null) return null;
		return add(pop, true);
	}

	@Override
	public void remove(final IPopulation<? extends IAgent> pop) {
		spatialIndexes.invalidate(pop);
	}

	@Override
	public void update(final Envelope envelope, final boolean parallel) {
		this.bounds = envelope;
		this.parallel = parallel;
		for (IPopulation<? extends IAgent> pop : spatialIndexes.asMap().keySet()) {
			remove(pop);
			add(pop, true);
		}
	}

	@Override
	public void mergeWith(final Compound spatialIndex) {
		final CompoundSpatialIndex other = (CompoundSpatialIndex) spatialIndex;
		if (null == other) return;
		other.spatialIndexes.asMap().forEach((species, index) -> {
			spatialIndexes.put(species, index);
		});
		spatialIndex.dispose();
	}

}