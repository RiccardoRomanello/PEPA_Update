/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;

import uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

/**
 * Creates a filter that is based on incoming transition names.
 * 
 * @author mtribast
 * 
 */
public abstract class ActionFilter extends AbstractConfigurableStateSpaceFilter {

	private String AFFIRMED = null;

	private String NEGATED = null;

	private String TAG_ACTION_FILTER = null;

	private String TAG_ACTION_NAME = null;
	
	private String TAG_NEGATION = "negation";
	
	private String fActionName = null;
	
	private boolean fIncoming;
	
	private IFilterValidatorListener fListener = new IFilterValidatorListener() {

		public void filterValidated(String message) {
		}
		
	};

	public ActionFilter(IProcessAlgebraModel model, boolean incoming) {
		super(model);
		fIncoming = incoming;
		String tagPrefix = incoming ? "incoming" : "outgoing";
		TAG_ACTION_FILTER = tagPrefix + "ActionFilter";
		TAG_ACTION_NAME = tagPrefix + "ActionName";
		AFFIRMED = "with " + tagPrefix + " action";
		NEGATED = "without " + tagPrefix + " action";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createGUI(Composite parent) {
		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(main, SWT.NULL);
		label.setText("States");
		label.setLayoutData(new GridData());
		
		Control negationCombo = createNegationCombo(main);
		negationCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text text = new Text(main, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				fActionName = text.getText();
				validate();
			}
			
		});
		if (fActionName != null)
			text.setText(fActionName);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#doCreateFilter()
	 */
	@Override
	protected IStateSpaceFilter doCreateFilter() {
		return FilterFactory.createActionFilter(fActionName, fIncoming);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#getAffirmedString()
	 */
	@Override
	protected String getAffirmedString() {
		return AFFIRMED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#getLabel()
	 */
	@Override
	public String getLabel() {
		return "States " + getDescription() + " " + getParameter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#getMemento(org.eclipse.ui.IMemento)
	 */
	@Override
	protected void getMemento(IMemento memento) {
		IMemento mem = memento.createChild(TAG_ACTION_FILTER);
		mem.putString(TAG_ACTION_NAME, fActionName);
		mem.putString(TAG_NEGATION, Boolean.toString(negation));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#getNegatedString()
	 */
	@Override
	protected String getNegatedString() {
		return NEGATED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#getParameter()
	 */
	@Override
	protected String getParameter() {

		return fActionName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#setFilterValidatorListener(uk.ac.ed.inf.pepa.eclipse.ui.internal.IFilterValidatorListener)
	 */
	@Override
	public void setFilterValidatorListener(IFilterValidatorListener listener) {
		this.fListener = listener;
		validate();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.AbstractConfigurableStateSpaceFilter#setMemento(org.eclipse.ui.IMemento)
	 */
	@Override
	protected void setMemento(IMemento memento) {
		IMemento mem = memento.getChild(TAG_ACTION_FILTER);
		fActionName = mem.getString(TAG_ACTION_NAME);
		negation = Boolean.parseBoolean(mem.getString(TAG_NEGATION));
	}
	
	private void validate() {
		String message = null;
		if (fActionName == null || fActionName.length()==0)
			message = "Please insert action name";
		fListener.filterValidated(message);
		
	}

}
