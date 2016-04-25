package gui;

import java.io.File;

import main.Main;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.*;

@SuppressWarnings("restriction")
public class Gui extends Application {
	
	private LoadControl loadcontrol = new LoadControl();
	private ExitControl exitcontrol = new ExitControl();
	
	/**
	 * GUI setup.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("DNAnalyzer");  //Stage = window
		String stylepath = "Styles/menubar.css";
		
		BorderPane layout = new BorderPane();
		MenuBar mainMenuBar = createMainMenuBar();
		mainMenuBar.getStylesheets().add(stylepath);
		mainMenuBar.getStyleClass().add("menubar");
		//mainMenuBar.setStyle("-fx-text-fill: #6DDB07; -fx-background-color: black;");
		
		layout.setTop(mainMenuBar);
		//layout.setStyle("-fx-background-color: #6DDB07;");
		layout.getStyleClass().add("scene");
		
		Scene scene = new Scene(layout, 1000, 400); //layout, sizeh, sizev
		scene.getStylesheets().add(stylepath);
		
		primaryStage.setScene(scene); //Scene = inside window
		primaryStage.show(); //shows stage to user
	}	
	
	public MenuBar createMainMenuBar() {
		Menu mainmenu = new Menu("File");
		mainmenu.getStyleClass().add("menutext");
		//mainmenu.setStyle("-fx-background-colour: #6DDB07; -fx-text-fill: #6DDB07;");
		
		MenuItem menuload = new MenuItem("Load...");
		MenuItem menuexit = new MenuItem("Exit");
		menuload.getStyleClass().add("button");
		menuexit.getStyleClass().add("button");
		
		menuload.setOnAction(loadcontrol);
		menuexit.setOnAction(exitcontrol);
		
		SeparatorMenuItem separator = new SeparatorMenuItem();
		
		mainmenu.getItems().add(menuload);
		mainmenu.getItems().add(separator);
		mainmenu.getItems().add(menuexit);
		
		MenuBar mainmenubar = new MenuBar();
		mainmenubar.getMenus().add(mainmenu);
		
		return mainmenubar;
	}

	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
	}
	
//	public VBox createButtonPanel() {
//	    VBox vbox = new VBox();
//	    vbox.setPadding(new Insets(15, 15, 15, 15));
//	    vbox.setSpacing(10);
//	    vbox.setStyle("-fx-background-color: #336699;");
//
//		loadbutton = new Button();
//		loadbutton.setText("Load DNA"); //Name of button
//		loadbutton.setOnAction(loadcontrol); //If clicked, run "handle" located in "this" class
//		loadbutton.setPrefSize(100, 50);
//		
//		exitbutton = new Button();
//		exitbutton.setText("Exit");
//		exitbutton.setOnAction(exitcontrol);
//		exitbutton.setPrefSize(100, 50);
//
//	    vbox.getChildren().addAll(loadbutton, exitbutton);
//	    return vbox;
//	}
}
