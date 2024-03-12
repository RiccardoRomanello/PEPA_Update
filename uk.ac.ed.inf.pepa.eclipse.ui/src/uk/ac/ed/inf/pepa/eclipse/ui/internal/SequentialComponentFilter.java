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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;

import uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory.Operator;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;

public class SequentialComponentFilter extends
		AbstractConfigurableStateSpaceFilter {

	private static final String AFFIRMED = "which are in state";

	private static final String NEGATED = "which are not in state";

	private static final String TAG_SEQUENTIAL_COMPONENT = "sequentialComponentFilter";

	private static final String TAG_NAME = "processName";

	private static final String TAG_NEGATION = "negation";

	private static final String TAG_NUMBER_OF_COPIES = "numberOfCopies";

	private static final String TAG_OPERATOR = "operator";

	private String topLevelComponentName;

	private String numberOfCopies;

	private Operator operator;

	private Combo combo;

	private IFilterValidatorListener validator;

	private Combo operatorCombo;

	private Text numberOfCopiesText;

	public SequentialComponentFilter(IProcessAlgebraModel model) {
		super(model);
		numberOfCopies = "1";
		operator = Operator.EQ;
	}

	@Override
	protected void getMemento(IMemento memento) {
		IMemento mem = memento.createChild(TAG_SEQUENTIAL_COMPONENT);
		mem.putString(TAG_NAME, topLevelComponentName);
		mem.putString(TAG_NEGATION, "" + negation);
		mem.putString(TAG_NUMBER_OF_COPIES, numberOfCopies);
		mem.putString(TAG_OPERATOR, operator.name());
	}

	@Override
	protected void setMemento(IMemento memento) {
		IMemento mem = memento.getChild(TAG_SEQUENTIAL_COMPONENT);
		topLevelComponentName = mem.getString(TAG_NAME);
		negation = Boolean.parseBoolean(mem.getString(TAG_NEGATION));
		operator = Operator.valueOf(mem.getString(TAG_OPERATOR));
		numberOfCopies = mem.getString(TAG_NUMBER_OF_COPIES);
	}

	public void createGUI(Composite parent) {

		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		main.setLayout(layout);
		main.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(main, SWT.NULL);
		label.setText("Sequential components");
		label.setLayoutData(new GridData());

		Control negationCombo = createNegationCombo(main);
		negationCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label stateSelection = new Label(main, SWT.NULL);
		stateSelection.setText("Select state:");
		stateSelection.setLayoutData(new GridData());

		Control other = createControl(main);
		other.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label expressionSelection = new Label(main, SWT.NULL);
		expressionSelection.setText("Number of copies:");
		expressionSelection.setLayoutData(new GridData());

		Composite expressionComposite = new Composite(main, SWT.NULL);
		expressionComposite
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout expressionLayout = new GridLayout();
		expressionLayout.numColumns = 2;
		expressionLayout.makeColumnsEqualWidth = false;
		expressionLayout.marginHeight = expressionLayout.marginWidth = 0;
		expressionComposite.setLayout(expressionLayout);

		operatorCombo = new Combo(expressionComposite, SWT.READ_ONLY);
		operatorCombo.setLayoutData(new GridData());
		for (Operator o : Operator.values()) {
			operatorCombo.add(o.toString());
			operatorCombo.setData(o.toString(), o);
		}
		operatorCombo.setText(operator.toString());

		operatorCombo.addListener(SWT.Modify, new Listener() {

			public void handleEvent(Event event) {
				operator =(Operator) operatorCombo.getData(operatorCombo.getText());
				validate();
			}

		});

		numberOfCopiesText = new Text(expressionComposite, SWT.BORDER
				| SWT.RIGHT);
		numberOfCopiesText
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		numberOfCopiesText.setText(numberOfCopies);

		numberOfCopiesText.addListener(SWT.Modify, new Listener() {

			public void handleEvent(Event event) {
				numberOfCopies = numberOfCopiesText.getText();
				validate();
			}

		});

	}

	private void validate() {
		if (topLevelComponentName == null) {
			validator.filterValidated("Choose local state");
			return;
		}
		int copies = 0;
		try {
			copies = Integer.parseInt(numberOfCopies);
		} catch (Exception e) {
			validator.filterValidated("Insert a correct number of copies");
			return;
		}
		if (copies < 0) {
			validator.filterValidated("Number of copies cannot be negative");
			return;
		}
		validator.filterValidated(null); // OK
	}

	protected Control createControl(Composite parent) {
		combo = new Combo(parent, SWT.READ_ONLY);
		fillCombo(combo);
		// initialise the control
		if (this.topLevelComponentName != null)
			combo.setText(topLevelComponentName);
		// reconcile with the underlying model
		combo.addListener(SWT.Modify, new Listener() {

			public void handleEvent(Event event) {
				topLevelComponentName = combo.getText();
				validate();
			}
		});
		return combo;
	}

	private void fillCombo(Combo combo) {
		for (String s : this.fModel.getStateSpace().getComponentNames())
			combo.add(s);

	}

	public String toString() {
		StringBuffer buf = new StringBuffer(super.toString());
		buf.append("negation:" + negation + ",");
		buf.append("reference:" + this.topLevelComponentName);
		buf.append("copies" + this.numberOfCopies);
		buf.append("operator:" + this.operator);
		return buf.toString();
	}

	@Override
	protected void dispose() {
		super.dispose();
		if (combo != null && !combo.isDisposed())
			combo.dispose();
	}

	@Override
	protected String getAffirmedString() {
		return AFFIRMED;
	}

	@Override
	protected String getNegatedString() {
		return NEGATED;
	}

	@Override
	public String getParameter() {
		return topLevelComponentName;
	}

	public String getLabel() {
		return "Sequential components " + getDescription() + " "
				+ getParameter() + " (" + operator + " " + numberOfCopies + ")";
	}

	@Override
	public void setFilterValidatorListener(IFilterValidatorListener listener) {
		this.validator = listener;
		validate();
	}

	@Override
	protected IStateSpaceFilter doCreateFilter() {
		try {
			return FilterFactory.createSequentialComponentFilter(this.topLevelComponentName,
					this.operator, Integer.parseInt(this.numberOfCopies));
		} catch (Exception e) {
			return null;
		}

	}

}
