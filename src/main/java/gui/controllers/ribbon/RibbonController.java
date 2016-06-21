package gui.controllers.ribbon;

import java.net.URL;

import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;

import db.DatabaseManager;
import gui.GuiPreProcessor;
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
	
	@FXML private Label checkboxesActiveLabel;
	@FXML private CheckBox checkboxSnp;
	@FXML private CheckBox checkboxInsert;
	@FXML private TextArea textArea;
	
	private Group innerGroup;
	private Group outerGroup;
	private Group otherGroup;
	private Group annotationRibbonGroup;
	
	private DatabaseManager dbm = Launcher.getDatabaseManager();
	private RibbonView ribbonView = new RibbonView(dbm);
	
	private GuiPreProcessor preProcessor = Launcher.getPreprocessor();
	
	private Group collapsedGroup = preProcessor.getCollapsedGroup();
	private Group normalGroup = preProcessor.getNormalGroup();
	private Group snpGroup = preProcessor.getSnpGroup();
	private Group indelGroup = preProcessor.getInDelGroup();

    private static final double MAX_SCALE = 1.0d;
    private static final double COLLAPSE = .2;
    private static final double GRAPH = .8;
    
    private DoubleProperty minScaleProperty = new SimpleDoubleProperty(.003d);
    private DoubleProperty scaleOffSetProperty = new SimpleDoubleProperty(.0d);
	
	private double prevScale = 1;
	
	private StringProperty selectedNodeContent = new SimpleStringProperty("");
	private BooleanProperty disableCheckBoxes = new SimpleBooleanProperty(false);
	private StringProperty checkboxLabelText = new SimpleStringProperty("Zoom in to enable selections");
	
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
				double oldBarValue = scrollPane.getHvalue();

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					scale = scale < minScaleProperty.get() ? minScaleProperty.get() : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}
				scaleOffSetProperty.set(scale - minScaleProperty.get());
				if (prevScale > COLLAPSE && scale <= COLLAPSE) {
					innerGroup.getChildren().clear();
					Group temp = new Group(collapsedGroup);
					innerGroup.getChildren().addAll(temp.getChildren());
					scrollPane.setHvalue(oldBarValue);
				} else if (prevScale < COLLAPSE && scale >= COLLAPSE 
						|| prevScale > GRAPH && scale <= GRAPH) {
					if (checkboxSnp.isSelected()) {
						innerGroup.getChildren().clear();
						Group temp = new Group(snpGroup);
						innerGroup.getChildren().addAll(temp.getChildren());
					} else if (checkboxInsert.isSelected()) {
						innerGroup.getChildren().clear();
						Group temp = new Group(indelGroup);
						innerGroup.getChildren().addAll(temp.getChildren());
					} else {
						innerGroup.getChildren().clear();
						Group temp = new Group(normalGroup);
						innerGroup.getChildren().addAll(temp.getChildren());
						scrollPane.setHvalue(oldBarValue);
					}
				} else if (prevScale < GRAPH && scale >= GRAPH) {
					innerGroup.getChildren().clear();
					Group temp = new Group(otherGroup);
					innerGroup.getChildren().addAll(temp.getChildren());
					scrollPane.setHvalue(oldBarValue);
				}
				scrollPane.setHvalue(oldBarValue);
				double barValue = scrollPane.getHvalue();
				scrollPane.setHvalue(barValue);
				otherGroup.setScaleX(scale);
				otherPane.setHvalue(barValue);
				
				annotationRibbonPane.setHvalue(barValue);
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
	 * function that gets executed when the matching fxml file is loaded.
	 * 
	 * The group is from within the FXML file. We use that group to add events and the pannable 
	 * canvas on which the drawing of the ribbon takes place.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateView();
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		
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
				annotationRibbonPane.setHvalue(newValue.doubleValue());
			}
		});
		
		innerGroup.scaleXProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				double scale = newValue.doubleValue();
				if (scale > COLLAPSE && scale <= GRAPH) {
					disableCheckBoxes.set(false);
				} else {
					disableCheckBoxes.set(true);
					if (scale > GRAPH) {
						checkboxLabelText.set("Zoom out to enable selections");
					} else {
						checkboxLabelText.set("Zoom in to enable selections");
					}
				}
			}
		});
		
		checkboxSnp.disableProperty().bind(disableCheckBoxes);
		checkboxInsert.disableProperty().bind(disableCheckBoxes);
		checkboxesActiveLabel.visibleProperty().bind(disableCheckBoxes);
		checkboxesActiveLabel.textProperty().bind(checkboxLabelText);

		checkboxInsert.selectedProperty().addListener(
				(ChangeListener<Boolean>) (observable, oldValue, newValue) -> { 
					
					double scale = innerGroup.getScaleX();
					double scroll = scrollPane.getHvalue();
					
					if (scale >= COLLAPSE && scale <= GRAPH) {
						if (oldValue == false) {
							if (checkboxSnp.isSelected()) {
								checkboxSnp.selectedProperty().setValue(false);
								checkboxSnp.setSelected(false);
							}
							innerGroup.getChildren().clear();
							Group temp = new Group(indelGroup);
							innerGroup.getChildren().addAll(temp.getChildren());
						} else if (oldValue == true) {
							innerGroup.getChildren().clear();
							Group temp = new Group(normalGroup);
							innerGroup.getChildren().addAll(temp.getChildren());
						}
					}
					scrollPane.setHvalue(scroll);
				}
			);
			
		checkboxSnp.selectedProperty().addListener(
			(ChangeListener<Boolean>) (observable, oldValue, newValue) -> { 
					
					double scale = innerGroup.getScaleX();
					double scroll = scrollPane.getHvalue();
					
					if (scale >= COLLAPSE && scale <= GRAPH) {
						if (oldValue == false) {
							if (checkboxInsert.isSelected()) {
								checkboxInsert.selectedProperty().setValue(false);
								checkboxInsert.setSelected(false);
							}
							innerGroup.getChildren().clear();
							Group temp = new Group(snpGroup);
							innerGroup.getChildren().addAll(temp.getChildren());
						} else if (oldValue == true) {
							innerGroup.getChildren().clear();
							Group temp = new Group(normalGroup);
							innerGroup.getChildren().addAll(temp.getChildren());
						}
					}
					scrollPane.setHvalue(scroll);
				}
			);
		addBindings();
	}
	
	private void addBindings() {
		minScaleProperty.unbind();
		innerGroup.scaleYProperty().unbind();
		innerGroup.scaleXProperty().unbind();
		
		minScaleProperty.bind(scrollPane.widthProperty()
				.divide(innerGroup.boundsInLocalProperty().get().getWidth()));
		double groupHeight = innerGroup.getBoundsInLocal().getHeight();
		innerGroup.scaleYProperty().bind(scrollPane.heightProperty().divide(groupHeight));
		innerGroup.scaleXProperty().bind(minScaleProperty.add(scaleOffSetProperty));
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
		
		addBindings();
	}

	public void redraw() {
		collapsedGroup = ribbonView.createCollapsedRibbons();
		normalGroup = ribbonView.createNormalRibbons();
		updateView();
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
		annotationRibbonGroup.scaleXProperty()
			.bind(innerGroup.scaleXProperty()
					.multiply(innerGroup.boundsInLocalProperty().get().getWidth())
					.divide(annotationRibbonGroup.boundsInLocalProperty().get().getWidth()));
	}
	
	public void setAnnotationRibbonPane(ScrollPane scrollpane) {
		annotationRibbonPane = scrollpane;
	}

	public RibbonView getRibbonView() {
		return ribbonView;
	}
	
	public void setSelectedContentProperty(StringProperty property) {
		this.selectedNodeContent = property;
		this.textArea.textProperty().bind(selectedNodeContent);
	}

}
