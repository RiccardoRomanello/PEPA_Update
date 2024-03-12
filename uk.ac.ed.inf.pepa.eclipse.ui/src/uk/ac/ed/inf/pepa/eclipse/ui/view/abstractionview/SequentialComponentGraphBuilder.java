/*******************************************************************************
 * Copyright (c) 2006, 2009 University of Edinburgh.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD Licence, which
 * accompanies this feature and can be downloaded from
 * http://groups.inf.ed.ac.uk/pepa/update/licence.txt
 *******************************************************************************/
package uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.zest.core.widgets.Graph;
import org.eclipse.zest.core.widgets.GraphConnection;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;
import org.eclipse.zest.layouts.LayoutAlgorithm;

import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayAction;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayComponent;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayState;
import uk.ac.ed.inf.pepa.ctmc.kronecker.KroneckerDisplayTransition;

/**
 * This is a bit of a hack to get around the limitations of zest. We provide a
 * method
 * 
 * @author msmith
 * 
 */
public class SequentialComponentGraphBuilder {

	private KroneckerDisplayComponent component;
	private Graph graph;
	private GraphLayout layoutAlgorithm = null;

	private HashMap<KroneckerDisplayState, GraphNode> nodeMap = new HashMap<KroneckerDisplayState, GraphNode>();
	private HashMap<KroneckerDisplayAction, GraphNode> actionNodeMap = new HashMap<KroneckerDisplayAction, GraphNode>();
	private ArrayList<GraphConnection> connectors = new ArrayList<GraphConnection>();

	private int fontSize = 9;

	private Font stateFont;
	private Font actionFont;

	public int getFontSize() {
		return fontSize;
	}

	public ArrayList<KroneckerDisplayState> getStates() {
		return new ArrayList<KroneckerDisplayState>(nodeMap.keySet());
	}
	
	public GraphLayout getLayoutAlgorithm() {
		return layoutAlgorithm;
	}
	
	public void setLayoutAlgorithm(GraphLayout layout) {
		this.layoutAlgorithm = layout;
		LayoutAlgorithm algorithm = layout.getAlgorithm();
		if (algorithm != null) {
			graph.setLayoutAlgorithm(algorithm, false);
		}
	}
	
	public void setFontSize(int size) {
		fontSize = size;
		Font oldStateFont = stateFont;
		Font oldActionFont = actionFont;
		stateFont = new Font(null, "Arial", fontSize + 2, SWT.NORMAL);
		actionFont = new Font(null, "Arial", fontSize, SWT.NORMAL);
		updateNodes();
		oldStateFont.dispose();
		oldActionFont.dispose();
	}

	public SequentialComponentGraphBuilder(KroneckerDisplayComponent component, Graph graph) {
		this.component = component;
		this.graph = graph;
		this.stateFont = new Font(null, "Arial", fontSize + 2, SWT.NORMAL);
		this.actionFont = new Font(null, "Arial", fontSize, SWT.NORMAL);
	}

	public void increaseFontSize() {
		Font oldStateFont = stateFont;
		Font oldActionFont = actionFont;
		fontSize++;
		stateFont = new Font(null, "Arial", fontSize + 2, SWT.NORMAL);
		actionFont = new Font(null, "Arial", fontSize, SWT.NORMAL);
		updateNodes();
		oldStateFont.dispose();
		oldActionFont.dispose();
	}

	public void decreaseFontSize() {
		if (fontSize > 1) {
			Font oldStateFont = stateFont;
			Font oldActionFont = actionFont;
			fontSize--;
			stateFont = new Font(null, "Arial", fontSize + 2, SWT.NORMAL);
			actionFont = new Font(null, "Arial", fontSize, SWT.NORMAL);
			updateNodes();
			oldStateFont.dispose();
			oldActionFont.dispose();
		}
	}

	private void updateNodes() {
		for (GraphNode node : nodeMap.values()) {
			node.setFont(stateFont);
		}
		for (GraphNode node : actionNodeMap.values()) {
			node.setFont(actionFont);
		}
	}

