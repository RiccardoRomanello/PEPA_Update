package uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.DirectedGraphLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.HorizontalShift;
import org.eclipse.zest.layouts.algorithms.HorizontalTreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.VerticalLayoutAlgorithm;

public enum GraphLayout {
	SPRING_LAYOUT      (0, "Spring Layout"),
	TREE_LAYOUT        (1, "Tree Layout"),
	HORIZ_TREE_LAYOUT  (2, "Horizontal Tree Layout"),
	RADIAL_LAYOUT      (3, "Radial Layout"),
	GRID_LAYOUT        (4, "Grid Layout"),
	HORIZ_SHIFT_LAYOUT (5, "Horizontal Shift Layout"),
	DAG_LAYOUT         (6, "Directed Graph Layout"),
	HORIZ_LAYOUT       (7, "Horizontal Layout"),
	VERT_LAYOUT        (8, "Vertical Layout");
	
	private static final Map<Integer,GraphLayout> lookup = new HashMap<Integer,GraphLayout>();
	
	static {
		for (GraphLayout alg : EnumSet.allOf(GraphLayout.class)) {
			lookup.put(alg.getIndex(), alg);
		}
	}
	 
	private final int index;
	private final String name;
	
	private GraphLayout(int index, String name) {
		this.index = index;
		this.name = name;
	}
	 
	public String toString() {
		return name;
	}
	 
	public int getIndex() {
		return index;
	}
	 
	public LayoutAlgorithm getAlgorithm() {
		int globalOptions = LayoutStyles.NO_LAYOUT_NODE_RESIZING;
		LayoutAlgorithm alg = null;
		switch (this) {
			case SPRING_LAYOUT:
		 		alg = new SpringLayoutAlgorithm(globalOptions | LayoutStyles.ENFORCE_BOUNDS);
				((SpringLayoutAlgorithm)alg).setIterations(3000);
				break;
		 	case TREE_LAYOUT:
		 		alg = new TreeLayoutAlgorithm(globalOptions);
		 		break;
		 	case HORIZ_TREE_LAYOUT:
		 		alg = new HorizontalTreeLayoutAlgorithm(globalOptions);
		 		break;
		 	case RADIAL_LAYOUT:
		 		alg = new RadialLayoutAlgorithm(globalOptions);
		 		break;
		 	case GRID_LAYOUT:
		 		alg = new GridLayoutAlgorithm(globalOptions);
		 		break;
		 	case HORIZ_SHIFT_LAYOUT:
		 		alg = new HorizontalShift(globalOptions);
		 		break;
		 	case DAG_LAYOUT:
		 		alg = new DirectedGraphLayoutAlgorithm(globalOptions);
		 		break;
		 	case HORIZ_LAYOUT:
		 		alg = new HorizontalLayoutAlgorithm(globalOptions);
		 		break;
		 	case VERT_LAYOUT:
		 		alg = new VerticalLayoutAlgorithm(globalOptions);
		 		break;
		}
		return alg;
	}
	 
	public static GraphLayout get(int index) {
		return lookup.get(index);
	}
	 
}
