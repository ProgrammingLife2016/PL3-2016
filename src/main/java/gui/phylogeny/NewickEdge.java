package gui.phylogeny;

import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;

/**
 * Class to represent a Path between two NewickNodes.
 */
public class NewickEdge extends Path {
	
	public boolean vertical;
	
	/**
	 * Create a Path from the current position to the position of the
	 * destination node. A vertical and horizontal segment are used to create a
	 * Line containing a 90 degree angle.
	 * 
	 * @param dst Destination node.
	 */
	public NewickEdge(NewickNode dst) {
		MoveTo moveTo = new MoveTo();
		
		moveTo.xProperty().bind(dst.translateXProperty().add(5));
		moveTo.yProperty().bind(dst.translateYProperty().add(5));
		this.getElements().add(moveTo);

		// Add a horizontal line from the current position to the x-coordinate
		// of the destination.
		HLineTo horizontal = new HLineTo();
		horizontal.setX(horizontal.getX() + 5);
		this.getElements().add(horizontal);

		// Add a vertical line from the current position to the y-coordinate of
		// the destination.
		VLineTo vertical = new VLineTo();
		vertical.setY(vertical.getY() + 5);
		this.getElements().add(vertical);
	}
}
