package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import db.DatabaseManager;

/**
 * @author hugokooijman
 *
 * Class containing all the logic involving the graph view. It reads out database tables,
 * stores required information in memory and adds the required to the Group "graphpane",
 * which is defined in the GraphLevel.fxml file. This will result in a visual representation
 * of all segments that are loaded into it.
 */
public class AnnotationController implements Initializable {
	
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	
	private Group innerGroup;
	private Group outerGroup;
	
    private static final double MAX_SCALE = 3.0d;
    private static final double MIN_SCALE = .0035d;
    
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
				scrollPane.setHvalue(barValue);
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
			innerGroup.setScaleX(scale);
		}
	};

	/**
	 * Initialize fxml file.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		scrollPane.addEventFilter(KeyEvent.KEY_TYPED, keyEventHandler);
		loadData();
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
		scrollPane.setHvalue(0);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		innerGroup = getAnnotations();
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
	}
	
	private Group getAnnotations() {
		Group res = new Group();
		res.getChildren().add(new Line(0,50,4411100,50));
		for (int i = 0; i < startLocations.size(); i++) {
			int startX = startLocations.get(i);
			int endX = endLocations.get(i);
			int width = endX - startX;
			
			Rectangle rect = new Rectangle(startX, 20, width , 60);
		    rect.setFill(Color.rgb(244, 244, 244));
		    rect.setStroke(Color.BLACK);
			
			Text text = new Text(startX, 95, names.get(i));
			
			res.getChildren().add(rect);
			res.getChildren().add(text);
		}
		return res;
	}
	
	private void loadData() {
		this.dbm = Launcher.dbm;
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
