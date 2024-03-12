/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.timeseriesanalysis;

import java.util.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.core.PepatoOptionForwarder;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.WizardMessages;
import uk.ac.ed.inf.pepa.sba.*;

/**
 * 
 * @author ajduguid
 * 
 */
public class StoichiometricWizardPage extends WizardPage {

	private class ReactionFilter extends ViewerFilter {

		String filterString = "";

		@Override
		public boolean select(Viewer viewer, Object parentElement,
				Object element) {
			if (!(element instanceof SBAReaction))
				return false;
			if (filterString == "")
				return true;
			SBAReaction r = (SBAReaction) element;
			if (r.getName().lastIndexOf(filterString) != -1)
				return true;
			if (r.toString().lastIndexOf(filterString) != -1)
				return true;
			return false;
		}

		public void updateFilter(String filter) {
			filterString = filter;
		}

	}

	private class ReactionTableCustomProvider extends OwnerDrawLabelProvider {// implements
																				// ITableLabelProvider{

		private class Style {
			int start, end;
			TextStyle type;

			Style(TextStyle type, int start, int end) {
				this.type = type;
				this.start = start;
				this.end = end;
			}

			final void set(TextLayout layout) {
				layout.setStyle(type, start, end);
			}
		}

		Display display;

		TextLayout layout;

		Map<SBAReaction, TextLayout> layouts;

		TextStyle plain, plainSelected, bold, boldSelected, underline,
				underlineSelected;

		StringBuilder sb;

		SBAReaction selected = null, reaction;

		LinkedList<Style> styles;

		ReactionTableCustomProvider(TableViewer tableViewer) {
			display = tableViewer.getControl().getDisplay();
			layouts = new HashMap<SBAReaction, TextLayout>();
			sb = new StringBuilder();
			styles = new LinkedList<Style>();
			FontRegistry fontRegistry = JFaceResources.getFontRegistry();
			Font font = fontRegistry.defaultFont();
			Color t = display.getSystemColor(SWT.COLOR_LIST_FOREGROUND), ts = display
					.getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT), b = display
					.getSystemColor(SWT.COLOR_LIST_BACKGROUND), bs = display
					.getSystemColor(SWT.COLOR_LIST_SELECTION);
			plain = new TextStyle(font, t, b);
			plainSelected = new TextStyle(font, ts, bs);
			underline = new TextStyle(font, t, b);
			underline.underline = true;
			underlineSelected = new TextStyle(font, ts, bs);
			underlineSelected.underline = true;
			font = fontRegistry.getBold(JFaceResources.DEFAULT_FONT);
			t = display.getSystemColor(SWT.COLOR_RED);
			bold = new TextStyle(font, t, b);
			ts = new Color(display, 255 - ts.getRed(), 255 - ts.getGreen(),
					255 - ts.getBlue());
			boldSelected = new TextStyle(font, ts, bs);

		}

		public void dispose() {
			super.dispose();
			for (TextLayout textLayout : layouts.values())
				textLayout.dispose();
		}

		@Override
		protected void measure(Event event, Object element) {
			reaction = (SBAReaction) element;
			Rectangle bounds = new Rectangle(0, 0, 0, 0);
			Point point;
			if (event.index == 0) {
				point = event.gc.stringExtent(reaction.getName());
				point.x += 20;
			} else {
				point = event.gc.stringExtent(reaction.toString());
				point.x += 50;
			}
			bounds.width = point.x;
			bounds.height = point.y * 2;
			event.setBounds(bounds);
		}

