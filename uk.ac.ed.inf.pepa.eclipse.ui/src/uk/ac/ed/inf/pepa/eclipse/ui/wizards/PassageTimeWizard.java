/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.draw2d.CheckBox;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uk.ac.ed.inf.common.ui.plotting.IChart;
import uk.ac.ed.inf.common.ui.plotting.Plotting;
import uk.ac.ed.inf.common.ui.plotting.data.InfoWithAxes;
import uk.ac.ed.inf.common.ui.plotting.data.Series;
import uk.ac.ed.inf.pepa.analysis.IAlphabetProvider;
import uk.ac.ed.inf.pepa.analysis.StaticAnalyser;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.PepaJHydra;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ExperimentPage;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ISensibleNode;
import uk.ac.ed.inf.pepa.jhydra.driver.passagetimesolver.PassageTimeResults;
import uk.ac.ed.inf.pepa.parsing.ASTFactory;
import uk.ac.ed.inf.pepa.parsing.ASTSupport;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.RateDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.RateDefinitions;
import uk.ac.ed.inf.pepa.parsing.RateDoubleNode;

/**
 * I had multiple nightmares just in setting up some controls for a passage-time
 * wizard, hence I have copied across the latexifier wizard and I'm going to
 * modify this until it does what I want it to :)
 * 
 * @author mtribast+aclark6
 * 
 */
public class PassageTimeWizard extends Wizard {

	/**
	 * Page for Passage-time settings
	 * 
	 * @author aclark6
	 * 
	 */
	private class SettingPage extends WizardPage {

		private static final String SECTION_NAME = "passagetime.settingPage";

		private Label startTimeLabel;
		private Text startTimeText;
		private double startTime = 0.1;

		public double getStartTime() {
			return startTime;
		}

		private Label timeStepLabel;
		private Text timeStepText;
		private double timeStep = 0.1;

		public double getTimeStep() {
			return timeStep;
		}

		private Label stopTimeLabel;
		private Text stopTimeText;
		private double stopTime = 10.0;

		public double getStopTime() {
			return stopTime;
		}

		private Label sourceActsLabel;
		private Text sourceActsText;
		private String[] sourceActs = new String[0];

		public String[] getSourceActs() {
			return sourceActs;
		}

		private Label targetActsLabel;
		private Text targetActsText;
		private String[] targetActs = new String[0];

		public String[] getTargetActs() {
			return targetActs;
		}

		private Label srcAvailableList;
		private Label targetAvailableList;

		private Button cdfCheckButton;
		private Button pdfCheckButton;

		public boolean getCdfChecked() {
			return cdfCheckButton.getSelection();
		}

		public boolean getPdfChecked() {
			return pdfCheckButton.getSelection();
		}

		private Listener commonListener = new Listener() {
			public void handleEvent(Event event) {
				validate();
			}
		};

		private IDialogSettings settings;

		protected SettingPage(String pageName) {
			super(pageName);
			this.setTitle("Passage-Time");
			this.setDescription("Select options for the passage-time analysis");

		}

		private HashSet<String> getModelActionsSet() {
			StaticAnalyser staticAnalyser = new StaticAnalyser(model.getAST());
			IAlphabetProvider alphabetProv = staticAnalyser
					.getAlphabetProvider();
			HashSet<String> actionSet = alphabetProv.getModelAlphabet();

			return actionSet;
		}

		/*
		 * This can also be used with the just the empty hashset as an argument
		 * to get the available actions as string in comma separated format.
		 */
		private String getAvailableActionString(HashSet<String> exempts) {
			HashSet<String> modelActions = getModelActionsSet();
			// Remove all those in the exempt list
			for (String action : exempts) {
				modelActions.remove(action);
			}

			Iterator<String> actionIter = modelActions.iterator();

			String result = new String();
			while (actionIter.hasNext()) {
				String next = actionIter.next();
				result = result.concat(next);
				if (actionIter.hasNext()) {
					result = result.concat(", ");
				}
			}

			return result;
		}

