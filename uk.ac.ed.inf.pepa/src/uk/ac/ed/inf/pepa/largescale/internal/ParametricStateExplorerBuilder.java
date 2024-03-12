/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.largescale.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.ac.ed.inf.pepa.ctmc.derivation.common.ISymbolGenerator;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.SequentialComponentData;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Transition;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.HashMapSequentialComponentData;
import uk.ac.ed.inf.pepa.ctmc.derivation.internal.SymbolGenerator;
import uk.ac.ed.inf.pepa.largescale.IParametricStructuralElement;
import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.model.ActionSet;
import uk.ac.ed.inf.pepa.model.Aggregation;
import uk.ac.ed.inf.pepa.model.Choice;
import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Cooperation;
import uk.ac.ed.inf.pepa.model.FiniteRate;
import uk.ac.ed.inf.pepa.model.Hiding;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.Prefix;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.Rate;
import uk.ac.ed.inf.pepa.model.Visitor;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

/**
 * Creates a state space explorer and a symbol table
 * 
 * @author mtribast
 * 
 */
public class ParametricStateExplorerBuilder {

	/* the input model */
	private Model model;

	/* offset for state descriptor */
	private int offset = 0;

	/**
	 * Maintains associations between sequential components and their
	 * identifiers used in the state space exploration
	 */
	private final HashMap<Short, Process> sequentialComponentsMap = new HashMap<Short, Process>();

	/**
	 * Maintains associations between action types and their identifiers used in
	 * the state space exploration
	 */
	private final HashMap<Short, NamedAction> actionMap = new HashMap<Short, NamedAction>();

	/**
	 * Holds sequential component data indexed by the process identifier.
	 */
	public final HashMap<Short, HashMapSequentialComponentData> sequentialComponentsData = new HashMap<Short, HashMapSequentialComponentData>();

	/* Temporary list of operators for the state explorer */
	private final ArrayList<ParametricOperator> operators = new ArrayList<ParametricOperator>();

	/* Same order as operators */
	private final ArrayList<List<Short>> cooperationSets = new ArrayList<List<Short>>();

	/* Temporary hiding sets */
	private final HashMap<IParametricStructuralElement, List<Short>> hidingSets = new HashMap<IParametricStructuralElement, List<Short>>();

	/* Temporary list of sequential components for the state explorer */
	private final LinkedList<ParametricComponent> sequentialComponents = new LinkedList<ParametricComponent>();

	/* Temporary copy of explorer */
	private final ParametricStateExplorer explorer = new ParametricStateExplorer();

	/* Counter for sequential components ( > 0) */
	private short seqProcess;

	/* Counter for actions (> 0) */
	private short seqAction;

	/* Temporary list for the initial state */
	private LinkedList<Short> initialState = new LinkedList<Short>();

	private short[] initialStateVector;

	class ComposerVisitor implements Visitor {

		public IParametricStructuralElement lastVisited;

		public ComposerVisitor() {

		}

		private List<Short> createActionSetIdentifiers(ActionSet set) {
			List<Short> actionSet = new LinkedList<Short>();
			Iterator<Action> iter = set.iterator();
			while (iter.hasNext()) {
				actionSet.add(getIndex((NamedAction) iter.next()));
			}
			return actionSet;
		}

		public void visitAggregation(Aggregation aggregation) {
			Constant constant = (Constant) aggregation.getSubProcesses().keySet()
				.iterator().next();
			addConstant(constant, aggregation.getCopies());
		}

		public void visitChoice(Choice choice) {
			throw new IllegalStateException(
					"Choice shouldn't be seen in the system equation");
		}

		public void visitConstant(Constant constant) {
			addConstant(constant, 1);
		}
		
		private void addConstant(Constant constant, int copies) {
			ParametricComponent component = new ParametricComponent(
					constant.getName(),copies, offset++, 1);
			sequentialComponents.add(component);
			initialState.add(getIndex(constant));
			lastVisited = component;
		}

