/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class PropertyManager {

	private static final int PROPERTY_UNSELECTED = -1;
	private static final int PROPERTY_FALSE      = 0;
	private static final int PROPERTY_TRUE       = 1;
	private static final int PROPERTY_MAYBE      = 2;
	
	public static final Color COLOR_UNSELECTED = new Color(null,255,255,255);
	public static final Color COLOR_FALSE      = new Color(null,255,150,150);
	public static final Color COLOR_TRUE       = new Color(null,150,255,150);
	public static final Color COLOR_MAYBE      = new Color(null,200,200,200);
	public static final Color COLOR_FALSE_TEXT = new Color(null,155,0,0);
	public static final Color COLOR_TRUE_TEXT  = new Color(null,0,155,0);
	public static final Color COLOR_MAYBE_TEXT = new Color(null,100,100,100);
	
	public static TableItem newProperty(Table parent, int style) {
		TableItem property = new TableItem(parent, style);
		setUnselected(property);
		return property;
	}
	
	private static int getData(TableItem property) {
		return (Integer) property.getData();
	}
	
	private static void setData(TableItem property, int data) {
		property.setData(data);
	}
	
	public static boolean isTrue(TableItem property) {
		return getData(property) == PROPERTY_TRUE;
	}
	
	public static void setTrue(TableItem property) {
		setData(property, PROPERTY_TRUE);
		property.setBackground(COLOR_TRUE);
		property.setText(1, "TRUE");
		property.setForeground(1, COLOR_TRUE_TEXT);
	}
	
	public static boolean isFalse(TableItem property) {
		return getData(property) == PROPERTY_FALSE;
	}
	
	public static void setFalse(TableItem property) {
		setData(property, PROPERTY_FALSE);
		property.setBackground(COLOR_FALSE);
		property.setText(1, "FALSE");
		property.setForeground(1, COLOR_FALSE_TEXT);
	}
	
	public static boolean isMaybe(TableItem property) {
		return getData(property) == PROPERTY_MAYBE;
	}
	
	public static void setMaybe(TableItem property) {
		setData(property, PROPERTY_MAYBE);
		property.setBackground(COLOR_MAYBE);
		property.setText(1, "???");
		property.setForeground(1, COLOR_MAYBE_TEXT);
	}
	
	public static boolean isUnselected(TableItem property) {
		return getData(property) == PROPERTY_UNSELECTED;
	}
	
	public static void setUnselected(TableItem property) {
		setData(property, PROPERTY_UNSELECTED);
		property.setBackground(COLOR_UNSELECTED);
		property.setText(1, "");
		property.setForeground(1, COLOR_UNSELECTED);
	}
	
	public static void click(TableItem property) {
		if (isTrue(property)) {
			setFalse(property);
		} else if (isFalse(property)) {
			setUnselected(property);
		} else {
			setTrue(property);
		}
	}
	
}
