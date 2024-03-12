/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
/*
 * Created on 08-Aug-2006
 *
 */
package uk.ac.ed.inf.pepa.model.internal;

import uk.ac.ed.inf.pepa.model.ActionSet;
import uk.ac.ed.inf.pepa.model.Cooperation;
import uk.ac.ed.inf.pepa.model.ProcessWithSet;
import uk.ac.ed.inf.pepa.model.Visitor;

/**
 * Implementation of <code>Cooperation</code>
 * 
 * @author mtribast
 * @see Cooperation
 */
public class CooperationImpl extends BinaryOperatorImpl implements Cooperation {

    private ActionSet actionSet;

    public CooperationImpl() {
        actionSet = null;
    }

    /**
     * @see ProcessWithSet
     */
    public void setActionSet(ActionSet actionSet) {
        if (actionSet == null)
            throw new NullPointerException("Cannot accept null action set");
        this.actionSet = actionSet;
    }

    /**
     * @see ProcessWithSet
     */
    public ActionSet getActionSet() {
        return this.actionSet;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Cooperation) {
            Cooperation coop = (Cooperation) o;
            return super.equals(coop)
                    && this.actionSet.equals(coop.getActionSet());
        } else
            return false;

    }

    public int hashCode() {

        return super.hashCode() + actionSet.hashCode();

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Process#accept(uk.ac.ed.inf.pepa.Visitor)
     */
    public void accept(Visitor v) {
        v.visitCooperation(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.ac.ed.inf.pepa.Process#prettyPrint()
     */
    public String prettyPrint() {
        String set;
        if (actionSet.size() == 0) {
            set = " || ";

        } else {
            set = " " + actionSet.prettyPrint() + " ";
        }
        return "(" + this.getLeftHandSide().prettyPrint() + set.toString()
                + this.getRightHandSide().prettyPrint() + ")";
    }

    /**
     * Provides a flattened view of the cooperation represented by this
     * aggregation.
     * <p>
     * Example (P ||P) || P ... getFlatRepresentation(3) ... [P, P, P]
     * 
     * @param copies
     *            length of the array
     * @return the array of cooperating processes. The lenght of this array is
     *         getCopies()
     * @throws ClassCastException
     *             when the length of this cooperation is less than
     *             <code>copies</code>
     *  
     */
//    public Process[] getFlatRepresentation(int copies) {
//        List<Process> processes = new ArrayList<Process>(copies);
//    	flat(this, processes, copies);
//    	Process[] result =  processes.toArray(new Process[copies]);
//    	return result;
//    }
//    
//    private void flat(Cooperation coop, List<Process> processes, int maxCopies) {
//    	if (maxCopies == processes.size())
//    		return;
//    	if (coop.getLeftHandSide() instanceof Cooperation) {
//    		flat((Cooperation) coop.getLeftHandSide(), processes, maxCopies);
//    	} else {
//    		processes.add(coop.getLeftHandSide());
//    	}
//    	if (coop.getRightHandSide() instanceof Cooperation) {
//    		flat((Cooperation) coop.getRightHandSide(), processes, maxCopies);
//    	} else {
//    		processes.add(coop.getRightHandSide());
//    	}
//    }

}