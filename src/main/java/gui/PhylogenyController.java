package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import gui.phylogeny.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import newick.NewickTree;
import parsers.NewickTreeParser;

/**
 * Controller class for the Phylogenetic tree view.
 */
public class PhylogenyController implements Initializable, SetScreen {
	
	private static final int SPACING = 5;
	private static final int SCALE = 1000;
	
	@FXML AnchorPane pane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		NewickTree tree = null;
		
		try {
			tree = NewickTreeParser.parse(new File("Data/TB10/340tree.rooted.TKK.nwk"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		NewickNode node = getDrawableTree(tree);
		node.setTranslateX(100);
		node.setTranslateY(100);
		
		Group root = new Group();
        Scene scene = new Scene(root, 800, 600);
        
		root.getChildren().add(node);

        ScreenManager.currentStage.setScene(scene);
        ScreenManager.currentStage.show();

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
		int currentY = 0;
		
		NewickNode root = new NewickNode();
		
		for(NewickTree child : tree.getChildren()) {
			NewickNode childNode = null;
			if(child.isLeaf()) {
				childNode = new NewickNode(child.getName());
			}
			else {
				childNode = getDrawableTree(child);
			}
			NewickEdge edge = new NewickEdge(childNode);
			root.getChildren().add(edge);
			root.getChildren().add(childNode);
			
			childNode.setTranslateX(SCALE*child.getDistance());
			childNode.setTranslateY(currentY);
			
			currentY += SPACING + childNode.boundsInLocalProperty().get().getHeight();
		}
		return root;
	}
	
	@Override
	public void setScreenDriver(ScreenManager screenPage) {
		
	}
}
