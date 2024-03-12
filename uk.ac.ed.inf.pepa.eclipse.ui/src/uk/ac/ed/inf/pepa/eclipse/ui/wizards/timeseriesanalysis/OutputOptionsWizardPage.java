/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis;

import java.util.*;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis.SpeciesSelectionWizardPage.SpeciesSelectionEvent;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis.SpeciesSelectionWizardPage.SpeciesSelectionListener;

/**
 * 
 * @author ajduguid
 * 
 */
public class OutputOptionsWizardPage extends WizardPage implements
		SpeciesSelectionListener {

	private class ComponentTableLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@SuppressWarnings("unchecked")
		public String getColumnText(Object element, int columnIndex) {
			Map.Entry<TreeItem, String> me = (Map.Entry<TreeItem, String>) element;
			if (columnIndex == 0)
				return me.getKey().getText();
			else if (columnIndex == 1)
				return me.getValue();
			else if (column3Visible) {
				StringBuilder sb = new StringBuilder();
				for (String s : amalgamatedComponents
						.get(me.getKey().getText()))
					sb.append(s).append(" + ");
				sb.delete(sb.length() - 3, sb.length());
				return sb.toString();
			}
			return "";
		}
	}

	/**
	 * 
	 * @author ajduguid
	 * 
	 */
	class Graph {
		Map<String, Set<String>> amalgamations;

		private HashMap<TreeItem, String> components;

		private TreeItem graphTreeItem;

		Map<String, String> nameMap;

		ArrayList<String> ordering;

		String title, xAxis, yAxis;

		Graph(TreeItem treeItem) {
			graphTreeItem = treeItem;
			components = new HashMap<TreeItem, String>();
			nameMap = new HashMap<String, String>();
			xAxis = "Time";
			yAxis = "Population sizes";
			title = treeItem.getText();
		}

		/**
		 * 
		 * @param treeItem
		 */
		private void addComponent(TreeItem treeItem) {
			if (!components.containsKey(treeItem)) {
				String name = treeItem.getText();
				components.put(treeItem, name);
				nameMap.put(name, name);
			}
		}

		/**
		 * 
		 * @param tableItems
		 */
		private void addSelection(TableItem[] tableItems) {
			String name;
			int i, index;
			TreeItem[] treeItems;
			for (TableItem tableItem : tableItems) {
				name = tableItem.getText();
				if (!nameMap.containsKey(name)) {
					treeItems = graphTreeItem.getItems();
					i = OutputOptionsWizardPage.this.ordering.indexOf(name);
					for (index = 0; index < treeItems.length; index++)
						if (i < OutputOptionsWizardPage.this.ordering
								.indexOf(treeItems[index].getText()))
							break;
					TreeItem child = new TreeItem(graphTreeItem, SWT.NONE,
							index);
					child.setText(name);
					addComponent(child);
				}
			}
		}

		/**
		 * 
		 * @param treeItem
		 */
		private void removeComponent(TreeItem treeItem) {
			if (components.containsKey(treeItem)) {
				nameMap.remove(treeItem.getText());
				components.remove(treeItem);
			}
		}

		/**
		 * 
		 * @param treeItem
		 * @param newLabel
		 */
		private void updateLabel(TreeItem treeItem, String newLabel) {
			if (components.containsKey(treeItem)) {
				components.put(treeItem, newLabel);
				nameMap.put(treeItem.getText(), newLabel);
			}
		}
	}

	public static final String name = "OutputOptions";

	boolean add, first, booleanSaveCMDL, booleanSaveResult, column3Visible,
			saveCMDLPossible;

	Map<String, Set<String>> amalgamatedComponents;

	private Font bold;

	Table componentTable;

	Graph currentGraph;

	ArrayList<Graph> graphs;

	TableViewer graphTable;

	Tree graphTree;

	ArrayList<String> ordering;

	Text resultsFileText, title, xAxis, yAxis;

	Button saveCMDL, saveResult;

	String stringSaveResult;

	protected OutputOptionsWizardPage() {
		super(name);
		graphs = new ArrayList<Graph>();
		FontData boldFontData = getFont().getFontData()[0];
		boldFontData.setStyle(SWT.BOLD);
		bold = new Font(getFont().getDevice(), new FontData[] { boldFontData });
		first = true;
		booleanSaveCMDL = booleanSaveResult = false;
		saveCMDLPossible = true;
		setTitle(WizardMessages.OUTPUT_OPTIONS_WIZARD_PAGE_TITLE);
		setDescription(WizardMessages.OUTPUT_OPTIONS_WIZARD_PAGE_DESCRIPTION);
	}

	void checkPage() {
		boolean allUsed = true;
		setPageComplete(!allUsed);
		HashSet<String> selectedSoFar = new HashSet<String>();
		for (Graph graph : graphs)
			for (TreeItem treeItem : graph.components.keySet())
				selectedSoFar.add(treeItem.getText());
		for (TableItem tableItem : componentTable.getItems())
			if (!selectedSoFar.contains(tableItem.getText())) {
				tableItem.setFont(bold);
				allUsed = false;
			} else {
				tableItem.setFont(getFont());
			}
		setPageComplete(allUsed);
		setErrorMessage((allUsed ? null
				: "Highlighted components in list are not associated with any defined graph."));
	}

	public void createControl(Composite parent) {
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FormLayout());
		// Disk options
		Group outputOptionsGroup = createDiskGroup(mainComposite);
		// Graph options
		Group graphOptionsGroup = createGraphGroup(mainComposite);
		// Layout
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		outputOptionsGroup.setLayoutData(formData);
		formData = new FormData();
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.top = new FormAttachment(outputOptionsGroup);
		formData.bottom = new FormAttachment(100);
		graphOptionsGroup.setLayoutData(formData);
		// mainComposite.layout();
		setControl(mainComposite);
	}

	/**
	 * Ordering of components is fragile due to inter-communication
	 * 
	 * @param parent
	 * @return
	 */
	private Group createDiskGroup(Composite parent) {
		// Group
		Group outputOptionsGroup = new Group(parent, SWT.NONE);
		outputOptionsGroup.setLayout(new FormLayout());
		outputOptionsGroup.setText("Disk Options");
		// Widget generation
		saveCMDL = new Button(outputOptionsGroup, SWT.CHECK);
		saveCMDL.setText("Save model in CMDL format");
		saveCMDL
				.setToolTipText("Chemical Model Definition Language. A format compatible with Dizzy.");
		saveCMDL.setEnabled(saveCMDLPossible);
		saveResult = new Button(outputOptionsGroup, SWT.CHECK);
		saveResult.setText("Save results to disk");
		saveResult
				.setToolTipText("Results are saved as CSV using the 2 character string 'comma + space' as the delimiter.");
		saveResult.setEnabled(false);
		/*
		final Label fileLabel = new Label(outputOptionsGroup, SWT.NONE);
		fileLabel.setText("Location:");
		fileLabel.setEnabled(saveResult.getSelection());
		final Button browseButton = new Button(outputOptionsGroup, SWT.NONE);
		browseButton.setText("browse...");
		browseButton
				.setToolTipText("Browse to select filename and destination of results. The extension '.csv' will be used.");
		browseButton.setEnabled(saveResult.getSelection());
		*/
		final Label redirectLabel = new Label(outputOptionsGroup, SWT.NONE);
		redirectLabel.setText("Please use the 'Export to CSV' button from within the Graph View.");
		/*
		resultsFileText = new Text(outputOptionsGroup, SWT.SINGLE
				| SWT.READ_ONLY);
		resultsFileText.setText("Use browse button to select a file...");
		resultsFileText.setEnabled(saveResult.getSelection());
		/*
		// Listener generation
		saveCMDL.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				booleanSaveCMDL = ((Button) e.widget).getSelection();
			}
		});
		/*
		saveResult.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				booleanSaveResult = ((Button) e.widget).getSelection();
				resultsFileText.setEnabled(((Button) e.widget).getSelection());
				browseButton.setEnabled(((Button) e.widget).getSelection());
				fileLabel.setEnabled(((Button) e.widget).getSelection());
			}
		});
		browseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				SaveAsDialog saveDialog = new SaveAsDialog(getShell());
				IFile file = ResourceUtilities.getIFileFromText(resultsFileText
						.getText());
				saveDialog.setTitle("Please select file for Matlab");
				if (file != null)
					saveDialog.setOriginalFile(file);
				else
					saveDialog.setOriginalName("results.csv");
				saveDialog.open();
				IPath path = saveDialog.getResult().removeFileExtension()
						.addFileExtension("csv");
				if (path != null) {
					resultsFileText.setText(path.toString());
					stringSaveResult = path.toString();
				}
			}
		});*/
		// Layout
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		saveCMDL.setLayoutData(formData);
		formData = new FormData();
		formData.top = new FormAttachment(saveCMDL);
		formData.left = new FormAttachment(0);
		saveResult.setLayoutData(formData);
		/*
		formData = new FormData();
		formData.top = new FormAttachment(resultsFileText, 0, SWT.CENTER);
		formData.left = new FormAttachment(0);
		fileLabel.setLayoutData(formData);
		formData = new FormData();
		formData.top = new FormAttachment(saveResult, 10);
		formData.left = new FormAttachment(fileLabel, 10);
		formData.right = new FormAttachment(browseButton, -10);
		resultsFileText.setLayoutData(formData);
		formData = new FormData();
		formData.top = new FormAttachment(resultsFileText, 0, SWT.CENTER);
		formData.right = new FormAttachment(100);
		browseButton.setLayoutData(formData);
		*/
		formData = new FormData();
		formData.top = new FormAttachment(saveResult, 10);
		formData.left = new FormAttachment(outputOptionsGroup, 0, SWT.CENTER);
		redirectLabel.setLayoutData(formData);
		return outputOptionsGroup;
	}

	/**
	 * Ordering of components is fragile due to inter-communication. Certain
	 * method calls rely on only being called after certain initialisations in
	 * here.
	 * 
	 * @param parent
	 * @return
	 */
	private Group createGraphGroup(Composite parent) {
		// Group
		Group graphOptionsGroup = new Group(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		graphOptionsGroup.setLayout(gridLayout);
		graphOptionsGroup.setText("Graph options");
		// Widget generation
		graphTree = new Tree(graphOptionsGroup, SWT.NONE);
		int height = graphTree.getItemHeight();
		TreeItem treeItem = new TreeItem(graphTree, SWT.NONE);
		final Button modifySelection = new Button(graphOptionsGroup, SWT.NONE);
		modifySelection.setText("<<");
		modifySelection.setEnabled(false);
		add = false;
		componentTable = new Table(graphOptionsGroup, SWT.MULTI);
		Composite composite = new Composite(graphOptionsGroup, SWT.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.pack = false;
		composite.setLayout(rowLayout);
		Button addGraph = new Button(composite, SWT.CENTER);
		addGraph.setText(" + ");
		addGraph.setToolTipText("Create another graph to draw from these results");
		final Button removeGraph = new Button(composite, SWT.CENTER);
		removeGraph.setText(" - ");
		removeGraph.setToolTipText("Remove previously created graph");
		removeGraph.setEnabled(false);
		// dynamic naming of group so final
		final Group graphTableOptions = new Group(graphOptionsGroup, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		graphTableOptions.setLayout(gridLayout);
		treeItem.setText("Graph 1");
		Graph graph = new Graph(treeItem);
		graphs.add(graph);
		// Enough of the Objects are made to populate the list and first graph
		updateList();
		currentGraph = graph;
		graphTable = new TableViewer(graphTableOptions);
		Table table = graphTable.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText("Component");
		// tableColumn.pack();
		tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText("Label as will appear on graph");
		// tableColumn.pack();
		tableColumn = new TableColumn(table, SWT.LEFT);
		tableColumn.setText("Summation of");
		// tableColumn.pack();
		graphTable.setLabelProvider(new ComponentTableLabelProvider());
		graphTable.setContentProvider(new ArrayContentProvider());
		graphTable.setCellEditors(new CellEditor[] {
				new TextCellEditor(graphTable.getTable()),
				new TextCellEditor(graphTable.getTable()),
				new TextCellEditor(graphTable.getTable()) });
		graphTable.setColumnProperties(new String[] { "Name", "Label",
				"Summation" });
		graphTable.setCellModifier(new ICellModifier() {
			public boolean canModify(Object element, String property) {
				return property.equals("Label");
			}

			@SuppressWarnings("unchecked")
			public Object getValue(Object element, String property) {
				return ((Map.Entry<TreeItem, String>) element).getValue();
			}

			@SuppressWarnings("unchecked")
			public void modify(Object element, String property, Object value) {
				if (element instanceof Item)
					element = ((Item) element).getData();
				Map.Entry<TreeItem, String> me = (Map.Entry<TreeItem, String>) element;
				currentGraph.updateLabel(me.getKey(), (String) value);
				updateTableView();
			}
		});
		graphTable.setComparator(new ViewerComparator() {
			@SuppressWarnings("unchecked")
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof Item) {
					e1 = ((Item) e1).getData();
					e2 = ((Item) e2).getData();
				}
				return ((Map.Entry<TreeItem, String>) e1).getKey().getText()
						.compareTo(
								((Map.Entry<TreeItem, String>) e2).getKey()
										.getText());
			}
		});
		Label titleLabel = new Label(graphTableOptions, SWT.NONE);
		titleLabel.setText("Title:");
		title = new Text(graphTableOptions, SWT.NONE);
		Label xAxisLabel = new Label(graphTableOptions, SWT.NONE);
		xAxisLabel.setText("x-axis:");
		xAxis = new Text(graphTableOptions, SWT.NONE);
		Label yAxisLabel = new Label(graphTableOptions, SWT.NONE);
		yAxisLabel.setText("y-axis:");
		yAxis = new Text(graphTableOptions, SWT.NONE);
		// Listener generation
		graphTree.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				TreeItem treeItem = (TreeItem) e.item;
				removeGraph.setEnabled(false);
				for (Graph graph : graphs) {
					if (graph.graphTreeItem.equals(treeItem)) {
						currentGraph = graph;
						updateTableView();
						removeGraph.setEnabled(true);
						modifySelection.setEnabled(false);
						return;
					}
					if (graph.components.containsKey(treeItem)) {
						currentGraph = graph;
						updateTableView();
						modifySelection.setEnabled(true);
						return;
					}
				}
			}
		});
		FocusListener focusListener = new FocusListener() {
			public void focusGained(FocusEvent e) {
				if (e.widget == graphTree) {
					modifySelection.setText(">>");
					add = false;
					modifySelection.setEnabled(!removeGraph.isEnabled());
				} else if (e.widget == componentTable) {
					modifySelection.setText("<<");
					add = true;
					modifySelection.setEnabled(graphs.size() > 0);
				}
			}

			public void focusLost(FocusEvent e) {
			}
		};
		graphTree.addFocusListener(focusListener);
		componentTable.addFocusListener(focusListener);
		addGraph.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				TreeItem treeItem = new TreeItem(graphTree, SWT.NONE);
				treeItem.setText("Graph " + (graphs.size() + 1));
				Graph graph = new Graph(treeItem);
				graphs.add(graph);
				graphTree.setFocus();
			}
		});
		removeGraph.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				TreeItem treeItem = graphTree.getSelection()[0];
				for (Graph graph : graphs)
					if (graph.graphTreeItem.equals(treeItem)) {
						treeItem.dispose();
						graphs.remove(graph);
						break;
					}
				currentGraph = null;
				updateTableView();
				checkPage();
				graphTree.setFocus();
			}
		});
		modifySelection.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				TreeItem treeItem = graphTree.getSelection()[0];
				if (add) {
					for (Graph graph : graphs)
						if (graph.graphTreeItem.equals(treeItem)
								|| graph.components.containsKey(treeItem)) {
							graph.addSelection(componentTable.getSelection());
							graph.graphTreeItem.setExpanded(true);
							break;
						}
					componentTable.setFocus();
				} else {
					for (Graph graph : graphs)
						if (graph.components.containsKey(treeItem)) {
							graph.removeComponent(treeItem);
							treeItem.dispose();
							break;
						}
					graphTree.setFocus();
				}
				updateTableView();
				checkPage();
			}
		});
		title.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (currentGraph != null) {
					currentGraph.title = title.getText();
					currentGraph.graphTreeItem.setText(currentGraph.title);
					Group group = (Group) graphTable.getTable().getParent();
					group.setText(currentGraph.title);
				}
			}
		});
		xAxis.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (currentGraph != null)
					currentGraph.xAxis = xAxis.getText();
			}
		});
		yAxis.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (currentGraph != null)
					currentGraph.yAxis = yAxis.getText();
			}
		});
		// Layout
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = height * 5;
		graphTree.setLayoutData(gridData);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		modifySelection.setLayoutData(gridData);
		gridData = new GridData(SWT.CENTER, SWT.FILL, false, false);
		gridData.heightHint = height * 5;
		componentTable.setLayoutData(gridData);
		gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
		gridData.horizontalSpan = 3;
		composite.setLayoutData(gridData);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 3;
		graphTableOptions.setLayoutData(gridData);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan = 2;
		gridData.heightHint = height * 6;
		graphTable.getTable().setLayoutData(gridData);
		gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		titleLabel.setLayoutData(gridData);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		title.setLayoutData(gridData);
		gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		xAxisLabel.setLayoutData(gridData);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		xAxis.setLayoutData(gridData);
		gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
		yAxisLabel.setLayoutData(gridData);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		yAxis.setLayoutData(gridData);
		return graphOptionsGroup;
	}

	public void disableCMDLSave() {
		if (saveCMDL != null)
			saveCMDL.setEnabled(false);
		saveCMDLPossible = false;
	}

	/**
	 * 
	 * @return
	 */
	public List<Graph> getGraphs() {
		if (first)
			updateList();
		for (Graph graph : graphs) {
			graph.amalgamations = amalgamatedComponents;
			graph.ordering = ordering;
		}
		return graphs;
	}

	/**
	 * 
	 * @return
	 */
	boolean saveCMDL() {
		return (saveCMDL.isEnabled() && booleanSaveCMDL);
	}

	/**
	 * 
	 * @return
	 */
	String saveResults() {
		if (booleanSaveResult)
			return stringSaveResult;
		else
			return null;
	}

	public void setVisible(boolean visible) {
		first = false;
		if (visible)
			updateList();
		super.setVisible(visible);
	}

	/**
	 * Updates list of components and ensures Tree only references valid
	 * components.
	 */
	void updateList() {
		componentTable.removeAll();
		if (ordering != null) {
			TableItem tableItem;
			for (String component : ordering) {
				tableItem = new TableItem(componentTable, SWT.NONE);
				tableItem.setText(component);
			}
			HashSet<TreeItem> toRemove = new HashSet<TreeItem>();
			for (Graph graph : graphs) {
				for (TreeItem treeItem : graph.components.keySet()) {
					if (!ordering.contains(treeItem.getText()))
						toRemove.add(treeItem);
				}
				for (TreeItem treeItem : toRemove) {
					graph.removeComponent(treeItem);
					treeItem.dispose();
				}
			}
			if (first) {
				graphs.get(0).addSelection(componentTable.getItems());
			}
		}
		componentTable.getParent().layout();
		checkPage();
	}

	public void updateSelection(SpeciesSelectionEvent event) {
		amalgamatedComponents = event.amalgamations;
		column3Visible = false;
		for (Set<String> hs : amalgamatedComponents.values())
			if (hs.size() > 1)
				column3Visible = true;
		this.ordering = event.ordering;
		if (componentTable != null)
			updateList();
	}

	private final void updateTableView() {
		if (currentGraph != null) {
			graphTable.setInput(currentGraph.components.entrySet());
			for (TableColumn tableColumn : graphTable.getTable().getColumns())
				if (!tableColumn.getText().equals("Summation of")
						|| column3Visible)
					tableColumn.pack();
				else
					tableColumn.setWidth(0);
			Group group = (Group) graphTable.getTable().getParent();
			group.setText(currentGraph.title);
			title.setText(currentGraph.title);
			xAxis.setText(currentGraph.xAxis);
			yAxis.setText(currentGraph.yAxis);
			group.layout();
		} else {
			graphTable.setInput(null);
			Group group = (Group) graphTable.getTable().getParent();
			group.setText("");
			title.setText("");
			xAxis.setText("");
			yAxis.setText("");
			group.layout();
		}
	}
}
