/*******************************************************************************************************
 *
 * ArrangeDisplayViews.java, in gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.experiment.commands;

import static gama.common.interfaces.IKeyword.LAYOUT;
import static gama.ui.base.utils.WorkbenchHelper.findDisplay;
import static gaml.operators.Displays.HORIZONTAL;
import static gaml.operators.Displays.VERTICAL;
import static org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory.INSTANCE;
import static org.eclipse.e4.ui.workbench.modeling.EModelService.IN_ACTIVE_PERSPECTIVE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainer;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import gama.common.preferences.GamaPreferences;
import gama.common.ui.IGamaView;
import gama.core.dev.utils.DEBUG;
import gama.ui.base.utils.PerspectiveHelper;
import gama.ui.base.utils.WorkbenchHelper;
import gama.util.tree.GamaNode;
import gama.util.tree.GamaTree;
import one.util.streamex.StreamEx;

/**
 * The Class ArrangeDisplayViews.
 */
@SuppressWarnings ({ "rawtypes" })
public class ArrangeDisplayViews extends AbstractHandler {

	static {
		DEBUG.ON();
	}

	/** The Constant LAYOUT_KEY. */
	public static final String LAYOUT_KEY = "gama.displays.layout";

	/** The Constant DISPLAY_INDEX_KEY. */
	static final String DISPLAY_INDEX_KEY = "GamaIndex";

	@Override
	public Object execute(final ExecutionEvent e) {
		final String layout = e.getParameter(LAYOUT_KEY);
		final int orientation = GamaPreferences.Displays.LAYOUTS.indexOf(layout);
		execute(orientation);
		return true;
	}

	/**
	 * Execute.
	 *
	 * @param layout
	 *            the layout
	 */
	@SuppressWarnings ({ "unchecked", "cast" })
	public static void execute(final Object layout) {
		listDisplayViews();
		if (layout instanceof Integer) {
			execute((Integer) layout);
		} else if (layout instanceof GamaTree) {
			execute((GamaTree<String>) layout);
		} else if (layout instanceof GamaNode) {
			final GamaTree<String> tree = LayoutTreeConverter.newLayoutTree();
			((GamaNode<String>) layout).attachTo(tree.getRoot());
			execute(tree);
		}
	}

	/**
	 * Execute.
	 *
	 * @param layout
	 *            the layout
	 */
	public static void execute(final Integer layout) {
		execute(new LayoutTreeConverter().convert(layout));
	}

	/**
	 * Gets the part service.
	 *
	 * @return the part service
	 */
	private static EPartService getPartService() {
		return WorkbenchHelper.getService(EPartService.class);
	}

	/**
	 * Gets the application.
	 *
	 * @return the application
	 */
	private static MApplication getApplication() {
		return WorkbenchHelper.getService(MApplication.class);
	}

	/**
	 * Gets the model service.
	 *
	 * @return the model service
	 */
	private static EModelService getModelService() {
		return WorkbenchHelper.getService(EModelService.class);
	}

	/**
	 * Execute.
	 *
	 * @param tree
	 *            the tree
	 */
	public static void execute(final GamaTree<String> tree) {
		listDisplayViews();
		// final List<IGamaView.Display> displays = WorkbenchHelper.getDisplayViews();

		if (tree != null) {
			DEBUG.LOG("Tree root = " + tree.getRoot().getChildren().get(0).getData() + " weight "
					+ tree.getRoot().getChildren().get(0).getWeight());
			if (tree.getRoot().getChildren().get(0).getWeight() == null) {
				tree.getRoot().getChildren().get(0).setWeight(5000);
			}
			final List<MPlaceholder> holders = listDisplayViews();
			final MPartStack displayStack = getDisplaysPlaceholder();
			if (displayStack == null) return;
			displayStack.setToBeRendered(true);
			final MElementContainer<?> root = displayStack.getParent();
			hideDisplays(displayStack, holders);
			process(root, tree.getRoot().getChildren().get(0), holders);
			showDisplays(root, holders);
		} else {
			decorateDisplays();
		}

	}

	/**
	 * Activate displays.
	 *
	 * @param holders
	 *            the holders
	 * @param focus
	 *            the focus
	 */
	private static void activateDisplays(final List<MPlaceholder> holders, final boolean focus) {
		holders.forEach(ph -> {
			getPartService().bringToTop((MPart) ph.getRef());
			getPartService().activate((MPart) ph.getRef(), focus);

		});
	}

	/**
	 * Gets the displays placeholder.
	 *
	 * @return the displays placeholder
	 */
	public static MPartStack getDisplaysPlaceholder() {
		final Object displayStack = getModelService().find("displays", getApplication());
		// DEBUG.OUT("Element displays found : " + displayStack);
		return displayStack instanceof MPartStack ? (MPartStack) displayStack : null;
	}

