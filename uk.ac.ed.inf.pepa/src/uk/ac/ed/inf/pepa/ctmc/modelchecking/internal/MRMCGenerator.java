package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.AbstractBoolean;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.IMRMCGenerator;

public class MRMCGenerator implements IMRMCGenerator {
	
	private static final int SIG_FIGS = 13;
	private static final double EPSILON = 1e-10;
	
	private AbstractCTMC model;
	
	public MRMCGenerator(AbstractCTMC model) {
		this.model = model;
	}
	
	public boolean print(String fileName) throws DerivationException, IOException {
		printLabelFile(fileName + ".lab");
		if (model.getMaxNondeterministicChoices() > 1) {
			printCTMDPIFile(fileName + ".ctmdpi");
		} else {
			printTransitionFile(fileName + ".tra");
		}
		DerivationException error = model.getGenerationError();
		if (error != null) {
			throw error;
		} else {
			return model.getMaxNondeterministicChoices() > 1;
		}
	}
	
	private void printLabelFile(String file) throws IOException, DerivationException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		int numStates = model.size();
		if (numStates == 0) return;
		
		// Write header
		CSLPropertyManager propertyManager = model.getPropertyManager();
		String[] labels = propertyManager.getPropertyBank().getAtomicPropertyNames();
		String header = "#DECLARATION\n  ";
		for (int i = 0; i < labels.length; i++) {
			// Replace any spaces, just in case
			String name = labels[i];
			String updatedName = name.replace(' ', '_');
			labels[i] = updatedName;
			header += updatedName + " ";
		}
		header += "\n#END\n";
		out.write(header);
		
		// Write state entries
		for (AbstractCTMCState state : model) {
			String entry = (state.getIndex() + 1) + " ";
			// Note: this relies on the way that states are labelled with
			// atomic propositions. If we change that, we'd need to alter
			// the below.
			boolean addedProperties = false;
			for (int i = 0; i < labels.length; i++) {
				AbstractBoolean propertyValue = state.getProperty(i);
				if (propertyValue == AbstractBoolean.TRUE) {
					entry += labels[i] + " ";
					addedProperties = true;
				} else if (propertyValue != AbstractBoolean.FALSE) {
					out.close();
					File fileObject = new File(file);
					fileObject.delete();
					throw new DerivationException("Atomic proposition " + labels[i] + " is non-deterministic.");
				}
			}
			entry += "\n";
			if (addedProperties) out.write(entry);
		}
		out.close();
	}
	
	private void printTransitionFile(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		int numStates = model.size();
		int numTransitions = model.getNumTransitions();
		if (numStates == 0) return;
		
		// Write header
		String header = "STATES " + numStates + "\nTRANSITIONS " + numTransitions + "\n";
		out.write(header);
		
		// Write entries
		double rate = model.getUniformisationConstant();
		for (AbstractCTMCState state : model) {
			ArrayList<AbstractCTMCTransition> transitions = state.getTransitions();
			for (AbstractCTMCTransition transition : transitions) {
				double tRate = rate * transition.getMaxProb();
				AbstractCTMCState toState = transition.getToState();
				String entry = (state.getIndex() + 1) + " " + (toState.getIndex() + 1)
								+ " " + round(tRate) + "\n";
				out.write(entry);
			}
		}
		out.close();
	}
	
	private void printCTMDPIFile(String file) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		int numStates = model.size();
		if (numStates == 0) return;
		
		// Write header
		int maxChoices = model.getMaxNondeterministicChoices();
		String header = "STATES " + numStates + "\n#DECLARATION\n  ";
		for (int i = 0; i < maxChoices; i++) {
			header += "c" + i + " ";
		}
		header += "\n#END\n";
		out.write(header);
		
		// Write entries
		double rate = model.getUniformisationConstant();
		for (AbstractCTMCState state : model) {
			String entry = getTransitions(state.getIndex() + 1, rate, state.getTransitions());
			if (entry.length() > 0) {
				out.write(entry);
			}
		}
		out.close();
	}
	
	private String getTransitions(int startState, double rate, ArrayList<AbstractCTMCTransition> transitions) {
		ArrayList<AbstractCTMCTransition> detTrans = new ArrayList<AbstractCTMCTransition>();
		ArrayList<AbstractCTMCTransition> nondetTrans = new ArrayList<AbstractCTMCTransition>();
		int numChoices = 1;
		for (AbstractCTMCTransition trans : transitions) {
			if (trans.isNonDeterministic()) {
				numChoices *= 2;
				nondetTrans.add(trans);
			} else {
				detTrans.add(trans);
			}
		}
		String transString = "";
		for (int choice = 0; choice < numChoices; choice++) {
			transString += getTransition(startState, choice, rate, detTrans, nondetTrans);
		}
		return transString;
	}
	
	private String getTransition(int startState, int choice, double rate, ArrayList<AbstractCTMCTransition> detTrans, ArrayList<AbstractCTMCTransition> nondetTrans) {
		String transString = startState + " c" + choice + "\n";
		double totalProb = 0;
		for (int i = 0; i < detTrans.size(); i++) {
			AbstractCTMCTransition trans = detTrans.get(i); 
			double transProb = trans.getMaxProb();
			if (transProb > EPSILON) {
				totalProb += transProb;
				transString += "* " + (trans.getToState().getIndex() + 1) + " " + (round (transProb * rate)) + "\n";
			}
		}
		if (nondetTrans.size() == 0) return transString;
		for (int i = 0; i < nondetTrans.size() - 1; i++) {
			AbstractCTMCTransition trans = nondetTrans.get(i);
			boolean maxProb = choice % 2 == 1;
			choice = choice >> 1;
			double transProb = (maxProb ? trans.getMaxProb() : trans.getMinProb());
			if (transProb > EPSILON) {
				totalProb += transProb;
				transString += "* " + (trans.getToState().getIndex() + 1) + " " + (round (transProb * rate)) + "\n";
			}
		}
		// Now, just add the remaining probability - we're assuming that the distribution has been delimited
		double remainingProb = 1 - totalProb;
		AbstractCTMCTransition trans = nondetTrans.get(nondetTrans.size() - 1);
		if (remainingProb >= (trans.getMinProb() - EPSILON) && remainingProb <= (trans.getMaxProb() + EPSILON)) {
			if (remainingProb > EPSILON) {
				transString += "* " + (trans.getToState().getIndex() + 1) + " " + (round (remainingProb * rate)) + "\n";
			}
			return transString;
		} else {
			// Not a possible distribution
			//System.out.println(transString);
			//System.out.println("Remaining = " + remainingProb * rate);
			return "";
		}
	}
		
	private double round(double rate) {
		if (rate == 0) return 0;
	    final double d = Math.ceil(Math.log10(rate < 0 ? -rate : rate));
	    final int power = SIG_FIGS - (int) d;
	    final double magnitude = Math.pow(10, power);
	    final long shifted = Math.round(rate * magnitude);
	    return shifted/magnitude;
	}
	
}
