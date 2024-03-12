package uk.ac.ed.inf.pepa.ode.internal.odetojava.modules;

import uk.ac.ed.inf.pepa.ode.internal.odetojava.interpolants.BthDopr;

/*
   class stores and retrieves a Butcher tableau for a Runge-Kutta scheme
*/
public class Btableau
{
     // constructors

     /*
        constructor stores Butcher tableau for RK scheme: used paricularily
        with an ERK scheme
     */
     public Btableau(double[][] a, double[] b, double[] c, String FSAL)
     {
          this.a = new double[a.length][a.length];   // initialize all parts of
          this.b = new double[b.length];   // particular Butcher tableau
          this.bEmb = new double[bEmb.length];
          this.c = new double[c.length];
      
          StdMet.matrixcpy(this.a, a);   // pass parameters to instance variables
      
          StdMet.arraycpy(this.b, b);
          StdMet.arraycpy(this.bEmb, bEmb);
          StdMet.arraycpy(this.c, c);

          // first same as last functionality

          if(FSAL.equals("FSALenabled"))
          {
               this.FSALenabled = true;
               
               double accum = 0.0;
               
               for(int i= 0; i< b.length; i++)
                    accum += b[i];
                    
               if(accum < 0.999999 || accum > 1.000001)
               {
                    System.out.println("weights don't add up to 1");
                    System.exit(0);
               }
               
               for(int i= 0; i< b.length; i++)
                    if(b[i] != a[b.length - 1][i])
                    {
                         System.out.println("scheme is not first same as last: option will default to disabled");
                         break;
                    }
          }
          else
               if(FSAL.equals("FSALdisabled"))
                    this.FSALenabled = false;
               else
               {
                    System.out.println("String parameter must be either: 1) \"FSALenabled\" or 2) \"FSALdisabled\"");
                    System.exit(0);
               }
     }

     /*
        constructor stores Butcher tableau for RK scheme: used paricularily
        with an embedded ERK scheme
     */   
     public Btableau(double[][] a, double[] b, double[] bEmb, double[] c, String FSAL)
     {
          this.a = new double[a.length][a.length];   // initialize all parts of
          this.b = new double[b.length];   // particular Butcher tableau
          this.bEmb = new double[bEmb.length];
          this.c = new double[c.length];
      
          StdMet.matrixcpy(this.a, a);   // pass parameters to instance variables
      
          StdMet.arraycpy(this.b, b);
          StdMet.arraycpy(this.bEmb, bEmb);
          StdMet.arraycpy(this.c, c);

          // first same as last functionality

          if(FSAL.equals("FSALenabled"))
          {
               this.FSALenabled = true;
               
               double accum = 0.0;
               
               for(int i= 0; i< b.length; i++)
                    accum += b[i];
                    
               if(accum < 0.999999 || accum > 1.000001)
               {
                    System.out.println("weights don't add up to 1");
                    System.exit(0);
               }
               
               for(int i= 0; i< b.length; i++)
                    if(b[i] != a[b.length - 1][i])
                    {
                         System.out.println("scheme is not first same as last: option will default to disabled");
                         break;
                    }
          }
          else
               if(FSAL.equals("FSALdisabled"))
                    this.FSALenabled = false;
               else
               {
                    System.out.println("String parameter must be either: 1) \"FSALenabled\" or 2) \"FSALdisabled\"");
                    System.exit(0);
               }
     }

