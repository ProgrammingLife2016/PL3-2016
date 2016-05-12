package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.fxml.Initializable;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import db.DatabaseManager;

/**
 * @author hugokooijman
 *
 * Class containing all the logic involving the graph view. It reads out database tables,
 * stores required information in memory and adds the required to the Group "graphpane",
 * which is defined in the GraphLevel.fxml file. This will result in a visual representation
 * of all segments that are loaded into it.
 */
public class GraphController implements Initializable {
	
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	
	private Group innerGroup;
	private Group outerGroup;
	
    private static final double MAX_SCALE = 100.0d;
    private static final double MIN_SCALE = .1d;
    
	private DatabaseManager dbm;
	
	/**
	 * Map of all GraphSegments.
	 */
	private HashMap<Integer, GraphSegment> segments;
	
	/**
	 * 5 lists of required data for constructing GraphSegments.
	 */
	private ArrayList<Integer> from;
	private ArrayList<Integer> to;
	private ArrayList<Integer> graphxcoords;
	private ArrayList<Integer> graphycoords;
	private ArrayList<String> segmentdna;
	
	// Handles the scrollwheel actions
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			event.consume();

			// Ctrl down: zoom in/out
			if (event.isControlDown()) {
				
				double deltaY = event.getDeltaY();

				double delta = 1.2;
				double scale = innerGroup.getScaleY();

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					// Cut off the scale if it is bigger than the minimum
					// allowed scale
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					// Cut off the scale if it is bigger than the maximum
					// allowed scale
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}

