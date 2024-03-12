/** BMR **/

/** TO DO
 * 
 *  currently the data structure re the 3 arrays is a little
 *  inadequate for representing combined graphs,
 *  for example if graphs need to be 'uncombined'
 *  	- so maybe change SimulationResults?
 * 
 * 
 * 
 * */

package org.systemsbiology.chem;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.systemsbiology.chem.tp.GraphMergeException;

public class SimulationResultsSetImp implements SimulationResultsSet
{
	// Holds the names that go with the colour key
	//private String[] mResultsSymbolNames;
    private Vector mResultsSymbolNames;
	private double[] mResultsTimeValues;
    private Object[] mResultsSymbolValues;
    double maxY = -1;
	
	/* Constructor that takes two result sets and merges them */
	public SimulationResultsSetImp (SimulationResultsSet setOne,  SimulationResultsSet setTwo)
	{
		
		
	}
	
	/* Constructor that creates an empty set */
	public SimulationResultsSetImp()
	{
		mResultsSymbolNames = new Vector();
	    mResultsTimeValues = new double[0];
	    mResultsSymbolValues = new Object[0];
	}
	
	/* Creates a set based on an existing resultsset */
	public SimulationResultsSetImp(SimulationResultsSet set)
	{
		this.mResultsSymbolNames = set.getSymbolNameSets();
		this.mResultsTimeValues = set.getResultsTimeValues();
		this.mResultsSymbolValues = set.getResultsSymbolValues();
	}
    
	public String []getResultsSymbolNames()
	{
		int count = 0;
		for(Iterator i = mResultsSymbolNames.iterator(); i.hasNext();)
		{
		    String[] temp = (String[]) i.next();
		    count += temp.length;
		}
		String toReturn[] = new String[count];
		count = 0;
		for(Iterator i = mResultsSymbolNames.iterator(); i.hasNext();)
		{
		    String[] temp = (String[]) i.next();
			for(int j = 0; j < temp.length; j++)
			{
				toReturn[count] = temp[j];
				count++;
			}
		}
		return toReturn;
	}
    
    public double []getResultsTimeValues()
    {
    	return mResultsTimeValues;
    }
    
    public Object []getResultsSymbolValues()
    {
    	return mResultsSymbolValues;
    }
    
    // Merges the data in this with set, returns false if the merge was unsuccessful
    private void merge(SimulationResultsSet set) throws GraphMergeException
    {
    	// If the data arrays are currently empty:
    	if(mResultsSymbolNames.size() == 0)
    	{
    		this.mResultsSymbolNames = set.getSymbolNameSets();
    		this.mResultsTimeValues = set.getResultsTimeValues();
    		this.mResultsSymbolValues = set.getResultsSymbolValues();
    		return;
    	}
    	
    	// Check the graphs have the same number of result points
    	if (set.getResultsTimeValues().length != this.getResultsTimeValues().length)
    		throw new GraphMergeException("Cannot merge graphs with different numbers of sample points");
    	
    	// Check the results points start and end at the same time
    	if (set.getResultsTimeValues()[0] != this.getResultsTimeValues()[0] ||
    			set.getResultsTimeValues()[set.getResultsTimeValues().length-1] 
    	                               != this.getResultsTimeValues()[set.getResultsTimeValues().length-1])
    		throw new GraphMergeException("Cannot merge graphs with data plotted over different time periods");
    	
    	// set is added after the current data
    	//String[] setSymbolNames = set.getResultsSymbolNames();
    	double[] setTimeValues = set.getResultsTimeValues();
    	Object[] setSymbolValues = set.getResultsSymbolValues();
    	
    	//String[] tempSymbolNames = new String [ mResultsSymbolNames.length + setSymbolNames.length ];
    	mResultsSymbolNames.addAll(set.getSymbolNameSets());
    	
    	// Note the same time scales are assumed
    	Object[] tempSymbolValues = new Object [ setSymbolValues.length ];
    	
    	// Merges the names. Some form of naming convention to indicate the source of data would be good to add
    	/*for(int i = 0; i < mResultsSymbolNames.length; i++)
    	{
    		tempSymbolNames[i] = mResultsSymbolNames[i];
    	}
    	for(int i = 0; i < setSymbolNames.length; i++)
    	{
    		tempSymbolNames[i + mResultsSymbolNames.length ] = setSymbolNames[i];
    	}*/
    
    	for(int i = 0; i < tempSymbolValues.length; i++)
    	{
    		double[] currentValues = (double[])mResultsSymbolValues[i];
    		double[] setValues = (double[])setSymbolValues[i];
    		double[] newValues = new double[currentValues.length + setValues.length];
    		
    		for(int j = 0; j < currentValues.length; j++)
    		{
    			newValues[j] = currentValues[j];
    		}
    		
    		for(int j = 0; j < setValues.length; j++)
    		{
    			// TEST mirroring goes here
    			newValues[j+currentValues.length] = setValues[j];
    		}
    		
    		tempSymbolValues[i] = newValues;
    	}
		
		//mResultsSymbolNames = tempSymbolNames;
        mResultsSymbolValues = tempSymbolValues;
        
    }
    
    public void addResultsSet(SimulationResultsSet set) throws GraphMergeException
    {
    	merge(set);
    }
    
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
    
    public Vector getSymbolNameSets()
    {
    	return mResultsSymbolNames;
    }

    // interface implementation methods
	public SimulatorParameters getSimulatorParameters() {
		return null;
	}

	//	 interface implementation methods
	public Object[] getStatCollector() {
		return null;
	}
	
	//	 interface implementation methods
    public Object[] getReactionFireCounter() {
        return null;
    }

    //	 interface implementation methods
	public LinkedList getDeadlockList() {
		return null;
	}
}
