/*******************************************************************************************************
 *
 * GamlEditor.java, in gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.modeling.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
// import org.eclipse.text.templates.TemplatePersistenceData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.internal.texteditor.LineNumberColumn;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.XtextUIMessages;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.outline.quickoutline.QuickOutlinePopup;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;
import org.eclipse.xtext.ui.editor.validation.IValidationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.eclipse.xtext.ui.editor.validation.MarkerIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.ValidationJob;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.ObjectArrays;
import com.google.inject.Inject;
import com.google.inject.Injector;

import gama.common.GamlFileExtension;
import gama.common.interfaces.IKeyword;
import gama.common.preferences.GamaPreferences;
import gama.common.preferences.IPreferenceChangeListener.IPreferenceAfterChangeListener;
import gama.core.dev.utils.FLAGS;
import gama.core.lang.resource.GamlResourceServices;
import gama.core.lang.validation.IGamlBuilderListener;
import gama.ui.base.controls.FlatButton;
import gama.ui.base.interfaces.IGamlEditor;
import gama.ui.base.interfaces.IModelRunner;
import gama.ui.base.resources.GamaColors;
import gama.ui.base.resources.GamaIcons;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.resources.IGamaIcons;
import gama.ui.base.utils.PlatformHelper;
import gama.ui.base.utils.WorkbenchHelper;
import gama.ui.modeling.decorators.GamlAnnotationImageProvider;
import gama.ui.modeling.editbox.BoxDecoratorPartListener;
import gama.ui.modeling.editbox.BoxProviderRegistry;
import gama.ui.modeling.editbox.IBoxDecorator;
import gama.ui.modeling.editbox.IBoxEnabledEditor;
import gama.ui.modeling.editor.toolbar.CreateExperimentSelectionListener;
import gama.ui.modeling.editor.toolbar.EditorSearchControls;
import gama.ui.modeling.editor.toolbar.EditorToolbar;
import gama.ui.modeling.editor.toolbar.GamlQuickOutlinePopup;
import gama.ui.modeling.editor.toolbar.OpenExperimentSelectionListener;
import gama.ui.modeling.editor.toolbar.OpenImportedErrorSelectionListener;
import gama.ui.modeling.editor.toolbar.RevalidateModelSelectionListener;
import gama.ui.modeling.templates.GamlEditTemplateDialogFactory;
import gama.ui.modeling.templates.GamlTemplateStore;
import gaml.descriptions.IDescription;
import gaml.descriptions.ValidationContext;
import gama.ui.base.toolbar.GamaToolbar2;
import gama.ui.base.toolbar.GamaToolbarFactory;
import gama.ui.base.toolbar.IToolbarDecoratedView;
import gama.ui.base.toolbar.Selector;

/**
 * The Class GamlEditor.
 */
/*
 * The class GamlEditor.
 *
 * @author drogoul
 *
 * @since 4 mars 2012
 */
