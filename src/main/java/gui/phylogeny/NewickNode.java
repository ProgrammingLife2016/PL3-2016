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
	private double offset = 0;

	private DoubleProperty xCoordinate = new SimpleDoubleProperty(0);
	private DoubleProperty yCoordinate = new SimpleDoubleProperty(0);
	
	public NewickNode() {
		this(0,0);
	}
	
	public NewickNode(int x, int y) {
		
		this.translateXProperty().bind(xCoordinate);
		this.translateYProperty().bind(yCoordinate);
		
		xCoordinate.set(x);
		yCoordinate.set(y);
		this.getChildren().add(node);
	}
	
	/**
	 * Shifts the rectangle of this NewickNode along the y-axis.
	 * @param y Distance to shift (y > 0 is a downwards shift)
	 */
	public void shiftRootNode(double y) {
		node.setTranslateY(y);
//		yCoordinate.set(yCoordinate.get() + y);
		offset += y;
	}
	
	public DoubleProperty getXProperty() {
		return xCoordinate;
	}
	
	public DoubleProperty getYProperty() {
		return yCoordinate;
	}
	
	public double getY() {
		return yCoordinate.get();
	}
	
	public double getX() {
		return xCoordinate.get();
	}
	
	public void translateY(double dy) {
		yCoordinate.set(yCoordinate.get() + dy);
//		this.setTranslateY(dy-offset);
	}
	
	public void translateX(double dx) {
		xCoordinate.set(xCoordinate.get() + dx);
//		this.setTranslateX(dx);
	}	
	
}
