package gui.phylogeny;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

public abstract class NewickNode extends Group {
	
	private static final int SIZE = 10;

	private Rectangle node = new Rectangle(0 - SIZE / 2, 0 - SIZE / 2, SIZE, SIZE);

	private DoubleProperty xCoordinate = new SimpleDoubleProperty(0);
	private DoubleProperty yCoordinate = new SimpleDoubleProperty(0);
	
	public NewickNode() {
		this(0,0);
	}
	
	public NewickNode(int x, int y) {
		
		node.translateXProperty().bind(xCoordinate);
		node.translateYProperty().bind(yCoordinate);
		
		xCoordinate.set(x);
		yCoordinate.set(y);
		this.getChildren().add(node);
	}
	
	/**
	 * Shifts the rectangle of this NewickNode along the y-axis.
	 * @param y Distance to shift (y > 0 is a downwards shift)
	 */
	public void shiftRootNode(double y) {
		yCoordinate.set(y);
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
	
//	public void setX(int x) {
//		coord.x = x;
//		this.node.setX(x - SIZE / 2);
//		this.setTranslateX(x);
//	}
//
//	public void setY(int y) {
//		coord.y = y;
//		this.node.setY(y - SIZE / 2);
//		this.setTranslateY(y);
//	}
	
//	public DoubleProperty getXProperty() {
//		return this.xProperty;
//	}
//	
//	public DoubleProperty getYProperty() {
//		return this.yProperty;
//	}
	
	
}
