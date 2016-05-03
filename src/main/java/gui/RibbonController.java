package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import coordinates.Coordinate;
import coordinates.CoordinateDetermination;
import db.DatabaseManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class RibbonController implements Initializable, SetScreen {
	@FXML PannableCanvas pane;
	private DatabaseManager dbm;
	private ScreenManager screenManager;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
     
		this.dbm = Launcher.dbm;
		
		PannableCanvas pc = new PannableCanvas();
		Group group = new Group();
		pc.setTranslateX(100);
        pc.setTranslateY(100);
		NodeGestures nodeGestures = new NodeGestures(pc);
		draw(pc, nodeGestures);
        Label label1 = new Label("SCENE 1");
        label1.setTranslateX(10);
        label1.setTranslateY(10);
        pc.getChildren().addAll(label1);
        group.getChildren().add(pc);
        
        
        Scene scene = new Scene(group, 1024, 768);
        SceneGestures sceneGestures = new SceneGestures(pc);
        scene.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        ScreenManager.currentStage.setScene(scene);
        ScreenManager.currentStage.show();
 
	}

	public void draw(PannableCanvas pc, NodeGestures nodeGestures) {
		CoordinateDetermination cdm = new CoordinateDetermination(dbm);
		Coordinate[] coords = cdm.calcCoords();
		ArrayList<Integer> from = dbm.getDBReader().getAllFromID();
		ArrayList<Integer> to = dbm.getDBReader().getAllToID();
		
		int maxX = getMaxX(coords);
		int maxY = getMaxY(coords);
		
		for(int i = 0; i < from.size(); i++) {
			int fromID = from.get(i);
			int toID = to.get(i);
			System.out.println(i);
			Path path = drawPath(coords[fromID-1], coords[toID-1], maxX, maxY);
	        path.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
	        path.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());
	        path.setStrokeWidth(0.01 + 0.003 * dbm.getDBReader().countGenomesInLink(fromID, toID));
	        pc.getChildren().add(path);
		}
	}
	
	private int getMaxX(Coordinate[] coordinates) {
		int x = 0;
		for(int i = 0; i < coordinates.length; i++) {
			if(coordinates[i].getX() > x) {
				x = coordinates[i].getX();
			}
		}
		return x;
	}
	
	private int getMaxY(Coordinate[] coordinates) {
		int y = 0;
		for(int i = 0; i < coordinates.length; i++) {
			if(coordinates[i].getY() > y) {
				y = coordinates[i].getY();
			}
		}
		return y;
	}
	
	private Path drawPath(Coordinate from, Coordinate to, int maxX, int maxY) {
		MoveTo moveto = new MoveTo(0.1 * from.getX(), 0.5 * from.getY());
		LineTo lineto = new LineTo(0.1 * to.getX() , 0.5 * to.getY());
		Path path = new Path();
		path.getElements().addAll(moveto, lineto);
		return path;
	}

	@Override
	public void setScreenDriver(ScreenManager screenPage) {
		this.screenManager = screenPage;
		
	}

}
