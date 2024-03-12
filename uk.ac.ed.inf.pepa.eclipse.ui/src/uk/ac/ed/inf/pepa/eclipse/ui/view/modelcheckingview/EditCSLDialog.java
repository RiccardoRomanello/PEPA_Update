/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.modelcheckingview;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractPathProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractProbability;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAndNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAtomicNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLBooleanNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLDouble;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLEventuallyNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLGloballyNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLImpliesNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLNextNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLNotNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLOrNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLPathPlaceHolder;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLPathPropertyNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLProbabilityComparator;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLProbabilityTest;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLStatePlaceHolder;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLSteadyStateNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLTimeInterval;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLUntilNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.StringPosition;

public class EditCSLDialog extends Dialog {

	private static final int NEXT_PATH = 0;
	private static final int UNTIL_PATH = 1;
	private static final int EVENT_PATH = 2;
	private static final int GLOBAL_PATH = 3;
	
	private static final Font PROPERTY_FONT = new Font(null, "Ariel", 14, SWT.BOLD);
	private static final Color SELECTED_BG_COLOR = new Color(null, 255, 240, 153);
	private static final Color SELECTED_FG_COLOR = new Color(null, 0, 0, 0);

	private KroneckerDisplayModel model; 
	private String name;
	private CSLAbstractStateProperty newProperty;
	
	private Text nameText;
	private StyledText propertyText;
	
	private Button steadyStateButton;
	private Button pathPropertyButton;
	private Button nextButton;
	private Button untilButton;
	private Button eventuallyButton;
	private Button globallyButton;
	private Button andButton;
	private Button orButton;
	private Button notButton;
	private Button impliesButton;
	private Button atomicButton;
	private Button trueButton;
	private Button falseButton;
	
	private Menu atomicMenu;
	private Menu steadyStateMenu;
	private Menu pathPropertyMenu;
	private Menu nextMenu;
	private Menu untilMenu;
	private Menu eventuallyMenu;
	private Menu globallyMenu;
	
	private StringPosition currentSelection = null;
	
	private boolean pathEnabled = false;
	private boolean stateEnabled = false;
	private boolean logicEnabled = false;
	private boolean pOperatorEnabled = false;
	private boolean sOperatorEnabled = false;
	
	public EditCSLDialog(Shell parentShell, KroneckerDisplayModel model, String name, CSLAbstractStateProperty property) {
		super(parentShell);
		this.model = model;
		this.name = name;
		this.newProperty = property.copy();
	}
	
	private void selectPathButtons() {
		pathEnabled = true;
		stateEnabled = false;
		steadyStateButton.setEnabled(false);
		pathPropertyButton.setEnabled(false);
		nextButton.setEnabled(true);
		untilButton.setEnabled(true);
		globallyButton.setEnabled(true);
		eventuallyButton.setEnabled(true);
		andButton.setEnabled(false);
		orButton.setEnabled(false);
		notButton.setEnabled(false);
		impliesButton.setEnabled(false);
		atomicButton.setEnabled(false);
		trueButton.setEnabled(false);
		falseButton.setEnabled(false);
	}
	
	private void selectStateButtons() {
		pathEnabled = false;
		stateEnabled = true;
		logicEnabled = true;
		pOperatorEnabled = true;
		sOperatorEnabled = true;
		if (currentSelection != null && currentSelection.getObject().isProbabilityTest()) {
			logicEnabled = false;
			if (currentSelection.getObject() instanceof CSLPathPropertyNode) {
				sOperatorEnabled = false;
			} else {
				pOperatorEnabled = false;
			}
		}
		steadyStateButton.setEnabled(sOperatorEnabled);
		pathPropertyButton.setEnabled(pOperatorEnabled);
		nextButton.setEnabled(false);
		untilButton.setEnabled(false);
		globallyButton.setEnabled(false);
		eventuallyButton.setEnabled(false);
		andButton.setEnabled(logicEnabled);
		orButton.setEnabled(logicEnabled);
		notButton.setEnabled(logicEnabled);
		impliesButton.setEnabled(logicEnabled);
		atomicButton.setEnabled(true);
		trueButton.setEnabled(true);
		falseButton.setEnabled(true);
	}
	
