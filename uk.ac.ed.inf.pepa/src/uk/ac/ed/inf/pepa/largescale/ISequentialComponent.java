package uk.ac.ed.inf.pepa.largescale;

import java.util.Map;
import java.util.Set;

import uk.ac.ed.inf.pepa.largescale.expressions.Coordinate;
import uk.ac.ed.inf.pepa.largescale.expressions.Expression;

public interface ISequentialComponent {

	public int getInitialPopulationLevel();

	public Set<Map.Entry<Short, Coordinate>> getComponentMapping();
	
	public Expression getApparentRate(short actionId);
	
	public short[] getActionAlphabet();
	
	public String getName();

}