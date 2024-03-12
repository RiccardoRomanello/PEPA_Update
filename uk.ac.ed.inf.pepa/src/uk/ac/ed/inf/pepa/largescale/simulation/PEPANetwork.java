package uk.ac.ed.inf.pepa.largescale.simulation;

import java.io.IOException;
import java.util.ArrayList;

import uk.ac.ed.inf.pepa.largescale.IGeneratingFunction;
import uk.ac.ed.inf.pepa.largescale.IParametricDerivationGraph;
import uk.ac.ed.inf.pepa.largescale.ParametricDerivationGraphBuilder;
import uk.ac.ed.inf.pepa.largescale.expressions.EvaluatorVisitor;
import uk.ac.ed.inf.pepa.ode.DifferentialAnalysisException;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;
import umontreal.iro.lecuyer.stat.Tally;
import fern.network.AmountManager;
import fern.network.AnnotationManager;
import fern.network.AnnotationManagerImpl;
import fern.network.Network;
import fern.network.PropensityCalculator;
import fern.simulation.Simulator;
import fern.simulation.Simulator.FireType;
import fern.simulation.algorithm.GillespieEnhanced;
import fern.simulation.observer.Observer;

/**
 * Implementation of a network interface for the stochastic simulation of the
 * population-based Markov chain.
 * 
 * @author Mirco
 * 
 */
public class PEPANetwork implements Network {

	public static void main(String[] args) throws IOException,
			DifferentialAnalysisException, InterruptedException {
		ModelNode model = (ModelNode) PepaTools.parse(PepaTools
				.readText(args[0]));
		final IParametricDerivationGraph derivationGraph = ParametricDerivationGraphBuilder
				.createDerivationGraph(model, null);
		PEPANetwork net = new PEPANetwork(derivationGraph);
		final Simulator sim = new GillespieEnhanced(net);
		final Tally tally1 = new Tally();
		final Tally tally2 = new Tally();
		Observer observer = new Observer(sim) {
			
			
			@Override
			public void activateReaction(int mu, double tau, FireType fireType,
					int times) {
			}

			@Override
			public void finished() {
				tally1.add(sim.getAmount(derivationGraph.getInitialState().length -1));
				tally2.add(sim.getAmount(derivationGraph.getInitialState().length -3));
			}

			@Override
			public void started() {
				// System.out.println("Started");
				//setTheta(0);
			}

			@Override
			public void step() {
				// System.out.println("Step:" + sim.getTime() + " "
				// + sim.getAmount(0));
			}

			@Override
			public void theta(double theta) {
				//System.out.println(theta + " : " + sim.getAmount(derivationGraph.getInitialState().length -1));
				//setTheta(theta + 0.1);
			}
		};
		sim.addObserver(observer);
		long tic = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			System.out.println("Sample " + i);
			sim.initialize();
			sim.start(10);
		}
		long toc = System.currentTimeMillis() - tic;
		System.out.println(toc + ": Average: "  + tally1.average());
		System.out.println(toc + ": Average: "  + tally2.average());


	}

	private IParametricDerivationGraph derivationGraph;

	private double[] initialState;

	private PropensityCalculator propensityCalculator;

	private AmountManager amountManager;

	private AnnotationManager annotationManager;

	private ArrayList<int[]> reactants;

	private ArrayList<int[]> products;

	public PEPANetwork(final IParametricDerivationGraph derivationGraph) {
		this.derivationGraph = derivationGraph;
		this.initialState = derivationGraph.getInitialState();
		this.propensityCalculator = new PropensityCalculator() {

			private final double[] state = new double[initialState.length];

			public double calculatePropensity(int reaction,
					AmountManager amount, Simulator sim) {
				IGeneratingFunction f = derivationGraph
						.getGeneratingFunctions()[reaction];
				for (int i = 0; i < state.length; i++)
					state[i] = amount.getAmount(i);
				double result;
				try {
					result = new EvaluatorVisitor(f.getRate(), state)
							.getResult();
				} catch (DifferentialAnalysisException e) {
					throw new IllegalStateException(e);
				}
				return result;
			}

		};
		this.annotationManager = new AnnotationManagerImpl();
		reactants = new ArrayList<int[]>();
		products = new ArrayList<int[]>();
		for (int i = 0; i < derivationGraph.getGeneratingFunctions().length; i++) {
			reactants.add(getElements(i, -1));
			products.add(getElements(i, +1));
		}
		// DEBUG
		/*
		 * for (int i = 0; i < derivationGraph.getGeneratingFunctions().length;
		 * i++) { System.out.println("\n" +
		 * derivationGraph.getGeneratingFunctions()[i].getRate().toString());
		 * for (int j : reactants.get(i)) System.out.print(" " + j + " ");
		 * System.out.println(); for (int j : products.get(i))
		 * System.out.print(" " + j + " ");
		 * 
		 * }
		 */
	}

	public AmountManager getAmountManager() {
		if (amountManager == null)
			this.amountManager = new AmountManager(this);
		return this.amountManager;
	}

	public AnnotationManager getAnnotationManager() {
		return this.annotationManager;
	}

	public long getInitialAmount(int species) {
		return (long) this.initialState[species];
	}

	public String getName() {
		return "Pepa Model";
	}

	public int getNumReactions() {
		return this.derivationGraph.getGeneratingFunctions().length;
	}

	public int getNumSpecies() {
		return this.derivationGraph.getInitialState().length;
	}

	public int[] getProducts(int reaction) {
		return products.get(reaction);
	}

	public PropensityCalculator getPropensityCalculator() {
		return propensityCalculator;
	}

	public int[] getReactants(int reaction) {
		return reactants.get(reaction);
	}

	/**
	 * kind is 1 if product, -1 if reagent
	 * 
	 * @param reaction
	 * @param k
	 * @return
	 */
	private int[] getElements(int reaction, int k) {
		short[] jump = this.derivationGraph.getGeneratingFunctions()[reaction]
				.getJump();
		int[] products = new int[jump.length];
		int j = 0;
		for (int i = 0; i < jump.length; i++)
			if (jump[i] == k)
				products[j++] = i;
		int[] result = new int[j];
		System.arraycopy(products, 0, result, 0, j);
		return result;

	}

	public String getReactionName(int index) {
		return "Reac" + index;
	}

	public int getSpeciesByName(String name) {
		return 0;
	}

	public String getSpeciesName(int index) {
		return null;
	}

	public void setInitialAmount(int species, long value) {
		if (species < initialState.length)
			initialState[species] = value;
	}

}
