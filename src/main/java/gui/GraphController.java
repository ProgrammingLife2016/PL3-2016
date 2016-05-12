package gui;

import db.DatabaseManager;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.fxml.Initializable;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * @author hugokooijman
 *
 * Class containing all the logic involving the graph view. It reads out database tables,
 * stores required information in memory and adds the required to the Group "graphpane",
 * which is defined in the GraphLevel.fxml file. This will result in a visual representation
 * of all segments that are loaded into it.
 */
@SuppressWarnings("restriction")
public class GraphController implements Initializable {
	
	/**
	 * Group in fxml file to which GraphSegments will be added.
	 */
	@FXML Group graphpane;
	
	/**
	 * DatabaseManager for reading out required data.
	 */
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
	 * Initialize fxml file.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.dbm = Launcher.dbm;
		loadSegmentData();
		constructSegmentMap();
		drawGraph();
	}
	
	/**
	 * Load in all necessary information from the datbase.
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
	 * Uses the created hash map of segments to draw a visual representation of the segments,
	 * such as where they are located, how they are related and what their DNA strand is.
	 */
	public void drawGraph() {
		drawGraphEdges();
		drawGraphSegments();
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
	 * Draws edges between all the segment coordinates.
	 */
	private void drawGraphEdges() {
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
	        graphpane.getChildren().add(path);
		}
	}
	
	/**
	 * Draws all GraphSegments on the segment coordinates.
	 */
	private void drawGraphSegments() {
		for (int i = 1; i <= segments.size(); i++) {
			graphpane.getChildren().add(segments.get(i));
		}
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
