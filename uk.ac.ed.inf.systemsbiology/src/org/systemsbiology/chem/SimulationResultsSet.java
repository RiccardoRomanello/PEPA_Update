/** BMR **/

/* I'm sorry to have changed the interface, but it was the only way
 * to achieve my target!
 * Luca Cacchiani 02/02/07
 */

package org.systemsbiology.chem;

import java.util.Vector;
import java.util.LinkedList;

public abstract interface SimulationResultsSet 

{
	/**
     * Returns an array containing the names of the symbols
     * for which the user requested to view the time-series
     * data results for the simulation.
     */
    public String[] getResultsSymbolNames();
    
    
    /**
     * Returns an array containing the time values of the
     * time points at which the symbols (requested by the
     * user) were evaluated.
     */
    public double[] getResultsTimeValues();
    
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
     */
    public Object[] getResultsSymbolValues();
    
    public String toString();
    
    /**
     *  At the moment the cut off point is determined when the increase or decrease in values is below 
     *  * @return The point on the x (time) axis at which the graph is no longer interesting (at the moment y = 0)
     */
    public int getTruncatePoint();
    
    public double getMaxY();
    
    /**
     * Method to retrieve the sets of value names, each set corresponding to an original graph
     * ie a new graph should return one array, the same as getResultsSymbolNames, a graph created
     * from combining three others would return three arrays
     * @return Vector of String arrays of value names
     */
    public Vector getSymbolNameSets();
    
    /**
     * A two-dimensional array of Tally/TallyStore containing the
     * actual Tally/TallyStore values of the symbols requested by the user.
     * The first index identifies the time point, and returns
     * an array of Tally/TallyStore.  That array of Tally/TallyStore is of the
     * same length as the number of symbols requested by the
     * user; it contains the values of the corresponding
     * symbols at the time point identified by the first array
     * index; schematically, access would look like this:
     * <code>
     * value = ((Tally/TallyStore [])resultsSymbolValues[timeIndex])[symbolIndex]
     * </code>
     * 
     * it returns 'null' if we don't collect stats
     * 
     */
    public Object []getStatCollector();
    
    /**
     * Method to retrive the simulator parameters, if they exist (null otherwise)
     * @return Simulator Parameters of this run
     */
    public SimulatorParameters getSimulatorParameters();
    
    /**
     * Method to retrive the number of times that a reaction fires
     */
    public Object []getReactionFireCounter();
    
    /**
     * Method to retrive deadlocks and when they occured
     */
    public LinkedList getDeadlockList();
}
