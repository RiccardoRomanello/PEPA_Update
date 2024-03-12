package uk.ac.ed.inf.pepa.cpt.config.control;

import uk.ac.ed.inf.pepa.cpt.config.Config;

public class Parser {
	
	public static Double parse(String type, String value){
		
		Integer i;
		Double d;
		Double fail = -1.0;
		boolean isInt = true;
		
		//catch non-numeric entries
		
		try {
			i = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			isInt = false;
			i = 0;
		}
		
		try {
			d = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return fail;
		}
		
		if(type.equals(Config.NATURAL) && isInt){

			if(i > 0){
				return d;
			} else {
				return fail;
			}
			
		} else if (type.equals(Config.INTEGER) && isInt){
			
			if(i > -1){
				return d;
			} else {
				return fail;
			}
			
		} else if (type.equals(Config.PERCENT)){
			
			if(d >= 0.0 && d <= 1.0){
				return d;
			} else {
				return fail;
			}
			
		} else if (type.equals(Config.DOUBLE)){
			
			if(d >= 0.0){
				return d;
			} else {
				return fail;
			}
			
		} else {
			return fail;
		}
		
	}
	
	public static String revert(String type, Double value){
		
		if(type.equals(Config.PERCENT)){
			return "" + value;
		} else if (type.equals(Config.INTEGER) || type.equals(Config.NATURAL)) {
			return "" + value.intValue();
		//TODO 
		//required for component?
		} else {
			return "" + value;
		}
	}

}
