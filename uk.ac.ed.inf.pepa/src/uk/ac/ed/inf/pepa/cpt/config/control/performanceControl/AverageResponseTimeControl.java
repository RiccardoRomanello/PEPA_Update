package uk.ac.ed.inf.pepa.cpt.config.control.performanceControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uk.ac.ed.inf.pepa.cpt.config.control.PerformanceControl;
import uk.ac.ed.inf.pepa.cpt.config.lists.IOptionList;
import uk.ac.ed.inf.pepa.cpt.config.lists.ProcessList;
import uk.ac.ed.inf.pepa.largescale.AverageResponseTimeCalculation;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.AverageResponseTimeCollector;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;


/**
 * This is the control for the average response time set up.
 * @author twig
 *
 */
public class AverageResponseTimeControl extends PerformanceControl {
	
	IParametricDerivationGraph graph;
	
	public AverageResponseTimeControl(IOptionList options, IParametricDerivationGraph graph) {
		super(options);
		this.graph = graph;
	}

	@Override
	public boolean validate() {
		
		Integer[] processIds = ((ProcessList) this.myOptions).getSelectedProcessIds();
		Set<String> setOfRoots= new HashSet<String>();
		
		if(processIds.length > 0){
			for(int i = 0; i < processIds.length;i++){
				setOfRoots.add(((ProcessList) this.myOptions).getRootComponent(processIds[i].shortValue()));
			}
		}
		
		return (setOfRoots.size() == 1);
	}

	@Override
	public IStatisticsCollector[] getCollectors(IPointEstimator[] estimators) {
		return new IStatisticsCollector[] { new AverageResponseTimeCollector(0, 1) };
	}

	@Override
	public IPointEstimator[] getEstimators() {
		
		Integer[] processIds = ((ProcessList) this.myOptions).getSelectedProcessIds();
		Set<String> root = new HashSet<String>();
		
		for(int i = 0; i < processIds.length;i++){
			root.add(((ProcessList) this.myOptions).getRootComponent(processIds[i].shortValue()));
		}
		
		if(root.size() == 1){
		
			String label = root.toArray(new String[1])[0];
			
			int componentIndex = ((ProcessList) this.myOptions).getComponentIndex(label);
			int[] inSystem = ((ProcessList) this.myOptions).getSelectedCoordinates(label);
			
			AverageResponseTimeCalculation art = new AverageResponseTimeCalculation(
					componentIndex, inSystem, graph);
			
			//and the estimators are born
			IPointEstimator[] estimators = new IPointEstimator[] { art.getUsersInSystemEstimator(),
					art.getIncomingThroughputEstimator() };
			return estimators;
		} else {
			IPointEstimator[] estimators = null;
			return estimators;
		}
		
	}
	
	@Override
	public ArrayList<HashMap<String, Short>> getOptions() {
		return ((ProcessList) this.myOptions).getProcessesUnderSequentialComponents();
	}

	@Override
	public boolean setSelected(short processId, boolean selected) {
		return ((ProcessList) this.myOptions).setSelectedART(processId, selected);
	}

	@Override
	public String[] getLabels() {
		return new String[] { "Average response time" };
	}

	@Override
	public boolean setSelected(String name, boolean selected) {
		return ((ProcessList) this.myOptions).setSelectedART(name, selected);
	}

	@Override
	public String[] getKeys() {
		
		ArrayList<HashMap<String, Short>> processIds = 
			((ProcessList) this.myOptions).getProcessIDOfSequentialComponents();
		
		ArrayList<String> output = new ArrayList<String>();
		
		for(HashMap<String,Short> h : processIds){
			for(String s : h.keySet()){
				output.add(s);
			}
		}
		
		return output.toArray(new String[output.size()]);
	}
	
	@Override
	public String[] getKeys(String s) {
		ArrayList<HashMap<String, Short>> options = getOptions();
		ArrayList<String> output = new ArrayList<String>();
		
		for(HashMap<String,Short> h : options){
			for(String s1 : h.keySet()){
				Short test = h.get(s1);
				if(((ProcessList) this.myOptions).getRootComponent(test).equals(s)){
					output.add(s1);
				}
			}
		}
		
		return output.toArray(new String[output.size()]);
	}

	@Override
	public String getType(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValue(String key) {
		if(((ProcessList) this.myOptions).getSelectionState(key)){
			return "True";
		} else {
			return "False";
		}
	}

	@Override
	public boolean setValue(String key, String value) {
		if(value.equals("True")){
			return this.setSelected(key, true);
		} else {
			return this.setSelected(key, false);
		}
		
	}

	@Override
	public boolean setValue(String component, String key, String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getValue(String component, String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toPrint() {
		return "Selected;" + this.myOptions.toPrint();
	}



}
