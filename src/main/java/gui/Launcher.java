package gui;

import java.io.File;
import java.util.ArrayList;

import db.DatabaseManager;
import db.DatabaseProcessor;
import db.GfaException;
import db.GfaParser;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This launcher which starts the application.
 */
@SuppressWarnings("restriction")
public class Launcher extends Application {
	public static DatabaseManager dbm;
		
	@Override
	public void start(Stage stage) throws Exception {

		String filename = "TB10";
    	String gfaPath = System.getProperty("user.dir") + "/Data/" + filename
    			+ "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		
		File database = new File(dbPath + ".mv.db");
			
		//Check to see whether the database needs to be parsed or not
		if (!database.exists()) {
			dbm = new DatabaseManager(dbPath);
			GfaParser parser = new GfaParser(dbm);
			System.out.println("Start Parsing");
			try {
				parser.parse(gfaPath);
			} catch (GfaException e) {
				e.printStackTrace();
			}
			System.out.println("Start Calculating");
			dbm.getDbProcessor().calculateLinkCounts();
			dbm.getDbProcessor().updateCoordinates();
		} else {
			dbm = new DatabaseManager(dbPath);
		}
		
        Parent root = FXMLLoader.load(getClass().getResource("splashScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
