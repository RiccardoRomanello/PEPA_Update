/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.sba;

import org.systemsbiology.chem.SimulationResults;

/**
 * 
 * @author ajduguid
 * 
 */
public class Results {

	private SimulationResults simulationResults;

	Results(SimulationResults results) {
		simulationResults = results;
	}

	public synchronized double[] getReactionCounts() {
		return simulationResults.getReactionCounts();
	}

	public synchronized String[] getReactionOrdering() {
		return simulationResults.getReactionNames();
	}

	public synchronized double[] getReactionTimes() {
		return simulationResults.getReactionTimes();
	}

	/*
	public synchronized TimePoint[] getResults(double confidenceInterval) {
		if (confidenceInterval >= 1.0 || confidenceInterval <= 0.0)
			throw new IllegalArgumentException(
					"Confidence interval must lie in (0.0 1.0)");
		double[] times = simulationResults.getResultsTimeValues();
		Object[] recordings = simulationResults.getStatCollector();
		TimePoint[] results = new TimePoint[times.length];
		TimePoint result;
		TallyStore[] tally;
		DataPoint[] dataPoints;
		DataPoint dataPoint;
		double[] ciValues = new double[2];
		for (int i = 0; i < results.length; i++) {
			result = new TimePoint();
			result.time = times[i];
			tally = (TallyStore[]) recordings[i];
			dataPoints = new DataPoint[tally.length];
			for (int j = 0; j < tally.length; j++) {
				dataPoint = new DataPoint();
				tally[j]
						.confidenceIntervalStudent(confidenceInterval, ciValues);
				dataPoint.average = ciValues[0];
				dataPoint.confidence = ciValues[1];
				dataPoint.max = tally[j].max();
				dataPoint.min = tally[j].min();
				dataPoints[j] = dataPoint;
			}
			result.species = dataPoints;
			results[i] = result;
		}
		return results;
	}
	*/

	public synchronized double[][] getSimpleTimeSeries() {
		double[] times = simulationResults.getResultsTimeValues();
		Object[] values = simulationResults.getResultsSymbolValues();
		double[] value;
		double[][] results = new double[times.length][simulationResults
				.getResultsSymbolNames().length + 1];
		for (int i = 0; i < times.length; i++)
			results[i][0] = times[i];
		for (int i = 0; i < values.length; i++) {
			value = (double[]) values[i];
			for (int i2 = 0; i2 < value.length; i2++)
				results[i][i2 + 1] = value[i2];
		}
		return results;
	}

	public synchronized String[] getSpeciesOrdering() {
		return simulationResults.getResultsSymbolNames();
	}

	public String returnSimpleResults() {
		StringBuilder results = new StringBuilder();
		String[] names = simulationResults.getResultsSymbolNames();
		results.append("# time");
		for (String name : names)
			results.append(", ").append(name);
		results.append("\n");
		// Woot! have to deal with pre 5.0 code
		double[] times = simulationResults.getResultsTimeValues();
		Object[] values = simulationResults.getResultsSymbolValues();
		double[] value;
		for (int i = 0; i < values.length; i++) {
			results.append(times[i]);
			value = (double[]) values[i];
			for (double d : value)
				results.append(", ").append(d);
			results.append("\n");
		}
		return results.toString();
	}
}
