package uk.ac.ed.inf.pepa.tests;

import java.io.IOException;

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Parameter;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.ParametricDerivationGraphBuilder;
import uk.ac.ed.inf.pepa.largescale.simulation.PEPANetwork;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.sba.PEPAtoSBA;
import uk.ac.ed.inf.pepa.sba.Results;
import uk.ac.ed.inf.pepa.sba.SBAParseException;
import uk.ac.ed.inf.pepa.sba.SBASimulatorException;
import uk.ac.ed.inf.pepa.sba.SBAtoISBJava;
import uk.ac.ed.inf.pepa.tools.PepaTools;
import fern.simulation.Simulator;
import fern.simulation.Simulator.FireType;
import fern.simulation.algorithm.GillespieEnhanced;
import fern.simulation.algorithm.GillespieSimple;
import fern.simulation.observer.Observer;

public class SimulationComparison {

	public static void main(String[] args) throws IOException,
			DifferentialAnalysisException, InterruptedException,
			SBAParseException, SBASimulatorException {
		ModelNode model = (ModelNode) PepaTools.parse(PepaTools
				.readText(args[0]));
		long timeFERN = 0L;
		long timeDIZZY = 0L;
		for (int i = 0; i < 5; i++) {
			//timeDIZZY += simulateDIZZY(model);
			
			timeFERN += simulateFERN(model);
		}
		System.out.println("FERN: " +timeFERN);
		System.out.println("DIZZY: " + timeDIZZY);
		
	}

	private static long simulateFERN(ModelNode model) throws DifferentialAnalysisException, InterruptedException {
		
		IParametricDerivationGraph derivationGraph = ParametricDerivationGraphBuilder
				.createDerivationGraph(model, null);
		PEPANetwork net = new PEPANetwork(derivationGraph);
		final Simulator sim = new GillespieEnhanced(net);
		Observer observer = new Observer(sim) {

			@Override
			public void activateReaction(int mu, double tau, FireType fireType,
					int times) {
			}

			@Override
			public void finished() {
			}

			@Override
			public void started() {
				// System.out.println("Started");
				setTheta(0);
			}

			@Override
			public void step() {
				//System.out.println("Step:" + sim.getTime() + " " + sim.getAmount(3));
			}

			@Override
			public void theta(double theta) {
				System.out.println(theta + " : " + sim.getAmount(3));
				setTheta(theta + 1.0);
			}
		};
		sim.addObserver(observer);
		long tic = System.currentTimeMillis();
		sim.start(30);
		long toc = System.currentTimeMillis();
		System.out.println("Partial FERN: " + (toc-tic));
		return toc-tic;
	
		
	}

	private static long simulateDIZZY(ModelNode model) throws SBAParseException, SBASimulatorException {
		OptionsMap map = new OptionsMap();
		map.setValue(Parameter.Components, new String [] {"PServer_Exe"});
		map.setValue(Parameter.Solver, OptionsMap.Solver.Gillespie);
		map.setValue(Parameter.Start_Time, 0.0);
		map.setValue(Parameter.Stop_Time, 30.0);
		map.setValue(Parameter.Independent_Replications, 1);
		PEPAtoSBA p2s = new PEPAtoSBA(model);
		p2s.parseModel();
		SBAtoISBJava modelSBA = new SBAtoISBJava(p2s);
		modelSBA.generateISBJavaModel("name", true);
		modelSBA.initialiseSimulator(map);
		long tic = System.currentTimeMillis();
		Results r = modelSBA.runModel();
		long toc = System.currentTimeMillis();
		System.out.println("Partial DIZZY: " + (toc-tic));
		return toc-tic;
	}

}
