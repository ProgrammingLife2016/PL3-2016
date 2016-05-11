package gui;

import java.io.File;
import db.DatabaseManager;
import db.GfaException;
import db.GfaParser;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This launcher starts up our program.
 * @author Björn Ho
 */
public class Launcher extends Application {
	public static DatabaseManager dbm;
	public static Stage stage;
	
	@Override
	public void start(Stage stage) throws Exception {
		Launcher.stage = stage;
		final String filename = "TB10";
    	final String gfaPath = System.getProperty("user.dir") + "/Data/" + filename
    			+ "/" + filename + ".gfa";
		final String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		final File database = new File(dbPath + ".mv.db");
		
		/**
		 * Loads up splash screen and display it.
		 */
		Parent root = FXMLLoader.load(getClass().getResource("splashScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
		
        /**
         * Make a new task and check whether the database needs to be parsed or not.
         * Database operations and parsing must be done on a separate thread or else
         * the UI will not be responsive.
         */
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
	}
	
	/**
	 * This method is ignored in a correct JavaFX program.
	 * This is just a fallback for IDE's with limited or no support 
	 * for JavaFX.
	 * 
	 * @param args		The arguments given through the command line.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
