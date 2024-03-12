package uk.ac.ed.inf.pepa.eclipse.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class ConfigurationCheckBox extends ConfigurationWidget {

		public ConfigurationCheckBox(OptionMap map, String key, IValidationCallback callback) {
			super(map, key, callback);
		}

		@Override
		public
		Object getValue() {
			return Boolean.parseBoolean(this.propertyValue);
		}

		@Override
		public
		void setValue(String value) {
			this.propertyValue = value;
		}

		@Override
		public boolean isValid() {

			return true;
		}


		@Override
		public
		Control createControl(Composite parent) {
			control = new Button(parent, SWT.CHECK);
			updateControl();
			control.addListener(SWT.Modify, new Listener() {

				public void handleEvent(Event event) {
					setValue(Boolean
							.toString(((Button) control).getSelection()));
					parentCallback.validate();
				}

			});
			return control;

		}

		@Override
		public
		void updateControl() {
			if (control != null)
				((Button) control).setSelection((Boolean) getValue());
		}
	
	}
