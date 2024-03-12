package uk.ac.ed.inf.pepa.cpt.config.control;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.config.lists.IOptionList;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;

public abstract class PerformanceControl implements Control {
	
	protected IOptionList myOptions;
	
	public PerformanceControl (IOptionList options){
		this.myOptions = options;
	}
	
	public abstract boolean setSelected(short processId, boolean selected);
	
	public abstract boolean setSelected(String name, boolean selected);
	
	public abstract ArrayList<HashMap<String,Short>> getOptions();
	
	public abstract IStatisticsCollector[] getCollectors(IPointEstimator[] estimators);
	
	public abstract IPointEstimator[] getEstimators();
	
	public abstract String[] getLabels();

}
