/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;

import uk.ac.ed.inf.common.ui.plotting.IChart;

/**
 * Interface for experiment model.
 * <p>
 * Its contents are generated automatically within a single page. It implements
 * dynamic children to notify its parent of new changes
 * 
 * @author mtribast
 * 
 */
public interface IExperiment extends IDynamicChildren {

	/**
	 * Human-readable name for this experiment
	 * 
	 * @return the name for the experiment
	 */
	public String getName();

	/**
	 * Set the name for this experiment
	 * 
	 * @param name
	 */
	public void setName(String name);

	/**
	 * The experiment is asked to paint its controls for parameter settings.
	 * <p>
	 * May be called multiple times during this experiment's life cycle. It
	 * should persist the most recent values across different invocations.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent);

	/**
	 * Dispose of this experiment
	 */
	public void dispose();

	/**
	 * Get the experiment page that contains this experiment
	 * 
	 * @return
	 */
	public ExperimentPage getExperimentPage();

	/**
	 * Set the experiment page containing the experiment. This method should be
	 * called before {@link #createControl(Composite)} is called.
	 * 
	 * @param page
	 */
	public void setExperimentPage(ExperimentPage page);

	/**
	 * Run the experiment.
	 * <p>
	 * The output is a {@link IChart} which is shown on the Graph View. Chart
	 * customisation can take place by using the view's context and toolbar
	 * menu.
	 * <p>
	 * The <code>showAsYouGo</code> option determines if the graph is updated
	 * as a point is computed. Notice however that this would incur performance
	 * loss. It has been estimated that the synchronous refresh is about 20%
	 * slower. Implementors must make sure that UI operations are performed in
	 * the UI thread, otherwise an SWT Illegal Access exception will be thrown.
	 * 
	 * @param monitor
	 *            progress monitor for this experiment
	 * @param showAsYouGo
	 *            for synchronous refresh
	 * 
	 */
	public void run(IProgressMonitor monitor, boolean showAsYouGo)
			throws EvaluationException;

	/**
	 * Determine if the experiment settings are valid and thus it can be run
	 * 
	 * @return <code>true</code> if the experiment settings are valid
	 */
	public boolean isCanRun();

	/**
	 * Set the {@link ISetting}'s which are currently available.
	 * <p>
	 * This method can be called before the control is created as well as
	 * afterwards. In this case the controls should be updated to reflect the
	 * new settings.
	 * <p>
	 * The method is called for example when the user navigates the wizard and
	 * add new elements from the first page. The experiment page won't be
	 * created again in order not to loose the user's previous settings. All the
	 * controls for will be updated to be in sync with the new settings.
	 * 
	 * @throws NullPointerException
	 *             if null is passed
	 * 
	 */
	public void setAvailableSettings(ISetting[] settings);

	/**
	 * Set the available AST Nodes for this experiment. This method is usually
	 * called by the wizard in response to the user selecting new nodes or
	 * deleting already chosen one on the first page of the wizard.
	 * <p>
	 * This call should trigger a notification of the status of the experiments.
	 * It should be called before {@link #createControl(Composite)} is called
	 * 
	 * @param astNodes
	 *            new available AST Nodes
	 */
	public void setAvailableNodes(Object[] astNodes);

	/**
	 * Set the performance metric factory to be used within this experiment
	 * 
	 * @param factory
	 */
	public void setPerformanceMetricFactory(
			AbstractPerformanceMetricFactory factory);
}
