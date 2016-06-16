package gui.controllers.ribbon;

import java.net.URL;
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
import javafx.scene.text.Text;

import db.DatabaseManager;
import gui.Launcher;
import gui.views.ribbon.RibbonView;

/**
 * Controller class for the Ribbon screen/tab.
 */
public class RibbonController implements Initializable {
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	private ScrollPane otherPane;

	private ScrollPane annotationRibbonPane;
	private ScrollPane annotationGraphPane;

	@FXML private CheckBox checkboxSnp;
	@FXML private CheckBox checkboxInsert;
	
	private Group innerGroup;
	private Group outerGroup;
	private Group otherGroup;
	private Group annotationRibbonGroup;
	private Group annotationGraphGroup;
	
	private DatabaseManager dbm = Launcher.getDatabaseManager();
	private RibbonView ribbonView = new RibbonView(dbm);
	
	private Group collapsedGroup = ribbonView.createCollapsedRibbons();
	private Group normalGroup = ribbonView.createNormalRibbons();
	
    private static final double MAX_SCALE = 1.0d;
    private static final double MIN_SCALE = .003d;
    private static final double COLLAPSE = .2;
    private static final double GRAPH = .8;
	
	private double prevScale = 1;
	

	
	/**
	 * Handles the scroll wheel event for the ribbon view.
	 */
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			if (event.isControlDown()) {
				event.consume();
				double deltaY = event.getDeltaY();
				double delta = 1.2;
				double scale = innerGroup.getScaleX();
				double oldHBarValue = scrollPane.getHvalue();
				double oldVBarValue = scrollPane.getVvalue();

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}
				if (prevScale > COLLAPSE && scale <= COLLAPSE) {
					innerGroup.getChildren().clear();
					Group temp = new Group(collapsedGroup);
					innerGroup.getChildren().addAll(temp.getChildren());
					scrollPane.setHvalue(oldHBarValue);
					scrollPane.setVvalue(oldVBarValue);
				} else if (prevScale < COLLAPSE && scale >= COLLAPSE 
						|| prevScale > GRAPH && scale <= GRAPH) {
					innerGroup.getChildren().clear();
					Group temp = new Group(normalGroup);
					innerGroup.getChildren().addAll(temp.getChildren());
					scrollPane.setHvalue(oldHBarValue);
					scrollPane.setVvalue(oldVBarValue);
				} else if (prevScale < GRAPH && scale >= GRAPH) {
					innerGroup.getChildren().clear();
					Group temp = new Group(otherGroup);
					innerGroup.getChildren().addAll(temp.getChildren());
					scrollPane.setHvalue(oldHBarValue);
					scrollPane.setVvalue(oldVBarValue);
				}
				
				double barValue = scrollPane.getHvalue();
				innerGroup.setScaleX(scale);
				scrollPane.setHvalue(barValue);
				otherGroup.setScaleX(scale);
				otherPane.setHvalue(barValue);
				
				annotationRibbonGroup.setScaleX(scale);
				annotationGraphGroup.setScaleX(scale);
				annotationRibbonPane.setHvalue(barValue);
				annotationGraphPane.setHvalue(barValue);
				prevScale = scale;
				return;
			} else if (event.isShiftDown()) {
				event.consume();
				double deltaY = event.getDeltaX();
				double delta = 1.1;
				double scale = innerGroup.getScaleY();
				if (deltaY < 0) {
					scale = scale / Math.pow(delta, -event.getDeltaX() / 20);
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaX() / 20);
				}
				
				double barValue = scrollPane.getVvalue();
				innerGroup.setScaleY(scale);
				scrollPane.setVvalue(barValue);
				otherPane.setVvalue(barValue);
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
			annotationRibbonGroup.setScaleX(scale);
			annotationRibbonPane.setPrefWidth(scrollPane.getPrefWidth());
			annotationGraphGroup.setScaleX(scale);
			annotationRibbonPane.setPrefWidth(scrollPane.getPrefWidth());

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
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				otherPane.setHvalue(newValue.doubleValue());
				annotationRibbonPane.setHvalue(newValue.doubleValue());
				annotationGraphPane.setHvalue(newValue.doubleValue());
			}
		});

		checkboxInsert.selectedProperty().addListener(
				(ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
				//For now, we just print a line. Should be toggling the insertions
				System.out.println("You pressed the insert checkbox")
			);
			
		checkboxSnp.selectedProperty().addListener(
			(ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
			//For now, we just print a line. Should be toggling the SNPs
			System.out.println("You pressed the SNP checkbox")
		);
		
		double maxY = dbm.getDbReader().getMaxYCoord();
		innerGroup.setScaleY(720.0 / maxY);
		innerGroup.setScaleX(MIN_SCALE);
		
		Text reminder1 = new Text("Hold Shift to scroll vertically");
		Text reminder2 = new Text("Hold Control to scroll horizontally");
	}
	

	
	/**
	 * Updates the view. Used when changing database files so the graph
	 * will have to adjust to the new file.
	 */
	public void updateView() {
		// Inner group and outer group according to the ScrollPane JavaDoc.
		innerGroup = new Group(collapsedGroup);
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
	}


	public void redraw() {
		collapsedGroup = ribbonView.createCollapsedRibbons();
		normalGroup = ribbonView.createNormalRibbons();
		updateView();
		double maxY = dbm.getDbReader().getMaxYCoord();
		innerGroup.setScaleY(720.0 / maxY);
		innerGroup.setScaleX(MIN_SCALE);
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
	
	public void setAnnotationRibbonGroup(Group group) {
		annotationRibbonGroup = group;
	}
	
	public void setAnnotationRibbonPane(ScrollPane scrollpane) {
		annotationRibbonPane = scrollpane;
	}
	
	public void setAnnotationGraphGroup(Group group) {
		annotationGraphGroup = group;
	}
	
	public void setAnnotationGraphPane(ScrollPane scrollpane) {
		annotationGraphPane = scrollpane;
	}



	public RibbonView getRibbonView() {
		return ribbonView;
	}
	

}
