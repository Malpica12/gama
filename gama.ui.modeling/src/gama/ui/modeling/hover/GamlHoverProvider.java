/*******************************************************************************************************
 *
 * GamlHoverProvider.java, in gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.ui.modeling.hover;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.internal.text.html.BrowserInformationControl;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hover.DispatchingEObjectTextHover;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hover.html.IXtextBrowserInformationControl;
import org.eclipse.xtext.ui.editor.hover.html.XtextBrowserInformationControl;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.Tuples;

import com.google.inject.Inject;

import gama.common.interfaces.IGamlDescription;
import gama.core.lang.EGaml;
import gama.core.lang.gaml.ActionDefinition;
import gama.core.lang.gaml.ActionRef;
import gama.core.lang.gaml.Facet;
import gama.core.lang.gaml.Function;
import gama.core.lang.gaml.Import;
import gama.core.lang.gaml.S_Definition;
import gama.core.lang.gaml.S_Global;
import gama.core.lang.gaml.Statement;
import gama.core.lang.gaml.TypeRef;
import gama.core.lang.gaml.UnitFakeDefinition;
import gama.core.lang.gaml.UnitName;
import gama.core.lang.resource.GamlResourceServices;
import gama.ui.base.utils.WorkbenchHelper;
import gaml.descriptions.FacetProto;
import gaml.descriptions.SymbolProto;
import gaml.expressions.units.UnitConstantExpression;
import gaml.factories.DescriptionFactory;
import gaml.operators.IUnits;

// TODO: Auto-generated Javadoc
/**
 * The Class GamlHoverProvider.
 */
public class GamlHoverProvider extends DefaultEObjectHoverProvider {

	// public static class NonXRefEObjectAtOffset extends EObjectAtOffsetHelper {
	//
	// @Override
	// protected EObject resolveCrossReferencedElement(final INode node) {
	// final EObject referenceOwner = NodeModelUtils.findActualSemanticObjectFor(node);
	// return referenceOwner;
	// }
	//
	// }

	/**
	 * The Class GamlDispatchingEObjectTextHover.
	 */
	public static class GamlDispatchingEObjectTextHover extends DispatchingEObjectTextHover {

		/** The e object at offset helper. */
		@Inject private EObjectAtOffsetHelper eObjectAtOffsetHelper;

		/** The location in file provider. */
		@Inject private ILocationInFileProvider locationInFileProvider;

		/** The correct. */
		EObject correct = null;

		/**
		 * Gets the xtext element at.
		 *
		 * @param resource the resource
		 * @param offset the offset
		 * @return the xtext element at
		 * @see org.eclipse.xtext.ui.editor.hover.AbstractEObjectHover#getXtextElementAt(org.eclipse.xtext.resource.XtextResource, int)
		 */
		@Override
		protected Pair<EObject, IRegion> getXtextElementAt(final XtextResource resource, final int offset) {
			// BUGFIX AD 2/4/13 : getXtextElementAt() is called twice, one to
			// compute the region
			// from the UI thread, one to compute the objects from the hover
			// thread. The offset in
			// the second call is always false (maybe we should file a bug in
			// XText). The following
			// code is a workaround.
			ITextRegion region = null;
			EObject o;
			if (correct == null) {
				correct = eObjectAtOffsetHelper.resolveContainedElementAt(resource, offset);
				o = correct;
			} else {
				o = correct;
				correct = null;
			}
			// /BUGFIX
			if (o != null) {
				// scope.getGui().debug("Object under hover:" + o.toString());
				if (o instanceof ActionRef) {
					final EObject container = o.eContainer();
					// scope.getGui().debug("Found " + ((ActionRef)
					// o).getRef().getName());
					if (container instanceof Function) {
						// scope.getGui().debug("---> Is a function");
						o = container;
						region = locationInFileProvider.getFullTextRegion(o);
					}
				}
				if (region == null) { region = locationInFileProvider.getSignificantTextRegion(o); }
				final IRegion region2 = new Region(region.getOffset(), region.getLength());
				/*
				 * if ( TextUtilities.overlaps(region2, new Region(offset, 0)) )
				 */ {
					 return Tuples.create(o, region2);
				 }
			}
			final ILeafNode node = NodeModelUtils.findLeafNodeAtOffset(resource.getParseResult().getRootNode(), offset);
			if (node != null && node.getGrammarElement() instanceof Keyword) {
				final IRegion region2 = new Region(node.getOffset(), node.getLength());
				return Tuples.create(node.getGrammarElement(), region2);
			}
			return null;
		}

