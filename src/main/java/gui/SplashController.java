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
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Manages the splash screen when loading the program.
 * Loads main.fxml when finished.
 * @author Björn Ho
 */
public class SplashController implements Initializable{
	
	/**
	 * Those variables will be changed whenever progress is being made.
	 * Both implement ObservableValue so they can be observed when changes
	 * are made to those variables
	 */
	public static final SimpleIntegerProperty progressNum = 
			new SimpleIntegerProperty(0);
	public static final SimpleStringProperty progressString = 
			new SimpleStringProperty("");
	private ChangeListener<String> stringListener;
	private ChangeListener<Number> numberListener;
	
	/**
	 * These enable the FXMLLoader to inject values defined in a FXML file
	 * into references in this controller class
	 */
	@FXML Label progressText;
	@FXML ProgressBar progressBar;
	@FXML VBox verticalBox;
	
	/**
	 * Creating a new listenerTask and binding progressText and progressBar
	 * to the task so the task can update it later on.
	 * Note that this task runs on a new thread and NOT the JavaFX application thread.
	 */
	@SuppressWarnings("restriction")
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
	
	/**
	 * Makes a task in which listeners get added.
	 * updateMessage and updateProgress perform the updates on the FX application thread.
	 * @return Task		a Task to start.
	 */
    @SuppressWarnings("restriction")
	public Task<Void> listenerTask() {
        final Task<Void> listenerTask = new Task<Void>() {
			@Override
            public Void call() throws InterruptedException {
				stringListener = new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> ov, 
							String oldVal, String newVal) {
						updateMessage(newVal);
					}
				};
				numberListener = new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> ov, 
							Number oldVal, Number newVal) {
						updateProgress(newVal.longValue(), 100);
						if ((int) newVal == 100) {
							launchMain();
						}
					}
				};
				
        		progressString.addListener(stringListener);
        		progressNum.addListener(numberListener);
        		return null;
            }
        };
        return listenerTask;
    }
	
    /**
     * Performs the animation of fading out and when finished it calls launchMain.
     */
	@SuppressWarnings("restriction")
	private void fadeOutSplash() {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				progressBar.progressProperty().unbind();
			    progressText.textProperty().unbind();
				progressBar.setProgress(1);
				progressString.removeListener(stringListener);
				progressNum.removeListener(numberListener);
			    FadeTransition fadeSplash = new FadeTransition(
			    		Duration.seconds(1.0), verticalBox);
			    fadeSplash.setFromValue(1.0);
			    fadeSplash.setToValue(0.3);
			    fadeSplash.setDelay(Duration.seconds(1.0));
			    fadeSplash.setOnFinished(actionEvent -> launchMain());
			    fadeSplash.play();
			}
		});
    }
	
	/**
	 * Loads up Main.fxml on the Java FX Application thread.
	 * This shows up our main scene of our program.
	 */
	@SuppressWarnings("restriction")
	private void launchMain() {
		Platform.runLater(new Runnable(){
			@Override
			public void run() {
				Parent root;
				try {
					root = FXMLLoader.load(
							getClass().getClassLoader().getResource("Main.fxml"));
			        Scene scene = new Scene(root);
			        Launcher.stage.setScene(scene);
			        Launcher.stage.setMaximized(false);
			        Launcher.stage.setMaximized(true);
			        Launcher.stage.show();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});
	}
}
