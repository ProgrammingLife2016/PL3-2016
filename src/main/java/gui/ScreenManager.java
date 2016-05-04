package gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("restriction")
public class ScreenManager extends StackPane {
    private HashMap<String, Node> screens = new HashMap<>();
    public static final String RibbonLevelFXML = "RibbonLevel.fxml";
	public static Stage currentStage;
	public static ArrayList<PannableCanvas> canvasList = new ArrayList<PannableCanvas>();

    public ScreenManager() {
        super();
    }

    /**
     * Add a screen to a hashmap
     * 
     * @param name
     *            Name of the screen.
     * @param screen
     *            Loaded screen.
     */
    public void addScreen(String name, Node screen) {
        screens.put(name, screen);
    }

    /**
     * 
     * @param name
     *            Name of the screen
     * @return Node with the given name from the hashmap.
     */
    public Node getScreen(String name) {
        return screens.get(name);
    }

    /**
     * It loads a FXML file and adds the screen to the screen hashmap.
     * 
     * @param name
     *            Name of the screen.
     * @param resource
     *            Name of the fxml file.
     * @return Boolean, true if screen loading succeeds, else false.
     */
    public boolean loadScreen(String name, String resource) {
        try {
            FXMLLoader myLoader = new FXMLLoader(getClass().getResource(resource));
            Parent loadScreen = (Parent) myLoader.load();
            SetScreen myScreenControler = ((SetScreen) myLoader.getController());
            myScreenControler.setScreenDriver(this);
            addScreen(name, loadScreen);
            return true;
        } catch (Exception e) {
			System.err.println("Failed to load screen " + getClass().getResource(resource));
			e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the screen has already been loaded and switches screen with
     * screen transistion
     * 
     * @param name
     *            Name of the screen.
     * @return Boolean, true if setting the screen succeeds, else false.
     */
    public boolean setScreen(final String name) {
        if (screens.get(name) != null) {
            final DoubleProperty opacity = opacityProperty();
            // if there is more than one screen
            if (!getChildren().isEmpty()) { 
            	//the duration is the fade out time
            	Timeline fade = new Timeline(new KeyFrame(Duration.ZERO,
            		new KeyValue(opacity, 1.0)), new KeyFrame(new Duration(
                    250), new EventHandler<ActionEvent>() { 
                           
            			@Override
            			public void handle(ActionEvent ae) {
            			//removing the displayed screen
	                    getChildren().remove(0); 
	                    //adds the new screen
	                    getChildren().add(0, screens.get(name));
	                    //fade in time
	                    Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO,
	                    		new KeyValue(opacity, 0.7)), new KeyFrame(new Duration(250),
	                    		new KeyValue(opacity, 1.0)));
	                    fadeIn.play();
	                    }
                    }, new KeyValue(opacity, 0.7)));
                fade.play();
            } else {
                setOpacity(0.7);
                getChildren().add(screens.get(name));
                Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO,
                		new KeyValue(opacity, 0.7)), new KeyFrame(new Duration(
                        250), new KeyValue(opacity, 1.0)));
                fadeIn.play();
            }
            return true;
        } else {
			System.err.println("Could not set screen " + name);
            return false;
        }
    }

    /**
     * this removes (unloads) a screen from the hashmap of screens
     * 
     * @param name
     *            Name of screen.
     * @return Boolean, true if removed, false if nothing to remove.
     */
    public boolean unloadScreen(String name) {
        if (screens.remove(name) == null) {
			System.err.println("Screen didn't exist");
            return false;
        } else {
            return true;
        }
    }
}

