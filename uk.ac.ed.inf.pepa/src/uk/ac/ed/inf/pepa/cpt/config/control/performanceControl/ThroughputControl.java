package uk.ac.ed.inf.pepa.cpt.config.control.performanceControl;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.config.control.PerformanceControl;
import uk.ac.ed.inf.pepa.cpt.config.lists.ActionList;
import uk.ac.ed.inf.pepa.cpt.config.lists.IOptionList;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.ThroughputCalculation;
import uk.ac.ed.inf.pepa.largescale.simulation.DefaultCollector;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;

public class ThroughputControl extends PerformanceControl {

	IParametricDerivationGraph graph;
	
	public ThroughputControl(IOptionList options, IParametricDerivationGraph graph) {
		super(options);
		this.graph = graph;
	}

	@Override
	public boolean validate() {
		return  ((ActionList) this.myOptions).getSelectedActionIds().length > 0;
	}

	@Override
	public IStatisticsCollector[] getCollectors(IPointEstimator[] estimators) {
		return DefaultCollector.create(estimators);
	}

	@Override
	public IPointEstimator[] getEstimators() {
		Short[] actionIds = ((ActionList) this.myOptions).getSelectedActionIds();
		IPointEstimator[] estimators = new ThroughputCalculation[actionIds.length];
		for (int i = 0; i < actionIds.length; i++) {
			estimators[i] = new ThroughputCalculation(
					actionIds[i], graph);
		}
		return estimators;
	}

	@Override
	public ArrayList<HashMap<String, Short>> getOptions() {
		return ((ActionList) this.myOptions).getAllActionIds();
	}

	@Override
	public boolean setSelected(short actionId, boolean selected) {
		return ((ActionList) this.myOptions).setSelectedHandler(actionId, selected);
	}

	@Override
	public String[] getLabels() {
		
		Short[] actionIds = ((ActionList) this.myOptions).getSelectedActionIds();
		String[] collector = new String[actionIds.length];
		
		for(int i = 0; i < actionIds.length; i++){
			collector[i] = ((ActionList) this.myOptions).getLabel(actionIds[i]);
		}
		
		return collector;
	}

	@Override
	public boolean setSelected(String name, boolean selected) {
		return ((ActionList) this.myOptions).setSelectedHandler(name, selected);
	}

	@Override
	public String[] getKeys() {
		
		ArrayList<HashMap<String, Short>> actionIds = ((ActionList) this.myOptions).getAllActionIds();
		
		ArrayList<String> output = new ArrayList<String>();
		
		for(HashMap<String,Short> h : actionIds){
			for(String s : h.keySet()){
				output.add(s);
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
		if(((ActionList) this.myOptions).getSelectionState(key)){
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
	public String[] getKeys(String s) {
		// TODO Auto-generated method stub
		return null;
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
