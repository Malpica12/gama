/*
 * generated by Xtext
 */
package msi.gama.lang.gaml.ui;

import org.eclipse.compare.IViewerCreator;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.builder.BuilderParticipant;
import org.eclipse.xtext.builder.EclipseOutputConfigurationProvider;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.builder.builderState.IBuilderState;
import org.eclipse.xtext.builder.clustering.CurrentDescriptions;
import org.eclipse.xtext.builder.impl.PersistentDataAwareDirtyResource;
import org.eclipse.xtext.builder.nature.NatureAddingEditorCallback;
import org.eclipse.xtext.builder.preferences.BuilderPreferenceAccess;
import org.eclipse.xtext.generator.IContextualOutputConfigurationProvider;
import org.eclipse.xtext.ide.LexerIdeBindings;
import org.eclipse.xtext.ide.editor.contentassist.antlr.IContentAssistParser;
import org.eclipse.xtext.ide.editor.contentassist.antlr.internal.Lexer;
import org.eclipse.xtext.ide.editor.partialEditing.IPartialEditingContentAssistParser;
import org.eclipse.xtext.parser.antlr.AntlrTokenDefProvider;
import org.eclipse.xtext.parser.antlr.ITokenDefProvider;
import org.eclipse.xtext.parser.antlr.LexerProvider;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.DefaultUiModule;
import org.eclipse.xtext.ui.UIBindings;
import org.eclipse.xtext.ui.codetemplates.ui.AccessibleCodetemplatesActivator;
import org.eclipse.xtext.ui.codetemplates.ui.partialEditing.IPartialEditingContentAssistContextFactory;
import org.eclipse.xtext.ui.codetemplates.ui.partialEditing.PartialEditingContentAssistContextFactory;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.AdvancedTemplatesPreferencePage;
import org.eclipse.xtext.ui.codetemplates.ui.preferences.TemplatesLanguageConfiguration;
import org.eclipse.xtext.ui.codetemplates.ui.registry.LanguageRegistrar;
import org.eclipse.xtext.ui.codetemplates.ui.registry.LanguageRegistry;
import org.eclipse.xtext.ui.compare.DefaultViewerCreator;
import org.eclipse.xtext.ui.editor.DocumentBasedDirtyResource;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.FQNPrefixMatcher;
import org.eclipse.xtext.ui.editor.contentassist.IContentProposalProvider;
import org.eclipse.xtext.ui.editor.contentassist.IProposalConflictHelper;
import org.eclipse.xtext.ui.editor.contentassist.PrefixMatcher;
import org.eclipse.xtext.ui.editor.contentassist.antlr.AntlrProposalConflictHelper;
import org.eclipse.xtext.ui.editor.contentassist.antlr.DelegatingContentAssistContextFactory;
import org.eclipse.xtext.ui.editor.outline.IOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.IOutlineTreeStructureProvider;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.ui.editor.templates.XtextTemplatePreferencePage;
import org.eclipse.xtext.ui.refactoring.IDependentElementsCalculator;
import org.eclipse.xtext.ui.refactoring.IReferenceUpdater;
import org.eclipse.xtext.ui.refactoring.IRenameRefactoringProvider;
import org.eclipse.xtext.ui.refactoring.IRenameStrategy;
import org.eclipse.xtext.ui.refactoring.impl.DefaultDependentElementsCalculator;
import org.eclipse.xtext.ui.refactoring.impl.DefaultReferenceUpdater;
import org.eclipse.xtext.ui.refactoring.impl.DefaultRenameRefactoringProvider;
import org.eclipse.xtext.ui.refactoring.impl.DefaultRenameStrategy;
import org.eclipse.xtext.ui.refactoring.ui.DefaultRenameSupport;
import org.eclipse.xtext.ui.refactoring.ui.IRenameSupport;
import org.eclipse.xtext.ui.refactoring.ui.RefactoringPreferences;
import org.eclipse.xtext.ui.resource.ResourceServiceDescriptionLabelProvider;
import org.eclipse.xtext.ui.shared.Access;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import gama.core.lang.ui.GamlUiModule;
import gama.core.lang.ui.contentassist.GamlProposalProvider;
import gama.core.lang.ui.labeling.GamlDescriptionLabelProvider;
import gama.core.lang.ui.labeling.GamlLabelProvider;
import gama.core.lang.ui.outline.GamlOutlineTreeProvider;
import gama.core.lang.ui.quickfix.GamlQuickfixProvider;
import msi.gama.lang.gaml.ide.contentassist.antlr.GamlParser;
import msi.gama.lang.gaml.ide.contentassist.antlr.PartialGamlContentAssistParser;
import msi.gama.lang.gaml.ide.contentassist.antlr.internal.InternalGamlLexer;

