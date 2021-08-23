/*******************************************************************************************************
 *
 * msi.gaml.extensions.fipa.FIPAProtocol.java, in plugin msi.gaml.extensions.fipa, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ext.fipa;

import static gama.ext.fipa.Performative.accept_proposal;
import static gama.ext.fipa.Performative.agree;
import static gama.ext.fipa.Performative.cancel;
import static gama.ext.fipa.Performative.cfp;
import static gama.ext.fipa.Performative.failure;
import static gama.ext.fipa.Performative.inform;
import static gama.ext.fipa.Performative.not_understood;
import static gama.ext.fipa.Performative.propose;
import static gama.ext.fipa.Performative.proxy;
import static gama.ext.fipa.Performative.query;
import static gama.ext.fipa.Performative.refuse;
import static gama.ext.fipa.Performative.reject_proposal;
import static gama.ext.fipa.Performative.request;
import static gama.ext.fipa.Performative.request_when;
import static gama.ext.fipa.Performative.subscribe;
import static java.util.Arrays.asList;
import static org.jgrapht.Graphs.addOutgoingEdges;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;

import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class FIPAProtocol.
 *
 * @author drogoul
 */
abstract public class FIPAProtocol extends DefaultDirectedGraph<ProtocolNode, Object> {

	public static enum Names {

