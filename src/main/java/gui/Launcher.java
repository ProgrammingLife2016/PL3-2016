package gui;

import java.io.File;
import java.util.ArrayList;

import db.DatabaseManager;
import db.DatabaseProcessor;
import db.GfaException;
import db.GfaParser;
import javafx.application.Application;
import javafx.concurrent.Task;
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
	public static Stage stage;
	
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		String filename = "TB10";
    	String gfaPath = System.getProperty("user.dir") + "/Data/" + filename
    			+ "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		File database = new File(dbPath + ".mv.db");
		
		Parent root = FXMLLoader.load(getClass().getResource("splashScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        System.out.println("stage showing in main");
        stage.show();
		
		//Check to see whether the database needs to be parsed or not
        
        Task<Void> task = new Task<Void>() {
            @Override 
            public Void call() throws Exception {
            	if (!database.exists()) {
        			dbm = new DatabaseManager(dbPath);
        			GfaParser parser = new GfaParser(dbm);
        			SplashController.progressNum.set(10);
        			SplashController.progressString.set("Start Parsing");
        			try {
        				parser.parse(gfaPath);
        			} catch (GfaException e) {
        				e.printStackTrace();
        			}
        			SplashController.progressString.set("Start Calculating");
        			dbm.getDbProcessor().calculateLinkCounts();
        			SplashController.progressNum.set(60);
        			dbm.getDbProcessor().updateCoordinates();
        			SplashController.progressNum.set(100);
        		} else {
        			dbm = new DatabaseManager(dbPath);
        		}
                return null ;
            }
        };
        new Thread(task).start();
        
        
        
        
        
		
		
		SplashController.doneLoading = true;
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
