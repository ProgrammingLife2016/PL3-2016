package GUI;

import java.io.File;

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

public class TutorialSceneswitch extends Gui implements EventHandler<ActionEvent> {
	
	Stage window;
	Scene scene1, scene2;
	Button button, button2;
	
	@SuppressWarnings("restriction")
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		
		//Buttons
		button = new Button("Switch to Scene 2");
		button.setOnAction(this);
		button2 = new Button("Back to Scene 1");
		button2.setOnAction(this);
		
		//Labels
		Label label1 = new Label("Scene 1");
		Label label2 = new Label("Scene 2");
		
		
		VBox layout1 = new VBox(20);
		layout1.getChildren().addAll(label1, button);
		scene1 = new Scene(layout1, 200, 200);
		
		StackPane layout2 = new StackPane();
		layout2.getChildren().add(button2);
		scene2 = new Scene(layout2, 200, 200);
		
		window.setScene(scene1);
		window.setTitle("Switch scene test");
		window.show();
	}
	
	@SuppressWarnings("restriction")
	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() == button ) {
			window.setScene(scene2);
		}
		if (event.getSource() == button2 ) {
			window.setScene(scene1);
		}
	}
	
	
	
}
