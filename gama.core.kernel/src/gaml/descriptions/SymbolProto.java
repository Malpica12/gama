/*******************************************************************************************************
 *
 * SymbolProto.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gaml.descriptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.GamlAnnotations.action;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.symbol;
import gama.util.GamaMapFactory;
import gaml.compilation.ISymbol;
import gaml.compilation.ISymbolConstructor;
import gaml.compilation.IValidator;
import gaml.factories.DescriptionFactory;
import gaml.factories.SymbolFactory;
import gaml.statements.Facets;
import gaml.types.IType;

import com.google.common.collect.Iterables;

/**
 * Written by drogoul Modified on 8 févr. 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class SymbolProto extends AbstractProto {

	/** The constructor. */
	private final ISymbolConstructor constructor;
	
	/** The validator. */
	private final IValidator validator;
	
	/** The serializer. */
	private SymbolSerializer serializer;
	
	/** The factory. */
	private final SymbolFactory factory;

	/** The kind. */
	private final int kind;
	
	/** The is unique in context. */
	private final boolean hasSequence, hasArgs, hasScope, isRemoteContext, isUniqueInContext;
	
	/** The context keywords. */
	private final ImmutableSet<String> contextKeywords;
	
	/** The context kinds. */
	private final boolean[] contextKinds = new boolean[ISymbolKind.__NUMBER__];
	
	/** The possible facets. */
	private final Map<String, FacetProto> possibleFacets;
	
	/** The mandatory facets. */
	private final ImmutableSet<String> mandatoryFacets;
	
	/** The omissible facet. */
	private final String omissibleFacet;
	
	/** The is primitive. */
	private final boolean isPrimitive;
	
	/** The is var. */
	private final boolean isVar;

	/** The Constant ids. */
	static final List<Integer> ids = Arrays.asList(IType.LABEL, IType.ID, IType.NEW_TEMP_ID, IType.NEW_VAR_ID);

	/**
	 * Instantiates a new symbol proto.
	 *
	 * @param clazz the clazz
	 * @param hasSequence the has sequence
	 * @param hasArgs the has args
	 * @param kind the kind
	 * @param doesNotHaveScope the does not have scope
	 * @param possibleFacets the possible facets
	 * @param omissible the omissible
	 * @param contextKeywords the context keywords
	 * @param parentKinds the parent kinds
	 * @param isRemoteContext the is remote context
	 * @param isUniqueInContext the is unique in context
	 * @param nameUniqueInContext the name unique in context
	 * @param constr the constr
	 * @param validator the validator
	 * @param serializer the serializer
	 * @param name the name
	 * @param plugin the plugin
	 */
	public SymbolProto(final Class clazz, final boolean hasSequence, final boolean hasArgs, final int kind,
			final boolean doesNotHaveScope, final FacetProto[] possibleFacets, final String omissible,
			final String[] contextKeywords, final int[] parentKinds, final boolean isRemoteContext,
			final boolean isUniqueInContext, final boolean nameUniqueInContext, final ISymbolConstructor constr,
			final IValidator validator, final SymbolSerializer serializer, final String name, final String plugin) {
		super(name, clazz, plugin);
		factory = DescriptionFactory.getFactory(kind);
		this.validator = validator;
		this.serializer = serializer;
		constructor = constr;
		this.isRemoteContext = isRemoteContext;
		this.hasSequence = hasSequence;
		this.isPrimitive = IKeyword.PRIMITIVE.equals(name);
		this.hasArgs = hasArgs;
		this.omissibleFacet = omissible;
		this.isUniqueInContext = isUniqueInContext;
		this.kind = kind;
		this.isVar = ISymbolKind.Variable.KINDS.contains(kind);
		this.hasScope = !doesNotHaveScope;
		if (possibleFacets != null) {
			final Builder<String> builder = ImmutableSet.builder();
			this.possibleFacets = GamaMapFactory.createUnordered();
			for (final FacetProto f : possibleFacets) {
				this.possibleFacets.put(f.name, f);
				f.setOwner(getTitle());
				if (!f.optional) {
					builder.add(f.name);
				}
			}
			mandatoryFacets = builder.build();
		} else {
			this.possibleFacets = null;
			mandatoryFacets = null;
		}
		this.contextKeywords = ImmutableSet.copyOf(contextKeywords);
		Arrays.fill(this.contextKinds, false);
		for (final int i : parentKinds) {
			contextKinds[i] = true;
		}
	}

	/**
	 * Gets the factory.
	 *
	 * @return the factory
	 */
	public SymbolFactory getFactory() {
		return factory;
	}

	/**
	 * Checks if is remote context.
	 *
	 * @return true, if is remote context
	 */
	public boolean isRemoteContext() {
		return isRemoteContext;
	}

	/**
	 * Checks if is label.
	 *
	 * @param s the s
	 * @return true, if is label
	 */
	public boolean isLabel(final String s) {
		final FacetProto f = getPossibleFacets().get(s);
		if (f == null) { return false; }
		return f.isLabel();
	}

	/**
	 * Checks if is id.
	 *
	 * @param s the s
	 * @return true, if is id
	 */
	public boolean isId(final String s) {
		final FacetProto f = getPossibleFacets().get(s);
		if (f == null) { return false; }
		return f.isId();
	}

	/**
	 * Checks for sequence.
	 *
	 * @return true, if successful
	 */
	public boolean hasSequence() {
		return hasSequence;
	}

	/**
	 * Checks if is primitive.
	 *
	 * @return true, if is primitive
	 */
	public boolean isPrimitive() {
		return isPrimitive;
	}

	/**
	 * Checks for args.
	 *
	 * @return true, if successful
	 */
	public boolean hasArgs() {
		return hasArgs;
	}

	/**
	 * Checks for scope.
	 *
	 * @return true, if successful
	 */
	public boolean hasScope() {
		return hasScope;
	}

	/**
	 * Gets the possible facets.
	 *
	 * @return the possible facets
	 */
	public Map<String, FacetProto> getPossibleFacets() {
		return possibleFacets == null ? Collections.emptyMap() : possibleFacets;
	}

	/**
	 * Checks if is top level.
	 *
	 * @return true, if is top level
	 */
	public boolean isTopLevel() {
		return kind == ISymbolKind.BEHAVIOR;
	}

	@Override
	public int getKind() {
		return kind;
	}

	/**
	 * Gets the constructor.
	 *
	 * @return the constructor
	 */
	public ISymbolConstructor getConstructor() {
		return constructor;
	}

	/**
	 * Gets the omissible.
	 *
	 * @return the omissible
	 */
	public String getOmissible() {
		return omissibleFacet;
	}

	@Override
	public String getTitle() {
		return isVar ? ISymbolKind.Variable.KINDS_AS_STRING.get(kind) + " declaration" : "statement " + getName();
	}

	@Override
	public doc getDocAnnotation() {
		if (support == null) { return null; }
		doc d = super.getDocAnnotation();
		if (d == null) {
			if (support.isAnnotationPresent(action.class)) {
				final doc[] docs = support.getAnnotation(action.class).doc();
				if (docs.length > 0) {
					d = docs[0];
				}
			} else if (support.isAnnotationPresent(symbol.class)) {
				final doc[] docs = support.getAnnotation(symbol.class).doc();
				if (docs.length > 0) {
					d = docs[0];
				}
			}
		}

		return d;
	}

	/**
	 * @return
	 */
	@Override
	public String getDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append(super.getDocumentation());
		sb.append(getFacetsDocumentation());
		return sb.toString();
	}

	/**
	 * Gets the facets documentation.
	 *
	 * @return the facets documentation
	 */
	public String getFacetsDocumentation() {
		final StringBuilder sb = new StringBuilder(200);
		sb.append("<b><br/>Possible facets :</b><ul>");
		final List<FacetProto> protos = new ArrayList(getPossibleFacets().values());
		Collections.sort(protos);
		for (final FacetProto f : protos) {
			if (!f.internal) {
				sb.append("<li>").append(f.getDocumentation());
			}
			sb.append("</li>");
		}
		return sb.toString();

	}

	/**
	 * Checks if is breakable.
	 *
	 * @return true, if is breakable
	 */
	public boolean isBreakable() {
		final String name = getName();
		return IKeyword.ASK.equals(name) || IKeyword.LOOP.equals(name) || IKeyword.SWITCH.equals(name);
	}

	/**
	 * Gets the validator.
	 *
	 * @return the validator
	 */
	IValidator getValidator() {
		return validator;
	}

	/**
	 * Gets the serializer.
	 *
	 * @return the serializer
	 */
	public SymbolSerializer getSerializer() {
		return serializer;
	}

	/**
	 * Sets the serializer.
	 *
	 * @param serializer the new serializer
	 */
	public void setSerializer(final SymbolSerializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * Creates the.
	 *
	 * @param description the description
	 * @return the i symbol
	 */
	public ISymbol create(final SymbolDescription description) {
		return constructor.create(description);
	}

	/**
	 * Can be defined in.
	 *
	 * @param sd the sd
	 * @return true, if successful
	 */
	public boolean canBeDefinedIn(final IDescription sd) {
		return contextKinds[sd.getKind()] || contextKeywords.contains(sd.getKeyword());
	}

	/**
	 * Should be defined in.
	 *
	 * @param context the context
	 * @return true, if successful
	 */
	public boolean shouldBeDefinedIn(final String context) {
		return contextKeywords.contains(context);
	}

	/**
	 * Checks if is unique in context.
	 *
	 * @return true, if is unique in context
	 */
	public boolean isUniqueInContext() {
		return isUniqueInContext;
	}

	/**
	 * Gets the facet.
	 *
	 * @param facet the facet
	 * @return the facet
	 */
	public FacetProto getFacet(final String facet) {
		return possibleFacets == null ? null : possibleFacets.get(facet);
	}

	/**
	 * Gets the missing mandatory facets.
	 *
	 * @param facets the facets
	 * @return the missing mandatory facets
	 */
	public Iterable<String> getMissingMandatoryFacets(final Facets facets) {
		if (facets == null || facets.isEmpty()) {
			if (mandatoryFacets == null || mandatoryFacets.isEmpty()) { return null; }
			return mandatoryFacets;
		}
		return Iterables.filter(mandatoryFacets, each -> !facets.containsKey(each));
	}

	/**
	 * Method serialize()
	 *
	 * @see gama.common.interfaces.IGamlable#serialize(boolean)
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		for (final FacetProto f : possibleFacets.values()) {
			final String s = f.serialize(includingBuiltIn);
			if (!s.isEmpty()) {
				sb.append(s).append(" ");
			}
		}
		return getName() + " " + sb.toString();
	}
	//
	// @Override
	// public void collectMetaInformation(final GamlProperties meta) {
	// meta.put(GamlProperties.STATEMENTS, name);
	// }

}