	public GraphNode getNode(KroneckerDisplayState state, boolean useShortNames) {
		GraphNode node = nodeMap.get(state);
		if (node == null) {
			node = new GraphNode(graph, SWT.NONE, state);
			node.setBackgroundColor(new Color(null, 255, 200, 100));
			nodeMap.put(state, node);
		}
		node.setText(state.getLabel(useShortNames));
		node.setFont(stateFont);
		return node;
	}

	public GraphNode getNode(KroneckerDisplayAction action, boolean useShortNames) {
		GraphNode node = actionNodeMap.get(action);
		if (node == null) {
			node = new GraphNode(graph, SWT.NONE, action);
			actionNodeMap.put(action, node);
		}
		node.setText(action.getLabel(useShortNames));
		node.setFont(actionFont);
		return node;
	}

	private GraphConnection addGraphConnection(GraphNode n1, GraphNode n2, double weight, Color connectionColor) {
		GraphConnection g = new GraphConnection(graph, SWT.NONE, n1, n2);
		g.setConnectionStyle(ZestStyles.CONNECTIONS_DIRECTED);
		g.setWeight(weight);
		g.setLineWidth(2);
		g.setLineColor(connectionColor);
		connectors.add(g);
		return g;
	}

	private void clearConnections() {
		for (GraphConnection connector : connectors) {
			connector.dispose();
		}
		connectors.clear();
	}

	private void deselectActions() {
		@SuppressWarnings("unchecked")
		List<GraphNode> selection = (List<GraphNode>) graph.getSelection();
		ArrayList<GraphNode> newSelection = new ArrayList<GraphNode>();
		for (GraphNode node : selection) {
			if (!actionNodeMap.containsValue(node)) {
				newSelection.add(node);
			}
		}
		GraphNode[] selectionArray = new GraphNode[newSelection.size()];
		newSelection.toArray(selectionArray);
		graph.setSelection(selectionArray);
	}

	private void clearActions() {
		// We need to deselect any action nodes first, because of a bug in Zest.
		deselectActions();
		for (GraphNode node : actionNodeMap.values()) {
			node.dispose();
		}
		actionNodeMap.clear();
	}

