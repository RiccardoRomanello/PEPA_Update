
//Title:        PetriNet.java
//Version:      1.73
//Author:       Nick Dingle <njd200@doc.ic.ac.uk>
//Description:  Class definition for PetriNets

//openXML() method based on the SAXTagCount example
//supplied with JAXP 1.1

package uk.ac.ed.inf.pepa.jhydra.petrinet;

import uk.ac.ed.inf.pepa.jhydra.evaluator.*;
import java.util.*;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;


public class PetriNet implements Serializable {

	//  these 4 vectors hold the Elements of the PetriNet
	private Vector<Place> places;
	private Vector<ImmediateTransition> immediateTransitions;
	private Vector<TimedTransition> timedTransitions;
	private Vector<Arc> arcs;

	private ExpressionEvaluator expressEval;
	private Hashtable<String,Double> constants;

	private Vector marking;
	private static Stack trace;



	//  constructor
	public PetriNet() {
//		System.out.println("PetriNet constructor called...");		
		places = new Vector<Place>(0, 1);
		immediateTransitions = new Vector<ImmediateTransition>(0, 1);
		timedTransitions = new Vector<TimedTransition>(0,1);
		arcs = new Vector<Arc>(0, 1);
		marking = new Vector(0, 1);

		expressEval = new ExpressionEvaluator();
	}

	//  function which actually opens a PetriNet by calling a parser
	public String openXML(String fileName) {
		/*
	  SAXParserFactory spf = SAXParserFactory.newInstance();

    //  This checks the XML is valid - but as yet have nothing to check
    //  the XML file against so need to do something about this
//    spf.setValidating(true);

    XMLReader xmlReader = null;

    try {

      // Create a JAXP SAXParser
      SAXParser saxParser = spf.newSAXParser();

      // Get the encapsulated SAX XMLReader
      xmlReader = saxParser.getXMLReader();

    } catch (Exception ex) {
      System.err.println(ex);
      return "Error opening " + fileName;
    }

    // Set the ContentHandler of the XMLReader
    xmlReader.setContentHandler(new XMLParser(this));

    // Set an ErrorHandler before parsing
//    xmlReader.setErrorHandler(new MyErrorHandler(System.err));

    try {

      // Tell the XMLReader to parse the XML document
      xmlReader.parse(convertToFileURL(fileName));

    } catch (SAXException se) {
      System.err.println("SAXException: " + se.getMessage());
      return "Error opening " + fileName;
    } catch (IOException ioe) {
      System.err.println("IOException: " + ioe);
      return "Error opening " + fileName;
    }

    saveMarking();
		 */
		return "Opened " + fileName;

	}

