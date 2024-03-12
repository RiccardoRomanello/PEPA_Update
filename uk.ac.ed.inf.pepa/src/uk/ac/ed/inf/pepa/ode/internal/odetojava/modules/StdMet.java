package uk.ac.ed.inf.pepa.ode.internal.odetojava.modules;

/*
   class contains a large amount of methods that perform routine array and matrix
   calculations which are called in many routines of this package 
*/
public class StdMet
{
     // constructors
   
     // methods
	
     // unary operating array/matrix methods, miscellaneous methods

     /*
        method prints to screen the array passed to it (each element on a separate
        line)
     */
     public static void arrayprt(double[] a)
     {
          for(int i= 0; i< a.length; i++)   // print to screen each element in array,
          {
               System.out.println(a[i] + "   ");   // each element on its own line
          }
     }

     /*
        method sets array passed to it an array of all zeros
     */
     public static void zero_out(double[] a)
     {
          for(int i= 0; i< a.length; i++)   // set each double in array to zero
               a[i] = 0.0;
     }

     /*
        method copies the contents of one array to the other (the two array
        arguments must have the same dimensions, program exits if this is
        not the case)
     */
     public static void arraycpy(double[] a, double[] b)
     {	
          if(a.length != b.length)
          {   // test if array dimensions agree and handle case if not
              throw new IllegalArgumentException("array dimensions don't agree");

          }
		
          for(int i= 0; i< a.length; i++)   // copy all elements from array b to
               a[i] = b[i];   // array a
     }

     /*
        method copies the contents of one matrix to the other (the two matix
        arguments must have the same dimensions)
     */
     public static void matrixcpy(double[][] a, double[][] b)
     {
          if((a[0].length != b[0].length) || (a.length != b.length))
          {   // test if matrix dimensions agree and handle case if not
               System.out.println("matrix dimensions don't agree");
               System.exit(0);
          }
			
          for(int i= 0; i< a.length; i++)   // copy all elements from matrix b to
               for(int j= 0; j< a[0].length; j++)   // matrix a
                    a[i][j] = b[i][j];
     }

     /*
        method takes the piecewise product of one array and the scalar value, and copies
        it into the other array (both arrays must have the same dimensions)
     */
     public static void stam(double[] result, double scalar, double[] array)
     {	
          if(result.length != array.length)
          {   // test if array dimensions agree and handle case if not
               System.out.println("array dimensions don't agree");
               System.exit(0);
          }
	
          for(int i= 0; i< array.length; i++)   // set each element array[i] in array
               result[i] = scalar * array[i];   // to scalar * array[i] and copy this
                  // to other array
     }

     // binary operating array/matrix methods

     /*
        method gets the piecewise sum of two of the arrays and copies this sum into
        the third array (all three array arguments must have the same
        dimensions)
     */
     public static void arraysum(double[] c, double[] a, double[] b)
     {   // test if all 3 array dimensions agree and handle case if not
          if((a.length != b.length) || (a.length != c.length))
          {
               System.out.println("array dimensions don't agree");
               System.exit(0);
          }

          for(int i= 0; i< a.length; i++)   // get sum of ith elements of arrays
               c[i] = a[i] + b[i];   // a and b, and place this sum at the corresponding
                  // position in array c
     }
   
     /*
        method gets the difference of two of the arrays and copies this
        difference into the third array (all three array arguments must
        have the same dimensions)
     */
     public static void arraydiff(double[] c, double[] a, double[] b)
     {   // test if all 3 array dimensions agree and handle case if not
          if((a.length != b.length) || (a.length != c.length))
          {
               System.out.println("array dimensions don't agree");
               System.exit(0);
          }
		
          for(int i= 0; i< a.length; i++)   // get difference of ith elements
               c[i] = a[i] - b[i];   // of arrays a and b, and place  this difference
                  // at the corresponding position in array c
     }
   
     /*
        method gets the dot product of the 2 arrays (an array resulting in the
        multiplication of each element in array a by each element in array
        b) and copies this to the result array (all three array arguments must
        have the same dimensions)
     */
     public static void dotPro(double[] result, double[] a, double[] b)
     {   // test if all 3 array dimensions agree and handle case if not
          if ((result.length != a.length) || (result.length != b.length))
          {
               System.out.println("array dimensions don't agree");
               System.exit(0);
          }

          for(int i= 0; i< a.length; i++)   // get product of ith elements of
               result[i] = a[i] * b[i];   // of arrays a and b, and place this product
                  // at the corresponding position in result array
     }
   
