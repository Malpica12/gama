/*******************************************************************************************************
 *
 * AbstractGamlAdditions.java, in gama.core.kernel, is part of the source code of the GAMA modeling and simulation
 * platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gaml.compilation;

import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static gama.common.interfaces.IKeyword.OF;
import static gama.common.interfaces.IKeyword.SPECIES;
import static gama.common.interfaces.IKeyword._DOT;
import static gama.common.util.JavaUtils.collectImplementationClasses;
import static gama.util.GamaMapFactory.create;
import static gama.util.GamaMapFactory.createUnordered;
import static gaml.expressions.IExpressionCompiler.OPERATORS;
import static gaml.factories.DescriptionFactory.create;
import static gaml.factories.DescriptionFactory.getStatementProto;
import static gaml.factories.DescriptionFactory.getStatementProtoNames;
import static gaml.types.Types.getBuiltInSpecies;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;

import gama.common.interfaces.IExperimentAgentCreator;
import gama.common.interfaces.IExperimentAgentCreator.ExperimentAgentDescription;
import gama.common.interfaces.ISkill;
import gama.common.ui.IDisplayCreator;
import gama.common.ui.IDisplayCreator.DisplayDescription;
import gama.common.ui.IGui;
import gama.core.dev.annotations.GamlAnnotations;
import gama.core.dev.annotations.GamlAnnotations.doc;
import gama.core.dev.annotations.GamlAnnotations.vars;
import gama.core.dev.annotations.ISymbolKind;
import gama.core.dev.annotations.ITypeProvider;
import gama.util.GamaMapFactory;
import gama.util.file.IGamaFile;
import gaml.compilation.annotations.serializer;
import gaml.compilation.annotations.validator;
import gaml.compilation.kernel.GamaMetaModel;
import gaml.compilation.kernel.GamaSkillRegistry;
import gaml.descriptions.FacetProto;
import gaml.descriptions.IDescription;
import gaml.descriptions.IDescription.DescriptionVisitor;
import gaml.descriptions.OperatorProto;
import gaml.descriptions.PrimitiveDescription;
import gaml.descriptions.SkillDescription;
import gaml.descriptions.StatementDescription;
import gaml.descriptions.SymbolProto;
import gaml.descriptions.SymbolSerializer;
import gaml.descriptions.TypeDescription;
import gaml.descriptions.VariableDescription;
import gaml.expressions.IExpression;
import gaml.expressions.IExpressionCompiler;
import gaml.factories.DescriptionFactory;
import gaml.factories.SymbolFactory;
import gaml.types.GamaFileType;
import gaml.types.IType;
import gaml.types.ParametricFileType;
import gaml.types.Signature;
import gaml.types.Types;

// TODO: Auto-generated Javadoc
/**
 *
 * The class AbstractGamlAdditions. Default base implementation for plugins' gaml additions.
 *
 * @author drogoul
 * @since 17 mai 2012
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public abstract class AbstractGamlAdditions implements IGamlAdditions {

	/** The current plugin name. */
	public static String CURRENT_PLUGIN_NAME;

	/** The Constant CONSTANTS. */
	public static final Set<String> CONSTANTS = new HashSet();

	/** The Constant ADDITIONS. */
	final static Multimap<Class, IDescription> ADDITIONS = HashMultimap.create();

	/** The into descriptions. */
	private static Function<Class, Collection<IDescription>> INTO_DESCRIPTIONS = input -> ADDITIONS.get(input);

	/** The Constant FIELDS. */
	private final static Multimap<Class, OperatorProto> FIELDS = HashMultimap.create();

	/** The Constant VARTYPE2KEYWORDS. */
	public final static Multimap<Integer, String> VARTYPE2KEYWORDS = HashMultimap.create();

	/** The Constant TEMPORARY_BUILT_IN_VARS_DOCUMENTATION. */
	public final static Map<String, String> TEMPORARY_BUILT_IN_VARS_DOCUMENTATION = new HashMap<>();

	/** The Constant LISTENERS_BY_CLASS. */
	public final static HashMultimap<Class, GamaHelper> LISTENERS_BY_CLASS = HashMultimap.create();

	/** The Constant LISTENERS_BY_NAME. */
	public final static HashMultimap<String, Class> LISTENERS_BY_NAME = HashMultimap.create();

	/**
	 * S.
	 *
	 * @param strings
	 *            the strings
	 * @return the string[]
	 */
	protected static String[] S(final String... strings) {
		return strings;
	}

	/**
	 * I.
	 *
	 * @param integers
	 *            the integers
	 * @return the int[]
	 */
	protected static int[] I(final int... integers) {
		return integers;
	}

	/**
	 * P.
	 *
	 * @param protos
	 *            the protos
	 * @return the facet proto[]
	 */
	protected static FacetProto[] P(final FacetProto... protos) {
		return protos;
	}

	/**
	 * C.
	 *
	 * @param classes
	 *            the classes
	 * @return the class[]
	 */
	protected static Class[] C(final Class... classes) {
		return classes;
	}

	/**
	 * T.
	 *
	 * @param c
	 *            the c
	 * @return the i type
	 */
	protected static IType<?> T(final Class<?> c) {
		return Types.get(c);
	}

	/**
	 * Ti.
	 *
	 * @param c
	 *            the c
	 * @return the string
	 */
	protected static String Ti(final Class c) {
		return String.valueOf(Types.get(c).id());
	}

	/**
	 * Ts.
	 *
	 * @param c
	 *            the c
	 * @return the string
	 */
	protected static String Ts(final Class c) {
		return Types.get(c).toString();
	}

	/**
	 * T.
	 *
	 * @param c
	 *            the c
	 * @return the i type
	 */
	protected static IType T(final String c) {
		return Types.get(c);
	}

	/**
	 * T.
	 *
	 * @param c
	 *            the c
	 * @return the i type
	 */
	protected static IType T(final int c) {
		return Types.get(c);
	}

	/**
	 * Display.
	 *
	 * @param string
	 *            the string
	 * @param d
	 *            the d
	 */
	public void _display(final String string, final IDisplayCreator d) {
		CONSTANTS.add(string);
		final DisplayDescription dd = new DisplayDescription(d, string, CURRENT_PLUGIN_NAME);
		IGui.DISPLAYS.put(string, dd);
	}

	/**
	 * Experiment.
	 *
	 * @param string
	 *            the string
	 * @param d
	 *            the d
	 */
	public void _experiment(final String string, final IExperimentAgentCreator d) {
		CONSTANTS.add(string);
		final ExperimentAgentDescription ed = new ExperimentAgentDescription(d, string, CURRENT_PLUGIN_NAME);
		GamaMetaModel.INSTANCE.addExperimentAgentCreator(string, ed);
	}

	/**
	 * Species.
	 *
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 * @param helper
	 *            the helper
	 * @param skills
	 *            the skills
	 */
	public void _species(final String name, final Class clazz, final IAgentConstructor helper, final String... skills) {
		GamaMetaModel.INSTANCE.addSpecies(name, clazz, helper, skills);
		// DescriptionFactory.addSpeciesNameAsType(name);
	}

	/**
	 * Type.
	 *
	 * @param keyword
	 *            the keyword
	 * @param typeInstance
	 *            the type instance
	 * @param id
	 *            the id
	 * @param varKind
	 *            the var kind
	 * @param wraps
	 *            the wraps
	 */
	protected void _type(final String keyword, final IType typeInstance, final int id, final int varKind,
			final Class... wraps) {
		initType(keyword, typeInstance, id, varKind, wraps);
	}

	/**
	 * File.
	 *
	 * @param string
	 *            the string
	 * @param clazz
	 *            the clazz
	 * @param helper
	 *            the helper
	 * @param innerType
	 *            the inner type
	 * @param keyType
	 *            the key type
	 * @param contentType
	 *            the content type
	 * @param s
	 *            the s
	 */
	protected void _file(final String string, final Class clazz, final GamaGetter.Unary<IGamaFile<?, ?>> helper,
			final int innerType, final int keyType, final int contentType, final String[] s) {
		// helper.setSkillClass(clazz);
		GamaFileType.addFileTypeDefinition(string, Types.get(innerType), Types.get(keyType), Types.get(contentType),
				clazz, helper, s, CURRENT_PLUGIN_NAME);
		VARTYPE2KEYWORDS.put(ISymbolKind.Variable.CONTAINER, string + "_file");
	}

	/**
	 * Skill.
	 *
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 * @param species
	 *            the species
	 */
	protected void _skill(final String name, final Class clazz, final String... species) {
		GamaSkillRegistry.INSTANCE.register(name, clazz, CURRENT_PLUGIN_NAME, ADDITIONS.get(clazz), species);
	}

	/**
	 * Factories.
	 *
	 * @param factories
	 *            the factories
	 */
	protected void _factories(final SymbolFactory... factories) {
		for (final SymbolFactory f : factories) {
			DescriptionFactory.addFactory(f);
		}
	}

	/**
	 * Symbol.
	 *
	 * @param names
	 *            the names
	 * @param c
	 *            the c
	 * @param sKind
	 *            the s kind
	 * @param remote
	 *            the remote
	 * @param args
	 *            the args
	 * @param scope
	 *            the scope
	 * @param sequence
	 *            the sequence
	 * @param unique
	 *            the unique
	 * @param name_unique
	 *            the name unique
	 * @param contextKeywords
	 *            the context keywords
	 * @param contextKinds
	 *            the context kinds
	 * @param fmd
	 *            the fmd
	 * @param omissible
	 *            the omissible
	 * @param sc
	 *            the sc
	 */
	protected void _symbol(final String[] names, final Class c, final int sKind, final boolean remote,
			final boolean args, final boolean scope, final boolean sequence, final boolean unique,
			final boolean name_unique, final String[] contextKeywords, final int[] contextKinds, final FacetProto[] fmd,
			final String omissible, final ISymbolConstructor sc) {

		IValidator validator2 = null;
		SymbolSerializer serializer2 = null;
		final validator v = (validator) c.getAnnotation(validator.class);
		final serializer s = (serializer) c.getAnnotation(serializer.class);
		try {
			if (v != null) { validator2 = v.value().newInstance(); }
			if (s != null) { serializer2 = s.value().newInstance(); }
		} catch (InstantiationException | IllegalAccessException e) {}

		final Collection<String> keywords;
		if (ISymbolKind.Variable.KINDS.contains(sKind)) {
			keywords = VARTYPE2KEYWORDS.get(sKind);
			keywords.remove(SPECIES);
		} else {
			keywords = Arrays.asList(names);
		}
		if (fmd != null) {
			for (final FacetProto f : fmd) {
				f.buildDoc(c);
			}
		}

		final SymbolProto md = new SymbolProto(c, sequence, args, sKind, !scope, fmd, omissible, contextKeywords,
				contextKinds, remote, unique, name_unique, sc, validator2, serializer2,
				names == null || names.length == 0 ? "variable declaration" : names[0], CURRENT_PLUGIN_NAME);
		DescriptionFactory.addProto(md, keywords);
	}

	/**
	 * Iterator.
	 *
	 * @param keywords
	 *            the keywords
	 * @param method
	 *            the method
	 * @param classes
	 *            the classes
	 * @param expectedContentTypes
	 *            the expected content types
	 * @param ret
	 *            the ret
	 * @param c
	 *            the c
	 * @param t
	 *            the t
	 * @param content
	 *            the content
	 * @param index
	 *            the index
	 * @param contentContentType
	 *            the content content type
	 * @param helper
	 *            the helper
	 */
	public void _iterator(final String[] keywords, final Method method, final Class[] classes,
			final int[] expectedContentTypes, final Class ret, final boolean c, final int t, final int content,
			final int index, final int contentContentType, final GamaGetter.Binary helper) {
		IExpressionCompiler.ITERATORS.addAll(Arrays.asList(keywords));
		_binary(keywords, method, classes, expectedContentTypes, ret, c, t, content, index, contentContentType, helper);
	}

	/**
	 * Binary.
	 *
	 * @param keywords
	 *            the keywords
	 * @param method
	 *            the method
	 * @param classes
	 *            the classes
	 * @param expectedContentTypes
	 *            the expected content types
	 * @param returnClassOrType
	 *            the return class or type
	 * @param c
	 *            the c
	 * @param t
	 *            the t
	 * @param content
	 *            the content
	 * @param index
	 *            the index
	 * @param contentContentType
	 *            the content content type
	 * @param helper
	 *            the helper
	 */
	public void _binary(final String[] keywords, final AccessibleObject method, final Class[] classes,
			final int[] expectedContentTypes, final Object returnClassOrType, final boolean c, final int t,
			final int content, final int index, final int contentContentType, final GamaGetter.Binary helper) {
		final Signature signature = new Signature(classes);
		final String plugin = CURRENT_PLUGIN_NAME;
		for (final String keyword : keywords) {
			final String kw = keyword;
			if (!OPERATORS.containsKey(kw)) { OPERATORS.put(kw, GamaMapFactory.createUnordered()); }
			final Map<Signature, OperatorProto> map = OPERATORS.get(kw);
			if (!map.containsKey(signature)) {
				OperatorProto proto;
				IType rt;
				if (returnClassOrType instanceof Class) {
					rt = Types.get((Class) returnClassOrType);
				} else {
					rt = (IType) returnClassOrType;
				}
				// binary
				if ((OF.equals(kw) || _DOT.equals(kw)) && signature.get(0).isAgentType()) {
					proto = new OperatorProto(kw, method, helper, c, true, rt, signature,
							IExpression.class.equals(classes[1]), t, content, index, contentContentType,
							expectedContentTypes, plugin);
				} else {
					proto = new OperatorProto(kw, method, helper, c, false, rt, signature,
							IExpression.class.equals(classes[1]), t, content, index, contentContentType,
							expectedContentTypes, plugin);
				}

				map.put(signature, proto);
			}
		}

	}

	/**
	 * Operator.
	 *
	 * @param keywords
	 *            the keywords
	 * @param method
	 *            the method
	 * @param classes
	 *            the classes
	 * @param expectedContentTypes
	 *            the expected content types
	 * @param returnClassOrType
	 *            the return class or type
	 * @param c
	 *            the c
	 * @param t
	 *            the t
	 * @param content
	 *            the content
	 * @param index
	 *            the index
	 * @param contentContentType
	 *            the content content type
	 * @param helper
	 *            the helper
	 */
	public void _operator(final String[] keywords, final AccessibleObject method, final Class[] classes,
			final int[] expectedContentTypes, final Object returnClassOrType, final boolean c, final int t,
			final int content, final int index, final int contentContentType, final GamaGetter.NAry helper) {
		final Signature signature = new Signature(classes);
		final String plugin = CURRENT_PLUGIN_NAME;
		for (final String keyword : keywords) {
			final String kw = keyword;
			if (!OPERATORS.containsKey(kw)) { OPERATORS.put(kw, GamaMapFactory.createUnordered()); }
			final Map<Signature, OperatorProto> map = OPERATORS.get(kw);
			if (!map.containsKey(signature)) {
				OperatorProto proto;
				IType rt;
				if (returnClassOrType instanceof Class) {
					rt = Types.get((Class) returnClassOrType);
				} else {
					rt = (IType) returnClassOrType;
				}
				if (classes.length == 1) { // unary
					proto = new OperatorProto(kw, method, helper, c, false, rt, signature,
							IExpression.class.equals(classes[0]), t, content, index, contentContentType,
							expectedContentTypes, plugin);
				} else if (classes.length == 2) { // binary
					if ((OF.equals(kw) || _DOT.equals(kw)) && signature.get(0).isAgentType()) {
						proto = new OperatorProto(kw, method, helper, c, true, rt, signature,
								IExpression.class.equals(classes[1]), t, content, index, contentContentType,
								expectedContentTypes, plugin);
					} else {
						proto = new OperatorProto(kw, method, helper, c, false, rt, signature,
								IExpression.class.equals(classes[1]), t, content, index, contentContentType,
								expectedContentTypes, plugin);
					}
				} else {
					proto = new OperatorProto(kw, method, helper, c, false, rt, signature,
							IExpression.class.equals(classes[classes.length - 1]), t, content, index,
							contentContentType, expectedContentTypes, plugin);
				}
				map.put(signature, proto);
			}
		}

	}

	/**
	 * Listener.
	 *
	 * @param varName
	 *            the var name
	 * @param clazz
	 *            the clazz
	 * @param helper
	 *            the helper
	 */
	public void _listener(final String varName, final Class clazz, final IGamaHelper helper) {
		GamaHelper gh = new GamaHelper(varName, clazz, helper);
		LISTENERS_BY_CLASS.put(clazz, gh);
		LISTENERS_BY_NAME.put(varName, clazz);
	}

	/**
	 * Unary.
	 *
	 * @param keywords
	 *            the keywords
	 * @param method
	 *            the method
	 * @param classes
	 *            the classes
	 * @param expectedContentTypes
	 *            the expected content types
	 * @param returnClassOrType
	 *            the return class or type
	 * @param c
	 *            the c
	 * @param t
	 *            the t
	 * @param content
	 *            the content
	 * @param index
	 *            the index
	 * @param contentContentType
	 *            the content content type
	 * @param helper
	 *            the helper
	 */
	public void _unary(final String[] keywords, final AccessibleObject method, final Class[] classes,
			final int[] expectedContentTypes, final Object returnClassOrType, final boolean c, final int t,
			final int content, final int index, final int contentContentType, final GamaGetter.Unary helper) {
		final Signature signature = new Signature(classes);
		final String plugin = CURRENT_PLUGIN_NAME;
		for (final String keyword : keywords) {
			final String kw = keyword;
			if (!OPERATORS.containsKey(kw)) { OPERATORS.put(kw, createUnordered()); }
			final Map<Signature, OperatorProto> map = OPERATORS.get(kw);
			if (!map.containsKey(signature)) {
				OperatorProto proto;
				IType rt;
				if (returnClassOrType instanceof Class) {
					rt = Types.get((Class) returnClassOrType);
				} else {
					rt = (IType) returnClassOrType;
				}
				proto = new OperatorProto(kw, method, helper, c, false, rt, signature,
						IExpression.class.equals(classes[0]), t, content, index, contentContentType,
						expectedContentTypes, plugin);
				map.put(signature, proto);
			}
		}

	}

	/**
	 * Operator.
	 *
	 * @param keywords
	 *            the keywords
	 * @param method
	 *            the method
	 * @param classes
	 *            the classes
	 * @param expectedContentTypes
	 *            the expected content types
	 * @param ret
	 *            the ret
	 * @param c
	 *            the c
	 * @param typeAlias
	 *            the type alias
	 * @param helper
	 *            the helper
	 */
	// For files
	public void _operator(final String[] keywords, final AccessibleObject method, final Class[] classes,
			final int[] expectedContentTypes, final Class ret, final boolean c, final String typeAlias,
			final GamaGetter.NAry helper) {
		final ParametricFileType fileType = GamaFileType.getTypeFromAlias(typeAlias);
		int indexOfIType = -1;
		for (int i = 0; i < classes.length; i++) {
			final Class cl = classes[i];
			if (IType.class.isAssignableFrom(cl)) { indexOfIType = i; }
		}
		final int content =
				indexOfIType == -1 ? ITypeProvider.NONE : ITypeProvider.DENOTED_TYPE_AT_INDEX + indexOfIType + 1;
		this._operator(keywords, method, classes, expectedContentTypes, fileType, c, ITypeProvider.NONE, content,
				ITypeProvider.NONE, ITypeProvider.NONE, helper);
	}

	/**
	 * Binary.
	 *
	 * @param keywords
	 *            the keywords
	 * @param method
	 *            the method
	 * @param classes
	 *            the classes
	 * @param expectedContentTypes
	 *            the expected content types
	 * @param ret
	 *            the ret
	 * @param c
	 *            the c
	 * @param typeAlias
	 *            the type alias
	 * @param helper
	 *            the helper
	 */
	public void _binary(final String[] keywords, final AccessibleObject method, final Class[] classes,
			final int[] expectedContentTypes, final Class ret, final boolean c, final String typeAlias,
			final GamaGetter.Binary helper) {
		final ParametricFileType fileType = GamaFileType.getTypeFromAlias(typeAlias);
		int indexOfIType = -1;
		for (int i = 0; i < classes.length; i++) {
			final Class cl = classes[i];
			if (IType.class.isAssignableFrom(cl)) { indexOfIType = i; }
		}
		final int content =
				indexOfIType == -1 ? ITypeProvider.NONE : ITypeProvider.DENOTED_TYPE_AT_INDEX + indexOfIType + 1;
		this._binary(keywords, method, classes, expectedContentTypes, fileType, c, ITypeProvider.NONE, content,
				ITypeProvider.NONE, ITypeProvider.NONE, helper);
	}

	/**
	 * Adds the.
	 *
	 * @param clazz
	 *            the clazz
	 * @param desc
	 *            the desc
	 */
	private void add(final Class clazz, final IDescription desc) {
		ADDITIONS.put(clazz, desc);
	}

	/**
	 * Var.
	 *
	 * @param clazz
	 *            the clazz
	 * @param desc
	 *            the desc
	 * @param get
	 *            the get
	 * @param init
	 *            the init
	 * @param set
	 *            the set
	 */
	protected void _var(final Class clazz, final IDescription desc, final IGamaHelper get, final IGamaHelper init,
			final IGamaHelper set) {
		add(clazz, desc);
		((VariableDescription) desc).addHelpers(clazz, get, init, set);
		TEMPORARY_BUILT_IN_VARS_DOCUMENTATION.putIfAbsent(desc.getName(), getVarDoc(desc.getName(), clazz));
		((VariableDescription) desc).setDefiningPlugin(CURRENT_PLUGIN_NAME);
	}

	/**
	 * Gets the var doc.
	 *
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 * @return the var doc
	 */
	private String getVarDoc(final String name, final Class<?> clazz) {
		final vars vars = clazz.getAnnotationsByType(vars.class)[0];
		for (final GamlAnnotations.variable v : vars.value()) {
			if (v.name().equals(name)) {
				final doc[] docs = v.doc();
				// final String d = "";
				if (docs.length > 0) // documentation of fields is not used
					return docs[0].value();
			}
		}
		return "";
	}

	/**
	 * Facet.
	 *
	 * @param name
	 *            the name
	 * @param types
	 *            the types
	 * @param ct
	 *            the ct
	 * @param kt
	 *            the kt
	 * @param values
	 *            the values
	 * @param optional
	 *            the optional
	 * @param internal
	 *            the internal
	 * @param isRemote
	 *            the is remote
	 * @return the facet proto
	 */
	protected FacetProto _facet(final String name, final int[] types, final int ct, final int kt, final String[] values,
			final boolean optional, final boolean internal, final boolean isRemote) {
		return new FacetProto(name, types, ct, kt, values, optional, internal, isRemote);
	}

	/**
	 * Proto.
	 *
	 * @param name
	 *            the name
	 * @param helper
	 *            the helper
	 * @param returnType
	 *            the return type
	 * @param signature
	 *            the signature
	 * @param typeProvider
	 *            the type provider
	 * @param contentTypeProvider
	 *            the content type provider
	 * @param keyTypeProvider
	 *            the key type provider
	 * @return the operator proto
	 */
	protected OperatorProto _proto(final String name, final GamaGetter.Unary helper, final int returnType,
			final Class signature, final int typeProvider, final int contentTypeProvider, final int keyTypeProvider) {
		return new OperatorProto(name, null, helper, false, true, returnType, signature, false, typeProvider,
				contentTypeProvider, keyTypeProvider, AI, CURRENT_PLUGIN_NAME);
	}

	/**
	 * Field.
	 *
	 * @param clazz
	 *            the clazz
	 * @param getter
	 *            the getter
	 */
	protected void _field(final Class clazz, final OperatorProto getter) {
		FIELDS.put(clazz, getter);
	}

	/**
	 * Desc.
	 *
	 * @param keyword
	 *            the keyword
	 * @param children
	 *            the children
	 * @param facets
	 *            the facets
	 * @return the i description
	 */
	protected IDescription desc(final String keyword, final Children children, final String... facets) {
		return create(keyword, null, children.getChildren(), facets);
	}

	/**
	 * Desc.
	 *
	 * @param keyword
	 *            the keyword
	 * @param facets
	 *            the facets
	 * @return the i description
	 */
	protected IDescription desc(final String keyword, final String... facets) {
		return create(keyword, facets);
	}

	/**
	 * Creates a VariableDescription.
	 *
	 * @param keyword
	 *            the keyword
	 * @param facets
	 *            the facets
	 * @return the i description
	 */
	protected IDescription desc(final int keyword, final String... facets) {
		final IType t = Types.get(keyword);
		if (t == null) throw new RuntimeException("Types not defined");
		return desc(t.toString(), facets);
	}

	/**
	 * Action.
	 *
	 * @param e
	 *            the e
	 * @param desc
	 *            the desc
	 * @param method
	 *            the method
	 */
	protected void _action(final IGamaHelper e, final IDescription desc, final Method method) {
		final Class clazz = method.getDeclaringClass();
		((PrimitiveDescription) desc).setHelper(new GamaHelper(desc.getName(), clazz, e), method);
		((PrimitiveDescription) desc).setDefiningPlugin(CURRENT_PLUGIN_NAME);
		add(clazz, desc);
	}

	/**
	 * Inits the type.
	 *
	 * @param keyword
	 *            the keyword
	 * @param typeInstance
	 *            the type instance
	 * @param id
	 *            the id
	 * @param varKind
	 *            the var kind
	 * @param wraps
	 *            the wraps
	 */
	public static void initType(final String keyword, final IType<?> typeInstance, final int id, final int varKind,
			final Class... wraps) {
		final IType<?> type = Types.builtInTypes.initType(keyword, typeInstance, id, varKind, wraps[0]);
		for (final Class cc : wraps) {
			Types.CLASSES_TYPES_CORRESPONDANCE.put(cc, type.getName());
		}
		type.setDefiningPlugin(CURRENT_PLUGIN_NAME);
		Types.cache(id, typeInstance);
		VARTYPE2KEYWORDS.put(varKind, keyword);
	}

	/**
	 * Gets the additions.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the additions
	 */
	public static Collection<IDescription> getAdditions(final Class clazz) {
		return ADDITIONS.get(clazz);
	}

	/**
	 * Gets the all fields.
	 *
	 * @param clazz
	 *            the clazz
	 * @return the all fields
	 */
	public static Map<String, OperatorProto> getAllFields(final Class clazz) {
		final List<Class> classes = collectImplementationClasses(clazz, Collections.EMPTY_SET, FIELDS.keySet());
		final Map<String, OperatorProto> fieldsMap = create();
		for (final Class c : classes) {
			for (final OperatorProto desc : FIELDS.get(c)) {
				fieldsMap.put(desc.getName(), desc);
			}
		}
		return fieldsMap;
	}

	/**
	 * Gets the all children of.
	 *
	 * @param base
	 *            the base
	 * @param skills
	 *            the skills
	 * @return the all children of
	 */
	public static Iterable<IDescription> getAllChildrenOf(final Class base,
			final Iterable<Class<? extends ISkill>> skills) {
		final List<Class> classes = collectImplementationClasses(base, skills, ADDITIONS.keySet());
		return concat(transform(classes, INTO_DESCRIPTIONS));
	}

	/**
	 * Gets the all fields.
	 *
	 * @return the all fields
	 */
	public static Collection<OperatorProto> getAllFields() { return FIELDS.values(); }

	/**
	 * Gets the all vars.
	 *
	 * @return the all vars
	 */
	public static Collection<IDescription> getAllVars() {
		final HashSet<IDescription> result = new HashSet<>();

		final DescriptionVisitor<IDescription> varVisitor = desc -> {
			result.add(desc);
			return true;
		};

		final DescriptionVisitor<IDescription> actionVisitor = desc -> {
			addAll(result, ((StatementDescription) desc).getFormalArgs());
			return true;
		};

		for (final TypeDescription desc : Types.getBuiltInSpecies()) {
			desc.visitOwnAttributes(varVisitor);
			desc.visitOwnActions(actionVisitor);

		}
		GamaSkillRegistry.INSTANCE.visitSkills(desc -> {
			((TypeDescription) desc).visitOwnAttributes(varVisitor);
			((TypeDescription) desc).visitOwnActions(actionVisitor);
			return true;
		});

		return result;
	}

	/**
	 * Gets the statements for skill.
	 *
	 * @param s
	 *            the s
	 * @return the statements for skill
	 */
	public static Collection<SymbolProto> getStatementsForSkill(final String s) {
		final Set<SymbolProto> result = new LinkedHashSet();
		for (final String p : getStatementProtoNames()) {
			final SymbolProto proto = getStatementProto(p, s);
			if (proto != null && proto.shouldBeDefinedIn(s)) { result.add(proto); }
		}
		return result;
	}

	/**
	 * Gets the all actions.
	 *
	 * @return the all actions
	 */
	public static Collection<IDescription> getAllActions() {
		SetMultimap<String, IDescription> result = MultimapBuilder.hashKeys().linkedHashSetValues().build();

		final DescriptionVisitor<IDescription> visitor = desc -> {
			result.put(desc.getName(), desc);
			return true;
		};

		for (final TypeDescription s : getBuiltInSpecies()) {
			s.visitOwnActions(visitor);
		}
		GamaSkillRegistry.INSTANCE.visitSkills(desc -> {
			((SkillDescription) desc).visitOwnActions(visitor);
			return true;
		});
		return result.values();
	}

	/**
	 * Constants.
	 *
	 * @param strings
	 *            the strings
	 */
	public static void _constants(final String[]... strings) {
		for (final String[] s : strings) {
			Collections.addAll(CONSTANTS, s);
		}
	}

	/**
	 * Checks if is unary operator.
	 *
	 * @param name
	 *            the name
	 * @return true, if is unary operator
	 */
	public static boolean isUnaryOperator(final String name) {
		if (!OPERATORS.containsKey(name)) return false;
		final Map<Signature, OperatorProto> map = OPERATORS.get(name);
		for (final Signature s : map.keySet()) {
			if (s.isUnary()) return true;
		}
		return false;
	}

}