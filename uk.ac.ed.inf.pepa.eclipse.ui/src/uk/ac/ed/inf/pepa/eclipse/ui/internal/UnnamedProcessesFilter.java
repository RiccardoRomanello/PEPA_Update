/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;

import uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public class UnnamedProcessesFilter extends
		AbstractConfigurableStateSpaceFilter {
	
	private static final String LABEL = "States ";
	
	private static final String AFFIRMED = "which contain unnamed processes";

	private static final String NEGATED = "which do not contain unnamed processes";

	private static final String TAG_UNNAMED = "unnamedProcessesFilter";
	
	private static final String TAG_NEGATION = "negation";

	public UnnamedProcessesFilter(IProcessAlgebraModel model) {
		super(model);
	}
	
	@Override
	public void createGUI(Composite parent) {
		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(main, SWT.NULL);
		label.setText(LABEL);
		label.setLayoutData(new GridData());
		
		Control negationCombo = createNegationCombo(main);
		negationCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// this is always valid
	}

	@Override
	protected String getAffirmedString() {
		return AFFIRMED;
	}

	@Override
	public String getLabel() {
		return LABEL + getDescription();
	}

	@Override
	protected void getMemento(IMemento memento) {
		IMemento mem = memento.createChild(TAG_UNNAMED);
		mem.putString(TAG_NEGATION, "" + negation);

	}

	@Override
	protected String getNegatedString() {
		return NEGATED;
	}

	@Override
	protected String getParameter() {
		return "";
	}
	
	@Override
	protected void setMemento(IMemento memento) {
		IMemento mem = memento.getChild(TAG_UNNAMED);
		negation = Boolean.parseBoolean(mem.getString(TAG_NEGATION));

	}
	
	@Override
	public void setFilterValidatorListener(IFilterValidatorListener listener) {
		listener.filterValidated(null);		
	}

	@Override
	protected IStateSpaceFilter doCreateFilter() {
		return FilterFactory.createUnnamedStatesFilter();
	}

}
