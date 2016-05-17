package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import java.util.ResourceBundle;

import db.DatabaseManager;
import gui.toolbar.ImportGfa;
import gui.toolbar.OpenRecent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author Björn Ho
 */
public class MainController implements Initializable {
	/**
	 * You can interact with an individual tab page if you need it. (Uncomment
	 * if necessary).
	 */
	// @FXML private GridPane ribbonTab;
	// @FXML private TabPane tabPane;
	
	@FXML private GraphController graphTabController;
	@FXML private RibbonController ribbonTabController;
	@FXML private VBox verticalBox;
	@FXML private Menu recentMenu;

	/**
	 * Right now this MainController is empty but perhaps there will be 
	 * additions later on. Keeping it for now.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addRecentItems();
		
	}
	
	private void addRecentItems() {
		OpenRecent rGfa = new OpenRecent();
		LinkedHashMap<String, String> recentMap = rGfa.readRecent();
		ArrayList<String> tmp = new ArrayList<String>();
		
		for (String name : recentMap.keySet()) {
			tmp.add(name);
		}
		
		for (int i = tmp.size() - 1; i >= 0; i--) {
			final String name = tmp.get(i);
			MenuItem item = new MenuItem();
			item.setText(name);
			item.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
			        openRecent(recentMap.get(name));
			    }
			});
			recentMenu.getItems().add(item);
		}
	}
	
	 // public because fxml cannot access it otherwise.
	 public void importNew(final ActionEvent e) {
		 final FileChooser fileExplorer = new FileChooser();
		 fileExplorer.getExtensionFilters().addAll(new ExtensionFilter("gfa files", "*.gfa"));
		 File file = fileExplorer.showOpenDialog(verticalBox.getScene().getWindow());
		 if (file != null) {
			 ImportGfa importer = new ImportGfa(Launcher.stage, file.getAbsolutePath(), file.getName());
			 importer.startImport();
         }
	 }
	 
	 private void openRecent(String dbPath) {
		 
		System.out.println(dbPath);
		Launcher.dbm.closeDbConnection();
		Launcher.dbm = new DatabaseManager(dbPath);
		ribbonTabController.updateView();
		graphTabController.updateView();
	 }
	 
	 public void Quit(final ActionEvent e) throws IOException {
		 System.exit(0);
	 }
}
