package gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;


import javafx.scene.paint.Paint;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import parsers.XlsxParser;
import db.DatabaseManager;
import gui.phylogeny.NewickColourMatching;

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
	private ScrollPane otherPane;
	
//	@FXML private CheckBox checkboxSnp;
//	@FXML private CheckBox checkboxInsert;
	
	private Group innerGroup;
	private Group outerGroup;
	private Group otherGroup;
	
	private static final double MAX_SCALE = 1.0d;
    private static final double MIN_SCALE = .003d;
    
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
	 * Location of metadata.xlsx
	 */
	private static String xlsxpath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TB10" + File.separator + "metadata" + ".xlsx";
	
	/**
	 * HashMap containing the lineages of the specimens.
	 */
	private HashMap<String, String> lineages = updateLineages();
	
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

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}
				
				double barValue = scrollPane.getHvalue();
				innerGroup.setScaleX(scale);
				otherGroup.setScaleX(scale);
				scrollPane.setHvalue(barValue);
				otherPane.setHvalue(barValue);
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
			innerGroup.setScaleX(MIN_SCALE);
		}
		
		private HashMap<String, String> updateLineages() {
			XlsxParser xlsxparser = new XlsxParser();
			xlsxparser.parse(xlsxpath);
			return xlsxparser.getLineages();
		}
	};


	
	/**
	 * Parse lineages of the specimens.
	 */
	public HashMap<String, String> updateLineages() {
		XlsxParser xlsxparser = new XlsxParser();
		xlsxparser.parse(xlsxpath);
		return xlsxparser.getLineages();
	}
	
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
		scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				otherPane.setHvalue(newValue.doubleValue());
			}
		});
		
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
		ArrayList<Integer> counts = dbm.getDbReader().getAllCounts();
		int countIdx = 0;
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

			line.setStrokeWidth(1 + counts.get(countIdx++));
			line.setStroke(getLineColor(fromId, toId));
	        res.getChildren().add(line);
		}
		System.out.println("Finished creating graph edges");
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
	
	public Paint getLineColor(int f, int t) {
		Paint color = Paint.valueOf("0xff0000ff");
		ArrayList<String> from = dbm.getDbReader().getGenomesThroughSegment(f);
		ArrayList<String> to = dbm.getDbReader().getGenomesThroughSegment(t);
		int size = 0;
		if(from.size() > to.size()) {
			for(int i = 0; i < to.size(); i++) {
				String genome = to.get(i);
				if(lineages.containsKey(genome) && from.contains(genome) && !genome.equals("MT_H37RV_BRD_V5.ref")) {
					return NewickColourMatching.getLineageColour(lineages.get(genome));
				}
			}
		} else if(from.size() < to.size()) {
			for(int i = 0; i < from.size(); i++) {
				String genome = from.get(i);
				if(lineages.containsKey(genome) && to.contains(genome) && !genome.equals("MT_H37RV_BRD_V5.ref")) {
					return NewickColourMatching.getLineageColour(lineages.get(genome));
				}
			}
		} else {
			for(int i = 0; i < from.size(); i++) {
				String genome = from.get(i);
				if(lineages.containsKey(genome) && to.contains(genome) && !genome.equals("MT_H37RV_BRD_V5.ref")) {
					return NewickColourMatching.getLineageColour(lineages.get(genome));
				}
			}
		}
		return color;
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
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public Group getInnerGroup() {
		return innerGroup;
	}
	
	public void setRibbonPane(ScrollPane scroll) {
		otherPane = scroll;
	}
	
	public void setRibbonGroup(Group group) {
		otherGroup = group;
	}
}
