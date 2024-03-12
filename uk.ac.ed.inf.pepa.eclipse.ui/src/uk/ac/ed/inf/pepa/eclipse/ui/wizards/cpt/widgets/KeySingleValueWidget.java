package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import uk.ac.ed.inf.pepa.cpt.config.control.Control;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;

public class KeySingleValueWidget extends CapacityPlanningWidget {
	
	private String key, value;
	protected Text text;

	public KeySingleValueWidget(final IValidationCallback cb, Composite container, String key, String value, Control control) {
		super(cb, container, control);
	
		
		this.key = key;
		this.value = value;
			
		//pad
		Label label = new Label(container, SWT.SINGLE | SWT.FILL);
		label.setText("");
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1);
		label.setLayoutData(data);
		
		label = new Label(container, SWT.SINGLE | SWT.FILL);
		label.setText(this.key);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		label.setLayoutData(data);
		
		text = new Text(container, SWT.SINGLE | SWT.RIGHT | SWT.BORDER);
		text.setText("" + this.value);
		data = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		text.setLayoutData(data);
		
		text.addListener(SWT.Modify, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				cb.validate();
				
			}
			
		});
		
		//pad
		label = new Label(container, SWT.SINGLE | SWT.FILL);
		label.setText("");
		data = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		label.setLayoutData(data);
		
	}

	@Override
	public Response isValid() {
			
		Response response = new Response(control.setValue(this.key, text.getText()));
		
		if(!response.valid){
			response.setComplaint("Invalid entry: " + this.key + " " + text.getText());
		}
		
		return response;
		
	}

}
