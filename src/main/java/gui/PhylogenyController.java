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

		Group node = getDrawableTree(tree, 20, 10, 20);
		
//		Group node = new Group();
//		node.getChildren().add(new NewickEdge(new InternalNewickNode(10,10),new InternalNewickNode(50,50)));
		
		
//		NewickNode node1 = new InternalNewickNode();
//		NewickNode child1 = new ExternalNewickNode(40,0,"A");
//		NewickNode child2 = new ExternalNewickNode(60,40,"B");
//		Path edge1 = new NewickEdge(child1);
//		Path edge2 = new NewickEdge(child2);
//		
//		node1.getChildren().add(child1);
//		node1.getChildren().add(child2);
//		node1.getChildren().add(edge1);
//		node1.getChildren().add(edge2);
//		
//		node1.shiftRootNode(20);
//		
//		node1.setTranslateX(200);
//		node1.setTranslateY(200);
//		
		
		Group root = new Group();
        Scene scene = new Scene(root, 800, 600);
        
        int size = 10;
        int scale = 1000;
        
        
		root.getChildren().add(node);
//		root.setTranslateX(200);
//		root.setTranslateY(200);
//		root.getChildren().add(new NewickEdge(new Coordinate(100,100),new Coordinate(200,200)));
//        Coordinate center1 = new Coordinate(20,20);
//        Coordinate center2 = new Coordinate(20,40);
//        
//		Rectangle r = new Rectangle(center1.getX()-size/2, center1.getY()-size/2, size, size);
//		Rectangle r2 = new Rectangle(center2.getX()-size/2, center2.getY()-size/2, size, size);
//		
//		Group test = new Group();
//		Line line = new Line();
//		line.setStartX(center1.getX());
//		line.setStartY(center1.getY());
//		line.setEndX(center2.getX());
//		line.setEndY(center2.getY());
//		Rectangle r3 = new Rectangle(100,100,10,10);
//		test.getChildren().add(line);
//		test.getChildren().add(r3);
//		
//		root.getChildren().add(test);
//		root.getChildren().add(r);
//		root.getChildren().add(r2);

        ScreenManager.currentStage.setScene(scene);
        ScreenManager.currentStage.show();

	}
	
//	private NewickNode calculateCoordinates(NewickTree tree, int startX, int minY, int vSpacing) {
//		if(tree.isLeaf()) {
//			return new ExternalNewickNode(startX,minY,tree.getName());
//		}
//		
//		NewickTree current = tree;
//		Stack<NewickTree> stack = new Stack<>();
//		
//		NewickNode root = new InternalNewickNode(startX,0);
//		
//		int currentX = startX;
//		int currentY = minY;
//		
//		while(!current.isLeaf()) {
//			stack.push(current);
//			current = current.getChildren().get(0);
//			currentX += current.getDistance();
//		}
//		
//		NewickTree parent = stack.pop();
//		
//		int fromY = Integer.MAX_VALUE;
//		int toY = -1;
//		
//		NewickNode parentNode = new InternalNewickNode();
//
//		for(NewickTree child : parent.getChildren()) {
//			
////			NewickNode leaf = new ExternalNewickNode(currentX + (int) child.getDistance(), currentY,child.getName());
//			NewickNode leaf = calculateCoordinates(parent,currentX + (int) child.getDistance(), currentY, vSpacing);
//			
//			parentNode.getChildren().add(leaf);
//			
//			currentY += vSpacing;
//			if(leaf.getY() < fromY) {
//				fromY = leaf.getY();
//			}
//			if(leaf.getY() > toY) {
//				toY = leaf.getY();
//			}
//		}
//		
//		parentNode.setTranslateY((fromY+toY)/2);
//
//		return parentNode;
//		
//	}
//	
//	
	public NewickNode getDrawableTree(NewickTree tree, int startX, int minY, int vSpacing) {
		NewickTree current = tree;
		Stack<NewickTree> stack = new Stack<>();
		
		int currentX = startX;
		int currentY = 0;
		
		while(!current.isLeaf()) {
			stack.push(current);
			current = current.getChildren().get(0);
			currentX += current.getDistance();
		}
		
		NewickTree parent = stack.pop();
		
		double fromY = Integer.MAX_VALUE;
		double toY = -1;
		
		NewickNode parentNode = new InternalNewickNode();

		for(NewickTree child : parent.getChildren()) {
			NewickNode leaf = new ExternalNewickNode(currentX + (int) child.getDistance(), currentY,child.getName());
			
			parentNode.getChildren().add(leaf);
			parentNode.getChildren().add(new NewickEdge(parentNode,leaf));
			
			currentY += vSpacing;
			if(leaf.getY() < fromY) {
				fromY = leaf.getY();
			}
			if(leaf.getY() > toY) {
				toY = leaf.getY();
			}
		}
		
		parentNode.shiftRootNode(100);

		parentNode.setTranslateX(100);
		parentNode.setTranslateY(100);
		
		return parentNode;
	}
	
	
