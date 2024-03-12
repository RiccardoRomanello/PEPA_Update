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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;

import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraManager;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraManagerListener;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.core.PepaCore;
import uk.ac.ed.inf.pepa.eclipse.core.PepaLog;
import uk.ac.ed.inf.pepa.eclipse.ui.IFilterManagerProvider;

/**
 * Internal implementation of the filter manager
 * 
 * @author mtribast
 * 
 */
public class FilterManagerProvider implements IFilterManagerProvider {

	// =============================================
	// List of tags for mementos
	// =============================================
	private static final String TAG_MODELS = "filters";

	private static final String TAG_MODEL = "model";

	private static final String TAG_MODEL_PATH = "modelpath";

	private static final String TAG_FILTER = "filter";

	private IProcessAlgebraManagerListener fListener = new IProcessAlgebraManagerListener() {

		public void modelAdded(IProcessAlgebraModel model) {
		}

		public void modelRemoved(IProcessAlgebraModel model) {
			checkModelRemoved(model);

		}

	};

	private HashMap<IProcessAlgebraModel, FilterManager> fMap;

	public FilterManagerProvider(IProcessAlgebraManager processAlgebraManager) {
		fMap = new HashMap<IProcessAlgebraModel, FilterManager>();
		checkExistence();
		processAlgebraManager.addListener(fListener);
	}

	/*
	 * Checks for exsistence of filter storage of the IPepaModel's that the
	 * manager kept track of during the last workbench session.
	 * 
	 * This is needed because a filter manager is not guaranteed to
	 * be created when an IPepaModel is obtained. Therefore,
	 * when this listener is not notified , unparented filter
	 * storage can be present in the workspace.
	 * 
	 */
	private void checkExistence()  {
		FileReader reader = null;
		try {
			reader = new FileReader(getStorageFile());
			doCheckExistence(XMLMemento.createReadRoot(reader));
		} catch (FileNotFoundException e) {
			// file not avaialble yet, it's OK
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

	private void doCheckExistence(XMLMemento memento) {
		IMemento[] children = memento.getChildren(TAG_MODEL);
		for (IMemento child : children) {
			String pepaModelString = child.getString(TAG_MODEL_PATH);
			//System.err.println("Checking " + pepaModelString);
			IPath pepaModelPath = Path.fromPortableString(pepaModelString);
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFile(pepaModelPath);
			try {
				resource.refreshLocal(IResource.DEPTH_ZERO, null);
				if (resource.getProject().isOpen() && 
						!resource.exists()) {
					// resource does not exist any more
					// delete filter storage
					String filterStorage = child.getString(TAG_FILTER);
					File file = new File(filterStorage);
					if (file.exists())
						tryDeleting(file);
					//System.err.println("which didn't exist");	
				} else {
					//System.err.println("which exists");
				}
			} catch (CoreException e) {
				PepaLog.logError(e);
				// move on to the next child
			}
			
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see uk.ac.ed.inf.pepa.eclipse.ui.IFilterManagerProvider#getFilterManager(uk.ac.ed.inf.pepa.eclipse.core.IPepaModel)
	 */
	public FilterManager getFilterManager(IProcessAlgebraModel model) {
		FilterManager manager = fMap.get(model);
		if (manager == null) {
			manager = new FilterManager(model);
			fMap.put(model, manager);
		}
		return manager;
	}

	public void shutdown() {
		PepaCore.getDefault().getPepaManager().removeListener(fListener);
		saveMappings();
	}

	private void saveMappings() {
		XMLMemento memento = XMLMemento.createWriteRoot(TAG_MODELS);
		saveModels(memento);
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

	private File getStorageFile() {
		return uk.ac.ed.inf.pepa.eclipse.ui.Activator.getDefault()
				.getStateLocation().append("filter_manager.xml").toFile();
	}

	private void saveModels(IMemento memento) {
		for (Map.Entry<IProcessAlgebraModel, FilterManager> mapping : fMap.entrySet()) {
			IMemento child = memento.createChild(TAG_MODEL);
			child.putString(TAG_FILTER, mapping.getValue().getStorageFile()
					.getAbsolutePath());
			child.putString(TAG_MODEL_PATH, mapping.getKey()
					.getUnderlyingResource().getFullPath().toPortableString());
		}
	}

	/*
	 * Called back by fListener
	 */
	private void checkModelRemoved(IProcessAlgebraModel model) {
		FilterManager mng = fMap.get(model);
		if (mng == null) {
			//System.err.println("No storage for "
			//		+ model.getUnderlyingResource().getName());
			return; // no storage
		}
		// storage that has to be deleted
		tryDeleting(mng.getStorageFile());
		
	}
	
	/* Deletes a model's filter storage */
	private void tryDeleting(File toBeDeleted) {
		if (toBeDeleted.exists()) {
			boolean isSuccess = toBeDeleted.delete();
			//if (isSuccess)
				//System.err.println(toBeDeleted.getAbsolutePath()
				//		+ " succesfully deleted");
		} else {
			//System.err.println("No storage file exists");
		}
	}

}
