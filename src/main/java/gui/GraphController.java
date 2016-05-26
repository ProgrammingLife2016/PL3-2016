package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;

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
	
    private static final double MAX_SCALE = 3.0d;
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
	
	/**
	 * Handles the scroll wheel event handler for zooming in and zooming out.
	 */
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			event.consume();
			if (event.isControlDown()) {
				double deltaY = event.getDeltaY();
				double delta = 1.2;
				double scale = innerGroup.getScaleX();
				double barValue = scrollPane.getHvalue();

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}
				innerGroup.setScaleX(scale);
				scrollPane.setHvalue(barValue);
				return;
			}

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
	
	/**
	 * Event handler for zooming in and out using the keyboard instead of the scroll wheel.
	 */
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
		}
	};

	/**
	 * Initialize fxml file.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		updateView();	
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		scrollPane.addEventFilter(KeyEvent.KEY_TYPED, keyEventHandler);
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
		scrollPane.setHvalue(0);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		
		double maxY = dbm.getDbReader().getMaxYCoord();
		innerGroup.setScaleY(720.0 / maxY);
		innerGroup.setScaleX(0.4);
	}
	
	/**
	 * Updates the view. Used when changing database files so the graph
	 * will have to adjust to the new file.
	 */
	public void updateView() {
		this.dbm = Launcher.dbm;
		loadSegmentData();
		constructSegmentMap();
		innerGroup = getGraph();
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
	}
	
	/**
	 * Load in all necessary information from the database.
	 */
	private void loadSegmentData() {
		
		from = dbm.getDbReader().getAllFromId();
		to = dbm.getDbReader().getAllToId();
		graphxcoords = dbm.getDbReader().getAllXCoord();
		graphycoords = dbm.getDbReader().getAllYCoord();
		segments = new HashMap<Integer, GraphSegment>((int) Math.ceil(to.size() / 0.75));
		segmentdna = new ArrayList<String>();
		
		for (int i = 1; i <= dbm.getDbReader().countSegments(); i++) {
			segmentdna.add(dbm.getDbReader().getContent(i));
		}
	}
	
	/**
	 * Creates a hash map of all segments, by storing their information in GraphSegment
	 * objects.
	 */
	public void constructSegmentMap() {
		int linkpointer = 1;
		
		for (int i = 1; i <= dbm.getDbReader().countSegments(); i++) {
			int childcount = 0;
			
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
	 * Returns a group containing the edges between all the segment coordinates.
	 */
	private Group getGraphEdges() {
		Group res = new Group();
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			GraphSegment fromsegment = segments.get(fromId);
			GraphSegment tosegment = segments.get(toId);
			double fromX = fromsegment.getLayoutX() + 2 * Math.log(fromsegment.getContentSize()) 
				+ fromsegment.getRadius();
			double toX = tosegment.getLayoutX() + 2 * Math.log(tosegment.getContentSize()) 
				+ tosegment.getRadius();
			
			Line line = new Line(fromX, fromsegment.getLayoutY() + fromsegment.getRadius(),
					toX, tosegment.getLayoutY() + tosegment.getRadius());
	        line.setStrokeWidth(1);
	        res.getChildren().add(line);
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
