
//Title:        Arc.java
//Version:      1.73
//Author:       Nick Dingle <njd200@doc.ic.ac.uk>
//Description:  Class definition for Arcs

package uk.ac.ed.inf.pepa.jhydra.petrinet;

import uk.ac.ed.inf.pepa.jhydra.petrinet.*;
import java.lang.*;
import java.awt.*;
import java.io.*;


public class Arc extends Element implements Serializable {

  private Node start, end;
  private Integer weight;
  private int boxX, boxY;
  private static int number = 1;
  private final static char character = 'a';

  //  constructor
  public Arc(Node s, Node e) {
    start = s;
    end = e;
    comment = null;
    weight = new Integer(1);
    setName(character, number++);
    boxX = (( start.getX() + end.getX() ) / 2 ) - 3;
    boxY = (( start.getY() + end.getY() ) / 2 ) - 3;
    System.out.println("Created a new arc between " + s.getName() + " and " + e.getName() + " with weight " + weight.toString());
  }

  public Arc(Node s, Node e, Integer i) {
	    start = s;
	    end = e;
	    comment = null;
	    weight = i;
	    setName(character, number++);
	    boxX = (( start.getX() + end.getX() ) / 2 ) - 3;
	    boxY = (( start.getY() + end.getY() ) / 2 ) - 3;
	    System.out.println("Created a new arc between " + s.getName() + " and " + e.getName() + " with weight " + weight.toString());
	  }
  
  //  function to assess if a click which is registered at (m,n)
  //  occurs within the handle of this arc
  public boolean boxContains(int m, int n) {
    int xmin, xmax, ymin, ymax;
    xmin = boxX;
    xmax = boxX+7;
    ymin = boxY;
    ymax = boxY+7;

    if (xmin >= m || xmax <= m || ymin >= n || ymax <= n)
      return false;
    else
      return true;
  }

  //  function which draws this arc on-screen
  public void draw(Graphics2D g2) {
    g2.setColor(Color.black);
//    g2.drawLine(startX, startY, endX, endY);
//    g2.drawLine(start.getX(), start.getY(), end.getX(), end.getY());
    g2.drawLine(start.getX(), start.getY(), boxX+3, boxY+3);
    g2.drawLine(boxX+3, boxY+3, end.getX(), end.getY());
    g2.fillRect(boxX, boxY, 7, 7);

//  draw arrows
	double xLength = end.getX() - (boxX+3);
	double yLength = end.getY() - (boxY+3);

	double theta = Math.atan2(yLength, xLength);
	theta = Math.toDegrees(theta);

	double angleMinusTheta = 45.0 - theta;

	//System.out.println("Theta is: " + theta);

	double alpha = 180.0 - 90.0 - 45.0 - theta;

	//System.out.println("Alpha is: " + alpha);

	alpha = Math.toRadians(alpha);
	theta = Math.toRadians(theta);
	angleMinusTheta = Math.toRadians(angleMinusTheta);

	double tipX = end.getX() - ((17.0 * xLength) / Math.sqrt((yLength * yLength) + (xLength * xLength)));
	double tipY = end.getY() - ((17.0 * yLength) / Math.sqrt((yLength * yLength) + (xLength * xLength)));
	
	double endX1 = tipX - (7.0 * Math.sin(alpha));
	double endY1 = tipY - (7.0 * Math.cos(alpha));

	double endX2 = tipX - (7.0 * Math.cos(angleMinusTheta));
	double endY2 = tipY + (7.0 * Math.sin(angleMinusTheta));

	g2.drawLine( (int) tipX, (int) tipY, (int) endX1, (int) endY1);
	g2.drawLine( (int) tipX, (int) tipY, (int) endX2, (int) endY2);


    if (weight.intValue() > 1) {
      g2.drawString(weight.toString(), boxX-15, boxY+20);
    }
  }

  //  accessor function to set the start Node of this arc
  public void setStart(Node n) { start = n; }

  //  accessor function to set the end Node of this arc
  public void setEnd(Node n) { end = n; }

  //  accessor function to set the x-dimension of the on-screen
  //  handle of this arc
  public void setBoxX(int x) { boxX = x; }

  //  accessor function to set the y-dimension of the on-screen
  //  handle of this arc
  public void setBoxY(int y) { boxY = y; }

  //  accessor function to set the weight of this arc
  public void setWeight(Integer i) { weight = i; }

  //  accessor function to reset the sequence number
  public void setNumber(int i) { number = i; }

  //  accessor function to get the start Node of this arc
  public Node getStart() { return start; }

  //  accessor function to get the end Node of this arc
  public Node getEnd() { return end; }

  //  accessor function to get the x-dimension of the on-screen
  //  handle of this arc
  public int getBoxX() { return boxX; }

  //  accessor function to get the y-dimension of the on-screen
  //  handle of this arc
  public int getBoxY() { return boxY; }

  //  accessor function to get the x-dimension of the on-screen
  //  arrow of this arc
  private int getArrowX() { return ( boxX + end.getX() ) / 2 ; }

  //  accessor function to get the y-dimension of the on-screen
  //  arrow of this arc
  private int getArrowY() { return ( boxY + end.getY() ) / 2 ; }

  //  accessor function to get the weight of this arc
  public Integer getWeight() { return weight; }

  public int calcBoxX() { return (( start.getX() + end.getX() ) / 2 ) - 3; }
  
  public int calcBoxY() { return (( start.getY() + end.getY() ) / 2 ) - 3; }

}