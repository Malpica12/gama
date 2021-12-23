/*******************************************************************************************************
 *
 * ChartDataSourceList.java, in gama.core.kernel, is part of the source code of the
 * GAMA modeling and simulation platform (v.2.0.0).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;

import gama.common.interfaces.IKeyword;
import gama.runtime.IScope;
import gama.util.IList;
import gaml.expressions.IExpression;
import gaml.operators.Cast;

/**
 * The Class ChartDataSourceList.
 */
public class ChartDataSourceList extends ChartDataSource {

	/** The currentseries. */
	ArrayList<String> currentseries;
	
	/** The name exp. */
	IExpression nameExp;

	@Override
	public boolean cloneMe(final IScope scope, final int chartCycle, final ChartDataSource source) {

		currentseries = ((ChartDataSourceList) source).currentseries;
		nameExp = ((ChartDataSourceList) source).nameExp;
		final boolean res = super.cloneMe(scope, chartCycle, source);
		return res;
	}

	@Override
	public ChartDataSource getClone(final IScope scope, final int chartCycle) {
		final ChartDataSourceList res = new ChartDataSourceList();
		res.cloneMe(scope, chartCycle, this);
		return res;
	}

	/**
	 * Instantiates a new chart data source list.
	 */
	public ChartDataSourceList() {
		// TODO Auto-generated constructor stub
		super();
	}

	/**
	 * Gets the name exp.
	 *
	 * @return the name exp
	 */
	public IExpression getNameExp() {
		return nameExp;
	}

	/**
	 * Sets the name exp.
	 *
	 * @param scope the scope
	 * @param expval the expval
	 */
	public void setNameExp(final IScope scope, final IExpression expval) {
		nameExp = expval;
	}

	@Override
	public void updatevalues(final IScope scope, final int chartCycle) {
		super.updatevalues(scope, chartCycle);
		Object o = null;
		// final Object oname = this.getNameExp();
		final HashMap<String, Object> barvalues = new HashMap<>();
		if (this.isUseYErrValues()) {
			barvalues.put(ChartDataStatement.YERR_VALUES, this.getValueyerr().value(scope));
		}
		if (this.isUseXErrValues()) {
			barvalues.put(ChartDataStatement.XERR_VALUES, this.getValuexerr().value(scope));
		}
		if (this.isUseYMinMaxValues()) {
			barvalues.put(ChartDataStatement.XERR_VALUES, this.getValuexerr().value(scope));
		}
		if (this.isUseSizeExp()) {
			barvalues.put(ChartDataStatement.MARKERSIZE, this.getSizeexp().value(scope));
		}
		if (this.isUseColorExp()) {
			barvalues.put(IKeyword.COLOR, this.getColorexp().value(scope));
		}

		// TODO check same length and list

		updateserielist(scope, chartCycle);

		// int type_val = this.DATA_TYPE_NULL;
		if (getValue() != null) {
			o = getValue().value(scope);
		}
		// type_val = get_data_type(scope, o);

		if (o == null) {
			// lastvalue??
		} else {
			// TODO Matrix case
			if (o instanceof IList) {
				final IList<?> lval = Cast.asList(scope, o);

				if (lval.size() > 0) {
					for (int i = 0; i < lval.size(); i++) {
						final Object no = lval.get(i);
						if (no != null) {
							updateseriewithvalue(scope, mySeries.get(currentseries.get(i)), no, chartCycle, barvalues,
									i);
						}
					}
				}
			}

		}

	}

