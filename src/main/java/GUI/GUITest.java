package GUI;

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

//Window is called the "Stage"
//Content inside window is called "Scene"

@SuppressWarnings("restriction")
public class GUITest extends Application {
	
	Button loadbutton;
	Button exitbutton;
	private LoadControl loadcontrol = new LoadControl();
	private ExitControl exitcontrol = new ExitControl();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("DNAnalyzer");
		
		loadbutton = new Button();
		loadbutton.setText("Load DNA"); //Name of button
		loadbutton.setOnAction(loadcontrol); //If clicked, run "handle" located in "this" class
		
		exitbutton = new Button();
		exitbutton.setText("Exit");
		exitbutton.setOnAction(exitcontrol);
		
		StackPane layout = new StackPane();
		layout.getChildren().add(loadbutton);
		layout.getChildren().add(exitbutton);
		
		Scene scene = new Scene(layout, 700, 400); //layout, sizeh, sizev
		
		primaryStage.setScene(scene); 
		primaryStage.show(); //shows stage to user
	}	
}
