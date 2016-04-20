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
import javafx.geometry.*;

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
		
		StackPane layout = new StackPane();
		VBox buttonpanel = createButtonPanel();
		layout.getChildren().add(buttonpanel);
		
		Scene scene = new Scene(layout, 700, 400); //layout, sizeh, sizev
		
		primaryStage.setScene(scene); 
		primaryStage.show(); //shows stage to user
	}	
	
	public VBox createButtonPanel() {
	    VBox vbox = new VBox();
	    vbox.setPadding(new Insets(15, 12, 15, 12));
	    vbox.setSpacing(10);
	    vbox.setStyle("-fx-background-color: #336699;");

		loadbutton = new Button();
		loadbutton.setText("Load DNA"); //Name of button
		loadbutton.setOnAction(loadcontrol); //If clicked, run "handle" located in "this" class
		loadbutton.setPrefSize(100, 50);
		
		exitbutton = new Button();
		exitbutton.setText("Exit");
		exitbutton.setOnAction(exitcontrol);
		exitbutton.setPrefSize(100, 50);

	    vbox.getChildren().addAll(loadbutton, exitbutton);
	    return vbox;
	}
}
