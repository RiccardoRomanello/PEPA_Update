/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;

public class PepaCorePreferenceInitializer extends
		AbstractPreferenceInitializer {

	public PepaCorePreferenceInitializer() {
		super();
	}

	@Override
	public void initializeDefaultPreferences() {
		Preferences preferences = PepaCore.getDefault().getPluginPreferences();
		initialisePepatoSolverSettings(preferences);

	}

	private void initialisePepatoSolverSettings(Preferences preferences) {
		String[] keys = OptionMap.defaultKeys();
		for (String pepatoKey : keys) {
			Object value = OptionMap.getDefaultValue(pepatoKey);
			if (value instanceof Boolean)
				preferences.setDefault(pepatoKey, (Boolean) value);
			else if (value instanceof String)
				preferences.setDefault(pepatoKey, (String) value);
			else if (value instanceof Integer)
				preferences.setDefault(pepatoKey, (Integer) value);
			else if (value instanceof Float)
				preferences.setDefault(pepatoKey, (Float) value);
			else if (value instanceof Long)
				preferences.setDefault(pepatoKey, (Long) value);
			else if (value instanceof Double)
				preferences.setDefault(pepatoKey, (Double) value);

		}

	}

}
