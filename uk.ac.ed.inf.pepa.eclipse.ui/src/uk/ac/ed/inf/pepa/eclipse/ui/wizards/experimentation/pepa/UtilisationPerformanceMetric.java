/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.pepa;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.pepa.analysis.StaticAnalyser;
import uk.ac.ed.inf.pepa.ctmc.LocalState;
import uk.ac.ed.inf.pepa.ctmc.SequentialComponent;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.AbstractPerformanceMetric;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.EvaluationException;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ExperimentationWizard;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.IEvaluator;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ISetting;
import uk.ac.ed.inf.pepa.parsing.ASTSupport;
import uk.ac.ed.inf.pepa.parsing.AggregationNode;
import uk.ac.ed.inf.pepa.parsing.ConstantProcessNode;
import uk.ac.ed.inf.pepa.parsing.CooperationNode;
import uk.ac.ed.inf.pepa.parsing.DefaultVisitor;
import uk.ac.ed.inf.pepa.parsing.HidingNode;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.PrefixNode;
import uk.ac.ed.inf.pepa.parsing.ProcessNode;

/**
 * Evaluates the utilisation for a particular sequential state. If the
 * sequential state appears in different position the the average utilisation is
 * returned.
 * 
 * @author mtribast
 * 
 */
public class UtilisationPerformanceMetric extends AbstractPerformanceMetric {

	private Combo combo;

	private String lastValue = null;

	private int position = -1;

	private Set<String> topLevelComponentName = new TreeSet<String>();
	
	/* Sequential Components in system equation */
	private ArrayList<ProcessNode> topLevelComponents = new ArrayList<ProcessNode>();

	public UtilisationPerformanceMetric(String description, IEvaluator evaluator) {
		super(description, evaluator);
	}

	private void fillActionTypes(ModelNode model) {
		IWizard wizard = this.experiment.getExperimentPage().getWizard();
		Assert.isTrue(wizard instanceof ExperimentationWizard);
		
		((IPepaModel) getModel()).getAST().accept(new DefaultVisitor() {

			@Override
			public void visitModelNode(ModelNode model) {
				model.getSystemEquation().accept(this);

			}

			@Override
			public void visitCooperationNode(CooperationNode coop) {
				coop.getLeft().accept(this);
				coop.getRight().accept(this);
			}

			@Override
			public void visitHidingNode(HidingNode hiding) {
				hiding.getProcess().accept(this);
			}

			@Override
			public void visitConstantProcessNode(ConstantProcessNode constant) {
				topLevelComponents.add(constant);
				topLevelComponentName.addAll(((IPepaModel) getModel()).getStaticAnalyser()
						.getAlphabetProvider().getProcessAlphabets().get(
								constant.getName()));
			}

			@Override
			public void visitPrefixNode(PrefixNode prefix) {
				topLevelComponents.add(prefix);
			}

			@Override
			public void visitAggregationNode(AggregationNode aggregation) {
				topLevelComponents.add(null);
			}
		});

	}

	public void createControl(Composite parent) {

		fillActionTypes(((IPepaModel) getModel()).getAST());

		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		// layout.marginHeight = 0;
		layout.marginWidth = 0;
		// layout.verticalSpacing = 0;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(main, SWT.WRAP);
		label.setText("Local state");
		label.setLayoutData(new GridData());

		combo = new Combo(main, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				handleSelection();
			}

		});

		populateCombo();

	}

	private void populateCombo() {
		for (String action : topLevelComponentName)
			combo.add(action);
		/* remember the last value from the model object */
		if (lastValue != null)
			combo.setText(lastValue);

	}

	private void handleSelection() {
		lastValue = combo.getText();
		int[] positions = getPositions(lastValue);
		for (int p : positions) {
			System.err.println("Position for " + lastValue + ":" + p);
		}
		if (positions.length > 1) {
			position = openWarningDialogBox(positions);
		} else {
			position = positions[0];
		}
		this.parent.updateParentState();

	}

	private int openWarningDialogBox(final int[] positions) {
		IInputValidator validator = new IInputValidator() {

			public String isValid(String newText) {
				int position = -1;
				try {
					position = Integer.parseInt(newText);
				} catch (Exception e) {
					return "Insert a correct position";
				}
				boolean found = false;
				for (int p : positions) {
					if (position == p) {
						found = true;
						break;
					}
				}

				if (!found) {
					return "Position must be one of: " + getString(positions);
				}
				return null;
			}

		};
		InputDialog dialog = new InputDialog(this.getExperiment()
				.getExperimentPage().getShell(),
				"Repeated sequential component",
				"This sequential component may appear in different positions ("
						+ getString(positions)
						+ "). Select your position of interest.", positions[0]
						+ "", validator);
		String value = null;
		if (InputDialog.OK != dialog.open())
			value = Integer.toString(positions[0]); // default
		else
			value = dialog.getValue(); // user-defined
		return Integer.parseInt(value);
	}

	private String getString(int[] positions) {
		StringBuffer buf = new StringBuffer();
		for (int p : positions)
			buf.append(p + ",");
		buf.deleteCharAt(buf.length() - 1);
		return buf.toString();
	}

	/*
	 * Get the positions in the system equation where the given component may
	 * appear.
	 */
	private int[] getPositions(String componentName) {
		StaticAnalyser analyser = ((IPepaModel) getModel()).getStaticAnalyser();
		// model must have been parsed
		Assert.isNotNull(analyser);
		ArrayList<Integer> list = new ArrayList<Integer>();
		// cycle through all the top level components
		for (int position = 0; position < topLevelComponents.size(); position++) {
			ProcessNode node = topLevelComponents.get(position);
			if (node == null)
				continue; // aggregation
			String topLevelName = ASTSupport.toString(node);
			// System.err.println("Name: " + topLevelName);
			if (analyser.getAlphabetProvider().getProcessAlphabets().get(
					topLevelName).contains(componentName)) {
				list.add(position);
			}

		}
		int[] result = new int[list.size()];
		for (int i = 0; i < list.size(); i++)
			result[i] = list.get(i);
		return result;
	}

	public double evaluate(ISetting[] settings, int[] currentIndex)
		throws EvaluationException {

		double result = Double.NaN;

		try {
			
			IStateSpace ss = evaluator.doEvaluate(settings, currentIndex);
			
			SequentialComponent[] utilisation = ss.getUtilisation();
			SequentialComponent c = utilisation[position];
			for (LocalState s : c.getLocalStates()) {
				if (s.getName().equals(lastValue)) {
					result = s.getUtilisation();
					break; // go to next
				}
			}
		} catch (Exception e) {
			PepaLog.logError(e);
			throw new EvaluationException(e);
		}
		if (Double.isNaN(result))
			throw new EvaluationException(new IllegalStateException(
					"No utilisation found"));
		return result;
	}

	public boolean isCanEvaluate() {
		return lastValue != null;
	}

	@Override
	public String getLabel() {
		return getDescription() + ": " + lastValue;
	}

}
