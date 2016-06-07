package gui.phylogeny;

import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;

/**
 * Class to represent a Path between two NewickNodes.
 */
public class NewickEdge extends Group {
	
	public boolean vertical;
	
	/**
	 * Create a Path from the current position to the position of the
	 * destination node. A vertical and horizontal segment are used to create a
	 * Line containing a 90 degree angle.
	 * 
	 * @param dst Destination node.
	 */
	public NewickEdge(NewickNode src, NewickNode dst) {
		Line line = new Line();
		
		line.startXProperty().bind(src.layoutXProperty());
		line.startYProperty().bind(src.layoutYProperty());
		line.endYProperty().bind(dst.translateYProperty());
		line.setStrokeWidth(1);
		
		Line line2 = new Line();
		line2.startXProperty().bind(src.layoutXProperty());
		line2.startYProperty().bind(dst.translateYProperty());
		line2.endXProperty().bind(dst.translateXProperty());
		line2.endYProperty().bind(dst.translateYProperty());
		
		this.getChildren().add(line);
		this.getChildren().add(line2);
		
//		MoveTo moveTo = new MoveTo();
//		
//		moveTo.xProperty().bind(dst.getRectangle().translateXProperty().add(5));
//		moveTo.yProperty().bind(dst.getRectangle().translateYProperty().add(5));
//		this.getElements().add(moveTo);
//
//		// Add a horizontal line from the current position to the x-coordinate
//		// of the destination.
//		HLineTo horizontal = new HLineTo();
//		horizontal.setX(horizontal.getX() + 5);
//		this.getElements().add(horizontal);
//
//		// Add a vertical line from the current position to the y-coordinate of
//		// the destination.
//		VLineTo vertical = new VLineTo();
//		vertical.setY(vertical.getY() + 5);
//		this.getElements().add(vertical);
	}
}
