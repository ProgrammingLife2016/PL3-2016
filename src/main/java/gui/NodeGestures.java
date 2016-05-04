package gui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

@SuppressWarnings("restriction")
public class NodeGestures {

	private DragContext nodeDragContext = new DragContext();

	PannableCanvas canvas;

	public NodeGestures(PannableCanvas canvas) {
		this.canvas = canvas;

	}

	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

		public void handle(MouseEvent event) {

			// left mouse button => dragging
			if (!event.isPrimaryButtonDown()) {
				return;
			}

			nodeDragContext.setMouseAnchorX(event.getSceneX());
			nodeDragContext.setMouseAnchorY(event.getSceneY());

			Node node = (Node) event.getSource();

			nodeDragContext.setTranslateAnchorX(node.getTranslateX());
			nodeDragContext.setTranslateAnchorY(node.getTranslateY());

		}

	};

	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {

			// left mouse button => dragging
			if (!event.isPrimaryButtonDown()) {
				return;
			}

			double scale = canvas.getScale();

			Node node = (Node) event.getSource();

			node.setTranslateX(nodeDragContext.getTranslateAnchorX()
					+ ((event.getSceneX() - nodeDragContext.getMouseAnchorX()) / scale));
			node.setTranslateY(nodeDragContext.getTranslateAnchorY()
					+ ((event.getSceneY() - nodeDragContext.getMouseAnchorY()) / scale));

			event.consume();

		}
	};
}