		fipa_brokering(new FIPAProtocol("fipa_brokering") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				addTree(iNode(proxy), iNode(cancel), pNode(refuse), pNode(agree));
				addTree(iNode(cancel), iNode(proxy), pNode(failure), pNode(inform));
				addTree(pNode(agree), iNode(cancel), pNode(failure), pNode(inform));
				return iNode(proxy);
			}
		}), fipa_contract_net(new FIPAProtocol("fipa_contract_net") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				addTree(iNode(cfp), pNode(failure), iNode(cancel), pNode(refuse), pNode(propose));
				addTree(pNode(propose), iNode(failure), iNode(cancel), iNode(accept_proposal), iNode(reject_proposal));
				addTree(iNode(cancel), pNode(failure), pNode(inform));
				addTree(iNode(accept_proposal), pNode(failure), pNode(inform));
				return iNode(cfp);
			}
		}), fipa_iterated_contract_net(new FIPAProtocol("fipa_iterated_contract_net") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				addTree(iNode(cfp), pNode(failure), iNode(cancel), pNode(refuse), pNode(propose));
				addTree(pNode(propose), iNode(failure), iNode(cancel), iNode(accept_proposal), iNode(reject_proposal),
						iNode(cfp));
				addTree(iNode(cancel), pNode(failure), pNode(inform));
				addTree(iNode(accept_proposal), pNode(failure), pNode(inform));
				return iNode(cfp);
			}
		}), fipa_propose(new FIPAProtocol("fipa_propose") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				addTree(iNode(propose), pNode(reject_proposal), pNode(accept_proposal), iNode(cancel));
				addTree(iNode(cancel), pNode(failure), pNode(inform));
				return iNode(propose);
			}
		}), fipa_query(new FIPAProtocol("fipa_query") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				addTree(iNode(query), pNode(refuse), pNode(agree), iNode(cancel));
				addTree(pNode(agree), pNode(inform), pNode(failure));
				addTree(iNode(cancel), pNode(inform), pNode(failure));
				return iNode(query);

			}
		}), fipa_request(new FIPAProtocol("fipa_request") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				addTree(iNode(request), pNode(not_understood), iNode(cancel), pNode(agree), pNode(refuse));
				addTree(iNode(cancel), pNode(failure), pNode(inform));
				addTree(pNode(agree), pNode(failure), pNode(inform));
				return iNode(request);
			}
		}), fipa_request_when(new FIPAProtocol("fipa_request_when") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				addTree(iNode(request_when), iNode(cancel), pNode(refuse), pNode(agree));
				addTree(iNode(cancel), pNode(failure), pNode(inform));
				addTree(iNode(agree), pNode(failure), pNode(inform));
				return iNode(request_when);
			}
		}), fipa_subscribe(new FIPAProtocol("fipa_subscribe") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				addTree(iNode(subscribe), pNode(refuse), pNode(agree), iNode(cancel));
				addTree(pNode(agree), pNode(inform), iNode(cancel), pNode(failure));
				addTree(pNode(inform), iNode(cancel), pNode(failure));
				addTree(iNode(cancel), pNode(inform), pNode(failure)); // ???
				return iNode(subscribe);
			}
		}), no_protocol(new FIPAProtocol("no_protocol") {
			@Override
			protected ProtocolNode populateProtocolGraph() {
				return null;
			}
		});

		FIPAProtocol protocol;

		Names(FIPAProtocol p) {
			protocol = p;
		}

	}

	public FIPAProtocol(String name) {
		super(Object.class);
		this.name = name;
		root = populateProtocolGraph();
	}

	private final ProtocolNode root;

	private String name;

	public final String getName() {
		return name;
	}

	/**
	 * Adds a subgraph (usually a tree) to the protocol. First parameter is the source node, others are the targets
	 *
	 * @param start
	 * @param nodes
	 */
	void addTree(ProtocolNode start, ProtocolNode... nodes) {
		addOutgoingEdges(this, start, asList(nodes));
	}

	/**
	 * Returns a node sent by a participant with this performative. If the node already exists, returns it. Otherwise,
	 * creates it and adds it to the protocol nodes
	 *
	 * @param performative
	 * @return
	 */
	ProtocolNode pNode(Performative performative) {
		for (ProtocolNode node : this.vertexSet()) {
			if (node.getPerformative() == performative && !node.isSentByInitiator()) { return node; }
		}
		ProtocolNode node = new ProtocolNode(this, performative, false);
		addVertex(node);
		return node;
	}

	/**
	 * Returns a node sent by the initiator with this performative. If the node already exists, returns it. Otherwise,
	 * creates it and adds it to the protocol nodes
	 *
	 * @param performative
	 * @return
	 */
	ProtocolNode iNode(Performative performative) {
		for (ProtocolNode node : this.vertexSet()) {
			if (node.getPerformative() == performative && node.isSentByInitiator()) { return node; }
		}
		ProtocolNode node = new ProtocolNode(this, performative, true);
		addVertex(node);
		return node;
	}

	protected abstract ProtocolNode populateProtocolGraph();

	/**
	 * Checks for protocol.
	 *
	 * @return true if a protocol tree is defined, false otherwise.
	 */
	public final boolean hasProtocol() {
		return root != null;
	}

	/**
	 * Gets the node corresponding to a performative after the current node of the protocol.
	 */
	protected ProtocolNode getNode(final IScope scope, final FIPAMessage message, final ProtocolNode currentNode,
			final Performative performative, final boolean initiator) throws GamaRuntimeException {
		if (currentNode == null) {
			if (root != null && root.getPerformative() == performative)
				return root;
			return null;
		}
		final List<ProtocolNode> followingNodes = Graphs.successorListOf(this, currentNode);
		if (followingNodes.size() == 0) {
			throw GamaRuntimeException.warning("Message received in a conversation which has already ended!", scope);
		}
		final List<ProtocolNode> potentialMatchingNodes = new ArrayList<>();
		for (final ProtocolNode followingNode : followingNodes) {
			if (performative == followingNode.getPerformative()) {
				potentialMatchingNodes.add(followingNode);
			}
		}
		if (potentialMatchingNodes.isEmpty()) {
			throw GamaRuntimeException.warning("Protocol : " + this.getName()
					+ ". Unexpected message received of performative : " + message.getPerformativeName(), scope);
		}
		ProtocolNode matchingNode = null;
		for (final ProtocolNode potentialMatchingNode : potentialMatchingNodes) {
			if (initiator == potentialMatchingNode.isSentByInitiator()) {
				matchingNode = potentialMatchingNode;
				break;
			}
		}

		if (matchingNode == null) {
			throw GamaRuntimeException.warning("Couldn't match expected message types and participant", scope);
		}
		return matchingNode;

	}
}
