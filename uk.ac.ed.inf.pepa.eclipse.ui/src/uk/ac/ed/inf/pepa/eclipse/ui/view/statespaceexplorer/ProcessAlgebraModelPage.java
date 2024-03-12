/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;

import uk.ac.ed.inf.pepa.ctmc.derivation.IFilterRunner;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModelChangedListener;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.core.ProcessAlgebraModelChangedEvent;
import uk.ac.ed.inf.pepa.eclipse.ui.Activator;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterManager;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterModel;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.FilterDialogs;

/**
 * A Page of the State Space Explorer containing information about a PEPA model.
 * A Default page is provided when the model doesn't have a state space derived
 * yet.
 * 
 * @author mtribast
 * 
 */
public abstract class ProcessAlgebraModelPage extends Page implements
		IProcessAlgebraModelChangedListener {

	/**
	 * The widget containing tabular representation of the model's state space
	 */
	protected TableViewer tableViewer;

	/* state space navigator action */
	Action navigateAction;

	IProcessAlgebraModel model;

	private ArrayList<Integer> filteredInput = new ArrayList<Integer>();

	private Action exportAction;

	// Import an external solution into the model
	private Action importAction;

	private Composite pgComp;

	protected Label messageLabel;

	private Action filterAction;

	private final IFilterManager filterManager;

	private IFilterModel currentConfiguration = null;

	private Action problemDescriptionAction;

	private LazyContentProvider contentProvider;

	/**
	 * Creates a new page. The message is the empty string.
	 */
	public ProcessAlgebraModelPage(IProcessAlgebraModel model) {
		this.model = model;
		this.filterManager = Activator.getDefault().getFilterManagerProvider()
				.getFilterManager(model);
		this.model.addModelChangedListener(this);
	}

	/**
	 * Dispose of this page.
	 */
	public void dispose() {
		/* removes the listener */
		this.model.removeModelChangedListener(this);
		super.dispose();
	}

	/*
	 * (non-Javadoc) Method declared on IPage.
	 */
	public void createControl(Composite parent) {
		// Message in default page of Outline should have margins
		pgComp = new Composite(parent, SWT.NONE);
		pgComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layoutTP = new GridLayout();
		layoutTP.marginWidth = 0;
		layoutTP.numColumns = 1;
		pgComp.setLayout(layoutTP);

		messageLabel = new Label(pgComp, SWT.NONE);
		messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		tableViewer = createTableViewer(pgComp);

		createActions();

		createContextMenu();

		contributeToActionBars();

	}

	protected TableViewer createTableViewer(Composite composite) {
		// tableViewer = new TableViewer(pgComp, SWT.BORDER |SWT.SINGLE |
		// SWT.FULL_SELECTION);
		TableViewer tableViewer = new TableViewer(composite, SWT.VIRTUAL
				| SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);

		final Table table = tableViewer.getTable();
		/*
		 * FIXME If headers are not visible then the columns cannot be resized
		 * on Mac
		 */
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		contentProvider = new LazyContentProvider(model, tableViewer);
		tableViewer.setLabelProvider(new StateLabelProvider(contentProvider));
		tableViewer.setContentProvider(contentProvider);
		return tableViewer;

	}

	private void createActions() {
		// state space navigator
		navigateAction = new Action() {

			public void run() {
				NavigationDialog dialog = new NavigationDialog(
						ProcessAlgebraModelPage.this,
						(Integer) ((IStructuredSelection) (tableViewer
								.getSelection())).getFirstElement());
				dialog.open();
			}

		};
		navigateAction.setToolTipText("Single Step Navigator");
		navigateAction.setText("Single Step Navigator...");

		// problem description message
		problemDescriptionAction = new Action() {
			public void run() {
				/*
				 * State current = (State) ((IStructuredSelection) (tableViewer
				 * .getSelection())).getFirstElement(); if (current != null) new
				 * StateModelInformationDialog(getSite().getShell(),
				 * current).open();
				 */
			}
		};
		problemDescriptionAction.setText("Properties");
		problemDescriptionAction
				.setToolTipText("Show properties of this state");

		exportAction = new StateSpaceExportAction(this);

		importAction = new StateSpaceImportAction(this);
	}

	private void contributeToActionBars() {
		contributeToLocalToolBar(getSite().getActionBars().getToolBarManager());
	}

	private void contributeToLocalToolBar(IToolBarManager mng) {
		mng.removeAll();
		mng.update(false);
		mng.add(exportAction);
		mng.add(importAction);
		mng.update(true);
	}

	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				fillContextMenu(manager);

			}

		});
		Menu menu = menuMgr.createContextMenu(tableViewer.getControl());
		tableViewer.getControl().setMenu(menu);
		getSite().registerContextMenu("pepaModelPage", menuMgr, tableViewer);
	}

	private void fillContextMenu(IMenuManager mgr) {
		IStructuredSelection sel = (IStructuredSelection) tableViewer
				.getSelection();
		if (!sel.isEmpty() && sel.getFirstElement() instanceof Integer) {
			mgr.add(navigateAction);
			mgr.add(new Separator());
			mgr.add(problemDescriptionAction);
		}
		mgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/*
	 * (non-Javadoc) Method declared on IPage.
	 */
	public Control getControl() {
		return pgComp;
	}

	/**
	 * 
	 */
	public static void revealStateSpace() {
		try {
			IWorkbenchWindow dw = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			dw.getActivePage().showView(
					"uk.ac.ed.inf.pepa.eclipse.ui.stateSpaceView");
		} catch (Exception e) {
			PepaLog.logError(e);
		}
	}

	/**
	 * Sets focus to a part in the page.
	 */
	public void setFocus() {
		pgComp.setFocus();
	}

	@Override
	public void makeContributions(IMenuManager menuManager,
			IToolBarManager toolBarManager, IStatusLineManager statusLineManager) {

		super.makeContributions(menuManager, toolBarManager, statusLineManager);

		filterAction = new Action() {
			public void run() {
				currentConfiguration = FilterDialogs.openFilterManagerDialog(
						filterManager, pgComp.getShell(), currentConfiguration);
				updateView();
			}

		};

		filterAction.setText("Filters...");
		filterAction
				.setToolTipText("Filter this view according to state number, "
						+ "sequential component or steady-state probability, if available");
		filterAction.setImageDescriptor(ImageManager.getInstance()
				.getImageDescriptor(ImageManager.FILTER));

		menuManager.add(filterAction);

		updateView();
	}

	private void filterElementsInDataModel() {
		filteredInput.clear();

		if (currentConfiguration == null) {
			// currentFilters = EMPTY;
			return;
		}

		IFilterRunner[] runners = new IFilterRunner[currentConfiguration
				.getFilters().length];
		for (int j = 0; j < runners.length; j++) {
			runners[j] = currentConfiguration.getFilters()[j].getRunner(model
					.getStateSpace());
		}
		for (int s = 0; s < this.model.getStateSpace().size(); s++) {
			boolean makesItThrough = true;
			for (IFilterRunner runner : runners) {
				if (!runner.select(s)) {
					makesItThrough = false;
					break;
				}
			}
			if (makesItThrough)
				filteredInput.add(s);
		}
	}

	private void updateView() {

		this.getControl().getDisplay().syncExec(new Runnable() {

			public void run() {

				updateTable();

				updateActions();

				updateMessageLabel();
			}
		});

	}

	/* refactored out of updateView */
	protected void updateMessageLabel() {

		if (model.getStateSpace() == null) {
			messageLabel.setText("");
		} else {
			int size = this.model.getStateSpace().size();
			if (currentConfiguration != null) {
				String text = filteredInput.size() + " states"
						+ " (filtered from " + size + " states)";
				if (model.isSolved())
					text += " Probability: " + getShownTotalProbability();
				messageLabel.setText(text);

			} else {
				messageLabel.setText(size + " states");
			}

		}

	}

	/**
	 * Calculates the total probability of the data model
	 * 
	 * @return the total probability
	 */
	protected double getShownTotalProbability() {
		// TODO At some point the probability may not be the last one
		// Think about a more clever way of accessing this information
		double result = 0.0;
		for (int i : filteredInput) {
			result += this.model.getStateSpace().getSolution(i);
		}
		return result < 0 ? 0 : result; // if sums -1 for some reason
	}

	/* refactored out of updateView */
	private void updateActions() {

		filterAction.setEnabled(model.getStateSpace() != null);
		exportAction.setEnabled(model.getStateSpace() != null);
		importAction.setEnabled(model.getStateSpace() != null);
	}

	/* refactored out of updateView */
	private void updateTable(/* IStateModel[] dataModel */) {

		IStateSpace ss = this.model.getStateSpace();
		if (model.getStateSpace() != null)
			filterElementsInDataModel();
		tableViewer.setInput(currentConfiguration != null ? filteredInput
				: null);
		/* Determine the number of columns to be shown */
		if (ss != null) {
			// must consider state number, sequential components and
			// possible solution
			// IStateModel firstProcess = dataModel[0];

			int totalNumberOfColumns = (!ss.isSolutionAvailable()) ? ss
					.getMaximumNumberOfSequentialComponents() + 1 : 2 + ss
					.getMaximumNumberOfSequentialComponents();
			int alreadyShownColumns = tableViewer.getTable().getColumnCount();

			if (totalNumberOfColumns != alreadyShownColumns) {
				if (totalNumberOfColumns < alreadyShownColumns) {
					/* remove some columns */
					for (int i = alreadyShownColumns - 1; i >= totalNumberOfColumns; i--)
						/* TODO are there any other ways? */
						tableViewer.getTable().getColumns()[i].dispose();

				} else {
					/* add some columns */
					for (int i = 0; i < totalNumberOfColumns
							- alreadyShownColumns; i++) {
						/* TODO change style */
						new TableColumn(tableViewer.getTable(), SWT.LEFT);
					}

				}

				/*
				 * TIP: profiling tool!!! Resize only if the new state space has
				 * a different number of columns
				 */
				resizeControl_(tableViewer.getTable());

			}

			tableViewer.refresh();

			if (!tableViewer.getTable().isVisible())
				tableViewer.getTable().setVisible(true);

		} else {
			// no model available
			tableViewer.getTable().setVisible(false);

		}
	}

	abstract protected void resizeControl_(Table table);

	public void processAlgebraModelChanged(ProcessAlgebraModelChangedEvent event) {
		updateView();
	}

}
