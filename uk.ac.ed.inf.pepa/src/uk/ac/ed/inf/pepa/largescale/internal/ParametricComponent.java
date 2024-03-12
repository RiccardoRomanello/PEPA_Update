package uk.ac.ed.inf.pepa.largescale.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.SequentialComponentData;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ShortArray;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;
import uk.ac.ed.inf.pepa.largescale.ISequentialComponent;
import uk.ac.ed.inf.pepa.largescale.expressions.Coordinate;
import uk.ac.ed.inf.pepa.largescale.expressions.Expression;
import uk.ac.ed.inf.pepa.largescale.expressions.MultiplicationExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.RateExpression;
import uk.ac.ed.inf.pepa.largescale.expressions.SummationExpression;

public class ParametricComponent extends ParametricStructuralElement implements ISequentialComponent {

	private HashMap<Short, ArrayList<ParametricTransition>> allDerivatives = new HashMap<Short, ArrayList<ParametricTransition>>();

	private HashMap<Short, Coordinate> coordinateMap = new HashMap<Short, Coordinate>();

	private String name;

	private int initialPopulationLevel;

	private short[] actionAlphabet;
	
	public ParametricComponent(String name, int initialPopulationLevel,
			int offset, int length) {
		if (name == null)
			throw new NullPointerException();
		if (initialPopulationLevel <= 0)
			throw new IllegalArgumentException(
					"Population level must be positive");
		this.name = name;
		this.initialPopulationLevel = initialPopulationLevel;
		setOffset(offset);
		setLength(length);

	}
	
	public String getName() {
		return name;
	}
	
	public short[] getActionAlphabet() {
		if (actionAlphabet == null) {
			ShortArray sa = new ShortArray(apparentRates.size());
			for (Map.Entry<Short, Expression> ar : this.apparentRates.entrySet()) {
				sa.add(ar.getKey());
			}
			actionAlphabet = sa.toArray();
		}
		return actionAlphabet;
	}
	public int getInitialPopulationLevel() {
		return initialPopulationLevel;
	}
	
	public Set<Map.Entry<Short, Coordinate>> getComponentMapping() {
		return coordinateMap.entrySet();
	}

	public String toString() {
		return "ODE Component:" + name + "[" + initialPopulationLevel + "]"
				+ "(offset:" + getOffset() + ")" + " (length: " + getLength() + ")";
	}
	
	public int getCoordinate(short processId) {
		return coordinateMap.get(processId).getCoordinate();
	}

	public void init(ParametricStateExplorer parametricStateExplorer) {
		super.init(parametricStateExplorer);

	}

	public void compose(short[] state) {
		this.derivatives = allDerivatives.get(state[this.getOffset()]);
	}

	/**
	 * Assigns a unique coordinate index to each local derivative of this
	 * sequential component, starting from the coordinate index which is passed
	 * to this method. It returns the next available coordinate index for the
	 * overall system
	 * 
	 * @param coordinateIndex
	 * @return
	 */
	public int setupCoordinateIndexes(int coordinateIndex) {
		debug("Accepting index:" + coordinateIndex);

		if (!derivatives.isEmpty())
			throw new IllegalStateException("Derivatives must be empty now!");
		if (!apparentRates.isEmpty())
			throw new IllegalStateException("Apparent rates must be empty now!");

		ParametricStateExplorer stateExplorer = getStateExplorer();
		short initialLocalState = stateExplorer.initialVector[getOffset()];
		// coordinate starts from 1
		Coordinate coordinate = new Coordinate(coordinateIndex++);
		allDerivatives.put(initialLocalState,
				new ArrayList<ParametricTransition>());
		coordinateMap.put(initialLocalState, coordinate);

		Stack<Short> unexploredProcesses = new Stack<Short>();
		unexploredProcesses.add(initialLocalState);
		while (!unexploredProcesses.isEmpty()) {
			short state = unexploredProcesses.pop();
			// creates a coordinate index for that state
			SequentialComponentData data = stateExplorer.getData(state);
			for (Transition t : data.fFirstStepDerivative) {
				// any position is fine
				short targetProcess = t.fTargetProcess[0];
				Coordinate targetCoordinate = coordinateMap.get(targetProcess);
				if (targetCoordinate == null) {
					targetCoordinate = new Coordinate(coordinateIndex++);
					coordinateMap.put(targetProcess, targetCoordinate);
					unexploredProcesses.push(targetProcess);
					allDerivatives.put(targetProcess,
							new ArrayList<ParametricTransition>());
				}
				ParametricTransition pt = new ParametricTransition();
				pt.setTarget(t.fTargetProcess); // same target as the original
												// one
				Expression parametricRate = new MultiplicationExpression(
						new RateExpression(t.fRate), coordinateMap.get(state));
				pt.setParametricRate(parametricRate);
				if (this.hidingSet.get(t.fActionId)) {
					// hidden action
					pt.setActionId(ISymbolGenerator.TAU_ACTION);
				} else {
					pt.setActionId(t.fActionId);
					Expression apparentRate = apparentRates.get(t.fActionId);
					if (apparentRate != null) {
						apparentRate = new SummationExpression(apparentRate,
								parametricRate);
					} else {
						apparentRate = parametricRate;
					}
					apparentRates.put(t.fActionId, apparentRate);
				}
				allDerivatives.get(state).add(pt);
			}

		}
		debug("Component: " + this);
		debug("Transitions");
		for (Map.Entry<Short, ArrayList<ParametricTransition>> entry : allDerivatives
				.entrySet()) {
			debug("Derivatives for " + coordinateMap.get(entry.getKey()));
			for (ParametricTransition t : entry.getValue())
				debug(t.toString());
		}
		debug("Apparent rates");
		for (Map.Entry<Short, Expression> e : this.apparentRates.entrySet())
			debug(e.getKey() + ": " + e.getValue());
		return coordinateIndex;
	}

	private void debug(String s) {
		//System.err.println(s);
	}

}
