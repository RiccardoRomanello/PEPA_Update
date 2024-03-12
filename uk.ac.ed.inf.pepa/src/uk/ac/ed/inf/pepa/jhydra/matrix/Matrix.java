package uk.ac.ed.inf.pepa.jhydra.matrix;

import java.util.Vector;
import java.util.BitSet;


public class Matrix {

	private Vector<MatrixRow> rows;
	private Vector<MatrixRow> cols;
	private Vector<Double> rowSums;

	public Matrix(){
		rows = new Vector<MatrixRow>(0,1);
		cols = new Vector<MatrixRow>(0,1);
		rowSums = new Vector<Double>(0,1);
	}

	public void addRow(MatrixRow r, int rowNumber){ 
		Double rowSum = new Double(r.getRowSum());
		MatrixElement diag = new MatrixElement(rowNumber, -rowSum.doubleValue());
		r.add(diag);
		rows.add(r);
		rowSums.add(rowSum);
	}

	public void columnify(){
//		cols.setSize((int) getTangible());

		for(int i=0;i<getTangible();i++){
			MatrixRow newCol = new MatrixRow(i);
			cols.add(newCol);
		}

		MatrixRow mR;
		MatrixElement mE, newME;

		for(int i=0;i<getTangible();i++){
			mR = rows.get(i);
//			mR.print();

			for(int j=0;j<mR.size();j++){
				mE = mR.getElement(j);
				newME = new MatrixElement(i, mE.getValue());
//				System.out.println(mE);
//				if(cols.get(i)!=null){
//				System.out.println(mE);
				cols.get((int)mE.getOffset()).add(newME);
//				}
			}
		}
	}

	public void pify(){

		Double[] factor = new Double[rowSums.size()];
		rowSums.toArray(factor);

//		Double factor[] = (Double []) rowSums.toArray();

		MatrixRow col;
		MatrixElement mE;

		for(int i=0;i<getTangible();i++){
			col=cols.get(i);
			for(int j=0;j<col.size();j++){
				mE=col.getElement(j);
				mE.setValue(mE.getValue()/(factor[(int)mE.getOffset()].doubleValue()));
			}
		}
	}

	public void unpify(){

		Double[] factor = new Double[rowSums.size()];
		rowSums.toArray(factor);

//		Double factor[] = (Double []) rowSums.toArray();


		MatrixRow col;
		MatrixElement mE;

		for(int i=0;i<getTangible();i++){
			col=cols.get(i);
			for(int j=0;j<col.size();j++){
				mE=col.getElement(j);
				mE.setValue(mE.getValue()*(factor[(int)mE.getOffset()].doubleValue()));
			}
		}

	}

	// TODO add gauss-seidel as a method of matrix, not a stand-alone routine....

	public void transMultiply(double[] input, double[] output){
		MatrixRow _col;
		double sum;

		for (int n=0; n<cols.size(); n++) {
			_col = cols.get(n);
			sum = 0;
			for (int t=0; t<_col.size(); t++) {
				sum += _col.getElement(t).getValue() * input[(int)_col.getElement(t).getOffset()];
			}
			output[n] = sum;
		}
	}

	public MatrixRow getRow(int i){ return rows.get(i); }

	public MatrixRow getCol(int i){ return cols.get(i); }

	public long getTangible(){ return rows.size(); }


	public void print(){
		for(int i=0;i<rows.size();i++){
			MatrixRow mR = rows.get(i);
			System.out.print("[" + i + "]  ");
			mR.print();
			System.out.print("  sum=" + rowSums.get(i).toString());
			System.out.println("");
		}
	}

	public double getLargestNonZero(){

		double value = 0;
		MatrixRow _col;
		double sum;

		for (int n=0; n<cols.size(); n++) {
			_col = cols.get(n);

			sum = 0;
			for (int t=0; t<_col.size(); t++) {
				if(_col.getElement(t).getOffset()!= n){
					sum += _col.getElement(t).getValue();
				}	
			}
			if(sum>value)
				value = sum;
		}

		return value;
	}


	public double uniformiseMatrix(BitSet target_states, boolean doPassage){
		double _q = getLargestNonZero();
		_q *= 1.01;

		MatrixRow _row;
		MatrixElement mE;
		double rowSum;
		
		for(int i=0;i<rows.size();i++){

			if(i%1000==0)
				System.out.println("Uniformising matrix row " + i + "...");

			
			_row = rows.get(i);

			rowSum = _row.getRowSum();

						
			for(int j=0; j<_row.size();j++){
				mE = _row.getElement(j);
				
				if(doPassage) {
					if(target_states.get(i)){
						mE.setValue(0);
					}else if( !target_states.get(i) && mE.getOffset() != i) {
						mE.setValue(mE.getValue()/_q);
					} else {
						//By now we should only be adding altering stuff that's a diagonal entry for a non-target state...
						mE.setValue(1.0 - rowSum/_q);
						
					}
				}
				else{
					if (mE.getOffset() != i) {
						mE.setValue(mE.getValue()/_q);
					}else if (mE.getOffset()==i){
						mE.setValue(1.0 - rowSum/_q);						
					}
				}

			}
		}
		
//		this.print();
		
		cols = new Vector<MatrixRow>(0,1);
		cols.setSize(0);

		this.columnify();

		return _q;

	}

}