/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.modelcheckingview;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAtomicNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLBooleanNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLPropertyParser;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;

/**
 * Action for exporting the Abstraction View configuration
 * 
 * @author msmith
 * 
 */
public class ImportPropertiesAction extends Action {

	private ModelCheckingView view;
	
	public ImportPropertiesAction(ModelCheckingView view) {
		super("Import Properties", Action.AS_PUSH_BUTTON);
		this.setImageDescriptor(ImageManager.getInstance().getImageDescriptor(ImageManager.IMPORT));
		this.view = view;
		this.setToolTipText("Import CSL properties");
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
					". Unable to load all CSL properties. " +
					"Check that it is a valid CSL property file.");
		}
		
		// We finally need to update the property table
		view.handlePropertiesChanged();
	}
	
	private String getFileName() {
		// Open the save dialog
		FileDialog saveDialog = new FileDialog(view.getSite().getShell(), SWT.OPEN);
		saveDialog.setText("Load CSL Properties");
		saveDialog.setFilterPath(view.getPEPAPath());
		String[] filterExt = { "*.csl", "*.xml" };
		saveDialog.setFilterExtensions(filterExt);
		return saveDialog.open();
	}

	private Document parseXML(String fileName) {
		try {
			SAXBuilder parser = new SAXBuilder();
			return parser.build(fileName);
		} catch (JDOMException e1) {
			MessageDialog.openError(view.getSite().getShell(), "Error",
					"Unable to parse CSL properties file: " + fileName + ". Check that it is a valid XML file.");
		} catch (IOException e1) {
			MessageDialog.openError(view.getSite().getShell(), "Error",
					"Unable to read CSL properties file: " + fileName + ". An I/O error occurred.");
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
		readPropertyXML(root);
	}
	
	private void readPropertyXML(Element root) {
		KroneckerDisplayModel model = view.getModel();
		if (root == null) return;
		
		// Clear existing properties
		for (String name : model.getCSLPropertyNames()) {
			model.removeCSLProperty(name);
		}
		
		// Add new properties
		@SuppressWarnings("unchecked")
		List<Element> content = root.getChildren();
		ArrayList<String> unparsedProperties = new ArrayList<String>();
		ArrayList<CSLAtomicNode> unfoundAtoms = new ArrayList<CSLAtomicNode>();
		for (Element propElement : content) {
			String name = propElement.getAttributeValue("name");
			String propertyText = propElement.getText();
			CSLAbstractStateProperty property = CSLPropertyParser.parse(propertyText); 
			if (property != null) {
				CSLAbstractStateProperty cleanProperty = checkAtomicProperties(property, unfoundAtoms);
				model.addCSLProperty(name, cleanProperty);
			} else {
				unparsedProperties.add(name);
			}
		}
		
		String warningMessage = "";
		
		// Give a warning if we couldn't load some properties
		if (unparsedProperties.size() > 0) {
			String properties = "";
			for (String name : unparsedProperties) {
				properties = properties + name + ", ";
			}
			properties = properties.substring(0, properties.length() - 2);
			warningMessage += "Could not parse the following CSL properties: " + properties +
					          ". These have not been loaded.";
		}
		
		// Give a warning if undefined atomic properties were used
		if (unfoundAtoms.size() > 0) {
			String atoms = "";
			for (CSLAtomicNode atom : unfoundAtoms) {
				atoms = atoms + atom.getName() + ", ";
			}
			atoms = atoms.substring(0, atoms.length() - 2);
			if (warningMessage.length() > 0) warningMessage += "\n\n";
			warningMessage += "The following undefined atomic properties were found: " + atoms +
					          ". All occurrences of these properties have been replaced with \"true\".";
		}
		
		// Display warning message, if there is one
		if (warningMessage.length() > 0) {
			MessageDialog.openError(view.getSite().getShell(), "Warning", warningMessage);
		}
		
	}
	
	private CSLAbstractStateProperty checkAtomicProperties(CSLAbstractStateProperty property, ArrayList<CSLAtomicNode> unfoundAtoms) {
		KroneckerDisplayModel model = view.getModel();
		CSLAbstractStateProperty newProperty = property;
		ArrayList<CSLAtomicNode> atomicProperties = property.getAtomicProperties();
		String[] definedProperties = model.getAtomicProperties();
		for (CSLAtomicNode atom : atomicProperties) {
			String name = atom.getName();
			boolean found = false;
			for (String n : definedProperties) {
				if (n.equals(name)) {
					found = true;
					break;
				}
			}
			if (!found) {
				unfoundAtoms.add(atom);
				newProperty = newProperty.replace(atom, new CSLBooleanNode(true));
			}
		}
		return newProperty;
	}
	
}
