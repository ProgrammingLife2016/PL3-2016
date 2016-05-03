package gui;

import coordinates.Coordinate;
import coordinates.CoordinateDetermination;
import db.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@SuppressWarnings("restriction")
public class RibbonController implements Initializable {
	@FXML AnchorPane pane;
	@FXML ScrollPane scrollPane;
	@FXML Canvas canvas;
	
	private GraphicsContext gc;
	
	private DatabaseManager dbm;
	
	/**
	 * function that gets executed when the matching fxml file is loaded.
	 * 
	 * The group is from within the FXML file. We use that group to add events and the pannable 
	 * canvas on which the drawing of the ribbon takes place.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.dbm = Launcher.dbm;
		
		gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(440, 410, 410, 440);
        gc.fillOval(410, 460, 30, 30);
        gc.strokeOval(460, 460, 30, 30);

	}
	
	/**
	 * Function to draw the Ribbons on a pannable canvas
	 * 
	 * @param pc 
	 * 		a given pannable canvas
	 * @param nodeGestures
	 * 		event handlers
	 */
	public void draw(PannableCanvas pc, NodeGestures nodeGestures) {
		ArrayList<Integer> from = dbm.getDbReader().getAllFromId();
		ArrayList<Integer> to = dbm.getDbReader().getAllToId();
		ArrayList<Integer> counts = dbm.getDbReader().getAllCounts();
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> ycoords = dbm.getDbReader().getAllYCoord();
		
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			Path path = drawPath(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
					xcoords.get(toId - 1), ycoords.get(toId - 1));
	        path.addEventFilter( MouseEvent.MOUSE_PRESSED,
	        		nodeGestures.getOnMousePressedEventHandler());
	        path.addEventFilter( MouseEvent.MOUSE_DRAGGED,
	        		nodeGestures.getOnMouseDraggedEventHandler());
	        path.setStrokeWidth(0.1 + 0.1 * counts.get(i));
	        pc.getChildren().add(path);
		}
	}
	
	/**
	 * Function to determine the maximum x-coordinate of all segments.
	 * 
	 * @param coordinates
	 * 				Array of segment coordinates.
	 * @return The maximum x-coordinate.
	 */
	@SuppressWarnings("unused")
	private int getMaxX(Coordinate[] coordinates) {
		int xc = 0;
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].getX() > xc) {
				xc = coordinates[i].getX();
			}
		}
		return xc;
	}
	
	/**
	 * Function to determine the maximum y-coordinate of all segments.
	 * 
	 * @param coordinates
	 * 				Array of segment coordinates.
	 * @return The maximum y-coordinate.
	 */
	@SuppressWarnings("unused")
	private int getMaxY(Coordinate[] coordinates) {
		int yc = 0;
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].getY() > yc) {
				yc = coordinates[i].getY();
			}
		}
		return yc;
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
	private Path drawPath(int fromX, int fromY, int toX, int toY) {
		MoveTo moveto = new MoveTo(10 * fromX, 5 * fromY);
		LineTo lineto = new LineTo(10 * toX , 5 * toY);
		Path path = new Path();
		path.getElements().addAll(moveto, lineto);
		return path;
	}
}
