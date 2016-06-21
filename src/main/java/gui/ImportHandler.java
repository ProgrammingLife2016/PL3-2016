package gui;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import parsers.GfaParser;
import parsers.GffParser;
import db.DatabaseManager;
import gui.controllers.SplashController;

/**
 * Class used to handle importing a new .gfa file.
 * It is similar to Launcher.java, however it is better to have
 * a separate class to handle importing with specific directories
 * instead of extending functionalities of a launcher.
 * @author Björn Ho
 */
public class ImportHandler {
	
	/**
	 * Used for the constructor. See below for more details.
	 */
	private Stage stage;
	private final String gfaPath;
	private final String fileName;
	
	/**
	 * Constructor
	 * @param stage		Stage used for setting scenes.
	 * @param gfaPath	Path to the file you want to import.
	 * @param fileName	Name of the file you want to import.
	 */
	public ImportHandler(Stage stage, String gfaPath, String fileName) {
		this.stage = stage;
		this.gfaPath = gfaPath;
		this.fileName = fileName;
	}
	
	/**
	 * Imports the new .gfa file into the program 
	 * and update the screen accordingly with the new data.
	 */
	public void startImport() {
		final String dbPath = System.getProperty("user.dir") 
				+ "/db/" + fileName;
		final String gffPath = System.getProperty("user.dir") 
				+ "/Data/" + fileName + "/" + "decorationV5_20130412.gff";
		/**
		 * Loads up splash screen and display it.
		 */
		try {
			Parent root;
			root = FXMLLoader.load(getClass().getClassLoader().getResource("splashScreen.fxml"));
			Scene scene = new Scene(root);
	        stage.setScene(scene);
	        stage.centerOnScreen();
	        stage.show();
	        
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		/**
		 * Makes a new task for the import. See Launcher.java for more 
		 * details why a task is needed.
		 */
        Task<Void> task = new Task<Void>() {
            @Override 
            public Void call() {
            	try {
            		Launcher.setDbManager(new DatabaseManager(dbPath));
            		DatabaseManager dbm = Launcher.dbm;
        			GfaParser parser = new GfaParser(dbm);
        			GffParser gffparser = new GffParser(dbm);
        			parser.parse(gfaPath);
    				gffparser.parse(gffPath);
    				GuiPreProcessor preProcessor = new GuiPreProcessor();
    			    Launcher.setPreprocessor(preProcessor);
        			SplashController.progressNum.set(10);
        			SplashController.progressString.set("Start Parsing");
        			SplashController.progressString.set("Start Calculating");
        			dbm.getDbProcessor().calculateLinkCounts();
        			SplashController.progressNum.set(20);
        			dbm.getDbProcessor().updateCoordinates();
        			dbm.getDbProcessor().locateBubbles();	
        			SplashController.progressNum.set(30);
        			SplashController.progressString.set("Creating collapsed ribbons");
        			preProcessor.createCollapsedRibbons();
        			SplashController.progressString.set("Creating normal ribbons");
        			preProcessor.createNormalRibbons();
        			SplashController.progressNum.set(70);
        			SplashController.progressString.set("Creating snips");
        			preProcessor.createSnips();
        			SplashController.progressNum.set(80);
        			SplashController.progressString.set("Creating indels... Almost done");
        			preProcessor.createInDels();
        			SplashController.progressNum.set(100);
            	} catch (Throwable error) {
            		error.printStackTrace();
            	}
                return null ;
            }
        };
        new Thread(task).start();
	}
}