		public void createControl(Composite parent) {
			int textStyle = SWT.SINGLE | SWT.LEFT | SWT.BORDER;
			Composite outerRows = new Composite(parent, SWT.NONE);
			outerRows.setLayout(new GridLayout(1, false));
			setControl(outerRows);
			
			Composite composite = new Composite(outerRows, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			

			String availableActions = new String();
			HashSet<String> actionSet = getModelActionsSet();

			for (String actionName : actionSet) {
				availableActions = availableActions.concat(actionName);
				// System.out.println("here it is" + actionName);
			}

			/* The start-time */
			startTimeLabel = new Label(composite, SWT.CHECK);
			startTimeLabel.setText("Start Time");
			startTimeLabel.setLayoutData(createDefaultGridData());

			startTimeText = new Text(composite, textStyle);
			startTimeText.setLayoutData(createDefaultGridData());
			startTimeText.addListener(SWT.Modify, commonListener);

			/* The time-step */
			timeStepLabel = new Label(composite, SWT.CHECK);
			timeStepLabel.setText("Time Step");
			timeStepLabel.setLayoutData(createDefaultGridData());

			timeStepText = new Text(composite, textStyle);
			timeStepText.setLayoutData(createDefaultGridData());
			timeStepText.addListener(SWT.Modify, commonListener);

			/* The stop-time */
			stopTimeLabel = new Label(composite, SWT.CHECK);
			stopTimeLabel.setText("Stop Time");
			stopTimeLabel.setLayoutData(createDefaultGridData());

			stopTimeText = new Text(composite, textStyle);
			stopTimeText.setLayoutData(createDefaultGridData());
			stopTimeText.addListener(SWT.Modify, commonListener);

			/* The source condition */
			sourceActsLabel = new Label(composite, SWT.CHECK);
			sourceActsLabel.setText("Source Actions");
			sourceActsLabel.setLayoutData(createDefaultGridData());

			sourceActsText = new Text(composite, textStyle);
			sourceActsText.setLayoutData(createDefaultGridData());
			sourceActsText.addListener(SWT.Modify, commonListener);

			/* The target condition */
			targetActsLabel = new Label(composite, SWT.CHECK);
			targetActsLabel.setText("Target Actions");
			targetActsLabel.setLayoutData(createDefaultGridData());

			targetActsText = new Text(composite, textStyle);
			targetActsText.setLayoutData(createDefaultGridData());
			targetActsText.addListener(SWT.Modify, commonListener);

			/*
			 * This code is a simple attempt to have a list of the available
			 * actions
			 */
			int marginLeft = 20;
			Group availActionsGroup = new Group(outerRows, SWT.DEFAULT);
			availActionsGroup.setText("Available Action Names");
			GridLayout availLayout = new GridLayout();
			availLayout.numColumns = 2;
			availLayout.marginLeft = marginLeft;
			availActionsGroup.setLayout(availLayout);
			availActionsGroup.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));
			availActionsGroup.setEnabled(true);

			HashSet<String> exempts = new HashSet<String>();
			Label srcAvailableLabel = new Label(availActionsGroup, SWT.CHECK);
			srcAvailableLabel.setText("Available source actions:");
			srcAvailableLabel.setLayoutData(createDefaultGridData());
			srcAvailableList = new Label(availActionsGroup, SWT.CHECK);
			srcAvailableList.setText(getAvailableActionString(exempts));
			srcAvailableList.setLayoutData(createDefaultGridData());

			Label targetAvailableLabel = new Label(availActionsGroup, SWT.CHECK);
			targetAvailableLabel.setText("Available target actions:");
			targetAvailableLabel.setLayoutData(createDefaultGridData());
			targetAvailableList = new Label(availActionsGroup, SWT.CHECK);
			targetAvailableList.setText(getAvailableActionString(exempts));
			targetAvailableList.setLayoutData(createDefaultGridData());

			/* Now the check boxes for the cdf and pdf graphs */
			Composite graphChecks = new Composite(composite, SWT.NONE);
			graphChecks.setLayout(new GridLayout(1, false));
			
			cdfCheckButton = new Button(graphChecks, SWT.CHECK);

			cdfCheckButton.setText("Graph CDF");
			cdfCheckButton.setLayoutData(createDefaultGridData());
			cdfCheckButton.addListener(SWT.Selection, commonListener);
			cdfCheckButton.setSelection(true);

			pdfCheckButton = new Button(graphChecks, SWT.CHECK);

