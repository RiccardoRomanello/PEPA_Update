package org.systemsbiology.chem;
/*
 * Copyright (C) 2003 by Institute for Systems Biology,
 * Seattle, Washington, USA.  All rights reserved.
 * 
 * This source code is distributed under the GNU Lesser 
 * General Public License, the text of which is available at:
 *   http://www.gnu.org/copyleft/lesser.html
 */

import java.util.*;
import java.text.*;

/**
 * Data structure that contains the results of a simulation.
 * This includes the time range for the simulation, the name
 * of the simulator used, the name of the chemical species
 * to be viewed, etc.
 */
public final class SimulationResults //implements SimulationResultsSet
{
    private String mSimulatorAlias;
    private double mStartTime;
    private double mEndTime;
    private SimulatorParameters mSimulatorParameters;
    private String []mResultsSymbolNames;
    private double []mResultsTimeValues;
    private Object []mResultsSymbolValues;
    private double []mResultsFinalSymbolFluctuations;
    private Date mResultsDateTime;
    private String mModelName;
    // Next 4 objects were added by Luca Cacchiani
    private Object []mStatCollector;
    //private Object []mReactionFireCounter;
    private LinkedList mDeadlockList;
    // Once the maximum Y value has been worked out, stores it
    private double maxY = -1;
    // Next x added by AJD
    private String[] reactionNames;
    private double[] reactionCounts;
    private double[] reactionTimes;

    public SimulationResults()
    {
        mResultsDateTime = new Date(System.currentTimeMillis());
        mSimulatorAlias = null;
        mSimulatorParameters = null;
        mResultsSymbolNames = null;
        mResultsTimeValues = null;
        mResultsSymbolValues = null;
        mResultsFinalSymbolFluctuations = null;
        mModelName = null;
        // Added by Luca Cacchiani
        mDeadlockList = null;
    }

    public void setModelName(String pModelName)
    {
        mModelName = pModelName;
    }

    public String getModelName()
    {
        return(mModelName);
    }

    public double []getResultsFinalSymbolFluctuations()
    {
        return(mResultsFinalSymbolFluctuations);
    }

    public void setResultsFinalSymbolFluctuations(double []pResultsFinalSymbolFluctuations)
    {
        mResultsFinalSymbolFluctuations = pResultsFinalSymbolFluctuations;
    }

    public String getSimulatorAlias()
    {
        return(mSimulatorAlias);
    }    

    public double getStartTime()
    {
        return(mStartTime);
    }

    public double getEndTime()
    {
        return(mEndTime);
    }

    public SimulatorParameters getSimulatorParameters()
    {
        return(mSimulatorParameters);
    }

    /**
     * Returns an array containing the names of the symbols
     * for which the user requested to view the time-series
     * data results for the simulation.
     */
    public String []getResultsSymbolNames()
    {
        return(mResultsSymbolNames);
    }

    /**
     * Returns an array containing the time values of the
     * time points at which the symbols (requested by the
     * user) were evaluated.
     */
    public double []getResultsTimeValues()
    {
        return(mResultsTimeValues);
    }

    /**
     * A two-dimensional array of doubles containing the
     * actual values of the symbols requested by the user.
     * The first index identifies the time point, and returns
     * an array of doubles.  That array of doubles is of the
     * same length as the number of symbols requested by the
     * user; it contains the values of the corresponding
     * symbols at the time point identified by the first array
     * index; schematically, access would look like this:
     * <code>
     * value = ((double [])resultsSymbolValues[timeIndex])[symbolIndex]
     * </code>
     * 
     * ATTENTION - To Comment
     * 
     */
    public Object []getResultsSymbolValues()
    {
        return(mResultsSymbolValues);
    }

    public void setSimulatorAlias(String pSimulatorAlias)
    {
        mSimulatorAlias = pSimulatorAlias;
    }

    public void setStartTime(double pStartTime)
    {
        mStartTime = pStartTime;
    }

    public void setEndTime(double pEndTime)
    {
        mEndTime = pEndTime;
    }
    
    public void setSimulatorParameters(SimulatorParameters pSimulatorParameters)
    {
        mSimulatorParameters = pSimulatorParameters;
    }

    public void setResultsSymbolNames(String []pResultsSymbolNames)
    {
        mResultsSymbolNames = pResultsSymbolNames;
    }

