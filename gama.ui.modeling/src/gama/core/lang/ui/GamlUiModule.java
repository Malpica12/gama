/*********************************************************************************************
 *
 * 'GamlUiModule.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.core.lang.ui;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.xtext.builder.builderState.IMarkerUpdater;
import org.eclipse.xtext.builder.resourceloader.IResourceLoader;
import org.eclipse.xtext.builder.resourceloader.ResourceLoaderProviders;
import org.eclipse.xtext.documentation.IEObjectDocumentationProvider;
import org.eclipse.xtext.ide.LexerIdeBindings;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.eclipse.xtext.parser.antlr.ISyntaxErrorMessageProvider;
import org.eclipse.xtext.resource.clustering.DynamicResourceClusteringPolicy;
import org.eclipse.xtext.resource.clustering.IResourceClusteringPolicy;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.service.DispatchingProvider;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.ui.IImageHelper;
import org.eclipse.xtext.ui.IImageHelper.IImageDescriptorHelper;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewer;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.actions.IActionContributor;
import org.eclipse.xtext.ui.editor.autoedit.AbstractEditStrategyProvider;
import org.eclipse.xtext.ui.editor.contentassist.ITemplateProposalProvider;
import org.eclipse.xtext.ui.editor.contentassist.XtextContentAssistProcessor;
import org.eclipse.xtext.ui.editor.contentassist.antlr.IContentAssistParser;
import org.eclipse.xtext.ui.editor.contentassist.antlr.internal.Lexer;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.model.IResourceForEditorInputFactory;
import org.eclipse.xtext.ui.editor.model.ResourceForIEditorInputFactory;
import org.eclipse.xtext.ui.editor.outline.actions.IOutlineContribution;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ITextAttributeProvider;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.ui.resource.SimpleResourceSetProvider;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.name.Names;

import gama.common.ui.IGamlLabelProvider;
import gama.core.lang.parsing.GamlSyntaxErrorMessageProvider;
import gama.core.lang.resource.GamlEncodingProvider;
import gama.core.lang.ui.contentassist.GamlTemplateProposalProvider;
import gama.core.lang.ui.decorators.GamlImageHelper;
import gama.core.lang.ui.decorators.GamlMarkerUpdater;
import gama.core.lang.ui.editor.GamaAutoEditStrategyProvider;
import gama.core.lang.ui.editor.GamaSourceViewerFactory;
import gama.core.lang.ui.editor.GamlEditor;
import gama.core.lang.ui.editor.GamlEditorTickUpdater;
import gama.core.lang.ui.editor.GamlHyperlinkDetector;
import gama.core.lang.ui.editor.GamlMarkOccurrenceActionContributor;
import gama.core.lang.ui.editor.GamlEditor.GamaSourceViewerConfiguration;
import gama.core.lang.ui.editor.folding.GamaFoldingActionContributor;
import gama.core.lang.ui.editor.folding.GamaFoldingRegionProvider;
import gama.core.lang.ui.highlight.GamlHighlightingConfiguration;
import gama.core.lang.ui.highlight.GamlReconciler;
import gama.core.lang.ui.highlight.GamlSemanticHighlightingCalculator;
import gama.core.lang.ui.highlight.GamlTextAttributeProvider;
import gama.core.lang.ui.hover.GamlDocumentationProvider;
import gama.core.lang.ui.hover.GamlHoverProvider;
import gama.core.lang.ui.hover.GamlHoverProvider.GamlDispatchingEObjectTextHover;
import gama.core.lang.ui.labeling.GamlLabelProvider;
import gama.core.lang.ui.outline.GamlLinkWithEditorOutlineContribution;
import gama.core.lang.ui.outline.GamlOutlinePage;
import gama.core.lang.ui.outline.GamlSortOutlineContribution;
import gama.core.lang.ui.templates.GamlTemplateStore;
import gama.core.lang.ui.utils.ModelRunner;
import gama.ui.base.interfaces.IModelRunner;
import msi.gama.lang.gaml.ide.contentassist.antlr.GamlParser;

/**
 * Use this class to register components to be used within the IDE.
 */
public class GamlUiModule extends msi.gama.lang.gaml.ui.AbstractGamlUiModule {

