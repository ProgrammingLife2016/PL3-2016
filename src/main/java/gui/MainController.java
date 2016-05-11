package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

@SuppressWarnings("restriction")
public class MainController implements Initializable {
	/**
	 * You can interact with an indiviual tab page if you need it.
	 */
	@FXML private AnchorPane ribbonTab;
	
	/**
	 * Right now this MainController is empty but perhaps 
	 * there will be additions later on. Keeping it for now.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		ribbonTab.getChildren();
	}
}
