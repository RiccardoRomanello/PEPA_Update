/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayComponent;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayPropertyMap;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayState;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayTransition;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.IMRMCGenerator;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.PropertyDependencyException;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.ProcessAlgebraModelChangedEvent;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.PEPAEditor;
import uk.ac.ed.inf.pepa.eclipse.ui.view.AbstractView;

/**
 * View for specifying abstractions (state space aggregation) of a PEPA model.
 * 
 * @author msmith
 */

public class AbstractionView extends AbstractView {

	/**
	 * View Components
	 */
	private Composite viewFrame;
	private CTabFolder tabFolder;
	private Button mrmcButton;
	private Button aggregateButton;
	private Button disaggregateButton;
	private Button useShortNamesButton;
	private Combo showActionCombo;
	private Combo chooseLayoutCombo;
	private Label propertyLabel;
	//private Label statusLabel;
	private Table propertyTable;
	private TableEditor propertyTableEditor;
	private DisplayOption displayOption = DisplayOption.SHOW_NONE;
	private boolean useShortNames = true;
	
	/**
	 * View Actions
	 */
	private Action addPropertyAction;
	private Action increaseSizeAction;
	private Action decreaseSizeAction;
	private ImportConfigAction importConfigAction;
	private ExportConfigAction exportConfigAction;

	/**
	 * View Data
	 */
	private SequentialComponentGraphBuilder[] builders;
	private KroneckerDisplayComponent[] displayComponents;
	private Graph[] componentViewers;
	private boolean[] applyLayoutOnSelect;
	private boolean[] displayingEntireGraph;
	
	/**
	 * Link to PEPA model
	 */
	private IPepaModel pepaModel;
	private File pepaFile = null;
	
	public String getPEPAPath() {
		if (pepaFile == null) {
			return System.getProperty("user.dir");
		} else {
			return pepaFile.getPath();
		}
	}
	
	public SequentialComponentGraphBuilder getBuilder(int index) {
		if (index >= 0 && index < builders.length) {
			return builders[index];
		} else {
			return null;
		}
	}
	
	public KroneckerDisplayComponent getDisplayComponent(int index) {
		if (index >= 0 && index < displayComponents.length) {
			return displayComponents[index];
		} else {
			return null;
		}
	}
	
	public Graph getGraph(int index) {
		if (index >= 0 && index < componentViewers.length) {
			return componentViewers[index];
		} else {
			return null;
		}
	}
	
	public boolean getApplyLayoutOnSelect(int index) {
		if (index >= 0 && index < applyLayoutOnSelect.length) {
			return applyLayoutOnSelect[index];
		} else {
			return false;
		}
	}
	
	public void setApplyLayoutOnSelect(int index, boolean applyLayout) {
		if (index >= 0 && index < applyLayoutOnSelect.length) {
			applyLayoutOnSelect[index] = applyLayout;
		}
	}
	
	public int getCurrentTab() {
		return tabFolder.getSelectionIndex();
	}
	
	public void setCurrentTab(int index) {
		if (index >= 0 && index < tabFolder.getItems().length) {
			tabFolder.setSelection(index);
		}
	}
	
	public int numComponents() {
		return displayComponents.length;
	}
	
	private void createAddPropertyAction() {
		addPropertyAction = new Action("New Property") {
			public void run() {
				int currentTab = getCurrentTab();
				KroneckerDisplayPropertyMap propertyMap = displayComponents[currentTab].getPropertyMap();
				String newName = propertyMap.addProperty();
				propertyMap.setPropertyAll(newName, false);
				TableItem property = PropertyManager.newProperty(propertyTable, SWT.NULL);
				property.setText(0, newName);
				ArrayList<KroneckerDisplayState> selection = getStates(componentViewers[currentTab].getSelection().toArray());
				if (selection.size() > 0) {
					propertyMap.setPropertyAll(newName, false);
					propertyMap.setProperty(newName, selection, true);
					PropertyManager.setTrue(property);
				} else {
					PropertyManager.setUnselected(property);
				}
				editProperty(property);
			}
		};
	}

	private void createFontSizeActions() {
		increaseSizeAction = new Action("Larger Font", Action.AS_PUSH_BUTTON) {
			public void run() {
				for (int i = 0; i < builders.length; i++) {
					builders[i].increaseFontSize();
				}
			}
		};
		increaseSizeAction.setToolTipText("Increase font size");
		increaseSizeAction.setImageDescriptor(ImageManager.getInstance().getImageDescriptor(ImageManager.ZOOM_IN));
		decreaseSizeAction = new Action("Smaller Font", Action.AS_PUSH_BUTTON) {
			public void run() {
				for (int i = 0; i < builders.length; i++) {
					builders[i].decreaseFontSize();
				}
			}
		};
		decreaseSizeAction.setToolTipText("Decrease font size");
		decreaseSizeAction.setImageDescriptor(ImageManager.getInstance().getImageDescriptor(ImageManager.ZOOM_OUT));
	}

