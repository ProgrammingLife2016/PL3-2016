package gui;
import java.util.ArrayList;

import db.DatabaseManager;
import db.GfaException;
import db.GfaParser;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;

/**
 * A temporary launcher with a zoomable and pannable canvas.
 */

@SuppressWarnings("restriction")
public class OldLauncher extends Application {
	 Scene scene;
	 Stage currentStage;
	 int position = 0;
	 ArrayList<PannableCanvas> canvasList = new ArrayList<PannableCanvas>();
	      
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
    	currentStage = stage;
    	String filename = "example";
    	String gfaPath = System.getProperty("user.dir") + "/Data/" + filename + "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		DatabaseManager dbm = new DatabaseManager(dbPath);
		GfaParser parser = new GfaParser(dbm);
		try {
			parser.parse(gfaPath);
		} catch (GfaException e) {
			e.printStackTrace();
		}
    	
    	// CREATING THE INITIAL GROUP TO DISPLAY
        PannableCanvas canvas = new PannableCanvas();
        canvas.setTranslateX(100);
        canvas.setTranslateY(100);
        NodeGestures nodeGestures = new NodeGestures(canvas);
        RibbonDrawer ribbonDrawer = new RibbonDrawer(dbm);
        Group group = ribbonDrawer.draw(canvas, nodeGestures);
        Label label1 = new Label("SCENE 1");
        label1.setTranslateX(10);
        label1.setTranslateY(10);
        canvas.getChildren().addAll(label1);
        group.getChildren().add(canvas);
        
        // CREATE THE SECOND SCENE FOR TESTING PURPOSES
        PannableCanvas canvas2 = new PannableCanvas();
        Label label12 = new Label("SCENE 2");
        label12.setTranslateX(10);
        label12.setTranslateY(10);
        canvas2.getChildren().addAll(label12);
        
        // SETTING UP THE INITIAL SCENE
        scene = new Scene(group, 1024, 768);
        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        currentStage.setScene(scene);
        currentStage.show();
        canvas.addGrid();
        canvas2.addGrid();
        canvasList.add(canvas);
        canvasList.add(canvas2);
    }
    
    public double clamp(double value, double min, double max) {
        if(Double.compare(value, min) < 0)
            return min;

        else if( Double.compare(value, max) > 0)
            return max;
        
        else
        return value;
    }
    
    public class SceneGestures {
        private final double MAX_SCALE = 10.0d;
        private final double MIN_SCALE = .1d;
        private DragContext sceneDragContext = new DragContext();
        PannableCanvas canvas;

        public SceneGestures(PannableCanvas canvas) {
            this.canvas = canvas;
        }

        public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
            return onMousePressedEventHandler;
        }

        public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
            return onMouseDraggedEventHandler;
        }

        public EventHandler<ScrollEvent> getOnScrollEventHandler() {
            return onScrollEventHandler;
        }

        private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if(!event.isSecondaryButtonDown())
                    return;
                sceneDragContext.mouseAnchorX = event.getSceneX();
                sceneDragContext.mouseAnchorY = event.getSceneY();
                sceneDragContext.translateAnchorX = canvas.getTranslateX();
                sceneDragContext.translateAnchorY = canvas.getTranslateY();

            }
        };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if(!event.isSecondaryButtonDown())
                    return;
                canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
                canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);
                event.consume();
            }
        };

        /**
         * Mouse wheel handler: zoom to pivot point
         */
        private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {
            	boolean changed = false;
                double delta = 1.2;
                double scale = canvas.getScale(); // currently we only use Y, same value is used for X
                double oldScale = scale;
                
                if (event.getDeltaY() < 0) {
                     scale /= Math.pow(delta, -event.getDeltaY()/20);
                } 
                
                else {
                     scale *= Math.pow(delta, event.getDeltaY()/20);
                }

                scale = clamp( scale, MIN_SCALE, MAX_SCALE);
                double zoom = scale/MAX_SCALE;
                System.out.println("Zoom percentage: " + zoom);
                
                if(zoom == 1.0 && position < canvasList.size() - 1) {
                	position++;
                	System.out.println("Zooming in");
                	changed = true;
                }
                else if(zoom == .01 && position > 0) {
                	position--;
                	System.out.println("Zooming out");
                	changed = true;
                }
                
                double f = (scale / oldScale)-1;
                double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth()/2 + canvas.getBoundsInParent().getMinX()));
                double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight()/2 + canvas.getBoundsInParent().getMinY()));
                canvas.setScale( scale);
                canvas.setPivot(f*dx, f*dy);
                
                if(changed) {
                	System.out.println("Switching to canvas #" + position);
                	canvas.reset();
                	canvas = canvasList.get(position);
                	System.out.println("Switching to scene #" + position);

                	Group groupTemp = new Group();
                	groupTemp.getChildren().add(canvasList.get(position));
                    Scene sceneTemp = new Scene(groupTemp, 1024, 768);
                	SceneGestures sceneGestures = new SceneGestures(canvasList.get(position));
                    sceneTemp.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
                    sceneTemp.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
                    sceneTemp.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
                	currentStage.setScene(sceneTemp);
                }
                event.consume();
            }
        };
    }
}


