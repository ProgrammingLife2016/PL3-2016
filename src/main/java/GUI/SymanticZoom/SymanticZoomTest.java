package gui.SymanticZoom;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * An application with a zoomable and pannable canvas.
 */
@SuppressWarnings("restriction")
public class SymanticZoomTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    ArrayList<Scene> scenes = new ArrayList<Scene>();
    ArrayList<PannableCanvas> canvases = new ArrayList<PannableCanvas>();
    Scene scene;
    Scene scene2;
    Scene scene3;
    Stage globStage;
    int position = 0;
    
    @Override
    public void start(Stage stage) {

    	globStage = stage;
    	
    	/**
    	 * CREATING FIRST GROUP
    	 */
    	
        Group group = new Group();

        // create canvas
        PannableCanvas canvas = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.setTranslateX(100);
        canvas.setTranslateY(100);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures = new NodeGestures( canvas);

        Label label1 = new Label("SCENE 1");
        label1.setTranslateX(10);
        label1.setTranslateY(10);
        label1.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label1.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Circle circle1 = new Circle( 300, 300, 50);
        circle1.setStroke(Color.ORANGE);
        circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
        circle1.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        circle1.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        canvas.getChildren().addAll(label1, circle1);

        group.getChildren().add(canvas);
        
        /**
         * CREATING SECOND CANVAS
         */

        // create canvas
        PannableCanvas canvas2 = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas2.setTranslateX(100);
        canvas2.setTranslateY(100);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures2 = new NodeGestures( canvas);

        Label label12 = new Label("SCENE 2");
        label12.setTranslateX(10);
        label12.setTranslateY(10);
        label12.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures2.getOnMousePressedEventHandler());
        label12.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures2.getOnMouseDraggedEventHandler());

        Rectangle rect12 = new Rectangle(100,100);
        rect12.setTranslateX(450);
        rect12.setTranslateY(450);
        rect12.setStroke(Color.BLUE);
        rect12.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
        rect12.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures2.getOnMousePressedEventHandler());
        rect12.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures2.getOnMouseDraggedEventHandler());

        canvas2.getChildren().addAll(label12, rect12);
        
        /**
         * CREATING THIRD CANVAS (FOR TESTING FUTURE IMPLEMENTATIONS WITH MORE LEVELS)
         */
        
        // create canvas
        PannableCanvas canvas3 = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas3.setTranslateX(100);
        canvas3.setTranslateY(100);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures3 = new NodeGestures( canvas);

        Label label3 = new Label("SCENE 3");
        label3.setTranslateX(10);
        label3.setTranslateY(10);
        label3.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures3.getOnMousePressedEventHandler());
        label3.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures3.getOnMouseDraggedEventHandler());

        Circle circle3 = new Circle( 300, 300, 50);
        circle3.setStroke(Color.ORANGE);
        circle3.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
        circle3.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures3.getOnMousePressedEventHandler());
        circle3.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures3.getOnMouseDraggedEventHandler());

        canvas3.getChildren().addAll(label3, circle3);
        
        /**
         * SETTING UP SCENE
         */
        
        // create scene which can be dragged and zoomed
        scene = new Scene(group, 1024, 768);
        
        // Adding the sceneGesture listeners to the scenes
        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        // Add the scenes to the global scene array list for use in listeners.
        scenes.add(scene);

        // Use the global instance of the stage so scene can be changed inside the listeners.
        globStage.setScene(scene);
        globStage.show();

        // Add grids to the canvases so that they look nicer.
        canvas.addGrid();
        canvas2.addGrid();
        canvas3.addGrid();
        
        // Add the canvases to the global array list of canvas for use in the listeners.
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

                // right mouse button => panning
                if( !event.isSecondaryButtonDown())
                    return;

                sceneDragContext.setMouseAnchorX(event.getSceneX());
                sceneDragContext.setMouseAnchorY(event.getSceneY());

                sceneDragContext.setTranslateAnchorX(canvas.getTranslateX());
                sceneDragContext.setTranslateAnchorY(canvas.getTranslateY());

            }

        };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {

                // right mouse button => panning
                if( !event.isSecondaryButtonDown())
                    return;

                canvas.setTranslateX(sceneDragContext.getTranslateAnchorX() + event.getSceneX() - sceneDragContext.getMouseAnchorX());
                canvas.setTranslateY(sceneDragContext.getTranslateAnchorY() + event.getSceneY() - sceneDragContext.getMouseAnchorY());

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


