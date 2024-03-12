
//Title:        TimedTransition.java
//Version:      1.73
//Author:       Nick Dingle <njd200@doc.ic.ac.uk>
//Description:  Class definition for Timed Transitions

package uk.ac.ed.inf.pepa.jhydra.petrinet;

import uk.ac.ed.inf.pepa.jhydra.petrinet.*;
import java.awt.*;


public class TimedTransition extends Transition {

//  private double rate;
	private String rate;
	
  //  constructor
  public TimedTransition(int x, int y, PetriNet pn) {
    super(x, y, pn);
//    rate = new String("1.0");
  }

  // constructor for JHydra (don't care about location)
  //public TimedTransition(String n, double r, PetriNet pn, String c) {
  public TimedTransition(String n, String r, PetriNet pn, String c) {

	  super(n, pn, c);
	    rate = r;
	    System.out.println("Created new timed transition " + name + " with rate " + r + "...");
  }	
  
  //  function to check if this TimedTransition is enabled.  We must first
  //  check to see if any ImmediateTransitions are enabled under this marking
  //  as they have priority
  public boolean isEnabled() {
    for (int count=0; count < myPN.getNumberOfImmTransitions(); count++) {
      ImmediateTransition iT = myPN.getImmediateTransition(count);
      if (iT != null && iT.isEnabled())
        return false;
    }
    //no ImmediateTransitions are enabled so check if this TimedTransition is
//    return checkInputArcs();
   
    
    //we have: a string which describes the enabling condition which we can evaluate
    //         access to the current marking
    return myPN.conditionHolds(enablingCondition, myPN.getCurrentMarking());
  }

  //  function which draws this TimedTransition
  public void draw(Graphics2D g2) {
    super.draw(g2);
    if (!myPN.isAnimating() || !isEnabled()) {
      g2.setColor(Color.white);
      g2.fillRect(centreX - (width/2)+1, centreY - (height/2)+1,
                  width-2, height-2);
    }
  }

  //  accessor function which gets this TimedTransition's rate
  public String getRate() { return rate; }

  //  accessor function which sets this TimedTransition's rate 
  public void setRate(String r) { rate = r; }

}
