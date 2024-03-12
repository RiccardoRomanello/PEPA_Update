package uk.ac.ed.inf.pepa.ctmc.modelchecking;

import java.util.ArrayList;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.internal.ICSLVisitor;

public class CSLLongRunNode extends CSLAbstractStateProperty {

	private CSLAbstractStateProperty property;
	private CSLAbstractProbability comparator;

	public CSLAbstractStateProperty getProperty() {
		return property;
	}
	
	public CSLAbstractProbability getComparator() {
		return comparator;
	}
	
	public CSLLongRunNode(CSLAbstractStateProperty property, CSLAbstractProbability comparator) {
		this.property = property;
		this.comparator = comparator;
	}
	
	public boolean isProbabilityTest() {
		return comparator.isProbabilityTest();
	}
	
	public String toString() {
		return "L" + comparator.toString() + " [ " + property.toString() + " ]";  
	}
	
	public boolean containsPlaceHolder() {
		return property.containsPlaceHolder();
	}
	
	public CSLAbstractStateProperty replace(CSLAbstractProperty object1, CSLAbstractProperty object2) {		
		if (this == object1 && object2 instanceof CSLAbstractStateProperty) {
			return (CSLAbstractStateProperty)object2;
		} else {
			property = property.replace(object1, object2);
			comparator = comparator.replace(object1, object2);
			return this;
		}
	}
	
	public CSLAbstractStateProperty copy() {
		return new CSLLongRunNode(property.copy(), comparator.copy());
	}
	
	public StringPosition[] getChildren() {
		int start1 = 1;
		StringPosition[] comparatorChildren = comparator.getChildren();
		int end1 = start1 + comparator.toString().length();
		int start2 = end1 + 3;
		int end2 = start2 + property.toString().length();
		StringPosition position2 = new StringPosition(start2, end2, property);
		
		StringPosition[] children = new StringPosition[1 + comparatorChildren.length]; 
		for (int i = 0; i < comparatorChildren.length; i++) {
			children[i] = comparatorChildren[i].addOffset(start1);
		}
		children[comparatorChildren.length] = position2;
		return children;
	}
	
	public boolean equals(Object o) {
		if (o instanceof CSLLongRunNode) {
			CSLLongRunNode node = (CSLLongRunNode)o;
			return property.equals(node.property) && comparator.equals(node.comparator);
		}
		return false;
	}
	
	public int hashCode() {
		return property.hashCode() + comparator.hashCode() + 25;
	}
	
	@Override
	public void accept(ICSLVisitor visitor) throws ModelCheckingException {
		property.accept(visitor);
		visitor.visit(this);
	}
	
	@Override
	protected void setCompositionality(boolean withinSteadyStateOperator) {
		isCompositional = false;
	}
	
	@Override
	public CSLAbstractStateProperty normalise() {
		CSLAbstractStateProperty normal = property.normalise();
		return new CSLLongRunNode(normal, comparator);
	}
	
	@Override
	public ArrayList<CSLAtomicNode> getAtomicProperties() {
		return property.getAtomicProperties();
	}

}