	/**
	 * Updateserielist.
	 *
	 * @param scope the scope
	 * @param chartCycle the chart cycle
	 */
	private void updateserielist(final IScope scope, final int chartCycle) {
		final Object oname = getNameExp().value(scope);
		final Object o = getValue().value(scope);

		final ArrayList<String> oldseries = currentseries;
		boolean somethingchanged = false;

		if (oname == null) {
			// lastvalue??
		} else {

			if (oname instanceof IList) {
				final IList<?> lvaln = Cast.asList(scope, oname);
				currentseries = new ArrayList<>();

				if (lvaln.size() > 0) {

					// value list case
					final IList<?> lval = Cast.asList(scope, o);

					for (int i = 0; i < Math.min(lvaln.size(), lval.size()); i++) {
						final Object no = lvaln.get(i);
						if (no != null) {
							final String myname = Cast.asString(scope, no);
							currentseries.add(i, myname);

							if (i >= oldseries.size() || !oldseries.get(i).equals(myname)) {
								somethingchanged = true;
								if (oldseries.contains(myname)) {
									// serie i was serie k before
								} else {
									// new serie
									newserie(scope, myname);
								}
							}
						}
					}
				}
				if (currentseries.size() != oldseries.size()) {
					somethingchanged = true;
				}
				if (somethingchanged) {
					for (int i = 0; i < oldseries.size(); i++) {
						if (!currentseries.contains(oldseries.get(i))) {
							// series i deleted
							removeserie(scope, oldseries.get(i));
						}

					}
					ChartDataSeries s;

					for (int i = 0; i < currentseries.size(); i++) {
						s = this.getDataset().getDataSeries(scope, currentseries.get(i));
						this.getDataset().series.remove(currentseries.get(i));
						this.getDataset().series.put(currentseries.get(i), s);
					}

				}

			}
		}
	}

	/**
	 * Removeserie.
	 *
	 * @param scope the scope
	 * @param string the string
	 */
	private void removeserie(final IScope scope, final String string) {
		// TODO Auto-generated method stub
		this.getDataset().removeserie(scope, string);

	}

	/**
	 * Newserie.
	 *
	 * @param scope the scope
	 * @param myname the myname
	 */
	private void newserie(final IScope scope, final String myname) {
		// TODO Auto-generated method stub
		if (this.getDataset().getDataSeriesIds(scope).contains(myname)) {
			// TODO
			// DO SOMETHING? create id and store correspondance
			// DEBUG.LOG("Serie "+myname+"s already exists... Will
			// replace old one!!");
		}
		final ChartDataSeries myserie = myDataset.createOrGetSerie(scope, myname, this);
		mySeries.put(myname, myserie);

	}

	@Override
	public void createInitialSeries(final IScope scope) {

		final Object on = getNameExp().value(scope);

		if (on instanceof IList) {
			final IList<?> lval = Cast.asList(scope, on);
			currentseries = new ArrayList<>();

			if (lval.size() > 0) {
				for (int i = 0; i < lval.size(); i++) {
					final Object no = lval.get(i);
					if (no != null) {
						final String myname = Cast.asString(scope, no);
						newserie(scope, myname);
						currentseries.add(i, myname);
					}
				}
			}
		}
		inferDatasetProperties(scope);
	}

	// public void inferDatasetProperties(final IScope scope) {
	// int type_val = ChartDataSource.DATA_TYPE_NULL;
	// final IExpression value = getValue();
	// if (value != null) {
	// if (Types.LIST.isAssignableFrom(value.getType()) && value instanceof ListExpression
	// && ((ListExpression) value).getElements().length > 0) {
	// type_val = computeTypeOfData(scope, value);
	// }
	//
	// }
	//
	// getDataset().getOutput().setDefaultPropertiesFromType(scope, this, type_val);
	//
	// }

	/**
	 * Infer dataset properties.
	 *
	 * @param scope the scope
	 */
	public void inferDatasetProperties(final IScope scope) {
		Object o = null;
		int type_val = ChartDataSource.DATA_TYPE_NULL;
		if (this.getValue() != null) {
			o = this.getValue().value(scope);
			if (o instanceof IList && Cast.asList(scope, o).size() > 0) {
				final Object o2 = Cast.asList(scope, o).get(0);
				type_val = get_data_type(scope, o2);
			}

		}

		getDataset().getOutput().setDefaultPropertiesFromType(scope, this, type_val);

	}
}