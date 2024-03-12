/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Selects model items to use in the experiments.
 * <p>
 * Rate definitions and Aggregations only can be chosen.
 * 
 * @author mtribast
 * 
 */
public class ASTSelectionPage extends WizardPage {

	public static final String NAME = "ASTSelectionPage";
	
	private ISensibleNode[] sensibleNodes;

	private CheckboxTableViewer viewer;

	protected ASTSelectionPage(ISensibleNode[] sensibleNodes) {
		super(NAME);
		this.sensibleNodes = sensibleNodes;
		this.setTitle("Experimentation");
		this.setDescription("Select the elements to include in experiments");
	}

	private class ASTSelectionTableProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 0)
				if (element instanceof ISensibleNode)
					return ((ISensibleNode) element).getName();
				else 
					throw new IllegalArgumentException();
			return null;
		}
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new FillLayout());
		setControl(main);
		viewer = CheckboxTableViewer.newCheckList(main, SWT.NONE);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ASTSelectionTableProvider());
		viewer.setInput(sensibleNodes);
		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				validate();
			}

		});
		validate();
	}

	private void validate() {
		this.setPageComplete(viewer.getCheckedElements().length != 0);
	}

	public void setPageComplete(boolean complete) {
		super.setPageComplete(complete);
		if (complete)
			((ExperimentationWizard) getWizard())
					.updateSelectionPages(getCheckedNodes());
	}

	/**
	 * Return the nodes which experimentation has to be carried on
	 * 
	 * @return the checked node
	 */
	public Object[] getCheckedNodes() {
		return viewer.getCheckedElements();
	}

}
