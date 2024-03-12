package uk.ac.ed.inf.pepa.ode.internal.odetojava.modules;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
  class does the special function of writing the solution to an ODE, produced by these
  solvers, to a file
*/
public class ODEFileWriter implements uk.ac.ed.inf.pepa.ode.internal.odetojava.modules.IWriterCallback
{
    // constructors

    public ODEFileWriter()
    {
    }

    // methods

    /*
      open an ODE file with append parameter set by caller
    */
    public void openFile(String fileName, boolean append)
    {
	if(fileName.equals(""))
	    fileWriting_on = false;
	else
	    fileWriting_on = true;

	if(fileWriting_on)
        {
	    try
	    {
		fw = new FileWriter(fileName, append);
		pw = new PrintWriter(fw);
	    }
	    catch(IOException e)
	    {
		System.out.println("error in opening file");
		System.out.println(e);
		System.exit(0);
	    }
	}
    }

    /*
      open an ODE file with append defaulting to false
    */
    public void openFile(String fileName)
    {
	if(fileName.equals(""))
	    fileWriting_on = false;
	else
	    fileWriting_on = true;

	if(fileWriting_on)
        {
	    try
	    {
		fw = new FileWriter(fileName);
		pw = new PrintWriter(fw);
	    }
	    catch(IOException e)
	    {
		System.out.println("error in opening file");
		System.out.println(e);
		System.exit(0);
	    }
	}
    }

    /*
      write one step to an ODE file
    */
    public void writeToFile(double t, double[] y)
    {
	if(fileWriting_on)
	{
	    pw.print(t + " ");   // write t to file
	    
	    for(int i= 0; i< y.length; i++)   // write y array to file
		pw.print(y[i] + " ");

	    pw.println();   // return for next line
	}
    }

    /*
      close an ODE file
    */
    public void closeFile()
    {
	if(fileWriting_on)
	{
	    try
	    {
		pw.close();
		fw.close();
	    }
	    catch(IOException e)
	    {
		System.out.println("error in closing file");
		System.out.println(e);
		System.exit(0);
	    }
	}
    }

    // instance variables

    private boolean fileWriting_on;

    private FileWriter fw;
    private PrintWriter pw;
	
    public boolean isCanceled() {
		return false;
	}
}
