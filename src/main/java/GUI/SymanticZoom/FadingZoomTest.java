package gui.SymanticZoom;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

/**
 * An application with a zoomable and pannable canvas.
 */
@SuppressWarnings("restriction")
public class FadingZoomTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public StackPane sp = new StackPane();
    public ArrayList<Group> zoomLevels = new ArrayList<Group>();
    public int currentZoom = 0;
    
    @Override
    public void start(Stage stage) {

        Group group = new Group();
        Group group2 = new Group();
        Group group3 = new Group();

        // create canvas
        PannableCanvas canvas = new PannableCanvas();
        PannableCanvas canvas2 = new PannableCanvas();
        PannableCanvas canvas3 = new PannableCanvas();

        // we don't want the canvas on the top/left in this example => just
        // translate it a bit
        canvas.setTranslateX(100);
        canvas.setTranslateY(100);
        canvas2.setTranslateX(100);
        canvas2.setTranslateY(100);
        canvas3.setTranslateX(100);
        canvas3.setTranslateY(100);

        group.getChildren().add(canvas);
        group2.getChildren().add(canvas2);
        group3.getChildren().add(canvas3);
        
        // create scene which can be dragged and zoomed
        Scene scene = new Scene(group, 1024, 768);

        SceneGestures sceneGestures = new SceneGestures(canvas);
        scene.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scene.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scene.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
        
        sp.getChildren().addAll(group);
        //sp.getChildren().addAll(group2);
        //sp.getChildren().addAll(group3);
        zoomLevels.add(group);
        zoomLevels.add(group2);
        //zoomLevels.add(group3);
        
        for(int i = 1; i < sp.getChildren().size(); i++) {
        	sp.getChildren().get(i).setOpacity(0.0);
        }
        
        stage.setScene(scene);
        stage.show();

        canvas.addGrid();

    }
    @SuppressWarnings("restriction")
    class SceneGestures {

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
                
                System.out.println("Zoom percentage: " + scale/MAX_SCALE);
                
                double opacity = scale/MAX_SCALE * 10;
                if(opacity > 1.0) opacity = 1.0;
                
                sp.getChildren().get(0).setOpacity(opacity);
                //sp.getChildren().get(1).setOpacity(1.0 - opacity);
                if(opacity == 0.1) {
                	//canvas = (PannableCanvas) sp.getChildren().get(1);
                }
                
                System.out.println("Opacity: " + opacity);
                
                double f = (scale / oldScale)-1;
                
                if(oldScale > scale) System.out.println("going out");
                if(oldScale < scale) System.out.println("going in");
                
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


