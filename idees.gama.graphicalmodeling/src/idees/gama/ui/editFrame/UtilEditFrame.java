package idees.gama.ui.editFrame;

import java.util.List;

import gama.EGamaObject;
import gama.EWorldAgent;
import idees.gama.features.edit.EditFeature;
import idees.gama.features.modelgeneration.ModelGenerator;

import msi.gaml.compilation.GamlCompilationError;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

public class UtilEditFrame {

	public static void buildCanvasValidation(Composite container, Canvas canvasValidation, final StyledText validationResult, final IFeatureProvider fp, final Diagram diagram, final EGamaObject eobject) {
		//****** CANVAS VALIDATION *********
		canvasValidation.setBounds(10, 580, 720, 95);
				
		validationResult.setBounds(5, 30, 700, 55);
		validationResult.setEditable(false);
				
		Button btnValidate = new Button(canvasValidation, SWT.NONE);
		btnValidate.addSelectionListener(new SelectionAdapter() {
				 
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<GamlCompilationError> errors = ModelGenerator.modelValidation(fp, diagram);
				String eC = "";
				for (GamlCompilationError val : errors) eC += val.toString() + "\n";
				validationResult.setText(eC);	
			}
		});
		btnValidate.setBounds(75, 5, 80, 20);;
		btnValidate.setText("Validate"); 		
		CLabel lblCompilation = new CLabel(canvasValidation, SWT.NONE);
		lblCompilation.setText("Validation"); 
		lblCompilation.setBounds(5, 5, 70, 20);
	}
	
	public static void buildCanvasName(Composite container, Canvas canvasName, final Text textName, final EGamaObject eobject, final EditFeature ef)  {
		//****** CANVAS NAME *********
		canvasName.setBounds(10, 10, 720, 30);
				
		//textName = new Text(canvasName, SWT.BORDER);
		textName.setBounds(70, 5, 300, 20);
		textName.setText(eobject.getName());
		if (eobject instanceof EWorldAgent) {
			textName.setEditable(false);
		}
			
		CLabel lblName = new CLabel(canvasName, SWT.NONE);
		lblName.setBounds(10, 5, 60, 20);
		lblName.setText("Name");
	}
	
	public static void buildGroupName(Composite container, Group groupName, Text textName, final EGamaObject eobject, final EditFeature ef)  {
		//****** GROUP NAME *********
		textName = new Text(groupName, SWT.BORDER);
		textName.setText(eobject.getName());
		if (eobject instanceof EWorldAgent) {
			textName.setEditable(false);
		}
			
		CLabel lblName = new CLabel(groupName, SWT.NONE);
		lblName.setText("Name");
	}
}
