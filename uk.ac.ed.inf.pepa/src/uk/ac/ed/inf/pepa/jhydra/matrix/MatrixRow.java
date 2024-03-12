package uk.ac.ed.inf.pepa.jhydra.matrix;

import java.util.Vector;


public class MatrixRow {

	private Vector<MatrixElement> row;
	private int row_no;
		
	public MatrixRow(int number){
		row = new Vector<MatrixElement>(0,1);
		row_no = number;
	}
	
	public void add(MatrixElement mE) { row.add(mE); }
	
	public double getRowSum() {
		double total = 0.0;
		
		MatrixElement mE;
		
		//ignore diagonals in calculating row sum
		for(int i=0;i<row.size();i++){
			mE = row.get(i);
			if(mE.getOffset() != row_no)
				total+= mE.getValue();
		}
		return total;
	}
	
	public int size() { return row.size(); }
	
	public int getRowNumber() { return row_no; }
	
	public MatrixElement getElement(int i) { return row.get(i); }
	
	public void print() {
		for(int i=0;i<row.size();i++){
			MatrixElement mE = row.get(i);
			mE.print();
		}
	}
}
