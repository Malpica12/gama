/*******************************************************************************************************
 *
 * DescriptionFactory.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.factories;

import static gama.common.interfaces.IKeyword.AGENT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;

import gama.common.interfaces.IGamlIssue;
import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.ISymbolKind;
import gaml.compilation.GAML;
import gaml.compilation.IAgentConstructor;
import gaml.compilation.ast.ISyntacticElement;
import gaml.compilation.ast.SyntacticFactory;
import gaml.compilation.ast.ISyntacticElement.SyntacticVisitor;
import gaml.descriptions.FacetProto;
import gaml.descriptions.IDescription;
import gaml.descriptions.ModelDescription;
import gaml.descriptions.SpeciesDescription;
import gaml.descriptions.SymbolProto;
import gaml.statements.Facets;
import gaml.types.IType;

/**
 * Written by drogoul Modified on 7 janv. 2011
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DescriptionFactory {

	static {
		// DEBUG.ON();
	}

	/** The factories. */
	static Map<Integer, SymbolFactory> FACTORIES = new HashMap();
	
	/** The statement keywords protos. */
	static ArrayListMultimap<String, SymbolProto> STATEMENT_KEYWORDS_PROTOS = ArrayListMultimap.create();
	
	/** The var keywords protos. */
	static Map<String, SymbolProto> VAR_KEYWORDS_PROTOS = new HashMap();
	
	/** The kinds protos. */
	static Map<Integer, SymbolProto> KINDS_PROTOS = new HashMap();

	/**
	 * Adds the factory.
	 *
	 * @param factory the factory
	 */
	public static void addFactory(final SymbolFactory factory) {
		factory.getHandles().forEach(i -> {
			FACTORIES.put(i, factory);
		});

	}

	/**
	 * Visit statement protos.
	 *
	 * @param consumer the consumer
	 */
	public final static void visitStatementProtos(final BiConsumer<String, SymbolProto> consumer) {
		STATEMENT_KEYWORDS_PROTOS.forEach(consumer);
	}

	/**
	 * Visit var protos.
	 *
	 * @param consumer the consumer
	 */
	public final static void visitVarProtos(final BiConsumer<String, SymbolProto> consumer) {
		VAR_KEYWORDS_PROTOS.forEach(consumer);
	}

	/**
	 * Gets the proto.
	 *
	 * @param keyword the keyword
	 * @param superDesc the super desc
	 * @return the proto
	 */
	public final static SymbolProto getProto(final String keyword, final IDescription superDesc) {
		final SymbolProto p =
				getStatementProto(keyword, superDesc == null ? null : superDesc.getSpeciesContext().getControlName());
		// If not a statement, we try to find a var declaration prototype
		if (p == null) return getVarProto(keyword, superDesc);
		return p;
	}

	/**
	 * Gets the statement proto.
	 *
	 * @param keyword the keyword
	 * @param control the control
	 * @return the statement proto
	 */
	public final static SymbolProto getStatementProto(final String keyword, final String control) {
		final List<SymbolProto> protos = STATEMENT_KEYWORDS_PROTOS.get(keyword);
		if (protos == null || protos.isEmpty()) return null;
		if (protos.size() == 1) return protos.get(0);
		if (control == null) return protos.get(protos.size() - 1);
		// DEBUG.OUT("Duplicate keyword: " + keyword + " ; looking for the one defined in " + control);
		for (final SymbolProto proto : protos) {
			if (proto.shouldBeDefinedIn(control)) return proto;
		}
		return null;
	}

	/**
	 * Gets the var proto.
	 *
	 * @param keyword the keyword
	 * @param superDesc the super desc
	 * @return the var proto
	 */
	public final static SymbolProto getVarProto(final String keyword, final IDescription superDesc) {
		final SymbolProto p = VAR_KEYWORDS_PROTOS.get(keyword);
		if (p == null) {
			// If not a var declaration, we try to find if it is not a species
			// name (in which case, it is an "agent"
			// declaration prototype)
			if (superDesc == null) return null;
			final ModelDescription md = superDesc.getModelDescription();
			if (md == null) return null;
			final IType t = md.getTypesManager().get(keyword);
			if (t.isAgentType()) return getVarProto(AGENT, null);
		}
		return p;
	}

	/**
	 * Gets the proto names.
	 *
	 * @return the proto names
	 */
	public final static Iterable<String> getProtoNames() {
		return Iterables.concat(getStatementProtoNames(), getVarProtoNames());
	}

	/**
	 * Gets the statement proto names.
	 *
	 * @return the statement proto names
	 */
	public final static Iterable<String> getStatementProtoNames() {
		return STATEMENT_KEYWORDS_PROTOS.keySet();
	}

	/**
	 * Gets the var proto names.
	 *
	 * @return the var proto names
	 */
	public final static Iterable<String> getVarProtoNames() {
		return VAR_KEYWORDS_PROTOS.keySet();
	}

	/**
	 * Checks if is statement proto.
	 *
	 * @param s the s
	 * @return true, if is statement proto
	 */
	public final static boolean isStatementProto(final String s) {
		// WARNING METHOD is treated here as a special keyword, but it should be
		// leveraged in the future
		return STATEMENT_KEYWORDS_PROTOS.containsKey(s) || IKeyword.METHOD.equals(s);
	}

	/**
	 * Gets the factory.
	 *
	 * @param kind the kind
	 * @return the factory
	 */
	public static SymbolFactory getFactory(final int kind) {
		return FACTORIES.get(kind);
	}

	/**
	 * Gets the omissible facet for symbol.
	 *
	 * @param keyword the keyword
	 * @return the omissible facet for symbol
	 */
	public static String getOmissibleFacetForSymbol(final String keyword) {
		final SymbolProto md = getProto(keyword, null);
		if (md == null) return IKeyword.NAME;
		return md.getOmissible();
	}

	/**
	 * Adds the proto.
	 *
	 * @param md the md
	 * @param names the names
	 */
	public static void addProto(final SymbolProto md, final Iterable<String> names) {
		final int kind = md.getKind();
		if (ISymbolKind.Variable.KINDS.contains(kind)) {
			for (final String s : names) {
				VAR_KEYWORDS_PROTOS.putIfAbsent(s, md);
			}
		} else {
			for (final String s : names) {
				STATEMENT_KEYWORDS_PROTOS.put(s, md);
			}
		}
		KINDS_PROTOS.put(kind, md);
	}

	/**
	 * Adds the new type name.
	 *
	 * @param s the s
	 * @param kind the kind
	 */
	public static void addNewTypeName(final String s, final int kind) {
		if (VAR_KEYWORDS_PROTOS.containsKey(s)) return;
		final SymbolProto p = KINDS_PROTOS.get(kind);
		if (p != null) {
			if (s.equals("species")) {
				VAR_KEYWORDS_PROTOS.put(SyntacticFactory.SPECIES_VAR, p);
			} else {
				VAR_KEYWORDS_PROTOS.put(s, p);
			}
		}
	}

	/**
	 * Gets the factory.
	 *
	 * @param keyword the keyword
	 * @return the factory
	 */
	public static SymbolFactory getFactory(final String keyword) {
		final SymbolProto p = getProto(keyword, null);
		if (p != null) return p.getFactory();
		return null;
	}

	/**
	 * Adds the species name as type.
	 *
	 * @param name the name
	 */
	public static void addSpeciesNameAsType(final String name) {
		if (!name.equals(AGENT) && !name.equals(IKeyword.EXPERIMENT)) {
			VAR_KEYWORDS_PROTOS.putIfAbsent(name, VAR_KEYWORDS_PROTOS.get(AGENT));
		}
	}

	/**
	 * Creates the.
	 *
	 * @param factory the factory
	 * @param keyword the keyword
	 * @param superDesc the super desc
	 * @param children the children
	 * @param facets the facets
	 * @return the i description
	 */
	public synchronized static IDescription create(final SymbolFactory factory, final String keyword,
			final IDescription superDesc, final Iterable<IDescription> children, final Facets facets) {
		return create(SyntacticFactory.create(keyword, facets, children != null), superDesc, children);
	}

	/**
	 * Creates the.
	 *
	 * @param keyword the keyword
	 * @param superDesc the super desc
	 * @param children the children
	 * @param facets the facets
	 * @return the i description
	 */
	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
			final Iterable<IDescription> children, final Facets facets) {
		return create(getFactory(keyword), keyword, superDesc, children, facets);
	}

	/**
	 * Creates the.
	 *
	 * @param keyword the keyword
	 * @param superDesc the super desc
	 * @param children the children
	 * @return the i description
	 */
	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
			final Iterable<IDescription> children) {
		return create(getFactory(keyword), keyword, superDesc, children, null);
	}

	/**
	 * Creates the.
	 *
	 * @param keyword the keyword
	 * @param superDesc the super desc
	 * @param children the children
	 * @param facets the facets
	 * @return the i description
	 */
	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
			final Iterable<IDescription> children, final String... facets) {
		return create(getFactory(keyword), keyword, superDesc, children, new Facets(facets));
	}

	/**
	 * Creates the.
	 *
	 * @param keyword the keyword
	 * @param superDescription the super description
	 * @param facets the facets
	 * @return the i description
	 */
	public synchronized static IDescription create(final String keyword, final IDescription superDescription,
			final String... facets) {
		return create(keyword, superDescription, null, facets);
	}

	/**
	 * Creates the.
	 *
	 * @param keyword the keyword
	 * @param facets the facets
	 * @return the i description
	 */
	public synchronized static IDescription create(final String keyword, final String... facets) {
		return create(keyword, GAML.getModelContext(), facets);
	}

	/**
	 * Gets the model factory.
	 *
	 * @return the model factory
	 */
	public static ModelFactory getModelFactory() {
		return (ModelFactory) getFactory(ISymbolKind.MODEL);
	}

	/**
	 * Gets the allowed facets for.
	 *
	 * @param keys the keys
	 * @return the allowed facets for
	 */
	public static Set<String> getAllowedFacetsFor(final String... keys) {
		if (keys == null || keys.length == 0) return Collections.EMPTY_SET;
		final Set<String> result = new HashSet();
		for (final String key : keys) {
			final SymbolProto md = getProto(key, null);
			if (md != null) { result.addAll(md.getPossibleFacets().keySet()); }
		}

		return result;
	}

	/**
	 * Creates a new Description object.
	 *
	 * @param name the name
	 * @param clazz the clazz
	 * @param superDesc the super desc
	 * @param parent the parent
	 * @param helper the helper
	 * @param skills the skills
	 * @param plugin the plugin
	 * @return the species description
	 */
	public static SpeciesDescription createBuiltInSpeciesDescription(final String name, final Class clazz,
			final SpeciesDescription superDesc, final SpeciesDescription parent, final IAgentConstructor helper,
			final Set<String> skills, final String plugin) {
		return ((SpeciesFactory) getFactory(ISymbolKind.SPECIES)).createBuiltInSpeciesDescription(name, clazz,
				superDesc, parent, helper, skills, null, plugin);
	}

	/**
	 * Creates a new Description object.
	 *
	 * @param name the name
	 * @param clazz the clazz
	 * @param macro the macro
	 * @param parent the parent
	 * @param helper the helper
	 * @param allSkills the all skills
	 * @param plugin the plugin
	 * @return the species description
	 */
	public static SpeciesDescription createPlatformSpeciesDescription(final String name, final Class clazz,
			final SpeciesDescription macro, final SpeciesDescription parent, final IAgentConstructor helper,
			final Set<String> allSkills, final String plugin) {
		return ((SpeciesFactory) getFactory(ISymbolKind.PLATFORM)).createBuiltInSpeciesDescription(name, clazz, macro,
				parent, helper, allSkills, null, plugin);
	}

	/**
	 * Creates a new Description object.
	 *
	 * @param name the name
	 * @param clazz the clazz
	 * @param superDesc the super desc
	 * @param parent the parent
	 * @param helper the helper
	 * @param skills the skills
	 * @param plugin the plugin
	 * @return the species description
	 */
	public static SpeciesDescription createBuiltInExperimentDescription(final String name, final Class clazz,
			final SpeciesDescription superDesc, final SpeciesDescription parent, final IAgentConstructor helper,
			final Set<String> skills, final String plugin) {
		return ((ExperimentFactory) getFactory(ISymbolKind.EXPERIMENT)).createBuiltInSpeciesDescription(name, clazz,
				superDesc, parent, helper, skills, null, plugin);
	}

	/**
	 * Creates a new Description object.
	 *
	 * @param name the name
	 * @param clazz the clazz
	 * @param macro the macro
	 * @param parent the parent
	 * @param helper the helper
	 * @param skills the skills
	 * @param plugin the plugin
	 * @return the model description
	 */
	public static ModelDescription createRootModelDescription(final String name, final Class clazz,
			final SpeciesDescription macro, final SpeciesDescription parent, final IAgentConstructor helper,
			final Set<String> skills, final String plugin) {
		return ModelFactory.createRootModel(name, clazz, macro, parent, helper, skills, plugin);
	}

	/**
	 * Creates the.
	 *
	 * @param source the source
	 * @param superDesc the super desc
	 * @param cp the cp
	 * @return the i description
	 */
	public static final IDescription create(final ISyntacticElement source, final IDescription superDesc,
			final Iterable<IDescription> cp) {
		if (source == null) return null;
		final String keyword = source.getKeyword();
		final SymbolProto md = DescriptionFactory.getProto(keyword, superDesc);
		if (md == null) {
			if (superDesc == null)
				throw new RuntimeException("Description of " + keyword + " cannot be built");
			else {
				superDesc.error("Unknown statement " + keyword, IGamlIssue.UNKNOWN_KEYWORD, source.getElement(),
						keyword);
			}
			return null;
		}
		Iterable<IDescription> children = cp;
		if (children == null) {
			final List<IDescription> childrenList = new ArrayList<>();
			final SyntacticVisitor visitor = element -> {
				final IDescription desc = create(element, superDesc, null);
				if (desc != null) { childrenList.add(desc); }

			};
			source.visitChildren(visitor);
			source.visitGrids(visitor);
			source.visitSpecies(visitor);
			source.visitExperiments(visitor);
			children = childrenList;
		}
		final Facets facets = source.copyFacets(md);
		final EObject element = source.getElement();
		return md.getFactory().buildDescription(keyword, facets, element, children, superDesc, md);

	}

	/**
	 * Gets the statement protos.
	 *
	 * @return the statement protos
	 */
	public static Iterable<SymbolProto> getStatementProtos() {
		return Iterables.concat(STATEMENT_KEYWORDS_PROTOS.values(), VAR_KEYWORDS_PROTOS.values());
	}

	/**
	 * Gets the facets protos.
	 *
	 * @return the facets protos
	 */
	public static Iterable<? extends FacetProto> getFacetsProtos() {
		return Iterables.concat(Iterables.transform(getStatementProtos(), (each) -> each.getPossibleFacets().values()));
	}

}