	//  function which saves a PetriNet in XML, which is accomplished by
	//  writing out ASCII characters to a File through a standard
	//  FileOutputStream
	public String saveAsXML(String fileName) {
		saveMarking();
		try {
			FileWriter out = new FileWriter(fileName);

			//write the header
			out.write("<?xml version=\"1.0\"?>");
			format(out, 1);
			out.write("<net id=\"n1\" type=\"null\">");
			format(out, 2);
			out.write("<name>");
			format(out, 3);
			out.write("<value>"+fileName+"</value>");
			format(out, 2);
			out.write("</name>");
			out.write(13);
			format(out, 2);

			//describe each place
			for(int count = 0; count < places.size(); count++) {
				Place p = (Place) places.get(count);
				out.write("<place id=\"" + p.getName() + "\">");
				format(out, 3);
				out.write("<graphics>");
				format(out, 4);
				out.write("<position x=\"" + p.getX() + "\" y=\"" + p.getY() + "\" />");
				format(out, 3);
				out.write("</graphics>");
				format(out, 3);
				out.write("<name>");
				format(out, 4);
				out.write("<value>" + p.getComment() + "</value>");
				format(out, 4);
				out.write("<graphics>");
				format(out, 5);
				out.write("<offset x=\"-15\" y=\"-15\" />");
				format(out, 4);
				out.write("</graphics>");
				format(out, 3);
				out.write("</name>");
				format(out, 3);
				out.write("<initialMarking>");
				format(out, 4);
				out.write("<value>" + p.getNOT() + "</value>");
				format(out, 4);
				out.write("<graphics>");
				format(out, 5);
				out.write("<offset x=\"0\" y=\"0\" />");
				format(out, 4);
				out.write("</graphics>");
				format(out, 3);
				out.write("</initialMarking>");
				format(out, 2);
				out.write("</place>");
				out.write(13);
				format(out, 2);
			}

			//describe each ImmediateTransition
			for(int count = 0; count < immediateTransitions.size(); count++) {
				ImmediateTransition iT = (ImmediateTransition)
				immediateTransitions.get(count);
				out.write("<transition id=\"" + iT.getName() + "\" type=\"immediate\" weight=\"" + iT.getWeight() + "\">");
				format(out, 3);
				out.write("<graphics>");
				format(out, 4);
				out.write("<position x=\"" + iT.getX() + "\" y=\"" + iT.getY() + "\" />");
				format(out, 3);
				out.write("</graphics>");
				format(out, 3);
				out.write("<name>");
				format(out, 4);
				out.write("<value>" + iT.getComment() + "</value>");
				format(out, 4);
				out.write("<graphics>");
				format(out, 5);
				out.write("<offset x=\"-15\" y=\"-15\" />");
				format(out, 4);
				out.write("</graphics>");
				format(out, 3);
				out.write("</name>");
				format(out, 2);
				out.write("</transition>");
				out.write(13);
				format(out, 2);
			}

			//describe each TimedTransition.  Note that it doesn't matter that this
			//is done separately to ImmediateTransitions as long as all Transitions
			//are defined before we start the Arcs.
			for(int count = 0; count < timedTransitions.size(); count++) {
				TimedTransition tT = (TimedTransition)
				timedTransitions.get(count);
				out.write("<transition id=\"" + tT.getName() + "\" type=\"timed\" distribution=\"exponential\" rate=\"" + tT.getRate()
						+ "\">");
				format(out, 3);
				out.write("<graphics>");
				format(out, 4);
				out.write("<position x=\"" + tT.getX() + "\" y=\"" + tT.getY() + "\" />");
				format(out, 3);
				out.write("</graphics>");
				format(out, 3);
				out.write("<name>");
				format(out, 4);
				out.write("<value>" + tT.getComment() + "</value>");
				format(out, 4);
				out.write("<graphics>");
				format(out, 5);
				out.write("<offset x=\"-15\" y=\"-15\" />");
				format(out, 4);
				out.write("</graphics>");
				format(out, 3);
				out.write("</name>");
				format(out, 2);
				out.write("</transition>");
				out.write(13);
				format(out, 2);
			}

			//describe the Arcs
			for(int count = 0; count < arcs.size(); count++) {
				Arc a = (Arc) arcs.get(count);
				Node s = a.getStart();
				Node e = a.getEnd();

				out.write("<arc id=\"" + a.getName() + "\" source=\"" + s.getName()
						+ "\" target=\"" + e.getName() + "\">");
				format(out, 3);
				out.write("<graphics>");
				format(out, 4);
				out.write("<position x=\"" + a.getBoxX() + "\" y=\"" + a.getBoxY() + "\"/>");
				format(out, 3);
				out.write("</graphics>");
				format(out, 3);
				out.write("<inscription>");
				format(out, 4);
				out.write("<value>" + a.getWeight() + "</value>");
				format(out, 4);
				out.write("<graphics>");
				format(out, 5);
				out.write("<offset x=\"-15\" y=\"+20\"/>");
				format(out, 4);
				out.write("</graphics>");
				format(out, 3);
				out.write("</inscription>");
				format(out, 2);
				out.write("</arc>");
				out.write(13);
				format(out, 2);
			}

			//close the net description and close the FileWriter stream
			format(out, 1);
			out.write("</net>");

			out.close();
		}
		catch (IOException e) {
			return "Error saving " + fileName;
		}

		return "Saved " + fileName;
	}

	//  function to add a Place.  The factor is passed in from above and is used
	//  when we are snapping to a Grid - if we are not it is "1"
	public void addPlace(int x, int y, int factor) {
		Place place = new Place(round(x, factor), round(y, factor), this);
		places.addElement(place);
	}

	public void addPlace(String s){
		Place place = new Place(s);
		places.add(place);
	}

	//  function to add an ImmediateTransition.  Comments as above
	public void addImmediateTransition(int x, int y, int factor) {
		ImmediateTransition iT = new ImmediateTransition(round(x, factor),
				round(y, factor), this);
		immediateTransitions.addElement(iT);
	}

