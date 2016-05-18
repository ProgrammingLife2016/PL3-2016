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

import org.apache.commons.io.FilenameUtils;

import db.DatabaseManager;
import gui.toolbar.ExistingHandler;
import gui.toolbar.ImportHandler;
import gui.toolbar.RecentHandler;
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
	@FXML private Menu existingMenu;

	/**
	 * Right now this MainController is empty but perhaps there will be 
	 * additions later on. Keeping it for now.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		RecentHandler recentHandler = new RecentHandler();
		ExistingHandler existHandler = new ExistingHandler();
		LinkedHashMap<String, String> recentMap = recentHandler.getRecent();
		HashMap<String, String> existingMap = existHandler.buildExistingMap();
		addItems(recentMap, recentMenu);
		addItems(existingMap, existingMenu);
	}
	
	private void addItems(HashMap<String, String> map, Menu menu) {
		for(String name : map.keySet()) {
			MenuItem item = new MenuItem();
			item.setText(name);
			item.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
			        openRecent(map.get(name), name);
			    }
			});
			menu.getItems().add(item);
		}
	}
	
	 // public because fxml cannot access it otherwise.
	 public void importNew(final ActionEvent e) {

		 final FileChooser fileExplorer = new FileChooser();
		 fileExplorer.getExtensionFilters().addAll(new ExtensionFilter("gfa files", "*.gfa"));
		 File file = fileExplorer.showOpenDialog(verticalBox.getScene().getWindow());
		 if (file != null) {
			 RecentHandler recent = new RecentHandler();
			 String fileName = FilenameUtils.removeExtension(file.getName());
				final String dbPath = System.getProperty("user.dir") 
						+ File.separator + "db" + File.separator + fileName;
			 recent.buildRecent(dbPath, fileName);
			 ImportHandler importer = new ImportHandler(Launcher.stage, file.getAbsolutePath(), fileName);
			 importer.startImport();
         }
	 }
	 
	 private void openRecent(String dbPath, String name) {
		Launcher.dbm.closeDbConnection();
		Launcher.dbm = new DatabaseManager(dbPath);
		
		RecentHandler recentHandler = new RecentHandler();
		recentHandler.buildRecent(dbPath, name);
		recentMenu.getItems().clear();
		addItems(recentHandler.getRecent(), recentMenu);
		
		ExistingHandler existHandler = new ExistingHandler();
		existingMenu.getItems().clear();
		addItems(existHandler.buildExistingMap(), existingMenu);
		
		ribbonTabController.updateView();
		graphTabController.updateView();
	 }
	 
	 public void Quit(final ActionEvent e) throws IOException {
		 System.exit(0);
	 }
}
