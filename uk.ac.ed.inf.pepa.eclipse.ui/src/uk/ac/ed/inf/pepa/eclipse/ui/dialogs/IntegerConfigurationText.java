package uk.ac.ed.inf.pepa.eclipse.ui.dialogs;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public  class IntegerConfigurationText extends ConfigurationText {

	public IntegerConfigurationText(OptionMap map, String key, IValidationCallback callback) {
		super(map, key, callback);
	}

	@Override
	public boolean isValid() {
		try {
			Integer.parseInt(this.propertyValue);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	@Override
	public
	Object getValue() {
		try {
			return Integer.parseInt(this.propertyValue);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