		public void visitCooperation(Cooperation cooperation) {
			ParametricOperator operator = new ParametricOperator();
			operators.add(operator);
			List<Short> list = createActionSetIdentifiers(cooperation
					.getActionSet());
			cooperationSets.add(list);
			ComposerVisitor leftVisitor = new ComposerVisitor();
			cooperation.getLeftHandSide().accept(leftVisitor);
			ComposerVisitor rightVisitor = new ComposerVisitor();
			cooperation.getRightHandSide().accept(rightVisitor);
			operator.setLeftChild(leftVisitor.lastVisited);
			operator.setRightChild(rightVisitor.lastVisited);
			lastVisited = operator;

		}

		public void visitHiding(Hiding hiding) {
			hiding.getHiddenProcess().accept(this);
			/* add the action set to the last visited */
			hidingSets.put(lastVisited, createActionSetIdentifiers(hiding
					.getActionSet()));

		}

		public void visitPrefix(Prefix prefix) {
			throw new IllegalStateException(
					"Prefix shouldn't be seen in the system equation");
		}

	}

	class SequentialComponentVisitor implements Visitor {

		short fSourceId;

		boolean fGuardedDefinition;

		HashMap<Short, ArrayList<Short>> fUnguardedDefinitionMap;

		/**
		 * Identifier of source process for first step derivatives found by this
		 * visitor.
		 * 
		 * @param sourceId
		 * @param unguardedDefinitionMap
		 */
		public SequentialComponentVisitor(short sourceId,
				boolean guardedDefinition,
				HashMap<Short, ArrayList<Short>> unguardedDefinitionMap) {
			this.fSourceId = sourceId;
			this.fGuardedDefinition = guardedDefinition;
			this.fUnguardedDefinitionMap = unguardedDefinitionMap;
		}

		public void visitAggregation(Aggregation aggregation) {
			throw new IllegalStateException("Aggregation "
					+ aggregation.prettyPrint() + " should"
					+ "not be found in sequential component definition");
		}

		public void visitChoice(Choice choice) {
			choice.getLeftHandSide().accept(this);
			choice.getRightHandSide().accept(this);
		}

		public void visitConstant(Constant constant) {
			if (!fGuardedDefinition) {
				// System.err.println(constant.getName() + ": definition not
				// guarded");
				// System.err.println("Source: " +
				// fGenerator.getProcessLabel(fSourceId));
				short targetId = getIndex(constant);
				fUnguardedDefinitionMap.get(fSourceId).add(targetId);
			}
			return; // does nothing here
		}

		public void visitCooperation(Cooperation cooperation) {
			throw new IllegalStateException("Cooperation "
					+ cooperation.prettyPrint() + " should"
					+ "not be found in sequential component definition");

		}

		public void visitHiding(Hiding hiding) {
			throw new IllegalStateException("Hiding " + hiding.prettyPrint()
					+ " should"
					+ "not be found in sequential component definition");

		}

