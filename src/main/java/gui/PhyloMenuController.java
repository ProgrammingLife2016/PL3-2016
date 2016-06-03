package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
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

	/**
	 * Initialize fxml file.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
			System.out.println(newValue.getHeight());
		});
		setUpAnchorPane();
	}
	
	private void setUpAnchorPane() {
		
	}
}