		@Override
		protected void paint(Event event, Object element) {
			reaction = (SBAReaction) element;
			layout = layouts.get(reaction);
			if (layout == null) {
				layout = new TextLayout(display);
				layouts.put(reaction, layout);
			}
			if (event.index == 0) {
				layout.setText(reaction.getName());
				layout.setStyle((reaction == selected ? plainSelected : plain),
						0, Integer.MAX_VALUE);
			} else {
				sb.setLength(0);
				styles.clear();
				int start = 0;
				for (SBAComponent reactant : reaction.getReactants()) {
					if (reactant.getStoichiometry() > 1) {
						sb.append(reactant.getStoichiometry()).append(".");
						styles.add(new Style(
								(reaction == selected ? boldSelected : bold),
								start, sb.length()));
						start = sb.length();
					}
					sb.append(reactant.getName());
					if (reactant.isCatalyst() || reactant.isInhibitor())
						styles.add(new Style(
								(reaction == selected ? underlineSelected
										: underline), start, sb.length()));
					else
						styles.add(new Style(
								(reaction == selected ? plainSelected : plain),
								start, sb.length()));
					start = sb.length();
					sb.append(" + ");
					styles.add(new Style((reaction == selected ? plainSelected
							: plain), start, sb.length()));
					start = sb.length();
				}
				if (reaction.getReactants().size() > 0) {
					sb.delete(sb.length() - 3, sb.length());
					start = sb.length();
					styles.removeLast();
				}
				sb.append(" -> ");
				styles.add(new Style((reaction == selected ? plainSelected
						: plain), start, sb.length()));
				start = sb.length();
				for (SBAComponent product : reaction.getProducts()) {
					if (product.getStoichiometry() > 1) {
						sb.append(product.getStoichiometry()).append(".");
						styles.add(new Style(
								(reaction == selected ? boldSelected : bold),
								start, sb.length()));
						start = sb.length();
					}
					sb.append(product.getName());
					if (product.isCatalyst() || product.isInhibitor())
						styles.add(new Style(
								(reaction == selected ? underlineSelected
										: underline), start, sb.length()));
					else
						styles.add(new Style(
								(reaction == selected ? plainSelected : plain),
								start, sb.length()));
					start = sb.length();
					sb.append(" + ");
					styles.add(new Style((reaction == selected ? plainSelected
							: plain), start, sb.length()));
					start = sb.length();
				}
				if (reaction.getProducts().size() > 0) {
					sb.delete(sb.length() - 3, sb.length());
					start = sb.length();
					styles.removeLast();
				}
				layout.setText(sb.toString());
				for (Style style : styles)
					style.set(layout);
			}
			layout.draw(event.gc, event.x, event.y);
		}

