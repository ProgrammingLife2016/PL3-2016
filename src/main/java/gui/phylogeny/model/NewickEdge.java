package gui.phylogeny.model;

import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;

/**
 * Class to represent a Path between two NewickNodes.
 */
public class NewickEdge extends Path {
	
	/**
	 * Create a Path from the current position to the position of the
	 * destination node. A vertical and horizontal segment are used to create a
	 * Line containing a 90 degree angle.
	 * 
	 * @param dst Destination node.
	 */
	
	public NewickEdge(NewickNode dst) {
		

		MoveTo moveTo = new MoveTo();
		moveTo.xProperty().bind(dst.translateXProperty());
		moveTo.yProperty().bind(dst.translateYProperty());

		this.getElements().add(moveTo);
		
		// Add a horizontal line from the current position to the x-coordinate
		// of the destination.
		HLineTo horizontal = new HLineTo();
		this.getElements().add(horizontal);
		
		// Add a vertical line from the current position to the y-coordinate of
		// the destination.
		VLineTo vertical = new VLineTo();
		this.getElements().add(vertical);
		
	}
}