	//  function to add an ImmediateTransition with a certain name and weight
//	public ImmediateTransition addImmediateTransition(String name, double weight, String condition) {
	public ImmediateTransition addImmediateTransition(String name, String weight, String condition) {
		ImmediateTransition iT = new ImmediateTransition(name, weight, this, condition);
		immediateTransitions.addElement(iT);
		return iT;
	}

	//  function to add a TimedTransition.  Comments as above
	public void addTimedTransition(int x, int y, int factor) {
		TimedTransition tT = new TimedTransition(round(x, factor),
				round(y, factor), this);
		timedTransitions.addElement(tT);
	}

	//  function to add a TimedTransition with a certain name and rate
//	public TimedTransition addTimedTransition(String name, double rate, String condition) {
	public TimedTransition addTimedTransition(String name, String rate, String condition) {
		TimedTransition tT = new TimedTransition(name, rate, this, condition);
		timedTransitions.addElement(tT);
		return tT;
	}


	//  function to add an Arc given a start and end Node.  Used when creating
	//  PetriNets from an XML file.
	public void addArc(Node s, Node e) {
		Arc a = new Arc(s, e);
		arcs.addElement(a);
	}

	//  function to add an Arc given a start and end Node with a given weight
	public void addArc(Node s, Node e, Integer i) {
		Arc a = new Arc(s, e, i);
		arcs.addElement(a);
	}

	//  function to add an Arc given an (x,y) location for the start and end.
	//  Used when editing an Arc thru the Medusa GUI.
	public void addArc(int sx, int sy, int ex, int ey, int which) {
		switch (which) {
		case 1: Place p = getPlace(sx, sy);   // start p, end t
		//             ImmediateTransition iT = getImmediateTransition(ex, ey);
		//             TimedTransition tT = getTimedTransition(ex, ey);
		Transition t = getTransition(ex, ey);  
		Arc arc = new Arc(p, t);
		arcs.addElement(arc);

		/*     if (iT == null) {
                Arc arc = new Arc(p, tT);
                arcs.addElement(arc);
              }
              else {
                Arc arc = new Arc(p, iT);
                arcs.addElement(arc);
              }  */
		break;

		case 2: //iT = getImmediateTransition(sx, sy); //starts t, end p
			//          tT = getTimedTransition(sx,sy);
			t = getTransition(sx, sy);
			p = getPlace(ex, ey);
			arc = new Arc(t, p);
			arcs.addElement(arc);

			/*    if (iT == null) {
                Arc arc = new Arc(tT, p);
                arcs.addElement(arc);
              }
              else {
                Arc arc = new Arc(iT, p);
                arcs.addElement(arc);
              }  */
			break;
		}
	}

	public boolean isEnabled(Marking currentMarking, Transition t){
		setMarking(currentMarking);

//		System.out.println("Checked against marking " + currentMarking.toString() + "...");

		return t.isEnabled();
	}

	public Marking fire(Marking currentMarking, Transition t){

		setMarking(currentMarking);

//		System.out.println("Previous marking: " + currentMarking + "...");

		t.fire();

//		System.out.println("Resulting marking: " + getMarking() + "...");

		return getMarking();
	}


	public void setConstants(Hashtable<String,Double> c) { constants = c; }


	public String conditionValue(String cond, Marking m){
		
//		System.out.println("Evaluating " + cond);

		expressEval.addSpace(cond);

//		System.out.println("Evaluating " + cond);
		
		//need to go through condition and replace all Place names and constants with their current values

		StringTokenizer s = new StringTokenizer(cond);
		String symbol = "";
	
		while(s.hasMoreTokens()){
			symbol = s.nextToken();
				
			if(constants.containsKey(symbol)){
//				System.out.print("Found constant " + symbol);
				Double value = constants.get(symbol);
//				System.out.println(" the value of which is " + value.toString());
				cond = cond.replace(" " + symbol + " ", " " + value.toString() + " ");
//				cond = cond.replace(symbol, value.toString());
			} else {
				int count;
				Place p = null;
				for (count = 0; count < places.size(); count++) {
					p = (Place) places.get(count);
					if (p.getName().equals(symbol)) {
						break;
					}
					p = null;
				}

				if(p!=null){
//      			System.out.print("Found place " + symbol);
					Double value = m.getElement(count).doubleValue();
//					System.out.println(" the value of which is " + value.toString());
					cond = cond.replace(" " + symbol + " ", " " + value.toString() + " ");
				}
			
			}				
		}


//		System.out.println("Evaluating " + cond);
		
		//then pass the resulting string to expressEval

		String evalResult = (expressEval.evaluate(cond)).toLowerCase();

		return evalResult;
	
	}

	
	
