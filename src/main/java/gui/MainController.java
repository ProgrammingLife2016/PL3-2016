package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class MainController implements Initializable {
	/**
	 * You can interact with an indiviual tab page if you need it.
	 */
	@FXML private GridPane ribbonTab;
	@FXML TabPane tabPane;
	
	/**
	 * Right now this MainController is empty but perhaps there will be additions later on. Keeping it for now.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		System.out.println(scrollPane.widthProperty().get());
//		ribbonTab.setPrefWidth(ribbonTab.boundsInParentProperty().get().getWidth());
//		ribbonTab.setPrefHeight(ribbonTab.boundsInParentProperty().get().getHeight());
	}
}
