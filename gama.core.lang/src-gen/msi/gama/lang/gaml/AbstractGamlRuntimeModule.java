/*******************************************************************************************************
 *
 * AbstractGamlRuntimeModule.java, in gama.core.lang, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml;

import java.util.Properties;

import org.eclipse.xtext.Constants;
import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.parser.ITokenToStringConverter;
import org.eclipse.xtext.parser.antlr.AntlrTokenDefProvider;
import org.eclipse.xtext.parser.antlr.AntlrTokenToStringConverter;
import org.eclipse.xtext.parser.antlr.IAntlrTokenFileProvider;
import org.eclipse.xtext.parser.antlr.ITokenDefProvider;
import org.eclipse.xtext.parser.antlr.Lexer;
import org.eclipse.xtext.parser.antlr.LexerBindings;
import org.eclipse.xtext.parser.antlr.LexerProvider;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.resource.containers.ResourceSetBasedAllContainersStateProvider;
import org.eclipse.xtext.resource.containers.StateBasedContainerManager;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.resource.impl.ResourceSetBasedResourceDescriptions;
import org.eclipse.xtext.scoping.IGlobalScopeProvider;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.IgnoreCaseLinking;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.scoping.impl.DefaultGlobalScopeProvider;
import org.eclipse.xtext.scoping.impl.ImportedNamespaceAwareLocalScopeProvider;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.serializer.impl.Serializer;
import org.eclipse.xtext.serializer.sequencer.ISemanticSequencer;
import org.eclipse.xtext.serializer.sequencer.ISyntacticSequencer;
import org.eclipse.xtext.service.DefaultRuntimeModule;
import org.eclipse.xtext.service.SingletonBinding;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import gama.core.lang.GamlRuntimeModule;
import gama.core.lang.generator.GamlGenerator;
import gama.core.lang.scoping.GamlScopeProvider;
import gama.core.lang.serializer.GamlSemanticSequencer;
import gama.core.lang.serializer.GamlSyntacticSequencer;
import gama.core.lang.validation.GamlValidator;
import msi.gama.lang.gaml.parser.antlr.GamlAntlrTokenFileProvider;
import msi.gama.lang.gaml.parser.antlr.GamlParser;
import msi.gama.lang.gaml.parser.antlr.internal.InternalGamlLexer;
import msi.gama.lang.gaml.services.GamlGrammarAccess;

/**
 * Manual modifications go to {@link GamlRuntimeModule}.
 */
@SuppressWarnings ("all")
public abstract class AbstractGamlRuntimeModule extends DefaultRuntimeModule {

	/** The properties. */
	protected Properties properties = null;

	@Override
	public void configure(final Binder binder) {
		properties = tryBindProperties(binder, "msi/gama/lang/gaml/Gaml.properties");
		super.configure(binder);
	}

	/**
	 * Configure language name.
	 *
	 * @param binder the binder
	 */
	public void configureLanguageName(final Binder binder) {
		binder.bind(String.class).annotatedWith(Names.named(Constants.LANGUAGE_NAME))
				.toInstance("msi.gama.lang.gaml.Gaml");
	}

	/**
	 * Configure file extensions.
	 *
	 * @param binder the binder
	 */
	public void configureFileExtensions(final Binder binder) {
		if (properties == null || properties.getProperty(Constants.FILE_EXTENSIONS) == null) {
			binder.bind(String.class).annotatedWith(Names.named(Constants.FILE_EXTENSIONS))
					.toInstance("gaml,experiment");
		}
	}

	/**
	 * Bind class loader to instance.
	 *
	 * @return the class loader
	 */
	// contributed by org.eclipse.xtext.xtext.generator.grammarAccess.GrammarAccessFragment2
	public ClassLoader bindClassLoaderToInstance() {
		return getClass().getClassLoader();
	}

