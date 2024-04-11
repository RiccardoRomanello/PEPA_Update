/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.core;

/**
 * A PEPA model changed event describes events which affected the state of a
 * PEPA model, such as state space derivation, CTMC solution, etc.
 * 
 * @author mtribast
 * @see IProcessAlgebraModelChangedListener
 * 
 */
public class ProcessAlgebraModelChangedEvent {

	/**
	 * Event type constant indicating that the model has been parsed
	 */
	public static final int PARSED = 0;

	/**
	 * Event type constant indicating that the state space has been derived
	 */
	public static final int STATE_SPACE_DERIVED = 1;

	/**
	 * Event type constant indicating that the state space has been solved
	 */
	public static final int CTMC_SOLVED = 2;

	public static final int REACTIONS_GENERATED = 3;

	public static final int TIME_SERIES_ANALYSED = 4;
	
	public static final int KRONECKER_DERIVED = 5;
	
	public static final int MODEL_CHECKED = 6;
	
	public static final int MODEL_CHECKING_INFO = 7;

	public static final int PSNI_CHECKED = 8;

	private int fType;

	private Exception fException;

	private IProcessAlgebraModel fModelChanged;

	private long fElapsedTimeMillis;
	
	private String fInformation;

	/**
	 * Create an event with no exception to be reported
	 * 
	 * @param type
	 *            the type of the event to be reported
	 */
	public ProcessAlgebraModelChangedEvent(int type,
			IProcessAlgebraModel modelChanged, long elapsedTimeMillis) {
		this(type, modelChanged, null, elapsedTimeMillis, "");
	}
	
	/**
	 * Create an event with no exception to be reported, and additional information
	 * 
	 * @param type
	 *            the type of the event to be reported
	 * @param information
	 *            additional information associated with the event
	 */
	public ProcessAlgebraModelChangedEvent(int type,
			IProcessAlgebraModel modelChanged, long elapsedTimeMillis, String information) {
		this(type, modelChanged, null, elapsedTimeMillis, information);
	}
	
	/**
	 * Create an event carrying an exception
	 * 
	 * @param type
	 * @param exception
	 */
	public ProcessAlgebraModelChangedEvent(int type,
			IProcessAlgebraModel modelChanged, Exception exception,
			long elapsedTimeMillis) {
		this(type, modelChanged, exception, elapsedTimeMillis, "");
	}
	
	
	/**
	 * Create an event carrying an exception, with additional information
	 * 
	 * @param type
	 * @param exception
	 * @param information
	 */
	public ProcessAlgebraModelChangedEvent(int type,
			IProcessAlgebraModel modelChanged, Exception exception,
			long elapsedTimeMillis, String information) {
		this.fType = type;
		this.fException = exception;
		this.fModelChanged = modelChanged;
		this.fElapsedTimeMillis = elapsedTimeMillis;
		this.fInformation = information;
	}

	/**
	 * Return the type of the event being reported
	 * 
	 * @return the type of the event
	 */
	public int getType() {
		return this.fType;
	}

	/**
	 * Return an exception associated to this event, or null if none.
	 * 
	 * @return the exception, or null
	 */
	public Exception getException() {
		return fException;
	}

	public String getInformation() {
		return fInformation;
	}
	
	/**
	 * Return the affected model
	 * 
	 * @return the affected model
	 */
	public IProcessAlgebraModel getProcessAlgebraModel() {
		return fModelChanged;
	}

	public long getElapsedTimeMillis() {
		return fElapsedTimeMillis;
	}

}
