package gui.phylogeny;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

/**
 * Class that is used to visualize a single node of a {@link NewickTree}
 */
public class NewickNode extends Group {
	
	private static final int SIZE = 10;
	private Rectangle node = new Rectangle(0 - SIZE / 2, 0 - SIZE / 2, SIZE, SIZE);
	
	public NewickNode() {
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
	
	public void hideRectangle() {
		node.setVisible(false);
	}
	
	private void addLabel(String text) {
		Label label = new Label(text);
		label.setTranslateX(10);
		label.setTranslateY(-8);
		this.getChildren().add(label);
	}
	
}
