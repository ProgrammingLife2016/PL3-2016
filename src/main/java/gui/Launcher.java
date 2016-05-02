package gui;

import db.DatabaseManager;
import db.GfaException;
import db.GfaParser;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class Launcher extends Application{
	public static DatabaseManager dbm;
	public static final String RibbonLevelFXML = "RibbonLevel.fxml";
	public static Stage currentStage;
	private ScreenManager scm;
	@Override
	public void start(Stage stage) throws Exception {
		String filename = "example";
    	String gfaPath = System.getProperty("user.dir") + "/Data/" + filename + "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		this.dbm = new DatabaseManager(dbPath);
		currentStage = stage;
//		GfaParser parser = new GfaParser(dbm);
//		try {
//			parser.parse(gfaPath);
//		} catch (GfaException e) {
//			e.printStackTrace();
//		}
		
		stage.setMinHeight(480);
        stage.setMinWidth(640);
        
		ScreenManager mainContainer = new ScreenManager();
		 mainContainer.loadScreen("RibbonLevel", Launcher.RibbonLevelFXML);
	        this.scm = mainContainer;
	        mainContainer.setScreen("RibbonLevel");
	        
//	        Scene scene = new Scene(mainContainer);   
//	        stage.setScene(scene);
//
//	        stage.show();
	        
//        StackPane root = new StackPane();
//        stage.setScene(new Scene(root, 300, 250));
//        stage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
