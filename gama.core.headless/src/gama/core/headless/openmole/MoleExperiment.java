/*********************************************************************************************
 *
 *
 * 'MoleExperiment.java', in plugin 'msi.gama.headless', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.core.headless.openmole;



import gama.core.headless.core.Experiment;
import gama.kernel.model.IModel;
import gama.runtime.exceptions.GamaRuntimeException;
import gaml.expressions.IExpression;
import gaml.expressions.IExpressionFactory;
import gaml.types.Types;

public class MoleExperiment extends Experiment implements IMoleExperiment {
	MoleExperiment(final IModel mdl) {
		super(mdl);
	}

	@Override
	public void play(int finalStep) {
		while(finalStep<this.step());
	}

	public void play(String finalCondition) {
		play(finalCondition,-1);
	}

	@Override
	public void play(String exp, int finalStep) {
			IExpression endCondition = this.compileExpression(exp);
			if(exp==null ||"".equals(exp)) {
				endCondition = IExpressionFactory.FALSE_EXPR;
			} else {
				endCondition =this.compileExpression(exp);			
			}
			if(endCondition.getGamlType() != Types.BOOL) {
				throw GamaRuntimeException.error("The until condition of the experiment should be a boolean", this.getSimulation().getScope());
			}
			long step = 0;
			while( ! Types.BOOL.cast(this.getSimulation().getScope(), this.evaluateExpression(endCondition), null, false).booleanValue()
					 && ((finalStep >= 0) ? (step < finalStep) : true)) {
				step = this.step();
			}
	}
		
}
