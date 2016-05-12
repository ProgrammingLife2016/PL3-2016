package gui;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

@SuppressWarnings("restriction")
public class GraphSegment extends StackPane {
	
	/**
	 * ID of segment.
	 */
	private int segmentid;
	
	/**
	 * All outgoing segments from this segment.
	 */
	private int[] children;
	
	/**
	 * DNA strand of segment.
	 */
	private char[] dnacontent;
	
	/**
	 * Check if current segment had already been drawn.
	 */
	private boolean drawn = false;
	
	/**
	 * Layout object of segment.
	 */
	private Circle image;
	
	/**
	 * Creates a new GraphSegment with some standard settings for layout.
	 */
	public GraphSegment(int segmentid, int childcount, char[] dnacontent) {
	    this.segmentid = segmentid;
	    this.children = new int[childcount];
	    this.dnacontent = dnacontent;
		this.setLayout();
		this.visualizeDnaContent();
	}
	
	/**
	 * Creates a new GraphSegment with some standard settings for layout and specific
	 * layout coordinates.
	 */
	public GraphSegment(int segmentid, int childcount, char[] dnacontent, int xcoord, int ycoord) {
	    this.segmentid = segmentid;
	    this.children = new int[childcount];
	    this.dnacontent = dnacontent;
		this.setLayout();
		this.setLayoutCoords(xcoord, ycoord);
		this.visualizeDnaContent();
	}
	
	/**
	 * Sets some basic options for appearance of a segment.
	 */
	public void setLayout() {
	    image = new Circle();
	    image.setRadius(30);
	    image.setFill(Color.DODGERBLUE);
	    image.setStroke(Color.BLACK);
	    image.setStrokeType(StrokeType.INSIDE);
	    this.getChildren().add(image);
	}
	
	/**
	 * Set specific layout coordinates.
	 * 
	 * @param xcoord
	 * 			x-coordinate.
	 * @param ycoord
	 * 			y-coordinate.
	 */
	public void setLayoutCoords(int xcoord, int ycoord) {
	    this.setLayoutX(xcoord);
	    this.setLayoutY(ycoord);
	}
	
	/**
	 * Adds a Text object to the GraphSegment displaying its DNA strand.
	 * DNA strands with more than 5 nucleotides only have the first 5 nucleotides displayed.
	 */
	private void visualizeDnaContent() {
		String dna = "";
		for (int j = 0; j < this.dnacontent.length && j <= 4; j++) {
			dna += "" +  this.dnacontent[j];
		}
		if ( this.dnacontent.length > 5) {
			dna += "...";
		}
		Text dnatext = new Text(dna);
		this.getChildren().add(dnatext);
	}
	
	public int getSegmentId() {
		return this.segmentid;
	}
	
	public void setSegmentId(int id) {
		this.segmentid = id;
	}
	
	public int[] getSegmentChildren() {
		return this.children;
	}
	
	public double getRadius() {
		return this.image.getRadius();
	}
}