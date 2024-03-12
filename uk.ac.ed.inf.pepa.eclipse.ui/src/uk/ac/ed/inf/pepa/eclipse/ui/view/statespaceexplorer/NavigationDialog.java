/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.eclipse.ui.Activator;

/**
 * Create a dialog listing the outgoing states from the given state. States
 * which are currently filtered out are greyed out in this box. A double-click
 * on a state should reveal it on the tableViewer.
 * 
 * @author mtribast
 * 
 */
class NavigationDialog extends Dialog {

	private static final String DIALOG_SETTINGS_NAME = "NAVIGATION_DIALOG";

	ProcessAlgebraModelPage page;

	int currentState;

	int[] outgoingStates;

	int[] incomingStates;

	TableViewer outgoingStatesTableViewer;

	TableViewer incomingStatesTableViewer;

	TableViewer currentStateTableViewer;

	IStructuredSelection selectedSelection;

	Button okButton;

	Button enableColouring;

	Label outgoingNumStates;

	Label incomingNumStates;

	Font boldFont;

	Color red, black;

	/*
	 * Resize all the tables at the same time
	 */
	final private Listener RESIZE_LISTENER = new Listener() {

		private boolean isResizing = false;

		public void handleEvent(Event event) {
			if (!(event.widget instanceof TableColumn))
				return;
			if (isResizing)
				return;
			if (!isResizing)
				isResizing = true;
			TableColumn column = (TableColumn) event.widget;
			tryTable(currentStateTableViewer.getTable(), column);
			tryTable(outgoingStatesTableViewer.getTable(), column);
			tryTable(incomingStatesTableViewer.getTable(), column);
			isResizing = false;
		}

		private void tryTable(Table table, TableColumn column) {
			if (table == null)
				return;
			Table parent = column.getParent();
			if (parent != table) {
				/* find column in table */
				int index = 0;
				for (; index < parent.getColumnCount(); index++)
					if (parent.getColumn(index) == column)
						break;
				table.getColumn(index).setWidth(
						parent.getColumn(index).getWidth());
			}
		}

	};

	boolean navigateStatesAnyway = true;

