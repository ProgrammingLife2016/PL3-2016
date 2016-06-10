package gui.phylogeny.model;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

import gui.phylogeny.EventHandlers.NewickNodeMouseEventHandler;


/**
 * Class that is used to visualize a single node of a {@link NewickTree}
 */
public class NewickNode extends Group {
	
	/**
	 * The rectangle shape that represents a NewickNode in the phylogenetic tree.
	 */
	private Rectangle node;
	
	private static final int RECTANGLE_SIZE = 10;
	
	/**
	 * Text label displaying the genome ID of leaf nodes.
	 */
	private Label label = null;
	
	/**
	 * Lineage of the specimen.
	 */
	private String lineage = "";
	
	private boolean isLeaf = false;
	private boolean isSelected = true;
	public static boolean changed = false;
	
	/**
	 * Set of currently selected NewickNodes.
	 */
	private static Set<NewickNode> selectedSet;
	
	/**
	 * static initializer
	 */
	static {
		selectedSet = new HashSet<NewickNode>();
	}
	 
	/**
	 * Main constructor of NewickNode.
	 */
	public NewickNode() {
		this.setupNodeLayout();
		this.getChildren().add(node);
	}
	
	private String nodeName;
	
	/**
	 * Leafnode constructor that adds a label with the given name to a leaf node and colours it
	 * accordingly.
	 * 
	 * @param name
	 *            Text that the Label should display.
	 * @param lineage
	 * 			  Lineage of the specimen.
	 */
	public NewickNode(String name, String lineage) {
		this();
		this.lineage = lineage;
		this.nodeName = name;
		this.setColoured();
		this.addLabel(name);
	}
	
	public String getName() {
		return this.nodeName;
	}
	
	/**
	 * Handles node layout aspects.
	 */
	public void setupNodeLayout() {
		node = new Rectangle(0 - RECTANGLE_SIZE / 2,
				0 - RECTANGLE_SIZE / 2, RECTANGLE_SIZE, RECTANGLE_SIZE);
		node.addEventFilter(MouseEvent.MOUSE_CLICKED, new NewickNodeMouseEventHandler(this));
	}
	
	public Rectangle getRectangle() {
		return node;
	}
	
	public void hideRectangle() {
		node.setVisible(false);		
	}
	
	
	/**
	 * Adds a label with the given text to the Group of the node.
	 * 
	 * @param text
	 * 			Text the label will display
	 */
	private void addLabel(String text) {
		label = new Label(text);
		setupLabelLayout();
		this.getChildren().add(label);
	}
	
	/**
	 * Handles label layout aspects.
	 */
	private void setupLabelLayout() {
		label.setTranslateX(10);
		label.setTranslateY(-8);
		label.addEventFilter(MouseEvent.MOUSE_CLICKED, new NewickNodeMouseEventHandler(this));
		label.setTextFill(NewickColour.colourMap.get(this.lineage));
	}
	
	public Label getLabel() {
		return label;
	}
	
	public String getLineage() {
		return this.lineage;
	}
	
	public void setLineage(String lineage) {
		this.lineage = lineage;
	}

	/**
	 * Toggles "selected" state of a node.
	 */
	public void toggleSelected() {
		if (isSelected) {
			isSelected = false;
		} else {
			isSelected = true;
		}
	}

	public boolean isSelected() {
		return isSelected;
	}
	
	public void setIsLeaf(boolean value) {
		isLeaf = value;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}
	
	
	/**
	 * Set colour corresponding to node's lineage.
	 */
	public void setColoured() {
		node.setFill(NewickColour.colourMap.get(this.lineage));
		if (this.isLeaf()) {
			label.setTextFill(NewickColour.colourMap.get(this.lineage));
			selectedSet.add((NewickNode) this);
		} else {
			for (Object child : this.getChildren()) {
				if (child instanceof NewickNode) {
					if (((NewickNode) child).getName() != null) {
						selectedSet.add(((NewickNode) child));
					}
					((NewickNode) child).setColoured();
				}
			}
		}
	}
	
	/**
	 * Unset colour of this node (makes node appear gray). Also unsets
	 * colour of all of its children.
	 */
	public void unsetColoured() {
		node.setFill(NewickColour.selected());
		if (this.isLeaf()) {
			label.setTextFill(NewickColour.selected());
			selectedSet.remove(((NewickNode) this));
		} else {
			for (Object child : this.getChildren()) {
				if (child instanceof NewickNode) {
					if (((NewickNode) child).getName() != null) {
						selectedSet.remove(((NewickNode) child));
					}
					((NewickNode) child).unsetColoured();
				}
			}
		}
	}
	
	public static Set<NewickNode> getSelected() {
		return selectedSet;
	}
	
	public static ArrayList<String> getSelectedGenomes() {
		ArrayList<String> names = new ArrayList<String>();
		changed = true;
		Iterator<NewickNode> iterator = selectedSet.iterator();
		while (iterator.hasNext()) {
			String name = iterator.next().getLabel().getText();
			names.add(name);
		}
		return names;
	}
}