package uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayState;

/**
 * Input dialog for selecting states from a component
 * 
 * @author msmith
 */
public class SelectStatesDialog extends Dialog {

	private String title;
    private String message;
    private Table stateTable;
    
    private ArrayList<KroneckerDisplayState> states;
    private ArrayList<KroneckerDisplayState> selection;
    
    public SelectStatesDialog(Shell shell, String title, String message, ArrayList<KroneckerDisplayState> states) {
        super(shell);
        this.title = title;
        this.message = message;
        this.states = states;
    }

    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
    }

    protected Control createDialogArea(Composite parent) {
    	FormData formData;
		
		Composite viewFrame = new Composite(parent, SWT.NONE);
		viewFrame.setLayout(new FormLayout());
		
		Label messageLabel = new Label(viewFrame, SWT.NONE);
		messageLabel.setText(message);
		formData = new FormData();
		formData.top = new FormAttachment(0, 10);
		formData.left = new FormAttachment(0, 5);
		messageLabel.setLayoutData(formData);
    	
		stateTable = new Table(viewFrame, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		stateTable.setHeaderVisible(false);
		stateTable.setLinesVisible(false);
		TableColumn stateColumn = new TableColumn(stateTable, SWT.NULL);
		stateColumn.setAlignment(SWT.LEFT);
		stateTable.setSortColumn(stateColumn);
		stateTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				setButtons();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		KroneckerDisplayState[] stateList = new KroneckerDisplayState[states.size()];	
		states.toArray(stateList);
		Arrays.sort(stateList);
		for (int i = 0; i < stateList.length; i++) {
			TableItem item = new TableItem(stateTable, SWT.NULL);
			item.setText(stateList[i].getLabel(false));
			item.setData(stateList[i]);
		}
		stateColumn.pack();
		formData = new FormData();
		formData.top = new FormAttachment(messageLabel, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100,-5);
		formData.bottom = new FormAttachment(100,-5);
		stateTable.setLayoutData(formData);
		
        return viewFrame;
    }

	protected void okPressed() {
		TableItem[] selectedItems = stateTable.getSelection();
		selection = new ArrayList<KroneckerDisplayState>();
		for (int i = 0; i < selectedItems.length; i++) {
			KroneckerDisplayState state = (KroneckerDisplayState)(selectedItems[i].getData());
			selection.add(state);
		}
		super.okPressed();
	}
	
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		setButtons();
		return control;
	}
	
	public ArrayList<KroneckerDisplayState> getSelection() {
		return selection;
	}

	private void setButtons() {
		boolean isOK = stateTable.getSelection().length > 0;
		if (getButton(IDialogConstants.OK_ID) == null) return;
		getButton(IDialogConstants.OK_ID).setEnabled(isOK);
		int defaultButton = isOK ? IDialogConstants.OK_ID : IDialogConstants.CANCEL_ID;
		Shell shell = getShell();
		if (shell != null) {
			shell.setDefaultButton(getButton(defaultButton));
		}
	}
	
}
