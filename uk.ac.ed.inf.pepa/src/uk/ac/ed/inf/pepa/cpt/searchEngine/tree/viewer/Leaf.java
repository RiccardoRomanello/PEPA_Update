package uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer;

public class Leaf extends ResultTreeNode {

	public Leaf(String name, IResultTreeNode parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void addChild(IResultTreeNode leaf) {
		
	}

}
