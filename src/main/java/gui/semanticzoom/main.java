package gui.semanticzoom;
import gui.RibbonDrawer;

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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * An application with a zoomable and pannable canvas.
 */
@SuppressWarnings("restriction")
public class main extends Application {
	
    public static void main(String[] args) {
        launch(args);
    }

    ArrayList<PannableCanvas> canvases = new ArrayList<PannableCanvas>();
    Scene scene;
    Stage globStage;
    int position = 0;
    
    @Override
    public void start(Stage stage) {

    	Alert alert = new Alert(AlertType.INFORMATION);
    	alert.setTitle("Startup");
    	alert.setHeaderText("The program is loading up.");
    	alert.setContentText("The program has started, please be patient as the program loads the data. It is currently slow, but as long as this message remains, it is still working. This generally takes a few minutes.");
    	alert.show();
    	
    	globStage = stage;
    	String filename = "TB10";
    	
    	String gfaPath = System.getProperty("user.dir") + "/Data/" + filename + "/" + filename + ".gfa";
		String dbPath = System.getProperty("user.dir") + "/db/" + filename;
		
		DatabaseManager dbm = new DatabaseManager(dbPath);
//		GfaParser parser = new GfaParser(dbm);
//		
//		try {
//			parser.parse(gfaPath);
//		} catch (GfaException e) {
//			e.printStackTrace();
//		}
    	

		
		
    	// CREATING THE INITIAL GROUP TO DISPLAY

        PannableCanvas canvas = new PannableCanvas();
        
        canvas.setTranslateX(100);
        canvas.setTranslateY(100);

        NodeGestures nodeGestures = new NodeGestures( canvas);
        RibbonDrawer ribbonDrawer = new RibbonDrawer(dbm);
        
        Group group = ribbonDrawer.draw(canvas, nodeGestures);

        
        Label label1 = new Label("ZOOM LEVEL 1");
        label1.setTranslateX(10);
        label1.setTranslateY(10);

        canvas.getChildren().addAll(label1);

        group.getChildren().add(canvas);
        
        // CREATE THE SECOND SCENE FOR TESTING PURPOSES
        // WONT BE NEEDED ONCE WE HAVE THE ACTUAL SECOND LEVEL
        
        PannableCanvas canvas2 = new PannableCanvas();

        Label label12 = new Label("SCENE 2");
        label12.setTranslateX(10);
        label12.setTranslateY(10);

        Circle circle1 = new Circle(300, 300, 50);
        circle1.setStroke(Color.ORANGE);
        circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));

        canvas2.getChildren().addAll(label12, circle1);
        
        // CREATING THE THIRD CANVAS AGAIN FOR TESTING PURPOSES
        // WILL BE DELETED ONCE ITS NO LONGER NEEDED
        
        PannableCanvas canvas3 = new PannableCanvas();

        Label label3 = new Label("SCENE 3");
        label3.setTranslateX(10);
        label3.setTranslateY(10);

        Circle circle2 = new Circle(300, 300, 50);
        circle2.setStroke(Color.ORANGE);
        circle2.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));

        canvas3.getChildren().addAll(label3, circle2);
        
        // SETTING UP THE INITIAL SCENE

        scene = new Scene(group, 1024, 768);

        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        globStage.setScene(scene);
        globStage.show();

        canvas.addGrid();
        canvas2.addGrid();
        canvas3.addGrid();

        canvases.add(canvas);
        canvases.add(canvas2);
        canvases.add(canvas3);
    }
    
    public class SceneGestures {

        private static final double MAX_SCALE = 10.0d;
        private static final double MIN_SCALE = .1d;

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

                if( !event.isSecondaryButtonDown())
                    return;

                sceneDragContext.mouseAnchorX = event.getSceneX();
                sceneDragContext.mouseAnchorY = event.getSceneY();

                sceneDragContext.translateAnchorX = canvas.getTranslateX();
                sceneDragContext.translateAnchorY = canvas.getTranslateY();

            }

        };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {

                if( !event.isSecondaryButtonDown())
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
                } else {
                     scale *= Math.pow(delta, event.getDeltaY()/20);
                }

                scale = clamp( scale, MIN_SCALE, MAX_SCALE);
                
                double zoom = scale/MAX_SCALE;
                System.out.println("Zoom percentage: " + zoom);
                
                if(zoom == 1.0 && position < canvases.size() - 1) {
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
                
                if(changed == true) {
                	System.out.println("Switching to canvas #" + position);
                	canvas.reset();
                	canvas = canvases.get(position);
                	System.out.println("Switching to scene #" + position);

                	Group groupTemp = new Group();
                	groupTemp.getChildren().add(canvases.get(position));
                	
                    Scene sceneTemp = new Scene(groupTemp, 1024, 768);
                	
                	SceneGestures sceneGestures = new SceneGestures(canvases.get(position));
                    sceneTemp.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
                    sceneTemp.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
                    sceneTemp.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
                    
                	globStage.setScene(sceneTemp);
                }
                
                event.consume();

            }

        };


        public double clamp( double value, double min, double max) {

            if( Double.compare(value, min) < 0)
                return min;

            if( Double.compare(value, max) > 0)
                return max;

            return value;
        }
    }
}


