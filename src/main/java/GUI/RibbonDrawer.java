package gui;

import gui.SymanticZoom.NodeGestures;
import gui.SymanticZoom.PannableCanvas;

import java.util.ArrayList;

import coordinates.Coordinate;
import coordinates.CoordinateDetermination;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.VLineTo;

public class RibbonDrawer {
	
	
	@SuppressWarnings("restriction")
	public static Group draw(PannableCanvas canvas, NodeGestures nodeGestures) {
		Coordinate[] coords = CoordinateDetermination.calcCoords();
		ArrayList<Integer> from = dummyFromIDData();
		ArrayList<Integer> to = dummyToIDData();
		int maxX = getMaxX(coords);
		int maxY = getMaxY(coords);
		
		Group group = new Group();
		
		for(int i = 0; i < from.size(); i++) {
			int fromID = from.get(i);
			int toID = to.get(i);
			Path path = drawPath(coords[fromID-1], coords[toID-1], maxX, maxY);
	        path.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
	        path.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
	        path.setStrokeWidth(countGenomesInLink(fromID, toID));
	        canvas.getChildren().add(path);
		}
		return group;
		
	}
	
	private static int getMaxX(Coordinate[] coordinates) {
		int x = 0;
		for(int i = 0; i < coordinates.length; i++) {
			if(coordinates[i].getX() > x) {
				x = coordinates[i].getX();
			}
		}
		return x;
	}
	
	private static int getMaxY(Coordinate[] coordinates) {
		int y = 0;
		for(int i = 0; i < coordinates.length; i++) {
			if(coordinates[i].getY() > y) {
				y = coordinates[i].getY();
			}
		}
		return y;
	}
	
	@SuppressWarnings("restriction")
	private static Path drawPath(Coordinate from, Coordinate to, int maxX, int maxY) {
		MoveTo moveto = new MoveTo(600/(maxX + 2) * from.getX() + 600/(maxX + 2), 600/(maxY + 2) * from.getY());
		LineTo lineto = new LineTo(600/(maxX + 2) * to.getX()+ 600/(maxX + 2), 600/(maxY + 2) * to.getY());
		Path path = new Path();
		path.getElements().addAll(moveto, lineto);
		return path;
	}
	
	
	private static ArrayList<Integer> dummyFromIDData() {
		ArrayList<Integer> fromIDs = new ArrayList<Integer>();
		fromIDs.add(1);
		fromIDs.add(1);
		fromIDs.add(2);
		fromIDs.add(2);
		fromIDs.add(3);
		fromIDs.add(3);
		fromIDs.add(4);
		fromIDs.add(5);
		fromIDs.add(6);
		fromIDs.add(7);
		fromIDs.add(8);
		return fromIDs;
	}
	
	private static ArrayList<Integer> dummyToIDData() {
		ArrayList<Integer> toIDs = new ArrayList<Integer>();
		toIDs.add(2);
		toIDs.add(3);
		toIDs.add(4);
		toIDs.add(5);
		toIDs.add(6);
		toIDs.add(8);
		toIDs.add(7);
		toIDs.add(7);
		toIDs.add(8);
		toIDs.add(9);
		toIDs.add(9);
		return toIDs;
	}
	
	public static int countGenomesInLink(int i, int j) {
		if(i == 1 || i == 7 || i == 8) {return 2;}
		else return 1;	
	}
}
