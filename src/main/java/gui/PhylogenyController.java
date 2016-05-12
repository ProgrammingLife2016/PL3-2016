package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;

import gui.phylogeny.NewickEdge;
import gui.phylogeny.NewickNode;
import newick.NewickTree;

/**
 * Controller class for the Phylogenetic tree view.
 */
public class PhylogenyController implements Initializable {
	
	private static final int SPACING = 10;
	private static final int SCALE = 50000;
	
	@FXML GridPane pane;
	@FXML ScrollPane scrollPane;
	private Group root;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		NewickNode node = getDrawableTree(Launcher.nwkTree);
		node.setTranslateX(100);
		node.setTranslateY(100);
		
		root = new Group();
		root.getChildren().add(node);
		scrollPane.setContent(root);
		
		// Resize the scrollpane along with the window.
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});

	}
	
	/**
	 * Returns a (drawable) {@link NewickNode} that represents the given
	 * {@link NewickTree}.
	 * 
	 * @param tree
	 *            The {@link NewickTree} to visualize
	 * @return A drawable {@link NewickNode} that represents the given tree.
	 */
	public NewickNode getDrawableTree(NewickTree tree) {
		
		if (tree == null) {
			return new NewickNode();
		}
		
		int currentY = 0;
		
		NewickNode root = new NewickNode();
		
		for (NewickTree child : tree.getChildren()) {
			NewickNode childNode = null;
			if (child.isLeaf()) {
				childNode = new NewickNode(child.getName());
			} else {
				childNode = getDrawableTree(child);
			}
			NewickEdge edge = new NewickEdge(childNode);
			root.getChildren().add(edge);
			root.getChildren().add(childNode);
			
			childNode.setTranslateX(SCALE * child.getDistance());
			childNode.setTranslateY(currentY);
			
			currentY += SPACING + childNode.boundsInLocalProperty().get().getHeight();
		}
		return root;
	}
}
