package gui;

import db.DatabaseManager;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Controller class for the Ribbon screen/tab.
 */
public class RibbonController implements Initializable {
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	
	private Group innerGroup;
	private Group outerGroup;
	
	private static final int YSCALE = 5;
	private static final int XSCALE = 10;
    private static final double MAX_SCALE = 100.0d;
    private static final double MIN_SCALE = .1d;
    
	private DatabaseManager dbm;
    
	// Handles the scrollwheel actions
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			event.consume();
			
			// Ctrl down: zoom in/out
	    	if(event.isControlDown()) {
	    		double deltaY = event.getDeltaY();
	    		
	            double delta = 1.2;
	            double scale = innerGroup.getScaleY(); // currently we only use Y, same value is used for X
	            
	            if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					// Cut off the scale if it is bigger than the minimum
					// allowed scale
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				}

				else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					// Cut off the scale if it is bigger than the maximum
					// allowed scale
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
	            }
				
	            double zoom = scale/MAX_SCALE;
	            System.out.println("Zoom percentage: " + zoom);
	            innerGroup.setScaleY( scale);
	    		return;
	    	}
	    	
	    	// Ctrl not down: scroll left/right (horizontally) or up/down (vertically)
	    	double deltaY = event.getDeltaY();
	    	double deltaX = event.getDeltaX();
	    	
	        if (deltaY < 0) {
	            scrollPane.setHvalue(Math.min(1,scrollPane.getHvalue()+0.0007));
	        }
	        else if (deltaY > 0){
	            scrollPane.setHvalue(Math.max(0,scrollPane.getHvalue()-0.0007));
	        }
	        if (deltaX < 0) {
	        	scrollPane.setVvalue(Math.min(1, scrollPane.getVvalue()+0.05));
	        }
	        else if (deltaX > 0) {
	        	scrollPane.setVvalue(Math.max(0, scrollPane.getVvalue()-0.05));
	        }
	    }
	};
    
	
	/**
	 * function that gets executed when the matching fxml file is loaded.
	 * 
	 * The group is from within the FXML file. We use that group to add events and the pannable 
	 * canvas on which the drawing of the ribbon takes place.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.dbm = Launcher.dbm;
		
		// Inner group and outer group according to the ScrollPane JavaDoc.
		innerGroup = createRibbons();
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
		
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		
		// Resize the scrollpane along with the window.
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});

	}

	/**
	 * Creates all paths that make up the ribbons and returns a {@link Group}
	 * containing those paths.
	 * 
	 * @return A group containing the ribbons.
	 */
	public Group createRibbons() {
		Group res = new Group();
		ArrayList<Integer> from = dbm.getDbReader().getAllFromId();
		ArrayList<Integer> to = dbm.getDbReader().getAllToId();
		ArrayList<Integer> counts = dbm.getDbReader().getAllCounts();
		ArrayList<Integer> xCoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> yCoords = dbm.getDbReader().getAllYCoord();
		
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			Path path = createPath(xCoords.get(fromId - 1), yCoords.get(fromId - 1), 
					xCoords.get(toId - 1), yCoords.get(toId - 1));
	        path.setStrokeWidth(0.1 + 0.1 * counts.get(i));
	        res.getChildren().add(path);
		}
		return res;
	}
	
	/**
	 * Creates a path from (fromX,fromY) to (toX,toY).
	 * 
	 * @param fromX
	 *            X coordinate of the starting point.
	 * @param fromY
	 *            Y coordinate of the starting point.
	 * @param toX
	 *            X coordinate of the destination point.
	 * @param toY
	 *            Y coordinate of the destination point.
	 * @return A path from (fromX,fromY) to (toX,toY).
	 */
	private Path createPath(int fromX, int fromY, int toX, int toY) {
		MoveTo moveTo = new MoveTo(XSCALE * fromX, YSCALE * fromY);
		LineTo lineTo = new LineTo(XSCALE * toX, YSCALE * toY);
		Path path = new Path();
		path.getElements().addAll(moveTo, lineTo);
		return path;
	}
}
