
//Title:        ImmediateTransition.java
//Version:      1.73
//Author:       Nick Dingle <njd200@doc.ic.ac.uk>
//Description:  Class definition for Immediate Transitions

package uk.ac.ed.inf.pepa.jhydra.petrinet;

import uk.ac.ed.inf.pepa.jhydra.petrinet.*;
import java.awt.*;


public class ImmediateTransition extends Transition {

//  private double weight;
	private String weight;

  //  constructor
  public ImmediateTransition(int x, int y, PetriNet pn) {
    super(x, y, pn);
//    weight = new String("1.0");
  }

//  public ImmediateTransition(String n, double w, PetriNet pn, String c) {
  public ImmediateTransition(String n, String w, PetriNet pn, String c) {
	    super(n, pn, c);
	    weight = w;
	    System.out.println("Created new immediate transition " + name + " with weight " + w + "...");
}	
  
  //  function to draw this Element on screen
  public void draw(Graphics2D g2) {
    super.draw(g2);
  }

  //  function which assesses if this Transition is enabled under the
  //  current marking.  All we have to do is check the InputArcs of
  //  this Transition - unlike ImmediateTransitions where we must ensure
  //  that no TimedTransitions are enabled first.
  public boolean isEnabled() { 
//	  return checkInputArcs();
	  return myPN.conditionHolds(enablingCondition, myPN.getCurrentMarking());

  }

  //  accessor function to get the weight of this Transition
  public String getWeight() { return weight; }

  //  accessor function to set the weight of this Transition
  public void setWeight(String d) { weight = d; }

}