	public void build(DisplayOption option, boolean useShortNames) {
		clearConnections();
		clearActions();
		KroneckerDisplayTransition[] transitions = component.getTransitions();
		
		// Determine information before drawing the graph
		double max_rate = 0;
		double min_rate = 0;
		ArrayList<KroneckerDisplayTransition> curveConnectors = new ArrayList<KroneckerDisplayTransition>();
		for (int i = 0; i < transitions.length; i++) {
			KroneckerDisplayTransition transition = transitions[i];
			// Determine which transitions use curves rather than straight lines
			int size = curveConnectors.size();
			for (int j = i + 1; j < transitions.length; j++) {
				if (transition.isReverse(transitions[j])) {
					if (!curveConnectors.contains(transitions[j])) {
						curveConnectors.add(transitions[j]);
					}
				}
			}
			if (size < curveConnectors.size()) {
				curveConnectors.add(transition);
			}
			
			// Update the largest and smallest rates
			double rate = transition.getAction().getRate();
			if (rate > max_rate) {
				max_rate = rate;
			}
			if (rate >= 0 && (rate < min_rate || min_rate == 0)) {
				min_rate = rate;
			}
		}

		// Actually draw the graph
		for (int i = 0; i < transitions.length; i++) {
			KroneckerDisplayTransition transition = transitions[i];
			GraphNode s1 = getNode(transition.getStartState(), useShortNames);
			GraphNode s2 = getNode(transition.getEndState(), useShortNames);

			// Set the colour - green for passive, and a shade of red for active
			double rate = transition.getAction().getRate();
			Color connectionColour = null;
			if (rate < 0) {
				connectionColour = new Color(null, 0, 255, 0);
			} else {
				connectionColour = new Color(null, (int) ((rate / max_rate) * 255), 0, 0);
			}
			
			// Construct connections
			if (displayAction(option, rate, max_rate, min_rate)) {
				// Make the action node
				GraphNode actionNode = getNode(transition.getAction(), useShortNames);
				actionNode.setBackgroundColor(connectionColour);
				actionNode.setForegroundColor(new Color(null, 255, 255, 255));
				// Set the position of the action node
				double min_x = Math.min(s1.getLocation().x + s1.getSize().width, s2.getLocation().x + s2.getSize().width);
				double max_x = Math.max(s1.getLocation().x, s2.getLocation().x);
				double min_y = Math.min(s1.getLocation().y + s1.getSize().height, s2.getLocation().y + s2.getSize().height);
				double max_y = Math.max(s1.getLocation().y, s2.getLocation().y);
				int slope = (max_x == s1.getLocation().x && max_y == s1.getLocation().y) ||
				            (max_x == s2.getLocation().x && max_y == s2.getLocation().y) ? -1 : 1;
				double x = (min_x + max_x) / 2;
				double y = (min_y + max_y) / 2;
				double dx = max_x - min_x;
				double dy = max_y - min_y;
				double max_diff = Math.max(dx, dy);
				if (max_diff > 0) {
					dx /= max_diff;
					dy /= max_diff;
				}
				actionNode.setData(new Point(dy > 0.3 ? 1 : 0, dx > 0.3 ? slope : 0));
				// Draw the connections
				GraphConnection t1 = addGraphConnection(s1, actionNode, 0.8, connectionColour);
				GraphConnection t2 = addGraphConnection(actionNode, s2, 0.2, connectionColour);
				if (s1 == s2) {
					y -= 30 + fontSize;
					t1.setCurveDepth(20);
					t2.setCurveDepth(20);
				}
				actionNode.setLocation(x, y);
				if (!s1.isVisible() || !s2.isVisible()) {
					actionNode.setVisible(false);
					t1.setVisible(false);
					t2.setVisible(false);
				}
			} else {
				GraphConnection t = addGraphConnection(s1, s2, -1, connectionColour);
				if (s1 == s2 || curveConnectors.contains(transition)) {
					t.setCurveDepth(20);
				}
				if (!s1.isVisible() || !s2.isVisible()) {
					t.setVisible(false);
				}
			}
		}

		// Finally, we need to adjust any action nodes that are overlapping
		// This could be made more efficient, but it probably doesn't matter
		for (GraphNode g1 : actionNodeMap.values()) {
			Point location = g1.getLocation();
			Point axis = (Point)g1.getData();
			int xoffset = (int)(axis.x * (g1.getSize().width + 3));
			int yoffset = (int)(axis.y * (g1.getSize().height + 3));
			ArrayList<GraphNode> changedNodes = new ArrayList<GraphNode>();
			changedNodes.add(g1);
			int total_xoffset = 0;
			int total_yoffset = 0;
			for (GraphNode g2 : actionNodeMap.values()) {
				if (g1 == g2) continue;
				Point p = g2.getLocation();
				if (location.x == p.x && location.y == p.y) {
					g2.setLocation(p.x + xoffset, p.y + yoffset);
					changedNodes.add(g2);
					total_xoffset += xoffset;
					total_yoffset += yoffset;
					xoffset += (int)(axis.x * (g2.getSize().width + 3));
					yoffset += (int)(axis.y * (g2.getSize().height + 3));
				}
			}
			for (GraphNode changed : changedNodes) {
				int x = changed.getLocation().x;
				int y = changed.getLocation().y;
				changed.setLocation(x - (total_xoffset / 2), y - (total_yoffset / 2));
			}
			g1.setLocation(location.x - (g1.getSize().width / 2), location.y - (g1.getSize().height / 2));
		}
		
	}

	private boolean displayAction(DisplayOption option, double rate, double max_rate, double min_rate) {
		boolean display = false;
		switch (option) {
		case SHOW_NONE:
			display = false;
			break;
		case SHOW_ALL:
			display = true;
			break;
		case SHOW_PASSIVE:
			display = rate < 0;
			break;
		case SHOW_ACTIVE:
			display = rate > 0;
			break;
		case SHOW_FASTEST:
			display = rate == max_rate;
			break;
		case SHOW_SLOWEST:
			display = rate == min_rate;
			break;
		}
		return display;
	}

	public KroneckerDisplayComponent getComponent() {
		return component;
	}

	public Graph getGraph() {
		return graph;
	}

}