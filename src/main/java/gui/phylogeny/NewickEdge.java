package gui.phylogeny;

import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;

public class NewickEdge extends Path {

	public NewickEdge(NewickNode src, NewickNode dst) {
		MoveTo moveTo = new MoveTo();
		
		moveTo.xProperty().bind(dst.translateXProperty());
		moveTo.yProperty().bind(dst.translateYProperty().add(dst.getRootNodeOffset()));
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
