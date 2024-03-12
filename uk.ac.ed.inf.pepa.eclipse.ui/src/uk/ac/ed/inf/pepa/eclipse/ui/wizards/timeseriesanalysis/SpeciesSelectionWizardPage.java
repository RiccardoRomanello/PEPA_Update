/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis;

import java.util.*;
import java.util.List;

import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.core.PepatoOptionForwarder;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;
import uk.ac.ed.inf.pepa.sba.Mapping;
import uk.ac.ed.inf.pepa.sba.SBAParseException;

/**
 * 
 * @author ajduguid
 * 
 */
public class SpeciesSelectionWizardPage extends WizardPage {

	private class Component {
		Component[] children = null;
		String cooperation = null, name = null, displayName = null;
		Component parent = null;
		boolean unlabelled = false;
	}

	private class ComponentLabelProvider extends LabelProvider implements
			IFontProvider {

		Font bold;

		ComponentLabelProvider(Font defaultFont) {
			FontData boldFontData = defaultFont.getFontData()[0];
			boldFontData.setStyle(SWT.BOLD);
			bold = new Font(defaultFont.getDevice(),
					new FontData[] { boldFontData });
		}

		public Font getFont(Object element) {
			Component c = (Component) element;
			if (!c.unlabelled && c.cooperation == null && c.children == null)
				return bold;
			return null;
		}

		public Image getImage(Object element) {
			if (((Component) element).cooperation != null)
				return ImageManager.getInstance().getImage(ImageManager.COOP);
			return null;
		}

		public String getText(Object element) {
			Component component = (Component) element;
			String s = "";
			if (component.cooperation != null)
				s = component.cooperation;
			else {
				if(component.unlabelled)
					s += "\t\t";
				s += component.displayName;
				if (component.name != null
						&& !component.displayName.equals(component.name))
					s += " (" + component.name + ")";
			}
			return s;
		}
	}