	public boolean conditionHolds(String cond, Marking m){

//		System.out.println("Evaluating " + cond);

		expressEval.addSpace(cond);

//		System.out.println("Evaluating " + cond);

		
		//need to go through condition and replace all Place names and constants with their current values

		StringTokenizer s = new StringTokenizer(cond);
		String symbol = "";
	
		while(s.hasMoreTokens()){
			symbol = s.nextToken();
			
			if(constants.containsKey(symbol)){
//				System.out.print("Found constant " + symbol);
				Double value = constants.get(symbol);
//				System.out.println(" the value of which is " + value.toString());
//				cond = cond.replace(" " + symbol + " ", " " + value.toString() + " ");
				cond = cond.replace(" " + symbol, " " + value.toString());
			} else {
				int count;
				Place p = null;
				for (count = 0; count < places.size(); count++) {
					p = (Place) places.get(count);
					if (p.getName().equals(symbol)) {
						break;
					}
					p = null;
				}

				if(p!=null){
//      			System.out.print("Found place " + symbol);
					Double value = m.getElement(count).doubleValue();
//					System.out.println(" the value of which is " + value.toString());
					cond = cond.replace(" " + symbol + " ", " " + value.toString() + " ");
				}
			
			}				
		}


//		System.out.println("Evaluating " + cond);
		
		//then pass the resulting string to expressEval

		String evalResult = (expressEval.evaluate(cond)).toLowerCase();

		if(evalResult.equals("true"))
			return true;
		else if(evalResult.equals("false"))	
			return false;
		else{
			System.out.println("ERROR: Condition evaluates to <" + evalResult + "> which is neither true nor false...");
			System.exit(-1);
			return false; //to keep the compiler happy, although it will never be reached - an exception would be far better!
		}

	}



	//  function which animates Transition firing.  Input is an (x,y) location
	//  which is checked against the points contained within every Transition
	//  and if the user has clicked over a transition we attempt to fire it.
	//  The firing routine checks if it is enabled.
	public boolean manualAnimate(int x, int y) {
		/*    TimedTransition tT = getTimedTransition(x,y);
    if (tT != null)
      return tT.fire();
    else {
      ImmediateTransition iT = getImmediateTransition(x,y);
      if (iT != null)
        return iT.fire();
    }
    return false;*/

		//  System.out.println("Using new definition!!!!!!");

		Transition t = getTransition(x,y);
		if (t != null)
			return t.fire();
		else
			return false;
	}

	//  function which steps backwards through a firing and undoes its effects.
	//  The stack has items pushed onto it as they are fired so the last
	//  Transition fired is always on top.
	public boolean undoFiring() {
//		System.out.println("Entering undoFiring()");
//		System.out.println("In undoFiring(), marking is " + marking);
//		System.out.println("In undoFiring(), trace is " + trace);
		if (trace != null && !trace.empty()) {
			String name = (String) trace.pop();
			Transition t = getTransition(name);
			if (t != null) {
//				System.out.println("Exiting undoFiring() where t != null");
				return t.unFire();
			}
			else {
//				System.out.println("Exiting undoFiring() where t == null");
				return false;
			}
		}
//		System.out.println("Exiting undoFiring() where trace != null && !trace.empty()");
		return false;
	}

	//  function to alter the number of tokens on a place at (x,y) by amount
	public void changeTokens(int x, int y, int amount) {
		Place p = getPlace(x,y);
		if (p != null)
			p.alterMyTokens(amount);
	}

	//  function to set the number of tokens on a place called p to amount
	public void setTokens(String pName, int amount) {
		Place p = getPlace(pName);
		if (p != null)
			p.setTokens(amount);
	}

