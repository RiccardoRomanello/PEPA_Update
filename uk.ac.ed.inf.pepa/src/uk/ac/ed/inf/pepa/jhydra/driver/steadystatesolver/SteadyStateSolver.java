package uk.ac.ed.inf.pepa.jhydra.driver.steadystatesolver;

import uk.ac.ed.inf.pepa.jhydra.matrix.*;
import java.util.BitSet;

public class SteadyStateSolver {

	private Matrix myMatrix;
	
	public SteadyStateSolver(Matrix m){
		myMatrix = m;
	}
	
	public double[] solve(BitSet sources){
		myMatrix.pify();

		//columns of original matrix are rows of the transposed matrix
		MatrixRow mC;
		MatrixElement mE;

		int converged = 0, iterations = 0;
//		int i = 0, j = 0;
		double maxSoln, maxDiff, accuracy = 1e-08;
		double sum = 0.0;
		
		int tangible = (int)myMatrix.getTangible();
		
		double[] x = new double[tangible];
		double[] result = new double[tangible];
		

		System.out.println("\nCalculating steady state solution...\n");

		for(int i=0; i<tangible; i++) {
			x[i] = 1.0/tangible;
		    result[i] = x[i];
		}

		System.out.println("         Iteration |        Accuracy |");
		System.out.println("-------------------+-----------------+");

		while(converged==0) {
		
			for(int i=0; i<tangible; i++) {

				mC=myMatrix.getCol(i);
				
				sum = 0.0;
		      
				for(int j=0; j< mC.size(); j++) {
					mE = mC.getElement(j);
					if(mE.getOffset()!=i){
//						System.out.println("On iterations=" + iterations + "  multiplying matrix " + mE.getValue() + " with result " + result[(int)mE.getOffset()]);
						sum += mE.getValue()*result[(int)mE.getOffset()];
					}
				}
		      
				//assume our diagonals are all -1, so no need to do this
				//result[i] = sum/diagonal[i];
				result[i]=sum;
		    }


			if((iterations != 0) && (iterations%5==0)) {
				maxSoln = 0; 
				maxDiff = 0;

				for(int i=0; i<tangible; i++) {
					if(result[i] > maxSoln)
						maxSoln = result[i];
					if(Math.abs(result[i]-x[i]) > maxDiff)
						maxDiff = Math.abs(result[i]-x[i]);
				}
		        
				System.out.println("                 "+iterations+" |             " + maxDiff/maxSoln + " |");

				if(maxDiff/maxSoln < accuracy)
					converged = 1;
	    }

		    for(int i=0; i<tangible; i++) {
		      x[i] = result[i];
		    }

		    iterations++;
		}

		System.out.println("-------------------+-----------------+\n");

		sum = 0;

		for(int i=0; i<tangible; i++) {
		  sum += result[i];
		}


		for(int i=0; i<tangible; i++) {
		  result[i] /= sum;
		}
	
		System.out.println("Finished calculating steady state solution in " + (iterations) + " iterations...");

		/*
		for(int i=0; i<tangible; i++) {
			System.out.println("["+i+"]  " + result[i]);
		}
		System.out.println("\n");
		*/
			
		myMatrix.unpify();

		
		double targetStatePiSum = 0;
		
		for(int i=0;i<tangible;i++){
			if(sources.get(i)){
//				System.out.println("Found a source State!!!");
				targetStatePiSum += result[i];
			}
			else{
				result[i]=0;
			}
		}

		for(int i=0;i<tangible;i++)
			result[i] /= targetStatePiSum;

		
		return result;
	
	}
	
	
	
}
