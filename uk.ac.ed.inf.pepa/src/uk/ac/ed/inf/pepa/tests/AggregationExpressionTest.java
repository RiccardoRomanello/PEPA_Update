/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import uk.ac.ed.inf.pepa.OptionsMap;
import uk.ac.ed.inf.pepa.OptionsMap.Parameter;
import uk.ac.ed.inf.pepa.ctmc.derivation.DerivationException;
import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.derivation.common.Compiler;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.model.Aggregation;
import uk.ac.ed.inf.pepa.model.Choice;
import uk.ac.ed.inf.pepa.model.Constant;
import uk.ac.ed.inf.pepa.model.Cooperation;
import uk.ac.ed.inf.pepa.model.Hiding;
import uk.ac.ed.inf.pepa.model.Model;
import uk.ac.ed.inf.pepa.model.Prefix;
import uk.ac.ed.inf.pepa.model.Visitor;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.sba.PEPAtoSBA;
import uk.ac.ed.inf.pepa.sba.Results;
import uk.ac.ed.inf.pepa.sba.SBASimulatorException;
import uk.ac.ed.inf.pepa.sba.SBAtoISBJava;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class AggregationExpressionTest {

	static class CompilerVisitor implements Visitor {

		public void visitAggregation(Aggregation aggregation) {
			for (Entry<uk.ac.ed.inf.pepa.model.Process, Integer> entry : aggregation
					.getSubProcesses().entrySet()) {
				entry.getKey().accept(this);
				System.out.println("Value:" + entry.getValue().doubleValue());
			}
		}

		public void visitChoice(Choice choice) {
		}

		
		public void visitConstant(Constant constant) {
			System.out.println(constant.getName());
		}

		
		public void visitCooperation(Cooperation cooperation) {
			cooperation.getLeftHandSide().accept(this);
			cooperation.getRightHandSide().accept(this);
		}

		
		public void visitHiding(Hiding hiding) {
		}

		public void visitPrefix(Prefix prefix) {
		}

	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws DerivationException 
	 * @throws SBASimulatorException 
	 */
	public static void main(String[] args) throws IOException, DerivationException, SBASimulatorException {
		String modelName = args[0];
		String modelText = TestFilter.readText(modelName);
		ModelNode modelNode = (ModelNode) PepaTools.parse(modelText);
		Compiler compiler = new Compiler(modelNode);
		Model model = compiler.getModel();
		
		model.getSystemEquation().accept(new CompilerVisitor());
		OptionMap map = new OptionMap();
		map.put(OptionMap.AGGREGATE_ARRAYS, false);
		IStateSpace ss = PepaTools.derive(map, modelNode, null, null);
		System.out.println("Size: " + ss.size());
		
		PEPAtoSBA p2s = new PEPAtoSBA(modelNode);
		try {
			p2s.parseModel();
		} catch(Exception e) {
			e.printStackTrace();
		}
		ArrayList<String> rules = new ArrayList<String>();
		for (String c : p2s.getPopulations().keySet()) {
			System.out.println(c);
			rules.add(c);
		}
		OptionsMap omap = new OptionsMap();
		omap.setValue(Parameter.Solver,
				OptionsMap.Solver.DOPR);
		//omap.setValue(Parameter.Absolute_Error, 1E-4);
		//omap.setValue(Parameter.Relative_Error, 1E-4);
		//omap.setValue(Parameter.Step_Size, 1E-3);
		omap.setValue(Parameter.Stop_Time, (double)10);
		omap.setValue(Parameter.Components, rules.toArray(new String[rules.size()]));
		
		SBAtoISBJava modelSBA = new SBAtoISBJava(p2s);
		modelSBA.generateISBJavaModel("name", true);
		modelSBA.initialiseSimulator(omap);
		Results r = null;
		r = modelSBA.runModel();
		for (int i = 0; i < r.getSpeciesOrdering().length; i++) {
			System.out.print(r.getSpeciesOrdering()[i]
					+ ((i == r.getSpeciesOrdering().length - 1) ? "\n" : ","));
		}
		int dataPoints = r.getSimpleTimeSeries().length;
		for (int i = 0; i < dataPoints; i++) {
			for (int j = 0; j < r.getSpeciesOrdering().length + 1; j++) {
				System.out.print(r.getSimpleTimeSeries()[i][j]
						+ ((j == r.getSpeciesOrdering().length) ? "\n" : ","));
			}
		}
	}

}
