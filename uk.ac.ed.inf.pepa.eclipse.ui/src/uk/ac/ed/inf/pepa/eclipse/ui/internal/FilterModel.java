/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpaceFilter;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.ui.Activator;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterModel;
import uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer.RuleLabelProvider;

public class FilterModel implements IFilterModel {

	/*
	 * MEMENTO TAGS
	 */
	private static final String TAG_FILTERS = "filters";

	private static final String TAG_NAME = "name";

	private static final String TAG_ORED = "or";

	private static final String TAG_FILTER = "filter";

	private static final String TAG_TYPE = "filterType";

	private static final String TAG_SPECIFIC_MEMENTO = "specificMemento";

	private IProcessAlgebraModel model;

	private final List<AbstractConfigurableStateSpaceFilter> filters = new ArrayList<AbstractConfigurableStateSpaceFilter>();

	private boolean orFilters = false; // default value

	private boolean calledProgrammatically = false;

	private String name = "";

	class FilterDialog extends TitleAreaDialog {

		private Map<String, String> tagToDescription = new HashMap<String, String>();

		private void fillHashMap() {
			tagToDescription.put(FilterFactory.TAG_SEQUENTIAL_COMPONENT,
					"Sequential components");
			tagToDescription.put(FilterFactory.TAG_STEADY_STATE,
					"State whose steady-state probability");
			tagToDescription.put(FilterFactory.TAG_PATTERN_MATCHING,
					"Pattern matching");
			tagToDescription.put(FilterFactory.TAG_UNNAMED_PROCESSES,
					"Unnamed processes");
			tagToDescription.put(FilterFactory.TAG_INCOMING_ACTION, "Incoming transitions");
			tagToDescription.put(FilterFactory.TAG_OUTGOING_ACTION, "Outgoing transitions");
		}

		private final static String DIALOG_SETTINGS_NAME = "FILTER_MODEL_DIALOG";

		private Button buttonMatchAny;

		private Button buttonMatchAll;

		private Button buttonRemoveRule;

		private NewRuleAction newRuleAction;

		private EditRuleAction editRuleAction;

		private RemoveRuleAction removeRuleAction;

		private ListViewer ruleListViewer;

		/**
		 * Business model object for the list viewer
		 */
		private ArrayList<AbstractConfigurableStateSpaceFilter> rules;

		private IInputValidator textValidator;

		private Text nameText;

