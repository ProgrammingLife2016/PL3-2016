package gui;

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
public class GUITest extends Application {
	
	private LoadControl loadcontrol = new LoadControl();
	private ExitControl exitcontrol = new ExitControl();
	
	/**
	 * Launch application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * GUI setup.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("DNAnalyzer");  //Stage = window
		
		BorderPane layout = new BorderPane();
		MenuBar mainMenuBar = createMainMenuBar();
		
		layout.setTop(mainMenuBar);
		layout.setStyle("-fx-background-color: #228899;");
		
		Scene scene = new Scene(layout, 1000, 400); //layout, sizeh, sizev
		
		primaryStage.setScene(scene); //Scene = inside window
		primaryStage.show(); //shows stage to user
	}	
	
	public MenuBar createMainMenuBar() {
		Menu mainmenu = new Menu("File");
		
		MenuItem menuload = new MenuItem("Load...");
		MenuItem menuexit = new MenuItem("Exit");
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
