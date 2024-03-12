/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.zest.core.widgets.GraphNode;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayComponent;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayPropertyMap;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayState;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;

/**
 * Action for exporting the Abstraction View configuration
 * 
 * @author msmith
 * 
 */
public class ImportConfigAction extends Action {

	private AbstractionView view;
	
	public ImportConfigAction(AbstractionView view) {
		super("Import Config", Action.AS_PUSH_BUTTON);
		this.setImageDescriptor(ImageManager.getInstance().getImageDescriptor(ImageManager.IMPORT));
		this.view = view;
		this.setToolTipText("Import abstraction view configuration");
	}

	public void run() {
		String openFile = getFileName();
		run(openFile, false);
	}
	
	public void run(String fileName, boolean ignoreErrors) {
		if (fileName == null) return;
		Document document = null;
		if (ignoreErrors) {
			document = parseXMLIgnoreErrors(fileName);
		} else {
			document = parseXML(fileName);
		}
		if (document == null) return;
		
		try {
			readXML(document);
		} catch (Exception e) {
			MessageDialog.openError(view.getSite().getShell(), "Error",
					"Error when reading configuration file: " + fileName +
					". Unable to load all configuration information. " +
					"Check that it is a valid PEPA configuration file.");
		}
		
		// We finally need to update the property table
		view.switchPropertyTable();
	}
	
	private String getFileName() {
		// Open the save dialog
		FileDialog saveDialog = new FileDialog(view.getSite().getShell(), SWT.OPEN);
		saveDialog.setText("Load Abstraction View Configuration");
		saveDialog.setFilterPath(view.getPEPAPath());
		String[] filterExt = { "*.config", "*.xml" };
		saveDialog.setFilterExtensions(filterExt);
		return saveDialog.open();
	}

	private Document parseXML(String fileName) {
		try {
			SAXBuilder parser = new SAXBuilder();
			return parser.build(fileName);
		} catch (JDOMException e1) {
			MessageDialog.openError(view.getSite().getShell(), "Error",
					"Unable to parse configuration file: " + fileName + ". Check that it is a valid XML file.");
		} catch (IOException e1) {
			MessageDialog.openError(view.getSite().getShell(), "Error",
					"Unable to read configuration file: " + fileName + ". An I/O error occurred.");
		}
		return null;
	}

	private Document parseXMLIgnoreErrors(String fileName) {
		try {
			SAXBuilder parser = new SAXBuilder();
			return parser.build(fileName);
		} catch (Exception e) { }
		return null;
	}
	
	private void readXML(Document document) {
		Element root = document.getRootElement();
		readPropertyXML(root.getChild("Properties"));
		readGraphXML(root.getChild("Display"));
	}
	
	private void readPropertyXML(Element root) {
		if (root == null) return;
		
		// Clear existing properties
		KroneckerDisplayPropertyMap propertyMap = view.getDisplayComponent(0).getPropertyMap();
		propertyMap.removeAllProperties();
		
		// Add new properties
		@SuppressWarnings("unchecked")
		List<Element> content = root.getChildren();
		@SuppressWarnings("unchecked")
		List<Element> names = content.get(0).getChildren();
		for (Element name : names) {
			propertyMap.addProperty(name.getText());
			// Note: we're assuming that all names are unique
		}
		
		// Set the values of the properties
		ArrayList<String> unknownComponents = new ArrayList<String>();
		// Note that the first node in the XML file lists the names
		for (int i = 0; i < names.size(); i++) {
			String propertyName = names.get(i).getText();
			Element propertyValues = content.get(i + 1);
			@SuppressWarnings("unchecked")
			List<Element> propertyList = propertyValues.getChildren();
			for (Element propertyNode : propertyList) {
				boolean propertyValue = Boolean.valueOf(propertyNode.getText());
				String componentName = propertyNode.getAttributeValue("component");
				int componentNumber = Integer.valueOf(propertyNode.getAttributeValue("componentNumber"));
				String stateName = propertyNode.getAttributeValue("name");
				short stateNumber = Short.valueOf(propertyNode.getAttributeValue("number"));
				int componentID = getComponentID(componentName, componentNumber);
				if (componentID >= 0) {
					KroneckerDisplayState state = view.getDisplayComponent(componentID).getModel().getState(stateName, stateNumber);
					if (state != null) {
						KroneckerDisplayPropertyMap map = view.getDisplayComponent(componentID).getPropertyMap();
						map.setProperty(propertyName, state, propertyValue);
					} else {
						unknownComponents.add(stateName);
					}
				} else {
					unknownComponents.add(stateName);
				}
			}
		}

		// Give a warning if we couldn't set the property for some components
		if (unknownComponents.size() > 0) {
			String components = "";
			for (String name : unknownComponents) {
				components = components + name + ", ";
			}
			components = components.substring(0, components.length() - 2);
			MessageDialog.openError(view.getSite().getShell(), "Warning",
					"Could not resolve the following states: " + components +
					". They have been set to satisfy all properties by default.");
		}
	}
	
	private int getComponentID(String name, int number) {
		int numComponents = view.numComponents();
		int instancesToFind = number;
		for (int i = 0; i < numComponents; i++) {
			KroneckerDisplayComponent component = view.getDisplayComponent(i);
			if (component.getName().equals(name)) {
				if (instancesToFind == 0) {
					return i;
				} else {
					instancesToFind--;
				}
			}
		}
		return -1;
	}
	
	private void readGraphXML(Element root) {
		if (root == null) return;
		Element graphs = root.getChild("Graphs");
		
		// Firstly make sure all components will have layout applied on select, in case
		// we don't have any information for them.
		for (int i = 0; i < view.numComponents(); i++) {
			view.setApplyLayoutOnSelect(i, true);
		}
		
		// Gather the graph information from the XML file
		for (Object o1 : graphs.getChildren()) {
			Element component = (Element)o1;
			String name = component.getAttributeValue("name");
			int number = Integer.parseInt(component.getAttributeValue("number"));
			boolean applyLayout = Boolean.parseBoolean(component.getAttributeValue("applyLayout"));
			int componentID = getComponentID(name, number);
			if (componentID >= 0) {
				view.setApplyLayoutOnSelect(componentID, applyLayout);				
				for (Object o2 : component.getChildren()) {
					Element point = (Element)o2;
					String stateName = point.getAttributeValue("name");
					int stateNumber = Integer.parseInt(point.getAttributeValue("number"));
					Point location = elementToPoint(point);
					KroneckerDisplayState state = view.getDisplayComponent(componentID).getModel().getState(stateName, stateNumber);
					if (state != null) {
						GraphNode node = view.getBuilder(componentID).getNode(state, true);
						node.setLocation(location.preciseX(), location.preciseY());
					}
				}
			}
		}
			
		// Set the font size
		int fontSize = Integer.valueOf(root.getAttributeValue("fontSize"));
		for (int i = 0; i < view.numComponents(); i++) {
			view.getBuilder(i).setFontSize(fontSize);
		}
		
		// Set the current tab
		String tabName = root.getAttributeValue("currentTabName");
		if (tabName != null) {
			int tabNumber = Integer.parseInt(root.getAttributeValue("currentTabNumber"));
			view.setCurrentTab(getComponentID(tabName, tabNumber));
		}
	}
	
	private Point elementToPoint(Element element) {
		Element xLocation = element.getChild("x");
		double x = Double.parseDouble(xLocation.getText());
		Element yLocation = element.getChild("y");
		double y = Double.parseDouble(yLocation.getText());
		return new Point(x, y);
	}
	
}