	private class ComponentTreeContentProvider extends ArrayContentProvider
			implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (!amalgamateCheckbox.getSelection())
				return ((Component) parentElement).children;
			else {
				ArrayList<Component> al = new ArrayList<Component>();
				for (Component child : ((Component) parentElement).children)
					if (!child.unlabelled)
						al.add(child);
				return al.toArray();
			}
		}

		public Object getParent(Object element) {
			return ((Component) element).parent;
		}

		public boolean hasChildren(Object element) {
			return (((Component) element).children != null);
			/*
			if (((Component) element).children != null) {
				for (Component child : ((Component) element).children)
					if (showingUnlabelled || !child.unlabelled)
						return true;
			}
			return false;
			*/
		}
	}

	public class SpeciesSelectionEvent {
		Map<String, Set<String>> amalgamations;
		ArrayList<String> ordering;

		SpeciesSelectionEvent(Map<String, Set<String>> amalgamations,
				ArrayList<String> ordering) {
			this.amalgamations = amalgamations;
			this.ordering = ordering;
		}
	}

	public interface SpeciesSelectionListener {
		public void updateSelection(SpeciesSelectionEvent event);
	}

	public final static String name = "SpeciesSelection";

	CheckboxTreeViewer checkboxTreeViewer;
	Component[] components = null;
	Composite composite;

	private List<SpeciesSelectionListener> listeners;

	IPepaModel model;

	OptionsMap optionsMap;

	Button selectAllButton, amalgamateCheckbox;

	boolean unlabelledPresent, listenersUpdated = false;

	Label unlabelledlabel;

	protected SpeciesSelectionWizardPage(IPepaModel model, OptionsMap optionsMap) {
		super(name);
		this.model = model;
		this.optionsMap = optionsMap;
		listeners = new ArrayList<SpeciesSelectionListener>();
		setTitle(WizardMessages.SPECIES_SELECTION_WIZARD_PAGE_TITLE);
		setDescription(WizardMessages.SPECIES_SELECTION_WIZARD_PAGE_DESCRIPTION);
	}

	public void addListener(SpeciesSelectionListener listener) {
		listeners.add(listener);
	}

	private void checkPage() {
		setPageComplete(false);
		Component component;
		ArrayList<String> selectedComponents = new ArrayList<String>();
		// for loop doesn't account for unlabelled components
		for (Object o : checkboxTreeViewer.getCheckedElements()) {
			component = (Component) o;
			if (component.cooperation == null && component.children == null)
				selectedComponents.add((component.name != null ? component.name
						: component.displayName));
		}
		if (selectedComponents.size() > 0) {
			setPageComplete(true);
			ArrayList<String> selectedComponents2 = updateAmalgamations(selectedComponents);
			selectedComponents2.addAll(selectedComponents);
			optionsMap.setValue(OptionsMap.Parameter.Components,
					selectedComponents2.toArray(new String[] {}));
		}
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());
		// Widget generation
		checkboxTreeViewer = new CheckboxTreeViewer(composite);
		checkboxTreeViewer.setLabelProvider(new ComponentLabelProvider(
				getFont()));
		checkboxTreeViewer
				.setContentProvider(new ComponentTreeContentProvider());
		selectAllButton = new Button(composite, SWT.PUSH);
		selectAllButton.setText("Select all");
		final Button selectLabelled = new Button(composite, SWT.PUSH);
		selectLabelled.setText("Select labelled components only");
		selectLabelled
				.setToolTipText("Selects only the components in bold (labelled components) contained within the tree.");
		selectLabelled.setVisible(false);
		unlabelledlabel = new Label(composite, SWT.WRAP);
		unlabelledlabel
				.setText("* Statistics of unlabelled components will be merged with their labelled component.");
		amalgamateCheckbox = new Button(composite, SWT.CHECK);
		amalgamateCheckbox
				.setToolTipText("Allows toggling of unlabelled states. If unlabelled states are not shown they are subsumed by their labelled state.");
		try {
			amalgamateCheckbox.setSelection(Boolean.valueOf(PepatoOptionForwarder.getOptionFromPersistentResource(model.getUnderlyingResource(), getName() + ".amalgamate")));
		} catch (Exception e) {
			PepaLog.logError(e);
		}
		unlabelledlabel.setVisible(amalgamateCheckbox.getSelection());

		// Listener generation
		checkboxTreeViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				Component component = (Component) event.getElement();
				if (component.cooperation != null)
					checkboxTreeViewer.setChecked(component, false);
				else if (component.children != null) {
					boolean state = event.getChecked();
					checkboxTreeViewer.setChecked(component, state);
					for (Component child : component.children)
						if (!(amalgamateCheckbox.getSelection() && child.unlabelled))
							checkboxTreeViewer.setChecked(child, state);
					checkboxTreeViewer.setGrayed(component, false);
				} else if (component.parent != null) {
					Component parent = component.parent;
					int count = 0;
					for (Component child : parent.children)
						if (checkboxTreeViewer.getChecked(child))
							count++;
					checkboxTreeViewer.setChecked(parent, (count > 0 ? true
							: false));
					checkboxTreeViewer
							.setGrayed(
									parent,
									((count == parent.children.length || count == 0) ? false
											: true));
				}
				// Alter selectAllButtonText if required
				boolean all = true;
				for (Component c : components)
					if (c.cooperation == null)
						if (!checkboxTreeViewer.getChecked(c)
								|| checkboxTreeViewer.getGrayed(c))
							all = false;
				selectAllButton.setText((all ? "Deselect all" : "Select all"));
				composite.layout();
				checkPage();
			}
		});
		selectAllButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				boolean select = !selectAllButton.getText().startsWith("De");
				for (Component c : components) {
					if (c.cooperation == null) {
						checkboxTreeViewer.setChecked(c, select);
						for (Component child : c.children)
							if (!(amalgamateCheckbox.getSelection() && child.unlabelled))
								checkboxTreeViewer.setChecked(child, select);
						checkboxTreeViewer.setGrayed(c, false);
					}
				}
				selectAllButton
						.setText((select ? "Deselect all" : "Select all"));
				composite.layout();
				checkPage();
			}
		});
		amalgamateCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				boolean showingUnlabelled = ((Button) e.widget).getSelection();
				amalgamateCheckbox.setText(showingUnlabelled ? "Amalgamate components *" : "Amalgamate components (labelled components in bold)");
				unlabelledlabel.setVisible(unlabelledPresent
						&& showingUnlabelled);
				selectLabelled.setVisible(unlabelledPresent
						&& !showingUnlabelled);
				updateTree();
				composite.layout();
				checkPage();
			}
		});
		selectLabelled.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				for (Component c : components) {
					if (c.cooperation == null) {
						checkboxTreeViewer.setChecked(c, true);
						checkboxTreeViewer.setGrayed(c, false);
						for (Component child : c.children)
							if (!child.unlabelled)
								checkboxTreeViewer.setChecked(child, true);
							else {
								checkboxTreeViewer.setChecked(child, false);
								checkboxTreeViewer.setGrayed(c, true);
							}

					}
				}
				selectAllButton.setText("Deselect all");
				composite.layout();
				checkPage();
			}
		});
		// Layout
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.left = new FormAttachment(0);
		amalgamateCheckbox.setLayoutData(formData);
		formData = new FormData();
		formData.top = new FormAttachment(amalgamateCheckbox);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		formData.bottom = new FormAttachment(selectAllButton);
		checkboxTreeViewer.getTree().setLayoutData(formData);
		formData = new FormData();
		formData.bottom = new FormAttachment(unlabelledlabel);
		formData.left = new FormAttachment(0);
		selectAllButton.setLayoutData(formData);
		formData = new FormData();
		formData.left = new FormAttachment(selectAllButton);
		formData.bottom = new FormAttachment(unlabelledlabel);
		selectLabelled.setLayoutData(formData);
		formData = new FormData();
		formData.bottom = new FormAttachment(100);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		unlabelledlabel.setLayoutData(formData);
		setControl(composite);
	}

	void ensureParse() {
		if (!listenersUpdated)
			processModel();
	}

	private void generateDataModel() {
		Mapping mapping = model.getMapping();
		if (mapping == null || mapping.length() == 0) {
			PepaLog.logError(new NullPointerException(
					"Error while returning components."));
			getWizard().dispose();
		}
		components = new Component[mapping.length() * 2 - 1];
		int index = 0, index2;
		HashSet<String> componentsToRecord = new HashSet<String>();
		for (String s : (String[]) optionsMap
				.getValue(OptionsMap.Parameter.Components))
			componentsToRecord.add(s);
		ArrayList<Component> labelled = new ArrayList<Component>();
		ArrayList<Component> unlabelled = new ArrayList<Component>();
		unlabelledPresent = false;
		HashMap<String, String> tHashMap = new HashMap<String, String>();
		String uniqueName, tString;
		String[] tsa;
		while (mapping != null) {
			components[index] = new Component();
			components[index].displayName = mapping.originalRepresentation;
			components[index].children = new Component[mapping.labelled.size()
					+ mapping.unlabelled.size()];
			index2 = 0;
			tHashMap.clear();
			tsa = mapping.labelled.keySet().toArray(new String[] {});
			for(String s : mapping.unlabelled.keySet()) {
				tString = "";
				for(String s2 : tsa)
					if(s.startsWith((s2.charAt(0) == '\"' ? s2.substring(0, s2.length() - 1) : s2)) && s2.length() > tString.length())
						tString = s2;
				tHashMap.put(s, tString);
			}
			for (Map.Entry<String, String> me : mapping.labelled.entrySet()) {
				components[index].children[index2] = new Component();
				uniqueName = me.getKey();
				components[index].children[index2].name = uniqueName;
				components[index].children[index2].displayName = me.getValue();
				components[index].children[index2].parent = components[index];
				if (componentsToRecord.contains(uniqueName))
					labelled.add(components[index].children[index2]);
				index2++;
				for (Map.Entry<String, String> me2 : mapping.unlabelled
						.entrySet())
					if (tHashMap.get(me2.getKey()).equals(uniqueName)) {
						unlabelledPresent = true;
						components[index].children[index2] = new Component();
						components[index].children[index2].name = me2.getKey();
						components[index].children[index2].displayName = me2
								.getValue();
						components[index].children[index2].parent = components[index];
						components[index].children[index2].unlabelled = true;
						if (componentsToRecord.contains(me2.getKey()))
							unlabelled.add(components[index].children[index2]);
						index2++;
					}
			}
			index++;
			if (mapping.cooperation != null) {
				components[index] = new Component();
				components[index].cooperation = mapping.cooperation;
				index++;
			}
			mapping = mapping.next;
		}
		checkboxTreeViewer.setInput(components);
		amalgamateCheckbox.setEnabled(unlabelledPresent);
		amalgamateCheckbox.setVisible(unlabelledPresent);
		unlabelledlabel.setVisible(unlabelledPresent);
		Event event = new Event();
		event.widget = amalgamateCheckbox;
		amalgamateCheckbox.notifyListeners(SWT.Selection, event);
		for (Component c : labelled)
			checkboxTreeViewer.setChecked(c, true);
		if (!amalgamateCheckbox.getSelection())
			for (Component c : unlabelled)
				checkboxTreeViewer.setChecked(c, true);
		Tree tree = checkboxTreeViewer.getTree();
		// hack for handling cmdl/reagent-centric approach
		if (tree.getItemCount() == 1)
			tree.getItem(0).setExpanded(true);
	}

	void processModel() {
		generateDataModel();
		updateTree();
		checkPage();
	}
	
	void saveOptions() {
		try {
			PepatoOptionForwarder.saveOptionInPersistentResource(model
					.getUnderlyingResource(), getName() + ".amalgamate", Boolean.toString(amalgamateCheckbox.getSelection()));
		} catch (Exception e) {
			PepaLog.logError(e);
		}
	}


	/*
	 * public void performHelp() {
	 * PlatformUI.getWorkbench().getHelpSystem().displayHelp(Activator.ID +
	 * ".time_series_analysis"); //
	 * PlatformUI.getWorkbench().getHelpSystem().displayHelpResource("/uk.ac.ed.inf.pepa.eclipse.help/html/reference/timeSeriesAnalysis.html"); }
	 */

	public void setVisible(boolean visible) {
		if (visible) {
			try {
				IWizardPage previous = getPreviousPage();
				if (previous != null
						&& previous instanceof StoichiometricWizardPage)
					((StoichiometricWizardPage) previous).updateReactions();
				else
					model.sbaParse();
				if (model.generateReactions() || components == null) {
					processModel();
				}
			} catch(SBAParseException e) {
				PepaLog.logError("Parsing Exception", e);
				((Wizard) getWizard()).getShell().close();
				MessageBox warning = new MessageBox(Display.getDefault().getActiveShell(),SWT.ICON_WARNING);
				warning.setText("Error parsing PEPA file");
				warning.setMessage(e.getMessage() == null ? "no message" : e.getMessage());
				warning.open();
				return;
			} catch (Exception e) {
				PepaLog.logError(e);
				((Wizard) getWizard()).getShell().close();
				MessageBox warning = new MessageBox(Display.getDefault().getActiveShell(),SWT.ICON_WARNING);
				warning.setText("Error parsing PEPA file");
				warning.setMessage("Sorry. There exists an error not caught by the current static analysis. Please check the cooperation sets and use of passive rates.");
				warning.open();
				return;
			}
		}
		super.setVisible(visible);
	}

	private ArrayList<String> updateAmalgamations(
			ArrayList<String> selectedComponents) {
		listenersUpdated = true;
		Map<String, Set<String>> amalgamations = new HashMap<String, Set<String>>();
		HashSet<String> hashSet;
		ArrayList<String> unlabelledStates = new ArrayList<String>();
		int index;
		Component child;
		String componentName, childName;
		boolean amalgamting = amalgamateCheckbox.getSelection();
		for (Component parent : components)
			if (parent.children != null) {
				index = 0;
				while (index < parent.children.length) {
					child = parent.children[index];
					componentName = (child.name != null ? child.name
							: child.displayName);
					if (selectedComponents.contains(componentName)) {
						hashSet = new HashSet<String>();
						hashSet.add(componentName);
						amalgamations.put(componentName, hashSet);
						if (amalgamting)
							while ((index + 1) < parent.children.length
									&& parent.children[index + 1].unlabelled) {
								child = parent.children[++index];
								childName = (child.name != null ? child.name
										: child.displayName);
								hashSet.add(childName);
								unlabelledStates.add(childName);
							}
					}
					index++;
				}
			}
		SpeciesSelectionEvent speciesSelectionEvent = new SpeciesSelectionEvent(
				amalgamations, selectedComponents);
		for (SpeciesSelectionListener ssl : listeners)
			ssl.updateSelection(speciesSelectionEvent);
		return unlabelledStates;
	}

	private void updateTree() {
		boolean[] expanded = new boolean[components.length];
		Object[] checkedComponents = checkboxTreeViewer.getCheckedElements();
		for (int index = 0; index < expanded.length; index++)
			expanded[index] = checkboxTreeViewer
					.getExpandedState(components[index]);
		checkboxTreeViewer.setInput(components);
		// clear old selection
		for (Component c : components)
			if (c.cooperation != null)
				checkboxTreeViewer.setChecked(c, false);
		boolean amalgamting = amalgamateCheckbox.getSelection();
		// update tree viewer
		for (Object o : checkedComponents)
			if (!(amalgamting && ((Component) o).unlabelled))
				checkboxTreeViewer.setChecked(((Component) o), true);
		int checked, total;
		for (int index = 0; index < expanded.length; index++) {
			checkboxTreeViewer.setExpandedState(components[index],
					expanded[index]);
			if (components[index].children != null) {
				checked = total = 0;
				for (Component child : components[index].children)
					if (!(amalgamting && child.unlabelled)) {
						total++;
						if (checkboxTreeViewer.getChecked(child))
							checked++;
					}
				checkboxTreeViewer.setChecked(components[index],
						(checked > 0 ? true : false));
				checkboxTreeViewer.setGrayed(components[index], (checked > 0
						&& checked != total ? true : false));
			}
		}
	}
}
