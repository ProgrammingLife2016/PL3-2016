package gui.phylogeny.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import gui.Launcher;
import parsers.XlsxParser;

public class NewickAlgorithm {
	
	ArrayList<String> genomeNames;

	/**
	 * HashMap containing the lineages of the specimens.
	 */
	private HashMap<String, String> lineages;
	
	/**
	 * Location of metadata.xlsx
	 */
	private static String xlsxpath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TB10" + File.separator + "metadata" + ".xlsx";
	
	private static final int SPACING = 10;
	private int scale;
	
	/**
	 * Updates the ArrayList with the names of the genomes from the database.
	 */
	public void updateGenomeNames() {
		genomeNames = Launcher.dbm.getDbReader().getGenomeNames();
	}
	
	/**
	 * Parse lineages of the specimens.
	 */
	public void parseLineages() {
		XlsxParser xlsxparser = new XlsxParser();
		xlsxparser.parse(xlsxpath);
		lineages = xlsxparser.getLineages();
	}
	
	/**
	 * Returns a (drawable) {@link NewickNode} that represents the given
	 * @param tree
	 * @return
	 */
	public NewickNode getDrawableTree(NewickTree tree) {
		updateGenomeNames();
		while (!isPruned(tree)) {
			pruneNewickTree(tree);
		}
		adjustScale(tree);
		return getDrawableTreeInner(tree);
	}
	
	/**
	 * Returns a (drawable) {@link NewickNode} that represents the given
	 * {@link NewickTree}.
	 * 
	 * @param tree
	 *            The {@link NewickTree} to visualize
	 * @return A drawable {@link NewickNode} that represents the given tree.
	 */
	public NewickNode getDrawableTreeInner(NewickTree tree) {
		
		if (tree == null) {
			return new NewickNode();
		}
		
		int currentY = 0;
		
		NewickNode root = new NewickNode();
		
		for (NewickTree child : tree.getChildren()) {
			updateGenomeNames();
			NewickNode childNode = null;
			if (child.isLeaf()) {
				String lineage = lineages.get(child.getName());
				childNode = new NewickNode(child.getName(), lineage);
				childNode.setIsLeaf(true);	
			} else {
				childNode = getDrawableTree(child);
			}
			if (child.getChildren().size() == 1) {
				childNode.hideRectangle();
			}
			
			NewickEdge edge = new NewickEdge(childNode);
			
			
			root.getChildren().add(edge);
			root.getChildren().add(childNode);
			
			childNode.setTranslateX(scale * child.getDistance());
			childNode.setTranslateY(currentY);
			
			currentY += SPACING + childNode.boundsInLocalProperty().get().getHeight();
		}
		return root;
	}
	
	/**
	 * Prunes the NewickTree of all genomes that are not in the database.
	 * @param tree
	 * @return
	 */
	public void pruneNewickTree(NewickTree tree) {
		
		if (tree == null) {
			return;
		}

		ArrayList<NewickTree> tobeRemoved = new ArrayList<NewickTree>();
		
		for (NewickTree child : tree.getChildren()) {
			
			if (child.isLeaf() 
					&& (genomeNames.contains(child.getName()) == false 
					|| child.getName() == null)) {
				tobeRemoved.add(child);
			} else {
				pruneNewickTree(child);
			}
		}
		
		for (NewickTree child : tobeRemoved) {
			tree.getChildren().remove(child);
		}
	}
	
	/**
	 * Adjusts the scale for the lines so that they dont get super long
	 * for a tree that isn't very big or visa versa.
	 * @param tree - the tree that determines the scale
	 */
	private void adjustScale(NewickTree tree) {
		if (countNodes(tree) <= 25) {
			scale = 5000;
		} else {
			scale = 50000;
		}
	}
	
	/**
	 * Checks to see if a tree is already pruned or not
	 * @param tree - the tree that needs to be checked
	 * @return will return true or false for if the input tree
	 * 		   is pruned or not
	 */
	private boolean isPruned(NewickTree tree) {

		for (NewickTree child : tree.getChildren()) {
			if (child.isLeaf() 
					&& (genomeNames.contains(child.getName()) == false 
					|| child.getName() == null)) {
				return false;
			} else {
				if (isPruned(child) == false) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Counts the number of nodes in a newick tree.
	 * @param tree
	 * @return
	 */
	private int countNodes(NewickTree tree) {
		int count = 1;
		for (NewickTree child : tree.getChildren()) {
			if (child.isLeaf()) {
				return 1;
			} else {
				count = 1 + countNodes(child);
			}
		}
		return count;
	}
}
