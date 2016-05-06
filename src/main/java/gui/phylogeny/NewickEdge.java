package gui.phylogeny;

import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;

public class NewickEdge extends Path {
	
	public NewickEdge(NewickNode src, NewickNode dst) {
		MoveTo moveTo = new MoveTo();

		// Set start coordinates of the path.
//		this.setTranslateX(src.getX());
//		this.setTranslateY(src.getX());
		
//		this.translateXProperty().bind(src.getXProperty());
//		this.translateYProperty().bind(src.getYProperty());

		// Set the destination of the path.
//		moveTo.setX(dst.getX());
//		moveTo.setY(dst.getY());

		
		moveTo.xProperty().bindBidirectional(dst.getXProperty());
		moveTo.yProperty().bindBidirectional(dst.getYProperty());
		this.getElements().add(moveTo);

		// Add a horizontal line from the current position to the x-coordinate
		// of the destination.
		HLineTo horizontal = new HLineTo();
		this.getElements().add(horizontal);

		// Add a vertical line from the current position to the y-coordinate of
		// the destination.
		VLineTo vertical = new VLineTo();
		this.getElements().add(vertical);
		
//		horizontal.xProperty().bindBidirectional(dst.translateXProperty());
//		vertical.yProperty().bindBidirectional(dst.translateXProperty());
	}
	
}
