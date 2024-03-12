package uk.ac.ed.inf.pepa.cpt.searchEngine.tree.viewer;

import org.eclipse.swt.graphics.Image;

import java.util.List;

public interface IResultTreeNode {
	
	public String getName();
	public Image getImage();
	@SuppressWarnings("rawtypes")
	public List getChildren();
	public boolean hasChildren();
	public IResultTreeNode getParent();
	public void addChild(IResultTreeNode leaf);

}