/**
 * Manual modifications go to {@link GamlUiModule}.
 */
@SuppressWarnings ("all")
public abstract class AbstractGamlUiModule extends DefaultUiModule {

	public AbstractGamlUiModule(final AbstractUIPlugin plugin) {
		super(plugin);
	}

	// contributed by org.eclipse.xtext.xtext.generator.ImplicitFragment
	public Provider<? extends IAllContainersState> provideIAllContainersState() {
		return Access.getJavaProjectsState();
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends IProposalConflictHelper> bindIProposalConflictHelper() {
		return AntlrProposalConflictHelper.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public void configureContentAssistLexer(final Binder binder) {
		binder.bind(Lexer.class).annotatedWith(Names.named(LexerIdeBindings.CONTENT_ASSIST))
				.to(InternalGamlLexer.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public void configureHighlightingLexer(final Binder binder) {
		binder.bind(org.eclipse.xtext.parser.antlr.Lexer.class)
				.annotatedWith(Names.named(LexerIdeBindings.HIGHLIGHTING))
				.to(msi.gama.lang.gaml.parser.antlr.internal.InternalGamlLexer.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public void configureHighlightingTokenDefProvider(final Binder binder) {
		binder.bind(ITokenDefProvider.class).annotatedWith(Names.named(LexerIdeBindings.HIGHLIGHTING))
				.to(AntlrTokenDefProvider.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends ContentAssistContext.Factory> bindContentAssistContext$Factory() {
		return DelegatingContentAssistContextFactory.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public Class<? extends IContentAssistParser> bindIContentAssistParser() {
		return GamlParser.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
	public void configureContentAssistLexerProvider(final Binder binder) {
		binder.bind(InternalGamlLexer.class).toProvider(LexerProvider.create(InternalGamlLexer.class));
	}

	// contributed by org.eclipse.xtext.xtext.generator.exporting.QualifiedNamesFragment2
	public Class<? extends PrefixMatcher> bindPrefixMatcher() {
		return FQNPrefixMatcher.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.exporting.QualifiedNamesFragment2
	public Class<? extends IDependentElementsCalculator> bindIDependentElementsCalculator() {
		return DefaultDependentElementsCalculator.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	public void configureIResourceDescriptionsBuilderScope(final Binder binder) {
		binder.bind(IResourceDescriptions.class)
				.annotatedWith(Names.named(ResourceDescriptionsProvider.NAMED_BUILDER_SCOPE))
				.to(CurrentDescriptions.ResourceSetAware.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	public Class<? extends IXtextEditorCallback> bindIXtextEditorCallback() {
		return NatureAddingEditorCallback.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	public Class<? extends IContextualOutputConfigurationProvider> bindIContextualOutputConfigurationProvider() {
		return EclipseOutputConfigurationProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	public void configureIResourceDescriptionsPersisted(final Binder binder) {
		binder.bind(IResourceDescriptions.class)
				.annotatedWith(Names.named(ResourceDescriptionsProvider.PERSISTED_DESCRIPTIONS))
				.to(IBuilderState.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.builder.BuilderIntegrationFragment2
	public Class<? extends DocumentBasedDirtyResource> bindDocumentBasedDirtyResource() {
		return PersistentDataAwareDirtyResource.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.generator.GeneratorFragment2
	public Class<? extends IXtextBuilderParticipant> bindIXtextBuilderParticipant() {
		return BuilderParticipant.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.generator.GeneratorFragment2
	public IWorkspaceRoot bindIWorkspaceRootToInstance() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	// contributed by org.eclipse.xtext.xtext.generator.generator.GeneratorFragment2
	public void configureBuilderPreferenceStoreInitializer(final Binder binder) {
		binder.bind(IPreferenceStoreInitializer.class).annotatedWith(Names.named("builderPreferenceInitializer"))
				.to(BuilderPreferenceAccess.Initializer.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.labeling.LabelProviderFragment2
	@Override
	public Class<? extends ILabelProvider> bindILabelProvider() {
		return GamlLabelProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.labeling.LabelProviderFragment2
	@Override
	public void configureResourceUIServiceLabelProvider(final Binder binder) {
		binder.bind(ILabelProvider.class).annotatedWith(ResourceServiceDescriptionLabelProvider.class)
				.to(GamlDescriptionLabelProvider.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.outline.OutlineTreeProviderFragment2
	public Class<? extends IOutlineTreeProvider> bindIOutlineTreeProvider() {
		return GamlOutlineTreeProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.outline.OutlineTreeProviderFragment2
	public Class<? extends IOutlineTreeStructureProvider> bindIOutlineTreeStructureProvider() {
		return GamlOutlineTreeProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.quickfix.QuickfixProviderFragment2
	@Override
	public Class<? extends IssueResolutionProvider> bindIssueResolutionProvider() {
		return GamlQuickfixProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.contentAssist.ContentAssistFragment2
	public Class<? extends IContentProposalProvider> bindIContentProposalProvider() {
		return GamlProposalProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.refactoring.RefactorElementNameFragment2
	public void configureIPreferenceStoreInitializer(final Binder binder) {
		binder.bind(IPreferenceStoreInitializer.class).annotatedWith(Names.named("RefactoringPreferences"))
				.to(RefactoringPreferences.Initializer.class);
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.refactoring.RefactorElementNameFragment2
	public Class<? extends IRenameStrategy> bindIRenameStrategy() {
		return DefaultRenameStrategy.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.refactoring.RefactorElementNameFragment2
	public Class<? extends IReferenceUpdater> bindIReferenceUpdater() {
		return DefaultReferenceUpdater.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.refactoring.RefactorElementNameFragment2
	public Class<? extends IRenameRefactoringProvider> bindIRenameRefactoringProvider() {
		return DefaultRenameRefactoringProvider.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.refactoring.RefactorElementNameFragment2
	public Class<? extends IRenameSupport.Factory> bindIRenameSupport$Factory() {
		return DefaultRenameSupport.Factory.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.templates.CodetemplatesGeneratorFragment2
	public Provider<? extends TemplatesLanguageConfiguration> provideTemplatesLanguageConfiguration() {
		return AccessibleCodetemplatesActivator.getTemplatesLanguageConfigurationProvider();
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.templates.CodetemplatesGeneratorFragment2
	public Provider<? extends LanguageRegistry> provideLanguageRegistry() {
		return AccessibleCodetemplatesActivator.getLanguageRegistry();
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.templates.CodetemplatesGeneratorFragment2
	@SingletonBinding (
			eager = true)
	public Class<? extends LanguageRegistrar> bindLanguageRegistrar() {
		return LanguageRegistrar.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.templates.CodetemplatesGeneratorFragment2
	public Class<? extends XtextTemplatePreferencePage> bindXtextTemplatePreferencePage() {
		return AdvancedTemplatesPreferencePage.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.templates.CodetemplatesGeneratorFragment2
	public Class<? extends IPartialEditingContentAssistParser> bindIPartialEditingContentAssistParser() {
		return PartialGamlContentAssistParser.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.templates.CodetemplatesGeneratorFragment2
	public Class<? extends IPartialEditingContentAssistContextFactory>
			bindIPartialEditingContentAssistContextFactory() {
		return PartialEditingContentAssistContextFactory.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.compare.CompareFragment2
	public Class<? extends IViewerCreator> bindIViewerCreator() {
		return DefaultViewerCreator.class;
	}

	// contributed by org.eclipse.xtext.xtext.generator.ui.compare.CompareFragment2
	public void configureCompareViewerTitle(final Binder binder) {
		binder.bind(String.class).annotatedWith(Names.named(UIBindings.COMPARE_VIEWER_TITLE))
				.toInstance("Gaml Compare");
	}

}
