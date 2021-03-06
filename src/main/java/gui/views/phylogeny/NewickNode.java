package gui.views.phylogeny;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.lang.reflect.Field;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import gui.eventhandlers.phylogeny.NewickNodeMouseEventHandler;

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
	
	private ArrayList<String> metainfo;
	
	private boolean isLeaf = false;
	private boolean isSelected = true;
	private static boolean changed = false;
	
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
	public NewickNode(String name, String lineage, ArrayList<String> metainfolist) {
		this();
		this.lineage = lineage;
		this.nodeName = name;
		this.setMetainfo(metainfolist);
		this.setColoured();
		this.addLabel(name);
		this.setToolTips();
	}
	
	/**
	 * Leafnode constructor that adds a label with the given name to a leaf node and colours it
	 * accordingly.
	 * 
	 * @param name
	 *            Text that the Label should display.
	 * @param lineage
	 * 			  Lineage of the specimen.
	 */
	public NewickNode(String name) {
		this();
		this.nodeName = name;
		this.setColoured();
		this.addLabel(name);
	}
	
	public String getName() {
		return this.nodeName;
	}
	
	public boolean getChanged() {
		return changed;
	}
	
	public static void setChanged(boolean change) {
		changed = change;
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
	
//	public void setLeafFunctionality() {
//		node.addEventFilter(MouseEvent.MOUSE_CLICKED, new NewickNodeMouseEventHandler(this));
//		label.addEventFilter(MouseEvent.MOUSE_CLICKED, new NewickNodeMouseEventHandler(this));
//	}
	
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
				if (child instanceof NewickEdge) {
					((NewickEdge)child)
						.setColoured(NewickColour.colourMap.get(this.lineage));
				}
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
				if (child instanceof NewickEdge) {
					((NewickEdge)child).unSetColoured();
				}
				if (child instanceof NewickNode) {
					if (((NewickNode) child).getName() != null) {
						selectedSet.remove(((NewickNode) child));
					}
					((NewickNode) child).unsetColoured();
				}
			}
		}
	}
	
	/**
	 * Stores the lineages for the children for the parent to use.
	 * @return
	 */
	public String setParentLineages() {
		if (!this.isLeaf()) {
			ArrayList<String> lineagelist = new ArrayList<String>();
			for (Object child : this.getChildren()) {
				if (child instanceof NewickNode) {
					if (((NewickNode) child).isLeaf()) {
						lineagelist.add(((NewickNode) child).getLineage());
					} else { //if child not a leaf
						lineagelist.add(((NewickNode) child).setParentLineages()); 
					}
				}
			} //looped through all children, stored their lineages in list
			if (isSameLineage(lineagelist)) {
				this.setLineage(lineagelist.get(0));
			} else {
				this.setLineage("");
			}
		} else { //if rootnode is a leaf
			return this.getLineage();
		}
		this.setColoured();
		return this.getLineage();
	}
	
	/**
	 * Check if lineages in list are the same.
	 * 
	 * @param lineagelist
	 * @return boolean
	 */
	public boolean isSameLineage(ArrayList<String> lineagelist) {
		for (int i = 1; i < lineagelist.size(); i++) {
			if (!lineagelist.get(0).equals(lineagelist.get(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static Set<NewickNode> getSelected() {
		return selectedSet;
	}
	
	public ArrayList<String> getSelectedGenomes() {
		ArrayList<String> names = new ArrayList<String>();
		Iterator<NewickNode> iterator = selectedSet.iterator();
		while (iterator.hasNext()) {
			String name = iterator.next().getLabel().getText();
			names.add(name);
		}
		return names;
	}
	
	private void setToolTips() {
		Tooltip tooltip = new Tooltip();
		String tooltext = metainfo.get(0);
		for (int i = 1; i < metainfo.size(); ++i) {
			tooltext += "\n";
			tooltext += metainfo.get(i);
		}
		
		tooltip.setText(tooltext);
		
		hackTooltipStartTiming(tooltip);
		Tooltip.install(node, tooltip);
		Tooltip.install(label, tooltip);
	}
	
	private void hackTooltipStartTiming(Tooltip tooltip) {
	    try {
	        Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
	        fieldBehavior.setAccessible(true);
	        Object objBehavior = fieldBehavior.get(tooltip);

	        Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
	        fieldTimer.setAccessible(true);
	        Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
	        objTimer.getKeyFrames().clear();
	        
	        Field fieldTimer2 = objBehavior.getClass().getDeclaredField("hideTimer");
	        fieldTimer2.setAccessible(true);
	        Timeline objTimer2 = (Timeline) fieldTimer2.get(objBehavior);
	        objTimer2.getKeyFrames().clear();
	        objTimer2.getKeyFrames().add(new KeyFrame(new Duration(120000)));
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public ArrayList<String> getMetainfo() {
		return metainfo;
	}

	public void setMetainfo(ArrayList<String> metainfo) {
		this.metainfo = metainfo;
	}
	
}