	protected NavigationDialog(ProcessAlgebraModelPage page, int startingState) {
		super(page.getControl().getShell());
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		red = page.getControl().getDisplay().getSystemColor(SWT.COLOR_RED);
		black = page.getControl().getDisplay().getSystemColor(SWT.COLOR_BLACK);
		this.page = page;
		updateStateModel(startingState);
	}

	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(DIALOG_SETTINGS_NAME);
		if (section == null) {
			section = settings.addNewSection(DIALOG_SETTINGS_NAME);
		}
		return section;
	}

	public boolean close() {
		boldFont.dispose();
		return super.close();
	}

	/* Initialise the model for the input */
	private void updateStateModel(int startingState) {
		this.currentState = startingState;
		prepareStates();
	}

	private void prepareStates() {

		IStateSpace ss = page.model.getStateSpace();
		this.outgoingStates = ss.getOutgoingStateIndices(this.currentState);
		this.incomingStates = ss.getIncomingStateIndices(this.currentState);
	}

	/* Requires updateStateModel being called first */
	private void updateDialogBox() {
		ArrayList<Integer> current = new ArrayList<Integer>();
		current.add(this.currentState);
		currentStateTableViewer.setInput(current);
		ArrayList<Integer> outgoing = new ArrayList<Integer>();
		for (int i : outgoingStates)
			outgoing.add(i);
		ArrayList<Integer> incoming = new ArrayList<Integer>();
		for (int i : incomingStates)
			incoming.add(i);

		outgoingStatesTableViewer.setInput(outgoing);
		incomingStatesTableViewer.setInput(incoming);

		outgoingNumStates.setText(outgoingStates.length + " outgoing states");
		incomingNumStates.setText(incomingStates.length + " incoming states");

		/* NEW Colours for local components which change */
		colouring();

	}

	/**
	 * Refactored out of {@link #updateDialogBox()}
	 * <p>
	 * Colour active local components differently.
	 */
	private void colouring() {
		TableItem currentItem = currentStateTableViewer.getTable().getItem(0);
		boolean enabled = enableColouring.getSelection();
		boolean solutionAvailable = page.model.getStateSpace()
				.isSolutionAvailable();
		compareAgainst(enabled, currentItem, incomingStatesTableViewer
				.getTable(), solutionAvailable);
		compareAgainst(enabled, currentItem, outgoingStatesTableViewer
				.getTable(), solutionAvailable);
	}

	private void compareAgainst(boolean enabled, TableItem currentItem,
			Table compareAgainst, boolean solutionAvailable) {
		for (TableItem item : compareAgainst.getItems())
			for (int i = 2 /* state number not affected */; i < compareAgainst
					.getColumnCount()
					- ((solutionAvailable) ? 1 : 0 /*
													 * don't consider solution
													 * column
													 */); i++)
				if (!item.getText(i).equals(currentItem.getText(i))) {
					item.setForeground(i, enabled ? red : black);
				} else {
					/* Don't forget the colour after refreshing */
					item.setForeground(i, black);
				}
	}

	protected void configureShell(Shell newShell) {
		/* Set Title */
		super.configureShell(newShell);
		newShell.setText("Single Step Navigator");
	}

	protected void okPressed() {
		page.tableViewer.setSelection(selectedSelection);
		updateStateModel((Integer) selectedSelection.getFirstElement());
		updateDialogBox();
	}

	protected void cancelPressed() {
		selectedSelection = null;
		super.cancelPressed();

	}

	protected Point getInitialSize() {
		/*
		 * int original = super.getInitialSize().y; int itemHeight =
		 * Math.max(original, page.tableViewer.getTable() .getItemHeight()
		 * outgoingStates.length); int max =
		 * page.getControl().getDisplay().getClientArea().height / 3; int w = 20
		 * offset ; for (TableColumn c :
		 * outgoingStatesTableViewer.getTable().getColumns()) w += c.getWidth();
		 * Point result = new Point(w, Math.min(itemHeight, max) + 40 offset );
		 * 
		 * return result;
		 */

		/*
		 * The above algorithm caused the Mac OS X implementation of SWT not to
		 * work fine. We let the SWT layout algorithm to compute the best size
		 * for this dialog box.
		 */
		return super.getInitialSize();
	}

	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		okButton = getButton(IDialogConstants.OK_ID);
		okButton.setEnabled(false);
		okButton.setText("Go to");
		Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
		cancelButton.setText("Finish");
		ISelectionChangedListener selListener = new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				handleSelection((IStructuredSelection) event.getSelection());
			}

		};
		IDoubleClickListener dblListener = new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				handleSelection((IStructuredSelection) event.getSelection());
				if (okButton.isEnabled())
					okPressed();
			}

		};

		outgoingStatesTableViewer.addSelectionChangedListener(selListener);
		incomingStatesTableViewer.addSelectionChangedListener(selListener);
		outgoingStatesTableViewer.addDoubleClickListener(dblListener);
		incomingStatesTableViewer.addDoubleClickListener(dblListener);
		return control;
	}

	private void handleSelection(IStructuredSelection sel) {
		okButton.setEnabled(false);
		if (sel == null)
			return;
		Integer state = (Integer) sel.getFirstElement();
		if (state == null)
			return;
		// if the state is shown (according to the filters)
		// enable ok
		boolean navigable = false;
		if (!navigateStatesAnyway) {
			navigable = ((LazyContentProvider) page.tableViewer
					.getContentProvider()).isFiltered(state);
		}
		if (navigateStatesAnyway || navigable) {
			okButton.setEnabled(true);
			selectedSelection = sel;
		}

	}

	protected Control createDialogArea(Composite parent) {
		int verticalSpan = 5;

		Composite composite = (Composite) super.createDialogArea(parent);
		/* Layout preparation */
		// composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layoutTP = new GridLayout();
		layoutTP.marginWidth = 5;
		layoutTP.numColumns = 1;
		composite.setLayout(layoutTP);
		final Button option = new Button(composite, SWT.CHECK);
		option.setText("Only states unaffected by filters can be reached");
		option.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		option.setSelection(!navigateStatesAnyway);
		option.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				navigateStatesAnyway = !option.getSelection();
				if (navigateStatesAnyway == false) {
					// the current selection may be disabled now
					handleSelection(selectedSelection);
				}
			}

		});

		enableColouring = new Button(composite, SWT.CHECK);
		enableColouring.setText("Enable colouring");
		enableColouring.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		enableColouring.setSelection(true);
		enableColouring.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				colouring();
			}
		});

		/* Incoming states section */
		Label incomingTitle = new Label(composite, SWT.NONE);
		incomingTitle.setText("Incoming States");
		GridData incomingData = new GridData();
		incomingData.verticalIndent = verticalSpan;
		incomingData.verticalAlignment = SWT.BOTTOM;
		incomingData.grabExcessVerticalSpace = false;
		incomingTitle.setLayoutData(incomingData);

		FontData[] datas = incomingTitle.getFont().getFontData();
		for (FontData d : datas)
			d.setStyle(SWT.BOLD);
		boldFont = new Font(incomingTitle.getDisplay(), datas);
		incomingTitle.setFont(boldFont);

		incomingStatesTableViewer = createTableViewer(true, false, composite);

		incomingNumStates = new Label(composite, SWT.NONE);
		incomingNumStates.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		/* Current State Section */
		Label currentStateTitle = new Label(composite, SWT.NONE);
		GridData currentData = new GridData();
		currentData.verticalSpan = verticalSpan;
		currentData.verticalAlignment = SWT.BOTTOM;
		currentData.grabExcessVerticalSpace = false;

		currentStateTitle.setLayoutData(currentData);

		currentStateTitle.setText("Current State");
		currentStateTitle.setFont(boldFont);

		currentStateTableViewer = createTableViewer(false, false, composite);

		/* Outgoing states section */
		Label outgoingTitle = new Label(composite, SWT.NONE);
		outgoingTitle.setText("Outgoing States");
		GridData outgoingData = new GridData();
		outgoingData.verticalSpan = verticalSpan;
		outgoingData.verticalSpan = verticalSpan;
		outgoingData.verticalAlignment = SWT.BOTTOM;
		outgoingTitle.setLayoutData(outgoingData);

		outgoingTitle.setFont(boldFont);

		outgoingStatesTableViewer = createTableViewer(true, true, composite);

		outgoingNumStates = new Label(composite, SWT.NONE);
		outgoingNumStates.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		currentStateTableViewer.getTable().getColumn(0).setWidth(
				outgoingStatesTableViewer.getTable().getColumn(0).getWidth());
		/* Bootstrap */
		updateDialogBox();

		return composite;

	}

	private TableViewer createTableViewer(boolean transitionTable,
			boolean outgoing, Composite composite) {
		TableViewer tableViewer = new TableViewer(composite, SWT.VIRTUAL
				| SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION);
		LazyContentProvider contentProvider = new LazyContentProvider(
				this.page.model, tableViewer);
		StateLabelProvider provider;
		provider = new TransitionLabelProvider(contentProvider, this,
				transitionTable, outgoing);
		tableViewer.setLabelProvider(provider);
		tableViewer.setContentProvider(contentProvider);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		Table originalTable = page.tableViewer.getTable();
		TableColumn actionColumn = new TableColumn(table, SWT.RIGHT);
		if (transitionTable)
			actionColumn.setText("Action");
		else
			actionColumn.setText("");

		for (int i = 0; i < originalTable.getColumnCount(); i++) {
			TableColumn c = new TableColumn(table, SWT.LEFT);
			if (i == 0)
				c.setText("No.");
			else
				c.setText("Comp. " + i);
			int originalWidth = originalTable.getColumns()[i].getWidth();
			c.setWidth(originalWidth);
			c.addListener(SWT.Resize, RESIZE_LISTENER);

		}

		actionColumn.pack();
		actionColumn.addListener(SWT.Resize, RESIZE_LISTENER);
		return tableViewer;
	}

}
