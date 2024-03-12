package uk.ac.ed.inf.pepa.eclipse.ui.view.abstractionview;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DisplayOption {
	SHOW_NONE(0, "Hide all activities"),
	SHOW_ALL(1, "Show all activities"),
	SHOW_PASSIVE(2, "Show passive activities"),
	SHOW_ACTIVE(3, "Show active activities"),
	SHOW_FASTEST(4, "Show fastest activities"),
	SHOW_SLOWEST(5, "Show slowest activities");
	
	private static final Map<Integer,DisplayOption> lookup = new HashMap<Integer,DisplayOption>();
	 
	static {
		for (DisplayOption option : EnumSet.allOf(DisplayOption.class)) {
			lookup.put(option.getIndex(), option);
		}
	}
	
	private int index;
	private String name;
	
	private DisplayOption(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String toString() {
		return name;
	}
	
	public static DisplayOption get(int index) {
		return lookup.get(index);
	}
	
}
