package uk.ac.ed.inf.pepa.jhydra.petrinet;


public class Marking{

	private Integer tokens[];
	
	public Marking(Integer[] t){
		tokens = t;
	}

	public Integer[] getMarking(){ return tokens; }
	
	public int getLength(){ return tokens.length; }

	public Integer getElement(int i){ return tokens[i]; }
	
	
	public int hashCode(){
		String s = this.toString();
		return s.hashCode();
	}
	
	
	// Interesting! Need to overload equals with an Object parameter, not a Marking one, or else it won't work!!!
	public boolean equals(Object m){
		if (!(m instanceof Marking)) return false;

		
		for(int i=0;i<tokens.length;i++){
			if(this.tokens[i].intValue()!=((Marking) m).getElement(i).intValue())
				return false;
		}
		return true;
	}
	
	public String toString(){
		String s = "< ";
		
		for(int i=0;i<tokens.length;i++)
			s+= (tokens[i].toString() + " ");
		s+=">";
		
		return s;
	}
}
