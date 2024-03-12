/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.core.PepatoOptionForwarder;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;

/**
 * 
 * @author ajduguid
 *
 */
public class FormSelectionWizardPage extends WizardPage {

	public static String name = "FormSelection";

	IPepaModel.PEPAForm chosenForm = null;

	IPepaModel model;

	protected FormSelectionWizardPage(IPepaModel model) {
		super(name);
		this.model = model;
		setTitle(WizardMessages.PEPAFORM_WIZARD_PAGE_TITLE);
		setDescription(WizardMessages.PEPAFORM_WIZARD_PAGE_DESCRIPTION);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		Composite formComposite = new Composite(composite, SWT.NONE);
		formComposite.setLayout(new RowLayout(SWT.VERTICAL));
		String persistentForm = null;
		try {
			persistentForm = PepatoOptionForwarder
					.getOptionFromPersistentResource(model
							.getUnderlyingResource(), getName() + ".form");
		} catch (Exception e) {
			PepaLog.logError(e);
		}
		if (persistentForm == null || persistentForm == "")
			persistentForm = IPepaModel.PEPAForm.PEPA.name();
		for (final IPepaModel.PEPAForm form : model.isSBAParseable()) {
			Button button = new Button(formComposite, SWT.RADIO);
			button.setText(form.toString());
			button.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					if (((Button) e.widget).getSelection()) {
						model.setForm(form);
						chosenForm = form;
					}
				}
			});
			if (form.name().equals(persistentForm)) {
				button.setSelection(true);
				model.setForm(form);
				chosenForm = form;
			}
		}
		setControl(composite);
	}

	public IPepaModel.PEPAForm getForm() {
		return chosenForm;
	}

	void saveOptions() {
		try {
			PepatoOptionForwarder.saveOptionInPersistentResource(model
					.getUnderlyingResource(), getName() + ".form", chosenForm
					.name());
		} catch (Exception e) {
			PepaLog.logError(e);
		}
	}
}