			pdfCheckButton.setText("Graph PDF");
			pdfCheckButton.setLayoutData(createDefaultGridData());
			pdfCheckButton.addListener(SWT.Selection, commonListener);
			pdfCheckButton.setSelection(false);

			initContents();
			validate();

		}

		private void updateAvailableActions(String[] srcActs, String[] tgtActs) {
			// Now the set of source actions available are those in
			// the model minus those already in the source actions:
			HashSet<String> exempts = new HashSet<String>();
			for (String action : srcActs) {
				exempts.add(action);
			}
			srcAvailableList.setText(getAvailableActionString(exempts));
			// Note that the target actions should include the source
			// actions since any action may not be specified as both.
			for (String action : tgtActs) {
				exempts.add(action);
			}
			targetAvailableList.setText(getAvailableActionString(exempts));
		}

		private void initContents() {
			/* Should init from default settings */
			IDialogSettings uiSettings = uk.ac.ed.inf.pepa.eclipse.ui.Activator
					.getDefault().getDialogSettings();
			settings = uiSettings.getSection(SECTION_NAME);
			if (settings == null)
				settings = uiSettings.addNewSection(SECTION_NAME);

			// Set the start time to the default
			startTimeText.setText("0.1");
			// Set the time step to the default
			timeStepText.setText("0.1");
			// Set the stop time to the default
			stopTimeText.setText("10.0");
			// Set the source condition to the default
			// it might be nice to actually look at the model
			// and see if we could get a default from there
			// sourceActsText.setText( Should either be the empty
			// string or if we can glean the actions from the model
			// we could just choose the first one for the source
			// and the second for the target).
			sourceActsText.setText("");
			// Set the target condition to the default, a bit
			// more difficult to see how we could set a default
			// by looking at the model.
			// Similarly for the above
			// targetActsText.setText(targetActs);
			targetActsText.setText("");
		}

		public void validate() {
			this.setErrorMessage(null);
			this.setPageComplete(false);
			boolean isComplete = true;
			double start, step, stop;

			// First of all we wish to update the list of available
			// action names, we do this first since otherwise we may
			// return before we get to it.
			String splitRegExp = "(\\s)*,(\\s)*";
			String sourceActsString = sourceActsText.getText().trim();
			sourceActs = sourceActsString.split(splitRegExp);

			String targetActsString = targetActsText.getText().trim();
			targetActs = targetActsString.split(splitRegExp);

			updateAvailableActions(sourceActs, targetActs);

			// Check the stop time is a valid number greater
			// than zero and if so record its time.
			try {
				start = Double.parseDouble(startTimeText.getText().trim());
				if (start < 0.0) {
					this.setErrorMessage("Start time must be positive");
					isComplete = false;
					return;
				} else {
					startTime = start;
				}

			} catch (NumberFormatException nfe) {
				this.setErrorMessage("Start time is not a valid double");
				isComplete = false;
				return;
			}

			// Check the time step is a valid number greater than zero
			// and if so record its value
			try {
				step = Double.parseDouble(timeStepText.getText().trim());
				if (step <= 0.0) {
					this.setErrorMessage("Time Step must be positive");
					isComplete = false;
					return;
				} else {
					timeStep = step;
				}

			} catch (NumberFormatException nfe) {
				this.setErrorMessage("Time Step is not a valid double");
				isComplete = false;
				return;
			}

			// Check the stop time is a valid number greater than zero
			// and if so record its value
			// Note we could do a bit more checking such as is the stop
			// time greater than the start time.
			try {
				stop = Double.parseDouble(stopTimeText.getText().trim());
				if (stop <= 0.0) {
					this.setErrorMessage("Stop time must be positive");
					isComplete = false;
					return;
				} else {
					stopTime = stop;
				}

			} catch (NumberFormatException nfe) {
				this.setErrorMessage("Stop time is not a valid double");
				isComplete = false;
				return;
			}

			// Get the actions actually done by the model so that
			// we can check the source and target sets against them.
			HashSet<String> actionSet = getModelActionsSet();

			// Validating the source and target conditions, clearly
			// we would like to be able to look at the model and
			// tell which conditions at least represent valid filters
			// We've already updated the source and target actions
			// The first thing we do is quickly check that neither the
			// source or target action list is empty.
			if (sourceActsString.length() == 0) {
				this.setErrorMessage("Source actions are empty");
				isComplete = false;
				return;
			}

			if (targetActsString.length() == 0) {
				this.setErrorMessage("Target actions are empty");
				isComplete = false;
				return;
			}

			// Now we check if any of the source strings are NOT
			// performed by the model.
			for (String actionName : sourceActs) {
				if (!actionSet.contains(actionName)) {
					this.setErrorMessage("The source action: " + actionName
							+ " is not performed by the model");
					isComplete = false;
					return;
				}
			}

			// Now we check if any of the target strings are NOT
			// performed by the model
			for (String actionName : targetActs) {
				if (!actionSet.contains(actionName)) {
					this.setErrorMessage("The target action: " + actionName
							+ " is not performed by the model");
					isComplete = false;
					return;
				}
			}

			// Finally check that the source and target sets of
			// actions do not overlap, this is because a state cannot
			// be both a target and a source state so to specify an
			// action as both does not make sense.
			for (String sAction : sourceActs) {
				for (String tAction : targetActs) {
					if (sAction.equals(tAction)) {
						this.setErrorMessage(sAction
								+ " cannot be both a source and target action");
						isComplete = false;
						return;
					}
				}
			}

			if (!cdfCheckButton.getSelection()
					&& !pdfCheckButton.getSelection()) {
				this
						.setErrorMessage("You must graph at least either the cdf or the pdf");
				isComplete = false;
				return;
			}

			// So if we get past all of the above without setting
			// the 'isComplete' variable to false then the page is
			// indeed complete.
			this.setPageComplete(isComplete);
		}