	/**
	 * Bind I grammar access.
	 *
	 * @return the class<? extends I grammar access>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.grammarAccess.GrammarAccessFragment2
	public Class<? extends IGrammarAccess> bindIGrammarAccess() {
		return GamlGrammarAccess.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.serializer.SerializerFragment2
	@Override
	public Class<? extends ISemanticSequencer> bindISemanticSequencer() {
		return GamlSemanticSequencer.class;
	}

	/**
	 * Bind I syntactic sequencer.
	 *
	 * @return the class<? extends I syntactic sequencer>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.serializer.SerializerFragment2
	public Class<? extends ISyntacticSequencer> bindISyntacticSequencer() {
		return GamlSyntacticSequencer.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.serializer.SerializerFragment2
	@Override
	public Class<? extends ISerializer> bindISerializer() {
		return Serializer.class;
	}

	/**
	 * Bind I parser.
	 *
	 * @return the class<? extends I parser>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends IParser> bindIParser() {
		return GamlParser.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	@Override
	public Class<? extends ITokenToStringConverter> bindITokenToStringConverter() {
		return AntlrTokenToStringConverter.class;
	}

	/**
	 * Bind I antlr token file provider.
	 *
	 * @return the class<? extends I antlr token file provider>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends IAntlrTokenFileProvider> bindIAntlrTokenFileProvider() {
		return GamlAntlrTokenFileProvider.class;
	}

	/**
	 * Bind lexer.
	 *
	 * @return the class<? extends lexer>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends Lexer> bindLexer() {
		return InternalGamlLexer.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	@Override
	public Class<? extends ITokenDefProvider> bindITokenDefProvider() {
		return AntlrTokenDefProvider.class;
	}

	/**
	 * Provide internal gaml lexer.
	 *
	 * @return the provider<? extends internal gaml lexer>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Provider<? extends InternalGamlLexer> provideInternalGamlLexer() {
		return LexerProvider.create(InternalGamlLexer.class);
	}

	/**
	 * Configure runtime lexer.
	 *
	 * @param binder the binder
	 */
	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public void configureRuntimeLexer(final Binder binder) {
		binder.bind(Lexer.class).annotatedWith(Names.named(LexerBindings.RUNTIME)).to(InternalGamlLexer.class);
	}

	/**
	 * Bind gaml validator.
	 *
	 * @return the class<? extends gaml validator>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.validation.ValidatorFragment2
	@SingletonBinding (
			eager = true)
	public Class<? extends GamlValidator> bindGamlValidator() {
		return GamlValidator.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.scoping.ImportNamespacesScopingFragment2
	@Override
	public Class<? extends IScopeProvider> bindIScopeProvider() {
		return GamlScopeProvider.class;
	}

	/**
	 * Configure I scope provider delegate.
	 *
	 * @param binder the binder
	 */
	// contributed by org.eclipse.xtext.xtext.generator.scoping.ImportNamespacesScopingFragment2
	public void configureIScopeProviderDelegate(final Binder binder) {
		binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE))
				.to(ImportedNamespaceAwareLocalScopeProvider.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.scoping.ImportNamespacesScopingFragment2
	@Override
	public Class<? extends IGlobalScopeProvider> bindIGlobalScopeProvider() {
		return DefaultGlobalScopeProvider.class;
	}

	/**
	 * Configure ignore case linking.
	 *
	 * @param binder the binder
	 */
	// contributed by org.eclipse.xtext.xtext.generator.scoping.ImportNamespacesScopingFragment2
	public void configureIgnoreCaseLinking(final Binder binder) {
		binder.bindConstant().annotatedWith(IgnoreCaseLinking.class).to(false);
	}

	// contributed by org.eclipse.xtext.xtext.generator.exporting.QualifiedNamesFragment2
	@Override
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return DefaultDeclarativeQualifiedNameProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	@Override
	public Class<? extends IContainer.Manager> bindIContainer$Manager() {
		return StateBasedContainerManager.class;
	}

	/**
	 * Bind I all containers state$ provider.
	 *
	 * @return the class<? extends I all containers state. provider>
	 */
	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	public Class<? extends IAllContainersState.Provider> bindIAllContainersState$Provider() {
		return ResourceSetBasedAllContainersStateProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	@Override
	public void configureIResourceDescriptions(final Binder binder) {
		binder.bind(IResourceDescriptions.class).to(ResourceSetBasedResourceDescriptions.class);
	}

	/**
	 * Configure I resource descriptions persisted.
	 *
	 * @param binder the binder
	 */
	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	public void configureIResourceDescriptionsPersisted(final Binder binder) {
		binder.bind(IResourceDescriptions.class)
				.annotatedWith(Names.named(ResourceDescriptionsProvider.PERSISTED_DESCRIPTIONS))
				.to(ResourceSetBasedResourceDescriptions.class);
	}

	/**
	 * Bind I generator 2.
	 *
	 * @return the class<? extends I generator 2 >
	 */
	// contributed by org.eclipse.xtext.xtext.generator.generator.GeneratorFragment2
	public Class<? extends IGenerator2> bindIGenerator2() {
		return GamlGenerator.class;
	}

}
