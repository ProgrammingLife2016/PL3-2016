package gui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import models.phylogeny.NewickTree;
import parsers.NewickTreeParser;
import javafx.stage.Stage;
import db.DatabaseManager;
import gui.GuiPreProcessor;
import gui.ImportHandler;
import gui.Launcher;
import gui.controllers.phylogeny.PhylogenyController;
import gui.controllers.ribbon.GraphController;
import gui.controllers.ribbon.RibbonController;
import gui.views.phylogeny.NewickNode;

import org.apache.commons.io.FilenameUtils;
import toolbar.ExistingHandler;
import toolbar.RecentHandler;




/**
 * @author Björn Ho
 */
public class MainController implements Initializable {
	/**
	 * You can interact with an individual tab page if you need it. (Uncomment
	 * if necessary).
	 */
	// @FXML private GridPane ribbonTab;
	@FXML private TabPane tabPane;
	
	/**
	 * Access to the controllers to update the view when changing files.
	 */
	@FXML private GraphController graphTabController;
	@FXML private RibbonController ribbonTabController;
	@FXML private AnnotationController annotationsController;
	@FXML private PhylogenyController phyloTabController;
	//@FXML private PhyloMenuController phyloMenuController;
	//@FXML private GridPane phyloGridPane;
	
	/**
	 * Access to the scene window.
	 */
	@FXML private VBox verticalBox;
	
	/**
	 * Access to the menus of the toolbar, to add new sub menus.
	 */
	@FXML private Menu recentMenu;
	@FXML private Menu existingMenu;
	
	@FXML private GridPane aboutPane;
	@FXML private GridPane controlsPane;
	@FXML private GridPane graphHelpPane;