    public void setResultsTimeValues(double []pResultsTimeValues)
    {
        mResultsTimeValues = pResultsTimeValues;
    }

    public void setResultsSymbolValues(Object []pResultsSymbolValues)
    {
        mResultsSymbolValues = pResultsSymbolValues;
    }

    public Date getResultsDateTime()
    {
        return(mResultsDateTime);
    }

    private static final int LENGTH_ABBREV_MODEL_NAME = 10;

    private static String generateSimulationResultsLabel(String pModelName,
                                                         String pSimulatorAlias,
                                                         Date pResultsDateTime)
    {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT,
                                                   Locale.FRANCE);
        String timeString = df.format(pResultsDateTime);
        df = DateFormat.getDateInstance(DateFormat.SHORT);
        String dateString = df.format(pResultsDateTime);
        String abbrevModelName = pModelName;
        if(pModelName.length() > LENGTH_ABBREV_MODEL_NAME)
        {
            abbrevModelName = abbrevModelName.substring(0,LENGTH_ABBREV_MODEL_NAME - 1);
        }
        StringBuffer retStr = new StringBuffer();
        retStr.append("[" + dateString + " " + timeString + "] ");
        retStr.append("[" + abbrevModelName + "] ");
        retStr.append("[" + pSimulatorAlias + "]");
        return(retStr.toString());
    }

    public String createLabel()
    {
        return(generateSimulationResultsLabel(mModelName,
                                              mSimulatorAlias,
                                              mResultsDateTime));
    }
    
    //  All further code added by Luca Cacchiani
    public String toString() { return createLabel(); }
    
    public int getTruncatePoint()
    {
    	int truncatePoint = 0;
    	for(int j = 0; j < mResultsTimeValues.length - 1; j++)
		{
    		double[] values = (double[]) mResultsSymbolValues[j];
    		double[] nextValues = (double[]) mResultsSymbolValues[j+1];
    		for(int i = 0; i < values.length; i++)
    		{
    			double dif = Math.abs( (nextValues[i] - values[i])/getMaxY() );
    			if(dif > 0.0003)
    			{
    				truncatePoint = j+1;
    				break;
    			}
    		}
    	}
    	return truncatePoint;
    }
    
    public double getMaxY()
    {
    	if(maxY == -1)
    	{
    		for(int j = 0; j < mResultsTimeValues.length; j++)
    		{
        		double[] values = (double[]) mResultsSymbolValues[j];
        		for(int i = 0; i < values.length; i++)
        		{
        			if(values[i] > maxY) maxY = values[i];
        		}
        	}
        }
    	
    	return maxY;
    }
    
    // Note this reflects the correct behaviour as specified in the interface as this
    // implemenation does not support merged graphs anyway
    public Vector getSymbolNameSets()
    {
    	Vector toReturn = new Vector();
    	toReturn.add(mResultsSymbolNames);
    	return toReturn;
    }
    
    public void setStatCollector(Object []pStatCollector) 
    {
    	mStatCollector = pStatCollector;
    }
    
    public Object []getStatCollector()
    {
        return(mStatCollector);
    }
    
    public String[] getReactionNames() {
    	return reactionNames;
    }
    
    public double[] getReactionCounts() {
    	return reactionCounts;
    }
    
    public double[] getReactionTimes() {
    	return reactionTimes;
    }
    
    public void setReactionNames(String[] pReactionNames) {
    	reactionNames = pReactionNames;
    }
    
    public void setReactionCounts(double[] pReactionCounts) {
    	reactionCounts = pReactionCounts;
    }
    
    public void setReactionTimes(double[] pReactionTimes) {
    	reactionTimes = pReactionTimes;
    }
    
    /*
    public void setReactionFireCounter(Object []pReactionFireCounter) 
    {
    	mReactionFireCounter = pReactionFireCounter;
    }
    
    public Object []getReactionFireCounter()
    {
        return(mReactionFireCounter);
    }*/
    
    /**
     * @param pDeadlockList List of Double Objects
     */
    public void setDeadlockList(LinkedList pDeadlockList)
    {
    	mDeadlockList = pDeadlockList;
    }
    
    /**
     * 
     * @return List of Double Objects
     */
    public LinkedList getDeadlockList()
    {
    	return mDeadlockList;
    }
}