	/**
	 * Show displays.
	 *
	 * @param root
	 *            the root
	 * @param holders
	 *            the holders
	 */
	public static void showDisplays(final MElementContainer<?> root, final List<MPlaceholder> holders) {
		root.setVisible(true);
		decorateDisplays();
		holders.forEach(ph -> {
			ph.setVisible(true);
			ph.setToBeRendered(true);
		});
		activateDisplays(holders, true);
	}

	/**
	 * Decorate displays.
	 */
	public static void decorateDisplays() {
		WorkbenchHelper.getDisplayViews().forEach(v -> {
			final Boolean tb = PerspectiveHelper.keepToolbars();
			if (tb != null) {
				if (tb) {
					v.showToolbar();
				} else {
					v.hideToolbar();
				}
			}
			if (PerspectiveHelper.showOverlays()) {
				v.showOverlay();
			} else {
				v.hideOverlay();
			}

		});
	}

	/**
	 * Hide displays.
	 *
	 * @param displayStack
	 *            the display stack
	 * @param holders
	 *            the holders
	 */
	public static void hideDisplays(final MPartStack displayStack, final List<MPlaceholder> holders) {
		final MElementContainer<MUIElement> parent = displayStack.getParent();
		parent.setVisible(false);
		holders.forEach(ph -> {
			ph.setVisible(false);
			displayStack.getChildren().add(ph);
		});
		activateDisplays(holders, false);
		for (final MUIElement element : new ArrayList<>(parent.getChildren())) {
			if (element.getTransientData().containsKey(LAYOUT)) {
				element.setVisible(false);
				element.setToBeRendered(false);
				parent.getChildren().remove(element);
			}
		}
	}

	/**
	 * Checks if is part of layout.
	 *
	 * @param e
	 *            the e
	 * @return true, if is part of layout
	 */
	static boolean isPartOfLayout(final MUIElement e) {
		return e.getTransientData().containsKey(LAYOUT);
	}

	/**
	 * Process.
	 *
	 * @param uiRoot
	 *            the ui root
	 * @param treeRoot
	 *            the tree root
	 * @param holders
	 *            the holders
	 */
	public static void process(final MElementContainer uiRoot, final GamaNode<String> treeRoot,
			final List<MPlaceholder> holders) {
		final String data = treeRoot.getData();
		final String weight = String.valueOf(treeRoot.getWeight());
		// DEBUG.OUT("Processing " + data + " with weight " + weight);
		final Boolean dir = !HORIZONTAL.equals(data) && !VERTICAL.equals(data) ? null : HORIZONTAL.equals(data);
		final MPlaceholder holder = StreamEx.of(holders)
				.findFirst(h -> h.getTransientData().get(DISPLAY_INDEX_KEY).equals(data)).orElse(null);
		final MElementContainer container = create(uiRoot, weight, dir);
		if (holder != null) {
			if (container.equals(uiRoot)) { holder.setContainerData(weight); }
			container.getChildren().add(holder);
		} else {
			for (final GamaNode<String> node : treeRoot.getChildren()) {
				process(container, node, holders);
			}
		}
	}

	/**
	 * List display views.
	 *
	 * @return the list
	 */
	static final List<MPlaceholder> listDisplayViews() {
		final List<MPlaceholder> holders = getModelService().findElements(getApplication(), MPlaceholder.class,
				IN_ACTIVE_PERSPECTIVE, e -> WorkbenchHelper.isDisplay(e.getElementId()));
		/// Issue #2680
		int currentIndex = 0;
		for (final MPlaceholder h : holders) {
			final IGamaView.Display display = findDisplay(h.getElementId());
			if (display != null) {
				display.setIndex(currentIndex++);
				h.getTransientData().put(DISPLAY_INDEX_KEY, String.valueOf(currentIndex - 1));
			}
		}

		return holders;
	}

	/**
	 * Creates the.
	 *
	 * @param root
	 *            the root
	 * @param weight
	 *            the weight
	 * @param dir
	 *            the dir
	 * @return the m element container
	 */
	static MElementContainer create(final MElementContainer root, final String weight, final Boolean dir) {
		if (dir == null && root instanceof MPartStack && isPartOfLayout(root)) return root;
		if (dir == null && (root instanceof MPartStack || !PerspectiveHelper.keepTabs())) return root;
		final MElementContainer c = dir != null ? INSTANCE.createPartSashContainer() : INSTANCE.createPartStack();
		c.getTransientData().put("Dynamic", true);
		c.getTransientData().put(LAYOUT, true);
		c.setContainerData(weight);
		if (dir != null) { ((MPartSashContainer) c).setHorizontal(dir); }
		if (root != null) { root.getChildren().add(c); }
		return c;
	}

}