	/**
	 * Adds the recent and existing items into the toolbar menus.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addItems(new RecentHandler().getRecent(), recentMenu);
		addItems(new ExistingHandler().buildExistingMap(), existingMenu);
		graphTabController.setRibbonPane(ribbonTabController.getScrollPane());
		graphTabController.setRibbonGroup(ribbonTabController.getInnerGroup());
		ribbonTabController.setGraphPane(graphTabController.getScrollPane());
		ribbonTabController.setGraphGroup(graphTabController.getInnerGroup());
		ribbonTabController.setAnnotationRibbonGroup(annotationsController.getInnerGroup());
		ribbonTabController.setAnnotationRibbonPane(annotationsController.getScrollPane());
		ribbonTabController.setSelectedContentProperty(graphTabController.getGraphView()
				.getSelectedContentProperty());
		//phyloMenuController.setOuterPane(phyloGridPane);
		tabPane.getTabs().get(2).setDisable(true);
		tabPane.getSelectionModel().selectedItemProperty().addListener(
			    new ChangeListener<Tab>() {
			        @Override
			        public void changed(ObservableValue<? extends Tab> ov, Tab t1, Tab t2) {
			        	if (t2.getText().startsWith("M") && phyloTabController.getNewickNode()
			        			.getChanged()) {
			        		ArrayList<String> genomeNames = phyloTabController.getNewickNode()
			        				.getSelectedGenomes();
			        		ArrayList<Integer> genomeIds = 
			        				Launcher.getDatabaseManager().getDbReader()
			        				.getGenomeIds(genomeNames);
			        		ribbonTabController.getRibbonView().setGenomeIds(genomeIds);
			        		graphTabController.getGraphView().setGenomeIds(genomeIds);
			        		graphTabController.redraw();
			        		ribbonTabController.setGraphGroup(graphTabController.getInnerGroup());
			        		ribbonTabController.redraw();
			        		NewickNode.setChanged(false);
			        	}
			        }
			    }
			);
	}
	
	/**
	 * Used to add recent items into the toolbar menus. When clicked, it will fire the 
	 * event handler to open the specified item.
	 * @param map	A hashmap which contains the file name as key and the directory as value.
	 * @param menu	The menu in which these items get added into.
	 */
	private void addItems(HashMap<String, String> map, Menu menu) {
		for (String name : map.keySet()) {
			MenuItem item = new MenuItem();
			item.setText(name);
			item.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent actionEvent) {
			        openExisting(map.get(name), name);
			    }
			});
			menu.getItems().add(item);
		}
	}
	
	/**
	 * Used to import a new .gfa file. It will open up a file explorer to browse to your file.
	 * It will build the recently opened file submenu and the existing submenu accordingly.
	 * It also checks if the import is really new. If it is, then getting an existing database
	 * should return null and it will start the import, else it will open it as an existing file.
	 * @param actionEvent	An Event representing some type of action
	 */
	 public void importNew(final ActionEvent actionEvent) {
		 final FileChooser fileExplorer = new FileChooser();
		 fileExplorer.getExtensionFilters().addAll(new ExtensionFilter("gfa files", "*.gfa"));
		 File file = fileExplorer.showOpenDialog(verticalBox.getScene().getWindow());

		 if (file != null) {
			 RecentHandler recent = new RecentHandler();
			 final String fileName = FilenameUtils.removeExtension(file.getName());
			 final String dbPath = System.getProperty("user.dir") 
						+ File.separator + "db" + File.separator + fileName;
			 if (new ExistingHandler().buildExistingMap().get(fileName) == null) {
				 recent.buildRecent(dbPath, fileName);
				 ImportHandler importer = new ImportHandler(
						 Launcher.getStage(), file.getAbsolutePath(), fileName);
				 importer.startImport();
				 updateExisting();
			 } else {
				 openExisting(dbPath, fileName);
			 }	
         }
	 }
	 
	 /**
	  * Used to open an existing database. It will first close the existing database
	  * connection. After that it will update the recently opened menu order.
	  * Finally it will update the views to display the graphs correctly.
	  * @param dbPath
	  * @param name
	  */
	 private void openExisting(String dbPath, String name) {
		System.out.println("opening existing");
		Launcher.setDatabaseManager(new DatabaseManager(dbPath));
		updateRecent(dbPath, name);
//		ribbonTabController.updateView();
//		graphTabController.updateView();
		
		Parent root;
		Stage stage = Launcher.getStage();
		try {
			root = FXMLLoader.load(getClass().getClassLoader().getResource("splashScreen.fxml"));
			Scene scene = new Scene(root);
	        stage.setScene(scene);
	        stage.centerOnScreen();
	        stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        GuiPreProcessor preProcessor = new GuiPreProcessor();
        Launcher.setPreprocessor(preProcessor);
    	final String nwkPath = System.getProperty("user.dir") 
				+ "/Data/" + name + "/" + "340tree.rooted.TKK.nwk";
    	
        Task<Void> task = new Task<Void>() {
            @Override 
            public Void call() {
				try {
					NewickTree tree = NewickTreeParser.parse(new File(nwkPath));
					Launcher.setNewickTree(tree);
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
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
            }
            };
            new Thread(task).start();     
	 }
	 
	 /**
	  * Used to update the recent menu in the toolbar. It will first build it and 
	  * then add the result to the menu on the toolbar.
	  * @param dbPath		path to these database
	  * @param name			name of the file
	  */
	 private void updateRecent(String dbPath, String name) {
			RecentHandler recentHandler = new RecentHandler();
			recentHandler.buildRecent(dbPath, name);
			recentMenu.getItems().clear();
			addItems(recentHandler.getRecent(), recentMenu);
	 }
	 
	 /**
	  * Used to update the existing menu in the toolbar. It will first build it and 
	  * then add the result to the menu on the toolbar.
	  */
	 private void updateExisting() {
			ExistingHandler existHandler = new ExistingHandler();
			existingMenu.getItems().clear();
			addItems(existHandler.buildExistingMap(), existingMenu);
	 }
	 
	 private void closeAllInfo() {
		 closeControls();
		 closeAbout();
		 closeGraphHelp();
	 }
	 
	 /**
	  * show the hidden pane
	  */
	 public void openControls() {
		 closeAllInfo();
		 controlsPane.setOpacity(1);
		 controlsPane.setDisable(false);
	 }
	 
	 /**
	  * close controls pane
	  */
	 public void closeControls() {
		 controlsPane.setOpacity(0);
		 controlsPane.setDisable(true);
	 }
	 
	 /**
	  * shows the hidden pane
	  */
	 public void openAbout() {
		 closeAllInfo();
		 aboutPane.setOpacity(1);
		 aboutPane.setDisable(false);
	 }
	 
	 /**
	  * closing about pane
	  * @param value mouse click
	  */
	 public void closeAbout() {
		 aboutPane.setOpacity(0);
		 aboutPane.setDisable(true);
	 }
	 
	 public void openGraphHelp() {
		 closeAllInfo();
		 graphHelpPane.setOpacity(1);
		 graphHelpPane.setDisable(false);
	 }
	 
	 public void closeGraphHelp() {
		 graphHelpPane.setOpacity(0);
		 graphHelpPane.setDisable(true);
	 }
	 
	 /**
	  * This executes whenever quit has been activated from
	  * the toolbar. It quits the program.
	  * @param actionEvent	An Event representing some type of action
	  */
	 @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "DM_EXIT", 
             justification = "It is intended to exit the program.")
	 public void quit(final ActionEvent actionEvent) {
		 System.exit(0);
	 }
}