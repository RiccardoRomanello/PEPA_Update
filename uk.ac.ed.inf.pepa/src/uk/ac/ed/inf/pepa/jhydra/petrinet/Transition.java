
//Title:        Transition.java
//Version:      1.73
//Author:       Nick Dingle <njd200@doc.ic.ac.uk>
//Description:  Superclass definition for Transitions

package uk.ac.ed.inf.pepa.jhydra.petrinet;

import uk.ac.ed.inf.pepa.jhydra.petrinet.*;
import java.awt.*;


abstract public class Transition extends Node {

  private static int number = 1;
  private final static char character = 't';
  protected double priority = 1;
  protected String enablingCondition;

  public Transition(int x, int y, PetriNet pn) {
    super(x, y, 27, 13, pn);
    setName(character, number++);
  }
  
  public Transition(String n, PetriNet pn, String c){
	  super(n, pn);
	  enablingCondition = c;
  }

  //  function which performs the firing of Timed and Immmediate Transitions
  //  first destroy correct number of tokens on all input Places
  //  then create correct number of tokens on all output Places
  public boolean fire() {
//    System.out.println("Firing transition " + name + "...");
    if (isEnabled()) {
      //for every Arc in the PetriNet . . .
      for (int count=0; count < myPN.getNumberOfArcs(); count++) {
        Arc arc = myPN.getArc(count);
        //if the Arc ends at this Transition . . .
//        if (arc != null && arc.getEnd().getX()==centreX && arc.getEnd().getY()==centreY) {
        if (arc != null && arc.getEnd()==this) {
          //get the Place where it starts and destroy the tokens
//            Place p = myPN.getPlace(arc.getStart().getX(), arc.getStart().getY());
          Place p = (Place) arc.getStart();
           if (p != null)
            p.alterMyTokens(-arc.getWeight().intValue());
        }
      }

      //for every Arc in the PetriNet . . .
      for (int count2=0; count2 < myPN.getNumberOfArcs(); count2++) {
        Arc arc2 = myPN.getArc(count2);
        //if the Arc starts at this Transition . . .
//        if (arc2 != null && arc2.getStart().getX()==getX() && arc2.getStart().getY()==getY()) {
        if (arc2 != null && arc2.getStart()==this) {
          //get the Place where it ends and create the token
//          Place p2 = myPN.getPlace(arc2.getEnd().getX(), arc2.getEnd().getY());
          Place p2 = (Place) arc2.getEnd();

        	if (p2 != null)
            p2.alterMyTokens(arc2.getWeight().intValue());
        }
      }
      
      if(myPN.getTrace() != null){
    	  	myPN.addToTrace(name);
      		return true;
      }
      
    }
    return false;
  }

  //  function which undoes the firing of this transition by doing the exact
  //  opposite of fire()
  public boolean unFire() {
    for (int count=0; count < myPN.getNumberOfArcs(); count++) {
      Arc arc = myPN.getArc(count);
      if (arc != null && arc.getEnd().getX()==centreX && arc.getEnd().getY()==centreY) {
        Place p = myPN.getPlace(arc.getStart().getX(), arc.getStart().getY());
         if (p != null)
          p.alterMyTokens(arc.getWeight().intValue());
      }
    }

    for (int count2=0; count2 < myPN.getNumberOfArcs(); count2++) {
      Arc arc2 = myPN.getArc(count2);
      if (arc2 != null && arc2.getStart().getX()==getX() && arc2.getStart().getY()==getY()) {
        Place p2 = myPN.getPlace(arc2.getEnd().getX(), arc2.getEnd().getY());
        if (p2 != null)
          p2.alterMyTokens(-arc2.getWeight().intValue());
      }
    }
    return true;
  }

  //  all Transitions must be able to check if they are enabled
  abstract public boolean isEnabled();

  //  function which checks if the number of tokens on the input Places are
  //  above the weight values of the Arcs which connect them to this Transition.
  //  If they are we return true.  Used when evaluating isEnabled().
  protected boolean checkInputArcs() {
    for (int count=0; count < myPN.getNumberOfArcs(); count++) {
      Arc arc = myPN.getArc(count);
//      if (arc != null && arc.getEnd().getX()==centreX && arc.getEnd().getY()==centreY) {
      if (arc != null && arc.getEnd()==this) {
//        Place p = myPN.getPlace(arc.getStart().getX(), arc.getStart().getY());
          Place p = (Place) arc.getStart();
        if ( p != null && p.getNOT() < arc.getWeight().intValue()) {
          return false;
        }
      }
    }
    return true;  
  }

  //  function which performs the rotation of this Transition by 90degrees
  public void rotate() {
    System.out.println("Rotating transition");
    int temp = width;
    width = height;
    height = temp;
  }

  //  function to draw a black Transition (over which is overlayed a white
  //  box if it is immediate) and the highlight it if it is enabled & we
  //  are animating
  public void draw(Graphics2D g2) {
      g2.setColor(Color.black);
      g2.fillRect(centreX - (width/2), centreY - (height/2),
                  width, height);
      g2.drawString(name, centreX-15, centreY-15);
      if (myPN.isAnimating() && isEnabled()) {
        g2.setColor(Color.red);
        g2.fillRect(centreX - (width/2)+1, centreY - (height/2)+1,
                    width-2, height-2);
      }
  }

  //  accessor function to get the sequence number (x in "tx") of this Transition
  public int getNumber() { return number; }

  //  accessor function to set the sequence number (x in "tx") of this
  //  Transition. Used when recreating from XML
  public void setNumber(int n) { number = n; }

}