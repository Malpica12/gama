/*******************************************************************************************************
 *
 * WrappedSyntacticContent.java, in gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.navigator.contents;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import gama.common.interfaces.IKeyword;
import gama.runtime.GAMA;
import gama.ui.base.resources.IGamaColors;
import gama.ui.base.resources.GamaColors.GamaUIColor;
import gama.ui.base.utils.ThemeHelper;
import gaml.compilation.ast.ISyntacticElement;

/**
 * The Class WrappedSyntacticContent.
 */
public class WrappedSyntacticContent extends VirtualContent<VirtualContent<?>>
		implements Comparable<WrappedSyntacticContent> {

	/** The element. */
	public final ISyntacticElement element;
	
	/** The uri. */
	final URI uri;

	/**
	 * Instantiates a new wrapped syntactic content.
	 *
	 * @param parent the parent
	 * @param e the e
	 */
	private WrappedSyntacticContent(final WrappedSyntacticContent parent, final ISyntacticElement e) {
		this(parent, e, GAMA.getGui().getGamlLabelProvider().getText(e));
	}

	/**
	 * Instantiates a new wrapped syntactic content.
	 *
	 * @param root the root
	 * @param e the e
	 * @param name the name
	 */
	public WrappedSyntacticContent(final VirtualContent<?> root, final ISyntacticElement e, final String name) {
		super(root, name == null ? GAMA.getGui().getGamlLabelProvider().getText(e) : name);
		element = e;
		uri = element == null || element.getElement() == null ? null : EcoreUtil.getURI(element.getElement());
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public WrappedGamaFile getFile() {
		return ((WrappedSyntacticContent) getParent()).getFile();
	}

	@Override
	public boolean hasChildren() {
		if (!element.hasChildren())
			return false;
		if (element.isSpecies())
			return true;
		return false;
	}

	@Override
	public Object[] getNavigatorChildren() {
		if (!hasChildren())
			return null;
		final List<WrappedSyntacticContent> children = new ArrayList<>();
		element.visitAllChildren(elt -> children.add(new WrappedSyntacticContent(WrappedSyntacticContent.this, elt)));
		return children.toArray();
	}

	@Override
	public Image getImage() {
		return (Image) GAMA.getGui().getGamlLabelProvider().getImage(element);
	}

	@Override
	public Color getColor() {
		return ThemeHelper.isDark() ? IGamaColors.VERY_LIGHT_GRAY.color() : IGamaColors.BLACK.inactive();
	}

	@Override
	public boolean handleDoubleClick() {
		GAMA.getGui().editModel(null, element.getElement());
		return true;
	}

	@Override
	public boolean handleSingleClick() {

		GAMA.getGui().editModel(null, element.getElement());
		return true;
	}

	/**
	 * Gets the element.
	 *
	 * @return the element
	 */
	public ISyntacticElement getElement() {
		return element;
	}

	@Override
	public int compareTo(final WrappedSyntacticContent o) {
		final var e = o.element;
		if (element.isSpecies()) {
			if (e.isSpecies())
				return getName().compareTo(o.getName());
			if (element.getKeyword().equals(IKeyword.GRID))
				return 1;
			return 1;
		} else if (e.isSpecies()) {
			return -1;
		} else
			return getName().compareTo(o.getName());

	}

	/**
	 * Gets the URI problem.
	 *
	 * @param fragment the fragment
	 * @return the URI problem
	 */
	public int getURIProblem(final URI fragment) {
		return getFile().getURIProblem(fragment);
	}

	@Override
	public int findMaxProblemSeverity() {
		return getURIProblem(uri);
	}

	@Override
	public void getSuffix(final StringBuilder sb) {}

	@Override
	public ImageDescriptor getOverlay() {
		final var severity = getURIProblem(uri);
		if (severity != -1)
			return DESCRIPTORS.get(severity);
		return null;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.GAML_ELEMENT;
	}

	@Override
	public String getStatusMessage() {
		return getName();
	}

	@Override
	public GamaUIColor getStatusColor() {
		return IGamaColors.BLACK;
	}

	@Override
	public Image getStatusImage() {
		return getImage();
	}

}