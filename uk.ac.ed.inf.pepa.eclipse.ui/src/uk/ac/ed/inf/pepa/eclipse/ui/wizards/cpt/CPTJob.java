package uk.ac.ed.inf.pepa.eclipse.ui.wizards.cpt;

import java.util.Observer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;

public class CPTJob extends Job {
	
	Observer observer;
	
	public CPTJob(String name, Observer observer) {
		super(name);
		this.observer = observer;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		CPTAPI.createCPT(monitor);
		
		IStatus status = CPTAPI.startCPT();
		
		observer.update(null, null);
		
		return status;
	}
	

}
