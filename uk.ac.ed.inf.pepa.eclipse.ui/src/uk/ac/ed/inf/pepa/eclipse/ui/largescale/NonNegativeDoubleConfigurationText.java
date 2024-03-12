package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.DoubleConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;

public class NonNegativeDoubleConfigurationText extends DoubleConfigurationText {

	public NonNegativeDoubleConfigurationText(OptionMap map, String key,
			IValidationCallback callback) {
		super(map, key, callback);
	}
	
	@Override
	public boolean isValid() {
		try {
			double d = Double.parseDouble(this.propertyValue);
			return d >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public
	Object getValue() {
		try {
			double d = Double.parseDouble(this.propertyValue);
			return d >= 0 ? d : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}


}
