/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import java.util.HashMap;

import uk.ac.ed.inf.pepa.ctmc.abstraction.SequentialAbstraction;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.AbstractBoolean;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAbstractStateProperty;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAndNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLAtomicNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLBooleanNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLEventuallyNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLGloballyNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLImpliesNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLLongRunNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLNextNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLNotNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLOrNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLPathPlaceHolder;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLPathPropertyNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLStatePlaceHolder;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLSteadyStateNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.CSLUntilNode;
import uk.ac.ed.inf.pepa.ctmc.modelchecking.ModelCheckingException;

public class CSLCompositionalModelChecker implements ICSLVisitor {

	private CSLPropertyManager propertyManager;
	private SequentialAbstraction[] abstraction;
	
	private HashMap<CSLAbstractStateProperty, CompositionalPropertyList> properties;
	
	public CSLCompositionalModelChecker(CSLPropertyManager propertyManager, SequentialAbstraction[] abstraction) {
		this.propertyManager = propertyManager;
		this.abstraction = abstraction;
	}
	
	public CompositionalPropertyList getProperty(CSLAbstractStateProperty property) throws ModelCheckingException {
		init();
		property.accept(this);
		CompositionalPropertyList found = properties.get(property);
		unregisterAllExcept(found);
		return found;
	}
	
	private void init() {
		properties = new HashMap<CSLAbstractStateProperty, CompositionalPropertyList>(10);
	}
	
	private void unregisterAllExcept(CompositionalPropertyList found) {
		for (CompositionalPropertyList property : properties.values()) {
			if (property != found) {
				property.unregister();
			}
		}
	}
	
	public void visit(CSLAtomicNode node) throws ModelCheckingException {
		AtomicProperty atomicProperty = propertyManager.getPropertyBank().getAtomicProperty(node.getName());
		CompositionalProperty property = new CompositionalProperty(abstraction, atomicProperty);
		properties.put(node, new CompositionalPropertyList(property));
	}

	public void visit(CSLBooleanNode node) throws ModelCheckingException {
		CompositionalProperty property = new CompositionalProperty(abstraction, node.getValue());
		properties.put(node, new CompositionalPropertyList(property));
	}

	public void visit(CSLNotNode node) throws ModelCheckingException {
		CompositionalPropertyList property = properties.get(node.getProperty());
		properties.put(node, property.complement());
	}

	public void visit(CSLAndNode node) throws ModelCheckingException {
		CompositionalPropertyList property1 = properties.get(node.getProperty1());
		CompositionalPropertyList property2 = properties.get(node.getProperty2());
		properties.put(node, property1.intersection(property2));
	}

	public void visit(CSLOrNode node) throws ModelCheckingException {
		CompositionalPropertyList property1 = properties.get(node.getProperty1());
		CompositionalPropertyList property2 = properties.get(node.getProperty2());
		properties.put(node, property1.union(property2));
	}

	public void visit(CSLImpliesNode node) throws ModelCheckingException {
		CompositionalPropertyList property1 = properties.get(node.getProperty1());
		CompositionalPropertyList property2 = properties.get(node.getProperty2());
		CompositionalPropertyList tempProperty = property1.complement();
		properties.put(node, property2.union(tempProperty));
		tempProperty.unregister();
	}

	public void visit(CSLPathPropertyNode node) throws ModelCheckingException {
		throw new ModelCheckingException("A non-compositional node was reached.");
	}

	public void visit(CSLSteadyStateNode node) throws ModelCheckingException {
		AbstractBoolean value = propertyManager.getConstantProperty(node); 
		if (value != null) {
			CompositionalProperty property;
			if (value == AbstractBoolean.TRUE || value == AbstractBoolean.MAYBE) {
				property = new CompositionalProperty(abstraction, true);
			} else {
				property = new CompositionalProperty(abstraction, false);
			}
			properties.put(node, new CompositionalPropertyList(property));
		} else {
			throw new ModelCheckingException("A nested steady state node has not been evaluated.");
		}
	}

	public void visit(CSLUntilNode node) throws ModelCheckingException {
		throw new ModelCheckingException("A non-compositional node was reached.");
	}

	public void visit(CSLNextNode node) throws ModelCheckingException {
		throw new ModelCheckingException("A non-compositional node was reached.");
	}
	
	public void visit(CSLEventuallyNode node) throws ModelCheckingException {
		throw new ModelCheckingException("A non-compositional node was reached.");
	}
	
	public void visit(CSLGloballyNode node) throws ModelCheckingException {
		throw new ModelCheckingException("A non-compositional node was reached.");
	}

	public void visit(CSLPathPlaceHolder node) throws ModelCheckingException {
		throw new ModelCheckingException("A place-holder node was reached.");
	}

	public void visit(CSLStatePlaceHolder node) throws ModelCheckingException {
		throw new ModelCheckingException("A place-holder node was reached.");
	}

	public void visit(CSLLongRunNode node) throws ModelCheckingException {
		throw new ModelCheckingException("A place-holder node was reached.");
	}

}
