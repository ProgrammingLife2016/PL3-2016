package gui;

import db.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;
import parsers.GfaException;
import parsers.GfaParser;

public class Launcher extends Application{
	public static DatabaseManager dbm;
	public static ScreenManager scm;
		
	@Override
	public void start(Stage stage) throws Exception {
		String filename = "TB10";
    	String gfaPath = System.getProperty("user.dir") + "/Data/" + filename + "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		dbm = new DatabaseManager(dbPath);
		ScreenManager mainContainer = new ScreenManager();
		
		ScreenManager.currentStage = stage;
		GfaParser parser = new GfaParser(dbm);
		try {
			parser.parse(gfaPath);
		} catch (GfaException e) {
			e.printStackTrace();
		}
		
		stage.setMinHeight(480);
        stage.setMinWidth(640);
        
		mainContainer.loadScreen("RibbonLevel", ScreenManager.RibbonLevelFXML);
		scm = mainContainer;
		mainContainer.setScreen("RibbonLevel");
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
