/*********************************************************************************************
 *
 * 'CSVTextEditor.java, in plugin ummisco.gama.ui.viewers, is part of the source code of the GAMA modeling and
 * simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package gama.ui.viewers.csv.text;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.editors.text.TextEditor;

import gama.ui.base.toolbar.GamaToolbar2;
import gama.ui.base.toolbar.GamaToolbarFactory;
import gama.ui.base.toolbar.IToolbarDecoratedView;

/**
 * {@link CSVTextEditor} extends basic {@link TextEditor} adding syntax highlighting for the separated elements
 * 
 * @author J. Andres Pizarro Gascon
 */
public class CSVTextEditor extends TextEditor implements IToolbarDecoratedView.Sizable {

	// GamaToolbar2 toolbar;
	Composite ancestor;
	Composite parent;

	public CSVTextEditor(final char delimiter) {
		final CSVTextSourceViewerConfiguration csvTextConfig =
				new CSVTextSourceViewerConfiguration(delimiter, getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
	}

	@Override
	public void createPartControl(final Composite composite) {
		ancestor = composite;
		parent = new Composite(composite, SWT.NONE);
		final Composite compo = GamaToolbarFactory.createToolbars(this, parent);
		super.createPartControl(compo);
	}

	public void setDelimiter(final char delimiter) {
		final CSVTextSourceViewerConfiguration csvTextConfig =
				new CSVTextSourceViewerConfiguration(delimiter, getPreferenceStore());
		setSourceViewerConfiguration(csvTextConfig);
		parent.dispose();
		createPartControl(ancestor);
	}

	@Override
	public Control getSizableFontControl() {
		if (getSourceViewer() == null) { return null; }
		return getSourceViewer().getTextWidget();
	}

	/**
	 * Method createToolItem()
	 * 
	 * @see gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int,
	 *      gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {

		tb.button("menu.saveas2", "Save as...", "Save as...", e -> doSaveAs(), SWT.RIGHT);

	}
	//
	// @Override
	// public void setToogle(final Action toggle) {}

}