/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.modelcheckingview;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.AbstractBoolean;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLStatePlaceHolder;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.IPropertyChangedListener;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ModelCheckingException;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ProbabilityInterval;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PEPAModelChecker;
import uk.ac.ed.inf.pepa.eclipse.core.ProcessAlgebraModelChangedEvent;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.IProcessAlgebraEditor;
import uk.ac.ed.inf.pepa.eclipse.ui.editor.PEPAEditor;
import uk.ac.ed.inf.pepa.eclipse.ui.view.AbstractView;
import uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview.PropertyManager;

public class ModelCheckingView extends AbstractView implements IPropertyChangedListener {

	
	private Composite modelCheckingView;
	
	/**
	 * The table of CSL properties
	 */
	private Table propertyTable;
	
	/**
	 * Perform the model checking
	 */
	private Button modelCheckButton;
	
	/**
	 * Accuracy of the transient model checking
	 */
	private Text boundAccuracyText;
	private Label boundAccuracyLabel;
	
	/**
	 * Actions	
	 */
	private Action newPropertyAction;
	private ImportPropertiesAction importPropertiesAction;
	private ExportPropertiesAction exportPropertiesAction;
	
	/**
	 * Data
	 */
	private KroneckerDisplayModel model;
	
	/**
	 * Link to PEPA model
	 */
	private IPepaModel pepaModel;
	File pepaFile;
	
	public ModelCheckingView() {
	}

	public String getPEPAPath() {
		if (pepaFile == null) {
			return System.getProperty("user.dir");
		} else {
			return pepaFile.getPath();
		}
	}
	
	public KroneckerDisplayModel getModel() {
		return model;
	}
	
	private void createNewPropertyAction() {
		newPropertyAction = new Action("New CSL Property...") {			
			public void run() {
				EditCSLDialog editDialog = new EditCSLDialog(getSite().getShell(), model, "", new CSLStatePlaceHolder());
				if (editDialog.open() != Dialog.OK) {
					editDialog.close();
				} else {
					String name = editDialog.getNewName();
					CSLAbstractStateProperty property = editDialog.getNewProperty();
					editDialog.close();
					model.addCSLProperty(name, property);
					handlePropertiesChanged();
					setFocus();
				}
			}
		};
	}

	private void editProperty(TableItem selected) {
		String name = selected.getText(0);
		CSLAbstractStateProperty property = model.getCSLProperty(name);
		EditCSLDialog editDialog = new EditCSLDialog(getSite().getShell(), model, name, property);
		if (editDialog.open() != Dialog.OK) {
			editDialog.close();
		} else {
			String newName = editDialog.getNewName();
			CSLAbstractStateProperty newProperty = editDialog.getNewProperty();
			editDialog.close();
			model.changeCSLProperty(name, property, newName, newProperty);
			handlePropertiesChanged();
			setFocus();
		}
	}
	
	private Action createEditPropertyAction(final TableItem selected) {
		Action editPropertyAction = new Action("Edit...") {			
			public void run() {
				editProperty(selected);
			}
		};
		return editPropertyAction;
	}
	
	private Action createViewResultAction(final TableItem selected) {
		Action editPropertyAction = new Action("View Result...") {			
			public void run() {
				String propertyName = selected.getText(0);
				String property = selected.getText(1);
				String propertyValue = (String)selected.getData();
				InputDialog dialog = new InputDialog(getSite().getShell(),
						"CSL Property: " + propertyName,
		                "The property \"" + property + "\" evalues to:", propertyValue, null);
				dialog.open();
			}
		};
		return editPropertyAction;
	}
	
	private Action createVerifyPropertyAction(final TableItem[] selection) {
		Action editPropertyAction = new Action("Verify") {			
			public void run() {
				String[] names = new String[selection.length];
				for (int i = 0; i < selection.length; i++) {
					names[i] = selection[i].getText(0);
				}
				for (int i = 0; i < names.length; i++) {
					modelCheck(names[i]);
				}
			}
		};
		return editPropertyAction;
	}
	