	//  function which attempts to delete an Element at (x,y)
	public void deleteElement(int x, int y) {
		Place p = getPlace(x,y);
		ImmediateTransition iT = getImmediateTransition(x,y);
		TimedTransition tT = getTimedTransition(x,y);
		Arc a = getArc(x,y);
		boolean check = false;
		int m = -1;
		int n = -1;

		if (p != null) {
			m = p.getX();
			n = p.getY();
			check = places.remove(p);
		}
		else if (iT != null) {
			m = iT.getX();
			n = iT.getY();
			check = immediateTransitions.remove(iT);
		}
		else if (tT != null) {
			m = tT.getX();
			n = tT.getY();
			check = timedTransitions.remove(tT);
		}
		else if (a != null)
			arcs.remove(a);

		//if we have deleted a Place or Transition we also remove the Arcs which
		//connect to it
		if (check)
			deleteArcs(m,n);
	}

	//  function which saves the current marking of the PetriNet in a dedicated
	//  Vector
	public void saveMarking() {
//		System.out.println("Entering saveMarking()");
		marking = new Vector(0, 1);
		trace = new Stack();

		for (int count = 0; count < places.size(); count++) {
			Place place = (Place) places.get(count);
			Integer x = new Integer(place.getNOT());
			marking.addElement(x);
		}
//		System.out.println("Marking is " + marking);
//		System.out.println("Trace is " + trace);
//		System.out.println("Exiting saveMarking()");
	}

	//  function which restores the marking of the PetriNet stored in the Vector
	//  marking
	public void restoreMarking() {
//		System.out.println("Marking is " + marking);
		for (int count = 0; count < marking.size(); count++) {
			Place place = (Place) places.get(count);
			Integer x = (Integer) marking.get(count);
			place.alterMyTokens(x.intValue()-place.getNOT());
		}
//		marking = null;
		trace = null;
	}


	//  function which sets the marking of the PN to a specified marking
	public void setMarking(Marking m){
		Place p;
		Integer x;

		for (int count = 0; count < getNumberOfPlaces(); count++) {
			p = places.get(count);
			x = m.getElement(count);
			p.setTokens(x);
		}	  
	}

	//  function which creates a marking from the current population of places
	public Marking getMarking(){

		Integer[] m = new Integer[getNumberOfPlaces()];

		Place p;
		Integer x;

		for (int count = 0; count < getNumberOfPlaces(); count++) {
			p = places.get(count);
			x = new Integer(p.getNOT());
			m[count] = x;
		}	  

		Marking marking = new Marking(m);

		return marking;
	}


	//  function which snaps all the Elements onto a Grid of size gs
	public void moveElementsOntoGrid(int gS) {
		int x, y;
//		System.out.println("moveElementsOntoGrid() called");
		for (int count = 0; count < places.size(); count++) {
			Place p = getPlace(count);
			x = round(p.getX(),gS);
//			System.out.println(p.getX()+" rounded to the nearest 20 is " + x);
			y = round(p.getY(),gS);
//			System.out.println(p.getY()+" rounded to the nearest 20 is " + y);
			p.moveNode(x,y);
		}

		for (int count2 = 0; count2 < immediateTransitions.size(); count2++) {
			ImmediateTransition iT = getImmediateTransition(count2);
			x = round(iT.getX(),gS);
			y = round(iT.getY(),gS);
			iT.moveNode(x,y);
		}

		for (int count3 = 0; count3 < timedTransitions.size(); count3++) {
			TimedTransition tT = getTimedTransition(count3);
			x = round(tT.getX(),gS);
			y = round(tT.getY(),gS);
			tT.moveNode(x,y);
		}

		for (int count4 = 0; count4 < arcs.size(); count4++) {
			Arc a = getArc(count4);
			//x = round(a.getBoxX(),gS);
			//y = round(a.getBoxY(),gS);
			//a.setBoxX(x-3);
			//a.setBoxY(y-3);

			x = a.calcBoxX();
			y = a.calcBoxY();
			a.setBoxX(x);
			a.setBoxY(y);

		}
	}


	public Marking getCurrentMarking(){
		Marking currMarking;

		Integer[] currMarkingVector = new Integer[this.getNumberOfPlaces()];

		for(int i=0;i<this.getNumberOfPlaces();i++)
			currMarkingVector[i] = new Integer(this.getPlace(i).getNOT());

		currMarking = new Marking(currMarkingVector);

		return currMarking;
	}

	//  function which returns the Place, if any, at (x,y)
	public Place getPlace(int x, int y) {
		for (int count = 0; count < places.size(); count++) {
			Place place = (Place) places.get(count);
			if (place.contains(x, y))
				return place;
		}
		return null;
	}

	//  function which returns the nth Place in the place Vector
	public Place getPlace(int n) {
		return (Place) places.get(n);
	}

