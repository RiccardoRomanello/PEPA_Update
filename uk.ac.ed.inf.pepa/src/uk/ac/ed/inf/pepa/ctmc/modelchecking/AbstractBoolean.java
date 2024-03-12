/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

public enum AbstractBoolean {
	NOT_SET {
		public String toString() {
			return "Error";
		}
	},
	
	TRUE {
		public String toString() {
			return "True";
		}
	},
	
	FALSE {
		public String toString() {
			return "False";
		}
	},
	
	MAYBE {
		public String toString() {
			return "Maybe";
		}
	};
	
	public static AbstractBoolean and(AbstractBoolean v1, AbstractBoolean v2) {
		if (v1 == AbstractBoolean.TRUE && v2 == AbstractBoolean.TRUE) {
			return AbstractBoolean.TRUE;
		} else if (v1 == AbstractBoolean.NOT_SET || v2 == AbstractBoolean.NOT_SET) {
			return AbstractBoolean.NOT_SET;
		} else if (v1 != AbstractBoolean.MAYBE && v2 != AbstractBoolean.MAYBE) {
			return AbstractBoolean.FALSE;
		} else {
			return AbstractBoolean.MAYBE;
		}
	}
	
	public static AbstractBoolean or(AbstractBoolean v1, AbstractBoolean v2) {
		if (v1 == AbstractBoolean.FALSE && v2 == AbstractBoolean.FALSE) {
			return AbstractBoolean.FALSE;
		} else if (v1 == AbstractBoolean.NOT_SET || v2 == AbstractBoolean.NOT_SET) {
			return AbstractBoolean.NOT_SET;
		} else if (v1 != AbstractBoolean.MAYBE && v2 != AbstractBoolean.MAYBE) {
			return AbstractBoolean.TRUE;
		} else {
			return AbstractBoolean.MAYBE;
		}
	}
	
	public static AbstractBoolean not(AbstractBoolean v) {
		if (v == AbstractBoolean.FALSE) {
			return AbstractBoolean.TRUE;
		} else if (v == AbstractBoolean.TRUE) {
			return AbstractBoolean.FALSE;
		} else if (v == AbstractBoolean.NOT_SET) {
			return AbstractBoolean.NOT_SET;
		} else {
			return AbstractBoolean.MAYBE;
		}
	}
	
	public static AbstractBoolean implies(AbstractBoolean v1, AbstractBoolean v2) {
		return or(not(v1),v2);
	}
	
}
