package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;

public class UtilCapacityTreeWidget extends CapacityPlanningWidget {
	
	final Tree tree;

	public UtilCapacityTreeWidget(final IValidationCallback cb, Composite container,
			Control control) {
		super(cb, container, control);
	
		String[] keys = control.getKeys();
		
		//pad
		Label label = new Label(container, SWT.FILL);
		label.setText("");
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		data.widthHint = 25;
		label.setLayoutData(data);
		
		tree = new Tree(container, SWT.CHECK | SWT.BORDER );
		for (int i = 0; i < keys.length; i++) {
			TreeItem item = new TreeItem(tree, SWT.NONE);
			item.setText(keys[i]);
		}
		
		tree.addListener(SWT.Selection, new Listener() {
            
			public void handleEvent(Event e) {
				cb.validate();   
			}
		}); 
		
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 14, 1);
		data.widthHint = 550;
		tree.setLayoutData(data);
		
		//pad
		label = new Label(container, SWT.FILL);
		label.setText("");
		data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		data.widthHint = 25;
		label.setLayoutData(data);

		
	}
		
	@Override
	public Response isValid() {
		
		TreeItem[] items = tree.getItems();
		ArrayList<Response> responses = new ArrayList<Response>();
		Response response = new Response(true);
		
		for(TreeItem item : items){
			String key, value;
			key = item.getText();
			if(item.getChecked()){
				value = "True";
			} else {
				value = "False";
			}
			if(!control.getValue(key).equals(value)){
				responses.add(new Response(control.setValue(key, value)));
			}
		} 
		
		for(Response r : responses){
			if(!r.valid){
				r.setComplaint("Invalid configuration");
				response = r;
			}
		}
		
		return response;
	}

}
