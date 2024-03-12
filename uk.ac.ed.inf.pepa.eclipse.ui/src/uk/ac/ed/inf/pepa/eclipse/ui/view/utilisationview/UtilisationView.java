/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.utilisationview;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.Page;

import uk.ac.ed.inf.common.ui.plotting.Plotting;
import uk.ac.ed.inf.common.ui.plotting.data.InfoWithAxes;
import uk.ac.ed.inf.common.ui.plotting.data.InfoWithoutAxes;
import uk.ac.ed.inf.common.ui.plotview.views.PlotView;
import uk.ac.ed.inf.pepa.ctmc.LocalState;
import uk.ac.ed.inf.pepa.ctmc.PopulationLevelResult;
import uk.ac.ed.inf.pepa.ctmc.SequentialComponent;
import uk.ac.ed.inf.pepa.ctmc.ThroughputResult;
import uk.ac.ed.inf.pepa.ctmc.UtilisationResult;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModelChangedListener;
import uk.ac.ed.inf.pepa.eclipse.core.ProcessAlgebraModelChangedEvent;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;
import uk.ac.ed.inf.pepa.eclipse.ui.view.AbstractPepaPageBookView;

public class UtilisationView extends AbstractPepaPageBookView {

	@Override
	protected Page getPageFor(IProcessAlgebraEditor editor) {
		return new UtilisationPage(editor.getProcessAlgebraModel());
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		if (pageRecord != null)
			pageRecord.page.dispose();
	}

}

interface GraphCreator {

	public void createGraph(ISelection selection);

}

class ViewerMenuListener implements IMenuListener {

	private Viewer viewer;

	private Action action;

	public ViewerMenuListener(Viewer viewer, final GraphCreator creator) {
		this.viewer = viewer;
		action = new Action() {
			public void run() {
				creator.createGraph(ViewerMenuListener.this.viewer
						.getSelection());
			}
		};
		action.setText("Create Graph");
		action.setToolTipText("Create graph and reveal in Graph View");

	}

	public void menuAboutToShow(IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		if (!selection.isEmpty()) {
			manager.add(action);
			action.setEnabled(true);
		}
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}

class UtilisationPage extends Page {

	/* Comparator for population levels */
	private final Comparator<PopulationLevelResult> populationComparator = new Comparator<PopulationLevelResult>() {

		public int compare(PopulationLevelResult arg0,
				PopulationLevelResult arg1) {
			return arg0.getName().compareTo(arg1.getName());
		}

	};

	/* Comparator for throughput results */
	private final Comparator<ThroughputResult> throughputComparator = new Comparator<ThroughputResult>() {

		public int compare(ThroughputResult arg0, ThroughputResult arg1) {
			return arg0.getActionType().compareTo(arg1.getActionType());
		}

	};

	private Composite parent;

	private TabFolder tabFolder;

	private TabItem fUtilisationTab;

	private TabItem fThroughputTab;

	private TabItem fPopulationTab;

	/* For utilisation */
	private TreeViewer fUtilisationTreeViewer;

	/* For throughput */
	private TableViewer fThroughputTableViewer;

	/* For population levels */
	private TableViewer fPopulationTableViewer;

	/**
	 * If true, display utilisation information every time the model listener
	 * gets notified of a new solution
	 */
	private boolean alwaysLinkToModel = true;

	private Action linkToModelAction;

	private Action expandAllAction;

	private Action collapseAllAction;

	/**
	 * The model linked to this view
	 */
	private IProcessAlgebraModel model;

	private IProcessAlgebraModelChangedListener listener = new IProcessAlgebraModelChangedListener() {

		public void processAlgebraModelChanged(ProcessAlgebraModelChangedEvent event) {
			if (event.getType() == ProcessAlgebraModelChangedEvent.CTMC_SOLVED) {
				// only interested in new solution
				if (alwaysLinkToModel) {
					updateView();
				}
			}
		}

	};

	public UtilisationPage(IProcessAlgebraModel model) {
		if (model == null)
			throw new NullPointerException("Model cannot be null");
		this.model = model;
		// register listener
		this.model.addModelChangedListener(listener);
	}

	public void dispose() {
		// removes listener
		this.model.removeModelChangedListener(listener);
		super.dispose();
	}

