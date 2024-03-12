
//Title:        Place.java
//Version:      1.73
//Author:       Nick Dingle <njd200@doc.ic.ac.uk>
//Description:  Class definition for Places

package uk.ac.ed.inf.pepa.jhydra.petrinet;

import uk.ac.ed.inf.pepa.jhydra.petrinet.*;
import java.lang.*;
import java.awt.*;


public class Place extends Node {

  private Integer numberOfTokens;
  private static int number = 1;
  private final static char character = 'p';

  //constructor
  public Place(int x, int y, PetriNet pn) {
    super(x, y, 27, 27, pn);
    numberOfTokens = new Integer(0);
    setName(character, number++);
  }

  public Place(String s){
	  super(s);
	  numberOfTokens = new Integer(0);	  
  }
  
  //  function which changes the number of tokens on this Place by n
  public void alterMyTokens(int n) {
    int x = numberOfTokens.intValue();
    if (x+n > -1)
      numberOfTokens = new Integer(x+n);
  }

  // function which sets the number of tokens to the value of n, overwriting the current value stored there.
  public void setTokens(int n){
	  numberOfTokens = new Integer(n);
	  
//	  System.out.println("Place " + name +" has marking set to " + numberOfTokens +"...");
  }
  
  //  function which draws this Place
  public void draw(Graphics2D g2) {
      g2.setColor(Color.black);
      g2.drawOval(centreX - (width/2), centreY - (height/2), width, height);
      g2.drawString(name, centreX-15, centreY-15);

      g2.setColor(Color.white);
      g2.fillOval(centreX - (width/2)+1, centreY - (height/2)+1,
                  width-2, height-2);

      if (numberOfTokens.intValue() > 0) {
        g2.setColor(Color.black);
        g2.drawString(numberOfTokens.toString(), centreX-4, centreY+4);
      }
  }

  //  accessor function to get the number of tokens on this Place
  //  expressed as a String
  public String stringNOT() { return numberOfTokens.toString(); }

  //  accessor function to get the number of tokens on this Place
  //  expressed as an int
  public int getNOT() { return numberOfTokens.intValue(); }

  //  accessor function to get the sequence number (x in "px") of this Place
  public int getNumber() { return number; }

  //  accessor function to set the sequence number (x in "px") of this Place
  //  Used when recreating from XML  
  public void setNumber(int n) { number = n; }

  
}
