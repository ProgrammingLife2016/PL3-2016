package gui;

import db.DatabaseManager;
import db.DatabaseProcessor;
import db.GfaException;
import db.GfaParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class Launcher extends Application {
	public static DatabaseManager dbm;
	public static ScreenManager scm;
		

	@Override
	public void start(Stage stage) throws Exception {
		String filename = "example";
    	String gfaPath = System.getProperty("user.dir") + "/Data/" + filename
    			+ "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		dbm = new DatabaseManager(dbPath);
		
		ScreenManager.currentStage = stage;
		GfaParser parser = new GfaParser(dbm);
		System.out.println("Start Parsing");
		try {
			parser.parse(gfaPath);
		} catch (GfaException e) {
			e.printStackTrace();
		}
		System.out.println("Start Calculating");
		dbm.getDbProcessor().calculateLinkCounts();
		
		
		
		stage.setMinHeight(480);
        stage.setMinWidth(640);
        
		ScreenManager mainContainer = new ScreenManager();
		mainContainer.loadScreen("Main", ScreenManager.MainFXML);
		scm = mainContainer;
		mainContainer.setScreen("Main");
		
		
		Scene scene = new Scene(mainContainer);
		System.out.println("showing");
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
