/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.ctmc.modelchecking.internal;

import uk.ac.ed.inf.pepa.ctmc.modelchecking.*;

public interface ICSLVisitor {
	
	public void visit(CSLAtomicNode node) throws ModelCheckingException;
	public void visit(CSLBooleanNode node) throws ModelCheckingException;
	public void visit(CSLNotNode node) throws ModelCheckingException;
	public void visit(CSLAndNode node) throws ModelCheckingException;
	public void visit(CSLOrNode node) throws ModelCheckingException;
	public void visit(CSLImpliesNode node) throws ModelCheckingException;
	public void visit(CSLPathPropertyNode node) throws ModelCheckingException;
	public void visit(CSLSteadyStateNode node) throws ModelCheckingException;
	public void visit(CSLLongRunNode node) throws ModelCheckingException;
	public void visit(CSLUntilNode node) throws ModelCheckingException;
	public void visit(CSLNextNode node) throws ModelCheckingException;
	public void visit(CSLEventuallyNode node) throws ModelCheckingException;
	public void visit(CSLGloballyNode node) throws ModelCheckingException;
	public void visit(CSLPathPlaceHolder node) throws ModelCheckingException;
	public void visit(CSLStatePlaceHolder node) throws ModelCheckingException;
	
}