		protected FilterDialog(Shell parentShell, IInputValidator validator) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
			this.textValidator = validator;
			fillHashMap();
		}

		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("Filter Rules");
		}

		protected void okPressed() {
			updateFilters();
			super.okPressed();
		}

		private void updateFilters() {
			filters.clear();
			for (AbstractConfigurableStateSpaceFilter filter : rules) {
				filters.add(filter);
			}
		}

		protected IDialogSettings getDialogBoundsSettings() {
			IDialogSettings settings = Activator.getDefault()
					.getDialogSettings();
			IDialogSettings section = settings.getSection(DIALOG_SETTINGS_NAME);
			if (section == null) {
				section = settings.addNewSection(DIALOG_SETTINGS_NAME);
			}
			return section;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
		 */
		protected Control createDialogArea(Composite parent) {
			Composite composite = new Composite(parent, SWT.NULL);
			setTitle("Configure Filter Rules");
			GridLayout layout = new GridLayout();
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));

			Composite nameComp = new Composite(composite, SWT.NULL);
			nameComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout nameLayout = new GridLayout();
			nameLayout.numColumns = 2;
			nameComp.setLayout(nameLayout);
			Label nameLabel = new Label(nameComp, SWT.NULL);
			nameLabel.setText("Filter rule name:");
			nameLabel.setLayoutData(new GridData());
			nameText = new Text(nameComp, SWT.BORDER);
			nameText.setText(name);

			nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Group orGroup = new Group(composite, SWT.NULL | SWT.SHADOW_NONE);
			GridLayout orLayout = new GridLayout();
			orLayout.numColumns = 2;
			orLayout.makeColumnsEqualWidth = true;
			orGroup.setLayout(orLayout);
			GridData orData = new GridData(GridData.FILL_HORIZONTAL);
			orGroup.setLayoutData(orData);

			buttonMatchAll = new Button(orGroup, SWT.RADIO);
			buttonMatchAll.setText("Match all of the following");
			GridData allData = new GridData(GridData.FILL_HORIZONTAL);
			allData.horizontalAlignment = SWT.LEFT;
			buttonMatchAll.setLayoutData(allData);

			buttonMatchAny = new Button(orGroup, SWT.RADIO);
			buttonMatchAny.setText("Match any of the following");
			GridData anyData = new GridData(GridData.FILL_HORIZONTAL);
			anyData.horizontalAlignment = SWT.RIGHT;
			buttonMatchAny.setLayoutData(anyData);

			Composite filterComposite = new Composite(composite, SWT.NULL);
			filterComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout filterCompositeLayout = new GridLayout();
			filterCompositeLayout.numColumns = 2;
			filterCompositeLayout.marginWidth = 0;
			filterComposite.setLayout(filterCompositeLayout);
			final org.eclipse.swt.widgets.List filterList = new org.eclipse.swt.widgets.List(
					filterComposite, SWT.SINGLE | SWT.BORDER);
			filterList.setLayoutData(new GridData(GridData.FILL_BOTH));

			ruleListViewer = new ListViewer(filterList);
			ruleListViewer.setContentProvider(new IStructuredContentProvider() {

				public Object[] getElements(Object inputElement) {
					List<AbstractConfigurableStateSpaceFilter> list = (List<AbstractConfigurableStateSpaceFilter>) inputElement;
					return list
							.toArray(new AbstractConfigurableStateSpaceFilter[list
									.size()]);
				}

				public void dispose() {
				}

				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
				}

			});
			ruleListViewer.setLabelProvider(new RuleLabelProvider());

			ruleListViewer
					.addSelectionChangedListener(new ISelectionChangedListener() {

						public void selectionChanged(SelectionChangedEvent event) {
							IStructuredSelection sel = (IStructuredSelection) event
									.getSelection();
							if (!sel.isEmpty()) {
								buttonRemoveRule.setEnabled(true);
							} else {
								buttonRemoveRule.setEnabled(false);
							}

						}

					});

			ruleListViewer.addDoubleClickListener(new IDoubleClickListener() {

				public void doubleClick(DoubleClickEvent event) {
					IStructuredSelection sel = (IStructuredSelection) event
							.getSelection();
					if (sel.isEmpty())
						return;
					AbstractConfigurableStateSpaceFilter rule = (AbstractConfigurableStateSpaceFilter) sel
							.getFirstElement();
					editRule(rule);

				}

			});

			reconcileTableWithModel();

			createActions();

			createContextMenu();

			Composite filterTableButtons = new Composite(filterComposite,
					SWT.NULL);
			filterTableButtons.setLayoutData(new GridData(
					GridData.FILL_VERTICAL));
			RowLayout compLayout = new RowLayout(SWT.VERTICAL);
			compLayout.fill = true;
			filterTableButtons.setLayout(compLayout);

			buttonRemoveRule = new Button(filterTableButtons, SWT.PUSH);
			buttonRemoveRule.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event event) {
					performRemoveRule();
				}

			});
			buttonRemoveRule.setText("-");
			buttonRemoveRule.setToolTipText("Remove this rule");

			reconcileButtonStatusWithModel();

			buttonMatchAll.addListener(SWT.Selection, listener);
			buttonMatchAny.addListener(SWT.Selection, listener);

			return composite;
		}

		/**
		 * As NewExperiment in ExperimentPage
		 * 
		 * @author mtribast
		 * 
		 */
		class NewRuleAction extends Action implements IMenuCreator {

			private Menu menu;

			public NewRuleAction() {
				super("", IAction.AS_DROP_DOWN_MENU);
				this.setText("New");
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
				for (Map.Entry<String, String> entry : tagToDescription
						.entrySet()) {
					ActionContributionItem item = new ActionContributionItem(
							new AddAction(entry.getKey(), entry.getValue()));
					item.fill(menu, -1);
				}

				return menu;
			}

			private class AddAction extends Action {

				private String tag;

				AddAction(String tag, String description) {
					this.tag = tag;
					this.setText(description);
				}

				public void run() {
					AbstractConfigurableStateSpaceFilter filter = FilterFactory
							.createFilter(model, tag);
					if (filter == null) {
						PepaLog.logError(new IllegalArgumentException("Tag "
								+ tag + " not recognised as filter"));
						return;
					}
					FilterSettingDialog dialog = new FilterSettingDialog(
							getShell(), filter);
					if (dialog.open() == dialog.OK) {
						// Create Rule and update business model
						rules.add(filter);
						ruleListViewer.refresh();
					}

				}
			}

		}

		class EditRuleAction extends Action {

			public EditRuleAction() {
				setText("Edit");
			}

			public void run() {
				IStructuredSelection sel = (IStructuredSelection) ruleListViewer
						.getSelection();
				if (sel.isEmpty())
					return;
				editRule((AbstractConfigurableStateSpaceFilter) sel
						.getFirstElement());
			}
		}

		private void editRule(AbstractConfigurableStateSpaceFilter rule) {
			FilterSettingDialog dialog = new FilterSettingDialog(getShell(),
					rule);
			if (dialog.open() == OK) {
				ruleListViewer.refresh();
			}
		}

		class RemoveRuleAction extends Action {

			public RemoveRuleAction() {
				setText("Delete");
			}

			public void run() {
				IStructuredSelection sel = (IStructuredSelection) ruleListViewer
						.getSelection();
				if (sel.isEmpty())
					return;
				performRemoveRule();
			}
		}

		class FilterSettingDialog extends TitleAreaDialog {

			private AbstractConfigurableStateSpaceFilter filter;

			private IFilterValidatorListener listener = new IFilterValidatorListener() {

				public void filterValidated(String message) {
					FilterSettingDialog.this.getButton(Dialog.OK).setEnabled(
							message == null);
					setErrorMessage(message);
				}

			};

			protected FilterSettingDialog(Shell parentShell,
					AbstractConfigurableStateSpaceFilter filter) {
				super(parentShell);
				this.filter = filter;
			}

			protected void configureShell(Shell newShell) {
				/* Set Title */
				super.configureShell(newShell);
				newShell.setText("Filter Settings");
			}

			protected Control createContents(Composite parent) {
				Control control = super.createContents(parent);
				setTitle("Configure Filter Settings");
				filter.setFilterValidatorListener(listener);
				return control;
			}

			protected Control createDialogArea(Composite parent) {
				Composite composite = new Composite(parent, SWT.NULL);
				GridLayout layout = new GridLayout();
				composite.setLayout(layout);
				composite.setLayoutData(new GridData(GridData.FILL_BOTH));
				filter.createGUI(composite);

				return composite;
			}

		}

		private void createActions() {
			newRuleAction = new NewRuleAction();
			editRuleAction = new EditRuleAction();
			removeRuleAction = new RemoveRuleAction();
		}

		private void createContextMenu() {
			MenuManager manager = new MenuManager();
			Menu menu = manager.createContextMenu(ruleListViewer.getControl());
			ruleListViewer.getControl().setMenu(menu);
			manager.setRemoveAllWhenShown(true);
			manager.addMenuListener(new IMenuListener() {

				public void menuAboutToShow(IMenuManager manager) {
					manager.add(newRuleAction);
					manager.add(new Separator());
					manager.add(editRuleAction);
					editRuleAction.setEnabled(!ruleListViewer.getSelection()
							.isEmpty());
					manager.add(removeRuleAction);
					removeRuleAction.setEnabled(editRuleAction.isEnabled());
				}

			});

		}

		protected Control createContents(Composite parent) {
			Control control = super.createContents(parent);

			nameText.addListener(SWT.Modify, new Listener() {

				public void handleEvent(Event event) {
					handleTextModified();
				}

			});
			handleTextModified();
			return control;
		}

		private void handleTextModified() {
			final Button okButton = getButton(IDialogConstants.OK_ID);
			String newText = nameText.getText().trim();
			String message = textValidator.isValid(newText);
			if (message == null) {
				name = nameText.getText();
				okButton.setEnabled(true);
				setErrorMessage(null);
			} else {
				setErrorMessage(message);
				okButton.setEnabled(false);
			}
		}

		/*
		 * Called at dialog creation time to fill the table with the initial
		 * model objects taken from the filter model
		 */
		private void reconcileTableWithModel() {
			rules = new ArrayList<AbstractConfigurableStateSpaceFilter>();
			for (int i = 0; i < filters.size(); i++)
				rules.add(filters.get(i));
			ruleListViewer.setInput(rules);
		}

		/*
		 * Called by the listener to the '-' button. The selected rule is
		 * deleted
		 */
		private void performRemoveRule() {
			IStructuredSelection sel = (IStructuredSelection) ruleListViewer
					.getSelection();
			Assert.isTrue(sel.size() == 1);
			AbstractConfigurableStateSpaceFilter selectedRule = (AbstractConfigurableStateSpaceFilter) sel
					.getFirstElement();
			ruleListViewer.remove(selectedRule);
			rules.remove(selectedRule);
		}

		private Listener listener = new Listener() {
			public void handleEvent(Event event) {
				/*
				 * Otherwise it would involve loops, ie setWidgetValues set text
				 * whose changes are notified to this listener which sets the
				 * model to its previous values again
				 */
				if (calledProgrammatically) {
					return;
				}
				orFilters = buttonMatchAny.getSelection();
			}
		};

		private void reconcileButtonStatusWithModel() {
			buttonMatchAny.setSelection(orFilters);
			buttonMatchAll.setSelection(!orFilters);
			buttonRemoveRule.setEnabled(!ruleListViewer.getSelection()
					.isEmpty());
		}

	}

	/**
	 * Create a filter model object for the state space view.
	 * <p>
	 * Create an instance of it only once, so that the content will be always
	 * persistent across different invocations of the same dialog.
	 * 
	 * @param model
	 */
	public FilterModel(IProcessAlgebraModel model) {
		/* caches the model */
		this.model = model;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.IFilterModel#createDialogControl(org.eclipse.swt.widgets.Shell, org.eclipse.jface.dialogs.IInputValidator)
	 */
	public Dialog createDialogControl(Shell shell, IInputValidator textTalidator) {

		return new FilterDialog(shell, textTalidator);
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.IFilterModel#getName()
	 */
	public String getName() {
		return this.name;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.internal.IFilterModel#getFilters()
	 */
	public IStateSpaceFilter[] getFilters() {
		IStateSpaceFilter[] filterArray = new IStateSpaceFilter[filters.size()];
		int i = 0;
		for (AbstractConfigurableStateSpaceFilter f : filters)
			filterArray[i++] = f.getFilter();
		if (filterArray.length != 0 && orFilters)
			return new IStateSpaceFilter[] { uk.ac.ed.inf.pepa.ctmc.derivation.FilterFactory
					.createOr(filterArray) };
		return filterArray;
	}

	/**
	 * Set the state of the filter with the contents of this memento
	 * 
	 * @param memento
	 */
	public void setMemento(IMemento memento) {
		filters.clear();
		IMemento filtersMemento = memento.getChild(TAG_FILTERS);
		name = filtersMemento.getString(TAG_NAME);
		orFilters = Boolean.parseBoolean(filtersMemento.getString(TAG_ORED));
		for (IMemento filterMemento : filtersMemento.getChildren(TAG_FILTER)) {
			AbstractConfigurableStateSpaceFilter filter = FilterFactory
					.createFilter(model, filterMemento.getString(TAG_TYPE));
			filter.setMemento(filterMemento.getChild(TAG_SPECIFIC_MEMENTO));
			filters.add(filter);
		}

	}

	/**
	 * Get the state of the filter and saves it on the current memento
	 * 
	 * @param memento
	 */
	public void getMemento(IMemento memento) {
		IMemento filtersMemento = memento.createChild(TAG_FILTERS);
		filtersMemento.putString(TAG_ORED, "" + orFilters);
		filtersMemento.putString(TAG_NAME, name);
		for (AbstractConfigurableStateSpaceFilter filter : filters) {
			IMemento filterMemento = filtersMemento.createChild(TAG_FILTER);
			filterMemento.putString(TAG_TYPE, FilterFactory
					.getTagForClass(filter));
			IMemento specificMemento = filterMemento
					.createChild(TAG_SPECIFIC_MEMENTO);
			filter.getMemento(specificMemento);

		}
	}

}