	private void createToolBar() {
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.removeAll();
		manager.update(false);
		manager.add(exportConfigAction);
		manager.add(importConfigAction);
		manager.add(increaseSizeAction);
		manager.add(decreaseSizeAction);
		manager.update(true);
	}

	private void deleteProperty(TableItem property) {
		int currentTab = getCurrentTab();
		KroneckerDisplayPropertyMap propertyMap = displayComponents[currentTab].getPropertyMap();
		String name = property.getText(0);
		try {
			propertyMap.removeProperty(name);
			propertyTable.remove(propertyTable.indexOf(property));
			computePropertyTableWidths();
		} catch (PropertyDependencyException e) {
			MessageBox errorMessage = new MessageBox(getSite().getShell(), SWT.ICON_ERROR | SWT.OK);
			errorMessage.setText("Error");
			errorMessage.setMessage("The property \"" + name + "\" cannot be deleted, because it is being used by "
					+ "a CSL property. You must remove all references to \"" + name + "\" from the "
					+ "model checking view before you can delete it.");
			errorMessage.open();
		}
	}

	private Action createDeletePropertyAction(final TableItem selection) {
		Action deletePropertyAction = new Action("Delete") {
			public void run() {
				deleteProperty(selection);
				updateCurrentGraph();
			}
		};
		return deletePropertyAction;
	}

	private void renameProperty(TableItem property) {
		int currentTab = getCurrentTab();
		if (currentTab == -1)
			return;
		String newName = ((Text) propertyTableEditor.getEditor()).getText();
		String oldName = property.getText(0);
		String renamedProperty = displayComponents[currentTab].getPropertyMap().renameProperty(oldName, newName);
		propertyTableEditor.getItem().setText(0, renamedProperty);
		computePropertyTableWidths();
	}

	private void editProperty(final TableItem property) {
		// Clean up any previous editor control
		Control oldEditor = propertyTableEditor.getEditor();
		if (oldEditor != null)
			oldEditor.dispose();

		// The control that will be the editor must be a child of the Table
		Text newEditor = new Text(propertyTable, SWT.NONE);
		newEditor.setBackground(property.getBackground());
		newEditor.setText(property.getText(0));
		newEditor.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}