     /*
        constructor stores Butcher tableau for RK scheme: used paricularily
        with an ERK triple scheme
     */ 
     public Btableau(double[][] a, double[] b, double[] bEmb, Btheta btheta, double[] c, String FSAL)
     {
          this.a = new double[a.length][a.length];   // initialize all parts of
          this.b = new double[b.length];   // particular Butcher tableau
          this.bEmb = new double[bEmb.length];
          this.btheta = btheta;
          this.c = new double[c.length];
      
          StdMet.matrixcpy(this.a, a);   // pass parameters to instance vars
      
          StdMet.arraycpy(this.b, b);
          StdMet.arraycpy(this.bEmb, bEmb);
          StdMet.arraycpy(this.c, c);

          // first same as last functionality

          if(FSAL.equals("FSALenabled"))
          {
               this.FSALenabled = true;
               
               double accum = 0.0;
               
               for(int i= 0; i< b.length; i++)
                    accum += b[i];

               if(accum < 0.999999 || accum > 1.000001)
               {
                    System.out.println("weights don't add up to 1");
                    System.exit(0);
               }
               
               for(int i= 0; i< b.length; i++)
                    if(b[i] != a[b.length - 1][i])
                    {
                         System.out.println("scheme is not first same as last: option will default to disabled");
                         break;
                    }
          }
          else
               if(FSAL.equals("FSALdisabled"))
                    this.FSALenabled = false;
               else
               {
                    System.out.println("String parameter must be either: 1) \"FSALenabled\" or 2) \"FSALdisabled\"");
                    System.exit(0);
               }         
     }

     /*
        constructor stores Butcher tableau for RK scheme: used paricularily
        with an Imex scheme (in autonomous form)
     */
     public Btableau(double[][] a, double[][] ahat, double[] b, double[] bhat)
     {
          this.a = new double[a.length][a.length];   // initialize all parts of
          this.ahat = new double[ahat.length][ahat.length];   // particular
          this.b = new double[b.length];   // Butcher tableau
          this.bhat = new double[bhat.length];
      
          StdMet.matrixcpy(this.a, a);   // pass parameters to instance vars
          StdMet.matrixcpy(this.ahat, ahat);
      
          StdMet.arraycpy(this.b, b);
          StdMet.arraycpy(this.bhat, bhat);
          
          FSALenabled = false;   // first same as last functionality automatically
             // disabled as it is not implemented in Imex schemes
     }

