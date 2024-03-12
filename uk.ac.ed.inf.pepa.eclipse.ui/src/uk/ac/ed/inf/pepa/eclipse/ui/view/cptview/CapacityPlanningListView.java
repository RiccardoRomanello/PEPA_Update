/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.cptview;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer.PopulationAndCost;

public class CapacityPlanningListView extends ViewPart {
	
	private Label label;
	private Composite container;
	private static TableViewer viewer;
	GridData data;
	
	public static void update(){
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				viewer.setInput(CPTAPI.getPACSList());
			}
			
			
		});
	}
        
	public void createPartControl(Composite parent) {
		
		container = center(parent);
		
		createTree();

	}
	
	public void createViewer(){
		
		viewer = new TableViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(container, viewer);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 14, 1);
		data.widthHint = 1050;
		data.grabExcessHorizontalSpace = true;
	    data.grabExcessVerticalSpace = true;
	    data.horizontalAlignment = GridData.FILL;
	    viewer.getControl().setLayoutData(data);
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
	    table.setLinesVisible(true);
		
		viewer.setContentProvider(new ArrayContentProvider());
	    viewer.setInput(CPTAPI.getPACSList());
	    getSite().setSelectionProvider(viewer);
		
	}
	
	public TableViewer getViewer() {
		return viewer;
	}
	
	public void createTree(){
		
		//pad
		label = new Label(container, SWT.FILL);
		label.setText("");
		data = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		data.widthHint = 25;
		label.setLayoutData(data);
		
		createViewer();
		
		//pad
		label = new Label(container, SWT.FILL);
		label.setText("");
		data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		data.widthHint = 25;
		label.setLayoutData(data);
	}
	
	public Composite center(Composite container){
		
		Composite child = new Composite(container, SWT.NONE );
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		child.setLayoutData(data);
		GridLayout layout = new GridLayout(16,false);
		child.setLayout(layout);
		
		return child;
	}
	
	  // create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Cost", "Population", "Performance measure", "Total Population"};
		int[] bounds = {100,700,100,100};

	    TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	        PopulationAndCost p = (PopulationAndCost) element;
	        return p.getCost();
	      }
	    });

	    col = createTableViewerColumn(titles[1], bounds[1], 1);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  PopulationAndCost p = (PopulationAndCost) element;
		        return p.getPopulation();
	      }
	    });
	    
	    col = createTableViewerColumn(titles[2], bounds[2], 2);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  PopulationAndCost p = (PopulationAndCost) element;
		        return p.getPerformance();
	      }
	    });
	    
	    col = createTableViewerColumn(titles[3], bounds[3], 3);
	    col.setLabelProvider(new ColumnLabelProvider() {
	      @Override
	      public String getText(Object element) {
	    	  PopulationAndCost p = (PopulationAndCost) element;
		        return p.getTotalPopulation();
	      }
	    });

	}
	
	 private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		    final TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
		        SWT.NONE);
		    final TableColumn column = viewerColumn.getColumn();
		    column.setText(title);
		    column.setWidth(bound);
		    column.setResizable(true);
		    column.setMoveable(true);
		    return viewerColumn;
		  }

	@Override
	public void setFocus() {
	    viewer.getControl().setFocus();
	}
	

}