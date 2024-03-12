/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.cptview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import uk.ac.ed.inf.pepa.cpt.CPTAPI;
import uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer.IResultTreeNode;

public class CapacityPlanningView extends ViewPart {
	
	private Label label;
	private Composite container;
	private static TreeViewer viewer;
	GridData data;
	
	class TreeContentProvider implements ITreeContentProvider
	{

		@Override
		public Object[] getChildren(Object parentElement) {
			return ((IResultTreeNode)parentElement).getChildren().toArray();
		}

		@Override
		public Object getParent(Object element) {
			return ((IResultTreeNode)element).getParent();
		}

		@Override
		public boolean hasChildren(Object element) {
			return ((IResultTreeNode)element).hasChildren();
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}	
	
	}
	
	class TreeLabelProvider extends LabelProvider
	{
		public String getText(Object element) {		
			return ((IResultTreeNode)element).getName();
		}
		
		public Image getImage(Object element) {		
			return ((IResultTreeNode)element).getImage();
		}
	}
	
	public static void update(){
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				viewer.setInput(CPTAPI.getResultNode());
			}
			
			
		});
	}
        
	public void createPartControl(Composite parent) {
		
		container = center(parent);
		
		createTree();

	}
	
	public void createViewer(){
		
		viewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 14, 1);
		data.widthHint = 1050;
		data.grabExcessHorizontalSpace = true;
	    data.grabExcessVerticalSpace = true;
	    data.horizontalAlignment = GridData.FILL;
	    viewer.getControl().setLayoutData(data);
		
		viewer.setContentProvider(new TreeContentProvider());
	    viewer.setLabelProvider(new TreeLabelProvider());
	    viewer.setInput(CPTAPI.getResultNode());
		
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

	@Override
	public void setFocus() {
	    viewer.getControl().setFocus();
	}
	
}