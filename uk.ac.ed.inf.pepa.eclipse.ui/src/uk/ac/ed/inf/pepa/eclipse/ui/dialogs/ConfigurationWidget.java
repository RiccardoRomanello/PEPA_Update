package uk.ac.ed.inf.pepa.eclipse.ui.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public abstract class ConfigurationWidget {

	protected String propertyKey;

	protected String propertyValue;

	public Control control;
	
	protected OptionMap optionMap;

	protected IValidationCallback parentCallback;
	
	ConfigurationWidget(OptionMap optionMap, String key, IValidationCallback callback) {

		Assert.isNotNull(key);

		this.propertyKey = key;
		this.control = null;
		this.propertyValue = null;
		this.optionMap = optionMap;
		this.parentCallback = callback;

		init();

	}

	public String getProperty() {
		return propertyKey;
	}

	public abstract Control createControl(Composite parent);

	/**
	 * Reconcile the control with the internal model
	 * 
	 */
	public abstract void updateControl();

	/**
	 * Parses the value contained by this widget and return the correct
	 * object, which may be a String, a Double, a Long, etc.
	 * 
	 * @return
	 */
	public abstract Object getValue();

	/**
	 * Set the value of the widget to the passed string. It is up to
	 * concrete implementations to treat the string properly.
	 * 
	 * @param value
	 */
	public abstract void setValue(String value);

	public abstract boolean isValid();

	public void resetToDefault() {
		setValue("" + OptionMap.getDefaultValue(propertyKey));
	}

	void init() {

		setValue("" + optionMap.get(propertyKey));
	}

}
