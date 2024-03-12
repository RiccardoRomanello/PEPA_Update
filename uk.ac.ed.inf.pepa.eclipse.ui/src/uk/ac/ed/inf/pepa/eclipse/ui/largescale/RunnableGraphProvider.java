package uk.ac.ed.inf.pepa.eclipse.ui.largescale;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import uk.ac.ed.inf.pepa.eclipse.core.PepatoProgressMonitorAdapter;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.ParametricDerivationGraphBuilder;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.parsing.ModelNode;

public class RunnableGraphProvider implements IRunnableWithProgress {
	
	private ModelNode node;
	
	private IParametricDerivationGraph graph;
	
	public RunnableGraphProvider(ModelNode node) {
		this.node = node;
	}
	
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		try {
			this.graph = ParametricDerivationGraphBuilder
			.createDerivationGraph(node, new PepatoProgressMonitorAdapter(monitor, "ODE Generation"));
			if (monitor.isCanceled())
				throw new InterruptedException("ODE generation has been cancelled");
		} catch (DifferentialAnalysisException e) {
			throw new InvocationTargetException(e);
		}
	}
	
	public IParametricDerivationGraph getGraph() {
		return this.graph;
	}

}
