package uk.ac.ed.inf.pepa.eclipse.ui.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public abstract class ConfigurationText extends ConfigurationWidget {

	public ConfigurationText(OptionMap map, String key, IValidationCallback callback) {
		super(map, key,callback);
	}

	@Override
	public
	void setValue(String value) {
		this.propertyValue = value;
	}

	@Override
	public
	Control createControl(Composite parent) {

		control = new Text(parent, SWT.BORDER | SWT.RIGHT);
		updateControl();
		control.addListener(SWT.Modify, new Listener() {

			public void handleEvent(Event event) {
				setValue(((Text) control).getText());
				parentCallback.validate();
			}

		});
		return control;

	}

	@Override
	public void updateControl() {
		((Text) control).setText("" + getValue());
	}

}