	@Override
	public void createControl(Composite parent) {
		this.parent = new Composite(parent, SWT.NONE);
		this.parent.setLayout(new FillLayout());
		// create tab folder
		tabFolder = new TabFolder(this.parent, SWT.NONE);
		// create utilisation tab
		fUtilisationTab = new TabItem(tabFolder, SWT.NULL);
		fUtilisationTab.setText("Utilisation");
		fUtilisationTab.setToolTipText("Utilisation");
		Composite utilisationParent = new Composite(tabFolder, SWT.NULL);
		utilisationParent.setLayout(new FillLayout());
		fUtilisationTab.setControl(utilisationParent);
		this.fUtilisationTreeViewer = new TreeViewer(utilisationParent,
				SWT.MULTI);
		fUtilisationTreeViewer
				.setLabelProvider(new UtilisationTreeLabelProvider());
		fUtilisationTreeViewer
				.setContentProvider(new UtilisationTreeContentProvider());

		// create throughput tab
		fThroughputTab = new TabItem(tabFolder, SWT.NULL);
		fThroughputTab.setText("Throughput");
		fThroughputTab.setToolTipText("Throughput");
		Composite throughputComposite = new Composite(tabFolder, SWT.NULL);
		throughputComposite.setLayout(new FillLayout());
		fThroughputTab.setControl(throughputComposite);
		// throughput table
		Table table = new Table(throughputComposite, SWT.SINGLE
				| SWT.FULL_SELECTION);
		TableColumn actionColumn = new TableColumn(table, SWT.LEFT);
		actionColumn.setText("Action");
		TableColumn valueColumn = new TableColumn(table, SWT.LEFT);
		valueColumn.setText("Throughput");
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		fThroughputTableViewer = new TableViewer(table);
		fThroughputTableViewer.setContentProvider(new ArrayContentProvider());
		fThroughputTableViewer.setLabelProvider(new ThroughputLabelProvider());

		// create population tab
		fPopulationTab = new TabItem(tabFolder, SWT.NULL);
		fPopulationTab.setText("Population");
		fPopulationTab.setToolTipText("Population");
		Composite populationComposite = new Composite(tabFolder, SWT.NULL);
		populationComposite.setLayout(new FillLayout());
		fPopulationTab.setControl(populationComposite);
		// throughput table
		Table populationTable = new Table(populationComposite, SWT.SINGLE
				| SWT.FULL_SELECTION);
		TableColumn componentColumn = new TableColumn(populationTable, SWT.LEFT);
		componentColumn.setText("Component Name");
		TableColumn meanColumn = new TableColumn(populationTable, SWT.LEFT);
		meanColumn.setText("Population");
		populationTable.setHeaderVisible(true);
		populationTable.setLinesVisible(true);
		fPopulationTableViewer = new TableViewer(populationTable);
		fPopulationTableViewer.setContentProvider(new ArrayContentProvider());
		fPopulationTableViewer.setLabelProvider(new PopulationTableProvider());

		createActions();

		contributeToActionBars();

		createContextMenu();

		tabFolder.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				// after the fact event
				contributeToActionBars();
			}

		});

		updateView();

	}

	@Override
	public Control getControl() {
		return this.parent;
	}

	@Override
	public void setFocus() {
		this.parent.setFocus();
	}

	/**
	 * Update the view when a model changed event occurs or the user wants an
	 * update (refresh button pressed).
	 * 
	 */
	private void updateView() {
		this.getControl().getDisplay().syncExec(new Runnable() {
			public void run() {
				/*
				 * Don't know if any solution is available If so, do utilisation
				 * analysis, otherwise clear the view
				 */
				if (!model.isSolved()) {
					fUtilisationTreeViewer.setInput(null);
					fThroughputTableViewer.setInput(null);
					fPopulationTableViewer.setInput(null);
					expandAllAction.setEnabled(false);
					collapseAllAction.setEnabled(false);
				} else {
					// sort and update utilisation
					fUtilisationTreeViewer.setInput(model.getStateSpace()
							.getUtilisation());

					// sort and update throughput
					ThroughputResult[] throughputResult = model.getStateSpace()
							.getThroughput();
					Arrays.sort(throughputResult, throughputComparator);
					fThroughputTableViewer.setInput(throughputResult);
					// sort and update population levels
					PopulationLevelResult[] populationResult = model
							.getStateSpace().getPopulationLevels();
					Arrays.sort(populationResult, populationComparator);
					fPopulationTableViewer.setInput(populationResult);

					packTables();
					expandAllAction.setEnabled(true);
					collapseAllAction.setEnabled(true);
				}
			}
		});

	}

	private void packTables() {
		for (TableColumn c : fThroughputTableViewer.getTable().getColumns())
			c.pack();
		for (TableColumn c : fPopulationTableViewer.getTable().getColumns())
			c.pack();
	}

	private void createActions() {

		linkToModelAction = new Action() {
			public void run() {
				performLinkToModel();
			}
		};

		linkToModelAction.setChecked(alwaysLinkToModel);
		linkToModelAction.setText("Sync with model");
		linkToModelAction
				.setToolTipText("Keep in sync with the active PEPA model");
		linkToModelAction.setImageDescriptor(ImageManager.getInstance()
				.getImageDescriptor(ImageManager.SYNC));

		expandAllAction = new Action() {
			public void run() {
				fUtilisationTreeViewer.expandAll();
			}
		};
		expandAllAction.setText("Expand all");
		expandAllAction.setToolTipText("Expand all");
		expandAllAction.setImageDescriptor(ImageManager.getInstance()
				.getImageDescriptor(ImageManager.EXPAND_ALL));

		collapseAllAction = new Action() {
			public void run() {
				fUtilisationTreeViewer.collapseAll();
			}
		};
		collapseAllAction.setText("Collapse all");
		collapseAllAction.setToolTipText("Collapse all");
		collapseAllAction.setImageDescriptor(ImageManager.getInstance()
				.getImageDescriptor(ImageManager.COLLAPSE_ALL));
	}

	private void performLinkToModel() {
		alwaysLinkToModel = linkToModelAction.isChecked();
		if (alwaysLinkToModel)
			updateView();
	}

	private void performCreateGraphUtilisation(ISelection selection) {
		IStructuredSelection sel = (IStructuredSelection) selection;
		UtilisationResult data = (UtilisationResult) sel.getFirstElement();
		if (data instanceof LocalState)
			data = ((LocalState) data).getSequentialComponent();
		InfoWithoutAxes info = uk.ac.ed.inf.pepa.eclipse.ui.internal.Utilities
				.createGraphData((SequentialComponent) data);
		uk.ac.ed.inf.common.ui.plotting.IChart chart = Plotting
				.getPlottingTools().createPieChart(info);
		showGraphInUI(chart);
	}
	
	private void performCreateGraphThroughput() {
		ThroughputResult[] inputData = (ThroughputResult[]) fThroughputTableViewer
				.getInput();
		InfoWithAxes info = uk.ac.ed.inf.pepa.eclipse.ui.internal.Utilities
				.createGraphData(inputData);
		info.setShowLegend(true);
		uk.ac.ed.inf.common.ui.plotting.IChart chart = Plotting
				.getPlottingTools().createBarChart(info);
		showGraphInUI(chart);
	}
	
	private void performCreateGraphPopulation() {
		PopulationLevelResult[] results = (PopulationLevelResult[]) fPopulationTableViewer.getInput();
		InfoWithAxes info = uk.ac.ed.inf.pepa.eclipse.ui.internal.Utilities
			.createGraphData(results);
		info.setShowLegend(true);
		uk.ac.ed.inf.common.ui.plotting.IChart chart = Plotting
		.getPlottingTools().createBarChart(info);
		showGraphInUI(chart);

	}

	private void showGraphInUI(
			final uk.ac.ed.inf.common.ui.plotting.IChart chart) {
		getSite().getShell().getDisplay().syncExec(new Runnable() {

			public void run() {
				try {
					PlotView plotView = (PlotView) getSite().getPage()
							.showView(PlotView.ID);
					plotView.reveal(chart);
				} catch (PartInitException e) {
					ErrorDialog.openError(getSite().getShell(), "Error",
							"Error displaying graph", e.getStatus());
				}

			}

		});
	}

	private void createContextMenu() {
		registerMenu("population", fPopulationTableViewer, new GraphCreator() {
			public void createGraph(ISelection selection) {
				performCreateGraphPopulation();
			}
		});
		registerMenu("throughput", fThroughputTableViewer, new GraphCreator() {
			public void createGraph(ISelection selection) {
				performCreateGraphThroughput();
			}
		});
		registerMenu("utilisation", fUtilisationTreeViewer, new GraphCreator() {
			public void createGraph(ISelection selection) {
				performCreateGraphUtilisation(selection);
			}
		});
		
	}

	private void registerMenu(String name, Viewer viewer, GraphCreator creator) {
		MenuManager menuManager = new MenuManager(null);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new ViewerMenuListener(viewer, creator));
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}
	
	private void contributeToActionBars() {
		IActionBars actionBars = getSite().getActionBars();
		contributeToLocalToolBar(actionBars.getToolBarManager());
	}

	private void contributeToLocalToolBar(IToolBarManager mng) {
		mng.removeAll();
		mng.update(false);
		int index;
		if ((index = tabFolder.getSelectionIndex()) != -1) {
			if (tabFolder.getItem(index) == fUtilisationTab) {
				mng.add(expandAllAction);
				mng.add(collapseAllAction);
			} else {
				// TODO Throughput
			}
		}
		mng.add(new Separator());
		mng.add(linkToModelAction);
		mng.update(true);
	}
}
