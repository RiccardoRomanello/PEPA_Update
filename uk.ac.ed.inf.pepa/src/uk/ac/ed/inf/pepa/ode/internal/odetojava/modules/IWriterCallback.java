package uk.ac.ed.inf.pepa.ode.internal.odetojava.modules;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public interface IWriterCallback {

	/*
	  open an ODE file with append parameter set by caller
	 */
	public void openFile(String fileName, boolean append);

	/*
	  open an ODE file with append defaulting to false
	 */
	public void openFile(String fileName);

	/*
	  write one step to an ODE file
	 */
	public void writeToFile(double t, double[] y) throws DifferentialAnalysisException;

	/*
	  close an ODE file
	 */
	public void closeFile();
	
	public boolean isCanceled();

}