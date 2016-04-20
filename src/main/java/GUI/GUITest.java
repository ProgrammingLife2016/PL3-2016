package GUI;

import javafx.application.*;
import javafx.stage.Stage;
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


//Window is called the "Stage"
//Content inside window is called "Scene"

public class GUITest extends Application {
	
	Button button;
	
	public static void main (String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("DNAnalyzer");
		
		button = new Button();
		button.setText("Load DNA");
		
		StackPane layout = new StackPane();
		layout.getChildren().add(button);
		
		Scene scene = new Scene(layout, 400, 300); //layout, sizeh, sizev
		
		primaryStage.setScene(scene); 
		primaryStage.show(); //shows stage to user
		
	}
	
}
