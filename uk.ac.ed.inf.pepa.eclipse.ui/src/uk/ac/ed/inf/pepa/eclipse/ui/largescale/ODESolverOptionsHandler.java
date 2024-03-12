package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import org.eclipse.swt.widgets.Composite;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.ConfigurationText;
import uk.ac.ed.inf.pepa.eclipse.ui.dialogs.IValidationCallback;

class ODESolverOptionsHandler extends SolverOptionsHandler {

	private ConfigurationText normText;
	private ConfigurationText startTime;

	public ODESolverOptionsHandler(boolean supportsTransient, OptionMap map,
			IValidationCallback cb) {
		super(supportsTransient, map, cb);
	}

	protected void fillDialogArea(Composite composite) {

		startTime = configure(composite, "Start time",
				OptionMap.ODE_START_TIME, true);

		configure(composite, "Stop time", OptionMap.ODE_STOP_TIME, true);

		configure(composite, "Number of time points", OptionMap.ODE_STEP, false);

		configure(composite, "Absolute tolerance", OptionMap.ODE_ATOL, true);

		configure(composite, "Relative tolerance", OptionMap.ODE_RTOL, true);

		normText = configure(composite, "Steady-state convergercence norm",
				OptionMap.ODE_STEADY_STATE_NORM, true);
		enableTransientParameters();

	}

	private void enableTransientParameters() {
		normText.control.setEnabled(!isTransient());
		startTime.control.setEnabled(isTransient());
	}

	protected void setTransient(boolean isTransient) {
		super.setTransient(isTransient);
		enableTransientParameters();
	}

	protected OptionMap updateOptionMap() {
		OptionMap map = super.updateOptionMap();
		Object value = (isTransient()) ? OptionMap.ODE_INTERPOLATION_ON
				: OptionMap.ODE_INTERPOLATION_OFF;
		map.put(OptionMap.ODE_INTERPOLATION, value);
		return map;
		
	}

}