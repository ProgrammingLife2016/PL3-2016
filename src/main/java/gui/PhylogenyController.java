package gui;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

import gui.phylogeny.InternalNewickNode;
import gui.phylogeny.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Path;
import newick.NewickTree;
import parsers.NewickTreeParser;

public class PhylogenyController implements Initializable, SetScreen {
	
	private static final int SPACING = 20;
	
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
		tree = NewickTreeParser.parse("(A:200,B:100,C:150,D:50);");

		NewickNode node = getDrawableTree(tree);
//		node.translateX(100);
//		node.translateY(100);	
		
		Group root = new Group();
        Scene scene = new Scene(root, 800, 600);
        
        int size = 10;
        int scale = 1000;
        
		root.getChildren().add(node);

        ScreenManager.currentStage.setScene(scene);
        ScreenManager.currentStage.show();

	}
	

	public NewickNode getDrawableTree(NewickTree tree) {
		if(tree.isLeaf()) {
			return new ExternalNewickNode(tree.getName());
		}
		
		NewickTree current = tree;
		Stack<NewickTree> stack = new Stack<>();
		
		int currentX = 0;
		int currentY = 0;
		
		while(!current.isLeaf()) {
			stack.push(current);
			currentX += current.getDistance();
			current = current.getChildren().get(0);
		}
		
		NewickTree parent = stack.pop();
		
		double fromY = currentY;
		
		NewickNode parentNode = new InternalNewickNode();

		for(NewickTree child : parent.getChildren()) {
			NewickNode childNode = getDrawableTree(child);
			
			childNode.translateX(currentX + child.getDistance());
			childNode.translateY(currentY);
			parentNode.getChildren().add(childNode);
			parentNode.getChildren().add(new NewickEdge(parentNode,childNode));
			
			currentY += SPACING;
		}
		
		double toY = currentY - SPACING;
		
		parentNode.shiftRootNode((fromY+toY)/2);
		
		return parentNode;
	}
	
	@Override
	public void setScreenDriver(ScreenManager screenPage) {
		this.screenManager = screenPage;
	}
}