	//  function which returns the Place called "name"
	public Place getPlace(String name) {
		for (int count = 0; count < places.size(); count++) {
			Place p = (Place) places.get(count);
			if (p.getName().equals(name)) {
				return p;
			}
		}

		return null;
	}

	//  function which returns the Transition, either Immediate or Timed, with
	//  its centre at (x,y)
	public Transition getTransition(int x, int y) {
//		System.out.println("Entering getTransition(name)");
		for (int count = 0; count < immediateTransitions.size(); count++) {
			Transition T1 = (Transition) immediateTransitions.get(count);
			if (T1.contains(x,y)) {
//				System.out.println("Transition " + name + " is an immediate, returning");
				return T1;
			}
		}

		for ( int count2 = 0; count2 < timedTransitions.size(); count2++) {
			Transition T2 = (Transition) timedTransitions.get(count2);
			if (T2.contains(x,y)) {
//				System.out.println("Transition " + name + " is a timed, returning");
				return T2;
			}
		}

//		System.out.println("Transition " + name + " not found, returning");
		return null;
	}

	//  function which returns the Transition, either Immediate or Timed, with
	//  the name "name".  Used in backwards animation to identify the Transition
	//  to be unfired from the name popped of the top of the trace Stack.  
	public Transition getTransition(String name) {
//		System.out.println("Entering getTransition(name)");
		for (int count = 0; count < immediateTransitions.size(); count++) {
			Transition T1 = (Transition) immediateTransitions.get(count);
			if (T1.getName().equals(name)) {
//				System.out.println("Transition " + name + " is an immediate, returning");
				return T1;
			}
		}

		for ( int count2 = 0; count2 < timedTransitions.size(); count2++) {
			Transition T2 = (Transition) timedTransitions.get(count2);
			if (T2.getName().equals(name)) {
//				System.out.println("Transition " + name + " is a timed, returning");
				return T2;
			}
		}

//		System.out.println("Transition " + name + " not found, returning");
		return null;
	}

	//  function which returns the ImmediateTransition at (x,y)
	public ImmediateTransition getImmediateTransition(int x, int y) {
		for (int count = 0; count < immediateTransitions.size(); count++) {
			ImmediateTransition iT =
				(ImmediateTransition) immediateTransitions.get(count);
			if (iT.contains(x, y))
				return iT;
		}
		return null;
	}

	//  function which returns the nth ImmediateTransition in the
	//  immediateTransition Vector.
	public ImmediateTransition getImmediateTransition(int n) {
		return (ImmediateTransition) immediateTransitions.get(n);
	}

	//  function which returns the TimedTransition at (x,y)
	public TimedTransition getTimedTransition(int x, int y) {
		for (int count = 0; count < timedTransitions.size(); count++) {
			TimedTransition tT = (TimedTransition) timedTransitions.get(count);
			if (tT.contains(x, y))
				return tT;
		}
		return null;
	}

	//  function which returns the nth TimedTransition in the TimedTransition
	//  Vector
	public TimedTransition getTimedTransition(int n) {
		return (TimedTransition) timedTransitions.get(n);
	}

	//  function which returns the Arc whose handle is at (x,y)
	public Arc getArc(int x, int y) {
		for (int count = 0; count < arcs.size(); count++) {
			Arc arc = (Arc) arcs.get(count);
			if (arc.boxContains(x,y))
				return arc;
		}
		return null;
	}

	//  function which returns the nth Arc in the arcs Vector
	public Arc getArc(int n) {
		return (Arc) arcs.get(n);
	}

	//  function which returns any Node in the PetriNet given its name
	public Node getNode(String name) {

		for ( int count = 0; count < places.size(); count++) {
			Node p = (Node) places.get(count);
			if (p.getName().equals(name)) {
				return p;
			}
		}

		for (int count2 = 0; count2 < immediateTransitions.size(); count2++) {
			Node iT = (Node) immediateTransitions.get(count2);
			if (iT.getName().equals(name)) {
//				System.out.println("Transition " + name + " is an immediate, returning");
				return iT;
			}
		}

		for ( int count3 = 0; count3 < timedTransitions.size(); count3++) {
			Node tT = (Node) timedTransitions.get(count3);
			if (tT.getName().equals(name)) {
//				System.out.println("Transition " + name + " is a timed, returning");
				return tT;
			}
		}
		//    System.out.println("Transition " + name + " not found, returning");
		return null;
	}

