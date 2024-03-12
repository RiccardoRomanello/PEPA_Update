package uk.ac.ed.inf.pepa.cpt.config.control.performanceControl;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.ed.inf.pepa.cpt.config.control.PerformanceControl;
import uk.ac.ed.inf.pepa.cpt.config.lists.IOptionList;
import uk.ac.ed.inf.pepa.cpt.config.lists.ProcessList;
import uk.ac.ed.inf.pepa.largescale.IPointEstimator;
import uk.ac.ed.inf.pepa.largescale.simulation.DefaultCollector;
import uk.ac.ed.inf.pepa.largescale.simulation.IStatisticsCollector;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class PopulationLevelControl extends PerformanceControl {

	public PopulationLevelControl(IOptionList options) {
		super(options);
	}

	@Override
	public boolean validate() {
		
		return  ((ProcessList) this.myOptions).getSelectedProcessIds().length > 0;
	}

	@Override
	public IStatisticsCollector[] getCollectors(IPointEstimator[] estimators) {
		return DefaultCollector.create(estimators);
	}
	
	class PopulationEstimator implements IPointEstimator {

		private int index;

		public PopulationEstimator(int index) {
			this.index = index;

		}

		public double computeEstimate(double timePoint, double[] solution)
				throws DifferentialAnalysisException {
			return solution[index];
		}
	}

	@Override
	public IPointEstimator[] getEstimators() {
		Integer[] ids = ((ProcessList) this.myOptions).getSelectedProcessIds();
		IPointEstimator[] estimators = new IPointEstimator[ids.length];
		for (int i = 0; i < ids.length; i++) {
			estimators[i] = new PopulationEstimator(ids[i]);
		}
		return estimators;
	}

	@Override
	public ArrayList<HashMap<String, Short>> getOptions() {
		return ((ProcessList) this.myOptions).getAllProcessIds();
	}

	@Override
	public boolean setSelected(short processId, boolean selected) {
		return ((ProcessList) this.myOptions).setSelectedHandler(processId, selected);
	}

	@Override
	public String[] getLabels() {
		
		Integer[] processIds = ((ProcessList) this.myOptions).getSelectedProcessIds();
		String[] collector = new String[processIds.length];
		
		for(int i = 0; i < processIds.length; i++){
			collector[i] = ((ProcessList) this.myOptions).getLabel(processIds[i].shortValue());
		}
		
		return collector;
	}

	@Override
	public boolean setSelected(String name, boolean selected) {
		return ((ProcessList) this.myOptions).setSelectedHandler(name, selected);
	}

	@Override
	public String[] getKeys() {
		
		ArrayList<HashMap<String, Short>> processIds = 
			((ProcessList) this.myOptions).getAllProcessIds();
		
		ArrayList<String> output = new ArrayList<String>();
		
		for(HashMap<String,Short> h : processIds){
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