			public void focusLost(FocusEvent e) {
				renameProperty(property);
				Control oldEditor = propertyTableEditor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();
			}
		});
		newEditor.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					renameProperty(property);
					Control oldEditor = propertyTableEditor.getEditor();
					if (oldEditor != null)
						oldEditor.dispose();
				} else if (e.character == SWT.ESC) {
					Control oldEditor = propertyTableEditor.getEditor();
					if (oldEditor != null)
						oldEditor.dispose();
				}
			}
		});
		newEditor.selectAll();
		newEditor.setFocus();
		propertyTableEditor.setEditor(newEditor, property, 0);
	}

	private Action createRenamePropertyAction(final TableItem property) {
		Action renamePropertyAction = new Action("Rename") {
			public void run() {
				editProperty(property);
			}
		};
		return renamePropertyAction;
	}

	private Action createSetPropertyAction(String propertyName, boolean isChecked) {
		Action setPropertyAction = new Action(propertyName, SWT.CHECK) {
			public void run() {
				int currentTab = getCurrentTab();
				KroneckerDisplayPropertyMap propertyMap = displayComponents[currentTab].getPropertyMap();
				ArrayList<KroneckerDisplayState> selected = getStates(componentViewers[currentTab].getSelection().toArray());
				propertyMap.setProperty(getText(), selected, isChecked());
				updatePropertyTable();
			}
		};
		setPropertyAction.setChecked(isChecked);
		return setPropertyAction;
	}

	private Action createSelectAdjacentAction() {
		Action selectAdjacentAction = new Action() {
			public void run() {
				int currentTab = getCurrentTab();
				KroneckerDisplayComponent component = displayComponents[currentTab];
				ArrayList<KroneckerDisplayState> selection = getStates(componentViewers[currentTab].getSelection().toArray());
				ArrayList<KroneckerDisplayState> newSelection = new ArrayList<KroneckerDisplayState>();
				for (KroneckerDisplayTransition t : component.getTransitions()) {
					KroneckerDisplayState start = t.getStartState();
					KroneckerDisplayState end = t.getEndState();
					if (selection.contains(start) || selection.contains(end)) {
						if (!newSelection.contains(start)) {
							newSelection.add(start);
						}
						if (!newSelection.contains(end)) {
							newSelection.add(end);
						}
					}
				}
				selectStates(builders[currentTab], newSelection, true);
			}
		};
		selectAdjacentAction.setText("Select adjacent nodes");
		return selectAdjacentAction;
	}

	// create subgraph with selected nodes
	private Action createSelectedSubgraphAction() {
		Action subgraphAction = new Action() {
			public void run() {
				int currentTab = getCurrentTab();
				Object[] selection = componentViewers[currentTab].getSelection().toArray();
				displayStates(currentTab, getStates(selection));
			}
		};
		subgraphAction.setText("Only show selection");
		return subgraphAction;
	}

	private Action createDisplayAllAction() {
		Action displayAllAction = new Action() {
			public void run() {
				displayAllStates(getCurrentTab());
			}
		};
		displayAllAction.setText("Display all states");
		return displayAllAction;
	}

	private Action createSelectAction() {
		Action selectAction = new Action() {
			public void run() {
				// Find out which states to select
				int currentTab = getCurrentTab();
				SelectStatesDialog dialog = new SelectStatesDialog(Display.getCurrent().getActiveShell(),
						"Choose States",
						"Select only the following states:",
						builders[currentTab].getStates());
				if (dialog.open() != Dialog.OK) return;
				// Select the correct states
				selectStates(builders[currentTab], dialog.getSelection(), true);
			}
		};
		selectAction.setText("Choose states to select...");
		return selectAction;
	}

	private Action createSubgraphAction() {
		Action subgraphAction = new Action() {
			public void run() {
				// Find out which states to show
				int currentTab = getCurrentTab();
				SelectStatesDialog dialog = new SelectStatesDialog(Display.getCurrent().getActiveShell(),
						"Choose States",
						"Show only the following states:",
						builders[currentTab].getStates());
				if (dialog.open() != Dialog.OK) return;
				// Display the correct states
				displayStates(currentTab, dialog.getSelection());
			}
		};
		subgraphAction.setText("Choose states to display...");
		return subgraphAction;
	}

	/**
	 * Changed current tab -> swap property table
	 */
	public void switchPropertyTable() {
		int currentTab = getCurrentTab();
		propertyTable.removeAll();
		if (currentTab == -1)
			return;
		KroneckerDisplayComponent component = displayComponents[currentTab];
		String[] properties = component.getPropertyMap().getProperties();
		for (int i = 0; i < properties.length; i++) {
			TableItem property = PropertyManager.newProperty(propertyTable, SWT.NULL);
			property.setText(0, properties[i]);
		}
		updatePropertyTable();
		computePropertyTableWidths();
	}

	/**
	 * Selection changed on graph -> update property table
	 */
	private void updatePropertyTable() {
		propertyTable.deselectAll();
		int currentTab = getCurrentTab();
		if (currentTab == -1)
			return;
		Graph componentViewer = componentViewers[currentTab];
		KroneckerDisplayComponent component = displayComponents[currentTab];
		KroneckerDisplayPropertyMap propertyMap = component.getPropertyMap();
		TableItem[] properties = propertyTable.getItems();
		ArrayList<KroneckerDisplayState> selection = getStates(componentViewer.getSelection().toArray());
		if (selection.size() > 0) {
			for (int i = 0; i < properties.length; i++) {
				if (propertyMap.testProperty(properties[i].getText(0), selection, false)) {
					PropertyManager.setFalse(properties[i]);
				} else if (propertyMap.testProperty(properties[i].getText(0), selection, true)) {
					PropertyManager.setTrue(properties[i]);
				} else {
					PropertyManager.setMaybe(properties[i]);
				}
			}
		} else {
			for (int i = 0; i < properties.length; i++) {
				PropertyManager.setUnselected(properties[i]);
			}
		}
	}

	/**
	 * Selection changed on property table -> update current graph
	 */
	private void updateCurrentGraph() {
		int currentTab = getCurrentTab();
		if (currentTab == -1)
			return;
		KroneckerDisplayComponent component = displayComponents[currentTab];
		KroneckerDisplayPropertyMap propertyMap = component.getPropertyMap();
		TableItem[] properties = propertyTable.getItems();
		ArrayList<KroneckerDisplayState> selection = null;
		boolean allUnselected = true;
		for (int i = 0; i < properties.length; i++) {
			ArrayList<KroneckerDisplayState> newSelection;
			if (PropertyManager.isFalse(properties[i])) {
				newSelection = propertyMap.getStates(properties[i].getText(0), false);
				allUnselected = false;
			} else if (PropertyManager.isTrue(properties[i])) {
				newSelection = propertyMap.getStates(properties[i].getText(0), true);
				allUnselected = false;
			} else if (PropertyManager.isMaybe(properties[i])) {
				newSelection = propertyMap.getAllStates();
				allUnselected = false;
			} else {
				newSelection = propertyMap.getAllStates();
			}
			if (selection == null) {
				selection = newSelection;
			} else {
				selection.retainAll(newSelection);
			}
		}
		if (allUnselected) {
			selectStates(builders[currentTab], new ArrayList<KroneckerDisplayState>(0), false);
		} else {
			component.clearSelection();
			for (KroneckerDisplayState state : selection) {
				component.selectState(state);
			}
			selectStates(builders[currentTab], selection, false);
		}
	}

	private void updateGraphLabels(boolean applyLayout) {
		for (int i = 0; i < builders.length; i++) {
			builders[i].build(displayOption, useShortNames);
			applyLayoutOnSelect[i] = applyLayoutOnSelect[i] || applyLayout;
		}
		int tabIndex = getCurrentTab();
		if (tabIndex > -1)
			applyLayout(tabIndex);
	}

	private void applyLayout(int tabIndex) {
		if (applyLayoutOnSelect[tabIndex]) {
			componentViewers[tabIndex].applyLayout();
			applyLayoutOnSelect[tabIndex] = false;
		}
	}

	@Override protected void updateView(final IProcessAlgebraEditor editor) {
		tabFolder.getDisplay().syncExec(new Runnable() {
			public void run() {
				// Save old configuration
				saveConfiguration();
				
				// Remove everything
				CTabItem[] tabs = tabFolder.getItems();
				for (int i = 0; i < tabs.length; i++) {
					tabs[i].dispose();
				}
				aggregateButton.setEnabled(false);
				disaggregateButton.setEnabled(false);
				enableView(false);
				//statusLabel.setText("");
				KroneckerDisplayModel displayModel = null;
				displayComponents = null;
				propertyTable.removeAll();
				pepaFile = null;

				// Get the new display model
				if (editor == null)
					return;
				pepaModel = ((PEPAEditor) editor).getProcessAlgebraModel();
				if (pepaModel == null)
					return;
				displayModel = pepaModel.getDisplayModel();
				if (displayModel == null)
					return;
				int numComponents = displayModel.getNumComponents();
				displayComponents = new KroneckerDisplayComponent[numComponents];
				enableView(true);
				componentViewers = new Graph[numComponents];
				applyLayoutOnSelect = new boolean[numComponents];
				displayingEntireGraph = new boolean[numComponents];
				builders = new SequentialComponentGraphBuilder[numComponents];
				for (int i = 0; i < numComponents; i++) {
					displayComponents[i] = displayModel.getComponent(i);
					CTabItem componentTab = new CTabItem(tabFolder, SWT.NONE);
					componentTab.setText(displayComponents[i].getName());
					componentViewers[i] = new Graph(tabFolder, SWT.NONE);
					builders[i] = new SequentialComponentGraphBuilder(displayComponents[i], componentViewers[i]);
					// We build initially with short names, so we can load the configuration
					builders[i].build(displayOption, true);
					builders[i].setLayoutAlgorithm(GraphLayout.SPRING_LAYOUT);
					componentViewers[i].setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
					componentViewers[i].addSelectionListener(new GraphSelectionListener(builders[i]));
					componentViewers[i].addMouseListener(new GraphMouseListener(componentViewers[i]));
					componentTab.setControl(componentViewers[i]);
					addGraphContextMenu(componentViewers[i]);
					applyLayoutOnSelect[i] = true;
					displayingEntireGraph[i] = true;
				}
				//switchPropertyTable();
				
				// Restore configuration
				pepaFile = editor.getProcessAlgebraModel().getResource().getLocation().toFile();
				loadConfiguration();
				
				// We need to build again, in case the actions weren't initialised properly
				for (int i = 0; i < numComponents; i++) {
					builders[i].build(displayOption, useShortNames);
				}
			}
		});
	}

	private void enableView(boolean isEnabled) {
		useShortNamesButton.setEnabled(isEnabled);
		propertyLabel.setEnabled(isEnabled);
		propertyTable.setEnabled(isEnabled);
		showActionCombo.setEnabled(isEnabled);
		chooseLayoutCombo.setEnabled(isEnabled);
		increaseSizeAction.setEnabled(isEnabled);
		decreaseSizeAction.setEnabled(isEnabled);
		exportConfigAction.setEnabled(isEnabled);
		importConfigAction.setEnabled(isEnabled);
		mrmcButton.setEnabled(isEnabled);
	}
	
	private void exportMRMC() {
		FileDialog saveDialog = new FileDialog(getSite().getShell(), SWT.SAVE);
		saveDialog.setText("Export Abstract PEPA Model to MRMC");
		saveDialog.setFilterPath(getPEPAPath());
		//String[] filterExt = { "*.lab", "*.ctmdpi" };
		//saveDialog.setFilterExtensions(filterExt);
		final String fileName = saveDialog.open();
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Exporting to MRMC...", IProgressMonitor.UNKNOWN);
				MRMCThread task = new MRMCThread(fileName);
				task.start();
				while (task.isAlive() && !monitor.isCanceled()) {
					Thread.sleep(500);
					monitor.worked(500);
				}
				String labelFile = fileName + ".lab";
				String transitionFile = fileName + (task.isNondeterminstic() ? ".ctmdpi" : ".tra");
				if (monitor.isCanceled()) {
					task.interrupt();
					task.join();
					transitionFile = fileName + (task.isNondeterminstic() ? ".ctmdpi" : ".tra");
					deleteFile(labelFile);
					deleteFile(transitionFile);
					throw new InterruptedException();
				} else {
					Exception e = task.getException();
					if (e != null) {
						deleteFile(labelFile);
						deleteFile(transitionFile);
						if (e instanceof IOException) {
							MessageDialog.openError(getSite().getShell(), "Error",
									"Unable to write to files: " + labelFile + " and " + transitionFile +
									". An I/O error occurred.");
						} else if (e instanceof DerivationException) {
							MessageDialog.openError(getSite().getShell(), "Error",
									"Error exporting PEPA model to MRMC: " + e.getMessage());
						} else {
							MessageDialog.openError(getSite().getShell(), "Error",
									"Error exporting PEPA model to MRMC: Unknown error.");
						}
					}
				}
			}
		};
		ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getSite().getShell());
		try {
			monitorDialog.run(true, true, runnable);
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getSite().getShell(), "Error",
					                "Error exporting PEPA model to MRMC: see the log in the Console view for details.");
		} catch (InterruptedException e) {
			MessageDialog.openInformation(getSite().getShell(), "Operation aborted", "Operation interrupted.");
		} 
	}
	
	private void deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) file.delete();
	}
	
	private void loadConfiguration() {
		if (pepaFile == null) return;
		String configFile = getConfigFile();
		importConfigAction.run(configFile, true);
	}
	
	private void saveConfiguration() {
		if (pepaFile == null) return;
		String configFile = getConfigFile();
		exportConfigAction.run(configFile);
	}
	
	private String getConfigFile() {
		String path = pepaFile.getParent();
		String fileName = pepaFile.getName();
		int suffix = fileName.lastIndexOf('.');
		if (suffix > 0) {
			fileName = fileName.substring(0, suffix);
		}
		String configFile = path + "/" + fileName + ".pepa.config";
		return configFile;
	}
	
	@Override protected void handleModelChanged(ProcessAlgebraModelChangedEvent event) {
		if (event.getType() == ProcessAlgebraModelChangedEvent.PARSED)
			updateView(this.fEditor);
	}

	@Override public void setFocus() {
		if (tabFolder != null && !tabFolder.isDisposed()) {
			tabFolder.setFocus();
		}
	}

	@Override protected void internalCreatePartControl(Composite parent) {
		FormData formData;

		viewFrame = new Composite(parent, SWT.NONE);
		viewFrame.setLayout(new FormLayout());

		// Select layout combo
		chooseLayoutCombo = new Combo(viewFrame, SWT.READ_ONLY);
		for (GraphLayout layout : EnumSet.allOf(GraphLayout.class)) {
			chooseLayoutCombo.add(layout.toString(), layout.getIndex());
		}
		chooseLayoutCombo.setEnabled(true);
		chooseLayoutCombo.select(0);
		chooseLayoutCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				GraphLayout layout = GraphLayout.get(chooseLayoutCombo.getSelectionIndex());
				applyLayoutAlgorithm(layout);
			}
		});
		formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.bottom = new FormAttachment(100, -7);
		chooseLayoutCombo.setLayoutData(formData);
		
		// Display rates
		useShortNamesButton = new Button(viewFrame, SWT.CHECK);
		useShortNamesButton.setText("Show Rates");
		useShortNamesButton.setEnabled(true);
		useShortNamesButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

			public void widgetSelected(SelectionEvent e) {
				useShortNames = !useShortNames;
				updateGraphLabels(false);
			}
		});
		formData = new FormData();
		formData.left = new FormAttachment(chooseLayoutCombo, 5);
		formData.bottom = new FormAttachment(100, -10);
		useShortNamesButton.setLayoutData(formData);
		
		// Show action combo
		showActionCombo = new Combo(viewFrame, SWT.READ_ONLY);
		for (DisplayOption option : EnumSet.allOf(DisplayOption.class)) {
			showActionCombo.add(option.toString(), option.getIndex());
		}
		showActionCombo.setEnabled(true);
		showActionCombo.select(0);
		showActionCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				displayOption = DisplayOption.get(showActionCombo.getSelectionIndex());
				updateGraphLabels(false);
			}
		});
		formData = new FormData();
		formData.left = new FormAttachment(useShortNamesButton, 5);
		formData.bottom = new FormAttachment(100, -7);
		showActionCombo.setLayoutData(formData);

		// Export to MRMC button
		mrmcButton = new Button(viewFrame, SWT.PUSH);
		mrmcButton.setText("Export to MRMC...");
		mrmcButton.setEnabled(false);
		mrmcButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

			public void widgetSelected(SelectionEvent e) {
				exportMRMC();
			}
		});
		formData = new FormData();
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(100, -5);
		mrmcButton.setLayoutData(formData);
		
		// Disaggregate button
		disaggregateButton = new Button(viewFrame, SWT.PUSH);
		disaggregateButton.setText("Disaggregate");
		disaggregateButton.setEnabled(false);
		disaggregateButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

			public void widgetSelected(SelectionEvent e) {
				int tabIndex = getCurrentTab();
				if (tabIndex == -1)
					return;
				displayComponents[tabIndex].disaggregateSelected();
				Object[] selection = componentViewers[tabIndex].getSelection().toArray();
				setControls(displayComponents[tabIndex], getStates(selection));
			}
		});
		formData = new FormData();
		formData.right = new FormAttachment(mrmcButton, -5);
		formData.bottom = new FormAttachment(100, -5);
		disaggregateButton.setLayoutData(formData);

		// Aggregate button
		aggregateButton = new Button(viewFrame, SWT.PUSH);
		aggregateButton.setText("Aggregate");
		aggregateButton.setEnabled(false);
		aggregateButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				return;
			}

			public void widgetSelected(SelectionEvent e) {
				int tabIndex = getCurrentTab();
				if (tabIndex == -1)
					return;
				displayComponents[tabIndex].aggregateSelected();
				Object[] selection = componentViewers[tabIndex].getSelection().toArray();
				setControls(displayComponents[tabIndex], getStates(selection));
			}
		});
		formData = new FormData();
		formData.right = new FormAttachment(disaggregateButton, -5);
		formData.bottom = new FormAttachment(100, -5);
		aggregateButton.setLayoutData(formData);

		// Status label
		//statusLabel = new Label(viewFrame, SWT.SHADOW_IN);
		//statusLabel.setText("");
		//formData = new FormData();
		//formData.left = new FormAttachment(0, 5);
		//formData.right = new FormAttachment(aggregateButton, -5);
		//formData.bottom = new FormAttachment(showActionCombo, -10);
		//statusLabel.setLayoutData(formData);

		// Property label
		propertyLabel = new Label(viewFrame, SWT.NONE);
		propertyLabel.setText("Atomic Properties");
		propertyLabel.setAlignment(SWT.CENTER);
		formData = new FormData();
		// formData.left = new FormAttachment(propertyTable, 0);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, 5);
		propertyLabel.setLayoutData(formData);

		// Table of properties
		propertyTable = new Table(viewFrame, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		propertyTable.setHeaderVisible(false);
		propertyTable.setLinesVisible(false);
		TableColumn propertyColumn = new TableColumn(propertyTable, SWT.NULL);
		propertyColumn.setAlignment(SWT.LEFT);
		TableColumn valueColumn = new TableColumn(propertyTable, SWT.NULL);
		valueColumn.setAlignment(SWT.CENTER);
		propertyColumn.setWidth(90);
		valueColumn.setWidth(40);
		computePropertyTableWidths();
		propertyTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				if (propertyTable.getMenu().isVisible())
					return;
				TableItem[] selected = propertyTable.getSelection();
				if (selected.length < 1)
					return;
				TableItem selectedItem = selected[0];
				propertyTable.setData(selectedItem);
				TableItem[] allItems = propertyTable.getItems();
				for (int i = 0; i < allItems.length; i++) {
					if (allItems[i] != selectedItem) {
						PropertyManager.setUnselected(allItems[i]);
					}
				}
				PropertyManager.click(selectedItem);
				updateCurrentGraph();
				propertyTable.deselectAll();
			}

			public void widgetDefaultSelected(SelectionEvent event) {
				widgetSelected(event);
			}
		});
		addPropertyContextMenu();
		createAddPropertyAction();
		propertyTableEditor = new TableEditor(propertyTable);
		propertyTableEditor.horizontalAlignment = SWT.LEFT;
		propertyTableEditor.grabHorizontal = true;
		propertyTableEditor.verticalAlignment = SWT.CENTER;
		// propertyTableEditor.minimumWidth = 80;
		formData = new FormData();
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(propertyLabel, 5);
		formData.bottom = new FormAttachment(disaggregateButton, -5);
		propertyTable.setLayoutData(formData);

		// Tabs
		tabFolder = new CTabFolder(viewFrame, SWT.BORDER);
		tabFolder.setSimple(false);
		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				switchPropertyTable();
				int tabIndex = getCurrentTab();
				if (tabIndex == -1) return;
				applyLayout(tabIndex);
				Object[] selection = componentViewers[tabIndex].getSelection().toArray();
				setControls(displayComponents[tabIndex], getStates(selection));
				GraphLayout layout = builders[tabIndex].getLayoutAlgorithm();
				chooseLayoutCombo.select(layout.getIndex());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.bottom = new FormAttachment(disaggregateButton, -5);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(propertyTable, -5);
		tabFolder.setLayoutData(formData);

		// Re-adjust property Label width
		formData = new FormData();
		formData.left = new FormAttachment(tabFolder, 5);
		formData.right = new FormAttachment(100, -5);
		formData.top = new FormAttachment(0, 5);
		propertyLabel.setLayoutData(formData);

		// Create import and export buttons
		exportConfigAction = new ExportConfigAction(this);
		importConfigAction = new ImportConfigAction(this);
		// Create font size buttons
		createFontSizeActions();
		createToolBar();

	}
	
	public void applyLayoutAlgorithm(GraphLayout layout) {
		int tabIndex = getCurrentTab();
		if (tabIndex == -1) return;
		builders[tabIndex].setLayoutAlgorithm(layout);
		applyLayoutOnSelect[tabIndex] = true;
		applyLayout(tabIndex);
	}

	private void computePropertyTableWidths() {
		Rectangle area = propertyTable.getBounds();
		Point size = propertyTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		ScrollBar vBar = propertyTable.getVerticalBar();
		int width = area.width - (2 * propertyTable.getBorderWidth()) - 2; 
		if (size.y > area.height + propertyTable.getHeaderHeight()) {
			width -= vBar.getSize().x;
		}
		propertyTable.getColumn(0).setWidth(width - 45);
		propertyTable.getColumn(1).setWidth(45);
		//System.out.println("Computing widths: " + propertyTable.getColumn(0).getWidth() + ", " + propertyTable.getColumn(1).getWidth());
	}

	private void addPropertyContextMenu() {
		MenuManager menuMgr = new MenuManager("#PropertyPopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillPropertyContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(propertyTable);
		propertyTable.setMenu(menu);
	}

	private void fillPropertyContextMenu(IMenuManager manager) {
		TableItem selected = (TableItem) propertyTable.getData();
		TableItem[] selection = propertyTable.getSelection();
		if (selection.length > 0) {
			propertyTable.deselectAll();
			selected = selection[0];
		}
		if (tabFolder.getSelection() != null) {
			manager.add(addPropertyAction);
			if (selected != null) {
				Action renameAction = createRenamePropertyAction(selected);
				manager.add(renameAction);
				Action deleteAction = createDeletePropertyAction(selected);
				manager.add(deleteAction);
			}
		}
		propertyTable.setData(null);
	}

	private void addGraphContextMenu(Graph graph) {
		MenuManager menuMgr = new MenuManager("#GraphPopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				int tabIndex = getCurrentTab();
				if (tabIndex != -1) {
					fillGraphContextMenu(manager, componentViewers[tabIndex], displayComponents[tabIndex]);
				}
			}
		});
		Menu menu = menuMgr.createContextMenu(graph);
		graph.setMenu(menu);
	}

	// Creates the context menu for the graph
	private void fillGraphContextMenu(IMenuManager manager, Graph graph, KroneckerDisplayComponent component) {
		Object[] selection = graph.getSelection().toArray();
		if (selection.length == 0) {
			// Display the general context menu
			if (displayingEntireGraph[getCurrentTab()]) {
				Action subgraphAction = createSubgraphAction();
				manager.add(subgraphAction);
			} else {
				Action displayAllAction = createDisplayAllAction();
				manager.add(displayAllAction);
			}
			Action selectAction = createSelectAction();
			manager.add(selectAction);
		} else {
			// Display the state context menu
			ArrayList<KroneckerDisplayState> selected = getStates(selection);
			if (selected.size() == 0) return;
			
			Action showSubgraph = createSelectedSubgraphAction();
			manager.add(showSubgraph);
			Action selectAdjacent = createSelectAdjacentAction();
			manager.add(selectAdjacent);
			
			// Show the property labelling of the selection
			TableItem[] properties = propertyTable.getItems();
			KroneckerDisplayPropertyMap propertyMap = component.getPropertyMap();
			if (properties.length > 0) {
				manager.add(new Separator());
			}
			for (int i = 0; i < properties.length; i++) {
				boolean isTrue = propertyMap.testProperty(properties[i].getText(), selected, true);
				Action setPropertyAction = createSetPropertyAction(properties[i].getText(), isTrue);
				manager.add(setPropertyAction);
			}
		}	
	}

	/**
	 * The constructor.
	 */
	public AbstractionView() {
	}

	private ArrayList<KroneckerDisplayState> getStates(Object[] selection) {
		ArrayList<KroneckerDisplayState> selectedStates = new ArrayList<KroneckerDisplayState>();
		for (int i = 0; i < selection.length; i++) {
			if (selection[i] instanceof GraphNode) {
				GraphNode node = (GraphNode) selection[i];
				if (node.getData() instanceof KroneckerDisplayState) {
					selectedStates.add((KroneckerDisplayState) node.getData());
				}
			}
		}
		return selectedStates;
	}

	private void displayStates(int componentID, ArrayList<KroneckerDisplayState> selection) {
		// First hide all the nodes
		Object[] allNodes = builders[componentID].getGraph().getNodes().toArray();
		for (int i = 0; i < allNodes.length; i++) {
			((GraphItem)(allNodes[i])).setVisible(false);
		}
		// Now display only those we need to
		GraphItem[] nodes = getGraphNodes(builders[componentID], selection);
		for (int i = 0; i < nodes.length; i++) {
			nodes[i].setVisible(true);
		}
		if (nodes.length < builders[componentID].getStates().size()) {
			displayingEntireGraph[componentID] = false;
		}
		// We need to re-draw the graph, to properly deal with actions
		updateGraphLabels(false);
		// Deselect all states
		selectStates(builders[componentID], new ArrayList<KroneckerDisplayState>(), false);
	}
	
	private void displayAllStates(int componentID) {
		Object[] allNodes = builders[componentID].getGraph().getNodes().toArray();
		for (int i = 0; i < allNodes.length; i++) {
			((GraphItem)(allNodes[i])).setVisible(true);
		}
		displayingEntireGraph[componentID] = true;
	}
	
	private void selectStates(SequentialComponentGraphBuilder builder, ArrayList<KroneckerDisplayState> selection, boolean selectAggregates) {
		GraphItem[] toSelect = getGraphNodes(builder, selection);
		ArrayList<GraphItem> visibleSelection = new ArrayList<GraphItem>();
		for (int i = 0; i < toSelect.length; i++) {
			if (toSelect[i].isVisible()) {
				visibleSelection.add(toSelect[i]);
			}
		}
		GraphItem[] canSelect = new GraphItem[visibleSelection.size()];
		builder.getGraph().setSelection(visibleSelection.toArray(canSelect));
		if (selectAggregates) updateGraphSelection(builder, true);
		updatePropertyTable();
		setControls(builder.getComponent(), selection);
	}

	private void setControls(KroneckerDisplayComponent component, ArrayList<KroneckerDisplayState> selectedStates) {
		if (selectedStates.size() == 0) {
			aggregateButton.setEnabled(false);
			disaggregateButton.setEnabled(false);
			//statusLabel.setText("");
			return;
		}
		ArrayList<Short> abstractStates = new ArrayList<Short>(10);
		for (KroneckerDisplayState currentConcrete : selectedStates) {
			short currentAbstract = component.getAbstractState(currentConcrete);
			if (!abstractStates.contains(currentAbstract)) {
				abstractStates.add(currentAbstract);
			}
		}
		disaggregateButton.setEnabled(abstractStates.size() < selectedStates.size());
		aggregateButton.setEnabled(abstractStates.size() > 1);
		//String text = "Concrete States: " + selectedStates.toString() + " => Abstract States: " + abstractStates.toString();
		//statusLabel.setText(text);
	}

	private GraphItem[] getGraphNodes(SequentialComponentGraphBuilder builder, ArrayList<KroneckerDisplayState> selection) {
		GraphItem[] nodes = new GraphItem[selection.size()];
		int i = 0;
		for (KroneckerDisplayState state : selection) {
			nodes[i++] = builder.getNode(state, useShortNames);
		}
		return nodes;
	}

	private void updateGraphSelection(SequentialComponentGraphBuilder builder, boolean makeSuper) {
		Object[] selectedStates = builder.getGraph().getSelection().toArray();
		builder.getComponent().clearSelection();
		ArrayList<KroneckerDisplayState> selection = new ArrayList<KroneckerDisplayState>(10);
		ArrayList<GraphNode> selectionMinusStates = new ArrayList<GraphNode>(10);
		KroneckerDisplayPropertyMap propertyMap = builder.getComponent().getPropertyMap();
		for (int i = 0; i < selectedStates.length; i++) {
			if (selectedStates[i] instanceof GraphNode) {
				GraphNode node = (GraphNode) selectedStates[i];
				if (node.getData() instanceof KroneckerDisplayState) {
					KroneckerDisplayState nodeState = (KroneckerDisplayState) node.getData();
					if (!makeSuper && !propertyMap.isConsistent(nodeState)) {
						continue;
					}
					KroneckerDisplayState[] aggregate = builder.getComponent().getAggregate(nodeState);
					for (int j = 0; j < aggregate.length; j++) {
						KroneckerDisplayState state = aggregate[j];
						if (!selection.contains(state)) {
							builder.getComponent().selectState(state);
							selection.add(state);
						}
					}
				} else {
					selectionMinusStates.add(node);
				}
			}
		}
		setControls(builder.getComponent(), selection);
		GraphItem[] selectedNodes;
		if (selection.size() == 0) {
			GraphItem[] selectedActions = new GraphItem[selectionMinusStates.size()];
			selectionMinusStates.toArray(selectedActions);
			selectedNodes = selectedActions;
		} else {
			selectedNodes = getGraphNodes(builder, selection);
		}
		builder.getGraph().setSelection(selectedNodes);
	}

	private class GraphSelectionListener implements SelectionListener {

		private SequentialComponentGraphBuilder builder;

		public GraphSelectionListener(SequentialComponentGraphBuilder builder) {
			this.builder = builder;
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			updateGraphSelection(builder, true);
			updatePropertyTable();
		}

	}
	
	private class GraphMouseListener implements MouseListener {

		private Graph graph;
		private int x_location = -1;
		private int y_location = -1;

		public GraphMouseListener(Graph graph) {
			this.graph = graph;
		}
		
		public void mouseDoubleClick(MouseEvent e) { }

		public void mouseDown(MouseEvent e) {
			x_location = e.x;
			y_location = e.y;			
		}

		public void mouseUp(MouseEvent e) {
			if (graph.getSelection().size() > 0) {
				if (x_location != e.x || y_location != e.y) {
					updateGraphLabels(false);
				}
			}
			x_location = -1;
			y_location = -1;
		}
	}
	
	// Use to run the MRMC exporter in a separate thread
	private class MRMCThread extends Thread {
		
		String fileName;
		
		private boolean isNondeterministic; 
		private Exception exception;
		
		public MRMCThread(String fileName) {
			super();
			this.fileName = fileName;
		}
		
		public Exception getException() {
			return exception;
		}
		
		public boolean isNondeterminstic() {
			return isNondeterministic;
		}
		
		public void run() {
			IMRMCGenerator exporter = pepaModel.getModelChecker(0.000001).getMRMCGenerator();
			try {
				isNondeterministic = exporter.print(fileName);
			} catch (DerivationException e) {
				exception = e;
			} catch (IOException e) {
				exception = e;
			}
		}
		
	}

}