				innerGroup.setScaleY(scale);
				innerGroup.setScaleX(scale);
				return;
			}

			// Ctrl not down: scroll left/right (horizontally) or up/down
			// (vertically)
			double deltaY = event.getDeltaY();
			double deltaX = event.getDeltaX();

			if (deltaY < 0) {
				scrollPane.setHvalue(Math.min(1, scrollPane.getHvalue() + 0.0007));
			} else if (deltaY > 0) {
				scrollPane.setHvalue(Math.max(0, scrollPane.getHvalue() - 0.0007));
			}
			if (deltaX < 0) {
				scrollPane.setVvalue(Math.min(1, scrollPane.getVvalue() + 0.05));
			} else if (deltaX > 0) {
				scrollPane.setVvalue(Math.max(0, scrollPane.getVvalue() - 0.05));
			}
		}
	};
	
	//Handler for zooming in/out with the keyboard
	private final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
		
		@Override
		public void handle(KeyEvent event) {
			String character = event.getCharacter();
			if (!event.isControlDown()) {
				return;
			}

			double delta = 1.2;
			double scale = innerGroup.getScaleY();

			// Zoom in when ctrl and the "+" or "+/=" key is pressed.
			if (character.equals("+") || character.equals("=")) {
				scale *= delta;

				// Cut off the scale if it is bigger than the maximum
				// allowed scale
				scale = scale > MAX_SCALE ? MAX_SCALE : scale;
			} else if (character.equals("-")) {
				scale /= delta;
				// Cut off the scale if it is bigger than the minimum
				// allowed scale
				scale = scale < MIN_SCALE ? MIN_SCALE : scale;
			} else {
				return;
			}

			innerGroup.setScaleY(scale);
			innerGroup.setScaleX(scale);
			return;
		}
	};

	/**
	 * Initialize fxml file.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.dbm = Launcher.dbm;
		loadSegmentData();
		constructSegmentMap();
		
		// Inner group and outer group according to the ScrollPane JavaDoc.
		innerGroup = getGraph();
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
		
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		scrollPane.addEventFilter(KeyEvent.KEY_TYPED, keyEventHandler);
		
		// Resize the scrollpane along with the window.
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
	}
	
	/**
	 * Load in all necessary information from the database.
	 */
	private void loadSegmentData() {
		from = dbm.getDbReader().getAllFromId();
		to = dbm.getDbReader().getAllToId();
		graphxcoords = scaleRibbonToGraphCoordsX(dbm.getDbReader().getAllXCoord());
		graphycoords = scaleRibbonToGraphCoordsY(dbm.getDbReader().getAllYCoord());
		
		//The loadfactor of a standard java hash map is 0.75. By setting the hash map size
		//this way, less memory is wasted by the hashmap, for not doubling storage capacity.
		segments = new HashMap<Integer, GraphSegment>((int) Math.ceil(to.size() / 0.75));
		
		segmentdna = new ArrayList<String>();
		for (int i = 1; i <= to.get(to.size() - 1); i++) {
			segmentdna.add(dbm.getDbReader().getContent(i));
		}
	}
	
	/**
	 * Creates a hash map of all segments, by storing their information in GraphSegment
	 * objects.
	 */
	public void constructSegmentMap() {
		int linkpointer = 1;
		
		//Loop over all segment id's.
		for (int i = 1; i <= to.get(to.size() - 1); i++) {
			int childcount = 0;
			
			//Figure out amount of childs of segment.
			while (linkpointer <= from.size() && i == from.get(linkpointer - 1)) {
				childcount++;
				linkpointer++;
			}
			linkpointer -= childcount;
			
			GraphSegment segment = new GraphSegment(i, childcount,
					segmentdna.get(i - 1).toCharArray(), graphxcoords.get(i - 1),
					graphycoords.get(i - 1));
			
			int childindex = 0;
			while (linkpointer <= from.size() && i == from.get(linkpointer - 1)) {
				segment.getSegmentChildren()[childindex] = to.get(linkpointer - 1);
				linkpointer++;
				childindex++;
			}
			segments.put(i, segment);
		}
	}
	
	/**
	 * Uses the created hash map of segments to create a drawable Group
	 * containing a visual representation of the segments, such as where they
	 * are located, how they are related and what their DNA strand is.
	 */
	public Group getGraph() {
		Group res = new Group();
		res.getChildren().add(getGraphEdges());
		res.getChildren().add(getGraphSegments());
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
	private Path drawPath(double fromX, double fromY, double toX, double toY) {
		MoveTo moveto = new MoveTo(fromX, fromY);
		LineTo lineto = new LineTo(toX , toY);
		Path path = new Path();
		path.getElements().addAll(moveto, lineto);
		return path;
	}
	
	/**
	 * Returns a group containing the edges between all the segment coordinates.
	 */
	private Group getGraphEdges() {
		Group res = new Group();
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			GraphSegment fromsegment = segments.get(fromId);
			GraphSegment tosegment = segments.get(toId);
			Path path = this.drawPath(fromsegment.getLayoutX() + fromsegment.getRadius(),
					fromsegment.getLayoutY() + fromsegment.getRadius(),
					tosegment.getLayoutX() + tosegment.getRadius(),
					tosegment.getLayoutY() + tosegment.getRadius());
	        path.setStrokeWidth(1);
	        res.getChildren().add(path);
		}
		return res;
	}
	
	/**
	 * Returns a group containing all GraphSegments on the segment coordinates.
	 */
	private Group getGraphSegments() {
		Group res = new Group();
		for (int i = 1; i <= segments.size(); i++) {
			res.getChildren().add(segments.get(i));
		}
		return res;
	}
	
	/**
	 * Scales and re-uses the x-coordinates calculated for the ribbon visualization.
	 * 
	 * @param xcoords
	 * 			x-coordinates of ribbons.
	 * @return xcoords
	 * 			x-coordinates of graph segments.
	 */
	public ArrayList<Integer> scaleRibbonToGraphCoordsX(ArrayList<Integer> xcoords) {
		for (int i = 0; i < xcoords.size(); i++) {
			int newc = xcoords.remove(i) * 100;
			xcoords.add(i, newc);
		}
		return xcoords;
	}
	
	/**
	 * Scales and re-uses the x-coordinates calculated for the ribbon visualization.
	 * 
	 * @param ycoords
	 * 			y-coordinates of ribbons.
	 * @return ycoords
	 * 			y-coordinates of graph segments.
	 */
	public ArrayList<Integer> scaleRibbonToGraphCoordsY(ArrayList<Integer> ycoords) {
		for (int i = 0; i < ycoords.size(); i++) {
			int newc = ycoords.remove(i) * 50;
			ycoords.add(i, newc);
		}
		return ycoords;
	}
}
