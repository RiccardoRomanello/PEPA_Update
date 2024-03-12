package uk.ac.ed.inf.pepa.jhydra.matrix;

public class MatrixElement {

	private int offset;
	private double value;
	
	public MatrixElement(int o, double v){
		offset = o;
		value = v;
	}
	
	public long getOffset(){ return offset; }
	
	public double getValue(){ return value; }
	
	public void setValue(double v) { value = v; }

	public boolean equals(Object m){
		if (!(m instanceof MatrixElement)) return false;

		if((this.offset==((MatrixElement) m).getOffset()) && (this.value==((MatrixElement) m).getValue()))
			return true;
		else
			return false;
	}
	
	public String toString(){ return "[" + offset + "," + value + "]"; }
	
	public void print() { System.out.print(this.toString()); }

}
