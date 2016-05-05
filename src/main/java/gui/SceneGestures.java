package gui;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

@SuppressWarnings("restriction")
public class SceneGestures {
	 int position = 0;
	 
    private final double MAX_SCALE = 100.0d;
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
            if (!event.isSecondaryButtonDown()) {
                return;
            }
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
            
            if(zoom == 1.0 && position < ScreenManager.canvasList.size() - 1) {
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
            	canvas = ScreenManager.canvasList.get(position);
            	System.out.println("Switching to scene #" + position);

            	Group groupTemp = new Group();
            	groupTemp.getChildren().add(ScreenManager.canvasList.get(position));
                Scene sceneTemp = new Scene(groupTemp, 1024, 768);
            	SceneGestures sceneGestures = new SceneGestures(ScreenManager.canvasList.get(position));
                sceneTemp.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
                sceneTemp.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
                sceneTemp.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
            	ScreenManager.currentStage.setScene(sceneTemp);
            }
            event.consume();
        }
    };
    
    public double clamp(double value, double min, double max) {
        if(Double.compare(value, min) < 0)
            return min;

        else if( Double.compare(value, max) > 0)
            return max;
        
        else
        return value;
    }
}
