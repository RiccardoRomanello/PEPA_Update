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
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public class PatternMatchingFilter extends AbstractConfigurableStateSpaceFilter {

	public PatternMatchingFilter(IProcessAlgebraModel model) {
		super(model);
	}

	private static final String LABEL = "States ";

	private static final String AFFIRMED = "which match";

	private static final String NEGATED = "which do not match";

	private static final String TAG_PATTERN_MATCHING = "patternMatchingFilter";

	private static final String TAG_PATTERN = "pattern";

	private static final String TAG_NEGATION = "negation";

	private String pattern;

	private IFilterValidatorListener validator;

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

		Label stateSelection = new Label(main, SWT.NULL);
		stateSelection.setText("Insert pattern:");

		stateSelection.setLayoutData(new GridData());
		final Text text = new Text(main, SWT.BORDER);
		// setText first, because it causes modify listener to be called.
		text.setText(pattern == null ? "" : pattern);
		text.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				pattern = text.getText();
				validate();
			}

		});
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	private void validate() {
		String message = null;
		if (pattern == null || pattern.equals("")) {
			message = "Insert pattern";
		} else {
			// check that all the components exist
			String[] componentNames = this.fModel.getStateSpace()
					.getComponentNames();

			String[] individualComponents = pattern
					.split(FilterFactory.VERTICAL_BAR);
			for (int i = 0; i < individualComponents.length; i++) {
				String comp = individualComponents[i].trim();
				if (!comp.equals(FilterFactory.WILDCARD)) {
					boolean found = false;
					for (String name : componentNames) {
						if (name.equals(comp)) {
							found = true;
							break;
						}
					}
					if (!found) {
						message = "Component " + comp + " does not exist";
						break;
					}
				}
			}
		}

		validator.filterValidated(message);
	}

	@Override
	protected String getAffirmedString() {
		return AFFIRMED;
	}

	@Override
	public String getLabel() {
		return LABEL + getDescription() + " " + getParameter();
	}

	@Override
	protected void getMemento(IMemento memento) {
		IMemento mem = memento.createChild(TAG_PATTERN_MATCHING);
		mem.putString(TAG_PATTERN, pattern);
		mem.putString(TAG_NEGATION, "" + negation);

	}

	@Override
	protected String getNegatedString() {
		return NEGATED;
	}

	@Override
	protected String getParameter() {
		return pattern;
	}

	@Override
	protected void setMemento(IMemento memento) {
		IMemento mem = memento.getChild(TAG_PATTERN_MATCHING);
		pattern = mem.getString(TAG_PATTERN);
		negation = Boolean.parseBoolean(mem.getString(TAG_NEGATION));

	}

	@Override
	public void setFilterValidatorListener(IFilterValidatorListener listener) {
		this.validator = listener;
		validate();

	}

	@Override
	protected IStateSpaceFilter doCreateFilter() {
		return FilterFactory.createPatternMatchingFilter(this.pattern);

	}

}
