/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.statespaceexplorer;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uk.ac.ed.inf.pepa.eclipse.core.IPepaModel;
import uk.ac.ed.inf.pepa.eclipse.core.IProcessAlgebraModel;
import uk.ac.ed.inf.pepa.parsing.ASTSupport;

public class PepaModelPage extends ProcessAlgebraModelPage {

	public PepaModelPage(IProcessAlgebraModel model) {
		super(model);
	}

	// New version, no need for aggregation visits
	protected void resizeControl_(Table table) {
		GC gc = new GC(table);
		TableColumn tc = table.getColumn(0);
		tc.pack();
		int spacing = tc.getWidth();
		LinkedList<Integer> columnWidths = new LinkedList<Integer>();
		columnWidths.add(gc.textExtent(Integer.toString(model.getStateSpace().size())).x);
		Map<String, Set<String>> alphabets = ASTSupport.getProcessAlphabets(((IPepaModel) model).getAST());
		int sc = model.getStateSpace().getMaximumNumberOfSequentialComponents();
		for (int i = 0; i < sc; i++) {
			String name = model.getStateSpace().getLabel(0, i);
			int max = 0;
			for (String derivative : alphabets.get(name)) {
				max = Math.max(max, gc.textExtent(derivative).x);
			}
			columnWidths.add(max);
		}
		columnWidths.add(gc.textExtent("00.000000000000000E-00").x);
		int i = 0;
		for(TableColumn column : table.getColumns())
			column.setWidth(columnWidths.get(i++) + spacing);
	
	}

}