		/*
		 * I believe this is basically what the default does? public boolean
		 * canFlipToNextPage() { return this.isPageComplete(); }
		 */

		private GridData createDefaultGridData() {
			/* ...with grabbing horizontal space */
			return new GridData(SWT.FILL, SWT.CENTER, true, false);
		}

	}

	private class PassageExperimentPage extends WizardPage {
		private String[] allModelRateNames;
		private Text rateValuesText;
		private Button[] rateCheckBoxes;

		protected PassageExperimentPage(String pageName, String[] allRates) {
			super(pageName);
			this.setTitle("Passage-Time Experimentation");
			this.setDescription("Set up a passage-time experiment");
			this.allModelRateNames = allRates;
		}

		public String getSelected() {
			for (int i = 0; i < rateCheckBoxes.length; i++) {
				if (rateCheckBoxes[i].getSelection()) {
					return allModelRateNames[i];
				}
			}
			return null; // Okay okay should throw an exception
		}

		public void createControl(Composite parent) {
			int textStyle = SWT.SINGLE | SWT.LEFT | SWT.BORDER;
			Composite composite = new Composite(parent, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));
			setControl(composite);

			/* Just a small label to say what to do */
			Label tmpLabel = new Label(composite, SWT.CHECK);
			tmpLabel.setText("Please select the rate you wish to modify?");
			tmpLabel.setLayoutData(createDefaultGridData());

			Composite checksParent = new Composite (composite, SWT.NONE);
			checksParent.setLayout(new GridLayout(1, false));
		
			rateCheckBoxes = new Button[allModelRateNames.length];

			for (int i = 0; i < allModelRateNames.length; i++) {
				String rateName = allModelRateNames[i];

				Button checkBox = new Button(checksParent, SWT.CHECK);
				rateCheckBoxes[i] = checkBox;

				checkBox.setText(rateName);
				checkBox.setLayoutData(createDefaultGridData());
				checkBox.addListener(SWT.Selection, commonListener);
			}

			Label rateValuesLabel = new Label (composite, SWT.NONE);
			rateValuesLabel.setText("Comma separated list of rate values (eg 1.0)");
			rateValuesLabel.setLayoutData(createDefaultGridData());
			
			rateValuesText = new Text(composite, textStyle);
			rateValuesText.setLayoutData(createDefaultGridData());
			rateValuesText.addListener(SWT.Modify, commonListener);

			validate();

		}

		private Listener commonListener = new Listener() {
			public void handleEvent(Event event) {
				validate();
			}
		};

		private void validate() {
			this.setPageComplete(false);
			this.setErrorMessage(null);
			int checked = 0;
			for (Button checkBox : rateCheckBoxes) {
				if (checkBox.getSelection()) {
					checked++;
				}
			}

			// First of all if no things are checked then
			// then we will perform no experimentation this
			// may result from the user pressing Finish before
			// they have even clicked next to the experimentation page.
			if (checked == 0) {
				this.setPageComplete(true);
			} else if (checked > 1) {
				this.setPageComplete(false);
				this
						.setErrorMessage("Currently we can only range over one rate");
				return;
			} else if (getRateValues().length == 0) {
				this.setPageComplete(false);
				this
						.setErrorMessage("If you are ranging over a rate you must provide values");
				return;
			} else {
				this.setPageComplete(true);
				return;
			}
		}

		public double[] getRateValues() {
			String[] values = rateValuesText.getText().trim().split("(\\s)*,(\\s)*");
			double[] errorResult = new double[0];
			if (values.length == 0)
				return errorResult;
			double[] list = new double[values.length];
			for (int i = 0; i < values.length; i++) {
				try {
					list[i] = Double.parseDouble(values[i]);
					if (list[i] <= 0)
						return errorResult;
					/*
					 * Now check that each value is less than the subsequent
					 * others
					 */
					if (i > 0)
						if (list[i] <= list[i - i])
							return errorResult;
				} catch (Exception e) {
					return errorResult;
				}
			}

			return list;
		}

		private GridData createDefaultGridData() {
			/* ...with grabbing horizontal space */
			return new GridData(SWT.FILL, SWT.CENTER, true, false);
		}
	}

	private IPepaModel model;
	private SettingPage settingPage;
	private PassageExperimentPage experimentPage;

	private String[] getModelRateNames() {
		ModelNode modelNode = model.getAST();
		RateDefinitions rateDefs = modelNode.rateDefinitions();
		LinkedList<String> result = new LinkedList<String>();
		for (RateDefinitionNode rateDef : rateDefs) {
			result.add(rateDef.getName().getName());
		}
		return result.toArray(new String[result.size()]);
	}

	/**
	 * The underlying resource for this model.
	 * 
	 * @param modelResource
	 *            the underlying resource for this model
	 * @throws NullPointerException
	 *             if model in <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the model's AST is not correct
	 */
	public PassageTimeWizard(IPepaModel model) {
		if (model == null)
			throw new NullPointerException();
		if (!model.isDerivable())
			throw new IllegalArgumentException("The model is not derivable");
		this.model = model;
		this.setForcePreviousAndNextButtons(true);
		this.setNeedsProgressMonitor(true);

		// Obtain all the rate names from the model to use
		// in the experimentation page
		String[] allRates = getModelRateNames();
		// Create the pages used in the wizard ready to be added
		settingPage = new SettingPage("Passage-Time Analysis");
		experimentPage = new PassageExperimentPage("Experimentation", allRates);
	}

	public void createPageControls(Composite parent) {
		super.createPageControls(parent);
		// this.getShell().setSize(400, 400);
	}

	public void addPages() {
		addPage(settingPage);
		addPage(experimentPage);
	}

	// Are all of the pages complete, then we can finish
	public boolean canFinish() {
		if (settingPage.isPageComplete() == false) {
			return false;
		}
		if (experimentPage.isPageComplete() == false) {
			return false;
		}
		return true;
	}

	private PassageTimeResults rateOverridePassageTime(String rateName,
			double newRateValue) throws IOException, DerivationException {

		ModelNode revisedModel = (ModelNode) ASTSupport.copy(model.getAST());
		String resultsName = rateName + " = " + newRateValue;

		// Apply the change
		boolean found = false;
		for (RateDefinitionNode rd : revisedModel.rateDefinitions()) {
			if (rd.getName().getName().equals(rateName)) {
				// change value
				RateDoubleNode newRate = ASTFactory.createRate();
				newRate.setValue(newRateValue);
				rd.setRate(newRate);
				found = true;
				break;
			}
		}
		if (!found)
			throw new IllegalArgumentException("Setting not found");

		return evaluatePassageTime(resultsName, resultsName, revisedModel);
	}

	private PassageTimeResults evaluatePassageTime(String cdfName,
			String pdfName, ModelNode modelNode) throws IOException,
			DerivationException {

		PepaJHydra passageSolver = new PepaJHydra("myfile.pepa", modelNode);
		passageSolver.setStartTime(settingPage.getStartTime());
		passageSolver.setTimeStep(settingPage.getTimeStep());
		passageSolver.setStopTime(settingPage.getStopTime());
		passageSolver.setSourceActions(settingPage.getSourceActs());
		passageSolver.setTargetActions(settingPage.getTargetActs());
		PassageTimeResults ptResults = passageSolver.performPassageTime(
				cdfName, pdfName);

		return ptResults;
	}

	private void drawPassageGraphs(PassageTimeResults[] ptResultsArray) {
		// Now create the graphs (depending on the state of the checkbuttons)
		if (settingPage.getCdfChecked()) {
			InfoWithAxes info = new InfoWithAxes();
			info.setXSeries(Series.create(ptResultsArray[0].getTimePoints(),
					"Time"));
			info.setShowLegend(true);
			info.setYLabel("Probability");
			info
					.setGraphTitle("Passage-time analysis Cumulative Distribution Function");
			info.setHas3DEffect(false);
			info.setShowMarkers(false);

			for (PassageTimeResults ptResults : ptResultsArray) {
				info.getYSeries().add(
						Series.create(ptResults.getCdfPoints(), ptResults
								.getCdfName()));
			}

			IChart chart = Plotting.getPlottingTools().createTimeSeriesChart(
					info);
			// I copied the code for drawing graphs from Adam's 'AnalysisJob'
			// code
			// which creates graphs from a time series analysis.
			// This line was in it but I basically don't know what it does.
			// chart.setSemanticElement(new ResultsAdapter());
			uk.ac.ed.inf.common.ui.plotview.PlotViewPlugin.getDefault().reveal(
					chart);
		}

		// The pdf is just as easy
		if (settingPage.getPdfChecked()) {
			InfoWithAxes infoPdf = new InfoWithAxes();
			infoPdf.setXSeries(Series.create(ptResultsArray[0].getTimePoints(),
					"Time"));
			infoPdf.setShowLegend(true);
			infoPdf.setYLabel("Probability Density");
			infoPdf
					.setGraphTitle("Passage-time analysis Probability Density Function");
			infoPdf.setHas3DEffect(false);
			infoPdf.setShowMarkers(false);

			for (PassageTimeResults ptResults : ptResultsArray) {
				infoPdf.getYSeries().add(
						Series.create(ptResults.getPdfPoints(), ptResults
								.getPdfName()));
			}

			IChart chartPdf = Plotting.getPlottingTools()
					.createTimeSeriesChart(infoPdf);
			// I copied the code for drawing graphs from Adam's 'AnalysisJob'
			// code
			// which creates graphs from a time series analysis.
			// This line was in it but I basically don't know what it does.
			// chart.setSemanticElement(new ResultsAdapter());
			uk.ac.ed.inf.common.ui.plotview.PlotViewPlugin.getDefault().reveal(
					chartPdf);
		}
	}

	@Override
	public boolean performFinish() {
		try {
			double[] values = experimentPage.getRateValues();
			String selectedRate = experimentPage.getSelected();

			// If we are not doing an experiment then
			// just do a single passage-time without any
			// rate overriding.
			if (values.length == 0 || selectedRate == null) {
				PassageTimeResults[] ptResultsArray = new PassageTimeResults[1];
				ptResultsArray[0] = evaluatePassageTime("cdf", "pdf", model
						.getAST());
				drawPassageGraphs(ptResultsArray);
			} else {
				// Otherwise we do a one result for each NEW model
				// where a new model is got by overriding the selected
				// rate with the successive values given.
				PassageTimeResults[] ptResultsArray = new PassageTimeResults[values.length];
				for (int i = 0; i < values.length; i++) {
					ptResultsArray[i] = rateOverridePassageTime(selectedRate,
							values[i]);
				}
				drawPassageGraphs(ptResultsArray);
			}

		} catch (IOException e) {
			e.printStackTrace();
			Shell activeShell = Display.getDefault().getActiveShell();
			MessageDialog.openError(activeShell, "Error during passage-time", e
					.getCause().getMessage());
		} catch (DerivationException e) {
			e.printStackTrace();
			Shell activeShell = Display.getDefault().getActiveShell();
			MessageDialog.openError(activeShell,
					"Error derivation of state space", e.getCause()
							.getMessage());
		}
		// System.out.println ("Hello passage-time world!");
		return true;
	}
}
