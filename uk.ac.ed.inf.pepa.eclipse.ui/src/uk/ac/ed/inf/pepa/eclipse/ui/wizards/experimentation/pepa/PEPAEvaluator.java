/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.pepa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import uk.ac.ed.inf.pepa.ctmc.derivation.IStateSpace;
import uk.ac.ed.inf.pepa.ctmc.solution.ISolver;
import uk.ac.ed.inf.pepa.ctmc.solution.OptionMap;
import uk.ac.ed.inf.pepa.ctmc.solution.SolverFactory;
import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.IEvaluator;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ISensibleNode;
import uk.ac.ed.inf.pepa.eclipse.ui.wizards.experimentation.ISetting;
import uk.ac.ed.inf.pepa.parsing.ASTFactory;
import uk.ac.ed.inf.pepa.parsing.ASTSupport;
import uk.ac.ed.inf.pepa.parsing.ModelNode;
import uk.ac.ed.inf.pepa.parsing.RateDefinitionNode;
import uk.ac.ed.inf.pepa.parsing.RateDoubleNode;
import uk.ac.ed.inf.pepa.tools.PepaTools;

public class PEPAEvaluator implements IEvaluator {

	private ISensibleNode[] nodes;

	private IPepaModel model;

	public PEPAEvaluator(IPepaModel model) {
		this.model = model;
		ArrayList<ISensibleNode> sensibleNodes = new ArrayList<ISensibleNode>();
		for (RateDefinitionNode n : model.getAST().rateDefinitions()) {
			final String name = n.getName().getName();
			sensibleNodes.add(new ISensibleNode() {

				public String getName() {
					return name;
				}

			});
		}
		Collections.sort(sensibleNodes, new Comparator<ISensibleNode>() {

			public int compare(ISensibleNode o1, ISensibleNode o2) {
				return o1.getName().compareTo(o2.getName());
			}

		});
		nodes = sensibleNodes.toArray(new ISensibleNode[sensibleNodes.size()]);
	}

	public ISensibleNode[] getSensibleNodes() {
		return nodes;
	}

	public IStateSpace doEvaluate(ISetting[] settings, int[] currentIndex)
			throws Exception {
		OptionMap map = model.getOptionMap();
		if (!(model instanceof IPepaModel))
			throw new IllegalStateException("Excepted PEPA model");
		IPepaModel pepaModel = (IPepaModel) model;
		ModelNode modelNode = (ModelNode) ASTSupport.copy(pepaModel.getAST());
		// apply changes
		for (int i = 0; i < settings.length; i++) {
			boolean found = false;
			for (RateDefinitionNode rd : modelNode.rateDefinitions()) {
				if (rd.getName().getName().equals(
						settings[i].getSensibleNode().getName())) {
					// change value
					RateDoubleNode newRate = ASTFactory.createRate();
					newRate.setValue(settings[i].getSetting(currentIndex[i]));
					rd.setRate(newRate);
					found = true;
					break;
				}
			}
			if (!found)
				throw new IllegalArgumentException("Setting not found");
		}
		IStateSpace ss = PepaTools.derive(map, modelNode, null, null);
		ISolver solver = SolverFactory.createSolver(ss, map);
		ss.setSolution(solver.solve(null));
		return ss;
	}

	public IProcessAlgebraModel getProcessAlgebraModel() {
		return model;
	}

}