	public Stack getTrace(){ return trace;}


	//  function which rotates the Transition at (x,y)
	public void rotateTransition(int x, int y) {
		/*    ImmediateTransition iT = getImmediateTransition(x,y);
    if (iT != null)
      iT.rotate();
    else {
      TimedTransition tT = getTimedTransition(x,y);
      if (tT != null)
        tT.rotate();
    }*/

		Transition t = getTransition(x,y);
		if (t != null)
			t.rotate();
	}

	//  function which deletes all Arcs which either start or end at (x,y)
	private void deleteArcs(int x, int y) {
		for (int count = 0; count < arcs.size(); count++) {
			Arc arc = (Arc) arcs.get(count);
			if ((arc.getStart().getX()==x && arc.getStart().getY()==y)
					|| (arc.getEnd().getX()==x && arc.getEnd().getY()==y)) {
				arcs.remove(arc);
				deleteArcs(x,y);
			}
		}
	}

	//  function which adds the name of a fired Transition to the trace Stack
	//  of all Transitions fired in this animation.
	public void addToTrace(String transName) {
//		System.out.println("In addToTrace, marking is " + marking);
//		System.out.println("In addToTrace, trace is " + trace);
//		System.out.println("Adding " + transName + " to trace");
//		System.out.println("in addToTrace(), trace is null!!");
		trace.push(transName);
//		System.out.println("In addToTrace, trace is " + trace);
//		}
	}

	//  function which causes every Arc to draw itself
	public void drawArcs(Graphics2D g2) {
		for (int count=0; count < arcs.size(); count++) {
			Arc arc = (Arc) arcs.get(count);
			arc.draw(g2);
		}
	}

	//  function which causes every Place to draw itslef
	public void drawPlaces(Graphics2D g2) {
		for (int count = 0; count < places.size(); count++) {
			Place place = (Place) places.get(count);
			place.draw(g2);
		}
	}

	//  function which causes every Transition to draw itself
	public void drawTransitions(Graphics2D g2) {
		for (int count = 0; count < immediateTransitions.size(); count++) {
			ImmediateTransition iT =
				(ImmediateTransition) immediateTransitions.get(count);
			iT.draw(g2);
		}

		for (int count = 0; count < timedTransitions.size(); count++) {
			TimedTransition tT = (TimedTransition) timedTransitions.get(count);
			tT.draw(g2);
		}
	}

	//  Convert from a filename to a file URL.
	private String convertToFileURL(String fileName) {
		try {
			String path = new File(fileName).toURL().toString();
			return path;
		} catch (MalformedURLException mue) {
			System.out.println(mue);
		}
		return null;
	}

	//  function which rounds an integer "value" to the nearest "toWhat"
	//  Java doesn't seem to have an in-built function to do this
	private int round(int value, int toWhat) {
		if (value % toWhat == 0)
			return value;
		else if (value % toWhat < 10)
			return value -= (value % toWhat);
		else
			return value += (toWhat - (value % toWhat));
	}

	//  function which combines moving onto a new line
	//  and adding any number of Tabs
	private void format(FileWriter fW, int numberOfTabs) {
		try {
			fW.write(13);
			for (int count = 0; count < numberOfTabs; count++) {
				fW.write(9);
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	//  true if the trace is empty, ie haven't fired any Transitions
	public boolean emptyTrace() {
		if (trace != null)
			return trace.empty();
		else
			return true;
	}

	//  accessor function which gets the number of Places in the PetriNet
	public int getNumberOfPlaces() { return places.size(); }

	//  accessor function which gets the total number of
	//  Transitions in the PetriNet
	public int getNumberOfTransitions() { return (immediateTransitions.size()+timedTransitions.size()); }

	//  accessor function which gets the number of
	//  ImmediateTransitions in the PetriNet
	public int getNumberOfImmTransitions() { return immediateTransitions.size(); }

	//  accessor function which gets the number of
	//  TimedTransitions in the PetriNet
	public int getNumberOfTimedTransitions() { return timedTransitions.size(); }

	//  accessor function which gets the number of Arcs in the PetriNet
	public int getNumberOfArcs() { return arcs.size(); }

	//  function which checks if Medusa is animating
//	public boolean isAnimating() { return myPNP.isAnimating(); }
	public boolean isAnimating() { return false; }

}
