/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IMemento;

import uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public abstract class AbstractConfigurableStateSpaceFilter /*implements IStateSpaceFilter/*extends ViewerFilter*/ {

	protected boolean negation = false;

	protected Combo doDontCombo = null;
	
	protected IProcessAlgebraModel fModel;

	public AbstractConfigurableStateSpaceFilter(IProcessAlgebraModel model) {
		if (model == null)
			throw new NullPointerException();
		this.fModel = model;
		
	}
	
	public IStateSpaceFilter getFilter() {
		if (negation) {
				return FilterFactory.createNegation(doCreateFilter());
		} else {
			return doCreateFilter();
		}
	}
	
	/**
	 * Creates the filter from the current configuration
	 * @return
	 */
	protected abstract IStateSpaceFilter doCreateFilter();
	
	/**
	 * For labelling purposes
	 * 
	 * @return the string describing this filter
	 */
	protected String getDescription() {
		if (negation)
			return getNegatedString();
		else
			return getAffirmedString();
	}

	/**
	 * For labelling purposes
	 * 
	 * @return the parameter of this filter
	 */
	protected abstract String getParameter();

	public abstract String getLabel();

	/**
	 * The state of this filter can be changed via this GUI. This method is also
	 * responsible for validating the GUI. An instance of
	 * <code>IFilterValidatorListener</code> is passed to this method for
	 * validation notification purposes.
	 * 
	 * @param parent
	 * @param filterValidator
	 *            listener to validation notification event to update the GUI
	 *            which is parenting this filter's UI.
	 */
	public abstract void createGUI(Composite parent);
	
	public abstract void setFilterValidatorListener(IFilterValidatorListener listener);
	
	protected abstract String getNegatedString();

	protected abstract String getAffirmedString();

	protected Combo createNegationCombo(Composite parent) {
		doDontCombo = new Combo(parent, SWT.READ_ONLY);
		doDontCombo.add(getAffirmedString());
		doDontCombo.setData(getAffirmedString(), Boolean.FALSE);
		doDontCombo.add(getNegatedString());
		doDontCombo.setData(getNegatedString(), Boolean.FALSE);
		if (negation)
			doDontCombo.select(1);
		else
			doDontCombo.select(0);
		doDontCombo.addListener(SWT.Modify, new Listener() {

			public void handleEvent(Event event) {
				negation = doDontCombo.getText().equals(getAffirmedString()) ? false
						: true;
			}

		});
		return doDontCombo;
	}
	
	protected abstract void setMemento(IMemento memento);

	protected abstract void getMemento(IMemento memento);

	protected void dispose() {
		if (doDontCombo != null && !doDontCombo.isDisposed())
			doDontCombo.dispose();
	}

}
