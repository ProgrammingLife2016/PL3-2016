package gui.phylogeny;

import coordinates.Coordinate;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

public abstract class NewickNode extends Group {
	
	private static final int SIZE = 10;
	
	private Rectangle node = new Rectangle(0,0,SIZE,SIZE);
	private Coordinate coord = new Coordinate(0,0);
	
	private DoubleProperty xProperty = new SimpleDoubleProperty(coord.x);
	private DoubleProperty yProperty = new SimpleDoubleProperty(coord.y);
	
	public NewickNode() {
		this(0,0);
	}
	
	public NewickNode(int x, int y) {
		node = new Rectangle(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE);
		coord.x = x;
		coord.y = y;
		this.getChildren().add(node);
	}
	
	public int getX() {
		return coord.x;
	}
	
	public int getY() {
		return coord.y;
	}
	
	public void setX(int x) {
		coord.x = x;
		this.node.setX(x - SIZE / 2);
		this.setTranslateX(x);
	}

	public void setY(int y) {
		coord.y = y;
		this.node.setY(y - SIZE / 2);
		this.setTranslateY(y);
	}
	
	public DoubleProperty getXProperty() {
		return this.xProperty;
	}
	
	public DoubleProperty getYProperty() {
		return this.yProperty;
	}
	
	
}