     /*
        method gets the piecewise quotient of 2 arrays (an array resulting in
        the division of each element in array a by each element in array b)
        and copies this to the result vector (all three array arguments must
        have the same dimensions)
     */
     public static void dotQuo(double[] result, double[] a, double[] b)
     {   // test if all 3 array dimensions agree and handle case if not
          if ((result.length != a.length) || (result.length != b.length))
          {
               System.out.println("array dimensions don't agree");
               System.exit(0);
          }

          for(int i= 0; i< a.length; i++)   // get quotient of ith elements of
               result[i] = a[i] / b[i];   // of arrays a and b, and place this quotient
                  // at the corresponding position in result array
     }

     /*
        method takes the product of a matrix and an array and returns an array
        (matrix width must be the same as array lengths)
     */
     public static void mtam(double[] result, double [][] matrix, double[] array)
     {
          if((matrix[0].length != array.length)||(result.length != array.length))
          {   // test if array dimensions agree and handle case if not
               System.out.println("array dimensions don't agree");
               System.exit(0);
          }

          double[] temp = new double[array.length];   // for each row multiplication
          double sigma = 0;   // the sum of each row multiplication
      
          for(int i= 0; i< array.length; i++)
          {
               dotPro(temp, matrix[i], array);   // row i of matrix multiplied by array

               for(int j= 0; j< array.length; j++)   // the sum of above row
                    sigma += temp[j];
         
               result[i] = sigma;   // put the sum in ith position of result array
               sigma = 0;   // reset the sum
          }
     }

     // norms

     /*
        method takes the infinity norm of the array and returns it
     */
     public static double normInf(double[] vector)
     {
          double max = Math.abs(vector[0]);   // start comparing to the first
             // element

          for(int i= 1; i< vector.length; i++)
          {
               if(Math.abs(vector[i]) > max)   // ultimately get the maximum
                    max = Math.abs(vector[i]);   // absolute element of the array
          }
      
          return(max);
     }

     /*
        method takes the rms-norm (root mean square) of the vector and returns it
     */
     public static double rmsNorm(double[] vector)
     {
          double sum = 0.0;

          for(int i= 0; i< vector.length; i++)   // sum of squared terms
               sum += vector[i] * vector[i];
      
          sum /= vector.length;   // sum of squared terms divided by n
          sum = Math.sqrt(sum);   // square root of above
      
          return(sum);
     }
   
     // tolerance methods 

     /*
        method calculates epsilon with arrays xn, xnPlusOne, atol and rtol and returns
        epsilon in the result array (all arrays must have the same dimensions)
     */
     public static void epsilon(double[] result, double[] xn, double[] xnPlusOne, double atol, double rtol)
     {
          if((result.length != xn.length) || (result.length != xnPlusOne.length))
          {   // test if array dimensions agree and handle case if not
               System.out.println("array dimensions don't agree");
               System.exit(0);
          }

          for(int i= 0; i< result.length; i++)
          {   // set each element in the epsilon vector to its intended value
               result[i] = atol + Math.max(Math.abs(xn[i]), Math.abs(xnPlusOne[i]))*rtol;
          }
     }

     /*
        method calculates tau with arrays xn, xnPlusOne, absolute tolerance
        and relative tolerance and returns tau in the result array
       (all arrays must have the same dimensions)
     */
     public static void tau(double[] result, double[] xn, double[] xnPlusOne, double[] atol, double[] rtol)
     {
          if((result.length != xn.length) || (result.length != xnPlusOne.length) || (result.length != atol.length) || (result.length != rtol.length))
          {   // test if array dimensions agree and handle case if not
               System.out.println("array dimensions don't agree");
               System.exit(0);
          }

          for(int i= 0; i< result.length; i++)
          {   // set each element in the tau vector to its intended value
               result[i] = atol[i] + Math.max(Math.abs(xn[i]), Math.abs(xnPlusOne[i])) * rtol[i];
          }
     }
}