	private void deselectButtons() {
		pathEnabled = false;
		stateEnabled = false;
		steadyStateButton.setEnabled(false);
		pathPropertyButton.setEnabled(false);
		nextButton.setEnabled(false);
		untilButton.setEnabled(false);
		andButton.setEnabled(false);
		orButton.setEnabled(false);
		notButton.setEnabled(false);
		impliesButton.setEnabled(false);
		atomicButton.setEnabled(false);
		trueButton.setEnabled(false);
		falseButton.setEnabled(false);
	}
	
	private Menu createAtomicMenu() {
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		String[] atomicProperties = model.getAtomicProperties();
		for (int i = 0; i < atomicProperties.length; i++) {
			final String propertyName = atomicProperties[i];
			MenuItem propertyItem = new MenuItem(menu, SWT.PUSH);
			propertyItem.setText(propertyName);
			propertyItem.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) { }
				public void widgetSelected(SelectionEvent e) {
					CSLAtomicNode property = new CSLAtomicNode(propertyName);
					newProperty = newProperty.replace(currentSelection.getObject(), property);
					updateSelection(property);
				}
			});
		}
		return menu;
	}
	
	private Menu createStateMenu(final boolean isSteadyState) {
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		MenuItem testItem = new MenuItem(menu, SWT.PUSH);
		testItem.setText("[=?] Test probability");
		testItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				addStateProperty(isSteadyState, new CSLProbabilityTest());
			}
		});
		MenuItem compareGTItem = new MenuItem(menu, SWT.PUSH);
		compareGTItem.setText("[<=P] Probability less than or equal to P");
		compareGTItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				addStateProperty(isSteadyState, new CSLProbabilityComparator(true, new CSLDouble(true, 1)));
			}
		});
		MenuItem compareLTItem = new MenuItem(menu, SWT.PUSH);
		compareLTItem.setText("[>=P] Probability greater than or equal to P");
		compareLTItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				addStateProperty(isSteadyState, new CSLProbabilityComparator(false, new CSLDouble(true, 0)));
			}
		});
		return menu;
	}
	
	private void addStateProperty(boolean isSteadyState, CSLAbstractProbability comparator) {
		CSLAbstractStateProperty property;
		if (isSteadyState) {
			if (currentSelection.getObject() instanceof CSLSteadyStateNode) {
				// Just change comparator
				property = new CSLSteadyStateNode(getStateChild(1, currentSelection.getObject()), comparator);
			} else {
				// Apply to current selection
				property = new CSLSteadyStateNode(getState(currentSelection.getObject()), comparator);
			}
		} else { // Path probability
			property = new CSLPathPropertyNode(getPathChild(currentSelection.getObject()), comparator);
		}
		newProperty = newProperty.replace(currentSelection.getObject(), property);
		updateSelection(property);
	}
	
	private Menu createPathMenu(final int type) {
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		MenuItem unboundedItem = new MenuItem(menu, SWT.PUSH);
		unboundedItem.setText("Unbounded: " + getSymbol(type, ""));
		unboundedItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				addPathProperty(type, new CSLTimeInterval());
			}
		});
		MenuItem lowerBoundItem = new MenuItem(menu, SWT.PUSH);
		lowerBoundItem.setText("Lower Bound: " + getSymbol(type, ">=t"));
		lowerBoundItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				addPathProperty(type, new CSLTimeInterval(new CSLDouble(false, 0), true));
			}
		});
		MenuItem upperBoundItem = new MenuItem(menu, SWT.PUSH);
		upperBoundItem.setText("Upper Bound: " + getSymbol(type, "<=t"));
		upperBoundItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				addPathProperty(type, new CSLTimeInterval(new CSLDouble(false, 0), false));
			}
		});
		MenuItem boundedItem = new MenuItem(menu, SWT.PUSH);
		boundedItem.setText("Bounded: " + getSymbol(type, "[t1,t2]"));
		boundedItem.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				addPathProperty(type, new CSLTimeInterval(new CSLDouble(false, 0), new CSLDouble(false, 0)));
			}
		});
		if (type == NEXT_PATH) {
			// Don't allow time bounded next operators, even on a non-abstracted model (for now)
			lowerBoundItem.setEnabled(false);
			upperBoundItem.setEnabled(false);
			boundedItem.setEnabled(false);
		}
		return menu;
	}
	
	private String getSymbol(int type, String bound) {
		String symbol = "";
		switch (type) {
		case NEXT_PATH:
			symbol = "X" + bound + " p";
			break;
		case UNTIL_PATH:
			symbol = "p1 U" + bound + " p2";
			break;
		case EVENT_PATH:
			symbol = "F" + bound + " p";
			break;
		case GLOBAL_PATH:
			symbol = "G" + bound + " p";
			break;
		}
		return symbol;
	}
	
	private void addPathProperty(int type, CSLTimeInterval interval) {
		CSLAbstractPathProperty property = null;
		switch (type) {
		case NEXT_PATH:
			property = new CSLNextNode(getStateChild(1, currentSelection.getObject()), interval);
			break;
		case UNTIL_PATH:
			property = new CSLUntilNode(getStateChild(1, currentSelection.getObject()), 
					                    getStateChild(2, currentSelection.getObject()), interval);
			break;
		case EVENT_PATH:
			property = new CSLEventuallyNode(getStateChild(1, currentSelection.getObject()), interval);
			break;
		case GLOBAL_PATH:
			property = new CSLGloballyNode(getStateChild(1, currentSelection.getObject()), interval);
			break;
		}
		newProperty = newProperty.replace(currentSelection.getObject(), property);
		updateSelection(property);
	}
	
	private void createMenus() {
		steadyStateMenu = createStateMenu(true);
		pathPropertyMenu = createStateMenu(false);
		nextMenu = createPathMenu(NEXT_PATH);
		untilMenu = createPathMenu(UNTIL_PATH);
		eventuallyMenu = createPathMenu(EVENT_PATH);
		globallyMenu = createPathMenu(GLOBAL_PATH);
		atomicMenu = createAtomicMenu();
	}
	
	private CSLAbstractStateProperty getState(CSLAbstractProperty property) {
		if (property instanceof CSLAbstractStateProperty) {
			return (CSLAbstractStateProperty)property.copy();
		} else {
			return new CSLStatePlaceHolder();
		}
	}
	
	// A bit of a hack, to work out when we can substitute existing terms
	// to avoid losing them when we replace an operator
	private CSLAbstractStateProperty getStateChild(int number, CSLAbstractProperty property) {
		StringPosition[] children = property.getChildren();
		int count = 0;
		for (int i = 0; i < children.length; i++) {
			CSLAbstractProperty child = children[i].getObject();
			if (child instanceof CSLAbstractStateProperty) {
				count++;
				if (count == number) {
					return (CSLAbstractStateProperty)child;
				}
			}
		}
		return new CSLStatePlaceHolder();
	}
	
	private CSLAbstractPathProperty getPathChild(CSLAbstractProperty property) {
		StringPosition[] children = property.getChildren();
		for (int i = 0; i < children.length; i++) {
			CSLAbstractProperty child = children[i].getObject();
			if (child instanceof CSLAbstractPathProperty) {
				return (CSLAbstractPathProperty)child;
			}
		}
		return new CSLPathPlaceHolder();
	}
		
	private void updateSelection() {
		int index = propertyText.getCaretOffset();
		StringPosition position = newProperty.objectAt(index);
		if (position == null) return;
		propertyText.setSelection(position.getStart(), position.getEnd());
		currentSelection = position;
		if (currentSelection.getObject() instanceof CSLAbstractStateProperty) {
			selectStateButtons();
		} else if (currentSelection.getObject() instanceof CSLAbstractPathProperty) {
			selectPathButtons();
		} else {
			deselectButtons();
		}
		if (propertyText.getEditable()) {
			String text = newProperty.toString();
			if (!propertyText.getText().equals(text)) {
				propertyText.setText(text);
				clearSelection();
			}
		}
		setEditable();
		//System.out.println(index + " => (" + position.getStart() + "," + position.getEnd());
	}
	
	private void setEditable() {
		boolean canEdit = currentSelection != null &&
		                  currentSelection.getObject() instanceof CSLDouble;
		propertyText.setEditable(canEdit);
	}
	
	private void deleteSelection() {
		CSLAbstractProperty replacement = null;
		if (currentSelection != null) {
			if (currentSelection.getObject() instanceof CSLAbstractStateProperty) {
				replacement = getStateChild(1, currentSelection.getObject());
			} else if (currentSelection.getObject() instanceof CSLAbstractPathProperty) {
				replacement = new CSLPathPlaceHolder();
			}
		}
		if (replacement != null) {
			newProperty = newProperty.replace(currentSelection.getObject(), replacement);
			updateSelection(replacement);		
		}
	}
	
	private void clearSelection() {
		propertyText.selectAll();
		propertyText.setSelection(0);
		currentSelection = null;
		deselectButtons();
		setEditable();
	}
	
	private void updateSelection(CSLAbstractProperty newSelection) {
		propertyText.setText(newProperty.toString());
		if (currentSelection != null) {
			StringPosition position = newProperty.indexOf(newSelection);
			propertyText.setSelection(position.getStart(), position.getEnd());
			currentSelection = position;
			if (currentSelection.getObject() instanceof CSLAbstractStateProperty) {
				// Update in case we changed from a property to a test (=?)
				selectStateButtons();
			}
		} else {
			clearSelection();
		}
		setEditable();
	}
	
	private void updateHighlighting() {
		if (currentSelection != null) {
			propertyText.setSelection(currentSelection.getStart(), currentSelection.getEnd());
		} else {
			clearSelection();
		}
	}
	
	private boolean canBeQuestion() {
		return currentSelection.getObject() == newProperty;
	}
	
	private Composite createStatePropertyButtons(Composite parent) {
		final Group buttonFrame = new Group(parent, SWT.NULL);
		buttonFrame.setText("State");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		buttonFrame.setLayout(layout);
		GridData layoutData;
		
		steadyStateButton = new Button(buttonFrame, SWT.PUSH);
		steadyStateButton.setText("Steady State");
		steadyStateButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = steadyStateButton.getBounds();
				Point position = new Point (rect.x + rect.width, rect.y);
				position = buttonFrame.toDisplay(position);
				steadyStateMenu.setLocation(position);
				steadyStateMenu.getItem(0).setEnabled(canBeQuestion());
				steadyStateMenu.setVisible(true);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		steadyStateButton.setLayoutData(layoutData);
		
		pathPropertyButton = new Button(buttonFrame, SWT.PUSH);
		pathPropertyButton.setText("Path Property");
		pathPropertyButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = pathPropertyButton.getBounds();
				Point position = new Point (rect.x + rect.width, rect.y);
				position = buttonFrame.toDisplay(position);
				pathPropertyMenu.setLocation(position);
				pathPropertyMenu.getItem(0).setEnabled(canBeQuestion());
				pathPropertyMenu.setVisible(true);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		pathPropertyButton.setLayoutData(layoutData);
		
		return buttonFrame;
	}
	
	private Composite createPathPropertyButtons(Composite parent) {
		final Group buttonFrame = new Group(parent, SWT.NULL);
		buttonFrame.setText("Path");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		buttonFrame.setLayout(layout);
		GridData layoutData;
		
		nextButton = new Button(buttonFrame, SWT.PUSH);
		nextButton.setText("X (next)");
		nextButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = nextButton.getBounds();
				Point position = new Point (rect.x + rect.width, rect.y);
				position = buttonFrame.toDisplay(position);
				nextMenu.setLocation(position);
				nextMenu.setVisible(true);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		nextButton.setLayoutData(layoutData);
		
		untilButton = new Button(buttonFrame, SWT.PUSH);
		untilButton.setText("U (until)");
		untilButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = untilButton.getBounds();
				Point position = new Point (rect.x + rect.width, rect.y);
				position = buttonFrame.toDisplay(position);
				untilMenu.setLocation(position);
				untilMenu.setVisible(true);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		untilButton.setLayoutData(layoutData);
		
		eventuallyButton = new Button(buttonFrame, SWT.PUSH);
		eventuallyButton.setText("F (eventually)");
		eventuallyButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = eventuallyButton.getBounds();
				Point position = new Point (rect.x + rect.width, rect.y);
				position = buttonFrame.toDisplay(position);
				eventuallyMenu.setLocation(position);
				eventuallyMenu.setVisible(true);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		eventuallyButton.setLayoutData(layoutData);
		
		globallyButton = new Button(buttonFrame, SWT.PUSH);
		globallyButton.setText("G (globally)");
		globallyButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = globallyButton.getBounds();
				Point position = new Point (rect.x + rect.width, rect.y);
				position = buttonFrame.toDisplay(position);
				globallyMenu.setLocation(position);
				globallyMenu.setVisible(true);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		globallyButton.setLayoutData(layoutData);
		
		return buttonFrame;
	}
	
	private void makeAnd() {
		CSLAndNode property;
		if (currentSelection.getObject() instanceof CSLAndNode ||
		    currentSelection.getObject() instanceof CSLOrNode  ||
		    currentSelection.getObject() instanceof CSLImpliesNode) {
			// Replace current operator
			property = new CSLAndNode(getStateChild(1, currentSelection.getObject()),
                                      getStateChild(2, currentSelection.getObject()));
		} else {
			// Apply operator to top-level selection
			property = new CSLAndNode(getState(currentSelection.getObject()),
                                      getState(currentSelection.getObject()));
		}
		newProperty = newProperty.replace(currentSelection.getObject(), property);
		updateSelection(property);
	}
	
	private void makeOr() {
		CSLOrNode property;
		if (currentSelection.getObject() instanceof CSLAndNode ||
		    currentSelection.getObject() instanceof CSLOrNode  ||
		    currentSelection.getObject() instanceof CSLImpliesNode) {
			// Replace current operator
			property = new CSLOrNode(getStateChild(1, currentSelection.getObject()),
                                     getStateChild(2, currentSelection.getObject()));
		} else {
			// Apply operator to top-level selection
			property = new CSLOrNode(getState(currentSelection.getObject()),
                                     getState(currentSelection.getObject()));
		}
		newProperty = newProperty.replace(currentSelection.getObject(), property);
		updateSelection(property);
	}
	
	private void makeNot() {
		CSLAbstractStateProperty property;
		if (currentSelection.getObject() instanceof CSLNotNode) {
			// double negation = remove not
			property = getStateChild(1, currentSelection.getObject());
		} else if (currentSelection.getObject() instanceof CSLBooleanNode) {
			// negating a Boolean node - just switch if
			CSLBooleanNode node = (CSLBooleanNode)currentSelection.getObject();
			property = new CSLBooleanNode(!node.getValue());
		} else {
			// negate the selection
			property = new CSLNotNode(getState(currentSelection.getObject()));
		}
		newProperty = newProperty.replace(currentSelection.getObject(), property);
		updateSelection(property);
	}
	
	private void makeImplies() {
		CSLImpliesNode property;
		if (currentSelection.getObject() instanceof CSLAndNode ||
		    currentSelection.getObject() instanceof CSLOrNode  ||
		    currentSelection.getObject() instanceof CSLImpliesNode) {
			// Replace current operator
			property = new CSLImpliesNode(getStateChild(1, currentSelection.getObject()),
                                          getStateChild(2, currentSelection.getObject()));
		} else {
			// Apply operator to top-level selection
			property = new CSLImpliesNode(getState(currentSelection.getObject()),
                                          getState(currentSelection.getObject()));
		}
		newProperty = newProperty.replace(currentSelection.getObject(), property);
		updateSelection(property);
	}
	
	private Composite createLogicPropertyButtons(Composite parent) {
		Group buttonFrame = new Group(parent, SWT.NULL);
		buttonFrame.setText("Logic");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		buttonFrame.setLayout(layout);
		GridData layoutData;
		
		andButton = new Button(buttonFrame, SWT.PUSH);
		andButton.setText("&& (and)");
		//andButton.setText("\u2227");
		andButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				makeAnd();
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		andButton.setLayoutData(layoutData);
		
		orButton = new Button(buttonFrame, SWT.PUSH);
		orButton.setText("| (or)");
		//orButton.setText("\u2228");
		orButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				makeOr();
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		orButton.setLayoutData(layoutData);
		
		notButton = new Button(buttonFrame, SWT.PUSH);
		notButton.setText("! (not)");
		notButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				makeNot();
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		notButton.setLayoutData(layoutData);
		
		impliesButton = new Button(buttonFrame, SWT.PUSH);
		impliesButton.setText("=> (implies)");
		//impliesButton.setText("\u21D2");
		impliesButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				makeImplies();
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		impliesButton.setLayoutData(layoutData);
		
		return buttonFrame;
	}
	
	private void makeBoolean(boolean isTrue) {
		CSLBooleanNode property = new CSLBooleanNode(isTrue);
		newProperty = newProperty.replace(currentSelection.getObject(), property);
		updateSelection(property);
	}
	
	private Composite createAtomicPropertyButtons(Composite parent) {
		final Group buttonFrame = new Group(parent, SWT.NULL);
		buttonFrame.setText("Atomic");
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		buttonFrame.setLayout(layout);
		GridData layoutData;
		
		atomicButton = new Button(buttonFrame, SWT.PUSH);
		atomicButton.setText("Atomic Property");
		atomicButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				Rectangle rect = atomicButton.getBounds();
				Point position = new Point (rect.x + rect.width, rect.y);
				position = buttonFrame.toDisplay(position);
				atomicMenu.setLocation(position);
				atomicMenu.setVisible(true);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.horizontalSpan = 2;
		layoutData.grabExcessHorizontalSpace = true;
		atomicButton.setLayoutData(layoutData);
		
		trueButton = new Button(buttonFrame, SWT.PUSH);
		trueButton.setText("True");
		trueButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				makeBoolean(true);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		trueButton.setLayoutData(layoutData);
		
		falseButton = new Button(buttonFrame, SWT.PUSH);
		falseButton.setText("False");
		falseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { return; }
			public void widgetSelected(SelectionEvent e) {
				makeBoolean(false);
			}
		});
		layoutData = new GridData();
		layoutData.horizontalAlignment = GridData.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		falseButton.setLayoutData(layoutData);
		
		return buttonFrame;
	}
	
	protected Control createDialogArea(Composite parent) {
		FormData formData;
		
		Composite viewFrame = new Composite(parent, SWT.NONE);
		viewFrame.setLayout(new FormLayout());
		
		Label nameLabel = new Label(viewFrame, SWT.NONE);
		nameLabel.setText("Name:");
		formData = new FormData();
		formData.top = new FormAttachment(0, 10);
		formData.left = new FormAttachment(0, 5);
		nameLabel.setLayoutData(formData);
		
		nameText = new Text(viewFrame, SWT.BORDER);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				setOKEnabled();		
			}
		});
		nameText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				updateSelection();
				clearSelection();
			}
			public void focusLost(FocusEvent e) { }
		});
		formData = new FormData();
		formData.top = new FormAttachment(0, 5);
		formData.left = new FormAttachment(nameLabel, 5);
		formData.right = new FormAttachment(100,-5);
		nameText.setLayoutData(formData);
		
		propertyText = new StyledText(viewFrame, SWT.BORDER | SWT.MULTI | SWT.WRAP);
		propertyText.setFont(PROPERTY_FONT);
		propertyText.setAlignment(SWT.CENTER);
		propertyText.setEditable(false);
		propertyText.setDoubleClickEnabled(false);
		propertyText.setDragDetect(true);
		propertyText.setSelectionBackground(SELECTED_BG_COLOR);
		propertyText.setSelectionForeground(SELECTED_FG_COLOR);
		propertyText.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				updateSelection();
			}
		});
		propertyText.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) { }
			public void keyPressed(KeyEvent e) {
				if (propertyText.getEditable()) {
					if (propertyText.getCaretOffset() < currentSelection.getStart()) {
						propertyText.setSelection(currentSelection.getStart());
					} else if (propertyText.getCaretOffset() > currentSelection.getEnd()) {
						propertyText.setSelection(currentSelection.getEnd());
					}
					if (e.character == SWT.CR) {
						updateSelection();
					} 
				} else {
					switch (e.character) {
					case SWT.DEL:
					case SWT.BS:
						deleteSelection(); break;
					case '?':
						if (stateEnabled && canBeQuestion() && currentSelection != null) {
							if (currentSelection.getObject() instanceof CSLPathPropertyNode) {
								addStateProperty(false, new CSLProbabilityTest());
							} else if (currentSelection.getObject() instanceof CSLSteadyStateNode) {
								addStateProperty(true, new CSLProbabilityTest());
							}
						}
						break;
					case '<':
						if (stateEnabled && currentSelection != null) {
							if (currentSelection.getObject() instanceof CSLPathPropertyNode) {
								addStateProperty(false, new CSLProbabilityComparator(true, new CSLDouble(true, 1)));
							} else if (currentSelection.getObject() instanceof CSLSteadyStateNode) {
								addStateProperty(true, new CSLProbabilityComparator(true, new CSLDouble(true, 1)));
							}
						}
						break;
					case '>':
						if (stateEnabled && currentSelection != null) {
							if (currentSelection.getObject() instanceof CSLPathPropertyNode) {
								addStateProperty(false, new CSLProbabilityComparator(false, new CSLDouble(true, 0)));
							} else if (currentSelection.getObject() instanceof CSLSteadyStateNode) {
								addStateProperty(true, new CSLProbabilityComparator(false, new CSLDouble(true, 0)));
							}
						}
						break;
					case '!':
						if (stateEnabled && logicEnabled) {
							makeNot();
						}
						break;
					case 'a': case 'A':
						if (stateEnabled && logicEnabled) {
							makeAnd();
						}
						break;
					case 'f': case 'F':
						if (pathEnabled) {
							CSLTimeInterval interval = new CSLTimeInterval();
							if (currentSelection.getObject() instanceof CSLEventuallyNode) {
								interval = cycleInterval(((CSLEventuallyNode)currentSelection.getObject()).getTimeInterval());
							}
							addPathProperty(EVENT_PATH, interval);
						}
						break;
					case 'g': case 'G':
						if (pathEnabled) {
							CSLTimeInterval interval = new CSLTimeInterval();
							if (currentSelection.getObject() instanceof CSLGloballyNode) {
								interval = cycleInterval(((CSLGloballyNode)currentSelection.getObject()).getTimeInterval());
							}
							addPathProperty(GLOBAL_PATH, interval);
						}
						break;
					case 'i': case 'I':
						if (stateEnabled && logicEnabled) {
							makeImplies();
						}
						break;
					case 'o': case 'O':
						if (stateEnabled && logicEnabled) {
							makeOr();
						}
						break;
					case 'p': case 'P':
						if (stateEnabled && pOperatorEnabled) {
							addStateProperty(false, new CSLProbabilityComparator(true, new CSLDouble(true, 1)));
						}
						break;
					case 's': case 'S':
						if (stateEnabled && sOperatorEnabled) {
							addStateProperty(true, new CSLProbabilityComparator(true, new CSLDouble(true, 1)));
						}
						break;
					case 't': case 'T':
						if (stateEnabled && logicEnabled) {
							makeBoolean(true);
						}
						break;
					case 'u': case 'U':
						if (pathEnabled) {
							CSLTimeInterval interval = new CSLTimeInterval();
							if (currentSelection.getObject() instanceof CSLUntilNode) {
								interval = cycleInterval(((CSLUntilNode)currentSelection.getObject()).getTimeInterval());
							}
							addPathProperty(UNTIL_PATH, interval);
						}
						break;
					case 'x': case 'X':
						if (pathEnabled) {
							CSLTimeInterval interval = new CSLTimeInterval();
							if (currentSelection.getObject() instanceof CSLNextNode) {
								interval = cycleInterval(((CSLNextNode)currentSelection.getObject()).getTimeInterval());
							}
							addPathProperty(NEXT_PATH, interval);
						}
						break;
					default:
						updateSelection();
					}
				}
			}
		});
		propertyText.addVerifyKeyListener(new VerifyKeyListener() {
			public void verifyKey(VerifyEvent e) {
				if (!propertyText.getEditable()) return;
				e.doit = false;
				int position = propertyText.getSelection().y;
				if (e.character == SWT.CR) {
					updateSelection();
				} else if ((e.keyCode >= '0' && e.keyCode <= '9') || e.keyCode == '.') {
					currentSelection.incrementEnd(1);
					e.doit = true;
				} else if (e.character == SWT.DEL && position < currentSelection.getEnd()) {
					currentSelection.incrementEnd(-1);
					e.doit = true;
				} else if (e.character == SWT.BS && position > currentSelection.getStart()) {
					currentSelection.incrementEnd(-1);
					e.doit = true;
				} 
				if (e.doit) {
					// Make sure we don't operate on the whole selection
					propertyText.setSelection(position);
				}
			}
		});
		propertyText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (propertyText.getEditable() && currentSelection.getObject() instanceof CSLDouble) {
					CSLDouble value = (CSLDouble)currentSelection.getObject();
					if (currentSelection.getStart() < currentSelection.getEnd()) {
						// length of selection is non-zero
						String newText = propertyText.getText(currentSelection.getStart(), currentSelection.getEnd() - 1);
						double newValue = value.getValue();
						try {
							newValue = Double.parseDouble(newText);
						} catch (NumberFormatException ex) {
							// Leave it at the old value
						}
						if (newValue != value.getValue()) {
							value.setValue(newValue);
						} 
					}
					updateHighlighting();
				}
				setOKEnabled();			
			}
		});
		GC gc = new GC(propertyText);
		formData = new FormData();
		formData.top = new FormAttachment(nameText, 5);
		formData.left = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100,-5);
		formData.height = 3 * gc.getFontMetrics().getHeight();
		gc.dispose();
		propertyText.setLayoutData(formData);
		
		Composite statePropertyButtons = createStatePropertyButtons(viewFrame);
		formData = new FormData();
		formData.left = new FormAttachment(0, 5);
		formData.top = new FormAttachment(propertyText, 5);
		statePropertyButtons.setLayoutData(formData);
		
		Composite pathPropertyButtons = createPathPropertyButtons(viewFrame);
		formData = new FormData();
		formData.left = new FormAttachment(statePropertyButtons, 5);
		formData.top = new FormAttachment(propertyText, 5);
		pathPropertyButtons.setLayoutData(formData);
		
		Composite logicPropertyButtons = createLogicPropertyButtons(viewFrame);
		formData = new FormData();
		formData.left = new FormAttachment(pathPropertyButtons, 5);
		formData.top = new FormAttachment(propertyText, 5);
		logicPropertyButtons.setLayoutData(formData);

		Composite atomicPropertyButtons = createAtomicPropertyButtons(viewFrame);
		formData = new FormData();
		formData.left = new FormAttachment(logicPropertyButtons, 5);
		formData.top = new FormAttachment(propertyText, 5);
		formData.right = new FormAttachment(100, -5);
		atomicPropertyButtons.setLayoutData(formData);
		
		createMenus();
		
		deselectButtons();
		
		return viewFrame;
	}
	
	public void create() {
		super.create();
		// A bit of a hack, to avoid propertyText from resizing horizontally.
		nameText.setText(name);
		propertyText.setText(newProperty.toString());
	}
	
	private CSLTimeInterval cycleInterval(CSLTimeInterval interval) {
		if (interval.isStartBounded()) {
			if (interval.isEndBounded()) {
				return new CSLTimeInterval();
			} else {
				return new CSLTimeInterval(new CSLDouble(false, 0), new CSLDouble(false, 0));
			}
		} else {
			if (interval.isEndBounded()) {
				return new CSLTimeInterval(new CSLDouble(false, 0), true);
			} else {
				return new CSLTimeInterval(new CSLDouble(false, 0), false);
			}
		}
	}
	
	private void setOKEnabled() {
		if (getButton(IDialogConstants.OK_ID) == null) return;
		String newName = nameText.getText();
		boolean isNameOK = newName.length() > 0;
		boolean isNameUnique = newName.equals(name) || !model.containsCSLPropertyName(newName);
		boolean isPropertyOK = !newProperty.containsPlaceHolder();
		boolean isOK = isNameOK && isNameUnique && isPropertyOK;
		getButton(IDialogConstants.OK_ID).setEnabled(isOK);
		int defaultButton = isOK ? IDialogConstants.OK_ID : IDialogConstants.CANCEL_ID;
		Shell shell = getShell();
		if (shell != null) {
			shell.setDefaultButton(getButton(defaultButton));
		}
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		setOKEnabled();
	}
	
	public CSLAbstractStateProperty getNewProperty() {
		return newProperty;
	}
	
	public String getNewName() {
		return name;
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Edit CSL Property");
	}
	
	protected void okPressed() {
		String newName = nameText.getText();
		if (newName.length() > 0) {
			name = newName;
		}
		super.okPressed();
	}
	
	
}
