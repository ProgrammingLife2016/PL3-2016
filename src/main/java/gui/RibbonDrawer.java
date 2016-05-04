package gui;

import coordinates.Coordinate;
import coordinates.CoordinateDetermination;
import db.DatabaseManager;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

import java.util.ArrayList;

/**
 * 
 * 
 * LIKELY REDUNDANT CLASS TO BE REMOVED, KEPT AROUND UNTIL CONTROLLER CLASS CAN
 * REPLACE THIS ONE.
 *
 *
 *
 *
 */
@SuppressWarnings("restriction")
public class RibbonDrawer {
	private DatabaseManager dbm;
	
	public RibbonDrawer(DatabaseManager db) {
		this.dbm = db;
	}

	public Group draw(PannableCanvas canvas, NodeGestures nodeGestures) {
		
		CoordinateDetermination cdm = new CoordinateDetermination(dbm);
		Coordinate[] coords = cdm.calcCoords();
		ArrayList<Integer> from = dbm.getDbReader().getAllFromId();
		ArrayList<Integer> to = dbm.getDbReader().getAllToId();
		
		int maxX = getMaxX(coords);
		int maxY = getMaxY(coords);
		
		Group group = new Group();
		
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			System.out.println(i);
			Path path = drawPath(coords[fromId - 1], coords[toId - 1], maxX, maxY);
	        path.addEventFilter( MouseEvent.MOUSE_PRESSED,
	        		nodeGestures.getOnMousePressedEventHandler());
	        path.addEventFilter( MouseEvent.MOUSE_DRAGGED,
	        		nodeGestures.getOnMouseDraggedEventHandler());
	        path.setStrokeWidth(0.01 + 0.003 * dbm.getDbReader().countGenomesInLink(fromId, toId));
	        canvas.getChildren().add(path);
		}
		return group;
	}
	
	private int getMaxX(Coordinate[] coordinates) {
		int xc = 0;
		for (int i = 0; i < coordinates.length; i++) {
			if (coordinates[i].getX() > xc) {
				xc = coordinates[i].getX();
			}
		}
		return xc;
	}
	
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
	private Path drawPath(Coordinate from, Coordinate to, int maxX, int maxY) {
		MoveTo moveto = new MoveTo(0.1 * from.getX(), 0.5 * from.getY());
		LineTo lineto = new LineTo(0.1 * to.getX() , 0.5 * to.getY());
		Path path = new Path();
		path.getElements().addAll(moveto, lineto);
		return path;
	}
}
