package gui.phylogeny;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Class that is used to visualize a single node of a {@link NewickTree}
 */
public class NewickNode extends Group {
	
	private static final int SIZE = 10;
	private Rectangle node = new Rectangle(0 - SIZE / 2, 0 - SIZE / 2, SIZE, SIZE);
	private Label label = null;
	public boolean isLeaf = false;
	
	/**
	 * Event handler for mouse click event with the phylogenetic view.
	 */
	private final EventHandler<MouseEvent> mouseEventHandler = new EventHandler<MouseEvent>() {
		
		@Override
		public void handle(MouseEvent event) {
			Paint currentColor = node.getFill();
			if (currentColor.toString().equals("0x000000ff")) {
				node.setFill(Paint.valueOf("#778899"));
				if (label != null) {
					label.setTextFill(Paint.valueOf("#778899"));
				}
				turnChildrenGrey();
				
			} else {
				node.setFill(Paint.valueOf("0x000000ff"));
				if (label != null) {
					label.setTextFill(Paint.valueOf("0x000000ff"));
				}
				turnChildrenBlack();
			}
		}
	};
	
	/**
	 * Colors all relevant children in the Group black.
	 */
	private void turnChildrenBlack() {
		for (Object child : this.getChildren()) {
			if (child instanceof NewickNode) {
				((NewickNode) child).getRectangle().setFill(Paint.valueOf("0x000000ff"));
				if (label != null) {
					((NewickNode) child).getLabel().setTextFill(Paint.valueOf("0x000000ff"));
				}
				if (!child.)
			} else if (child instanceof NewickEdge) {
				((NewickEdge) child).setFill(Paint.valueOf("0x000000ff"));
			}
		}
	}
	
	/**
	 * Colors all relevant children in the Group light grey.
	 */
	private void turnChildrenGrey() {
		for (Object child : this.getChildren()) {
			if (child instanceof NewickNode) {
				((NewickNode) child).getRectangle().setFill(Paint.valueOf("#778899"));
				if (label != null) {
					((NewickNode) child).getLabel().setTextFill(Paint.valueOf("#778899"));
				}
			} else if (child instanceof NewickEdge) {
				((NewickEdge) child).setFill(Paint.valueOf("#778899"));
			}
		}
	}
	
	public NewickNode() {
		node.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
		this.getChildren().add(node);
	}
	
	/**
	 * Add a label with the given name to the right of the Rectangle. This
	 * constructor should only be used for leaf nodes.
	 * 
	 * @param name
	 *            Text that the Label should display.
	 */
	public NewickNode(String name) {
		this();
		this.addLabel(name);
	}
	
	public Rectangle getRectangle() {
		return node;
	}
	
	public Label getLabel() {
		return label;
	}
	
	public void hideRectangle() {
		node.setVisible(false);
	}
	
	private void addLabel(String text) {
		label = new Label(text);
		label.setTranslateX(10);
		label.setTranslateY(-8);
		label.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
		this.getChildren().add(label);
	}
	
}
