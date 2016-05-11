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
	@FXML GridPane pane;
	@FXML ScrollPane scrollPane;
	@FXML Group graph;
	
	private DatabaseManager dbm;
	private static final int YSCALE = 5;
	private static final int XSCALE = 10;
    private final double MAX_SCALE = 100.0d;
    private final double MIN_SCALE = .1d;
    
	private EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {

			event.consume();
			
	    	if(event.isControlDown()) {
	            double delta = 1.2;
	            double scale = graph.getScaleY(); // currently we only use Y, same value is used for X
	            
	            if (event.getDeltaY() < 0) {
	                 scale /= Math.pow(delta, -event.getDeltaY()/20);
	            } 
	            
	            else {
	                 scale *= Math.pow(delta, event.getDeltaY()/20);
	            }

	            scale = clamp( scale, MIN_SCALE, MAX_SCALE);
	            double zoom = scale/MAX_SCALE;
	            System.out.println("Zoom percentage: " + zoom);
	            graph.setScaleY( scale);
	    		return;
	    	}
	    	
	        if (event.getDeltaY() < 0) {
	            scrollPane.setHvalue(Math.min(1,scrollPane.getHvalue()+0.0007));
	        }
	        else {
	            scrollPane.setHvalue(Math.max(0,scrollPane.getHvalue()-0.0007));
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
		
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		
		// Resize the scrollpane along with the window.
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
		draw();

	}
	

	
	
	
    public double clamp(double value, double min, double max) {
        if(Double.compare(value, min) < 0)
            return min;

        else if( Double.compare(value, max) > 0)
            return max;
        
        else
        return value;
    }
	
	/**
	 * Function to draw the Ribbons on the pane
	 */
	public void draw() {
		ArrayList<Integer> from = dbm.getDbReader().getAllFromId();
		ArrayList<Integer> to = dbm.getDbReader().getAllToId();
		ArrayList<Integer> counts = dbm.getDbReader().getAllCounts();
		ArrayList<Integer> xCoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> yCoords = dbm.getDbReader().getAllYCoord();
		
//		int maxX = XSCALE * xCoords.get(xCoords.size()-1);
//		int maxY = YSCALE * Collections.max(yCoords);
//		
//		graph.setPrefHeight(maxY);
//		graph.setPrefWidth(maxX);
		
		graph.translateYProperty().bind(scrollPane.heightProperty().divide(2));
		
		System.out.println(xCoords.get(xCoords.size()-1));
		
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			Path path = createPath(xCoords.get(fromId - 1), yCoords.get(fromId - 1), 
					xCoords.get(toId - 1), yCoords.get(toId - 1));
	        path.setStrokeWidth(0.1 + 0.1 * counts.get(i));
	        graph.getChildren().add(path);
		}
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
