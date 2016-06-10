package gui.phylogeny.model;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;


/**
 * Class to represent a Path between two NewickNodes.
 */
public class NewickEdge extends Group {
	
	public boolean vertical;
	private Line line = new Line();
	private Line line2 = new Line();
	
	/**
	 * Create a Path from the current position to the position of the
	 * destination node. A vertical and horizontal segment are used to create a
	 * Line containing a 90 degree angle.
	 * 
	 * @param dst Destination node.
	 */
<<<<<<< HEAD:src/main/java/gui/phylogeny/NewickEdge.java
	public NewickEdge(NewickNode src, NewickNode dst) {
		line.startXProperty().bind(src.layoutXProperty());
		line.startYProperty().bind(src.layoutYProperty());
		line.endYProperty().bind(dst.translateYProperty());
		line.setStrokeWidth(1);
		
		line2.startXProperty().bind(src.layoutXProperty());
		line2.startYProperty().bind(dst.translateYProperty());
		line2.endXProperty().bind(dst.translateXProperty());
		line2.endYProperty().bind(dst.translateYProperty());
		
		this.getChildren().add(line);
		this.getChildren().add(line2);	
	}
	
	/**
	 * Sets edges to be coloured according to their lineage
	 * @param lineagecolour
	 */
	public void setColoured(Paint lineagecolour) {
		line.setStroke(lineagecolour);
		line2.setStroke(lineagecolour);
	}
	
	/**
	 * Unset colour of this node (makes node appear gray).
	 */
	public void unSetColoured() {
		line.setStroke(NewickColourMatching.getDeactivatedColour());
		line2.setStroke(NewickColourMatching.getDeactivatedColour());
=======
	
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
		
>>>>>>> refs/remotes/origin/master:src/main/java/gui/phylogeny/model/NewickEdge.java
	}
}