//	public void getDrawableTree(Group root, NewickTree tree, int startX, int minY, int vSpacing) {
		
//		NewickNode parent = new InternalNewickNode();
//		NewickNode child1 = new ExternalNewickNode(10,10,"test");
//		NewickNode child2 = new ExternalNewickNode(40,40,"test2");
//		NewickEdge edge1 = new NewickEdge(parent,child1);
//		NewickEdge edge2 = new NewickEdge(parent,child2);
//		
//		
//		parent.getChildren().add(child1);
//		parent.getChildren().add(child2);
//		parent.getChildren().add(edge1);
//		parent.getChildren().add(edge2);
//		
//		parent.setTranslateX(100);
//		parent.setTranslateY(50);
//		
//		root.getChildren().add(parent);
		
		
//		NewickTree current = tree;
//		Stack<NewickTree> stack = new Stack<>();
//		
//		int currentX = startX;
//		int currentY = minY;
//		
//		while(!current.isLeaf()) {
//			stack.push(current);
//			current = current.getChildren().get(0);
//			currentX += current.getDistance();
//		}
//		
//		NewickTree parent = stack.pop();
//		List<NewickNode> leafs = new LinkedList<>();
//		
//		int fromY = Integer.MAX_VALUE;
//		int toY = -1;
//		
//		NewickNode parentNode = new InternalNewickNode();
//
//		for(NewickTree child : parent.getChildren()) {
//			NewickNode leaf = new ExternalNewickNode(currentX + (int) child.getDistance(), currentY,child.getName());
//			
//			parentNode.getChildren().add(leaf);
//			parentNode.getChildren().add(new NewickEdge(leaf));
//			
//			currentY += vSpacing;
//			if(leaf.getY() < fromY) {
//				fromY = leaf.getY();
//			}
//			if(leaf.getY() > toY) {
//				toY = leaf.getY();
//			}
//		}
//		
//		parentNode.setX(200);
//		parentNode.setY(200);
//		root.getChildren().add(parentNode);
//		for(NewickNode leaf : leafs) {
//			NewickEdge edge = new NewickEdge(parentNode,leaf);
//			parentNode.getChildren().add(edge);
//		}
//		
//		NewickTree parent = stack.pop();
//		List<NewickNode> leafs = new LinkedList<>();
//		
//		int fromY = Integer.MAX_VALUE;
//		int toY = -1;
//		
//		NewickNode parentNode = new InternalNewickNode();
//
//		for(NewickTree child : parent.getChildren()) {
//			NewickNode leaf = new ExternalNewickNode(currentX + (int) child.getDistance(), currentY,child.getName());
//			leafs.add(leaf);
//			parentNode.getChildren().add(leaf);
//			currentY += vSpacing;
//			if(leaf.getY() < fromY) {
//				fromY = leaf.getY();
//			}
//			if(leaf.getY() > toY) {
//				toY = leaf.getY();
//			}
//		}
//		
//		parentNode.setX(currentX);
//		parentNode.setY((fromY+toY) / 2);
//		root.getChildren().add(parentNode);
//		for(NewickNode leaf : leafs) {
//			NewickEdge edge = new NewickEdge(parentNode,leaf);
//			parentNode.getChildren().add(edge);
//		}
		
		
//		if (tree.isLeaf()) {
//
//			NewickLeaf leaf = new NewickLeaf(coord.x, coord.y, tree.getName());
//			group.getChildren().add(leaf);
//
//			return coord;
//		}
//		
//		Coordinate current = coord;
//		double averageY = 0;
//		for (NewickTree child : tree.getChildren()) {
//			averageY += current.y;
//			current = getDrawableTree(group, child, current, vSpacing);
//			current.y += vSpacing;
//			
//		}
//		averageY /= tree.getChildren().size();
//		group.getChildren().add(new NewickCell(current.getX() - 20, (int) averageY));
//
//		return current;

//	}
	
	@Override
	public void setScreenDriver(ScreenManager screenPage) {
		this.screenManager = screenPage;
	}
}
