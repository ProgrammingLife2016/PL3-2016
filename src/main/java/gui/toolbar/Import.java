package gui.toolbar;

import java.io.File;
import java.io.IOException;

import db.DatabaseManager;
import gui.Launcher;
import gui.SplashController;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import parsers.GfaException;
import parsers.GfaParser;

public class Import {
	private Stage stage;
	private final String gfaPath;
	private final String fileName;
	
	public Import(Stage stage, String gfaPath, String fileName) {
		this.stage = stage;
		this.gfaPath = gfaPath;
		this.fileName = fileName;
	}
	
	public void startImport() {
		final String dbPath = System.getProperty("user.dir") 
				+ "/db/" + fileName;
		
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

        
        Task<Void> task = new Task<Void>() {
            @Override 
            public Void call() throws Exception {
        			Launcher.dbm = new DatabaseManager(dbPath);
        			GfaParser parser = new GfaParser(Launcher.dbm);
        			SplashController.progressNum.set(10);
        			SplashController.progressString.set("Start Parsing");
        			try {
        				parser.parse(gfaPath);
        			} catch (GfaException e) {
        				e.printStackTrace();
        			}
        			SplashController.progressString.set("Start Calculating");
        			Launcher.dbm.getDbProcessor().calculateLinkCounts();
        			SplashController.progressNum.set(60);
        			Launcher.dbm.getDbProcessor().updateCoordinates();
        			SplashController.progressNum.set(100);
        		
                return null ;
            }
        };
        
        new Thread(task).start();
	}
	
	
}
