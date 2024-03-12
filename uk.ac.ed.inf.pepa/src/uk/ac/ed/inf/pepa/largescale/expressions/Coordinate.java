package uk.ac.ed.inf.pepa.largescale.expressions;

import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class Coordinate extends Expression {
	
	private static final String VECTOR = "x";
	
	private int coordinate;
	
	/**
	 * Coordinates are zero-based
	 * @param coordinate index of the coordinate
	 */
	public Coordinate(int coordinate) {
		if (coordinate < 0)
			throw new IllegalArgumentException("Coordinate must be positive");
		this.coordinate = coordinate;
	}
	
	public int getCoordinate() {
		return coordinate;
	}
	
	@Override
	public String toString() {
		return VECTOR + "(" + coordinate + ")";
	}

	@Override
	public void accept(ExpressionVisitor visitor) throws DifferentialAnalysisException {
		visitor.visitCoordinate(this);
	}

}
