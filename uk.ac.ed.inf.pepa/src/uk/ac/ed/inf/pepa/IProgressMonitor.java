/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa;

/**
 * This interface mimics the behaviour of the Eclipse IProgressMonitor. However,
 * a new interface has been preferred in order to avoid any dependency with
 * third-party libraries.
 * <p>
 * The <code>IProgressMonitor</code> is implemented by objects that monitor
 * the progress of an acitivity. Most of its methods are invoked by the code
 * that perform the activity.
 * <p>
 * The listener, i.e. the class which passes an <code>IProgressMonitor</code>
 * to the operation, can ask the operation to interrupt by calling the
 * <code>setCanceled</code> method. Operations accepting progress monitor are
 * required to poll the <code>isCanceled</code> method in order to acknowledge
 * the request.
 * <p>
 * When an operation is started, the listener should be notified via
 * <code>beginTask</code>, indicating the total amount of work that needs to
 * be done. For operations whose work cannot be determined a priori, the
 * <code>UNKNOWN</code> constant should be passed.
 * <p>
 * When an operation is done, the <code>done</code> method should be called in
 * order to notify the listener of the end of the activity. Note that
 * <code>done</code> should be called when the activity acknowledge the cancel
 * request as well.
 * 
 * @author mtribast
 * 
 */
public interface IProgressMonitor {

	/**
	 * Constant indicating an unknown amount of work to be performed by the
	 * operation
	 * 
	 * @see #beginTask(int)
	 */
	public static final int UNKNOWN = -1;

	/**
	 * Called by the operation to indicate that the activity has been started.
	 * 
	 * @param amount
	 *            the total amount of work which has to be done.
	 */
	public void beginTask(int amount);

	/**
	 * Usually called by the listener to ask the monitored activity to
	 * interrupt.
	 * 
	 * @param state
	 *            true when a request is made. It can be cleared afterwards.
	 */
	public void setCanceled(boolean state);

	/**
	 * Called by the operation to determine if a cancel request is pending.
	 * 
	 * @return true if a cancel request is pending.
	 */
	public boolean isCanceled();

	/**
	 * Called by the operation to notify that a given amount of work has been
	 * done.
	 * 
	 * @param worked
	 *            amount of work done
	 */
	public void worked(int worked);

	/**
	 * Called by the operation to indicate that the job has been done.
	 * 
	 */
	public void done();

}
