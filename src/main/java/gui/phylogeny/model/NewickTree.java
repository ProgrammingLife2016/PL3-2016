package gui.phylogeny.model;

import java.util.ArrayList;
import java.util.List;

import gui.Launcher;

/**
 * 
 * Class to represent a Newick Tree
 *
 */
public class NewickTree {
	private String name = null;
	private double distance = 0;
	private List<NewickTree> children = new ArrayList<>();
	
	public List<NewickTree> getChildren() {
		return children;
	}
	
	public void addChild(NewickTree child) {
		children.add(child);
	}
	
	public boolean isLeaf() {
		return children.isEmpty();
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public String toString() {
		if (isLeaf()) {
			return name + ":" + distance;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (NewickTree tree : children) {
			sb.append(tree.toString() + ",");
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}
	
	
}
