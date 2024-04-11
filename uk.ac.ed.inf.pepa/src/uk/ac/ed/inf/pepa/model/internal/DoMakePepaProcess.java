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

import uk.ac.ed.inf.pepa.model.Action;
import uk.ac.ed.inf.pepa.model.ActionLevel;
import uk.ac.ed.inf.pepa.model.ActionSet;
import uk.ac.ed.inf.pepa.model.Activity;
import uk.ac.ed.inf.pepa.model.Aggregation;
import uk.ac.ed.inf.pepa.model.Choice;
import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Cooperation;
import uk.ac.ed.inf.pepa.model.FiniteRate;
import uk.ac.ed.inf.pepa.model.Hiding;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.model.NamedAction;
import uk.ac.ed.inf.pepa.model.NamedRate;
import uk.ac.ed.inf.pepa.model.PassiveRate;
import uk.ac.ed.inf.pepa.model.Prefix;
import uk.ac.ed.inf.pepa.model.Process;
import uk.ac.ed.inf.pepa.model.Rate;
import uk.ac.ed.inf.pepa.model.SilentAction;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

/**
 * This class represents a factory for creating PEPA processes.
 * <p>
 * Example on how to use this API for creating a PEPA model are available in the
 * <code>uk.ac.ed.inf.pepa.test.Test</code> class.
 * 
 * @author mtribast
 *  
 */
public class DoMakePepaProcess {

    private static final DoMakePepaProcess INSTANCE = new DoMakePepaProcess();

    /**
     * @return the factory
     *  
     */
    public static DoMakePepaProcess getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a passive rate.
     * 
     * @param weight
     *            the weight of the passive activity.
     * @return the passive rate
     */
    public PassiveRate createPassiveRate(double weight) {
        PassiveRateImpl rate = new PassiveRateImpl();
        rate.setWeight(weight);
        return rate;
    }

    /**
     * Creates a finite rate.
     * 
     * @param value
     *            the rate.
     * @return the finite rate representation
     */
    public FiniteRate createFiniteRate(double value) {
        FiniteRateImpl rate = new FiniteRateImpl();
        rate.setValue(value);
        return rate;
    }
    
    public NamedRate createNamedRate(String name, double value) {
    	if (name == null || name.equals(""))
    		throw new IllegalArgumentException();
    	NamedRateImpl rate = new NamedRateImpl();
    	rate.setName(name);
    	rate.setValue(value);
    	return rate;
    }

    /**
     * Creates a Constant.
     * 
     * @param name
     *            the name of the constant
     * @return the constant
     */
    public Constant createConstant(String name) {
        ConstantImpl constant = new ConstantImpl();
        constant.setName(name);
        return constant;
    }

    /**
     * Creates a new PEPA model.
     * 
     * @return the new model
     */
    public Model createModel(ModelNode modelNode) {
    	if (modelNode == null)
    		throw new NullPointerException();
        return new ModelImpl(modelNode);
    }

    /**
     * Creates an activity.
     * <p>
     * <b>Important. </b> Activity instances must not be reused. If two prefix
     * are to be assigned equal action type and rate, two instances of
     * <code>Activity</code> must be created.
     * <p>
     * Example: P1 = (a,r).P1; P2 = (a,r).P2;
     * <p>
     * 
     * <pre>
     * 
     *  Constant p1 = factory.createConstant(&quot;P1&quot;);
     *  Constant p2 = factory.createConstant(&quot;P2&quot;);
     *  Action a = factory.createAction(false);
     *  a.setName(&quot;a&quot;);
     *  FiniteRate r = factory.createFiniteRate(1.0);
     *  Activity act = factory.createActivity(a,r);
     *  p1.resolve(act, p1);
     *  
     *  p2.resolve(act, p2); &lt;b&gt;// WRONG&lt;/b&gt;
     *  
     *  p2.resolve(factory.createActivity(a,r),p2); // CORRECT
     *  
     *  
     * </pre>
     * 
     * @param action
     * @param rate
     * @return the new Activity
     */
    public Activity createActivity(Action action, Rate rate) {
        ActivityImpl activity = new ActivityImpl();
        activity.setAction(action);
        activity.setRate(rate);
        return activity;
    }
    
    /**
     * Convenience method for creating a named (typed) action.
     * 
     * @param name Action type
     * @param level Action level
     * @return the requested action
     */
    public NamedAction createNamedAction(String name, ActionLevel level) {
        NamedActionImpl action = new NamedActionImpl();
        action.setName(name);
        action.setLevel(level);

        return action;
    }

    /**
     * Convenience method for creating a named (typed) action.
     * 
     * @param name Action type
     * @return the requested action
     */
    public NamedAction createNamedAction(String name) {
        return createNamedAction(name, ActionLevel.UNDEFINED);
    }

    /**
     * Creates a silent (tau) action.
     * 
     * @return the requested action
     */
    public SilentAction createSilentAction(NamedAction hiddenAction) {
    	SilentActionImpl silentAction = new SilentActionImpl();
    	silentAction.setHiddenAction(hiddenAction);
        return silentAction;
    }

    /**
     * Creates a Prefix
     * 
     * @param activity
     * @param destination
     * @return
     */
    public Prefix createPrefix(Activity activity, Process destination) {
        PrefixImpl prefix = new PrefixImpl();
        prefix.setActivity(activity);
        prefix.setTargetProcess(destination);
        return prefix;
    }

    /**
     * Convenience method for creating an instance of Cooperation
     * 
     * @param lhs Left hand side
     * @param rhs Right hand side
     * @param actions Action set
     * @return
     */
    public Cooperation createCooperation(Process lhs, Process rhs,
            ActionSet actions) {
        CooperationImpl cooperation = new CooperationImpl();
        cooperation.setLeftHandSide(lhs);
        cooperation.setRightHandSide(rhs);
        cooperation.setActionSet(actions);
        return cooperation;
    }
    
    public Aggregation createAggregation() {
        return new AggregationImpl();
    }

    /**
     * Creates a Choice.
     * 
     * @param lhs
     * @param rhs
     * @return
     */
    public Choice createChoice(Process lhs, Process rhs) {
        ChoiceImpl choice = new ChoiceImpl();
        choice.setLeftHandSide(lhs);
        choice.setRightHandSide(rhs);
        return choice;
    }

    /**
     * Creates a Hiding
     * 
     * @param actionSet
     * @return
     */
    public Hiding createHiding(Process hiddenProcess, ActionSet actionSet) {
        HidingImpl hiding = new HidingImpl();
        hiding.setActionSet(actionSet);
        hiding.setHiddenProcess(hiddenProcess);
        return hiding;
    }

    public ActionSet createActionSet() {
        return new ActionSetImpl();
    }
}