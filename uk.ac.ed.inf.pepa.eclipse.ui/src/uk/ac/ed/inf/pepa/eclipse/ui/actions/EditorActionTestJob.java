/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/** 
 * This class has been created to test the job api under Mac OS X
 * @author mtribast
 *
 */
public class EditorActionTestJob implements IEditorActionDelegate {
	
	public class TestJob extends Job {
		public TestJob() {
			super("Test job...");
			this.setUser(true);
		}
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Begin to sleep", 10);
			for (int i = 0; i < 10; i++)
				try {
					Thread.sleep(10000);
					monitor.worked(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			monitor.done();
			return Status.OK_STATUS;
		}
		
		
	}
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		// TODO Auto-generated method stub

	}

	public void run(IAction action) {
		new TestJob().schedule();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

}
