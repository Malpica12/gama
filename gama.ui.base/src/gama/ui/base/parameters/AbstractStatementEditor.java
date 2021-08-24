package gama.ui.base.parameters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import gama.runtime.IScope;
import gama.runtime.exceptions.GamaRuntimeException;
import gama.ui.base.controls.FlatButton;
import gama.ui.base.interfaces.EditorListener;

public abstract class AbstractStatementEditor<T> extends AbstractEditor<Object> {

	protected FlatButton textBox;
	T statement;

	public AbstractStatementEditor(final IScope scope, final T command, final EditorListener<Object> l) {
		super(scope, l);
		this.statement = command;
	}

	public T getStatement() {
		return statement;
	}

	public void setStatement(final T s) {
		statement = s;
	}

	@Override
	protected final int[] getToolItems() {
		return new int[0];
	}

	@Override
	protected final Object retrieveValueOfParameter() throws GamaRuntimeException {
		return null;
	}

	@Override
	protected void updateToolbar() {}

	@Override
	public void createControls(final EditorsGroup parent) {
		this.parent = parent;
		internalModification = true;
		// Create the label of the value editor
		editorLabel = createEditorLabel();
		// Create the composite that will hold the value editor and the toolbar
		createValueComposite();
		// Create and initialize the value editor
		editorControl = createEditorControl();

		if (isSubParameter) {
			editorLabel = new EditorLabel(this, composite, name, isSubParameter);
			editorLabel.setHorizontalAlignment(SWT.LEAD);
		}
		// Create and initialize the toolbar associated with the value editor
		editorToolbar = createEditorToolbar();
		internalModification = false;
		parent.layout();
	}

	@Override
	EditorLabel createEditorLabel() {
		if (!isSubParameter)
			return super.createEditorLabel();
		else
			return new EditorLabel(this, parent, " ", isSubParameter);
	}

	@Override
	protected final void displayParameterValue() {}

	@Override
	protected GridData getParameterGridData() {
		final var d = new GridData(SWT.FILL, SWT.CENTER, false, false);
		d.minimumWidth = 50;
		return d;
	}

}