	public GamlUiModule(final AbstractUIPlugin plugin) {
		super(plugin);
	}

	@SuppressWarnings ("unchecked")
	@Override
	public void configure(final Binder binder) {

		super.configure(binder);
		binder.bind(String.class).annotatedWith(
				com.google.inject.name.Names.named(XtextContentAssistProcessor.COMPLETION_AUTO_ACTIVATION_CHARS))
				.toInstance(".");
		binder.bind(IContentAssistParser.class).to((Class<? extends IContentAssistParser>) GamlParser.class);
		binder.bind(Lexer.class).annotatedWith(Names.named(LexerIdeBindings.CONTENT_ASSIST))
				.to(InternalGamlLexer.class);
		binder.bind(IResourceLoader.class).toProvider(ResourceLoaderProviders.getParallelLoader());
		binder.bind(IResourceClusteringPolicy.class).to(DynamicResourceClusteringPolicy.class);
		binder.bind(IModelRunner.class).to(ModelRunner.class);
		// binder.bind(XtextDocumentProvider.class).to(XtextDocumentProvider.class);
		binder.bind(IMarkerUpdater.class).to(GamlMarkerUpdater.class);
		binder.bind(IGamlLabelProvider.class).to(GamlLabelProvider.class);
		// binder.bind(IHighlightingConfiguration.class).to(GamlHighlightingConfiguration.class).asEagerSingleton();
	}

	@Override
	public void configureUiEncodingProvider(final Binder binder) {
		binder.bind(IEncodingProvider.class).annotatedWith(DispatchingProvider.Ui.class).to(GamlEncodingProvider.class);
	}

	public Class<? extends org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory.StatefulFactory>
			bindParserBasedContentAssistContextFactory$StatefulFactory() {
		return gama.core.lang.ui.contentassist.ContentAssistContextFactory.class;
	}

	public Class<? extends XtextSourceViewer.Factory> bindSourceViewerFactory() {
		return GamaSourceViewerFactory.class;
	}

	@Override
	@SingletonBinding (
			eager = true)
	public Class<? extends org.eclipse.jface.viewers.ILabelProvider> bindILabelProvider() {
		return gama.core.lang.ui.labeling.GamlLabelProvider.class;
	}

	@Override
	public Class<? extends ITemplateProposalProvider> bindITemplateProposalProvider() {
		return GamlTemplateProposalProvider.class;
	}

	public Class<? extends IFoldingRegionProvider> bindFoldingRegionProvider() {
		return GamaFoldingRegionProvider.class;
	}

	@Override
	public Class<? extends org.eclipse.jface.text.ITextHover> bindITextHover() {
		return GamlDispatchingEObjectTextHover.class;
	}

	// For performance issues on opening files : see
	// http://alexruiz.developerblogs.com/?p=2359
	@Override
	public Class<? extends IResourceSetProvider> bindIResourceSetProvider() {
		return SimpleResourceSetProvider.class;
	}

	@Override
	public void configureXtextEditorErrorTickUpdater(final com.google.inject.Binder binder) {
		binder.bind(IXtextEditorCallback.class).annotatedWith(Names.named("IXtextEditorCallBack")).to( //$NON-NLS-1$
				GamlEditorTickUpdater.class);
	}

	/**
	 * @author Pierrick
	 * @return GAMLSemanticHighlightingCalculator
	 */
	public Class<? extends ISemanticHighlightingCalculator> bindSemanticHighlightingCalculator() {
		return GamlSemanticHighlightingCalculator.class;
	}

