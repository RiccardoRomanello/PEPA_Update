package uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer;

public class Node extends ResultTreeNode {

	public Node(String name, IResultTreeNode node) {
		super(name, node);
	}

	@Override
	public boolean hasChildren() {
		if(this.children.size() > 0)
			return true;
		else 
			return false;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	public void addChild(IResultTreeNode leaf){
		this.children.add(leaf);
	}
	
	public void addChildToFront(IResultTreeNode leaf){
		this.children.add(0,leaf);
	}
}
