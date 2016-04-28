package gui;

import gui.semanticzoom.DragContext;
import gui.semanticzoom.NodeGestures;
import gui.semanticzoom.PannableCanvas;

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
public class Graphdrawer extends Application {

    ArrayList<Scene> scenes = new ArrayList<Scene>();
    Scene scene;
    Stage primarystage;
    int position = 0;
    
    @Override
    public void start(Stage primaryStage) {
    	primarystage = primaryStage;

        // create canvas  999999 -> will be a variable with ~amt of nodes.
        PannableCanvas canvas = new PannableCanvas(5000, 200);
        
        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.setTranslateX(0.5 * canvas.getPrefHeight());
        canvas.setTranslateY(0.5 * canvas.getPrefHeight());
        
        GraphNode root = drawRootGraphNode(canvas);
        
        //CALL FUNCTION TO DRAW NODES RELATIVE TO ROOT NODE
        
        canvas.getChildren().add(root.getAnchorPane());
        
        Group group = new Group();
        group.getChildren().add(canvas);
        
        // create scene which can be dragged and zoomed
        scene = new Scene(group, 800, 2 * canvas.getPrefHeight());

        //scenes.add(scene);
        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        primarystage.setScene(scene);
        primarystage.show();
    }
    
    public GraphNode drawRootGraphNode(Pane canvas) {
    	GraphNode root = new GraphNode();
        root.getAnchorPane().setTranslateY(0.25 * canvas.getPrefHeight());
        Rectangle segment = new Rectangle(50, 50);
        
        root.getAnchorPane().setLeftAnchor(segment, 0.0);
        root.getAnchorPane().setBottomAnchor(segment, 0.25 * root.getAnchorPane().getPrefHeight());
        root.getAnchorPane().getChildren().add(segment);
		return root;
    }
    
    public GraphNode drawChildGraphNode(GraphNode root, GraphNode[] list, Pane canvas) {
    	
    	
		return null;
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

                double delta = 1.2;

                double scale = canvas.getScale(); // currently we only use Y, same value is used for X
                double oldScale = scale;
                
                if (event.getDeltaY() < 0) {
                     scale /= Math.pow(delta, -event.getDeltaY()/20);
                } else {
                     scale *= Math.pow(delta, event.getDeltaY()/20);
                }

                scale = clamp(scale, MIN_SCALE, MAX_SCALE);
                double zoom = scale/MAX_SCALE;
                System.out.println("Zoom percentage: " + zoom);
                
                if(zoom == 1.0 && position < scenes.size() && position >= 0) {
                	position++;
                	primarystage.setScene(scenes.get(position));
                }
                else if(zoom == .01 && position < scenes.size() && position > 0) {
                	position--;
                	primarystage.setScene(scenes.get(position));
                }
                else {
                	System.out.println("Fully zoomed in or zoomed out");
                }
                
                double f = (scale / oldScale)-1;

                double dx = (event.getSceneX() - (canvas.getBoundsInParent().getWidth()/2 + canvas.getBoundsInParent().getMinX()));
                double dy = (event.getSceneY() - (canvas.getBoundsInParent().getHeight()/2 + canvas.getBoundsInParent().getMinY()));

                canvas.setScale( scale);
                canvas.setPivot(f*dx, f*dy);

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


