/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;

import uk.ac.ed.inf.common.ui.plotting.*;
import uk.ac.ed.inf.common.ui.plotting.data.InfoWithAxes;
import uk.ac.ed.inf.common.ui.plotting.data.Series;
import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Parameter;
import uk.ac.ed.inf.pepa.OptionsMap.Solver;
import uk.ac.ed.inf.pepa.eclipse.core.*;
import uk.ac.ed.inf.pepa.eclipse.ui.Activator;
import uk.ac.ed.inf.pepa.sba.Results;
import uk.ac.ed.inf.pepa.sba.SBASimulatorException;

/**
 * 
 * @author ajduguid
 * 
 */
public class AnalysisJob extends Job {
	
	private static String term = System.getProperty("line.separator");

	// List<OutputOptionsWizardPage.Graph> graphs;

	IPepaModel model;

	OptionsMap optionsMap;

	// String resultsFilename;

	AnalysisJob(String title, IPepaModel model, OptionsMap optionsMap) {// ,List<OutputOptionsWizardPage.Graph> graphs, String resultsFilename) {
		super(title);
		this.model = model;
		this.optionsMap = optionsMap;
		// this.graphs = graphs;
		// this.resultsFilename = resultsFilename;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			// run model (for now)
			model.generateTimeSeries(optionsMap, monitor);
			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
			Results results = model.getTimeSeries();
			String[] names = results.getSpeciesOrdering();
			ArrayList<String> namesArrayList = new ArrayList<String>();
			for (String s : names)
				namesArrayList.add(s);
			double[][] values = results.getSimpleTimeSeries();

			/* Mirco - NEW API */
			newApi(names, values);

		} catch (SBASimulatorException e) {
			PepaLog.logError(e);
			return new Status(IStatus.ERROR, Activator.ID, IStatus.OK, e
					.getMessage(), e);
		}
		return Status.OK_STATUS;
	}

	private void newApi(String[] labels, double[][] values) {
		double[][] results = new double[values[0].length][values.length];
		for (int timePoint = 0; timePoint < values.length; timePoint++) {
			for (int component = 0; component < values[0].length; component++) {
				results[component][timePoint] = values[timePoint][component];
			}
		}
		InfoWithAxes info = new InfoWithAxes();
		info.setXSeries(Series.create(results[0], "Time"));
		for (int i = 0; i < labels.length; i++) {
			info.getYSeries().add(Series.create(results[i + 1], labels[i]));
		}
		info.setShowLegend(true);
		info.setYLabel("Population");
		info.setGraphTitle("Time series analysis");
		info.setHas3DEffect(false);
		info.setShowMarkers(false);
		IChart chart = Plotting.getPlottingTools().createTimeSeriesChart(info);
		chart.setSemanticElement(new ResultsAdapter());
		uk.ac.ed.inf.common.ui.plotview.PlotViewPlugin.getDefault().reveal(
				chart);
	}
	
	private class ResultsAdapter implements ISemanticElement {
		
		String semanticElement;

		ResultsAdapter() {
			StringBuilder sb = new StringBuilder();
			Solver solver = (Solver) optionsMap.getValue(Parameter.Solver);
			sb.append("Simulator: ").append(solver.getDescriptiveName()).append(term);
			Parameter[] parameters = solver.getRequiredParameters();
			for (Parameter parameter : parameters)
				if (!parameter.equals(Parameter.Components))
					sb.append(parameter.toString()).append(": ").append(optionsMap.serialise(parameter)).append(term);
			semanticElement = sb.toString();
		}

		@SuppressWarnings("unchecked")
		public Object getAdapter(Class adapter) {
			return null;
		}

		public String getDescription(String format) {
			StringBuilder sb = new StringBuilder();
			if(ISemanticElement.CSV_FORMAT.equals(format)) {
				Solver solver = (Solver) optionsMap.getValue(Parameter.Solver);
				sb.append("# Simulator: ").append(solver.getDescriptiveName()).append(term);
				Parameter[] parameters = solver.getRequiredParameters();
				for (Parameter parameter : parameters)
					if (!parameter.equals(Parameter.Components))
						sb.append("# ").append(parameter.toString()).append(": ").append(optionsMap.serialise(parameter)).append(term);
			}
			return sb.toString();
		}
	}
}
