package gama.ext.traffic.driving;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;

import gama.common.util.StringUtils;
import gama.metamodel.agent.IAgent;
import gama.metamodel.shape.GamaPoint;
import gama.metamodel.shape.IShape;
import gama.metamodel.topology.graph.GamaSpatialGraph;
import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.util.IContainer;
import gama.util.IMap;
import gama.util.graph.GraphEvent;
import gama.util.graph.GraphEvent.GraphEventType;
import gama.util.graph._Edge;

public class DrivingGraph extends GamaSpatialGraph {
	public DrivingGraph(final IContainer edges, final IContainer vertices, final IScope scope) {
		super(scope, vertices.getGamlType().getContentType(), edges.getGamlType().getContentType());
		init(scope, edges, vertices);
	}

	@Override
	public boolean addEdgeWithNodes(final IScope scope, final IShape e, final IMap<GamaPoint, IShape> nodes) {
		if (containsEdge(e)) return false;
		final Coordinate[] coord = e.getInnerGeometry().getCoordinates();
		final IShape ptS = new GamaPoint(coord[0]);
		final IShape ptT = new GamaPoint(coord[coord.length - 1]);
		final IShape v1 = nodes.get(ptS);
		if (v1 == null) return false;
		final IShape v2 = nodes.get(ptT);
		if (v2 == null) return false;

		if (e instanceof IAgent && ((IAgent) e).getSpecies().implementsSkill("skill_road")) {
			final IAgent roadAgent = e.getAgent();
			final IAgent source = v1.getAgent();
			final IAgent target = v2.getAgent();
			final List<IAgent> v1ro = RoadNodeSkill.getRoadsOut(source);
			if (!v1ro.contains(roadAgent)) { v1ro.add(roadAgent); }
			final List<IAgent> v2ri = RoadNodeSkill.getRoadsIn(target);
			if (!v2ri.contains(roadAgent)) { v2ri.add(roadAgent); }
			RoadSkill.setSourceNode(roadAgent, source);
			RoadSkill.setTargetNode(roadAgent, target);
		}

		addVertex(v1);
		addVertex(v2);
		_Edge<IShape, IShape> edge;
		try {
			edge = newEdge(e, v1, v2);
		} catch (final GamaRuntimeException e1) {
			e1.addContext("Impossible to create edge from " + StringUtils.toGaml(e, false) + " in graph " + this);
			throw e1;
		}
		// if ( edge == null ) { return false; }
		edgeMap.put(e, edge);
		dispatchEvent(scope, new GraphEvent(scope, this, this, e, null, GraphEventType.EDGE_ADDED));
		return true;
	}
}