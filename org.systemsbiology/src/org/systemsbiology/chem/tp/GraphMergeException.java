package org.systemsbiology.chem.tp;

public class GraphMergeException extends Exception
{
	private String message;
	public GraphMergeException(String s)
	{
		super(s);
		this.message = s;
	}
	public String getMessage() { return message; }
}
