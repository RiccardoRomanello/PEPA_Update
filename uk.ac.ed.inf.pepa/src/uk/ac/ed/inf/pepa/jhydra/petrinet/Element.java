
//Title:        Element.java
//Version:      1.73
//Author:       Nick Dingle <njd200@doc.ic.ac.uk>
//Description:  Abstract superclass for all consitiuent parts of
//              a PetriNet

package uk.ac.ed.inf.pepa.jhydra.petrinet;

import java.awt.*;


abstract public class Element {

  protected String name = null;   //a unique identifier (ie "p1", "t4") 
  protected String comment = "";  //only visible in the EditBox

  //  constructor
  public Element(){
  
  }
  
  public Element(String s) {
	  name = s;
  }

  //  function which configures the name of this Element correctly
  //  based on the value passed up from the sub-class.  As all Elements'
  //  names have the same format (ie "p1", "t4") the way in which this
  //  configuration is done is common to all Elements.
  protected void setName(char ch, int num) {
    Character prefix = new Character(ch);
    Integer number = new Integer(num);
    name = new String(prefix.toString().concat(number.toString()));
  }

  //  all Elements must be able to be drawn on-screen and so must
  //  define this function.
  abstract public void draw(Graphics2D g2);

  //  accessor function to get the Name of this Element
  public String getName() { return name; }

  //  accessor function to get the Comment of this Element
  public String getComment() { return comment; }

  //  accessor function to set the Name of this Element
  public void setName(String s) { name = s; }

  //  accessor function to set the Comment of this Element
  public void setComment(String s) { comment = s; }
} 