package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import db.DatabaseManager;

/**
 * Class for dealing with the annotations. The elements are added to a
 * new group, which are added to a ScrollPane. This view will give an idea
 * of the actual length of the genome.
 */
public class PhyloMenuController implements Initializable {
	
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	@FXML private AnchorPane anchorPane;
	@FXML private Button expandButton;
	
	private boolean expanded = false;
	private GridPane outerPane;

	/**
	 * Event handler for mouse click event with the phylogenetic view.
	 */
	private final EventHandler<MouseEvent> expandButtonHandler = new EventHandler<MouseEvent>() {
		
		@Override
		public void handle(MouseEvent event) {
			if (expanded == false) {
				Timer animTimer = new Timer();
			    animTimer.scheduleAtFixedRate(new TimerTask() {

			        int i=0;

			        @Override
			        public void run() {
			            if (i<35){

			            	outerPane.getRowConstraints().get(1).setPrefHeight(outerPane.getRowConstraints().get(1).getPrefHeight() + 20);
			            }
			            else {
			                this.cancel();
			            }

			            i++;
			        }
			    }, 2000, 25);
				expanded = true;
			} else if (expanded == true) {
				Timer animTimer = new Timer();
			    animTimer.scheduleAtFixedRate(new TimerTask() {

			        int i=0;

			        @Override
			        public void run() {
			            if (i<25){

			            	outerPane.getRowConstraints().get(1).setPrefHeight(outerPane.getRowConstraints().get(1).getPrefHeight() - 20);
			            }
			            else {
			                this.cancel();
			            }

			            i++;
			        }
			    }, 2000, 25);
				expanded = false;
			}
		}
	};
	
	/**
	 * Initialize fxml file.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		expandButton.addEventHandler(MouseEvent.MOUSE_CLICKED, expandButtonHandler);
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		    anchorPane.setTopAnchor(expandButton, newValue.getHeight()/2);
		    anchorPane.setLeftAnchor(expandButton, newValue.getWidth()/2);
			System.out.println(newValue.getHeight());
		});
		setUpAnchorPane();
	}
	
	private void setUpAnchorPane() {
		
	}
	
	public void setOuterPane(GridPane pane) {
		outerPane = pane;
	}
}
