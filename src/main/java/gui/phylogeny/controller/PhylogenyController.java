package gui.phylogeny.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;

import gui.Launcher;
import gui.phylogeny.model.NewickAlgorithm;
import gui.phylogeny.model.NewickNode;

/**
 * Controller class for the Phylogenetic tree view.
 */
public class PhylogenyController implements Initializable {
	
	@FXML GridPane pane;
	@FXML ScrollPane scrollPane;
	private Group root;
	
	/**
	 * The upper boundary for zooming.
	 */
    private static final double MAX_SCALE = 100.0d;
    
    /**
     * The lower boundary for zooming.
     */
    private static final double MIN_SCALE = .1d;
    
	/**
	 * Handles the scroll wheel event for the phylogenetic view.
	 */
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			event.consume();
			
			if (event.isControlDown()) {
				double deltaY = event.getDeltaY();
				double delta = 1.2;
				double scale = root.getScaleY();

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}

				root.setScaleY(scale);
				root.setScaleX(scale);
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
	 * Event handler for keyboard events with the phylogenetic view.
	 */
	private final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
		
		@Override
		public void handle(KeyEvent event) {
			String character = event.getCharacter();
			if (!event.isControlDown()) {
				return;
			}

			double delta = 1.2;
			double scale = root.getScaleY();

			if (character.equals("+") || character.equals("=")) {
				scale *= delta;

				scale = scale > MAX_SCALE ? MAX_SCALE : scale;
			} else if (character.equals("-")) {
				scale /= delta;
				scale = scale < MIN_SCALE ? MIN_SCALE : scale;
			} else {
				return;
			}

			root.setScaleY(scale);
			root.setScaleX(scale);
		}
	};
	
	/**
	 * Initializes the phylogeny tree. Creating the basis for the
	 * phylogenetic tree to be created.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		NewickAlgorithm newickAlg = new NewickAlgorithm();
		newickAlg.parseLineages();
		NewickNode node = newickAlg.getDrawableTree(Launcher.nwkTree);
		node.setParentLineages();
		node.setColoured();
		
		node.setTranslateX(100);
		node.setTranslateY(100);
		
		root = new Group();
		root.getChildren().add(node);	
		scrollPaneSetup();
	}
	
	/**
	 * Setup basic properties of the ScrollPane.
	 */
	private void scrollPaneSetup() {
		scrollPane.setContent(root);
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		scrollPane.addEventFilter(KeyEvent.KEY_TYPED, keyEventHandler);
		
		// Resize the scrollpane along with the window.
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
	}
}
