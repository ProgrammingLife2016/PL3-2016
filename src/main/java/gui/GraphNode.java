package gui;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.List;
import java.util.jar.JarFile;
import java.lang.instrument.*;

@SuppressWarnings("restriction")
public class GraphNode extends Pane {
	
	private GraphNode[] children;
	private AnchorPane anchorpane;
	
	/**
	 * GraphNode constructor. Each node in the graph will be represented by this object.
	 * @param x
	 * @param y
	 */
	public GraphNode() {
		this.anchorpane = createAnchorPane();
		this.children = new GraphNode[4];
	}
	
	/**
	 * Create new AnchorPane.
	 * 
	 * @param x
	 * @param y
	 * @return AnchorPane
	 */
	public AnchorPane createAnchorPane() {
		AnchorPane ap = new AnchorPane();
		ap.setPrefHeight(100);
		ap.setPrefWidth(100);
		ap.setStyle("-fx-background-color: yellow;");
		return ap;
	}
	
	public AnchorPane getAnchorPane() {
		return this.anchorpane;
	}
	
	public GraphNode[] getNodeChildren() {
		return this.children;
	}
}