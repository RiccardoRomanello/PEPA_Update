/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import uk.ac.ed.inf.common.ui.plotting.data.InfoWithAxes;
import uk.ac.ed.inf.common.ui.plotting.data.InfoWithoutAxes;
import uk.ac.ed.inf.common.ui.plotting.data.Series;
import uk.ac.ed.inf.pepa.ctmc.LocalState;
import uk.ac.ed.inf.pepa.ctmc.PopulationLevelResult;
import uk.ac.ed.inf.pepa.ctmc.SequentialComponent;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;

/**
 * An utility class which offers UI-related services
 * @author mtribast
 *
 */
public class Utilities {
	
	public static InfoWithoutAxes createGraphData(SequentialComponent utilisation) {
		InfoWithoutAxes info = new InfoWithoutAxes();
		info.setGraphTitle(utilisation.getName());
		info.setHas3DEffect(true);
		LocalState[] states = utilisation.getLocalStates();
		String[] categories = new String[states.length];
		double[] values = new double[states.length];
		for (int i = 0; i < states.length; i++) {
			categories[i] = states[i].getName();
			values[i] = states[i].getUtilisation();
		}
		info.setCategories(categories);
		info.setValues(values);
		info.setShowLegend(true);
		return info;
	}
	
	public static InfoWithAxes createGraphData(ThroughputResult[] throughput) {
		InfoWithAxes info = new InfoWithAxes();
		info.setGraphTitle("Throughput");
		info.setHas3DEffect(true);
		String[] categories = new String[throughput.length];
		double[] values = new double[throughput.length];
		for (int i =0; i < throughput.length;i++) {
			categories[i]= throughput[i].getActionType();
			values[i] = throughput[i].getThroughput();
		}
		info.setCategories(categories);
		info.setXSeries(Series.create(new  double[0], "Action type"));
		info.getYSeries().add(Series.create(values, ""));
		return info;
	}

	public static InfoWithAxes createGraphData(PopulationLevelResult[] population) {
		InfoWithAxes info = new InfoWithAxes();
		info.setGraphTitle("Population");
		info.setHas3DEffect(true);
		String[] categories = new String[population.length];
		double[] values = new double[population.length];
		for (int i =0; i < population.length;i++) {
			categories[i]= population[i].getName();
			values[i] = population[i].getMean();
		}
		info.setCategories(categories);
		info.setXSeries(Series.create(new  double[0], "Population"));
		info.getYSeries().add(Series.create(values, ""));
		return info;
	}

}