		/**
		 * Gets the hover info.
		 *
		 * @param first the first
		 * @param textViewer the text viewer
		 * @param hoverRegion the hover region
		 * @return the hover info
		 * @see org.eclipse.xtext.ui.editor.hover.DispatchingEObjectTextHover#getHoverInfo(org.eclipse.emf.ecore.EObject, org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
		 */
		@Override
		public Object getHoverInfo(final EObject first, final ITextViewer textViewer, final IRegion hoverRegion) {
			return super.getHoverInfo(first, textViewer, hoverRegion);
		}

	}

	/**
	 * The Class GamlHoverControlCreator.
	 */
	public class GamlHoverControlCreator extends HoverControlCreator {

		/**
		 * Instantiates a new gaml hover control creator.
		 *
		 * @param informationPresenterControlCreator the information presenter control creator
		 */
		public GamlHoverControlCreator(final IInformationControlCreator informationPresenterControlCreator) {
			super(informationPresenterControlCreator);
		}

		/**
		 * The Class GamlInformationControl.
		 */
		public class GamlInformationControl extends XtextBrowserInformationControl {

			/**
			 * Sets the size.
			 *
			 * @param width the width
			 * @param height the height
			 * @see org.eclipse.jface.internal.text.html.BrowserInformationControl#setSize(int, int)
			 */
			@Override
			public void setSize(final int width, final int height) {
				super.setSize(width, height);
				final org.eclipse.swt.graphics.Point p = WorkbenchHelper.getDisplay().getCursorLocation();
				p.x -= 5;
				p.y += 15;
				setLocation(p);
			}

			/**
			 * Instantiates a new gaml information control.
			 *
			 * @param parent the parent
			 * @param symbolicFontName the symbolic font name
			 * @param statusFieldText the status field text
			 */
			public GamlInformationControl(final Shell parent, final String symbolicFontName,
					final String statusFieldText) {
				super(parent, symbolicFontName, statusFieldText);
			}

			/**
			 * Gets the information presenter control creator.
			 *
			 * @return the information presenter control creator
			 * @see org.eclipse.jface.text.AbstractInformationControl#getInformationPresenterControlCreator()
			 */
			/*
			 * @see org.eclipse.jface.text.IInformationControlExtension5# getInformationPresenterControlCreator()
			 */
			@Override
			public IInformationControlCreator getInformationPresenterControlCreator() {
				return GamlHoverProvider.this.getInformationPresenterControlCreator();
			}
		}

		/**
		 * Do create information control.
		 *
		 * @param parent the parent
		 * @return the i information control
		 * @see org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider.HoverControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
		 */
		@Override
		public IInformationControl doCreateInformationControl(final Shell parent) {

			final String tooltipAffordanceString = EditorsUI.getTooltipAffordanceString();
			if (BrowserInformationControl.isAvailable(parent)) {
				final String font = "org.eclipse.jdt.ui.javadocfont"; // FIXME:
				// PreferenceConstants.APPEARANCE_JAVADOC_FONT;
				final IXtextBrowserInformationControl iControl =
						new GamlInformationControl(parent, font, tooltipAffordanceString) {

				};
				addLinkListener(iControl);
				return iControl;
			}
			return new DefaultInformationControl(parent, tooltipAffordanceString);
		}
	}

	/** The creator. */
	private IInformationControlCreator creator;