		public void visitPrefix(Prefix prefix) {
			HashMapSequentialComponentData data = sequentialComponentsData
					.get(this.fSourceId);
			if (data == null) {
				data = new HashMapSequentialComponentData();
				sequentialComponentsData.put(fSourceId, data);
			}
			// update apparent rates
			Action action = prefix.getActivity().getAction();
			short actionId;
			double rate;
			try {
				rate = convertRate(prefix.getActivity().getRate());
			} catch (DifferentialAnalysisException e) {
				throw new IllegalStateException(e);
			}
			if (action instanceof NamedAction) {
				actionId = getIndex((NamedAction) action);
				double oldValue = data.fApparentRates.containsKey(actionId) ? data.fApparentRates
						.get(actionId)
						: 0.0d;
				double newValue = oldValue + rate;
				data.fApparentRates.put(actionId, newValue);
			} else {
				actionId = ISymbolGenerator.TAU_ACTION;
			}
			// update first step derivatives of this component
			Process target = prefix.getTargetProcess();
			Transition transition = new Transition();
			/* If unnamed state, reserve a new index */
			short targetProcessId;

			/*
			 * 01/11/2007 Old code if (target instanceof Constant) {
			 * targetProcessId = fGenerator.getIndex(target); } else {
			 * fGenerator.sequentialComponentsMap.put(fGenerator.seqProcess,
			 * target); targetProcessId = fGenerator.seqProcess++; }
			 */
			targetProcessId = getIndex(target);
			// System.err.println("Target: " + target.prettyPrint() + " id:"
			// + targetProcessId);

			// set target process
			transition.fTargetProcess = new short[initialStateVector.length];
			Arrays.fill(transition.fTargetProcess, targetProcessId);
			transition.fActionId = actionId;
			transition.fRate = rate;

			data.fFirstStepDerivative.add(transition);
			// check if target has been explored already
			HashMapSequentialComponentData targetData = sequentialComponentsData
					.get(targetProcessId);
			if (targetData != null) {
				// System.err.println("Target " + target.prettyPrint()
				// + " already visited");
				// if the target is a constant, the visit will do nothing
				// if not, it is a unnamed component which
				// doesn't need to be visited again
			} else {

				target.accept(new SequentialComponentVisitor(targetProcessId,
						true, fUnguardedDefinitionMap));
			}

		}
	};

	public ParametricStateExplorerBuilder(Model model) throws DifferentialAnalysisException {
		seqProcess = seqAction = 0;
		this.model = model;
		// creates structure, initialises sequentialComponents, operators,
		// and adds a number of actions
		ComposerVisitor v = new ComposerVisitor();
		this.model.getSystemEquation().accept(v);
		// we have the initial state
		initialStateVector = new short[initialState.size()];
		int i = 0;
		for (short stateId : initialState)
			initialStateVector[i++] = stateId;
		// creates all symbols
		scan_two();
		// all action types have been explored
		// seq_action - 1 is the number of actions found
		int n = seqAction;
		// creates cooperation sets
		for (int op = 0; op < operators.size(); op++) {
			List<Short> cooperationSet = cooperationSets.get(op);
			ParametricOperator operator = operators.get(op);
			operator.setCooperationSet(createBitSet(cooperationSet, n));
			// no need to clear, it will be done upon compose()
			// hiding sets for operators
			List<Short> hs = hidingSets.get(operator);
			if (hs!=null) {
				operator.setHidingSet(createBitSet(hs,n));
			} else {
				operator.setHidingSet(createBitSet(new ArrayList<Short>(),n));
			}
		}
		// hiding set for components
		for (ParametricComponent c : sequentialComponents) {
			List<Short> hs = hidingSets.get(c);
			if (hs!=null) {
				c.setHidingSet(createBitSet(hs,n));
			} else {
				c.setHidingSet(createBitSet(new ArrayList<Short>(),n));
			}
		}
		// prepares the state explorer
		explorer.operators = operators.toArray(new ParametricOperator[operators.size()]);
		explorer.sequentialComponents = sequentialComponents
				.toArray(new ParametricComponent[sequentialComponents.size()]);
		explorer.sequentialComponentInfo = new SequentialComponentData[seqProcess];
		for (Map.Entry<Short, HashMapSequentialComponentData> entry : sequentialComponentsData
				.entrySet()) {
			double[] apparentRates = new double[n];
			Arrays.fill(apparentRates, 0.0d);
			HashMapSequentialComponentData value = entry.getValue();
			for (Map.Entry<Short, Double> apparentRateEntry : value.fApparentRates
					.entrySet()) {
				apparentRates[apparentRateEntry.getKey()] = apparentRateEntry
						.getValue();
			}
			SequentialComponentData data = new SequentialComponentData();
			data.fFirstStepDerivative = entry.getValue().fFirstStepDerivative;
			data.fArrayApparentRates = apparentRates;
			explorer.sequentialComponentInfo[entry.getKey()] = data;
		}
		explorer.initialVector = initialStateVector;
		explorer.init();
	}

