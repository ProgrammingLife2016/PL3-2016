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
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@SuppressWarnings("restriction")
public class RibbonController implements Initializable {
	@FXML Group group;
	private DatabaseManager dbm;
	
	/**
	 * function that gets executed when the matching fxml file is loaded.
	 * 
	 * The group is from within the FXML file. We use that group to add events and the pannable canvas
	 * on which the drawing of the ribbon takes place.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.dbm = Launcher.dbm;
		
		PannableCanvas pc = new PannableCanvas();
		pc.setTranslateX(100);
        pc.setTranslateY(100);
        
		NodeGestures nodeGestures = new NodeGestures(pc);
		draw(pc, nodeGestures);
		
        Label label1 = new Label("SCENE 1");
        label1.setTranslateX(10);
        label1.setTranslateY(10);
        pc.getChildren().addAll(label1);
        SceneGestures sceneGestures = new SceneGestures(pc);
        group.addEventFilter( MouseEvent.MOUSE_PRESSED,
		sceneGestures.getOnMousePressedEventHandler());
        group.addEventFilter( MouseEvent.MOUSE_DRAGGED,
		sceneGestures.getOnMouseDraggedEventHandler());
        group.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        group.getChildren().add(pc);

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
		CoordinateDetermination cdm = new CoordinateDetermination(dbm);
		Coordinate[] coords = cdm.calcCoords();
		ArrayList<Integer> from = dbm.getDbReader().getAllFromId();
		ArrayList<Integer> to = dbm.getDbReader().getAllToId();
		ArrayList<Integer> counts = dbm.getDbReader().getAllCounts();
		
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			System.out.println("FromID: " + from.get(i));
			System.out.println("ToID: " + to.get(i));
			System.out.println(counts.get(i));
			Path path = drawPath(coords[fromId - 1], coords[toId - 1]);
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
	private Path drawPath(Coordinate from, Coordinate to) {
		MoveTo moveto = new MoveTo(10 * from.getX(), 5 * from.getY());
		LineTo lineto = new LineTo(10 * to.getX() , 5 * to.getY());
		Path path = new Path();
		path.getElements().addAll(moveto, lineto);
		return path;
	}
}
