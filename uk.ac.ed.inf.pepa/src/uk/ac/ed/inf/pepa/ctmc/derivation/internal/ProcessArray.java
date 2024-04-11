/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.derivation.internal;

import java.util.Arrays;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.Component;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.SequentialComponentData;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;
import uk.ac.ed.inf.pepa.model.ActionLevel;

public class ProcessArray extends Component {
	
	public ProcessArray() {
		super();
	}

	@Override
	public void update(short[] currentState) {
		// clear the current values
		Arrays.fill(fApparentRates, 0.0d);
		fFirstStepDerivatives.clear();

		// assume process vector is ordered,
		// i.e. same ids are adjacent

		// n has the number of copies
		for (int i = 0, n = 1; i < fLength; i++) {
			short currentId = currentState[i + fOffset];
			if (i != fLength - 1) {
				if (currentState[i + 1 + fOffset] == currentId) {
					n++;
					continue;
				}
			}
			SequentialComponentData data = fExplorer.getData(currentId);
			// update first-step derivatives
			for (Transition t : data.fFirstStepDerivative) {
				// change one element with its target
				// important, sorts the aggregation subarray
				Transition newTransition = buf.getTransition(currentState, fOffset, fLength, 
						fHidingSet.get(t.fActionId) ? ISymbolGenerator.TAU_ACTION : t.fActionId, 
						fHidingSet.get(t.fActionId) ? ActionLevel.UNDEFINED : t.fLevel,
								n * t.fRate);
				newTransition.fTargetProcess[fOffset + i] = t.fTargetProcess[0];
				Arrays.sort(newTransition.fTargetProcess, fOffset, fOffset + fLength);
				fFirstStepDerivatives.add(newTransition);
			}
			// update apparent rates
			for (int r = 0; r < data.fArrayApparentRates.length; r++) {
				//short actionId = (short) r;
				double value = n * data.fArrayApparentRates[r];
				double currentValue = fApparentRates[r];
				currentValue += value;
				fApparentRates[r] = currentValue;
			}
			/*for (Map.Entry<Short, Double> entry : data.fApparentRates
					.entrySet()) {
				short actionId = entry.getKey();
				double value = n * entry.getValue();
				Double currentValue = fApparentRates.get(actionId);
				if (currentValue == null)
					currentValue = 0.0d;
				currentValue += value;
				fApparentRates.put(actionId, currentValue);
			}*/
			// set n back to 1
			n = 1;
		}
	}

	public String toString() {
		return "Process array, offset: " + this.fOffset + ", length:" + fLength;
	}

}