		public void update(ViewerCell cell) {
			cell.setText("");
		}
	}

	private class StoicDialog extends org.eclipse.jface.dialogs.Dialog {

		SBAReaction reaction;

		Map<Spinner, SBAComponent> spinnerMap;

		StoicDialog(Shell parent) {
			super(parent);
			spinnerMap = new HashMap<Spinner, SBAComponent>();
		}

		protected void buttonPressed(int buttonId) {
			if (buttonId == IDialogConstants.OK_ID)
				for (Map.Entry<Spinner, SBAComponent> me : spinnerMap
						.entrySet())
					me.getValue().setStoichiometry(me.getKey().getSelection());
			super.buttonPressed(buttonId);
		}

		protected Control createDialogArea(Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);
			GridLayout gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			composite.setLayout(gridLayout);
			final Composite reactants, products;
			reactants = new Composite(composite, SWT.NONE);
			gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			reactants.setLayout(gridLayout);
			GridData gridData = new GridData();
			gridData.verticalAlignment = SWT.FILL;
			gridData.grabExcessVerticalSpace = true;
			reactants.setLayoutData(gridData);
			Label label = new Label(reactants, SWT.NONE);
			label.setText("Reactants");
			gridData = new GridData();
			gridData.horizontalSpan = 2;
			label.setLayoutData(gridData);
			Label hiddenLabel, name;
			Spinner spinner;
			for (SBAComponent reactant : reaction.getReactants()) {
				if (reactant.isCatalyst() || reactant.isInhibitor()) {
					hiddenLabel = new Label(reactants, SWT.NONE);
					hiddenLabel.setVisible(false);
				} else {
					spinner = new Spinner(reactants, SWT.NONE);
					spinner.setValues(reactant.getStoichiometry(), 1,
							Integer.MAX_VALUE, 0, 1, 10);
					spinnerMap.put(spinner, reactant);
				}
				name = new Label(reactants, SWT.NONE);
				name.setText(reactant.getName());
				if (reactant.isCatalyst())
					name.setToolTipText("Catalyst");
				else if (reactant.isInhibitor())
					name.setToolTipText("Inhibitor");
			}
			products = new Composite(composite, SWT.NONE);
			gridLayout = new GridLayout();
			gridLayout.numColumns = 2;
			products.setLayout(gridLayout);
			gridData = new GridData();
			gridData.verticalAlignment = SWT.FILL;
			gridData.grabExcessVerticalSpace = true;
			products.setLayoutData(gridData);
			label = new Label(products, SWT.NONE);
			label.setText("Products");
			gridData = new GridData();
			gridData.horizontalSpan = 2;
			label.setLayoutData(gridData);
			for (SBAComponent product : reaction.getProducts()) {
				spinner = new Spinner(products, SWT.NONE);
				spinner.setValues(product.getStoichiometry(), 1,
						Integer.MAX_VALUE, 0, 1, 10);
				spinnerMap.put(spinner, product);
				name = new Label(products, SWT.NONE);
				name.setText(product.getName());
			}
			products.addPaintListener(new PaintListener() {
				public void paintControl(PaintEvent e) {
					e.gc.setLineWidth(2);
					e.gc.drawLine(0, 0, 0, products.getClientArea().height);
				}
			});
			return composite;
		}

		public void setReaction(SBAReaction reaction) {
			this.reaction = reaction;
			spinnerMap.clear();
		}
	}

	public final static String name = "Stoichiometry";

	boolean first = true;

	IPepaModel model;

	ReactionFilter reactionFilter;

	HashMap<TableItem, SBAReaction> reactionMap = new HashMap<TableItem, SBAReaction>();

	TableViewer reactionTable;

	StoicDialog stoicDialog;

	Map<String, Map<String, Integer>> stoicInfo = new HashMap<String, Map<String, Integer>>();

	StoichiometricWizardPage(IPepaModel model) {
		super(name);
		this.model = model;
		setTitle(WizardMessages.STOICHIOMETRIC_WIZARD_PAGE_TITLE);
		setDescription(WizardMessages.STOICHIOMETRIC_WIZARD_PAGE_DESCRIPTION);
		loadStoichiometry();
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FormLayout());
		stoicDialog = new StoicDialog(getShell());
		reactionTable = new TableViewer(composite, SWT.SINGLE
				| SWT.FULL_SELECTION);
		reactionTable.setContentProvider(new ArrayContentProvider());
		final ReactionTableCustomProvider rtcp = new ReactionTableCustomProvider(
				reactionTable);
		reactionTable.setLabelProvider(rtcp);
		// ReactionTableCustomProvider.setUpOwnerDraw(reactionTable);
		reactionTable
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						rtcp.selected = (SBAReaction) ((StructuredSelection) event
								.getSelection()).getFirstElement();
					}
				});
		reactionTable.setComparator(new ViewerComparator() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				SBAReaction r1 = (SBAReaction) e1, r2 = (SBAReaction) e2;
				return r1.getName().compareTo(r2.getName());
			}
		});
		new TableColumn(reactionTable.getTable(), SWT.LEFT);
		new TableColumn(reactionTable.getTable(), SWT.LEFT);
		reactionTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				SBAReaction r = (SBAReaction) ((StructuredSelection) event
						.getSelection()).getFirstElement();
				stoicDialog.setReaction(r);
				if (stoicDialog.open() == IDialogConstants.OK_ID) {
					reactionTable.update(r, null);
				}
			}
		});
		reactionTable.getTable().addPaintListener(new PaintListener() {
			Rectangle prior;
			public void paintControl(PaintEvent e) {
				if(prior == null || !prior.equals(reactionTable.getTable().getBounds())) {
					prior = reactionTable.getTable().getBounds();
					pack();
				}
			}			
		});
		reactionFilter = new ReactionFilter();
		reactionTable.addFilter(reactionFilter);
		Label label = new Label(composite, SWT.NONE);
		label.setText("filter:");
		final Text filterInput = new Text(composite, SWT.NONE);
		filterInput.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				reactionFilter.updateFilter(filterInput.getText());
				reactionTable.refresh();
			}
		});
		Image image = ImageManager.getInstance().getImage(ImageManager.CLEAR);
		ImageData imageData = image.getImageData();
		imageData = imageData.scaledTo(10, 10);
		image = new Image(image.getDevice(), imageData);
		ControlDecoration controlDecoration = new ControlDecoration(
				filterInput, SWT.RIGHT | SWT.TOP, composite);
		controlDecoration.setImage(image);
		controlDecoration.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				filterInput.setText("");
				reactionFilter.updateFilter("");
				reactionTable.refresh();
			}
		});
		controlDecoration.setDescriptionText("Clear the filter");
		Button restoreButton = new Button(composite, SWT.NONE);
		restoreButton.setText("Restore");
		restoreButton
				.setToolTipText("Restore from previously stored stoichiometric information");
		restoreButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				reset();
			}
		});
		// Layout
		FormData formData = new FormData();
		formData.top = new FormAttachment(0);
		formData.right = new FormAttachment(100, -10);
		filterInput.setLayoutData(formData);
		formData = new FormData();
		formData.right = new FormAttachment(filterInput);
		label.setLayoutData(formData);
		formData = new FormData();
		formData.top = new FormAttachment(filterInput, 5);
		formData.bottom = new FormAttachment(restoreButton);
		formData.left = new FormAttachment(0);
		formData.right = new FormAttachment(100);
		reactionTable.getTable().setLayoutData(formData);
		formData = new FormData();
		formData.bottom = new FormAttachment(100);
		formData.left = new FormAttachment(0);
		restoreButton.setLayoutData(formData);
		setPageComplete(true);
		setControl(composite);
	}

	private void loadStoichiometry() {
		stoicInfo.clear();
		try {
			String value = PepatoOptionForwarder
					.getOptionFromPersistentResource(model
							.getUnderlyingResource(), "stoichiometry");
			if (value == null)
				return;
			StringBuilder sb = new StringBuilder(value);
			ArrayList<String> al = new ArrayList<String>();
			int index, length;
			try {
				while (sb.length() > 0) {
					index = sb.indexOf(":");
					length = Integer.parseInt(sb.substring(0, index));
					sb.delete(0, index + 1);
					al.add(sb.substring(0, length));
					sb.delete(0, length);
				}
			} catch (NumberFormatException e) {
				return;
			}
			if (al.size() % 3 != 0)
				return;
			String[] sa = al.toArray(new String[] {});
			Map<String, Integer> component;
			for (int i = 0; i < sa.length; i += 3) {
				component = stoicInfo.get(sa[i]);
				if (component == null) {
					component = new HashMap<String, Integer>();
					stoicInfo.put(sa[i], component);
				}
				component.put(sa[i + 1], new Integer(sa[i + 2]));
			}
		} catch (Exception e) {
			PepaLog.logError(e);
		}
	}
	
	private final void pack() {
		TableColumn[] tca = reactionTable.getTable().getColumns();
		tca[0].pack();
		tca[1].setWidth(Math.max(reactionTable.getTable().getClientArea().width - tca[0].getWidth(), tca[1].getWidth()));
	}

	@SuppressWarnings("unchecked")
	private void reset() {
		Map<String, Integer> components, emptySet = new HashMap<String, Integer>();
		Integer value;
		for (SBAReaction reaction : (Set<SBAReaction>) reactionTable.getInput()) {
			components = stoicInfo.get(reaction.getName());
			if (components == null)
				components = emptySet;
			for (SBAComponent component : reaction.getReactants()) {
				value = components.get(component.getName());
				if (value != null)
					component.setStoichiometry(value);
				else
					component.setStoichiometry(1);
			}
			for (SBAComponent component : reaction.getProducts()) {
				value = components.get(component.getName());
				if (value != null)
					component.setStoichiometry(value);
				else
					component.setStoichiometry(1);

			}
		}
		reactionTable.refresh();
	}

	@SuppressWarnings("unchecked")
	void saveStoichiometry() {
		StringBuilder value = new StringBuilder();
		if (reactionTable == null || reactionTable.getInput() == null) {
			return;
		}
		String sReaction, sComponent, s;
		int iReaction;
		for (SBAReaction reaction : (Set<SBAReaction>) reactionTable.getInput()) {
			sReaction = reaction.getName();
			iReaction = sReaction.length();
			for (SBAComponent component : reaction.getReactants())
				if (component.getStoichiometry() > 1) {
					value.append(iReaction).append(":").append(sReaction);
					sComponent = component.getName();
					value.append(sComponent.length()).append(":").append(
							sComponent);
					s = Integer.toString(component.getStoichiometry());
					value.append(s.length()).append(":").append(s);
				}
			for (SBAComponent component : reaction.getProducts())
				if (component.getStoichiometry() > 1) {
					value.append(iReaction).append(":").append(sReaction);
					sComponent = component.getName();
					value.append(sComponent.length()).append(":").append(
							sComponent);
					s = Integer.toString(component.getStoichiometry());
					value.append(s.length()).append(":").append(s);
				}
		}
		try {
			PepatoOptionForwarder
					.saveOptionInPersistentResource(model
							.getUnderlyingResource(), "stoichiometry", value
							.toString());
		} catch (CoreException e) {
			PepaLog.logError(e);
		}
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			boolean updated = false;
			try {
				updated = model.sbaParse();
			} catch (SBAParseException e) {
				PepaLog.logError(e);
				getWizard().dispose();
			}
			if (updated || first) {
				reactionTable.setInput(model.getReactions());
				if (first) {
					reset();
					first = false;
				}
				pack();
			}
		}
	}

	@SuppressWarnings("unchecked")
	void updateReactions() {
		model.updateReactions((Set<SBAReaction>) reactionTable.getInput());
	}
}