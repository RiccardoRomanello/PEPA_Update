
//Title:        Node.java
//Version:      1.73
//Author:       Nick Dingle <njd200@doc.ic.ac.uk>
//Description:  Class definition for Nodes from which
//              Places and Transitions inherit

package uk.ac.ed.inf.pepa.jhydra.petrinet;

//import jhydra.*;
import uk.ac.ed.inf.pepa.jhydra.petrinet.*;
import java.awt.*;
import java.io.*;


abstract public class Node extends Element implements Serializable {

  protected PetriNet myPN;
  protected int centreX, centreY;
  protected int width, height;

  //constructor
  public Node(int m, int n, int w, int h, PetriNet pn) {
    centreX = m;
    centreY = n;
    width = w;
    height = h;
    myPN = pn;
    comment = null;
  }

  public Node(String s){
	  super(s);
  }
  
  public Node(String s, PetriNet pn){
	  super(s);
	  myPN = pn;
  } 
  
  //  function which checks if a click at (m,n) occurred over this Node
  public boolean contains (int m, int n) {
    int xmin, xmax, ymin, ymax;
    xmin = getX() - getWidth()/2;
    xmax = getX() + getWidth()/2;
    ymin = getY() - getHeight()/2;
    ymax = getY() + getHeight()/2;

    if (xmin >= m || xmax <= m || ymin >= n || ymax <= n)
      return false;
    else
      return true;
  }

  //  function which moves the Node to the new location (x,y)
  public void moveNode(int x, int y) {
    centreX = x;
    centreY = y;
    moveArcs();
  }

  //  function which moves the Arcs which connect to this place when
  //  it is moved
  private void moveArcs() {
    //for every Arc in the PetriNet . . .
    for (int count = 0; count < myPN.getNumberOfArcs(); count++) {
      Arc arc = myPN.getArc(count);
      //if the Arc starts with this Node . . .
      if (arc.getStart()==this)
        arc.setStart(this);
      //else if the Arc ends with this Node . . .
      else if(arc.getEnd()==this)
        arc.setEnd(this);
      //if neither, continue through the Arcs . . .
	
	  int x = arc.calcBoxX();
	  int y = arc.calcBoxY();
	  arc.setBoxX(x);
	  arc.setBoxY(y);
	  
    }
	
/*	for (int count4 = 0; count4 < myPN.getNumberOfArcs(); count4++) {
	  Arc a = myPN.getArc(count4);
	  //x = round(a.getBoxX(),gS);
	  //y = round(a.getBoxY(),gS);
	  //a.setBoxX(x-3);
	  //a.setBoxY(y-3);
	  
	  int x = a.calcBoxX();
	  int y = a.calcBoxY();
	  a.setBoxX(x);
	  a.setBoxY(y);
	  
    }
	*/
  }

  //  all Nodes must know how to draw themselves
  abstract public void draw(Graphics2D g2);

  //  accessor function to get the x-coord of this Node's location
  public int getX() { return centreX; }

  //  accessor function to get the y-coord of this Node's location
  public int getY() { return centreY; }

  //  accessor function to the get the width of this Node
  public int getWidth() { return width; }

  //  accessor function to the get the height of this Node  
  public int getHeight() { return height; }

}