	/**
	 * Gets the hover control creator.
	 *
	 * @return the hover control creator
	 * @see org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider#getHoverControlCreator()
	 */
	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (creator == null) { creator = new GamlHoverControlCreator(getInformationPresenterControlCreator()); }
		return creator;
	}

	/**
	 * Gets the hover info.
	 *
	 * @param first the first
	 * @param textViewer the text viewer
	 * @param hoverRegion the hover region
	 * @return the hover info
	 * @see org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider#getHoverInfo(org.eclipse.emf.ecore.EObject, org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public IInformationControlCreatorProvider getHoverInfo(final EObject first, final ITextViewer textViewer,
			final IRegion hoverRegion) {

		return super.getHoverInfo(first, textViewer, hoverRegion);

	}

	//
	// @Override
	// protected String getHoverInfoAsHtml(final EObject o) {
	// String s = super.getHoverInfoAsHtml(o);
	// if ( s == null || s.trim().isEmpty() ) { return null; }
	// return s;
	// }

	/**
	 * Checks for hover.
	 *
	 * @param o the o
	 * @return true, if successful
	 * @see org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider#hasHover(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected boolean hasHover(final EObject o) {
		return true;
		// String s = getFirstLine(o);
		// return s != null && !s.isEmpty();
	}

	/**
	 * Gets the first line.
	 *
	 * @param o the o
	 * @return the first line
	 * @see org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider#getFirstLine(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	protected String getFirstLine(final EObject o) {
		if (o instanceof Import) {
			String uri = ((Import) o).getImportURI();
			uri = uri.substring(uri.lastIndexOf('/') + 1);
			final String model = ((Import) o).getName() != null ? "micro-model" : "model";
			return "<b>Import of the " + model + " defined in <i>" + uri + "</i></b>";
		}
		if (o instanceof S_Global) return "<b>Global definitions of </b>" + getFirstLine(o.eContainer().eContainer());
		final Statement s = EGaml.getInstance().getStatement(o);
		if (o instanceof TypeRef && s instanceof S_Definition && ((S_Definition) s).getTkey() == o)
			return getFirstLine(s);
		// Case of do xxx;
		// if (o instanceof VariableRef && o.eContainer() instanceof S_Do && ((S_Do) o.eContainer()).getExpr() == o) {
		// final VarDefinition vd = ((VariableRef) o).getRef();
		// final IGamlDescription description = GamlResourceServices.getResourceDocumenter().getGamlDocumentation(vd);
		// if (description != null) {
		// String result = description.getTitle();
		// if (result == null || result.isEmpty()) return "";
		// return "<b>" + result + "</b>";
		// }
		// if (vd != null && vd.eContainer() == null) {
		// final IEObjectDescription desc = BuiltinGlobalScopeProvider.getVar(vd.getName());
		// if (desc != null) {
		// String userData = desc.getUserData("title");
		// if (userData != null && !userData.isEmpty()) return "<b>" + userData + "</b>";
		// }
		// }
		// }
		if (o instanceof Function) {
			final ActionRef ref = getActionFrom((Function) o);
			if (ref != null) {
				final ActionDefinition def = ref.getRef();
				if (def != null) {
					final String temp = getFirstLine(def);
					if (!temp.isEmpty()) return temp;
				}
			}
		} else if (o instanceof UnitName) {
			final UnitFakeDefinition fake = ((UnitName) o).getRef();
			if (fake == null) return "<b> Unknown unit or constant </b>";
			final UnitConstantExpression unit = IUnits.UNITS_EXPR.get(fake.getName());
			if (unit == null) return "<b> Unknown unit or constant </b>";
			return "<b>" + unit.getTitle() + "</b>";
		}

		final IGamlDescription description = GamlResourceServices.getResourceDocumenter().getGamlDocumentation(o);
		if (description != null) {
			String result = description.getTitle();
			if (result == null || result.isEmpty()) return "";
			return "<b>" + result + "</b>";
		}
		if (o instanceof Facet) return "<b>" + getFirstLineOf((Facet) o) + "</b>";

		if (s != null && DescriptionFactory.isStatementProto(EGaml.getInstance().getKeyOf(o))) {
			if (s == o) return "";
			return getFirstLine(s);
		}
		if (o instanceof TypeRef) return "<b>Type " + EGaml.getInstance().getKeyOf(o) + "</b>";
		return "";
	}

	/**
	 * Gets the action from.
	 *
	 * @param f
	 *            the f
	 * @return the action from
	 */
	private ActionRef getActionFrom(final Function f) {
		if (f.getLeft() instanceof ActionRef) return (ActionRef) f.getLeft();
		return null;
	}

	/**
	 * Gets the first line of.
	 *
	 * @param o the o
	 * @return the first line of
	 */
	private String getFirstLineOf(final Facet o) {

		String facetName = o.getKey();
		facetName = facetName.substring(0, facetName.length() - 1);
		final EObject cont = o.eContainer();
		final String key = EGaml.getInstance().getKeyOf(cont);
		final SymbolProto p = DescriptionFactory.getProto(key, null);
		if (p != null) {
			final FacetProto f = p.getPossibleFacets().get(facetName);
			if (f != null) return f.getTitle();
		}
		return "Facet " + o.getKey();

	}
}