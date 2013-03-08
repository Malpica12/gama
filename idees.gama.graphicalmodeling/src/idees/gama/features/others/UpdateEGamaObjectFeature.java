package idees.gama.features.others;

import gama.EGamaObject;
import gama.ESpecies;
import gama.EVariable;

import msi.gama.precompiler.GamlAnnotations.getter;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

public class UpdateEGamaObjectFeature extends AbstractUpdateFeature {
 
    public UpdateEGamaObjectFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    public boolean canUpdate(IUpdateContext context) {
        // return true, if linked business object is a EClass
        Object bo =
            getBusinessObjectForPictogramElement(context.getPictogramElement());
        return (bo instanceof EGamaObject);
    }
 
    public IReason updateNeeded(IUpdateContext context) {
        // retrieve name from pictogram model
        String pictogramName = null;
        String pictogramVar = "";
        PictogramElement pictogramElement = context.getPictogramElement();
        if (pictogramElement instanceof ContainerShape) {
            ContainerShape cs = (ContainerShape) pictogramElement;
            for (Shape shape : cs.getChildren()) {
                if (shape.getGraphicsAlgorithm() instanceof Text) {
                    Text text = (Text) shape.getGraphicsAlgorithm();
                   
                    if (text.getY() != 25)
                    	 pictogramName = text.getValue();
                    else pictogramVar = text.getValue();
                }
            }
        }
 
        // retrieve name from business model
        String businessName = null;
        String varNames = "";
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof EGamaObject) {
        	EGamaObject eClass = (EGamaObject) bo;
            businessName = eClass.getName();
            if (bo instanceof ESpecies) {
	           	 for (EVariable var:((ESpecies)eClass).getVariables() ) {
	           		 String type = (var.getType() == null || var.getType().isEmpty() ) ? "var" : var.getType() ;
	           		 varNames += type + " " + var.getName()+ "\n";
	               }
           }
        }
 
        // update needed, if names are different
        boolean updateNameNeeded =
            ((pictogramName == null && businessName != null) ||
                (pictogramName != null && !pictogramName.equals(businessName)));
        if (updateNameNeeded) {
            return Reason.createTrueReason("Name is out of date");
        } else {
        	 boolean updateVarNeeded = !pictogramVar.equals(varNames);
        	 if (updateVarNeeded) {
        	        return Reason.createTrueReason("Variables are out of date");
            } else {
            	return Reason.createFalseReason();
            }
        }
    }
 
    public boolean update(IUpdateContext context) {
        // retrieve name from business model
        String businessName = null;
        String varNames = "";
        PictogramElement pictogramElement = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        int cpt = 0;
        if (bo instanceof EGamaObject) {
        	EGamaObject eClass = (EGamaObject) bo;
            businessName = eClass.getName();
            if (bo instanceof ESpecies) {
            	 for (EVariable var:((ESpecies)eClass).getVariables() ) {
            		 String type = (var.getType() == null || var.getType().isEmpty() ) ? "var" : var.getType() ;
	           		 varNames += type + " " + var.getName()+ "\n";
	           		 
                }
            	 cpt = ((ESpecies)eClass).getVariables().size();
            }
        }
        
        // Set name in pictogram model
        boolean update = false;
        if (pictogramElement instanceof ContainerShape) {
            ContainerShape cs = (ContainerShape) pictogramElement;
            for (Shape shape : cs.getChildren()) {
            	 if (shape.getGraphicsAlgorithm() instanceof Text) {
                    Text text = (Text) shape.getGraphicsAlgorithm();
                 	if (text.getY() != 25)
                    	text.setValue(businessName);
                    else  {
                    	text.setValue(varNames);
                    	Graphiti.getGaService().setLocationAndSize(text, 5, 25, text.getWidth(), 1 + cpt * 20);
                    }
                    
                    update = true;
                }
            }
        }
 
        return update;
    }
}