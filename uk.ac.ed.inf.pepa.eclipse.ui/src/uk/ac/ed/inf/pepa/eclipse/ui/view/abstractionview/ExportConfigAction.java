/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.zest.core.widgets.GraphNode;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

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
public class ExportConfigAction extends Action {

	private AbstractionView view;

	public ExportConfigAction(AbstractionView view) {
		super("Export Config", Action.AS_PUSH_BUTTON);
		this.setImageDescriptor(ImageManager.getInstance().getImageDescriptor(ImageManager.EXPORT));
		this.view = view;
		this.setToolTipText("Export abstraction view configuration");
	}

	public void run() {
		String saveFile = getFileName();
		run(saveFile);
	}
	
	public void run(String fileName) {
		if (fileName == null) return;
		Document doc = constructXML();
		writeFile(fileName, doc);
	}
	
	private String getFileName() {
		// Open the save dialog
		FileDialog saveDialog = new FileDialog(view.getSite().getShell(), SWT.SAVE);
		saveDialog.setText("Save Abstraction View Configuration");
		saveDialog.setFilterPath(view.getPEPAPath());
		String[] filterExt = { "*.config", "*.xml" };
		saveDialog.setFilterExtensions(filterExt);
		return saveDialog.open();
	}
	
	private void writeFile(String fileName, Document document) {
		try {
			XMLOutputter serializer = new XMLOutputter();
			serializer.setFormat(Format.getPrettyFormat());
			FileWriter writer = new FileWriter(fileName);
			serializer.output(document, writer);
			writer.flush();
			writer.close();
		} catch (IOException e1) {
			MessageDialog.openError(view.getSite().getShell(), "Error",
					"Unable to write to file: " + fileName + ". An I/O error occurred.");
		}
	}

	private Document constructXML() {
		Element root = new Element("Configuration");
		Element properties = constructPropertyXML();
		Element graph = constructGraphXML();
		root.addContent(properties);
		root.addContent(graph);
		return new Document(root);	
	}
	
	private Element constructPropertyXML() {
		// Construct the XML file structure, for the properties
		Element root = new Element("Properties");
		String[] propertyNames = view.getDisplayComponent(0).getPropertyMap().getProperties();
		Element names = new Element("Names");
		for (int i = 0; i < propertyNames.length; i++) {
			Element propertyNode = new Element("Property");
			propertyNode.setText(propertyNames[i]);
			names.addContent(propertyNode);
		}
		root.addContent(names);

		// Write the values of the properties
		for (int i = 0; i < propertyNames.length; i++) {
			Element values = new Element("Values");
			values.setAttribute("property", propertyNames[i]);
			for (int n = 0; n < view.numComponents(); n++) {
				KroneckerDisplayComponent component = view.getDisplayComponent(n);
				KroneckerDisplayPropertyMap propertyMap = component.getPropertyMap();
				ArrayList<KroneckerDisplayState> selected = view.getBuilder(n).getStates();
				for (KroneckerDisplayState state : selected) {
					boolean propertyValue = propertyMap.testProperty(propertyNames[i], state, true);
					String name = state.getLabel(true);
					Element valueNode = new Element("Value");
					valueNode.setAttribute("name", name);
					valueNode.setAttribute("number", Integer.toString(component.getModel().getStateNameInstance(state)));
					valueNode.setAttribute("component", component.getName());
					valueNode.setAttribute("componentNumber", Integer.toString(getComponentInstance(component)));
					valueNode.setText(Boolean.toString(propertyValue));
					values.addContent(valueNode);
				}
			}
			root.addContent(values);
		}
		return root;	
	}
	
	private int getComponentInstance(KroneckerDisplayComponent component) {
		int numComponents = view.numComponents();
		int instance = 0;
		for (int i = 0; i < numComponents; i++) {
			KroneckerDisplayComponent c = view.getDisplayComponent(i);
			if (c == component) {
				break;
			} else if (c.getName().equals(component.getName())) {
				instance++;
			}
		}
		return instance;
	}
	
	private Element constructGraphXML() {
		Element root = new Element("Display");
		
		// Store the current tab and font size
		KroneckerDisplayComponent tabComponent = view.getDisplayComponent(view.getCurrentTab());
		if (tabComponent != null) {
			root.setAttribute("currentTabName", tabComponent.getName());
			root.setAttribute("currentTabNumber", Integer.toString(getComponentInstance(tabComponent)));
		}
		root.setAttribute("fontSize", Integer.toString(view.getBuilder(0).getFontSize()));
		
		// Store the positioning information
		Element graphs = new Element("Graphs");
		for (int i = 0; i < view.numComponents(); i++) {
			KroneckerDisplayComponent component = view.getDisplayComponent(i);
			Element graph = new Element("Graph");
			graph.setAttribute("name", component.getName());
			graph.setAttribute("number", Integer.toString(getComponentInstance(component)));
			graph.setAttribute("applyLayout", Boolean.toString(view.getApplyLayoutOnSelect(i)));
			for (KroneckerDisplayState state : view.getBuilder(i).getStates()) {
				GraphNode node = view.getBuilder(i).getNode(state, true);
				int number = component.getModel().getStateNameInstance(state);
				graph.addContent(pointToElement(state.getLabel(true), number, node));
			}
			graphs.addContent(graph);
		}
		root.addContent(graphs);
				
		return root;
	}

	private Element pointToElement(String name, int number, GraphNode point) {
		Element node = new Element("Node");
		node.setAttribute("name", name);
		node.setAttribute("number", Integer.toString(number));
		Element x = new Element("x");
		x.setText(Double.toString(point.getLocation().x));
		Element y = new Element("y");
		y.setText(Double.toString(point.getLocation().y));
		node.addContent(x);
		node.addContent(y);
		return node;
	}
	
}
