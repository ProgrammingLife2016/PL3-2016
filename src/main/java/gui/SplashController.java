package gui;

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
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
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
	public static boolean doneLoading = false;
	
	@FXML Label progressText;
	@FXML ProgressBar progressBar;
	@FXML VBox vBox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			start(Launcher.stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
    public void start(final Stage initStage) throws Exception {
        final Task<Void> friendTask = new Task<Void>() {
            @Override
            protected Void call() throws InterruptedException {
    		    progressString.addListener(new ChangeListener<String>() {
    		
  			      @Override
  			      public void changed(ObservableValue<? extends String> ov, String oldVal,
  			          String newVal) {
  			        System.out.println("old value:"+oldVal);
  			        System.out.println("new value:"+newVal);
  		            updateMessage(newVal);
  			      }
      		    });
    		    
    		  progressNum.addListener(new ChangeListener<Number>() {
    			  @Override
		      public void changed(ObservableValue<? extends Number> ov, Number oldVal,
		          Number newVal) {
		        System.out.println("old value:"+oldVal);
		        System.out.println("new value:"+newVal);
		        updateProgress(newVal.longValue(), 100);
		      }
		    });
    		    
    		    return null;
            }
        };
        
        System.out.println("Showing splash");
        showSplash(
                initStage,
                friendTask);
        
        Thread th = new Thread(friendTask);

        th.setDaemon(true);

        th.start();
       // new Thread(friendTask).start();
    }
	
	
	
    public interface InitCompletionHandler {
        void complete();
    }
	
	
	
	
	
	
	
    
    private void showSplash(
            final Stage initStage,
            Task<?> task
    ) {
    	System.out.println("show splash called");
        progressText.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());
        task.stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
            	System.out.println("worker succeeded");
            	//progressBar.progressProperty().unbind();
            	//progressBar.setProgress(1);
                initStage.toFront();
//                FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
//                fadeSplash.setFromValue(1.0);
//                fadeSplash.setToValue(0.0);
//                fadeSplash.setOnFinished(actionEvent -> initStage.hide());
//                fadeSplash.play();

            } 
        });
        
    }
	
	
	
	

}
