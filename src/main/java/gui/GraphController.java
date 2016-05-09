package gui;

import db.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

@SuppressWarnings("restriction")
public class GraphController implements Initializable {
	@FXML AnchorPane graphpane;
	private DatabaseManager dbm;
	
	/**
	 * Map of all GraphSegments.
	 */
	private HashMap<Integer, GraphSegment> segments;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.dbm = Launcher.dbm;
		
		ArrayList<Integer> from = dbm.getDbReader().getAllFromId();
		ArrayList<Integer> to = dbm.getDbReader().getAllToId();
		ArrayList<Integer> graphxcoords =
				scaleRibbonToGraphCoordsX(dbm.getDbReader().getAllXCoord());
		ArrayList<Integer> graphycoords =
				scaleRibbonToGraphCoordsY(dbm.getDbReader().getAllYCoord());
		
		segments = constructSegmentMap(from, to, graphxcoords, graphycoords);
		drawGraph(from, to, graphxcoords, graphycoords);
		
		graphpane.setPrefWidth(segments.get(to.get(to.size() - 1)).getLayoutX());
		graphpane.setPrefHeight(1500);
	}
	
	public HashMap<Integer, GraphSegment> constructSegmentMap(ArrayList<Integer> from,
			ArrayList<Integer> to, ArrayList<Integer> xcoords, ArrayList<Integer> ycoords) {
		HashMap<Integer, GraphSegment> segments = new HashMap<Integer,
				GraphSegment>((int) Math.ceil(to.size() / 0.75));
		int linkpointer = 1;
		
		for (int i = 1; i <= to.get(to.size() - 1); i++) {
			int childcount = 0;
			while (linkpointer <= from.size() && i == from.get(linkpointer - 1)) {
				childcount++;
				linkpointer++;
			}
			linkpointer -= childcount;
			
			String content = dbm.getDbReader().getContent(i);
			char[] dnacontent = content.toCharArray();
			
			GraphSegment segment = new GraphSegment(i, childcount, dnacontent);
			int childindex = 0;
			while (linkpointer <= from.size() && i == from.get(linkpointer - 1)) {
				segment.getSegmentChildren()[childindex] = to.get(linkpointer - 1);
				linkpointer++;
				childindex++;
			}
			Text text = new Text();

			String dna = "";
			for (int j = 0; j < segment.getDnacontent().length && j <= 4; j++) {
				dna += "" + segment.getDnacontent()[j];
			}
			if (segment.getDnacontent().length > 5) {
				dna += "...";
			}
			text.setText(dna);
			segment.getChildren().add(text);
			
			segment.setLayoutX(xcoords.get(i - 1));
			segment.setLayoutY(ycoords.get(i - 1));	
			segments.put(i, segment);
		}
		return segments;
	}
	
	public void drawGraph(ArrayList<Integer> from, ArrayList<Integer> to, 
			ArrayList<Integer> xcoords, ArrayList<Integer> ycoords) {
		
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			GraphSegment fromsegment = segments.get(fromId);
			GraphSegment tosegment = segments.get(toId);
			Path path = drawGraphPath(fromsegment.getLayoutX() + fromsegment.getRadius(),
					fromsegment.getLayoutY() + fromsegment.getRadius(),
					tosegment.getLayoutX() + tosegment.getRadius(),
					tosegment.getLayoutY() + tosegment.getRadius());
	        path.setStrokeWidth(1);

	        graphpane.getChildren().add(path);
	        if (segments.get(fromId).isDrawn()) {
	        	graphpane.getChildren().remove(segments.get(fromId));
	        	graphpane.getChildren().add(segments.get(fromId));
	        } else {
	        	graphpane.getChildren().add(segments.get(fromId));
	        	segments.get(fromId).setDrawn(true);
	        }
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
	private Path drawGraphPath(double fromX, double fromY, double toX, double toY) {
		MoveTo moveto = new MoveTo(fromX, fromY);
		LineTo lineto = new LineTo(toX , toY);
		Path path = new Path();
		path.getElements().addAll(moveto, lineto);
		return path;
	}
	
	public ArrayList<Integer> scaleRibbonToGraphCoordsX(ArrayList<Integer> xcoords) {
		for (int i = 0; i < xcoords.size(); i++) {
			int newc = xcoords.remove(i) * 100;
			xcoords.add(i, newc);
		}
		return xcoords;
	}
	
	public ArrayList<Integer> scaleRibbonToGraphCoordsY(ArrayList<Integer> ycoords) {
		for (int i = 0; i < ycoords.size(); i++) {
			int newc = ycoords.remove(i) * 50;
			ycoords.add(i, newc);
		}
		return ycoords;
	}
}
