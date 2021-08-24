/*******************************************************************************************************
 *
 * AbstractOutput.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.outputs;

import java.util.Collections;
import java.util.List;

import gama.common.interfaces.IKeyword;
import gama.core.dev.annotations.GamlAnnotations.inside;
import gama.kernel.experiment.ExperimentAgent;
import gama.runtime.GAMA;
import gama.runtime.IScope;
import gaml.compilation.ISymbol;
import gaml.compilation.Symbol;
import gaml.descriptions.IDescription;
import gaml.descriptions.ModelDescription;
import gaml.expressions.IExpression;
import gaml.expressions.IExpressionFactory;
import gaml.operators.Cast;

/**
 * The Class AbstractOutput.
 *
 * @author drogoul
 */
@inside (
		symbols = IKeyword.OUTPUT)
public abstract class AbstractOutput extends Symbol implements IOutput {

	/** The output scope. */
	private IScope outputScope;
	
	/** The permanent. */
	boolean paused, open, permanent = false;
	
	/** The is user created. */
	private boolean isUserCreated = true;
	
	/** The refresh. */
	final IExpression refresh;
	
	/** The original name. */
	final String originalName;

	/** The refresh rate. */
	private int refreshRate = 1;

	/**
	 * Instantiates a new abstract output.
	 *
	 * @param desc the desc
	 */
	public AbstractOutput(final IDescription desc) {
		super(desc);
		if (hasFacet(IKeyword.REFRESH)) {
			refresh = this.getFacet(IKeyword.REFRESH);
		} else {
			refresh = IExpressionFactory.TRUE_EXPR;
		}

		name = desc.getName();
		originalName = name;
		if (name != null) {
			name = name.replace(':', '_').replace('/', '_').replace('\\', '_');
			if (name.length() == 0) {
				name = "output";
			}
		}
	}

	@Override
	public String getOriginalName() {
		return originalName;
	}

	/**
	 * Checks if is user created.
	 *
	 * @return true, if is user created
	 */
	// @Override
	final boolean isUserCreated() {
		return isUserCreated;
	}

	// @Override
	@Override
	public final void setUserCreated(final boolean isUserCreated) {
		this.isUserCreated = isUserCreated;
	}

	@Override
	public boolean init(final IScope scope) {
		setScope(scope.copy("of " + getDescription().getKeyword() + " " + getName()));
		final IExpression refreshExpr = getFacet(IKeyword.REFRESH_EVERY);
		if (refreshExpr != null) {
			setRefreshRate(Cast.asInt(getScope(), refreshExpr.value(getScope())));
		}
		getScope().setCurrentSymbol(this);
		return true;
	}

	@Override
	public void close() {
		setPaused(true);
		setOpen(false);
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public void open() {
		setOpen(true);
	}

	// @Override
	@Override
	public boolean isRefreshable() {
		if (!isOpen()) { return false; }
		if (isPaused()) { return false; }
		final IScope scope = getScope();
		if (scope == null || scope.interrupted()) { return false; }
		return Cast.asBool(scope, refresh.value(scope)) && refreshRate > 0
				&& scope.getClock().getCycle() % refreshRate == 0;
	}

	@Override
	public int getRefreshRate() {
		return refreshRate;
	}

	@Override
	public void setRefreshRate(final int refresh) {
		refreshRate = refresh;
	}

	@Override
	public abstract boolean step(IScope scope);

	/**
	 * Sets the open.
	 *
	 * @param open the new open
	 */
	void setOpen(final boolean open) {
		this.open = open;
	}

	@Override
	public void setPaused(final boolean suspended) {
		paused = suspended;
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {

	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<? extends ISymbol> getChildren() {
		return Collections.EMPTY_LIST;
	}

	// @Override
	@Override
	public String getId() {
		if (!this.getDescription().getModelDescription().getAlias().equals("")) {
			return getName() + "#" + this.getDescription().getModelDescription().getAlias() + "#"
					+ getScope().getExperiment().getName();
		}
		return getName(); // by default
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope the new scope
	 */
	public void setScope(final IScope scope) {
		if (this.outputScope != null) {
			GAMA.releaseScope(this.outputScope);
		}
		final ModelDescription micro = this.getDescription().getModelDescription();
		if (scope.getModel() != null) {
			final ModelDescription main = (ModelDescription) scope.getModel().getDescription();
			final Boolean fromMicroModel = main.getMicroModel(micro.getAlias()) != null;
			if (fromMicroModel) {
				final ExperimentAgent exp = (ExperimentAgent) scope.getRoot()
						.getExternMicroPopulationFor(micro.getAlias() + "." + this.getDescription().getOriginName())
						.getAgent(0);
				this.outputScope = exp.getSimulation().getScope();
			} else {
				this.outputScope = scope;
			}
		} else {
			this.outputScope = scope;
		}
	}

	@Override
	public IScope getScope() {
		return outputScope;
	}

	/**
	 * Sets the permanent.
	 */
	// @Override
	void setPermanent() {
		permanent = true;
	}

	/**
	 * Checks if is permanent.
	 *
	 * @return true, if is permanent
	 */
	public boolean isPermanent() {
		return permanent;
	}

}
