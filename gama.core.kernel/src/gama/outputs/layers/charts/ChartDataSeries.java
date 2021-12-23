/*******************************************************************************************************
 *
 * ChartDataSeries.java, in gama.core.kernel, is part of the source code of the
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
import gama.util.GamaColor;
import gama.util.IList;
import gaml.expressions.IExpression;
import gaml.operators.Cast;

/**
 * The Class ChartDataSeries.
 */
@SuppressWarnings ({ "rawtypes" })
public class ChartDataSeries {

	/** The cvalues. */
	ArrayList<String> cvalues = new ArrayList<>(); // for categories
	
	/** The xvalues. */
	ArrayList<Double> xvalues = new ArrayList<>(); // for xy charts
	
	/** The yvalues. */
	ArrayList<Double> yvalues = new ArrayList<>();
	
	/** The svalues. */
	ArrayList<Double> svalues = new ArrayList<>(); // for marker sizes or
													
													/** The xerrvaluesmax. */
													// 3d charts
	ArrayList<Double> xerrvaluesmax = new ArrayList<>();
	
	/** The yerrvaluesmax. */
	ArrayList<Double> yerrvaluesmax = new ArrayList<>();
	
	/** The xerrvaluesmin. */
	ArrayList<Double> xerrvaluesmin = new ArrayList<>();
	
	/** The yerrvaluesmin. */
	ArrayList<Double> yerrvaluesmin = new ArrayList<>();

	/** The mymedcolor. */
	GamaColor mycolor, mymincolor, mymedcolor;

	/** The mysource. */
	// HashMap<String,Object> serieParameters=new HashMap<String,Object>();
	ChartDataSource mysource;
	
	/** The mydataset. */
	ChartDataSet mydataset;

	/** The name. */
	String name;

	/** The ongoing update. */
	boolean ongoing_update = false;

	/** The oldcvalues. */
	ArrayList<String> oldcvalues = new ArrayList<>(); // for categories
	
	/** The oldxvalues. */
	ArrayList<Double> oldxvalues = new ArrayList<>(); // for xy charts
	
	/** The oldyvalues. */
	ArrayList<Double> oldyvalues = new ArrayList<>();
	
	/** The oldsvalues. */
	ArrayList<Double> oldsvalues = new ArrayList<>(); // for marker sizes

	/**
	 * Checks if is ongoing update.
	 *
	 * @return true, if is ongoing update
	 */
	public boolean isOngoing_update() {
		return ongoing_update;
	}

	/**
	 * Gets the dataset.
	 *
	 * @return the dataset
	 */
	public ChartDataSet getDataset() {
		return mydataset;
	}