     /*
        constructor constructs one of several special pre-defined
        schemes given a String that identifies that scheme
     */
     public Btableau(String special)
     {
          if(special.equals("erk4"))
          {
               // classic ERK4 butcher tableau

               a = new double[4][4];   // initialize the matrices and arrays that
               b = new double[4];   // store each Butcher tableau component to
               bhat = new double[4];   // lengths equal to number of stages in
               c = new double[4];   // scheme (s)
 
               a[1][0] = 0.5;   // hard coded explicit assignment of each element
               a[2][1] = 0.5;
               a[3][2] = 1.0;
		
               b[0] = (1.0)/(6.0);
               b[1] = (1.0)/(3.0);
               b[2] = (1.0)/(3.0);
               b[3] = (1.0)/(6.0);
		
               c[1] = 0.5;
               c[2] = 0.5;
               c[3] = 1.0;

               FSALenabled = false;   // first same as last functionality automatically
                  // disabled (no checking required)
          }
          else if(special.equals("dopr54"))
          {
               // Dormand-Prince Butcher tableau

               a = new double[7][7];   // initializations
               b = new double[7];
               bEmb = new double[7];
               c = new double[7];
         
               a[1][0] = (1.0)/(5.0);   // assignment
      
               a[2][0] = (3.0)/(40.0);
               a[2][1] = (9.0)/(40.0);
		
               a[3][0] = (44.0)/(45.0);
               a[3][1] = (-56.0)/(15.0);
               a[3][2] = (32.0)/(9.0);
		
               a[4][0] = (19372.0)/(6561.0);
               a[4][1] = (-25360.0)/(2187.0);
               a[4][2] = (64448.0)/(6561.0);
               a[4][3] = (-212.0)/(729.0);
         
               a[5][0] = (9017.0)/(3168.0);
               a[5][1] = (-355.0)/(33.0);
               a[5][2] = (46732.0)/(5247.0);
               a[5][3] = (49.0)/(176.0);
               a[5][4] = (-5103.0)/(18656.0);
		
               a[6][0] = (35.0)/(384.0);
               a[6][1] = 0.0;
               a[6][2] = (500.0)/(1113.0);
               a[6][3] = (125.0)/(192.0);
               a[6][4] = (-2187.0)/(6784.0);
               a[6][5] = (11.0)/(84.0);
		
               b[0] = (35.0)/(384.0);
               b[1] = 0.0;
               b[2] = (500.0)/(1113.0);
               b[3] = (125.0)/(192.0);
               b[4] = (-2187.0)/(6784.0);
               b[5] = (11.0)/(84.0);
               b[6] = 0.0;

               bEmb[0] = (5179.0)/(57600.0);   // note: this is the embedded part
               bEmb[1] = 0.0;
               bEmb[2] = (7571.0)/(16695.0);
               bEmb[3] = (393.0)/(640.0);
               bEmb[4] = (-92097.0)/(339200.0);
               bEmb[5] = (187.0)/(2100.0);
               bEmb[6] = (1.0)/(40.0);

               btheta = new BthDopr(b);   // use pre-defined interpolant for Dormand-Prince
                  // interpolation (defined in interpolants folder)
		
               c[0] = 0.0;
               c[1] = (1.0)/(5.0);
               c[2] = (3.0)/(10.0);
               c[3] = (4.0)/(5.0);
               c[4] = (8.0)/(9.0);
               c[5] = 1.0;
               c[6] = 1.0;

               FSALenabled = true;   // first same as last functionality automatically
                  // enabled (no checking required)
          }
          else if(special.equals("imex443"))
          {
               // IMEX443 Butcher tableau

               // initializations

               a = new double[4][4];   // implicit part
               b = new double[4];

               ahat = new double[5][5];   // explicit part
               bhat = new double[5];

               // assignment

               a[0][0] = (1.0)/(2.0);   // implicit part

               a[1][0] = (1.0)/(6.0);
               a[1][1] = (1.0)/(2.0);
         
               a[2][0] = (-1.0)/(2.0);
               a[2][1] = (1.0)/(2.0);
               a[2][2] = (1.0)/(2.0);
         
               a[3][0] = (3.0)/(2.0);
               a[3][1] = (-3.0)/(2.0);
               a[3][2] = (1.0)/(2.0);
               a[3][3] = (1.0)/(2.0);

               b[0] = (3.0)/(2.0);
               b[1] = (-3.0)/(2.0);
               b[2] = (1.0)/(2.0);
               b[3] = (1.0)/(2.0);
         
               ahat[1][0] = (1.0)/(2.0);   // explicit part
         
               ahat[2][0] = (11.0)/(18.0);
               ahat[2][1] = (1.0)/(18.0);
         
               ahat[3][0] = (5.0)/(6.0);
               ahat[3][1] = (-5.0)/(6.0);
               ahat[3][2] = (1.0)/(2.0);
         
               ahat[4][0] = (1.0)/(4.0);
               ahat[4][1] = (7.0)/(4.0);
               ahat[4][2] = (3.0)/(4.0);
               ahat[4][3] = (-7.0)/(4.0);
         
               bhat[0] = (1.0)/(4.0);
               bhat[1] = (7.0)/(4.0);
               bhat[2] = (3.0)/(4.0);
               bhat[3] = (-7.0)/(4.0);       

               FSALenabled = false;   // first same as last functionality automatically
                  // disabled (no checking required)
          }
          else if(special.equals("imex343"))
          {
               // IMEX343 Butcher tableau

               // initializations

               a = new double[3][3];   // implicit part
               b = new double[3];

               ahat = new double[4][4];   // explicit part
               bhat = new double[4];

               // assignment

               a[0][0] = 0.4358665215;   // implicit part

               a[1][0] = 0.2820667392;
               a[1][1] = 0.4358665215;
         
               a[2][0] = 1.208496649;
               a[2][1] = -0.644363171;
               a[2][2] = 0.4358665215;

               b[0] = 1.208496649;
               b[1] = -0.644363171;
               b[2] = 0.4358665215;
         
               ahat[1][0] = 0.4358665215;   // explicit part
         
               ahat[2][0] = 0.3212788860;
               ahat[2][1] = 0.3966543747;
         
               ahat[3][0] = -0.105858296;
               ahat[3][1] = 0.5529291479;
               ahat[3][2] = 0.5529291479;
         
               bhat[0] = 0.0;
               bhat[1] = 1.208496649;
               bhat[2] = -0.644363171;
               bhat[3] = 0.4358665215;

               FSALenabled = false;   // first same as last functionality automatically
                  // disabled (no checking required)
          }
          else
          {
               // default case: strings other than the ones above are not handled,
               // so we print message and halt program exectution

               System.out.println("unknown Butcher tableau scheme");
               System.exit(0);
          }
     }

