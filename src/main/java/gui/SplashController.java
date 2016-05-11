package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class SplashController implements Initializable{

	public static SimpleIntegerProperty progressNum = new SimpleIntegerProperty(0);
	public static SimpleStringProperty progressString = new SimpleStringProperty("");
	
	@FXML Label progressText;
	@FXML ProgressBar progressBar;
	@FXML VBox vBox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			final Task<Void> listenerTask = listenerTask();
			Thread th = new Thread(listenerTask);
		    th.setDaemon(true);
		    progressText.textProperty().bind(listenerTask.messageProperty());
		    progressBar.progressProperty().bind(listenerTask.progressProperty());
		    th.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public Task<Void> listenerTask() throws Exception {
        final Task<Void> listenerTask = new Task<Void>() {
        	@Override
            public Void call() throws InterruptedException {
        		progressString.addListener(new ChangeListener<String>() {
  			    @Override
  			    public void changed(ObservableValue<? extends String> ov, String oldVal,
  			    String newVal) {
  			    	System.out.println("old value: " + oldVal);
  			        System.out.println("new value:" + newVal);
  		            updateMessage(newVal);
  			      }
      		    });
        		progressNum.addListener(new ChangeListener<Number>() {
    			@Override
    			public void changed(ObservableValue<? extends Number> ov, Number oldVal, 
		    	Number newVal) {
    				System.out.println("old value: " + oldVal);
    				System.out.println("new value: " + newVal);
    				updateProgress(newVal.longValue(), 100);
    				if ((int) newVal == 100) {
    					fadeOutSplash();
    				}
    			}
        	    });
        		return null;
            }
        };
        return listenerTask;
    }
	
	private void fadeOutSplash() {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				progressBar.progressProperty().unbind();
			    progressText.textProperty().unbind();
				progressBar.setProgress(1);
			    FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.0), vBox);
			    fadeSplash.setFromValue(1.0);
			    fadeSplash.setToValue(0.3);
			    fadeSplash.setDelay(Duration.seconds(1.0));
			    fadeSplash.setOnFinished(actionEvent -> launchMain());
			    fadeSplash.play();
			}
		});
    }
	
	private void launchMain() {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				Parent root;
				try {
					root = FXMLLoader.load(getClass().getResource("Main.fxml"));
			        Scene scene = new Scene(root);
			        Launcher.stage.setScene(scene);
			        Launcher.stage.show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
	}
}
