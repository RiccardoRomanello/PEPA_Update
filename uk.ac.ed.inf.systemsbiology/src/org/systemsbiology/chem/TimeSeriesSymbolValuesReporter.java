package org.systemsbiology.chem;
/*
 * Copyright (C) 2003 by Institute for Systems Biology,
 * Seattle, Washington, USA.  All rights reserved.
 * 
 * This source code is distributed under the GNU Lesser 
 * General Public License, the text of which is available at:
 *   http://www.gnu.org/copyleft/lesser.html
 */

import java.io.PrintWriter;
import java.text.*;


import umontreal.iro.lecuyer.stat.TallyStore;

import org.systemsbiology.math.Value;

/**
 * Class for printing time-series data to a PrintWriter.
 */
public class TimeSeriesSymbolValuesReporter
{
    public static final void reportTimeSeriesSymbolValues(PrintWriter pPrintWriter, 
                                                          String []pRequestedSymbolNames, 
                                                          double []pTimeValues,
                                                          Object []pSymbolValues,
                                                          TimeSeriesOutputFormat pTimeSeriesOutputFormat) throws IllegalArgumentException
    {
        DecimalFormat nf = new DecimalFormat("0.######E0");
        DecimalFormatSymbols decimalFormatSymbols = nf.getDecimalFormatSymbols();
        pTimeSeriesOutputFormat.updateDecimalFormatSymbols(decimalFormatSymbols);
        nf.setDecimalFormatSymbols(decimalFormatSymbols);
        nf.setGroupingUsed(false);
        reportTimeSeriesSymbolValues(pPrintWriter,
                                     pRequestedSymbolNames,
                                     pTimeValues,
                                     pSymbolValues,
                                     nf,
                                     pTimeSeriesOutputFormat);
    }
    
    public static final void reportTimeSeriesSymbolValues(PrintWriter pPrintWriter, 
                                                          String []pRequestedSymbolNames, 
                                                          double []pTimeValues,
                                                          Object []pSymbolValues,
                                                          NumberFormat pNumberFormat,
                                                          TimeSeriesOutputFormat pTimeSeriesOutputFormat) throws IllegalArgumentException
    