@SuppressWarnings ("all")
public class GamlEditor extends XtextEditor implements IGamlBuilderListener, IGamlEditor, IBoxEnabledEditor,
		IToolbarDecoratedView /* IToolbarDecoratedView.Sizable, ITooltipDisplayer */ {

	static {
		final var store = EditorsUI.getPreferenceStore();
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR, false);
		store.setDefault("spellingEnabled", false);
		store.setValue("spellingEnabled", false);
	}

	/**
	 * Instantiates a new gaml editor.
	 */
	public GamlEditor() {
		dndHandler = new GamlEditorDragAndDropHandler(this);
	}

	/** The part listeners. */
	protected static Map<IPartService, IPartListener2> partListeners;

	/** The decorator. */
	IBoxDecorator decorator;
	
	/** The state. */
	GamlEditorState state = new GamlEditorState(null, Collections.EMPTY_LIST);
	
	/** The toolbar. */
	GamaToolbar2 toolbar;
	
	/** The toolbar parent. */
	Composite toolbarParent;
	
	/** The find control. */
	private EditorSearchControls findControl;
	
	/** The decoration enabled. */
	boolean decorationEnabled = GamaPreferences.Modeling.EDITBOX_ENABLED.getValue();
	// boolean editToolbarEnabled = AutoStartup.EDITOR_SHOW_TOOLBAR.getValue();

	/** The resource set provider. */
	@Inject public IResourceSetProvider resourceSetProvider;
	
	/** The injector. */
	@Inject Injector injector;
	
	/** The runner. */
	@Inject IModelRunner runner;
	
	/** The template dialog factory. */
	@Inject private GamlEditTemplateDialogFactory templateDialogFactory;
	
	/** The template store. */
	@Inject private TemplateStore templateStore;
	
	/** The validator. */
	@Inject private IResourceValidator validator;
	
	/** The marker creator. */
	@Inject private MarkerCreator markerCreator;
	
	/** The marker type provider. */
	@Inject private MarkerTypeProvider markerTypeProvider;
	
	/** The issue resolver. */
	@Inject private IssueResolutionProvider issueResolver;
	
	/** The highlighting configuration. */
	@Inject private IHighlightingConfiguration highlightingConfiguration;
	
	/** The dnd handler. */
	private final GamlEditorDragAndDropHandler dndHandler;
	
	/** The dnd changed listener. */
	private final IPreferenceAfterChangeListener dndChangedListener = newValue -> {
		uninstallTextDragAndDrop(getInternalSourceViewer());
		installTextDragAndDrop(getInternalSourceViewer());
	};

	/** The is text drag and drop installed. */
	private boolean fIsTextDragAndDropInstalled;

	/** The file URI. */
	private URI fileURI;

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		gama.core.dev.utils.DEBUG.OUT("init of Editor for " + input.getName());
		assignBoxPartListener();
	}

	/** The image provider. */
	static GamlAnnotationImageProvider imageProvider = new GamlAnnotationImageProvider();

	@Override
	protected IAnnotationAccess createAnnotationAccess() {
		return new DefaultMarkerAnnotationAccess() {

			@Override
			public int getLayer(final Annotation annotation) {
				if (annotation.isMarkedDeleted()) return IAnnotationAccessExtension.DEFAULT_LAYER;
				return super.getLayer(annotation);
			}

			@Override
			public void paint(final Annotation annotation, final GC gc, final Canvas canvas, final Rectangle bounds) {
				final var image = imageProvider.getManagedImage(annotation);
				if (image != null) {
					ImageUtilities.drawImage(image, gc, canvas, bounds, SWT.CENTER, SWT.TOP);
				} else {
					super.paint(annotation, gc, canvas, bounds);
				}

			}

			@Override
			public boolean isPaintable(final Annotation annotation) {
				if (imageProvider.getManagedImage(annotation) != null) return true;
				return super.isPaintable(annotation);
			}

		};
	}

	@Override
	protected void rulerContextMenuAboutToShow(final IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		menu.remove("projection");

		final IMenuManager foldingMenu = new MenuManager(XtextUIMessages.Editor_FoldingMenu_name, "projection"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);
		var action = getAction("FoldingToggle"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingExpandAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingCollapseAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingCollapseStrings"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingRestore"); //$NON-NLS-1$
		foldingMenu.add(action);
	}

	@Override
	public void dispose() {
		decorator = null;
		GamaPreferences.Modeling.EDITOR_DRAG_RESOURCES.removeChangeListener(dndChangedListener);
		GamlResourceServices.removeResourceListener(this);

		super.dispose();
	}

	/**
	 * Gets the template store.
	 *
	 * @return the template store
	 */
	public GamlTemplateStore getTemplateStore() {
		return (GamlTemplateStore) templateStore;
	}

	/**
	 * Gets the template factory.
	 *
	 * @return the template factory
	 */
	public GamlEditTemplateDialogFactory getTemplateFactory() {
		return templateDialogFactory;
	}

	/**
	 * Sets the show other enabled.
	 *
	 * @param showOtherEnabled the new show other enabled
	 */
	public void setShowOtherEnabled(final boolean showOtherEnabled) {
		buildRightToolbar();
	}

	/**
	 * Builds the right toolbar.
	 */
	private void buildRightToolbar() {
		toolbar.wipe(SWT.LEFT, true);
		final var t = toolbar.button(IGamaColors.NEUTRAL, "Waiting...", GamaIcons.create("status.clock").image(), null,
				SWT.LEFT);
		toolbar.sep(4, SWT.LEFT);
		findControl = new EditorToolbar(this).fill(toolbar.getToolbar(SWT.RIGHT));

		// toolbar.sep(4, SWT.RIGHT);
		toolbar.refresh(true);
	}

	@Override
	public boolean isLineNumberRulerVisible() {
		final var store = getAdvancedPreferenceStore();
		return store != null ? store.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER)
				: false;
	}

	/**
	 * Checks if is range indicator enabled.
	 *
	 * @return true, if is range indicator enabled
	 */
	public boolean isRangeIndicatorEnabled() {
		return getInternalSourceViewer().isProjectionMode();
	}

	/**
	 * Gets the advanced preference store.
	 *
	 * @return the advanced preference store
	 */
	public final IPreferenceStore getAdvancedPreferenceStore() {
		return super.getPreferenceStore();
	}

	/**
	 * Configure tab folder.
	 *
	 * @param compo the compo
	 */
	private void configureTabFolder(final Composite compo) {
		var c = compo;
		while (c != null) {
			if (c instanceof CTabFolder) { break; }
			c = c.getParent();
		}
		if (c != null) {
			final var folder = (CTabFolder) c;
			folder.setMaximizeVisible(true);
			folder.setMinimizeVisible(true);
			folder.setMinimumCharacters(10);
			folder.setMRUVisible(true);
			folder.setUnselectedCloseVisible(true);
			folder.setHighlightEnabled(true);
			// folder.setTabHeight(16);
		}

	}

	@Override
	public void createPartControl(final Composite compo) {
		gama.core.dev.utils.DEBUG.OUT("Creating part control of " + this.getPartName());
		configureTabFolder(compo);
		toolbarParent = GamaToolbarFactory.createToolbars(this, compo);
		final var layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = -5;
		toolbarParent.setLayout(layout);
		toolbarParent.setBackground(IGamaColors.WHITE.color());

		// Asking the editor to fill the rest
		// final int style = GamaToolbarFactory.REDUCED_VIEW_TOOLBAR_HEIGHT.getValue() ? SWT.NONE : SWT.BORDER;
		final var editor = new Composite(toolbarParent, SWT.BORDER);
		final var data = new GridData(SWT.FILL, SWT.FILL, true, true);
		editor.setLayoutData(data);
		editor.setLayout(new FillLayout());
		super.createPartControl(editor);
		toolbarParent.layout();
		installGestures();
		// this.getStyledText().setEditable(!FLAGS.IS_READ_ONLY);
	}

	@Override
	public boolean isEditable() {
		return FLAGS.IS_READ_ONLY ? false : super.isEditable();
	}

	@Override
	protected void initializeDirtyStateSupport() {
		if (getDocument() != null) {
			fileURI = ((XtextDocument) getDocument()).getResourceURI();
			GamlResourceServices.addResourceListener(fileURI, GamlEditor.this);
			super.initializeDirtyStateSupport();
			scheduleValidationJob();
		}
	}

	/**
	 * Schedule validation job.
	 */
	private void scheduleValidationJob() {
		// if (!isEditable()) return;
		final IValidationIssueProcessor processor = new MarkerIssueProcessor(getResource(),
				getInternalSourceViewer().getAnnotationModel(), markerCreator, markerTypeProvider);
		final ValidationJob validate = new ValidationJob(validator, getDocument(), processor, CheckMode.FAST_ONLY) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final var issues = getDocument().readOnly(resource -> {
					if (resource.isValidationDisabled()) return Collections.emptyList();
					return validator.validate(resource, getCheckMode(), null);
				});
				processor.processIssues((List<Issue>) issues, monitor);
				return Status.OK_STATUS;
			}

		};
		validate.schedule();

	}

	@Override
	public boolean isOverviewRulerVisible() {
		final var viewer = getInternalSourceViewer();
		if (viewer == null) return super.isOverviewRulerVisible();
		return viewer.isOverviewVisible();
	}

	@Override
	public void showOverviewRuler() {
		getInternalSourceViewer().showAnnotationsOverview(true);
	}

	@Override
	public void hideOverviewRuler() {
		getInternalSourceViewer().showAnnotationsOverview(false);
	}

	@Override
	public GamaSourceViewer getInternalSourceViewer() {
		return (GamaSourceViewer) super.getInternalSourceViewer();
	}

	/**
	 * Install gestures.
	 */
	private void installGestures() {
		final var text = this.getInternalSourceViewer().getTextWidget();
		if (text != null) {
			text.addGestureListener(ge -> {
				if (ge.detail == SWT.GESTURE_END) { updateBoxes(); }
			});
		}
	}

	@Override
	protected void installFoldingSupport(final ProjectionViewer projectionViewer) {
		super.installFoldingSupport(projectionViewer);
		if (!isRangeIndicatorEnabled()) { projectionViewer.doOperation(ProjectionViewer.TOGGLE); }
	}

	@Override
	protected void handleCursorPositionChanged() {
		if (getSelectionProvider() == null || getInternalSourceViewer() == null
				|| getInternalSourceViewer().getControl() == null
				|| getInternalSourceViewer().getControl().isDisposed())
			return;
		super.handleCursorPositionChanged();
		this.markInNavigationHistory();
	}

	/**
	 * Enable button.
	 *
	 * @param index the index
	 * @param text the text
	 * @param listener the listener
	 */
	private void enableButton(final int index, final String text, final SelectionListener listener) {
		if (text == null) return;
		final var expType = state.types.get(index);
		final var image = IKeyword.BATCH.equals(expType) ? GamaIcons.create(IGamaIcons.BUTTON_BATCH).image()
				: IKeyword.MEMORIZE.equals(expType) ? GamaIcons.create(IGamaIcons.BUTTON_BACK).image()
						: GamaIcons.create(IGamaIcons.BUTTON_GUI).image();

		final var t = toolbar.button(IGamaColors.OK, text, image, SWT.LEFT);
		final var type =
				IKeyword.BATCH.equals(expType) ? "batch" : IKeyword.MEMORIZE.equals(expType) ? "memorize" : "regular";

		t.getControl().setToolTipText("Executes the " + type + " experiment " + text);
		((FlatButton) t.getControl()).addSelectionListener(listener);
		t.setData("index", index);
		((FlatButton) t.getControl()).setData("exp", text);
		toolbar.sep(4, SWT.LEFT);
	}

	/**
	 * Update toolbar.
	 *
	 * @param newState the new state
	 * @param forceState the force state
	 */
	private void updateToolbar(final GamlEditorState newState, final boolean forceState) {
		if (forceState || !state.equals(newState)) {
			WorkbenchHelper.runInUI("Editor refresh", 50, m -> {
				if (toolbar == null || toolbar.isDisposed()) return;
				toolbar.wipe(SWT.LEFT, true);
				if (PlatformHelper.isWindows()) { toolbar.sep(4, SWT.LEFT); }

				final var c = state.getColor();
				var msg = state.getStatus();

				Selector listener = null;
				String imageName = null;

				if (GamlEditorState.NO_EXP_DEFINED.equals(msg)) {
					msg = null;
				} else if (newState.hasImportedErrors) {
					listener = new OpenImportedErrorSelectionListener(GamlEditor.this, newState,
							toolbar.getToolbar(SWT.LEFT));
					imageName = "small.dropdown";
				} else if (msg != null) {
					listener = new RevalidateModelSelectionListener(GamlEditor.this);
					imageName = "marker.error2";
				} else {
					listener = new OpenExperimentSelectionListener(GamlEditor.this, newState, runner);
				}

				if (msg != null) {
					final var t = toolbar.button(c, msg, GamaIcons.create(imageName).image(), listener, SWT.LEFT);

					// without the following line, the display of the
					// text "msg" is not updated
					// correctly (at least for Windows OS)
					toolbar.sep(4, SWT.LEFT);
				} else {
					var i = 0;
					if (newState.showExperiments) {
						for (final String e : state.abbreviations) {
							enableButton(i++, e, listener);

						}
					}
				}
				if (newState.showExperiments
						&& !GamlFileExtension.isExperiment(getDocument().getAdapter(IFile.class).getName())) {
					toolbar.button(IGamaColors.NEUTRAL, "Add experiment", GamaIcons.create("small.plus").image(),
							new CreateExperimentSelectionListener(GamlEditor.this, toolbar.getToolbar(SWT.LEFT)),
							SWT.LEFT);
				}

				toolbar.refresh(true);

			});
		}

	}

	@Override
	public void validationEnded(final Iterable<? extends IDescription> newExperiments, final ValidationContext status) {
		// final String platformString = getURI().toPlatformString(true);
		// final IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
		// final WrappedGamaFile file =
		// (WrappedGamaFile) NavigatorRoot.getInstance().getManager().findWrappedInstanceOf(myFile);
		// NavigatorRoot.getInstance().getManager().refreshResource(file);
		// NavigatorRoot.getInstance().getManager().resourceChanged(null);
		if (newExperiments == null && state != null) {
			updateToolbar(state, true);
		} else {
			final var newState = new GamlEditorState(status, newExperiments);
			updateToolbar(newState, false);
			state = newState;
		}
	}

	/**
	 * The Class GamaSourceViewerConfiguration.
	 */
	public static class GamaSourceViewerConfiguration extends XtextSourceViewerConfiguration {

		@Override
		public ITextHover getTextHover(final ISourceViewer sourceViewer, final String contentType) {
			return super.getTextHover(sourceViewer, contentType);
		}

	}

	@Override
	public void doSave(final IProgressMonitor progressMonitor) {
		this.beforeSave();
		super.doSave(progressMonitor);
	}

	@Override
	public void doSaveAs() {
		this.beforeSave();
		super.doSaveAs();
	}

	/**
	 * Before save.
	 */
	private void beforeSave() {
		if (!GamaPreferences.Modeling.EDITOR_CLEAN_UP.getValue()) return;
		final SourceViewer sv = getInternalSourceViewer();
		final var p = sv.getSelectedRange();
		sv.setSelectedRange(0, sv.getDocument().getLength());
		if (sv.canDoOperation(ISourceViewer.FORMAT)) { sv.doOperation(ISourceViewer.FORMAT); }
		sv.setSelectedRange(p.x, p.y);
	}

	@Override
	protected String[] collectContextMenuPreferencePages() {
		final var commonPages = super.collectContextMenuPreferencePages();
		final String[] langSpecificPages = { "pm.eclipse.editbox.pref.default" };
		return ObjectArrays.concat(langSpecificPages, commonPages, String.class);
	}

	/**
	 * @see gama.ui.modeling.editbox.IBoxEnabledEditor#getDecorator()
	 */
	@Override
	public IBoxDecorator getDecorator() {
		if (decorator == null) { createDecorator(); }
		return decorator;
	}

	/**
	 * @see gama.ui.modeling.editbox.IBoxEnabledEditor#createDecorator(gama.ui.modeling.editbox.IBoxProvider)
	 */
	@Override
	public void createDecorator() {
		if (decorator != null) return;
		final var provider = BoxProviderRegistry.getInstance().getGamlProvider();
		decorator = provider.createDecorator();
		decorator.setStyledText(getStyledText());
		decorator.setSettings(provider.getEditorsBoxSettings());
	}

	/**
	 * Gets the styled text.
	 *
	 * @return the styled text
	 */
	private StyledText getStyledText() {
		return (StyledText) super.getAdapter(Control.class);
	}

	/**
	 * @see gama.ui.modeling.editbox.IBoxEnabledEditor#decorate()
	 */
	@Override
	public void decorate(final boolean doIt) {
		if (doIt) {
			getDecorator().decorate(false);
		} else {
			getDecorator().undecorate();
		}
		enableUpdates(doIt);
	}

	@Override
	public void enableUpdates(final boolean visible) {
		getDecorator().enableUpdates(visible);
	}

	/**
	 * Sets the decoration enabled.
	 *
	 * @param toggle the new decoration enabled
	 */
	public void setDecorationEnabled(final boolean toggle) {
		decorationEnabled = toggle;
	}

	/**
	 * Update boxes.
	 */
	public void updateBoxes() {
		if (!decorationEnabled) return;
		getDecorator().forceUpdate();
	}

	@Override
	public boolean isDecorationEnabled() {
		return decorationEnabled;
	}

	/**
	 * Assign box part listener.
	 */
	private void assignBoxPartListener() {
		final var partService = getSite().getWorkbenchWindow().getPartService();
		if (partService == null) return;
		if (partListeners == null) { partListeners = new HashMap<>(); }
		final var oldListener = partListeners.get(partService);
		if (oldListener == null) {
			final IPartListener2 listener = new BoxDecoratorPartListener();
			partService.addPartListener(listener);
			partListeners.put(partService, listener);
		}
	}

	/**
	 * Insert text.
	 *
	 * @param s the s
	 */
	public void insertText(final String s) {
		final var selection = (ITextSelection) getSelectionProvider().getSelection();
		final var offset = selection.getOffset();
		final var length = selection.getLength();
		try {
			new ReplaceEdit(offset, length, s).apply(getDocument());
		} catch (final MalformedTreeException | BadLocationException e) {
			e.printStackTrace();
			return;
		}
		getSelectionProvider().setSelection(new TextSelection(getDocument(), offset + s.length(), 0));
	}

	/**
	 * Gets the selected text.
	 *
	 * @return the selected text
	 */
	public String getSelectedText() {
		final var sel = (ITextSelection) getSelectionProvider().getSelection();
		final var length = sel.getLength();
		if (length == 0) return "";
		final IDocument doc = getDocument();
		try {
			return doc.get(sel.getOffset(), length);
		} catch (final BadLocationException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * Open edit template dialog.
	 *
	 * @param data the data
	 * @param edit the edit
	 * @return true, if successful
	 * @see msi.gama.lang.gama.ui.interfaces.IGamlEditor#openEditTemplateDialog()
	 */
	public boolean openEditTemplateDialog(final TemplatePersistenceData data, final boolean edit) {
		final var d = getTemplateFactory().createDialog(data, edit, getEditorSite().getShell());
		if (d.open() == Window.OK) {
			getTemplateStore().directAdd(d.getData(), edit);
			return true;
		}
		return false;
	}

	/**
	 * Gets the new template id.
	 *
	 * @param path the path
	 * @return the new template id
	 * @see msi.gama.lang.gama.ui.interfaces.IGamlEditor#getNewTemplateId(java.lang.String)
	 */
	public String getNewTemplateId(final String path) {
		return getTemplateStore().getNewIdFromId(path);
	}

	/**
	 * Apply template at the end.
	 *
	 * @param t the t
	 * @see msi.gama.lang.gama.ui.interfaces.IGamlEditor#applyTemplate(org.eclipse.jface.text.templates.Template)
	 */

	public void applyTemplateAtTheEnd(final Template t) {

		try {
			final IDocument doc = getDocument();
			var offset = doc.getLineOffset(doc.getNumberOfLines() - 1);
			doc.replace(offset, 0, "\n\n");
			offset += 2;
			final var length = 0;
			final var pos = new Position(offset, length);
			final var ct = new XtextTemplateContextType();
			final var dtc = new DocumentTemplateContext(ct, doc, pos);
			final IRegion r = new Region(offset, length);
			final var tp = new TemplateProposal(t, dtc, r, null);
			tp.apply(getInternalSourceViewer(), (char) 0, 0, offset);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Apply template.
	 *
	 * @param t the t
	 */
	public void applyTemplate(final Template t) {
		// TODO Create a specific context type (with GAML specific variables ??)
		final var ct = new XtextTemplateContextType();
		final IDocument doc = getDocument();
		final var selection = (ITextSelection) getSelectionProvider().getSelection();
		final var offset = selection.getOffset();
		final var length = selection.getLength();
		final var pos = new Position(offset, length);
		final var dtc = new DocumentTemplateContext(ct, doc, pos);
		final IRegion r = new Region(offset, length);
		final var tp = new TemplateProposal(t, dtc, r, null);
		tp.apply(getInternalSourceViewer(), (char) 0, 0, offset);
	}

	/**
	 * Open outline popup.
	 */
	public void openOutlinePopup() {

		getDocument().readOnly(new CancelableUnitOfWork<Object, XtextResource>() {

			@Override
			public Object exec(final XtextResource state, final CancelIndicator c) throws Exception {
				final QuickOutlinePopup popup = new GamlQuickOutlinePopup(GamlEditor.this, toolbar);
				injector.injectMembers(popup);
				return popup.open();
			}
		});

	}

	/**
	 * @see gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int,
	 *      gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;
		buildRightToolbar();
	}

	@Override
	protected void handlePreferenceStoreChanged(final PropertyChangeEvent event) {
		final LineNumberColumn c;
		super.handlePreferenceStoreChanged(event);
		if (PREFERENCE_COLOR_BACKGROUND.equals(event.getProperty())) {
			// this.fSourceViewerDecorationSupport.updateOverviewDecorations();

			this.getVerticalRuler().getControl()
					.setBackground(GamaColors.get(GamaPreferences.Modeling.EDITOR_BACKGROUND_COLOR.getValue()).color());

			final Iterator e = ((CompositeRuler) getVerticalRuler()).getDecoratorIterator();
			while (e.hasNext()) {
				final var column = (IVerticalRulerColumn) e.next();
				column.getControl().setBackground(
						GamaColors.get(GamaPreferences.Modeling.EDITOR_BACKGROUND_COLOR.getValue()).color());
				column.redraw();
			}
		}
	}

	/**
	 * Do search.
	 */
	public void doSearch() {
		if (findControl.getFindControl().isFocusControl()) {
			findControl.findNext();
		} else {
			findControl.getFindControl().setFocus();
		}
	}

	@Override
	protected void initializeDragAndDrop(final ISourceViewer viewer) {
		GamaPreferences.Modeling.EDITOR_DRAG_RESOURCES.addChangeListener(dndChangedListener);
		super.initializeDragAndDrop(viewer);
	}

	@Override
	protected void installTextDragAndDrop(final ISourceViewer viewer) {
		dndHandler.install(!GamaPreferences.Modeling.EDITOR_DRAG_RESOURCES.getValue());
	}

	@Override
	protected void uninstallTextDragAndDrop(final ISourceViewer viewer) {
		dndHandler.uninstall();
	}

	/**
	 * Gets the uri.
	 *
	 * @return the uri
	 */
	public URI getURI() {
		return fileURI;
	}

}