	private BitSet createBitSet(List<Short> set, int n) {
		BitSet bitSet = new BitSet(n);
		for (short s : set) {
			bitSet.set(s);
		}
		return bitSet;
	}

	public ParametricStateExplorer getExplorer() {
		return explorer;
	}

	public ISymbolGenerator getSymbolGenerator() {
		return new SymbolGenerator(initialStateVector, sequentialComponentsMap,
				actionMap);
	}

	private short getIndex(NamedAction action) {
		for (Map.Entry<Short, NamedAction> entry : actionMap.entrySet()) {
			if (action.equals(entry.getValue()))
				return entry.getKey();
		}
		// index null, create new index
		actionMap.put(seqAction, action);
		return seqAction++;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ed.inf.pepa.ctmc.derivation.internal.ISymbolGenerator#getIndex(
	 * uk.ac.ed.inf.pepa.model.Process)
	 */
	public short getIndex(Process process) {
		Short id = basicGetIndex(process);
		if (id != null)
			return id;
		// index null, create new index
		sequentialComponentsMap.put(seqProcess, process);
		return seqProcess++;

	}

	private Short basicGetIndex(Process process) {
		for (Map.Entry<Short, Process> entry : sequentialComponentsMap
				.entrySet()) {
			if (process.equals(entry.getValue()))
				return entry.getKey();
		}
		return null;
	}

	private void scan_two() throws DifferentialAnalysisException {
		short index;
		// unguarded definition map:
		// key: source process id
		// value: list of unguarded process ids
		HashMap<Short, ArrayList<Short>> unguardedDefinitionMap = new HashMap<Short, ArrayList<Short>>();

		for (Constant constant : model.getProcessDefinitions()) {
			// index works for constants
			index = getIndex(constant);
			unguardedDefinitionMap.put(index, new ArrayList<Short>());
			try {
			constant.getBinding().accept(
					new SequentialComponentVisitor(index, false,
							unguardedDefinitionMap));
			} catch (IllegalStateException e) {
				throw new DifferentialAnalysisException(e.getMessage());
			}
		}

		boolean addedOnce;
		do {
			addedOnce = false;

			for (Map.Entry<Short, ArrayList<Short>> entry : unguardedDefinitionMap
					.entrySet()) {

				HashMapSequentialComponentData sourceData = this.sequentialComponentsData
						.get(entry.getKey());
				if (sourceData == null) {
					sourceData = new HashMapSequentialComponentData();
					this.sequentialComponentsData.put(entry.getKey(),
							sourceData);
				}

				for (Short target : entry.getValue()) {
					HashMapSequentialComponentData targetData = sequentialComponentsData
							.get(target);

					if (targetData == null) {
						targetData = new HashMapSequentialComponentData();
						this.sequentialComponentsData.put(target, targetData);
					}

					// add target to source only if not added before
					for (Transition transition : targetData.fFirstStepDerivative) {
						if (!sourceData.fFirstStepDerivative
								.contains(transition)) {
							// copy first step derivative
							sourceData.fFirstStepDerivative.add(transition);
							// update apparent rates
							Double old = sourceData.fApparentRates
									.get(transition.fActionId);
							if (old == null)
								old = 0d;
							double newRate = old + transition.fRate;
							sourceData.fApparentRates.put(transition.fActionId,
									newRate);
							addedOnce = true;
						}
					}
				}
			}
		} while (addedOnce);

	}

	private double convertRate(Rate r) throws DifferentialAnalysisException {
		if (r instanceof FiniteRate)
			return ((FiniteRate) r).getValue();
		else
			throw new DifferentialAnalysisException("Passive rates are not currently supported.");
	}

}
