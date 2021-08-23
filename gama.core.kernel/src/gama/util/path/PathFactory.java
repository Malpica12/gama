/*******************************************************************************************************
 *
 * msi.gama.util.path.PathFactory.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.util.path;

import static gama.common.geometry.GeometryUtils.getFirstPointOf;
import static gama.common.geometry.GeometryUtils.getLastPointOf;

import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.GamaShape;
import gama.metamodel.shape.IShape;
import gama.metamodel.topology.ITopology;
import gama.metamodel.topology.continuous.AmorphousTopology;
import gama.metamodel.topology.continuous.ContinuousTopology;
import gama.metamodel.topology.graph.GamaSpatialGraph;
import gama.metamodel.topology.graph.GraphTopology;
import gama.metamodel.topology.grid.GridTopology;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IList;
import gama.util.graph.IGraph;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class PathFactory {

	public static <V, E> GamaPath<V, E, IGraph<V, E>> newInstance(final IGraph<V, E> g,
			final IList<? extends V> nodes) {
		if (nodes.isEmpty() && g instanceof GamaSpatialGraph || nodes.get(0) instanceof GamaPoint
				|| g instanceof GamaSpatialGraph)
			return (GamaPath) new GamaSpatialPath((GamaSpatialGraph) g, (IList<IShape>) nodes);
		else
			return new GamaPath<>(g, nodes);
	}

	public static <V, E> GamaPath<V, E, IGraph<V, E>> newInstance(final IGraph<V, E> g, final V start, final V target,
			final IList<E> edges) {
		if (g instanceof GamaSpatialGraph) {
			edges.removeIf(e -> e == null);
			return (GamaPath) new GamaSpatialPath((GamaSpatialGraph) g, (IShape) start, (IShape) target,
					(IList<IShape>) edges);
		} else
			return new GamaPath<>(g, start, target, edges);
	}

	public static <V, E> GamaPath<V, E, IGraph<V, E>> newInstance(final IGraph<V, E> g, final V start, final V target,
			final IList<E> edges, final boolean modify_edges) {
		if (g instanceof GamaSpatialGraph)
			return (GamaPath) new GamaSpatialPath((GamaSpatialGraph) g, (IShape) start, (IShape) target,
					(IList<IShape>) edges, modify_edges);
		else
			return new GamaPath<>(g, start, target, edges, modify_edges);
	}

	// With Topology
	public static GamaSpatialPath newInstance(final IScope scope, final ITopology g,
			final IList<? extends IShape> nodes, final double weight) {
		GamaSpatialPath path;
		if (g instanceof GraphTopology) {
			path = (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(), nodes);
		} else if (g instanceof ContinuousTopology || g instanceof AmorphousTopology || g instanceof GridTopology) {
			path = new GamaSpatialPath(null, nodes);
		} else
			throw GamaRuntimeException.error("Topologies that are not Graph are not yet taken into account", scope);
		path.setWeight(weight);
		return path;
	}

	public static GamaSpatialPath newInstance(final IScope scope, final ITopology g, final IShape start,
			final IShape target, final IList<IShape> edges) {
		if (g instanceof GraphTopology)
			return (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(), start, target, edges);
		else
			return new GamaSpatialPath(start, target, edges);
	}

	public static GamaSpatialPath newInstance(final IScope scope, final ITopology g, final IShape start,
			final IShape target, final IList<IShape> edges, final boolean modify_edges) {
		if (g instanceof GraphTopology)
			return (GamaSpatialPath) newInstance(((GraphTopology) g).getPlaces(), start, target, edges, modify_edges);
		else
			// AmorphousTopology ) {
			return new GamaSpatialPath(null, start, target, edges, modify_edges);
	}

	public static IPath newInstance(final IScope scope, final IList<? extends IShape> edgesNodes,
			final boolean isEdges) {
		if (isEdges) {
			final GamaShape shapeS = (GamaShape) edgesNodes.get(0).getGeometry();
			final GamaShape shapeT = (GamaShape) edgesNodes.get(edgesNodes.size() - 1).getGeometry();
			return new GamaSpatialPath(null, getFirstPointOf(shapeS), getLastPointOf(shapeT), edgesNodes, false);
		}
		return new GamaSpatialPath(edgesNodes);
	}

}
