/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterManager;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterModel;

/**
 * Creates, saves, renames and deletes state filter configurations.
 * <p>
 * This is the CareTaker of the Memento Pattern. The Memento Provider is the
 * FilterModel.
 * 
 * @author mtribast
 * 
 */
public class FilterManager implements IFilterManager {

	private IProcessAlgebraModel pepaModel;
	
	List<FilterModel> filterModels;

	private final static String TAG_CONFIGURATIONS = "configurations";

	private final static String TAG_CONFIGURATION = "configuration";

	/**
	 * Create the filter manager for this filter model.
	 * <p>
	 * A listener can be added in order to be notified by changes in the current
	 * selection of a memento.
	 * 
	 * @param model
	 *            the PEPA model
	 * @param listener
	 *            the memento listener if a new memento is selected, can be null
	 */
	public FilterManager(IProcessAlgebraModel pepaModel) {
		this.pepaModel = pepaModel;
		initConfigurations();
	}

	public IProcessAlgebraModel getProcessAlgebraModel() {
		return this.pepaModel;
	}

	private void initConfigurations() {
		//configurations = new ArrayList<IConfiguration>();
		filterModels = new ArrayList<FilterModel>();

		FileReader reader = null;
		try {
			reader = new FileReader(getStorageFile());
			loadConfigurations(XMLMemento.createReadRoot(reader));

		} catch (FileNotFoundException e) {
			// do nothing, no resource saved so far
		} catch (WorkbenchException e) {
			PepaLog.logError(e);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					PepaLog.logError(e);
				}
		}

	}

	private void loadConfigurations(XMLMemento memento) {
		IMemento[] children = memento.getChildren(TAG_CONFIGURATION);
		for (IMemento child : children) {
			FilterModel model = new FilterModel(pepaModel);
			model.setMemento(child);
			filterModels.add(model);
		}

	}

	File getStorageFile() {
		return pepaModel.getUnderlyingResource().getLocation()
				.addFileExtension("filters").toFile();
	}
	
	/**
	 * Should be called when OK is pressed on the dialog
	 */
	void saveConfigurations() {
		XMLMemento memento = XMLMemento.createWriteRoot(TAG_CONFIGURATIONS);
		saveConfigurations(memento);
		FileWriter writer = null;
		try {
			writer = new FileWriter(getStorageFile());
			memento.save(writer);
		} catch (IOException e) {
			PepaLog.logError(e);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
					PepaLog.logError(e);
				}
		}
	}

	private void saveConfigurations(XMLMemento memento) {
		for (FilterModel configuration : filterModels) {
			IMemento child = memento.createChild(TAG_CONFIGURATION);
			configuration.getMemento(child);
		}
	}
	
	public IFilterModel[] getFilterModels() {
		return filterModels.toArray(new IFilterModel[filterModels.size()]);
	}

}