	/**
	 * Sets the dataset.
	 *
	 * @param mydataset the new dataset
	 */
	public void setDataset(final ChartDataSet mydataset) {
		this.mydataset = mydataset;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the mysource.
	 *
	 * @return the mysource
	 */
	public ChartDataSource getMysource() {
		return mysource;
	}

	/**
	 * Sets the mysource.
	 *
	 * @param mysource the new mysource
	 */
	public void setMysource(final ChartDataSource mysource) {
		this.mysource = mysource;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the serie legend.
	 *
	 * @param scope the scope
	 * @return the serie legend
	 */
	public Comparable getSerieLegend(final IScope scope) {
		// TODO Auto-generated method stub
		return name;
	}

	/**
	 * Gets the serie id.
	 *
	 * @param scope the scope
	 * @return the serie id
	 */
	public String getSerieId(final IScope scope) {
		// TODO Auto-generated method stub
		return name;
	}

	/**
	 * Gets the style.
	 *
	 * @param scope the scope
	 * @return the style
	 */
	public String getStyle(final IScope scope) {
		return this.getMysource().getStyle(scope);
	}

	/**
	 * Gets the mycolor.
	 *
	 * @return the mycolor
	 */
	public GamaColor getMycolor() {
		return mycolor;
	}

	/**
	 * Gets the my medcolor.
	 *
	 * @return the my medcolor
	 */
	public GamaColor getMyMedcolor() {
		return mymedcolor;
	}

	/**
	 * Gets the my mincolor.
	 *
	 * @return the my mincolor
	 */
	public GamaColor getMyMincolor() {
		return mymincolor;
	}

	/**
	 * Sets the mycolor.
	 *
	 * @param mycolor the new mycolor
	 */
	public void setMycolor(final GamaColor mycolor) {
		this.mycolor = mycolor;
	}

	/**
	 * Sets the my medcolor.
	 *
	 * @param mycolor the new my medcolor
	 */
	public void setMyMedcolor(final GamaColor mycolor) {
		this.mymedcolor = mycolor;
	}

	/**
	 * Sets the my mincolor.
	 *
	 * @param mycolor the new my mincolor
	 */
	public void setMyMincolor(final GamaColor mycolor) {
		this.mymincolor = mycolor;
	}

	/**
	 * Checks if is use Y err values.
	 *
	 * @return true, if is use Y err values
	 */
	public boolean isUseYErrValues() {
		return this.getMysource().useYErrValues;
	}

	/**
	 * Sets the use Y err values.
	 *
	 * @param useYErrValues the new use Y err values
	 */
	public void setUseYErrValues(final boolean useYErrValues) {
		this.getMysource().useYErrValues = useYErrValues;
	}

	/**
	 * Checks if is use X err values.
	 *
	 * @return true, if is use X err values
	 */
	public boolean isUseXErrValues() {
		return this.getMysource().useXErrValues;
	}

	/**
	 * Sets the use X err values.
	 *
	 * @param useXErrValues the new use X err values
	 */
	public void setUseXErrValues(final boolean useXErrValues) {
		this.getMysource().useXErrValues = useXErrValues;
	}

	/**
	 * Checks if is use Y min max values.
	 *
	 * @return true, if is use Y min max values
	 */
	public boolean isUseYMinMaxValues() {
		return this.getMysource().useYMinMaxValues;
	}

	/**
	 * Sets the use Y min max values.
	 *
	 * @param useYMinMaxValues the new use Y min max values
	 */
	public void setUseYMinMaxValues(final boolean useYMinMaxValues) {
		this.getMysource().useYMinMaxValues = useYMinMaxValues;
	}

	/**
	 * Gets the c values.
	 *
	 * @param scope the scope
	 * @return the c values
	 */
	public ArrayList<String> getCValues(final IScope scope) {
		// TODO Auto-generated method stub
		if (isOngoing_update()) { return oldcvalues; }
		return cvalues;
	}

	/**
	 * Gets the x values.
	 *
	 * @param scope the scope
	 * @return the x values
	 */
	public ArrayList<Double> getXValues(final IScope scope) {
		// TODO Auto-generated method stub
		if (isOngoing_update()) { return oldxvalues; }
		return xvalues;
	}

	/**
	 * Gets the y values.
	 *
	 * @param scope the scope
	 * @return the y values
	 */
	public ArrayList<Double> getYValues(final IScope scope) {
		// TODO Auto-generated method stub
		if (isOngoing_update()) { return oldyvalues; }
		return yvalues;
	}

	/**
	 * Gets the s values.
	 *
	 * @param scope the scope
	 * @return the s values
	 */
	public ArrayList<Double> getSValues(final IScope scope) {
		// TODO Auto-generated method stub
		if (isOngoing_update()) { return oldsvalues; }
		return svalues;
	}

	/**
	 * Clear values.
	 *
	 * @param scope the scope
	 */
	/*
	 * public void addxysvalue(double dx, double dy, double dz, int date) { // TODO Auto-generated method stub
	 * xvalues.add(dx); yvalues.add(dy); svalues.add(dz); this.getDataset().serieToUpdateBefore.put(this.getName(),
	 * date);
	 *
	 * }
	 */
	public void clearValues(final IScope scope) {

		oldcvalues = cvalues;
		oldxvalues = xvalues;
		oldyvalues = yvalues;
		oldsvalues = svalues;

		cvalues = new ArrayList<>(); // for xy charts
		xvalues = new ArrayList<>(); // for xy charts
		yvalues = new ArrayList<>();
		svalues = new ArrayList<>(); // for marker sizes or 3d charts
		xerrvaluesmax = new ArrayList<>();
		yerrvaluesmax = new ArrayList<>();
		xerrvaluesmin = new ArrayList<>();
		yerrvaluesmin = new ArrayList<>();

	}

	/**
	 * Gets the listvalue.
	 *
	 * @param scope the scope
	 * @param barvalues the barvalues
	 * @param valuetype the valuetype
	 * @param listvalue the listvalue
	 * @return the listvalue
	 */
	private Object getlistvalue(final IScope scope, final HashMap barvalues, final String valuetype,
			final int listvalue) {
		// TODO Auto-generated method stub
		if (!barvalues.containsKey(valuetype)) { return null; }
		boolean uselist = true;
		if (listvalue < 0) {
			uselist = false;
		}
		final Object oexp = barvalues.get(valuetype);
		Object o = oexp;
		if (oexp instanceof IExpression) {
			o = ((IExpression) oexp).value(scope);
		}

		if (!uselist) { return o; }

		if (o instanceof IList) {
			final IList ol = Cast.asList(scope, o);
			if (ol.size() < listvalue) { return null; }
			return ol.get(listvalue);

		}
		return o;

	}

	/**
	 * Addxysvalue.
	 *
	 * @param scope the scope
	 * @param dx the dx
	 * @param dy the dy
	 * @param ds the ds
	 * @param date the date
	 * @param barvalues the barvalues
	 * @param listvalue the listvalue
	 */
	public void addxysvalue(final IScope scope, final double dx, final double dy, final double ds, final int date,
			final HashMap barvalues, final int listvalue) {

		svalues.add(ds);
		addxyvalue(scope, dx, dy, date, barvalues, listvalue);

	}

	/**
	 * Addxyvalue.
	 *
	 * @param scope the scope
	 * @param dx the dx
	 * @param dy the dy
	 * @param date the date
	 * @param barvalues the barvalues
	 * @param listvalue the listvalue
	 */
	public void addxyvalue(final IScope scope, final double dx, final double dy, final int date,
			final HashMap barvalues, final int listvalue) {
		// TODO Auto-generated method stub
		xvalues.add(dx);
		yvalues.add(dy);
		if (barvalues.containsKey(IKeyword.COLOR)) {
			final Object o = getlistvalue(scope, barvalues, IKeyword.COLOR, listvalue);
			if (o != null) {
				if (o instanceof IList) {
					final IList ol = Cast.asList(scope, o);
					if (ol.size() == 1) {
						this.setMycolor(Cast.asColor(scope, ol.get(0)));
					}
					if (ol.size() == 2) {
						this.setMycolor(Cast.asColor(scope, ol.get(1)));
						this.setMyMincolor(Cast.asColor(scope, ol.get(0)));
					}
					if (ol.size() > 2) {
						this.setMyMincolor(Cast.asColor(scope, ol.get(0)));
						this.setMyMedcolor(Cast.asColor(scope, ol.get(1)));
						this.setMycolor(Cast.asColor(scope, ol.get(2)));
					}
				} else {
					final GamaColor col = Cast.asColor(scope, o);
					this.setMycolor(col);

				}

			}

		}
		if (barvalues.containsKey(ChartDataStatement.MARKERSIZE)) {
			final Object o = getlistvalue(scope, barvalues, ChartDataStatement.MARKERSIZE, listvalue);
			if (o != null) {
				if (svalues.size() > xvalues.size()) {
					svalues.remove(svalues.get(svalues.size() - 1));
				}
				svalues.add(Cast.asFloat(scope, o));
			}

		}
		if (this.isUseYErrValues()) {
			final Object o = getlistvalue(scope, barvalues, ChartDataStatement.YERR_VALUES, listvalue);
			if (o != null) {
				if (o instanceof IList) {
					final IList ol = Cast.asList(scope, o);
					if (ol.size() > 1) {
						this.yerrvaluesmin.add(Cast.asFloat(scope, ol.get(0)));
						this.yerrvaluesmax.add(Cast.asFloat(scope, ol.get(1)));

					} else {
						this.yerrvaluesmin.add(dy - Cast.asFloat(scope, ol.get(0)));
						this.yerrvaluesmax.add(dy + Cast.asFloat(scope, ol.get(0)));
					}
				} else {
					this.yerrvaluesmin.add(dy - Cast.asFloat(scope, o));
					this.yerrvaluesmax.add(dy + Cast.asFloat(scope, o));

				}
			}

		}
		if (this.isUseXErrValues()) {
			final Object o = getlistvalue(scope, barvalues, ChartDataStatement.XERR_VALUES, listvalue);
			if (o != null) {
				if (o instanceof IList) {
					final IList ol = Cast.asList(scope, o);
					if (ol.size() > 1) {
						this.xerrvaluesmin.add(Cast.asFloat(scope, ol.get(0)));
						this.xerrvaluesmax.add(Cast.asFloat(scope, ol.get(1)));

					} else {
						this.xerrvaluesmin.add(dx - Cast.asFloat(scope, ol.get(0)));
						this.xerrvaluesmax.add(dx + Cast.asFloat(scope, ol.get(0)));
					}
				} else {
					this.xerrvaluesmin.add(dx - Cast.asFloat(scope, o));
					this.xerrvaluesmax.add(dx + Cast.asFloat(scope, o));

				}
			}

		}

		this.getDataset().serieToUpdateBefore.put(this.getName(), date);

	}

	/**
	 * Addcysvalue.
	 *
	 * @param scope the scope
	 * @param dx the dx
	 * @param dy the dy
	 * @param ds the ds
	 * @param date the date
	 * @param barvalues the barvalues
	 * @param listvalue the listvalue
	 */
	public void addcysvalue(final IScope scope, final String dx, final double dy, final double ds, final int date,
			final HashMap barvalues, final int listvalue) {

		svalues.add(ds);
		addcyvalue(scope, dx, dy, date, barvalues, listvalue);

	}

	/**
	 * Addcyvalue.
	 *
	 * @param scope the scope
	 * @param dx the dx
	 * @param dy the dy
	 * @param date the date
	 * @param barvalues the barvalues
	 * @param listvalue the listvalue
	 */
	public void addcyvalue(final IScope scope, final String dx, final double dy, final int date,
			final HashMap barvalues, final int listvalue) {
		cvalues.add(dx);
		yvalues.add(dy);
		if (barvalues.containsKey(IKeyword.COLOR)) {
			final Object o = getlistvalue(scope, barvalues, IKeyword.COLOR, listvalue);
			if (o != null) {
				if (o instanceof IList) {
					final IList ol = Cast.asList(scope, o);
					if (ol.size() == 1) {
						this.setMycolor(Cast.asColor(scope, ol.get(0)));
					}
					if (ol.size() == 2) {
						this.setMycolor(Cast.asColor(scope, ol.get(1)));
						this.setMyMincolor(Cast.asColor(scope, ol.get(0)));
					}
					if (ol.size() > 2) {
						this.setMyMincolor(Cast.asColor(scope, ol.get(0)));
						this.setMyMedcolor(Cast.asColor(scope, ol.get(1)));
						this.setMycolor(Cast.asColor(scope, ol.get(2)));
					}
				} else {
					final GamaColor col = Cast.asColor(scope, o);
					this.setMycolor(col);

				}
			}

		}
		if (barvalues.containsKey(ChartDataStatement.MARKERSIZE)) {
			final Object o = getlistvalue(scope, barvalues, ChartDataStatement.MARKERSIZE, listvalue);
			if (o != null) {
				if (svalues.size() > xvalues.size()) {
					svalues.remove(svalues.get(svalues.size() - 1));
				}
				svalues.add(Cast.asFloat(scope, o));
			}

		}
		if (this.isUseYErrValues()) {
			final Object o = getlistvalue(scope, barvalues, ChartDataStatement.YERR_VALUES, listvalue);
			if (o != null) {
				if (o instanceof IList) {
					final IList ol = Cast.asList(scope, o);
					if (ol.size() > 1) {
						this.yerrvaluesmin.add(Cast.asFloat(scope, ol.get(0)));
						this.yerrvaluesmax.add(Cast.asFloat(scope, ol.get(1)));

					} else {
						this.yerrvaluesmin.add(dy - Cast.asFloat(scope, ol.get(0)));
						this.yerrvaluesmax.add(dy + Cast.asFloat(scope, ol.get(0)));
					}
				} else {
					this.yerrvaluesmin.add(dy - Cast.asFloat(scope, o));
					this.yerrvaluesmax.add(dy + Cast.asFloat(scope, o));

				}
			}

		}

		this.getDataset().serieToUpdateBefore.put(this.getName(), date);

	}

	/**
	 * Endupdate.
	 *
	 * @param scope the scope
	 */
	public void endupdate(final IScope scope) {
		this.ongoing_update = false;
	}

	/**
	 * Startupdate.
	 *
	 * @param scope the scope
	 */
	public void startupdate(final IScope scope) {
		this.ongoing_update = true;
	}

	/**
	 * Savelistd.
	 *
	 * @param scope the scope
	 * @param history the history
	 * @param mylist the mylist
	 */
	private void savelistd(final IScope scope, final ChartHistory history, final ArrayList<Double> mylist) {
		if (mylist.size() == 0) {
			history.append(",");
			return;
		}
		for (int i = 0; i < mylist.size(); i++) {
			history.append(Cast.asFloat(scope, mylist.get(i)).floatValue() + ",");
		}

	}

	/**
	 * Savelists.
	 *
	 * @param scope the scope
	 * @param history the history
	 * @param mylist the mylist
	 */
	private void savelists(final IScope scope, final ChartHistory history, final ArrayList mylist) {
		if (mylist.size() == 0) { return; }
		for (int i = 0; i < mylist.size(); i++) {
			history.append(Cast.asString(scope, mylist.get(i)) + ",");
		}

	}

	/**
	 * Savehistory.
	 *
	 * @param scope the scope
	 * @param history the history
	 */
	public void savehistory(final IScope scope, final ChartHistory history) {
		history.append(this.getName() + ",");
		if (mysource.isByCategory()) {
			if (this.cvalues.size() > 0) {
				if (this.getMysource().isCumulative) {
					history.append(this.cvalues.get(this.cvalues.size() - 1) + ",");
				} else {
					savelists(scope, history, this.cvalues);
				}
			}
		} else {
			if (this.xvalues.size() > 0) {
				if (this.getMysource().isCumulative) {
					history.append(this.xvalues.get(this.xvalues.size() - 1) + ",");
				} else {
					savelistd(scope, history, this.xvalues);
				}

			}
		}
		if (this.yvalues.size() > 0) {
			if (this.getMysource().isCumulative) {
				history.append(this.yvalues.get(this.yvalues.size() - 1) + ",");
			} else {
				savelistd(scope, history, this.yvalues);
			}

		}
		if (this.svalues.size() > 0) {
			if (this.svalues.size() >= this.yvalues.size()) {
				if (this.getMysource().isCumulative) {
					history.append(this.svalues.get(this.svalues.size() - 1) + ",");
				} else {
					savelistd(scope, history, this.svalues);
				}

			}
		}
	}

	/**
	 * Gets the line thickness.
	 *
	 * @return the line thickness
	 */
	public double getLineThickness() {
		return getMysource().getLineThickness();
	}

}