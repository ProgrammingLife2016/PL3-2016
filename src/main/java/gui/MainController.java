package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

public class MainController implements Initializable, SetScreen{
	
	@SuppressWarnings("unused")
	private ScreenManager screenManager;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setScreenDriver(ScreenManager screenPage) {
		this.screenManager = screenPage;
	
	}

}
