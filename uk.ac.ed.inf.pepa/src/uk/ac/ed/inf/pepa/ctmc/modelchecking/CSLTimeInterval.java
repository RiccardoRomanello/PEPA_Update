/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking;

public class CSLTimeInterval extends CSLAbstractProperty {

	private CSLDouble startTime;
	private CSLDouble endTime;
	private boolean isStartBounded;
	private boolean isEndBounded;
	
	public CSLTimeInterval(CSLDouble startTime, CSLDouble endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.isStartBounded = true;
		this.isEndBounded = true;
	}
	
	public CSLTimeInterval(CSLDouble time, boolean isStartBound) {
		if (isStartBound) {
			this.startTime = time;
			this.isStartBounded = true;
			this.isEndBounded = false;
		} else {
			this.endTime = time;
			this.isStartBounded = false;
			this.isEndBounded = true;
		}
	}
	
	public CSLTimeInterval() {
		this.isStartBounded = false;
		this.isEndBounded = false;
	}
	
	public boolean isStartBounded() {
		return isStartBounded;
	}
	
	public boolean isEndBounded() {
		return isEndBounded;
	}
	
	public CSLDouble getStartTime() {
		return startTime;
	}
	
	public CSLDouble getEndTime() {
		return endTime;
	}
	
	public boolean containsPlaceHolder() {
		return false;
	}
	
	public String toString() {
		if (isStartBounded && isEndBounded) {
			return "[" + startTime.toString() + "," + endTime.toString() + "]";
		} else if (isStartBounded) {
			return ">=" + startTime.toString();
		} else if (isEndBounded) {
			return "<=" + endTime.toString();
		} else {
			return "";
		}
	}
	
	public CSLTimeInterval copy() {
		CSLTimeInterval newInterval = new CSLTimeInterval(startTime, endTime);
		newInterval.isEndBounded = isEndBounded;
		newInterval.isStartBounded = isStartBounded;
		return newInterval;
	}
	
	public StringPosition[] getChildren() {
		if (isStartBounded && isEndBounded) {
			int start1 = 1;
			int end1 = start1 + startTime.toString().length();
			StringPosition position1 = new StringPosition(start1, end1, startTime);
			int start2 = end1 + 1;
			int end2 = start2 + endTime.toString().length();
			StringPosition position2 = new StringPosition(start2, end2, endTime);
			return new StringPosition[] {position1, position2};
		} else if (isStartBounded) {
			int start = 2;
			int end = start + startTime.toString().length();
			StringPosition position = new StringPosition(start, end, startTime);
			return new StringPosition[] {position};
		} else if (isEndBounded) {
			int start = 2;
			int end = start + endTime.toString().length();
			StringPosition position = new StringPosition(start, end, endTime);
			return new StringPosition[] {position};
		} else {
			return new StringPosition[] { };
		}
	}
	
	public CSLTimeInterval replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {
		if (object1 == this && object2 instanceof CSLTimeInterval) {
			return (CSLTimeInterval)object2;
		} else {
			return this;
		}
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLTimeInterval) {
			CSLTimeInterval node = (CSLTimeInterval)o;
			boolean sameBounds = isStartBounded == node.isStartBounded && isEndBounded == node.isEndBounded;
			if (isStartBounded) {
				sameBounds = sameBounds && startTime.equals(node.startTime);
			} 
			if (isEndBounded) {
				sameBounds = sameBounds && endTime.equals(node.endTime);
			}
			return sameBounds;
		}
		return false;
	}
	
	public int hashCode() {
		return (isStartBounded ? 1 + startTime.hashCode() : 0) + (isEndBounded ? 1 + endTime.hashCode() : 0) + 9;
	}
	
}
