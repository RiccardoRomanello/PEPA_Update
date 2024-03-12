package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt.pages;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.config.Config;

public class SaveAsCapacityPlanningWizardPage extends WizardNewFileCreationPage {
	
	public SaveAsCapacityPlanningWizardPage(String title, IStructuredSelection selection) {
		
		super(CPTAPI.getSearchControls().getValue() + ": " + CPTAPI.getEvaluationControls().getValue(), selection);
		
		setTitle(CPTAPI.getSearchControls().getValue() + ": " + CPTAPI.getEvaluationControls().getValue());
		setDescription("Save model configurations to");
		
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss_");
		Date date = new Date();
		
		String newFilename = dateFormat.format(date) + CPTAPI.getFileName();
		
		CPTAPI.setFileName(newFilename);
		
		setFileName(newFilename);
		setPageComplete(false);
		
	}
	
	public boolean validatePage() {
		
		Path frontPath = new Path(CPTAPI.getFolderName());
		String fileName = getFileName();
		String newName = frontPath.addTrailingSeparator().toOSString() + fileName;
		CPTAPI.setFileName(newName);
		
		
		boolean complete, exists;
		complete = false;
		exists = doesFileExist();
		
		/* Check extension */
		Path path = new Path(fileName);
		
		if (path.getFileExtension() == null || path.getFileExtension().compareToIgnoreCase(Config.EXTENSION) != 0) {
			this.setErrorMessage("Wrong extension. It must be a ."
					+ Config.EXTENSION + " file");
		} else if (exists) {
			this.setErrorMessage("File already exists!");
			complete = false;
		} else {
			this.setErrorMessage(null);
			complete = true;
		}
		
		return complete;
	}
	
	public boolean doesFileExist(){
		
		File f = new File(CPTAPI.getFileName());
		
		if(f.exists()){
			return true;
		} else {
			return false;
		}
		
	}

}