    {
        int numSymbols = pRequestedSymbolNames.length;

        if(null == pTimeSeriesOutputFormat)
        {
            throw new IllegalArgumentException("required argument pTimeSeriesOutputFormat was passed as null");
        }

        if(null == pNumberFormat)
        {
            throw new IllegalArgumentException("required argument pNumberFormat was passed as null");
        }

        StringBuffer sb = new StringBuffer();
        sb.append(Character.toString(pTimeSeriesOutputFormat.getCommentChar()));
        sb.append(" time, ");
        for(int symCtr = 0; symCtr < numSymbols; ++symCtr)
        {
            sb.append(pRequestedSymbolNames[symCtr]);
            if(symCtr < numSymbols - 1)
            {
                sb.append(", ");
            }
        }
        sb.append("\n");
        int numTimePoints = pTimeValues.length;
        for(int ctr = 0; ctr < numTimePoints; ++ctr)
        {
            double []symbolValue = (double []) pSymbolValues[ctr];
            if(null == symbolValue)
            {
                break;
            }
            sb.append(pNumberFormat.format(pTimeValues[ctr]) + ", ");
            for(int symCtr = 0; symCtr < numSymbols; ++symCtr)
            {
                sb.append(pNumberFormat.format(symbolValue[symCtr]));
                if(symCtr < numSymbols - 1)
                {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        pPrintWriter.println(sb.toString());
    }
    
    // I need to seek the highest value reached in order to plot the candlestick pretty well
    public static final double reportTimeSeriesSymbolValues(PrintWriter pPrintWriter, 
            											  String []pRequestedSymbolNames, 
            											  double []pTimeValues,
            											  Object []pSymbolValues,
            											  NumberFormat pNumberFormat,
            											  TimeSeriesOutputFormat pTimeSeriesOutputFormat,
            											  double pConfidenceInterval) throws IllegalArgumentException

    {       
		int numSymbols = pRequestedSymbolNames.length;
		double notAlpha = 1 - pConfidenceInterval;
		
		double highestValue = 0;
		
		if(null == pTimeSeriesOutputFormat)
		{
			throw new IllegalArgumentException("required argument pTimeSeriesOutputFormat was passed as null");
		}
		
		if(null == pNumberFormat)
		{
			throw new IllegalArgumentException("required argument pNumberFormat was passed as null");
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toString(pTimeSeriesOutputFormat.getCommentChar()));
		sb.append(" time, ");
		for(int symCtr = 0; symCtr < numSymbols; ++symCtr)
		{
			sb.append(pRequestedSymbolNames[symCtr] + "(opn) ");
			sb.append(pRequestedSymbolNames[symCtr] + "(max) ");
			sb.append(pRequestedSymbolNames[symCtr] + "(min) ");
			sb.append(pRequestedSymbolNames[symCtr] + "(cls)");
			if(symCtr < numSymbols - 1)
			{
				sb.append(", ");
			}
		}
		sb.append("\n");
		int numTimePoints = pTimeValues.length;
	    TallyStore []timeSnapshot = null;
        double []values = new double[2];
        for(int i = 0; i < numTimePoints; ++i)
        {
        	timeSnapshot = (TallyStore []) pSymbolValues[i];
        	sb.append(pNumberFormat.format(pTimeValues[i]) + ", ");
            for(int j = 0; j < numSymbols; ++j)
            {
                timeSnapshot[j].confidenceIntervalStudent(notAlpha, values);
                sb.append(pNumberFormat.format(values[0]+values[1]) + " " 
                		+ pNumberFormat.format(timeSnapshot[j].max()) + " " 
                		+ pNumberFormat.format(timeSnapshot[j].min()) + " " 
                		+ pNumberFormat.format(values[0]-values[1]));
                if(timeSnapshot[j].max() > highestValue)
                {
                	highestValue = timeSnapshot[j].max();
                }
    			if(j < numSymbols - 1)
    			{
    				sb.append(", ");
    			}
            }
            sb.append("\n");
        }       
		pPrintWriter.println(sb.toString());
		
		return(highestValue);
	}
    
    public static final void reportProfileValues(PrintWriter pPrintWriter, 
            									 Object []pProfileValues,
            									 NumberFormat pNumberFormat,
            									 TimeSeriesOutputFormat pTimeSeriesOutputFormat) throws IllegalArgumentException
    {
    	int profileValuesNum = pProfileValues.length;
    	boolean error = false;
    	
        if(null == pTimeSeriesOutputFormat)
        {
            throw new IllegalArgumentException("required argument pTimeSeriesOutputFormat was passed as null");
        }

        if(null == pNumberFormat)
        {
            throw new IllegalArgumentException("required argument pNumberFormat was passed as null");
        }
        
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toString(pTimeSeriesOutputFormat.getCommentChar()));
		sb.append(" profile\n");
		
        for(int i = 0; i < profileValuesNum; ++i)
        {
        	sb.append(i + " " + ((ReactionCount)pProfileValues[i]).getCounter());
			if(i < profileValuesNum - 1)
			{
				sb.append("\n");
			}
			if(((ReactionCount)pProfileValues[i]).getCounter() == 0)
				error = true;
        }
               
        pPrintWriter.println(sb.toString());
        
        if(error)
        	System.err.println("\none or more reactions have never been fired");
    }
    
    public static final double reportTimeSeriesSymbolValues(PrintWriter pPrintWriter, 
            String []pRequestedSymbolNames,
            SimulationResults []simulationResultsArray,
            Value []midValue,
            String changeParameterName,
            NumberFormat pNumberFormat,
            TimeSeriesOutputFormat pTimeSeriesOutputFormat) throws IllegalArgumentException
    {
    	double highestValue = 0;
    	
    	int numSymbols = pRequestedSymbolNames.length;

    	if(null == pTimeSeriesOutputFormat)
    	{
    		throw new IllegalArgumentException("required argument pTimeSeriesOutputFormat was passed as null");
    	}

    	if(null == pNumberFormat)
    	{
    		throw new IllegalArgumentException("required argument pNumberFormat was passed as null");
    	}

    	StringBuffer sb = new StringBuffer();
    	sb.append(Character.toString(pTimeSeriesOutputFormat.getCommentChar()));
    	sb.append(" time, (" + changeParameterName + "), ");
    	for(int symCtr = 0; symCtr < numSymbols; ++symCtr)
    	{
    		sb.append(pRequestedSymbolNames[symCtr]);
    		if(symCtr < numSymbols - 1)
    		{
    			sb.append(", ");
    		}
    	}
    	sb.append("\n");
    	int numTimePoints = simulationResultsArray[0].getResultsTimeValues().length;
    	int numChangeParameter = simulationResultsArray.length;
    	for(int ctr = 0; ctr < numTimePoints; ++ctr)
    	{
    		for(int cp = 0; cp < numChangeParameter; cp++)
    		{
    			double []symbolValue = (double []) simulationResultsArray[cp].getResultsSymbolValues()[ctr];
    			if(null == symbolValue)
    			{
    				break;
    			}
    			sb.append(pNumberFormat.format(simulationResultsArray[cp].getResultsTimeValues()[ctr]) + ", ");
    			sb.append(pNumberFormat.format(midValue[cp].getValue()) + ", ");
    			for(int symCtr = 0; symCtr < numSymbols; ++symCtr)
    			{
                    if(symbolValue[symCtr] > highestValue)
                    {
                    	highestValue = symbolValue[symCtr];
                    }
    				sb.append(pNumberFormat.format(symbolValue[symCtr]));
    				if(symCtr < numSymbols - 1)
    				{
    					sb.append(", ");
    				}
    			}
    			sb.append("\n");
    		}
    		sb.append("\n");
    	}
    	
    	pPrintWriter.println(sb.toString());
    	
    	return(highestValue);
    }
}