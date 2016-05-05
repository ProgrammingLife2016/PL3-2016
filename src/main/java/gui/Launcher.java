package gui;

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
		String filename = "example";
    	String gfaPath = System.getProperty("user.dir") + "/Data/" + filename
    			+ "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
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
		
        Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
