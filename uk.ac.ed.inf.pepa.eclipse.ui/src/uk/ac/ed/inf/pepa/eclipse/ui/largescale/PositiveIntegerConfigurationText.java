package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IntegerConfigurationText;

public class PositiveIntegerConfigurationText extends IntegerConfigurationText {

	public PositiveIntegerConfigurationText(OptionMap map, String key,
			IValidationCallback callback) {
		super(map, key, callback);
	}
	
	@Override
	public boolean isValid() {
		try {
			int i = Integer.parseInt(this.propertyValue);
			return i > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public
	Object getValue() {
		try {
			int i =  Integer.parseInt(this.propertyValue);
			return i > 0 ? i : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}


}
