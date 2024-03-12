/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

public class ExperimentPage extends WizardPage implements IDynamicParent {

	private class ExperimentLabelProvider extends LabelProvider {

		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			IExperiment experiment = (IExperiment) element;
			return experiment.getName();
		}

	}

	private class NewExperimentAction extends Action implements IMenuCreator {

		private Menu menu;

		public NewExperimentAction() {
			super("", IAction.AS_DROP_DOWN_MENU);
			this.setText("New");
			/*
			 * this.setImageDescriptor(ImageManager.getInstance()
			 * .getImageDescriptor(ImageManager.CHART));
			 */
			this.setMenuCreator(this);
		}

		public void dispose() {
			if (menu != null)
				menu.dispose();

		}

		public Menu getMenu(Control parent) {
			return null;
		}

		public Menu getMenu(Menu parent) {
			if (menu != null)
				menu.dispose();

			menu = new Menu(parent);
			for (String description : experimentFactory.getDescriptions()) {
				ActionContributionItem item = new ActionContributionItem(
						new AddAction(description));
				item.fill(menu, -1);
			}

			return menu;
		}

		private class AddAction extends Action {
			private String description;

			AddAction(String experimentDescription) {
				this.description = experimentDescription;
				this.setText(experimentDescription);
			}

			/* Add and reveal the experiment */
			public void run() {
				IExperiment newExperiment = experimentFactory
						.createExperiment(description);
				/* fed with the current settings */
				newExperiment.setDynamicParent(ExperimentPage.this);
				newExperiment.setAvailableSettings(currentSettings);
				newExperiment.setPerformanceMetricFactory(performanceFactory);
				newExperiment.setExperimentPage(ExperimentPage.this);
				managedExperiments.add(newExperiment);
				updatePage();
				experiments
						.setSelection(new StructuredSelection(newExperiment));
			}
		}

	}

	private AbstractExperimentFactory experimentFactory;

	private AbstractPerformanceMetricFactory performanceFactory;

	private ArrayList<IExperiment> managedExperiments = new ArrayList<IExperiment>();

	private ISetting[] currentSettings = new ISetting[0];

	private ListViewer experiments;

	private NewExperimentAction newExperimentAction;

	private Action removeExperimentAction;

	private Composite experimentSettingsComposite;
	
	private Font boldFont;

	protected ExperimentPage(AbstractExperimentFactory experimentFactory,
			AbstractPerformanceMetricFactory performanceFactory) {
		super("Experiment Page");
		this.experimentFactory = experimentFactory;
		this.performanceFactory = performanceFactory;
		setTitle("Experiments");
		setDescription("");
		Assert.isNotNull(experimentFactory);
	}

	/**
	 * Called by the wizard to notify experiments of new settings.
	 * <p>
	 * The new settings are all correct, as this method is called on the
	 * nextPage of the last page of the ASTSettings
	 */
	public void updateAvailableSettings(ISetting[] newSettings) {
		currentSettings = newSettings;
		Assert.isNotNull(newSettings);
		for (IExperiment e : managedExperiments)
			e.setAvailableSettings(newSettings);
		updateParentState();
	}

	/**
	 * This method is called by the {@link ExperimentationWizard} when any new
	 * node is selected or existing ones are unselected.
	 * <p>
	 * The experiment pages reacts by passing this information on to the
	 * experiments in order to update the status of their control. In
	 * particular, the experiment should update their fnish status if they were
	 * previously set with ASTNodes that are no longer available.
	 * 
	 * @param astNodes
	 */
	public void updateAvailableNodes(Object[] astNodes) {
		for (IExperiment e : managedExperiments)
			e.setAvailableNodes(astNodes);
		updateParentState();
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		main.setLayout(layout);
		setControl(main);
		Composite lhs = new Composite(main, SWT.NULL);
		lhs.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout lhsLayout = new GridLayout();
		lhsLayout.numColumns = 1;
		lhs.setLayout(lhsLayout);

		Label listLabel = new Label(lhs, SWT.WRAP);
		listLabel.setText("All Experiments");
		// TIP How to make a font bold
		FontData data = listLabel.getFont().getFontData()[0];
		data.setStyle(SWT.BOLD);
		boldFont = new Font(listLabel.getFont().getDevice(), new FontData[] { data });
		listLabel.setFont(boldFont);
		listLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		/* It will hold the Experiment objects held by this wizard page */
		experiments = new ListViewer(lhs, SWT.BORDER | SWT.SINGLE);
		GridData listLayoutData = new GridData(GridData.FILL_BOTH);
		// listLayoutData.grabExcessHorizontalSpace = true;
		// listLayoutData.grabExcessVerticalSpace = true;
		experiments.getList().setLayoutData(listLayoutData);
		experiments.setLabelProvider(new ExperimentLabelProvider());
		experiments.setContentProvider(new ArrayContentProvider());
		experiments
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection selection = (IStructuredSelection) event
								.getSelection();
						if (!selection.isEmpty()) {
							IExperiment selectedExperiment = (IExperiment) selection
									.getFirstElement();
							showExperiment(selectedExperiment);

							/* DEBUG */
							setPageComplete(validate());
						}

					}

				});
		experiments.setInput(managedExperiments);

		experimentSettingsComposite = new Composite(main, SWT.NULL);
		experimentSettingsComposite.setLayoutData(new GridData(
				GridData.FILL_BOTH));
		GridLayout experimentSettingsLayout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		experimentSettingsComposite.setLayout(experimentSettingsLayout);

		createActions();

		createContextMenu();

		setPageComplete(validate());

	}
	
	public void dispose() {
		// dispose of the bold font
		if (boldFont != null && !boldFont.isDisposed())
			boldFont.dispose();
		super.dispose();
	}

	private void updatePage() {

		experiments.refresh();
		setPageComplete(validate());
	}

	private void createActions() {

		newExperimentAction = new NewExperimentAction();

		removeExperimentAction = new Action() {
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) experiments
						.getSelection();
				IExperiment experiment = (IExperiment) sel.getFirstElement();
				if (!managedExperiments.remove(experiment)) {
					org.eclipse.jface.dialogs.MessageDialog.openError(
							ExperimentPage.this.getShell(),
							"Experiment not found", experiment.getName()
									+ " not found");
				}
				showExperiment(null);
				updatePage();
				/* Set the focus to retrieve another experiment */
				experiments.getList().setFocus();

			}
		};
		removeExperimentAction.setText("Delete");

	}

	private void createContextMenu() {
		MenuManager manager = new MenuManager();
		Menu menu = manager.createContextMenu(experiments.getControl());
		experiments.getControl().setMenu(menu);
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {

			public void menuAboutToShow(IMenuManager manager) {
				fillActionsInContextMenu(manager);
			}

		});
	}

	private void showExperiment(IExperiment e) {

		for (Control child : this.experimentSettingsComposite.getChildren()) {
			if (!child.isDisposed())
				child.dispose();
		}

		/* Null called by delete action */
		if (e != null)
			e.createControl(this.experimentSettingsComposite);

		/*
		 * TIP To dinamically change the content of a composite See Snippet98
		 * http://www.eclipse.org/swt/snippets/
		 */
		this.experimentSettingsComposite.layout(true);

	}

	private void fillActionsInContextMenu(IMenuManager manager) {
		manager.add(newExperimentAction);
		if (!experiments.getSelection().isEmpty()) {
			manager.add(new Separator());
			manager.add(removeExperimentAction);
		}

	}

	private boolean validate() {
		if (managedExperiments.size() == 0)
			return false;
		for (IExperiment e : managedExperiments)
			if (!e.isCanRun())
				return false;
		return true;
	}

	public void updateParentState() {
		updatePage();
	}

	public IExperiment[] getExperiments() {
		return managedExperiments.toArray(new IExperiment[managedExperiments
				.size()]);
	}

}
