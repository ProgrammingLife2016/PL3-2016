package gui;

import java.io.File;
import java.io.IOException;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import gui.phylogeny.model.NewickTree;
import parsers.GfaException;
import parsers.GfaParser;
import parsers.GffParser;
import parsers.NewickTreeParser;
import toolbar.RecentHandler;

import db.DatabaseManager;

/**
 * This launcher starts up our program.
 * @author Björn Ho
 */
public class Launcher extends Application {
	public static DatabaseManager dbm;
	static Stage stage;
	public static NewickTree nwkTree = null;
	
	@Override
	public void start(Stage stage) throws Exception {
		Launcher.setStage(stage,"DNA Lab");
		
		final String filename = "TB10";
		final String gfaPath = System.getProperty("user.dir") 
				+ "/Data/" + filename + "/" + filename + ".gfa";
		final String dbPath = System.getProperty("user.dir") 
				+ File.separator + "db" + File.separator + filename;
		final String nwkPath = System.getProperty("user.dir") 
				+ "/Data/" + filename + "/" + "340tree.rooted.TKK.nwk";
		final String gffPath = System.getProperty("user.dir") 
				+ "/Data/" + filename + "/" + "decorationV5_20130412.gff";
		final File database = new File(dbPath + ".mv.db");
	
		try {
			NewickTree tree = NewickTreeParser.parse(new File(nwkPath));
			setNewickTree(tree);
			
		} catch (IOException e) {
			System.err.println("File: " + nwkPath + " not found");
		}
		
		RecentHandler rgfa = new RecentHandler();
		rgfa.buildRecent(dbPath, filename);

		/**
		 * Loads up splash screen and display it.
		 */
		Parent root = FXMLLoader.load(getClass().getClassLoader()
				.getResource("splashScreen.fxml"));
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
            		Launcher.setDbManager(new DatabaseManager(dbPath));
        			GfaParser parser = new GfaParser(dbm);
        			GffParser gffparser = new GffParser(dbm);
        			SplashController.progressNum.set(10);
        			SplashController.progressString.set("Start Parsing");
        			try {
        				parser.parse(gfaPath);
        				gffparser.parse(gffPath);
        			} catch (GfaException e) {
        				e.printStackTrace();
        			}
        			SplashController.progressString.set("Start Calculating");
        			dbm.getDbProcessor().calculateLinkCounts();
        			SplashController.progressNum.set(60);
        			dbm.getDbProcessor().updateCoordinates();
        			dbm.getDbProcessor().locateBubbles();	
        			SplashController.progressNum.set(100);
        		} else {
        			dbm = new DatabaseManager(dbPath);
        			SplashController.progressNum.set(100);
        		}
                return null ;
            }
        };
        new Thread(task).start();
	}
	
	public static void setDbManager(DatabaseManager databaseManager) {
		Launcher.dbm = databaseManager;
	}

	private static void setNewickTree(NewickTree tree) {
		Launcher.nwkTree = tree;
	}

	private static void setStage(Stage stage, String title) {
		Launcher.stage = stage;
		Launcher.stage.setTitle(title);
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
