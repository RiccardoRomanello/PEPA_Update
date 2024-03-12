package uk.ac.ed.inf.pepa.ode.internal.odetojava.modules;

/*
   class stores and retrieves values representing the span of time for an
   integration interval
*/
public class Span
{
     // constructors
	
     /*
        constructor initializes the two doubles to the starting and stopping
        points of the span (span is defined such that there will be integration
        between these 2 points but not interpolation)
     */
     public Span(double a, double b)
     {
          this.t0 = a;   // get initial and final times
          this.tf = b;

          if(tf > t0)   // property check
               this.proper = true;
          else
               this.proper = false;
     }

     /*
        constructor initializes the 2 doubles to the starting and stopping
        points of the span of the integration interval and then creates
        an array of evenly separated points, starting at with the initial time
        point for an interval of integration with interpolation at these points
        (since the endpoint is also included in this array, it is usually the
        only point that doesn't have the same separation as the rest of the
        points).  The separation of these points is specified by the 3rd
        parameter.  note: only solvers with continuous interpolation will
        interpolate the solution, given a span defined in this way
     */
     public Span(double a, double b, double inc)
     {
          this.t0 = a;   // get initial and final times
          this.tf = b;

          if((tf > t0) && (inc > 0))
          {
               double intervalSize = tf - t0;   // find out how many of these evenly
               double arraySizeD = intervalSize/inc;   // spaced points lie on interval
               int arraySize = (int)Math.floor(arraySizeD);   // to figure out length of array
               arraySize += 2;   // account for initial and final time points in array

               this.times = new double[arraySize];   // put evenly spaced points in array
               double accum = t0;
               for(int i= 0; i< times.length - 1; i++)
               {
                    times[i] = accum;
                    accum += inc;
               }

               times[times.length - 1] = tf;   // put final time point in array

               this.proper = true;   // property check
               for(int i= 0; i< times.length - 1; i++)
                    if(times[i] >= times[i + 1])
                         this.proper = false;
          }
          else
               this.proper = false;
     }

     /*
        constructor gets array and stores it in the span, so that interpolation
        can be done according to the points in that array
        note: only solvers with continuous interpolation will
        interpolate the solution, given a span defined in this way
     */
     public Span(double[] times)
     {
          this.times = new double[times.length];   // fill array
          for(int i= 0; i< times.length; i++)
             this.times[i] = times[i];

          this.t0 = times[0];   // get initial and final times
          this.tf = times[times.length - 1];

          this.proper = true;   // property check
          for(int i= 0; i< times.length - 1; i++)
               if(times[i] >= times[i + 1])
                    this.proper = false;
     }

     // methods
	
     /*
        method retrieves the staring point of the span
     */
     public double get_t0()
     {
          return(t0);
     }
	
     /*
        method retrieves the stopping point of the span
     */
     public double get_tf()
     {
          return(tf);
     }

     /*
        method retrieves the array of times for interpolation
     */
     public double[] get_times()
     {
          return(times);
     }

     /*
        method retrieves the length of the times array
     */
     public int get_timesLength()
     {
          if(times == null)
               return(0);
          return(times.length);
     }

     /*
        method retrieves whether the span is out of order or not
     */
     public boolean get_property()
     {
          return(proper);
     }

     // instance variables

     private double t0;   // starting time
     private double tf;   // stopping time
     private double[] times;   // array of times for interpolation
     private boolean proper;   // whether the span is out of order or not
}