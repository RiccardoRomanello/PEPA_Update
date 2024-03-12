package uk.ac.ed.inf.pepa.ode.internal.odetojava.modules;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

/*
   this is the ODE interface (or abstract superclass).  Any ODE that is defined in
   this package or that the user wants to define must implement (be a subclass
   of) this class
*/
public interface ODE
{
     // constructors
   
     // methods

     /*
        an ODE function declaration that is defined by the implementor
     */
     public double[] f(double t, double[] x) throws DifferentialAnalysisException;   // a dummy f function
        // (method) that will be overloaded to represent an ODE

     /*
        an event function declarataion that is defined by the implementor
        (*note that an event function is one that defines where an event
        occurs in the ODE).  Event functions are recgnized by the event
        locator of ErkTriple and DormandPrince solvers
     */
     public double[] g(double t, double[] x);   // a dummy g function
        // (method) that will be overloaded to represent an event function
}