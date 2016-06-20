package gui.controllers;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import db.DatabaseManager;
import gui.Launcher;

/**
 * @author RobKapel
 *
 * Class for dealing with the annotations. The elements are added to a
 * new group, which are added to a ScrollPane. This view will give an idea
 * of the actual length of the genome.
 */
public class AnnotationController implements Initializable {
	
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	
	private Group innerGroup;
	private Group outerGroup;
    
    private ArrayList<Integer> startLocations;
    private ArrayList<Integer> endLocations;
    private ArrayList<String> names;
    
	private DatabaseManager dbm;
	
	/**
	 * Handles the scroll wheel event handler for zooming in and zooming out.
	 */
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			event.consume();

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
	 * Initialize fxml file.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		loadData();
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
		scrollPane.setHvalue(0);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		innerGroup = getAnnotations();
//		innerGroup.scaleYProperty().bind(scrollPane.heightProperty().divide(innerGroup.getBoundsInLocal().getHeight()));
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
	}
	
	/**
	 * Creates a visualization of the genome annotations.
	 * @return a Group containing this visualization
	 */
	private Group getAnnotations() {
		Group res = new Group();
		
		List<Integer> levelsTaken = new ArrayList<>();
		for (int i = 0; i < startLocations.size(); i++) {
			int startX = startLocations.get(i);
			int endX = endLocations.get(i);
			int width = endX - startX;
			int level;
			for (level = 0; level < levelsTaken.size(); level++) {
				if (startX >= levelsTaken.get(level)) {
					break;
				}
			}
			try {
				levelsTaken.set(level, endX);
			} catch (IndexOutOfBoundsException e) {
				levelsTaken.add(endX);
			}
			int heigth = 60;
			int spacing = 10;
			Rectangle rect = new Rectangle(startX, 20 + level * (heigth+spacing), width , heigth);
		    rect.setFill(Color.rgb(244, 244, 244));
		    rect.setStroke(Color.BLACK);
			
		    Tooltip tooltip = new Tooltip();
		    tooltip.setText(names.get(i));
		    hackTooltipStartTiming(tooltip);
		    Tooltip.install(rect, tooltip);
			res.getChildren().add(rect);
		}
		return res;
	}
	
	public static void hackTooltipStartTiming(Tooltip tooltip) {
	    try {
	        Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
	        fieldBehavior.setAccessible(true);
	        Object objBehavior = fieldBehavior.get(tooltip);

	        Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
	        fieldTimer.setAccessible(true);
	        Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

	        objTimer.getKeyFrames().clear();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Load in the necessary data for the annotations.
	 */
	private void loadData() {
		this.dbm = Launcher.getDatabaseManager();
		startLocations = dbm.getDbReader().getAllAnnotationStartLocations();
		endLocations = dbm.getDbReader().getAllAnnotationEndLocations();
		names = dbm.getDbReader().getAllAnnotationNames();
	}
	
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public Group getInnerGroup() {
		return innerGroup;
	}
}