     // methods
	
     /*
        method returns length (number of columns) of the matrix a of Butcher
        tableau
     */
     public int getal()
     {
          return(a[0].length);
     }
	
     /*
        method returns height (number of rows) of the matrix a of Butcher
        tableau
     */
     public int getah()
     {
          return(a.length);
     }

     /*
        method returns length (number of columns) of the matrix ahat of Butcher
        tableau
     */
     public int getahatl()
     {
          return(ahat[0].length);
     }
	
     /*
        method returns height (number of rows) of the matrix ahat of Butcher
        tableau
     */
     public int getahath()
     {
          return(ahat.length);
     }
	
     /*
        method returns length of the vector b of the Butcher tableau
     */
     public int getbl()
     {
          return(b.length);
     }
	
     /*
        method returns length of the vector bhat of the Butcher tableau
     */
     public int getbhatl()
     {
          return(bhat.length);
     }

     /*
        method returns length of the vector bEmb of the Butcher tableau
     */
     public int getbEmbl()
     {
          return(bEmb.length);
     }
   
     /*
        method returns length of the vector bhatEmb of the Butcher tableau
     */
     public int getbhatEmbl()
     {
          return(bhatEmb.length);
     }   
	
     /*
        method returns length of the vector c of the Butcher tableau
     */
     public int getcl()
     {
          return(c.length);
     }
   
     /*
        method returns length of the vector chat of the Butcher tableau
     */
     public int getchatl()
     {
          return(chat.length);
     }
	
     /*
        method returns the matrix a of the Butcher tableau
     */
     public double[][] get_a()
     {
          return(a);
     }

     /*
        method returns the matrix ahat of the Butcher tableau
     */
     public double[][] get_ahat()
     {
          return(ahat);
     }
	
     /*
        method returns the array b of the Butcher tableau
     */
     public double[] get_b()
     {
          return(b);
     }
	
     /*
        method returns the array bhat of the Butcher tableau
     */
     public double[] get_bhat()
     {
          return(bhat);
     }

     /*
        method returns the array bEmb of the Butcher tableau
     */
     public double[] get_bEmb()
     {
          return(bEmb);
     }
   
     /*
        method returns the array bhatEmb of the Butcher tableau
     */
     public double[] get_bhatEmb()
     {
          return(bhatEmb);
     }
	
     /*
        method returns the object btheta of the Butcher tableau
     */
     public Btheta get_btheta()
     {
          return(btheta);
     }

     /*
        method returns the array c of the Butcher tableau
     */
     public double[] get_c()
     {
          return(c);
     }
   
     /*
        method returns the array chat of the Butcher tableau
     */
     public double[] get_chat()
     {
          return(chat);
     }

     /*
        method returns whether scheme is first same as last or not
     */
     public boolean get_FSALenabled()
     {
          return(FSALenabled);
     }

     // instance variables
	
     private double[][] a;   // matrix a: explicit ERK, implicit in IMEX
     private double[][] ahat;   // matrix ahat: unused in ERK, explicit in IMEX

     private double[] b;   // array b: expicit in ERK, implicit in IMEX
     private double[] bhat;   // array bhat: unused in ERK, explicit in IMEX

     private double[] bEmb;   // array bEmb, only used in embedded method: 
        // explicit in embedded ERK, implicit in embedded IMEX (*note that in theory
        // an embedded IMEX scheme exists, no solvers in this package use such a scheme
     private double[] bhatEmb;   // array bhatEmb, only used in embedded method:
        // unused in embedded ERK, explicit in embedded IMEX

     private Btheta btheta;   // array btheta, only used with schemes with interpolants

     private double[] c;   // array c: explicit in ERK, implicit in IMEX
     private double[] chat;   // array chat: unused in ERK, explicit in IMEX
     
     private boolean FSALenabled;   // whether first same as last functionality of the
        // scheme (if this scheme has the property to begin with) is enabled
}