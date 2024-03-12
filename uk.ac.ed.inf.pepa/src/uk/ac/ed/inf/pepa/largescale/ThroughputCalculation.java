package uk.ac.ed.inf.pepa.largescale;

import uk.ac.ed.inf.pepa.largescale.expressions.EvaluatorVisitor;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;

public class ThroughputCalculation implements IPointEstimator {

	private short actionId;

	private IParametricDerivationGraph derivationGraph;
	
	//private EvaluatorVisitor visitor = new EvaluatorVisitor();

	public ThroughputCalculation(short actionId,
			IParametricDerivationGraph derivationGraph) {
		if (actionId < 0)
			throw new IllegalArgumentException("Action id not valid");
		this.actionId = actionId;
		this.derivationGraph = derivationGraph;

	}

	public double computeEstimate(double timePoint, double[] solution)
			throws DifferentialAnalysisException {
		double currentThroughput = 0;

		for (IGeneratingFunction f : derivationGraph.getGeneratingFunctions()) {
			if (f.getActionId() == actionId) {
				currentThroughput += new EvaluatorVisitor(f.getRate(), solution)
						.getResult();
				//currentThroughput += visitor.getResult(f.getRate(), solution);
			}
		}
		return currentThroughput;
		
	}
	public short getActionId() {
		return actionId;
	}

}
