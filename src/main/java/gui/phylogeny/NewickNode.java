package gui.phylogeny;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

public abstract class NewickNode extends Group {
	
	private static final int SIZE = 10;

	private Rectangle node = new Rectangle(0 - SIZE / 2, 0 - SIZE / 2, SIZE, SIZE);
	
	// Keeps track of the vertical distance between the upper left corner of the
	// complete group and the root Rectangle.
	private DoubleProperty rootNodeOffset = new SimpleDoubleProperty(0);
	
	public NewickNode() {
		this(0,0);
	}
	
	public NewickNode(int x, int y) {
		rootNodeOffset.bind(node.translateYProperty());
		this.translateXProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("TranslateXProperty: " + newValue);
		});
		this.translateYProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("TranslateYProperty: " + newValue);
		});
		node.translateXProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("NodeTranslateXProperty: " + newValue);
		});
		node.translateYProperty().addListener((observable, oldValue, newValue) -> {
			System.out.println("NodeTranslateYProperty: " + newValue);
		});
		rootNodeOffset.addListener((observable, oldValue, newValue) -> {
			System.out.println("RootNodeOffset: " + newValue);
		});
		
		this.setTranslateX(x);
		this.setTranslateY(y);
		this.getChildren().add(node);
	}
	
	/**
	 * Shifts the rectangle of this NewickNode along the y-axis.
	 * @param y Distance to shift (y > 0 is a downwards shift)
	 */
	public void shiftRootNode(double y) {
		node.setTranslateY(y);
//		yCoordinate.set(yCoordinate.get() + y);
//		rootNodeOffset.set(rootNodeOffset.get() + y);
	}
	
	public DoubleProperty getRootNodeOffset() {
		return rootNodeOffset;
	}
	
}
