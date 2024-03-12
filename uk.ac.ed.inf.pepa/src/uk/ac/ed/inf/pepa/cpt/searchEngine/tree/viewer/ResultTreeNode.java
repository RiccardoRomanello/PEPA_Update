package uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

public abstract class ResultTreeNode implements IResultTreeNode {
	
	protected String name;
	protected IResultTreeNode parent;
	protected ArrayList<IResultTreeNode> children;
	
	public ResultTreeNode(String name, IResultTreeNode parent){
		this.children = new ArrayList<IResultTreeNode>();
		this.name = name;
		this.parent = parent;
	}
	
	public Image getImage() {
		return null;
	}
	
	public abstract boolean hasChildren();
	
	public IResultTreeNode getParent() {
		return parent;
	}
	
	public ArrayList<IResultTreeNode> getChildren(){
		return children;
	}
	
	public abstract void addChild(IResultTreeNode leaf);
	
}
