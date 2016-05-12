package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;

import gui.phylogeny.InternalNewickNode;
import gui.phylogeny.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import newick.NewickTree;
import parsers.NewickTreeParser;

public class PhylogenyController implements Initializable, SetScreen {
	
	private static final int SPACING = 5;
	private static final int SCALE = 1;
	
	private ScreenManager screenManager;
	@FXML AnchorPane pane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		NewickTree tree = null;
		
//		try {
//			tree = NewickTreeParser.parse(new File("Data/TB10/340tree.rooted.TKK.nwk"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		tree = NewickTreeParser.parse("((B:75,(E:75,F:100,G:25):25,C:50):25,(D:25,Z:50,Y:25):25,A:100);");
//		tree = NewickTreeParser.parse("((A:100,B:75):25,C:50,D:25);");

		NewickNode node = getDrawableTree(tree);
		node.setTranslateX(100);
		node.setTranslateY(100);
//		node.setLayoutX(100);
//		node.setLayoutY(100);
		
		Group root = new Group();
        Scene scene = new Scene(root, 800, 600);
        
        int size = 10;
        int scale = 1000;
        
		root.getChildren().add(node);

        ScreenManager.currentStage.setScene(scene);
        ScreenManager.currentStage.show();

	}
	
	public NewickNode getDrawableTree(NewickTree tree) {
		int currentY = 0;
		
		NewickNode root = new InternalNewickNode();
		
		double fromY = currentY;
		double toY = 0;
		
		for(NewickTree child : tree.getChildren()) {
			NewickNode childNode = null;
			if(child.isLeaf()) {
				childNode = new ExternalNewickNode(child.getName());
			}
			else {
				childNode = getDrawableTree(child);
			}
			NewickEdge edge = new NewickEdge(root,childNode);
			root.getChildren().add(edge);
			root.getChildren().add(childNode);
			
			childNode.setTranslateX(SCALE*child.getDistance());
			childNode.setTranslateY(currentY);
			toY = currentY;
			
			System.out.println(childNode.boundsInLocalProperty().get().getHeight());
			currentY += SPACING + childNode.boundsInLocalProperty().get().getHeight();
		}
//		root.shiftRootNode((fromY+toY)/2);
		return root;
	}
	

//	public NewickNode getDrawableTree(NewickTree tree) {
//		if(tree.isLeaf()) {
//			return new ExternalNewickNode(tree.getName());
//		}
//		
//		NewickTree current = tree;
//		Stack<NewickTree> stack = new Stack<>();
//		
//		int currentX = 0;
//		int currentY = 0;
//		
//		while(!current.isLeaf()) {
//			stack.push(current);
//			currentX += current.getDistance();
//			current = current.getChildren().get(0);
//		}
//		
//		NewickTree parent = stack.pop();
//		
//		double fromY = currentY;
//		
//		NewickNode parentNode = new InternalNewickNode();
//
//		for(NewickTree child : parent.getChildren()) {
//			NewickNode childNode = getDrawableTree(child);
////			System.out.println(child.getName() + " " + currentX + "," + child.getDistance());
//			childNode.setTranslateX(child.getDistance()*SCALE);
//			childNode.setTranslateY(currentY);
//
//			parentNode.getChildren().add(childNode);
//			parentNode.getChildren().add(new NewickEdge(parentNode,childNode));
//			
//			currentY += SPACING + 2*childNode.getRootNodeOffset().get();
//		}
//		
//		double toY = currentY - SPACING;
//		
//		double offset = (fromY+toY)/2;
//		parentNode.shiftRootNode(offset);
//		
//		return parentNode;
//	}
	
	@Override
	public void setScreenDriver(ScreenManager screenPage) {
		this.screenManager = screenPage;
	}
}
