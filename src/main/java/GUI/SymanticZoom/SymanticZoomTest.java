package GUI.SymanticZoom;
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
    Scene scene;
    Scene scene2;
    Scene scene3;
    Stage globStage;
    int position = 0;
    
    @Override
    public void start(Stage stage) {

    	globStage = stage;
    	
        Group group = new Group();

        // create canvas
        PannableCanvas canvas = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.setTranslateX(100);
        canvas.setTranslateY(100);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures = new NodeGestures( canvas);

        Label label1 = new Label("Draggable node 1");
        label1.setTranslateX(10);
        label1.setTranslateY(10);
        label1.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label1.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Label label2 = new Label("Draggable node 2");
        label2.setTranslateX(100);
        label2.setTranslateY(100);
        label2.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label2.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Label label3 = new Label("Draggable node 3");
        label3.setTranslateX(200);
        label3.setTranslateY(200);
        label3.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label3.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Circle circle1 = new Circle( 300, 300, 50);
        circle1.setStroke(Color.ORANGE);
        circle1.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
        circle1.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        circle1.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Rectangle rect1 = new Rectangle(100,100);
        rect1.setTranslateX(450);
        rect1.setTranslateY(450);
        rect1.setStroke(Color.BLUE);
        rect1.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
        rect1.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        rect1.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        canvas.getChildren().addAll(label1, label2, label3, circle1, rect1);

        group.getChildren().add(canvas);

        Group group2 = new Group();

        // create canvas
        PannableCanvas canvas2 = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.setTranslateX(100);
        canvas.setTranslateY(100);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures2 = new NodeGestures( canvas);

        Label label12 = new Label("Draggable node 1");
        label12.setTranslateX(10);
        label12.setTranslateY(10);
        label12.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label12.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Label label22 = new Label("Draggable node 2");
        label22.setTranslateX(100);
        label22.setTranslateY(100);
        label22.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label22.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Label label32 = new Label("Draggable node 3");
        label32.setTranslateX(200);
        label32.setTranslateY(200);
        label32.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label32.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Circle circle12 = new Circle( 300, 300, 50);
        circle12.setStroke(Color.ORANGE);
        circle12.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
        circle12.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        circle12.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Rectangle rect12 = new Rectangle(100,100);
        rect12.setTranslateX(450);
        rect12.setTranslateY(450);
        rect12.setStroke(Color.BLUE);
        rect12.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
        rect12.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        rect12.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        canvas2.getChildren().addAll(label12, label22, label32, circle12, rect12);

        group2.getChildren().add(canvas2);
        
        Group group22 = new Group();

        // create canvas
        PannableCanvas canvas22 = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.setTranslateX(100);
        canvas.setTranslateY(100);

        // create sample nodes which can be dragged
        NodeGestures nodeGestures22 = new NodeGestures( canvas);

        Label label122 = new Label("Draggable node 1");
        label122.setTranslateX(10);
        label122.setTranslateY(10);
        label122.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label122.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Label label222 = new Label("Draggable node 2");
        label222.setTranslateX(100);
        label222.setTranslateY(100);
        label222.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label222.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Label label322 = new Label("Draggable node 3");
        label322.setTranslateX(200);
        label322.setTranslateY(200);
        label322.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        label322.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Circle circle122 = new Circle( 300, 300, 50);
        circle122.setStroke(Color.ORANGE);
        circle122.setFill(Color.ORANGE.deriveColor(1, 1, 1, 0.5));
        circle122.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        circle122.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        Rectangle rect122 = new Rectangle(100,100);
        rect122.setTranslateX(450);
        rect122.setTranslateY(450);
        rect122.setStroke(Color.BLUE);
        rect122.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.5));
        rect122.addEventFilter( MouseEvent.MOUSE_PRESSED, nodeGestures.getOnMousePressedEventHandler());
        rect122.addEventFilter( MouseEvent.MOUSE_DRAGGED, nodeGestures.getOnMouseDraggedEventHandler());

        canvas22.getChildren().addAll(label122, label222, label322, circle122, rect122);

        group22.getChildren().add(canvas22);
        
        // create scene which can be dragged and zoomed
        scene = new Scene(group, 1024, 768);
        scene2 = new Scene(group2, 1024, 768);
        scene3 = new Scene(group22, 1024, 768);

        scenes.add(scene);
        scenes.add(scene2);
        scenes.add(scene3);
        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

        globStage.setScene(scene);
        globStage.show();

        canvas.addGrid();

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

                sceneDragContext.mouseAnchorX = event.getSceneX();
                sceneDragContext.mouseAnchorY = event.getSceneY();

                sceneDragContext.translateAnchorX = canvas.getTranslateX();
                sceneDragContext.translateAnchorY = canvas.getTranslateY();

            }

        };

        private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {

                // right mouse button => panning
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
                
                if(zoom == 1.0 && position < scenes.size() && position >= 0) {
                	position++;
                	globStage.setScene(scenes.get(position));
                }
                else if(zoom == .01 && position < scenes.size() && position > 0) {
                	position--;
                	globStage.setScene(scenes.get(position));
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


