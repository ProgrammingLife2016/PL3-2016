package gui;

import coordinates.Coordinate;
import coordinates.CoordinateDetermination;
import db.DatabaseManager;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;



import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

//<Circle fill="DODGERBLUE" layoutX="40.0" layoutY="200.0" radius="25.0" stroke="BLACK" strokeType="INSIDE" />

@SuppressWarnings("restriction")
public class GraphController implements Initializable {
	@FXML AnchorPane graphpane;
	private DatabaseManager dbm;
	
	private static final int ROOT_X_OFFSET = 40;
	private static final int CHILD_X_OFFSET = 100;
	
	private int[] from = {1, 1, 2, 3, 4, 4, 4, 5, 6, 7};
	private int[] to = {2, 3, 4, 4, 5, 6, 7, 8, 8, 8};
	
	/**
	 * Map of all GraphSegments.
	 */
	private HashMap<Integer, GraphSegment> segments;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.dbm = Launcher.dbm;
		
		graphpane.setPrefWidth(10000);
		graphpane.setPrefHeight(1000);
		segments = constructSegmentMap();
		drawGraph();
	}
	
	public HashMap<Integer, GraphSegment> constructSegmentMap() { 
		HashMap<Integer, GraphSegment> segments = new HashMap<Integer,
				GraphSegment>((int) Math.ceil(to.length / 0.75));
		int linkpointer = 1;
		
		for (int i = 1; i <= getSegmentCount(); i++) {
			int childcount = 0;
			System.out.println("LinkPointer is now " + linkpointer);
			while (linkpointer <= from.length && i == from[linkpointer - 1]) {
				System.out.println("SegmentId now: " + i);
				childcount++;
				linkpointer++;
			}
			linkpointer -= childcount;
			
			GraphSegment segment = new GraphSegment(i, childcount);
			int childindex = 0;
			while (linkpointer <= from.length && i == from[linkpointer - 1]) {
				System.out.println("Going to: " + to[linkpointer - 1]);
				segment.getSegmentChildren()[childindex] = to[linkpointer - 1];
				linkpointer++;
				childindex++;
			}
			System.out.println("Now putting " + i + " in hashmap");
			segments.put(i, segment);
		}
		return segments;
	}
	
	public void drawGraph() {
		GraphSegment root = segments.get(1);
		drawRootSegment();
		drawChildSegments();
		for (int i = 1; i <= segments.size(); i++) {
			Text text = new Text("" + segments.get(i).getSegmentId());
			segments.get(i).getChildren().add(text);
			graphpane.getChildren().add(segments.get(i));
		}	
	}
	
	public void drawRootSegment() {
		GraphSegment root = segments.get(1);
		root.setLayoutX(ROOT_X_OFFSET);
		root.setLayoutY((int) graphpane.getPrefHeight() / 2); //Middle of Y-axis
	}
	
	
	public void drawChildSegments() {
		for (int j = 1; j <= segments.size(); j++) {
			for (int i = 0; i < segments.get(j).getSegmentChildren().length; i++) {
				System.out.println("Drawing segmentchild " + segments.get(j).getSegmentChildren()[i] +
						" of: " + segments.get(j).getSegmentId());
				GraphSegment seg = segments.get(segments.get(j).getSegmentChildren()[i]);
				seg.setLayoutX(segments.get(j).getLayoutX() + CHILD_X_OFFSET);
				seg.setLayoutY(segments.get(j).getLayoutY() + 70 * i);
			}
		}
	}
	
	public int getSegmentCount() {
		return to[to.length - 1];
	}
	
	public GraphSegment createSegment(GraphSegment root, int childnr) {
		int xoffset = (int) (ROOT_X_OFFSET + root.getLayoutX() * CHILD_X_OFFSET);
		GraphSegment segment = new GraphSegment(1, 0);
	    segment.setLayoutX(xoffset);
	    segment.setLayoutY(200);
		return segment;
	}

}
