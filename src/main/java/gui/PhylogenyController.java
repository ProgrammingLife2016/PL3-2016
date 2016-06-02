package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import gui.phylogeny.NewickEdge;
import gui.phylogeny.NewickNode;
import newick.NewickTree;

/**
 * Controller class for the Phylogenetic tree view.
 */
public class PhylogenyController implements Initializable {
	
	private static final int SPACING = 10;
	private static final int SCALE = 50000;
	
	ArrayList<String> genomeNames;
	
	@FXML GridPane pane;
	@FXML ScrollPane scrollPane;
	private Group root;
	
    private static final double MAX_SCALE = 100.0d;
    private static final double MIN_SCALE = .1d;
	
	/**
	 * Handles the scroll wheel event for the phylogenetic view.
	 */
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			event.consume();

			if (event.isControlDown()) {
				
				double deltaY = event.getDeltaY();
				double delta = 1.2;
				double scale = root.getScaleY();

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}

				root.setScaleY(scale);
				root.setScaleX(scale);
				return;
			}

			double deltaY = event.getDeltaY();
			double deltaX = event.getDeltaX();

			if (deltaY < 0) {
				scrollPane.setHvalue(Math.min(1, scrollPane.getHvalue() + 0.0007));
			} else if (deltaY > 0) {
				scrollPane.setHvalue(Math.max(0, scrollPane.getHvalue() - 0.0007));
			}
			if (deltaX < 0) {
				scrollPane.setVvalue(Math.min(1, scrollPane.getVvalue() + 0.05));
			} else if (deltaX > 0) {
				scrollPane.setVvalue(Math.max(0, scrollPane.getVvalue() - 0.05));
			}
		}
	};
	
	/**
	 * Event handler for keyboard events with the phylogenetic view.
	 */
	private final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
		
		@Override
		public void handle(KeyEvent event) {
			String character = event.getCharacter();
			if (!event.isControlDown()) {
				return;
			}

			double delta = 1.2;
			double scale = root.getScaleY();

			if (character.equals("+") || character.equals("=")) {
				scale *= delta;

				scale = scale > MAX_SCALE ? MAX_SCALE : scale;
			} else if (character.equals("-")) {
				scale /= delta;
				scale = scale < MIN_SCALE ? MIN_SCALE : scale;
			} else {
				return;
			}

			root.setScaleY(scale);
			root.setScaleX(scale);
		}
	};
	
	/**
	 * Initializes the phylogeny tree. Creating the basis for the
	 * phylogenetic tree to be created.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		NewickNode node = getDrawableTree(Launcher.nwkTree);
		node.setTranslateX(100);
		node.setTranslateY(100);
		
		root = new Group();
		root.getChildren().add(node);
		scrollPane.setContent(root);
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		scrollPane.addEventFilter(KeyEvent.KEY_TYPED, keyEventHandler);
		
		// Resize the scrollpane along with the window.
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefHeight(newValue.getHeight());
		    scrollPane.setPrefWidth(newValue.getWidth());
		});
	}
	
	/**
	 * Updates the ArrayList with the names of the genomes from the database.
	 */
	public void updateGenomeNames() {
		genomeNames = Launcher.dbm.getDbReader().getGenomeNames();
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
	 * Returns a (drawable) {@link NewickNode} that represents the given
	 * @param tree
	 * @return
	 */
	public NewickNode getDrawableTree(NewickTree tree) {
		updateGenomeNames();
		while (!isPruned(tree)) {
			pruneNewickTree(tree);
		}
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
				childNode = new NewickNode(child.getName());
				
			} else {
				childNode = getDrawableTree(child);
			}
			if (child.getChildren().size() == 1) {
				childNode.hideRectangle();
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
