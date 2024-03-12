package uk.ac.ed.inf.pepa.eclipse.ui.dialogs;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class DoubleConfigurationText extends ConfigurationText {

	public DoubleConfigurationText(OptionMap map, String key, IValidationCallback callback) {
		super(map, key, callback);
	}

	@Override
	public boolean isValid() {
		try {
			Double.parseDouble(this.propertyValue);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public
	Object getValue() {
		try {
			return Double.parseDouble(this.propertyValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
