package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;

import db.DatabaseManager;

/**
 * Controller class for the Ribbon screen/tab.
 */
public class RibbonController implements Initializable {
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	private ScrollPane otherPane;

	@FXML private CheckBox checkboxSnp;
	@FXML private CheckBox checkboxInsert;
	
	private DatabaseManager dbm = Launcher.dbm;
	
	private Group innerGroup;
	private Group outerGroup;
	private Group otherGroup;

	private Group collapsedGroup = createCollapsedRibbons();
	private Group normalGroup = createNormalRibbons();
	
    private static final double MAX_SCALE = 100.0d;
    private static final double MIN_SCALE = .0035d;
    private static final double COLLAPSE = 2;
	
	private double prevScale = 1;
	
	/**
	 * Handles the scroll wheel event for the ribbon view.
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
				if (prevScale < COLLAPSE && scale >= COLLAPSE) {
					System.out.println("switch to normal");
					innerGroup.getChildren().clear();
					innerGroup.getChildren().addAll(normalGroup.getChildren());
				} else if (prevScale > COLLAPSE && scale <= COLLAPSE) {
					System.out.println("switch to collapsed");
					innerGroup.getChildren().clear();
					innerGroup.getChildren().addAll(collapsedGroup.getChildren());
				}
				
				System.out.println(prevScale + "->" + scale);
				double barValue = scrollPane.getHvalue();
				innerGroup.setScaleX(scale);
				otherGroup.setScaleX(scale);
				scrollPane.setHvalue(barValue);
				otherPane.setHvalue(barValue);
				prevScale = scale;
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
	 * Event handler for keyboard events with the ribbon view.
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

			if (character.equals("+") || character.equals("=")) {
				scale *= delta;

				scale = scale > MAX_SCALE ? MAX_SCALE : scale;
			} else if (character.equals("-")) {
				scale /= delta;
				scale = scale < MIN_SCALE ? MIN_SCALE : scale;
			} else {
				return;
			}
			
			double barValue = scrollPane.getHvalue();
			innerGroup.setScaleX(scale);
			otherGroup.setScaleX(scale);
			scrollPane.setHvalue(barValue);
			otherPane.setHvalue(barValue);
		}
	};
    
	
	/**
	 * function that gets executed when the matching fxml file is loaded.
	 * 
	 * The group is from within the FXML file. We use that group to add events and the pannable 
	 * canvas on which the drawing of the ribbon takes place.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateView();
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		scrollPane.addEventFilter(KeyEvent.ANY, keyEventHandler);
		
		// Resize the scrollpane along with the window.
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
		
		scrollPane.setHvalue(0);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				otherPane.setHvalue(newValue.doubleValue());
			}
		});
		
		double maxY = dbm.getDbReader().getMaxYCoord();
		innerGroup.setScaleY(720.0 / maxY);
		innerGroup.setScaleX(0.4);
	}
	
	/**
	 * Updates the view. Used when changing database files so the graph
	 * will have to adjust to the new file.
	 */
	public void updateView() {
		// Inner group and outer group according to the ScrollPane JavaDoc.
		innerGroup = collapsedGroup;
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
	}
	

	/**
	 * Creates all paths that make up the ribbons and returns a {@link Group}
	 * containing those paths.
	 * 
	 * @return A group containing the ribbons.
	 */
	public Group createNormalRibbons() {
		Group res = new Group();
		ArrayList<ArrayList<Integer>> links = dbm.getDbReader().getLinks();
		ArrayList<Integer> counts = dbm.getDbReader().getAllCounts();
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> ycoords = dbm.getDbReader().getAllYCoord();

		int countIdx = 0;
		
		for (int fromId = 1; fromId <= links.size(); fromId++) {
			for (int toId : links.get(fromId - 1)) {
				Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
						xcoords.get(toId - 1), ycoords.get(toId - 1));
//		        line.setStrokeWidth(0.02 + 0.02 * counts.get(countIdx++));
				line.setStrokeWidth(1 + counts.get(countIdx++));
		        res.getChildren().add(line);
			}
		}
		return res;
	}
	
	/**
	 * Creates all paths that make up the collapsed ribbons and returns a
	 * {@link Group} containing those paths.
	 * 
	 * @return A group containing the ribbons.
	 */
	public Group createCollapsedRibbons() {
		Group res = new Group();
		ArrayList<ArrayList<Integer>> links = dbm.getDbReader().getLinks();
		ArrayList<Integer> counts = dbm.getDbReader().getAllCounts();
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> ycoords = dbm.getDbReader().getAllYCoord();
		Queue<int[]> bubbles = new LinkedList<>(dbm.getDbReader().getBubbles());
		
		int countIdx = 0; // current index in the counts list.
		
		List<Integer> ignore = new LinkedList<>();
		
		for (int fromId = 1; fromId <= links.size(); fromId++) {
			List<Integer> edges = links.get(fromId - 1);
			
			if (!bubbles.isEmpty() && fromId == bubbles.peek()[0]) {
				int[] bubble = bubbles.poll();
				Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
						xcoords.get(bubble[1] - 1), ycoords.get(bubble[1] - 1));
				line.setStrokeWidth(0.2);
		        res.getChildren().add(line);
		        ignore.addAll(edges);
			} else {
				if (ignore.contains(fromId)) {
					continue;
					
				}
				for (int toId : edges) {
					if (!bubbles.isEmpty() && toId == bubbles.peek()[1]) {
						countIdx += edges.size();
						break;
					}
					Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
							xcoords.get(toId - 1), ycoords.get(toId - 1));
//					line.setStrokeWidth(0.02 + 0.02 * counts.get(countIdx++));
					line.setStrokeWidth(1 + counts.get(countIdx++));
					res.getChildren().add(line);
				}
			}
		}
		
		return res;
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public Group getInnerGroup() {
		return innerGroup;
	}
	
	public void setGraphPane(ScrollPane scroll) {
		otherPane = scroll;
	}
	
	public void setGraphGroup(Group group) {
		otherGroup = group;
	}

}
