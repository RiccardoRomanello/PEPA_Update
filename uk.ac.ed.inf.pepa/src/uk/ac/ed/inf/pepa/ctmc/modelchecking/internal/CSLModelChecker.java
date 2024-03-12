/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import uk.ac.ed.inf.pepa.IProgressMonitor;
import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.kronecker.internal.KroneckerStateSpace;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.*;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class CSLModelChecker implements ICSLVisitor, ICSLModelChecker {
	
	private double boundAccuracy;
	
	private OptionMap optionMap;
	private IProgressMonitor monitor;
	
	private KroneckerStateSpace stateSpace;
	private CSLPropertyManager propertyManager;
	private AbstractCTMC abstractCTMC;
	private SequentialAbstraction[] abstraction;
	
	private ProbabilityInterval testAnswer;
	
	private ModelCheckingLog eventLog;
	
	public CSLModelChecker(KroneckerStateSpace stateSpace, AbstractCTMC abstractCTMC, SequentialAbstraction[] abstraction,
			               OptionMap optionMap, IProgressMonitor monitor, double boundAccuracy, ModelCheckingLog eventLog) {
		this.stateSpace = stateSpace;
		this.abstractCTMC = abstractCTMC;
		this.propertyManager = abstractCTMC.getPropertyManager();
		this.abstraction = abstraction;
		this.optionMap = optionMap;
		this.boundAccuracy = boundAccuracy;
		this.eventLog = eventLog;
	}
	
	public void visit(CSLPathPropertyNode node) throws ModelCheckingException {
		CSLAbstractProbability abstractProb = node.getComparator();
		if (abstractProb instanceof CSLProbabilityTest) {
			double lowerProbability = 1;
			double upperProbability = 0;
			// What is the probability of the initial state satisfying the property?
			AbstractCTMCState state = abstractCTMC.getInitialState();
			lowerProbability = Math.min(lowerProbability, state.getMinProbability());
			upperProbability = Math.max(upperProbability, state.getMaxProbability());
			if (((CSLProbabilityTest)abstractProb).isInverse()) {
				// Need to give 1 - answer
				testAnswer = new ProbabilityInterval(1 - upperProbability, 1 - lowerProbability);
			} else {
				testAnswer = new ProbabilityInterval(lowerProbability, upperProbability);
			}
		} else if (abstractProb instanceof CSLProbabilityComparator) {
			CSLProbabilityComparator comparator = (CSLProbabilityComparator)abstractProb;
			int counter = 0;
			for (AbstractCTMCState state : abstractCTMC) {
				testInterrupted(counter++);
				AbstractBoolean isOK = comparator.checkProbability(state.getMinProbability(), state.getMaxProbability());
				propertyManager.set(node, state, isOK);
			}
		}
	}

	public void visit(CSLSteadyStateNode node) throws ModelCheckingException {
		CSLAbstractStateProperty property = node.getProperty();
		CSLAbstractProbability comparator = node.getComparator();
		SteadyStateAnalyser analyser = new SteadyStateAnalyser(stateSpace, abstractCTMC, propertyManager, abstraction, eventLog);
		ProbabilityInterval answer;
		if (property.isCompositional()) {
			CSLCompositionalModelChecker compositionalChecker = new CSLCompositionalModelChecker(propertyManager, abstraction);
			CompositionalPropertyList compositionalProperty = compositionalChecker.getProperty(property);
			answer = analyser.checkSteadyState(compositionalProperty, optionMap, monitor);
		} else {
			answer = analyser.checkSteadyState(property, optionMap, monitor);
		}
		if (comparator instanceof CSLProbabilityComparator) {
			CSLProbabilityComparator probabilityComparator = (CSLProbabilityComparator)comparator;
			boolean upperOK = probabilityComparator.checkProbability(answer.getUpper());
			boolean lowerOK = probabilityComparator.checkProbability(answer.getLower());
			AbstractBoolean isOK;
			if (upperOK && lowerOK) {
				isOK = AbstractBoolean.TRUE;
			} else if (!upperOK && !lowerOK) {
				isOK = AbstractBoolean.FALSE;
			} else {
				isOK = AbstractBoolean.MAYBE;
			}
			propertyManager.setConstantProperty(node, isOK);
		} else if (comparator instanceof CSLProbabilityTest) {
			testAnswer = answer;
		} else {
			assert false;
		}
	}
	
	public void visit(CSLLongRunNode node) throws ModelCheckingException {
		CSLAbstractStateProperty property = node.getProperty();
		CSLAbstractProbability abstractProb = node.getComparator();
		TransientAnalyser analyser = new TransientAnalyser(abstractCTMC, propertyManager, boundAccuracy);
		analyser.checkLongRun(property);
		if (abstractProb instanceof CSLProbabilityComparator) {
			CSLProbabilityComparator comparator = (CSLProbabilityComparator)abstractProb;
			int counter = 0;
			for (AbstractCTMCState state : abstractCTMC) {
				testInterrupted(counter++);
				AbstractBoolean isOK = comparator.checkProbability(state.getMinProbability(), state.getMaxProbability());
				propertyManager.set(node, state, isOK);
			}
		} else if (abstractProb instanceof CSLProbabilityTest) {
			double lowerProbability = 1;
			double upperProbability = 0;
			// What is the long-run probability of the property starting from the initial state?
			AbstractCTMCState state = abstractCTMC.getInitialState();
			lowerProbability = Math.min(lowerProbability, state.getMinProbability());
			upperProbability = Math.max(upperProbability, state.getMaxProbability());
			testAnswer = new ProbabilityInterval(lowerProbability, upperProbability);
		} else {
			assert false;
		}
	}
	
	public void visit(CSLUntilNode node) throws ModelCheckingException {
		CSLAbstractStateProperty property1 = node.getProperty1();
		CSLAbstractStateProperty property2 = node.getProperty2();
		CSLTimeInterval timeInterval = node.getTimeInterval();
		TransientAnalyser analyser = new TransientAnalyser(abstractCTMC, propertyManager, boundAccuracy);
		analyser.checkUntil(property1, property2, timeInterval);
	}

	public void visit(CSLNextNode node) throws ModelCheckingException {
		CSLAbstractStateProperty property = node.getProperty();
		CSLTimeInterval timeInterval = node.getTimeInterval();
		TransientAnalyser analyser = new TransientAnalyser(abstractCTMC, propertyManager, boundAccuracy);
		analyser.checkNext(property, timeInterval);
	}
	
	public void visit(CSLEventuallyNode node) throws ModelCheckingException {
		assert false;
	}
	
	public void visit(CSLGloballyNode node) throws ModelCheckingException {
		assert false;
	}
	
	public void visit(CSLBooleanNode node) throws ModelCheckingException {
		// Either globally true or false, so nothing to do
		return;
	}
	
	public void visit(CSLAtomicNode node) throws ModelCheckingException {
		// Atomic properties have already been added
		return;
	}
	
	public void visit(CSLPathPlaceHolder node) throws ModelCheckingException {
		throw new ModelCheckingException("This is not a valid CSL formula.");
	}
	
	public void visit(CSLStatePlaceHolder node) throws ModelCheckingException {
		throw new ModelCheckingException("This is not a valid CSL formula.");
	}
	
	public void visit(CSLNotNode node) throws ModelCheckingException {
		CSLAbstractStateProperty property = node.getProperty();
		AbstractBoolean value = propertyManager.getConstantProperty(property);
		if (value != null) {
			propertyManager.setConstantProperty(node, AbstractBoolean.not(value));
		} else if (property.isCompositional()) {
			// We will look at this node when we reach the steady state operator
			// that contains it.
			propertyManager.setConstantProperty(node, AbstractBoolean.NOT_SET);
		} else {
			int counter = 0;
			for (AbstractCTMCState state : abstractCTMC) {
				testInterrupted(counter++);
				AbstractBoolean v = propertyManager.test(property, state);
				AbstractBoolean isOK = AbstractBoolean.not(v);
				propertyManager.set(node, state, isOK);
			}
		}
	}
	
	public void visit(CSLAndNode node) throws ModelCheckingException {
		CSLAbstractStateProperty property1 = node.getProperty1();
		CSLAbstractStateProperty property2 = node.getProperty2();
		AbstractBoolean value1 = propertyManager.getConstantProperty(property1);
		AbstractBoolean value2 = propertyManager.getConstantProperty(property2);
		if (value1 != null && value2 != null) {
			propertyManager.setConstantProperty(node, AbstractBoolean.and(value1, value2));
		} else if (property1.isCompositional() && property2.isCompositional()) {
			// We will look at these nodes when we reach the steady state operator
			// that contains them.
			propertyManager.setConstantProperty(node, AbstractBoolean.NOT_SET);
		} else {
			int counter = 0;
			for (AbstractCTMCState state : abstractCTMC) {
				testInterrupted(counter++);
				AbstractBoolean v1 = propertyManager.test(node.getProperty1(), state);
				AbstractBoolean v2 = propertyManager.test(node.getProperty2(), state);
				AbstractBoolean isOK = AbstractBoolean.and(v1, v2); 
				propertyManager.set(node, state, isOK);
			}
		}
	}
	
	public void visit(CSLOrNode node) throws ModelCheckingException {
		CSLAbstractStateProperty property1 = node.getProperty1();
		CSLAbstractStateProperty property2 = node.getProperty2();
		AbstractBoolean value1 = propertyManager.getConstantProperty(property1);
		AbstractBoolean value2 = propertyManager.getConstantProperty(property2);
		if (value1 != null && value2 != null) {
			propertyManager.setConstantProperty(node, AbstractBoolean.or(value1, value2));
		} else if (property1.isCompositional() && property2.isCompositional()) {
			// We will look at these nodes when we reach the steady state operator
			// that contains them.
			propertyManager.setConstantProperty(node, AbstractBoolean.NOT_SET);
		} else {
			int counter = 0;
			for (AbstractCTMCState state : abstractCTMC) {
				testInterrupted(counter++);
				AbstractBoolean v1 = propertyManager.test(node.getProperty1(), state);
				AbstractBoolean v2 = propertyManager.test(node.getProperty2(), state);
				AbstractBoolean isOK = AbstractBoolean.or(v1, v2); 
				propertyManager.set(node, state, isOK);
			}
		}
	}
	
	public void visit(CSLImpliesNode node) throws ModelCheckingException {
		CSLAbstractStateProperty property1 = node.getProperty1();
		CSLAbstractStateProperty property2 = node.getProperty2();
		AbstractBoolean value1 = propertyManager.getConstantProperty(property1);
		AbstractBoolean value2 = propertyManager.getConstantProperty(property2);
		if (value1 != null && value2 != null) {
			propertyManager.setConstantProperty(node, AbstractBoolean.implies(value1, value2));
		} else if (property1.isCompositional() && property2.isCompositional()) {
			// We will look at these nodes when we reach the steady state operator
			// that contains them.
			propertyManager.setConstantProperty(node, AbstractBoolean.NOT_SET);
		} else {
			int counter = 0;
			for (AbstractCTMCState state : abstractCTMC) {
				testInterrupted(counter++);
				AbstractBoolean v1 = propertyManager.test(node.getProperty1(), state);
				AbstractBoolean v2 = propertyManager.test(node.getProperty2(), state);
				AbstractBoolean isOK = AbstractBoolean.implies(v1, v2); 
				propertyManager.set(node, state, isOK);
			}
		}
	}

	private ProbabilityInterval getTestAnswer() {
		return testAnswer;
	}
	
	private AbstractBoolean getTestOK(CSLAbstractStateProperty property) throws ModelCheckingException {
		AbstractBoolean isOK = propertyManager.getConstantProperty(property);
		if (isOK == null) {
			// Work out whether the property is true of all states
			isOK = AbstractBoolean.TRUE;
			int counter = 0;
			for (AbstractCTMCState state: abstractCTMC) {
				testInterrupted(counter++);
				AbstractBoolean isStateOK = propertyManager.test(property, state);
				isOK = AbstractBoolean.and(isOK, isStateOK);
			}
		}
		return isOK;
	}

	private void modelCheck(CSLAbstractStateProperty property) throws ModelCheckingException {
		property.setCompositionality();
		propertyManager.addProperty(property);
		property.accept(this);
		// This is a safety measure, in case we forget to unregister a property
		for (int i = 0; i < abstraction.length; i++) {
			abstraction[i].unregisterAllProperties();
		}
	}
	
	public AbstractBoolean checkProperty(CSLAbstractStateProperty property) throws ModelCheckingException {
		if (property.isProbabilityTest()) {
			throw new ModelCheckingException("The property is not boolean: use testProperty() instead.");
		}
		CSLAbstractStateProperty normalisedProperty = property.normalise();
		modelCheck(normalisedProperty);
		DerivationException error = abstractCTMC.getGenerationError();
		if (error == null) {
			return getTestOK(normalisedProperty);
		} else {
			throw new ModelCheckingException(error.getMessage());
		}
	}

	public ProbabilityInterval testProperty(CSLAbstractStateProperty property) throws ModelCheckingException {
		if (!property.isProbabilityTest()) {
			throw new ModelCheckingException("The property is not testable: use checkProperty() instead.");
		}
		CSLAbstractStateProperty normalisedProperty = property.normalise();
		modelCheck(normalisedProperty);
		DerivationException error = abstractCTMC.getGenerationError();
		if (error == null) {
			return getTestAnswer();
		} else {
			throw new ModelCheckingException(error.getMessage());
		}
	}

	public IMRMCGenerator getMRMCGenerator() {
		return new MRMCGenerator(abstractCTMC);
	}
	
	public void addLogListener(ILogListener listener) {
		if (eventLog != null) {
			eventLog.addListener(listener);
		}
	}
	
	private void testInterrupted(int counter) throws ModelCheckingException {
		if (counter % 1000 == 0 && Thread.interrupted()) {
			throw new ModelCheckingException("The model checker was interrupted.");
		}
	}
	
}