	@SingletonBinding (
			eager = false)
	public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
		return GamlHighlightingConfiguration.class;
	}

	@SingletonBinding ()
	public Class<? extends ITextAttributeProvider> bindITextAttributeProvider() {
		return GamlTextAttributeProvider.class;
	}

	@Override
	public Class<? extends org.eclipse.xtext.ui.editor.IXtextEditorCallback> bindIXtextEditorCallback() {
		// TODO Verify this as it is only needed, normally, for languages that
		// do not use the builder infrastructure
		// (see http://www.eclipse.org/forums/index.php/mv/msg/167666/532239/)
		// not correct for 2.7: return GamlEditorCallback.class;
		return IXtextEditorCallback.NullImpl.class;
	}

	public Class<? extends ISyntaxErrorMessageProvider> bindISyntaxErrorMessageProvider() {
		return GamlSyntaxErrorMessageProvider.class;
	}

	public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
		return GamlHoverProvider.class;
	}

	public Class<? extends IEObjectDocumentationProvider> bindIEObjectDocumentationProviderr() {
		return GamlDocumentationProvider.class;
	}

	@Override
	public Provider<IAllContainersState> provideIAllContainersState() {
		return org.eclipse.xtext.ui.shared.Access.getWorkspaceProjectsState();
	}

	public Class<? extends XtextEditor> bindXtextEditor() {
		return GamlEditor.class;
	}

	public Class<? extends XtextSourceViewerConfiguration> bindXtextSourceViewerConfiguration() {
		return GamaSourceViewerConfiguration.class;
	}

	@Override
	public Class<? extends IHyperlinkDetector> bindIHyperlinkDetector() {
		return GamlHyperlinkDetector.class;
	}

	@Override
	public void configureBracketMatchingAction(final Binder binder) {
		// actually we want to override the first binding only...
		binder.bind(IActionContributor.class).annotatedWith(Names.named("foldingActionGroup")).to( //$NON-NLS-1$
				GamaFoldingActionContributor.class);
		binder.bind(IActionContributor.class).annotatedWith(Names.named("bracketMatcherAction")).to( //$NON-NLS-1$
				org.eclipse.xtext.ui.editor.bracketmatching.GoToMatchingBracketAction.class);
		binder.bind(IPreferenceStoreInitializer.class).annotatedWith(Names.named("bracketMatcherPrefernceInitializer")) //$NON-NLS-1$
				.to(org.eclipse.xtext.ui.editor.bracketmatching.BracketMatchingPreferencesInitializer.class);
		binder.bind(IActionContributor.class).annotatedWith(Names.named("selectionActionGroup")).to( //$NON-NLS-1$
				org.eclipse.xtext.ui.editor.selection.AstSelectionActionContributor.class);
	}

	@Override
	public void configureMarkOccurrencesAction(final Binder binder) {
		binder.bind(IActionContributor.class).annotatedWith(Names.named("markOccurrences"))
				.to(GamlMarkOccurrenceActionContributor.class);
		binder.bind(IPreferenceStoreInitializer.class).annotatedWith(Names.named("GamlMarkOccurrenceActionContributor")) //$NON-NLS-1$
				.to(GamlMarkOccurrenceActionContributor.class);
	}

	@Override
	public Class<? extends IResourceForEditorInputFactory> bindIResourceForEditorInputFactory() {
		return ResourceForIEditorInputFactory.class;
	}

	@Override
	public Class<? extends IContentOutlinePage> bindIContentOutlinePage() {
		return GamlOutlinePage.class;
	}

	@Override
	public Class<? extends IImageHelper> bindIImageHelper() {
		return GamlImageHelper.class;
	}

	@Override
	public Class<? extends IImageDescriptorHelper> bindIImageDescriptorHelper() {
		return GamlImageHelper.class;
	}

	@Override
	public void configureIOutlineContribution$Composite(final Binder binder) {
		binder.bind(IPreferenceStoreInitializer.class).annotatedWith(IOutlineContribution.All.class)
				.to(IOutlineContribution.Composite.class);
	}

	@Override
	public Class<? extends AbstractEditStrategyProvider> bindAbstractEditStrategyProvider() {
		return GamaAutoEditStrategyProvider.class;
	}

	@Override
	public void configureToggleSortingOutlineContribution(final Binder binder) {
		binder.bind(IOutlineContribution.class).annotatedWith(IOutlineContribution.Sort.class)
				.to(GamlSortOutlineContribution.class);
	}

	@Override
	public void configureToggleLinkWithEditorOutlineContribution(final Binder binder) {
		binder.bind(IOutlineContribution.class).annotatedWith(IOutlineContribution.LinkWithEditor.class)
				.to(GamlLinkWithEditorOutlineContribution.class);
	}

	@Override
	@SingletonBinding
	public Class<? extends TemplateStore> bindTemplateStore() {
		return GamlTemplateStore.class;
	}

	@Override
	public Class<? extends IReconciler> bindIReconciler() {
		return GamlReconciler.class;
	}

}
