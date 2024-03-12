package uk.ac.ed.inf.pepa.ode;


public class DifferentialAnalysisException extends Exception {
	
	public static final int UNDEFINED = 0;
	
	public static final int NOT_CONVERGED = 1;
	
	private int kind = UNDEFINED;
	
	public DifferentialAnalysisException(String string, int kind) {
		super(string);
		this.kind = kind;
	}
	public DifferentialAnalysisException(String string) {
		this(string, UNDEFINED);
	}
	
	public int getKind() {
		return kind;
	}
	


}
