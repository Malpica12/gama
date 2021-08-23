/*********************************************************************************************
 *
 * 'WrappedFile.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.ui.navigator.contents;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

import gama.ui.base.resources.GamaColors;
import gama.ui.base.utils.ThemeHelper;
import gama.ui.base.utils.WorkbenchHelper;

/**
 * Class LinkedFile.
 *
 * @author drogoul
 * @since 5 févr. 2015
 *
 */
public class LinkedFile extends VirtualContent<Category> implements IAdaptable {

	final WrappedFile file;
	final String suffix;

	/**
	 * @param root
	 * @param name
	 */
	public LinkedFile(final Category root, final IFile wrapped, final String originalName) {
		super(root, NavigatorRoot.getInstance().getManager().findWrappedInstanceOf(wrapped).getName());
		suffix = originalName;
		file = (WrappedFile) getManager().findWrappedInstanceOf(wrapped);
	}

	/**
	 * Method hasChildren()
	 *
	 * @see gama.ui.navigator.contents.VirtualContent#hasChildren()
	 */
	@Override
	public boolean hasChildren() {
		return false;
	}

	// @Override
	// public Font getFont() {
	// return GamaFonts.getNavigLinkFont(); // by default
	// }

	/**
	 * Method getNavigatorChildren()
	 *
	 * @see gama.ui.navigator.contents.VirtualContent#getNavigatorChildren()
	 */
	@Override
	public Object[] getNavigatorChildren() {
		return EMPTY;
	}

	/**
	 * Method getImage()
	 *
	 * @see gama.ui.navigator.contents.VirtualContent#getImage()
	 */
	@Override
	public Image getImage() {
		return DEFAULT_LABEL_PROVIDER.getImage(file.getResource());
	}

	/**
	 * Method getColor()
	 *
	 * @see gama.ui.navigator.contents.VirtualContent#getColor()
	 */
	@Override
	public Color getColor() {
		return ThemeHelper.isDark() ? GamaColors.system(SWT.COLOR_WHITE) : GamaColors.system(SWT.COLOR_BLACK);
	}

	/**
	 * Method isParentOf()
	 *
	 * @see gama.ui.navigator.contents.VirtualContent#isParentOf(java.lang.Object)
	 */
	// @Override
	// public boolean isParentOf(final Object element) {
	// return false;
	// }

	@Override
	public boolean handleDoubleClick() {
		try {
			IDE.openEditor(WorkbenchHelper.getPage(), file.getResource());
		} catch (final PartInitException e1) {
			e1.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Method getAdapter()
	 *
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings ({ "unchecked", "rawtypes" })
	@Override
	public Object getAdapter(final Class adapter) {
		return adapter == IResource.class || adapter == IFile.class ? file.getResource() : null;
	}

	@Override
	public int findMaxProblemSeverity() {
		return file.findMaxProblemSeverity();
	}

	@Override
	public void getSuffix(final StringBuilder sb) {
		sb.append(suffix);
	}

	@Override
	public ImageDescriptor getOverlay() {
		return null;
	}

	@Override
	public VirtualContentType getType() {
		return VirtualContentType.FILE_REFERENCE;
	}

	public WrappedFile getTarget() {
		return file;
	}
}
