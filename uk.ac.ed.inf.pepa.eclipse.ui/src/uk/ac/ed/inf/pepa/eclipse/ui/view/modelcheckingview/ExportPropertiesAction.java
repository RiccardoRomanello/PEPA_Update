/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.modelcheckingview;

import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayModel;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.eclipse.ui.ImageManager;

/**
 * Action for exporting the Abstraction View configuration
 * 
 * @author msmith
 * 
 */
public class ExportPropertiesAction extends Action {

	private ModelCheckingView view;

	public ExportPropertiesAction(ModelCheckingView view) {
		super("Export Properties", Action.AS_PUSH_BUTTON);
		this.setImageDescriptor(ImageManager.getInstance().getImageDescriptor(ImageManager.EXPORT));
		this.view = view;
		this.setToolTipText("Export CSL properties");
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
		saveDialog.setText("Save CSL Properties");
		saveDialog.setFilterPath(view.getPEPAPath());
		String[] filterExt = { "*.csl", "*.xml" };
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
		KroneckerDisplayModel model = view.getModel();
		Element root = new Element("Properties");
		
		for (String name : model.getCSLPropertyNames()) {
			CSLAbstractStateProperty property = model.getCSLProperty(name);
			Element propElement = new Element("CSLProperty");
			propElement.setAttribute("name", name);
			propElement.setText(property.toString());
			root.addContent(propElement);
		}
		
		return new Document(root);	
	}
	
}
