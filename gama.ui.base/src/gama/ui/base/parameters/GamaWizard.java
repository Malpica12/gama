/*******************************************************************************************************
 *
 * GamaWizard.java, in gama.ui.base, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.ui.base.parameters;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import gama.runtime.GAMA;
import gama.util.GamaMapFactory;
import gama.util.IMap;
import gaml.descriptions.ActionDescription;
import gaml.descriptions.ConstantExpressionDescription;
import gaml.expressions.IExpression;
import gaml.statements.ActionStatement;
import gaml.statements.Arguments;


/**
 * The Class GamaWizard.
 */
public class GamaWizard extends Wizard{
	
	/** The pages. */
	protected List<GamaWizardPage> pages;
	
	/** The title. */
	protected String title;
	
	/** The exp. */
	protected IExpression exp;
	
	/** The finish. */
	protected ActionDescription finish;
	
	/**
	 * Instantiates a new gama wizard.
	 *
	 * @param title the title
	 * @param finish the finish
	 * @param pages the pages
	 */
	public GamaWizard(String title, ActionDescription finish, List<GamaWizardPage> pages) {
        super();
        this.title = title;
        this.pages = pages;
        setNeedsProgressMonitor(true);
       	this.finish = finish;
    }

    @Override
    public String getWindowTitle() {
        return title;
    }
    
    /**
     * Gets the values.
     *
     * @return the values
     */
    public IMap<String,IMap<String, Object>> getValues() {
    	IMap<String,IMap<String, Object>> values = GamaMapFactory.create();
    	for(GamaWizardPage p : pages) {
    		values.put(p.getTitle(),p.getValues());
    	}
    	return values;
    }

    @Override
    public void addPages() {
    	for (GamaWizardPage p : pages) {
    		  addPage(p);
    	}
    }

    @Override
    public boolean canFinish() {
    	if (finish == null) return true;
    	ActionStatement actionSC = (ActionStatement) finish.compile();
    	if(finish.getArgNames().isEmpty()) return  (Boolean) actionSC.executeOn(GAMA.getRuntimeScope());
        final Arguments argsSC = new Arguments();
    	argsSC.put(finish.getArgNames().get(0), ConstantExpressionDescription.create(getValues()));
		actionSC.setRuntimeArgs(GAMA.getRuntimeScope(), argsSC);
		final Boolean isFinished = (Boolean) actionSC.executeOn(GAMA.getRuntimeScope());
		return isFinished;
    }

	@Override
	public boolean performFinish() {
		return true;
	}
    
   

}