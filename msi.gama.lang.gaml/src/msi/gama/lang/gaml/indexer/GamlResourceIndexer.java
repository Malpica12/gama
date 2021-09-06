/*******************************************************************************************************
 *
 * GamlResourceIndexer.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation
 * platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.indexer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import com.google.common.base.Objects;
import com.google.common.collect.Iterators;
import com.google.common.collect.LinkedHashMultimap;
import com.google.inject.Singleton;

import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.lang.gaml.gaml.ExperimentFileStructure;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Import;
import msi.gama.lang.gaml.gaml.Model;
import msi.gama.lang.gaml.gaml.impl.ModelImpl;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;

/**
 * The Class GamlResourceIndexer.
 */
@Singleton
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlResourceIndexer {

	/** The index. */
	static SimpleDirectedGraph<URI, Edge> index = new SimpleDirectedGraph(Edge.class);

	static {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(
				event -> { if (event.getBuildKind() == IncrementalProjectBuilder.CLEAN_BUILD) { eraseIndex(); } },
				IResourceChangeEvent.PRE_BUILD);
	}

	/** The Constant EMPTY_MAP. */
	protected final static IMap EMPTY_MAP = GamaMapFactory.create();

	/** The Constant IMPORTED_URIS. */
	public static final Object IMPORTED_URIS = "ImportedURIs";

	/**
	 * Gets the imports as absolute URIS.
	 *
	 * @param baseURI
	 *            the base URI
	 * @param m
	 *            the m
	 * @return the imports as absolute URIS
	 */
	protected static IMap<URI, String> getImportsAsAbsoluteURIS(final URI baseURI, final Model m) {
		IMap<URI, String> result = EMPTY_MAP;
		if (((ModelImpl) m).eIsSet(GamlPackage.MODEL__IMPORTS)) {
			result = GamaMapFactory.create();
			for (final Import e : m.getImports()) {
				final String u = e.getImportURI();
				if (u != null) {
					URI uri = URI.createURI(u, true);
					uri = GamlResourceServices.properlyEncodedURI(uri.resolve(baseURI));
					final String label = e.getName();
					result.put(uri, label);
				}
			}
		}
		return result;
	}

	/**
	 * Gets the imports as absolute URIS.
	 *
	 * @param baseURI
	 *            the base URI
	 * @param m
	 *            the m
	 * @return the imports as absolute URIS
	 */
	protected static IMap<URI, String> getImportsAsAbsoluteURIS(final URI baseURI, final ExperimentFileStructure m) {
		final IMap<URI, String> result = GamaMapFactory.create();
		final String u = m.getExp().getImportURI();
		if (u != null) {
			URI uri = URI.createURI(u, true);
			uri = GamlResourceServices.properlyEncodedURI(uri.resolve(baseURI));
			result.put(uri, null);
		}

		return result;
	}

	/**
	 * All labeled imports of.
	 *
	 * @param r
	 *            the r
	 * @return the i map
	 */
	public static IMap<URI, String> allLabeledImportsOf(final GamlResource r) {
		return r.getCache().get(IMPORTED_URIS, r, () -> allLabeledImportsOf(r.getURI()));
	}

	/**
	 * The Class Edge.
	 */
	static class Edge {

		/** The label. */
		String label;

		/** The target. */
		final URI target;

		/**
		 * Instantiates a new edge.
		 *
		 * @param l
		 *            the l
		 * @param target
		 *            the target
		 */
		Edge(final String l, final URI target) {
			this.label = l;
			this.target = target;
		}

		/**
		 * Gets the target.
		 *
		 * @return the target
		 */
		URI getTarget() { return target; }

		/**
		 * Gets the label.
		 *
		 * @return the label
		 */
		String getLabel() { return label; }

		/**
		 * Sets the label.
		 *
		 * @param b
		 *            the new label
		 */
		public void setLabel(final String b) { label = b; }
	}

	/**
	 * Adds the import.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @param label
	 *            the label
	 */
	static void addImport(final URI from, final URI to, final String label) {
		index.addVertex(to);
		index.addVertex(from);
		index.addEdge(from, to, new Edge(label, to));
	}

	/**
	 * Clear resource set.
	 *
	 * @param resourceSet
	 *            the resource set
	 */
	public static void clearResourceSet(final ResourceSet resourceSet) {
		final boolean wasDeliver = resourceSet.eDeliver();
		try {
			resourceSet.eSetDeliver(false);
			resourceSet.getResources().clear();
		} finally {
			resourceSet.eSetDeliver(wasDeliver);
		}
	}

	/**
	 * Synchronized method to avoid concurrent errors in the graph in case of a parallel resource loader
	 */
	public static synchronized EObject updateImports(final GamlResource r) {
		final URI baseURI = GamlResourceServices.properlyEncodedURI(r.getURI());
		final Set<Edge> nativeEdges = index.containsVertex(baseURI) ? index.outgoingEdgesOf(baseURI) : null;
		final Set<Edge> edges =
				nativeEdges == null || nativeEdges.isEmpty() ? Collections.EMPTY_SET : new HashSet(nativeEdges);
		if (r.getContents().isEmpty()) return null;
		final EObject contents = r.getContents().get(0);
		if (contents == null) return null;
		final boolean isModel = contents instanceof Model;
		final boolean isExpe = contents instanceof ExperimentFileStructure;
		final IMap<URI, String> added;
		if (isModel) {
			added = getImportsAsAbsoluteURIS(baseURI, (Model) contents);
		} else if (isExpe) {
			added = getImportsAsAbsoluteURIS(baseURI, (ExperimentFileStructure) contents);
		} else
			return null;
		final EObject[] faulty = new EObject[1];
		if (added.forEachPair(new BiConsumerWithPruning<URI, String>() {

			@Override
			public boolean process(final URI uri, final String b) {
				if (baseURI.equals(uri)) return true;
				final Iterator<Edge> iterator = edges.iterator();
				boolean found = false;
				while (iterator.hasNext()) {
					final Edge edge = iterator.next();
					if (edge.getTarget().equals(uri)) {
						found = true;
						if (!Objects.equal(edge.getLabel(), b)) { edge.setLabel(b); }
						iterator.remove();
						break;
					}
				}
				if (!found) {
					if (!EcoreUtil2.isValidUri(r, uri)) {
						if (isModel) {
							faulty[0] = findImport((Model) contents, uri);
						} else {
							faulty[0] = findImport((ExperimentFileStructure) contents, uri);
						}
						return false;
					}
					final boolean alreadyThere = index.containsVertex(uri);
					addImport(baseURI, uri, b);
					if (!alreadyThere) {
						// This call should trigger the recursive call to
						// updateImports()
						r.getResourceSet().getResource(uri, true);
					}
				}
				return true;
			}

			private EObject findImport(final ExperimentFileStructure model, final URI uri) {
				if (model.getExp().getImportURI().contains(URI.decode(uri.lastSegment())) || (uri.equals(baseURI) && model.getExp().getImportURI().isEmpty())) return model;
				return null;
			}

			private EObject findImport(final Model model, final URI uri) {
				for (final Import e : model.getImports()) {
					if (e.getImportURI().contains(URI.decode(uri.lastSegment())) || (uri.equals(baseURI) && e.getImportURI().isEmpty())) return e;
				}
				return null;
			}
		})) {
			index.removeAllEdges(edges);
			return null;
		}
		return faulty[0];

	}

	/**
	 * Validate imports of.
	 *
	 * @param resource
	 *            the resource
	 * @return the linked hash multimap
	 */
	public static LinkedHashMultimap<String, GamlResource> validateImportsOf(final GamlResource resource) {
		final IMap<URI, String> uris = allLabeledImportsOf(resource);
		uris.remove(GamlResourceServices.properlyEncodedURI(resource.getURI()));
		if (!uris.isEmpty()) {
			final LinkedHashMultimap<String, GamlResource> imports = LinkedHashMultimap.create();
			if (uris.forEachPair((a, b) -> {
				final GamlResource r = (GamlResource) resource.getResourceSet().getResource(a, true);
				if (r.hasErrors()) {
					resource.invalidate(r, "Errors detected");
					return false;
				}
				imports.put(b, r);
				return true;
			})) return imports;

		}
		return null;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#directImportersOf(org.eclipse.emf.common.util.URI)
	 */
	public static Set<URI> directImportersOf(final URI uri) {
		final URI newURI = GamlResourceServices.properlyEncodedURI(uri);
		if (index.containsVertex(newURI)) return new HashSet(Graphs.predecessorListOf(index, newURI));
		return Collections.EMPTY_SET;
	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#directImportsOf(org.eclipse.emf.common.util.URI)
	 */
	public static Set<URI> directImportsOf(final URI uri) {
		final URI newURI = GamlResourceServices.properlyEncodedURI(uri);
		if (index.containsVertex(newURI)) return new HashSet(Graphs.successorListOf(index, newURI));
		return Collections.EMPTY_SET;
	}

	/**
	 * All labeled imports of.
	 *
	 * @param uri
	 *            the uri
	 * @return the i map
	 */
	private static IMap<URI, String> allLabeledImportsOf(final URI uri) {
		final URI newURI = GamlResourceServices.properlyEncodedURI(uri);
		final IMap<URI, String> result = GamaMapFactory.create();
		allLabeledImports(newURI, null, result);
		return result;
	}

	/**
	 * All labeled imports.
	 *
	 * @param uri
	 *            the uri
	 * @param currentLabel
	 *            the current label
	 * @param result
	 *            the result
	 */
	private static void allLabeledImports(final URI uri, final String currentLabel, final Map<URI, String> result) {
		if (!result.containsKey(uri)) {
			result.put(uri, currentLabel);
			if (indexes(uri)) {
				final Collection<Edge> edges = index.outgoingEdgesOf(uri);
				for (final Edge e : edges) {
					allLabeledImports(index.getEdgeTarget(e), e.getLabel() == null ? currentLabel : e.getLabel(),
							result);
				}
			}
		}

	}

	/**
	 * @see msi.gama.lang.gaml.indexer.IModelIndexer#allImportsOf(org.eclipse.emf.common.util.URI)
	 */
	public static Iterator<URI> allImportsOf(final URI uri) {
		if (!indexes(uri)) return Iterators.singletonIterator(uri);// .emptyIterator();
		final Iterator<URI> result = new BreadthFirstIterator(index, GamlResourceServices.properlyEncodedURI(uri));
		result.next(); // to eliminate the uri
		return result;
	}

	/**
	 * Indexes.
	 *
	 * @param uri
	 *            the uri
	 * @return true, if successful
	 */
	public static boolean indexes(final URI uri) {
		return index.containsVertex(GamlResourceServices.properlyEncodedURI(uri));
	}

	/**
	 * Equals.
	 *
	 * @param uri1
	 *            the uri 1
	 * @param uri2
	 *            the uri 2
	 * @return true, if successful
	 */
	public static boolean equals(final URI uri1, final URI uri2) {
		if (uri1 == null) return uri2 == null;
		if (uri2 == null) return false;
		return GamlResourceServices.properlyEncodedURI(uri1).equals(GamlResourceServices.properlyEncodedURI(uri2));
	}

	/**
	 * Erase index.
	 */
	public static void eraseIndex() {
		// DEBUG.OUT("Erasing GAML indexer index");
		index = new SimpleDirectedGraph(Edge.class);
	}

	/**
	 * Checks if is imported.
	 *
	 * @param r
	 *            the r
	 * @return true, if is imported
	 */
	public static boolean isImported(final GamlResource r) {
		return !directImportersOf(r.getURI()).isEmpty();
	}

}
