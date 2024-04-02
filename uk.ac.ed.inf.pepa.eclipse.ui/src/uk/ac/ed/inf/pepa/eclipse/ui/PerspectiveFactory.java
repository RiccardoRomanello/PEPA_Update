/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import uk.ac.ed.inf.common.ui.plotview.views.PlotView;

/**
 * This shows the default perspective for PEPA projects.
 * 
 * @author mtribast
 *
 */
public class PerspectiveFactory implements IPerspectiveFactory {
	
	public static final String PERSPECTIVE_ID = "uk.ac.ed.inf.pepa.eclipse.ui.PepaPerspective";
	/* Taken from plugin.xml */
	private static final String AST_VIEW = "uk.ac.ed.inf.pepa.eclipse.ui.ASTView";
	
	private static final String ABSTRACTION_VIEW = "uk.ac.ed.inf.pepa.eclipse.ui.AbstractionView";
	
	private static final String MODEL_CHECKING_VIEW = "uk.ac.ed.inf.pepa.eclipse.ui.ModelCheckingView";
	
	private static final String STATE_SPACE_VIEW = "uk.ac.ed.inf.pepa.eclipse.ui.stateSpaceView";
	
	private static final String PERFORMANCE_EVALUATION_VIEW = "uk.ac.ed.inf.pepa.eclipse.ui.utilisationView";
	
	private static final String PLOT_VIEW = PlotView.ID;
	
	private static final String CAPACITY_PLANNING_VIEW = "uk.ac.ed.inf.pepa.eclipse.ui.view.cptview";
	
	private static final String CAPACITY_PLANNING_LIST = "uk.ac.ed.inf.pepa.eclipse.ui.view.cptlist";
	
	public void createInitialLayout(IPageLayout layout) {
		
		String editorArea = layout.getEditorArea();
		/* Resource Navigator on the left */
		layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT, 0.25f, editorArea);
		/* Folder layout for bottom views */
		IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.66f, editorArea);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		/* Create a place holder for the Error Log */
		
		bottom.addView(AST_VIEW);
		bottom.addView(ABSTRACTION_VIEW);
		bottom.addView(MODEL_CHECKING_VIEW);
		bottom.addView(STATE_SPACE_VIEW);
		bottom.addView(PLOT_VIEW);
		/* Folder layout for right views */
		IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.75f, editorArea);
		right.addView(IPageLayout.ID_OUTLINE);
		right.addView(PERFORMANCE_EVALUATION_VIEW);
		right.addView(CAPACITY_PLANNING_LIST);
		right.addView(CAPACITY_PLANNING_VIEW);
		
		
	}

}