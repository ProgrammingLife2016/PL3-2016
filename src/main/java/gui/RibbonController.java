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


@SuppressWarnings("restriction")
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
    
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
//			System.out.println("Type: " + event.getEventType() + ", dx: " + event.getDeltaX() + ", dy: " + event.getDeltaY());
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
		
		innerGroup = drawRibbons();
		outerGroup = new Group(innerGroup);
		
		scrollPane.setContent(outerGroup);
		
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		
//		outerGroup.translateYProperty().bind(scrollPane.heightProperty().divide(2));
		
		// Resize the scrollpane along with the window.
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
		drawRibbons();
		innerGroup.requestLayout();
		outerGroup.requestLayout();
		scrollPane.requestLayout();

	}
	
	/**
	 * Function to draw the Ribbons on the pane
	 */
	public Group drawRibbons() {
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
	 * Draws a line between 2 points of segments.
	 * 
	 * @param from
	 * 			Segment from which path is drawn.
	 * @param to
	 * 			Segment to which path is drawn.	
	 * @return A Path through which the lines goes.
	 */
	private Path createPath(int fromX, int fromY, int toX, int toY) {
		MoveTo moveto = new MoveTo(XSCALE * fromX, YSCALE * fromY);
		LineTo lineto = new LineTo(XSCALE * toX , YSCALE * toY);
		Path path = new Path();
		path.getElements().addAll(moveto, lineto);
		return path;
	}
}