	private Action createDeletePropertyAction(final TableItem[] selection) {
		Action deletePropertyAction = new Action("Delete") {			
			public void run() {
				for (int i = 0; i < selection.length; i++) {
					TableItem selected = selection[i];
					model.removeCSLProperty(selected.getText(0));
					int index = propertyTable.indexOf(selected);
					propertyTable.remove(index);
				}
				updateModelCheckingButton();
			}
		};
		return deletePropertyAction;
	}
	
	private void modelCheck(final String propertyName) {
		if (pepaModel == null) return;
		double accuracy = 0.00001;
		try {
			accuracy = Double.parseDouble(boundAccuracyText.getText());
		} catch (NumberFormatException e1) { /* use default */ }
		final double boundAccuracy = accuracy;
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Model checking property: " + propertyName, IProgressMonitor.UNKNOWN);
				ModelCheckingThread task = new ModelCheckingThread(propertyName, boundAccuracy);
				task.start();
				while (task.isAlive() && !monitor.isCanceled()) {
					Thread.sleep(500);
					monitor.worked(500);
				}
				if (monitor.isCanceled()) {
					task.interrupt();
					throw new InterruptedException();
				} else {
					ModelCheckingException e = task.getException();
					if (e != null) {
						MessageDialog.openError(getSite().getShell(),
							"Error during model checking",
			                "Error when model checking property \"" + propertyName + "\": " + e.getMessage());
						model.setCSLPropertyValue(propertyName, "", "");
					}
				}
			}
		};
		ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(getSite().getShell());
		try {
			monitorDialog.run(true, true, runnable);
		} catch (InvocationTargetException e) {
			MessageDialog.openError(getSite().getShell(),
					                "Error during model checking",
					                "Error when model checking property \"" + propertyName + "\": see the log in the Console view for details.");
			model.setCSLPropertyValue(propertyName, "", "");
		} catch (InterruptedException e) {
			MessageDialog.openInformation(getSite().getShell(), "Operation aborted", "Operation interrupted.");
			model.setCSLPropertyValue(propertyName, "", "");
		} finally {
			// notify of a change to the property
			handlePropertiesChanged();
			setFocus();
		}
		computeTableWidths();
	}
	
	private void colorTableItem(TableItem item) {
		String value = item.getText(2);
		if (value.equals(AbstractBoolean.FALSE.toString())) {
			item.setBackground(PropertyManager.COLOR_FALSE);
		} else if (value.equals(AbstractBoolean.TRUE.toString())) {
			item.setBackground(PropertyManager.COLOR_TRUE);
		} else {
			item.setBackground(PropertyManager.COLOR_UNSELECTED);
		}
	}
	
	private void updateModelCheckingButton() {
		TableItem[] properties = propertyTable.getItems();
		modelCheckButton.setEnabled(properties.length > 0);
	}
	
	@Override
	protected void internalCreatePartControl(Composite parent) {
		FormData formData;
		
		modelCheckingView = new Composite(parent, SWT.NONE);
		modelCheckingView.setLayout(new FormLayout());
		
		modelCheckButton = new Button(modelCheckingView, SWT.PUSH);
		modelCheckButton.setText("Verify All");
		modelCheckButton.setEnabled(false);
		modelCheckButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				TableItem[] properties = propertyTable.getItems();
				String[] names = new String[properties.length];
				for (int i = 0; i < properties.length; i++) {
					names[i] = properties[i].getText(0);
				}
				// Now do the model checking (have to do it separately, because
				// we update the table after each property, and so destroy the TableItem[]
				for (int i = 0; i < properties.length; i++) {
					modelCheck(names[i]);
				}
			}
		});
		formData = new FormData();
		formData.right = new FormAttachment(100,-5);
		formData.bottom = new FormAttachment(100,-5);
		modelCheckButton.setLayoutData(formData);
		
		// TEMPORARY
		boundAccuracyText = new Text(modelCheckingView, SWT.BORDER);
		boundAccuracyText.setText("0.00001");

		formData = new FormData();
		formData.right = new FormAttachment(modelCheckButton,-10);
		formData.bottom = new FormAttachment(100,-5);
		formData.width = 100;
		boundAccuracyText.setLayoutData(formData);
		
		boundAccuracyLabel = new Label(modelCheckingView, SWT.NONE);
		boundAccuracyLabel.setText("Iteration Termination Threshold:");
		formData = new FormData();
		formData.right = new FormAttachment(boundAccuracyText,-5);
		formData.bottom = new FormAttachment(100,-10);
		boundAccuracyLabel.setLayoutData(formData);
		// END TEMPORARY
		
		propertyTable = new Table(modelCheckingView, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		propertyTable.setHeaderVisible(true);
		propertyTable.setLinesVisible(true);
		formData = new FormData();
		formData.top = new FormAttachment(0, 0);
		formData.left = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, 0);
		formData.bottom = new FormAttachment(modelCheckButton, -5);
		propertyTable.setLayoutData(formData);
		
		TableColumn nameColumn = new TableColumn(propertyTable, SWT.NULL);
		nameColumn.setAlignment(SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setResizable(false);
		
		TableColumn propertyColumn = new TableColumn(propertyTable, SWT.NULL);
		propertyColumn.setAlignment(SWT.LEFT);
		propertyColumn.setText("CSL Property");
		propertyColumn.setResizable(false);
		
		TableColumn resultColumn = new TableColumn(propertyTable, SWT.NULL);
		resultColumn.setAlignment(SWT.LEFT);
		resultColumn.setText("Result");
		resultColumn.setResizable(false);
		addPropertyContextMenu();
		createNewPropertyAction();
		
		modelCheckingView.addControlListener(new ControlAdapter() {
		    public void controlResized(ControlEvent e) {
		        computeTableWidths();
		    }
		});
		
		// Create import and export buttons
		exportPropertiesAction = new ExportPropertiesAction(this);
		importPropertiesAction = new ImportPropertiesAction(this);
		createToolBar();
		
	}

	private void createToolBar() {
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.removeAll();
		manager.update(false);
		manager.add(exportPropertiesAction);
		manager.add(importPropertiesAction);
		manager.update(true);
	}
	
	private int minimumWidth(int column) {
		GC gc = new GC(propertyTable);
        int columnWidth = gc.textExtent(propertyTable.getColumn(column).getText()).x;
        for (int i = 0; i < propertyTable.getItemCount(); i++) {
        	int textWidth = gc.textExtent(propertyTable.getItem(i).getText(column)).x;
        	columnWidth = Math.max(columnWidth, textWidth);
        }
        gc.dispose();
		return columnWidth;
	}
	
	private void computeTableWidths() {
		Rectangle area = modelCheckingView.getClientArea();
        Point preferredSize = propertyTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        Point buttonPreferredSize = modelCheckButton.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        int width = area.width - (2 * propertyTable.getBorderWidth()) - 2;
        if (preferredSize.y > area.height - buttonPreferredSize.y - 10 - propertyTable.getHeaderHeight()) {
        	// Subtract the scrollbar width from the total column width
        	// if a vertical scrollbar will be required
        	Point vBarSize = propertyTable.getVerticalBar().getSize();
        	width -= vBarSize.x;
        }
        propertyTable.getColumn(0).setWidth(Math.max(150, minimumWidth(0) + 20));
        propertyTable.getColumn(2).setWidth(Math.max(120, minimumWidth(2)));
        propertyTable.getColumn(1).setWidth(Math.max(width - propertyTable.getColumn(0).getWidth() - propertyTable.getColumn(2).getWidth(), 
                                                     minimumWidth(1) + 20));
	}
	
	private void addPropertyContextMenu() {
		MenuManager menuMgr = new MenuManager("#CSLPopupMenu");
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
		if (model == null) return;
		TableItem[] selection = propertyTable.getSelection();
		if (selection.length > 0) {
			Action verifyAction = createVerifyPropertyAction(selection);
			manager.add(verifyAction);
			if (selection.length == 1) {
				if (selection[0].getData() != null) {
					Action viewResultAction = createViewResultAction(selection[0]);
					manager.add(viewResultAction);
				}
				Action editAction = createEditPropertyAction(selection[0]);
				manager.add(editAction);
			}
			Action deleteAction = createDeletePropertyAction(selection);
			manager.add(deleteAction);
			manager.add(new Separator());
		}
		manager.add(newPropertyAction);
	}

	@Override
	protected void updateView(final IProcessAlgebraEditor editor) {
		propertyTable.getDisplay().syncExec(new Runnable() {
			public void run() {
				pepaFile = null;
				model = null;
				propertyTable.removeAll();
				propertyTable.setEnabled(false);
				boundAccuracyText.setEnabled(false);
				boundAccuracyLabel.setEnabled(false);
				if (editor == null) {
					pepaModel = null;
					return;
				} 
				pepaModel = ((PEPAEditor)editor).getProcessAlgebraModel();
				if (pepaModel == null) return;
				model = pepaModel.getDisplayModel();
				if (model == null) return;
				propertyTable.setEnabled(true);
				boundAccuracyText.setEnabled(true);
				boundAccuracyLabel.setEnabled(true);
				model.addPropertyChangedListener(ModelCheckingView.this);
				pepaFile = editor.getProcessAlgebraModel().getResource().getLocation().toFile();
				handlePropertiesChanged();
			}
		});
	}
	
	@Override
	protected void handleModelChanged(ProcessAlgebraModelChangedEvent event) {
		if (event.getType() == ProcessAlgebraModelChangedEvent.PARSED)
			updateView(this.fEditor);
	}
	
	public void handlePropertiesChanged() {
		propertyTable.removeAll();
		String[] propertyNames = model.getCSLPropertyNames();
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			String property = model.getCSLProperty(name).toString();
			String shortValue = model.getCSLShortPropertyValue(name);
			String longValue = model.getCSLLongPropertyValue(name);
			TableItem newTableProperty = new TableItem(propertyTable, SWT.NULL);
			newTableProperty.setText(new String[] {name , property, shortValue});
			newTableProperty.setData(longValue);
			colorTableItem(newTableProperty);
		}
		computeTableWidths();
		updateModelCheckingButton();
	}
	
	public void setFocus() {
		if (propertyTable != null && !propertyTable.isDisposed() && !propertyTable.isFocusControl()) {
			propertyTable.setFocus();
		}
	}
	
	// Use to run the model checker in a separate thread
	private class ModelCheckingThread extends Thread {
		
		private String propertyName;
		private double boundAccuracy;
		private ModelCheckingException exception;
		
		public ModelCheckingThread(String propertyName, double boundAccuracy) {
			super();
			this.propertyName = propertyName;
			this.boundAccuracy = boundAccuracy;
		}
		
		public ModelCheckingException getException() {
			return exception;
		}
		
		public void run() {
			PEPAModelChecker modelChecker = pepaModel.getModelChecker(boundAccuracy);
			CSLAbstractStateProperty property = model.getCSLProperty(propertyName);
			String valueShort;
			String valueLong;
			if (property.isProbabilityTest()) {
				ProbabilityInterval interval = modelChecker.testProperty(property);
				valueShort = interval.toString(5);
				valueLong = interval.toString(13);
			} else {
				AbstractBoolean isOK = modelChecker.checkProperty(property);
				valueShort = isOK.toString();
				valueLong = valueShort;
			}
			model.setCSLPropertyValue(propertyName, valueShort, valueLong);
			exception = modelChecker.getModelCheckingException();
		}
		
	}

}
