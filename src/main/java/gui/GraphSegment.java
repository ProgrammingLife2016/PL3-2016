package gui;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

@SuppressWarnings("restriction")
public class GraphSegment extends StackPane {
	
	private int segmentid;
	private int depth;
	private int[] children;
	private char[] dnacontent;
	private boolean drawn = false;
	Circle image;
	
	/**
	 * Creates a new GraphSegment with some standard settings.
	 * (subject to change)
	 */
	public GraphSegment(int segmentid, int childcount, char[] dnacontent) {
		this.setLayout();
	    this.depth = 1;
	    this.segmentid = segmentid;
	    this.children = new int[childcount];
	    this.setDnacontent(dnacontent);
	}
	
	public void setLayout() {
	    image = new Circle();
	    image.setRadius(30);
	    image.setFill(Color.DODGERBLUE);
	    image.setStroke(Color.BLACK);
	    image.setStrokeType(StrokeType.INSIDE);
	    this.getChildren().add(image);
	}
	
	public int getDepth() {
		return this.depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
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

	public boolean isDrawn() {
		return drawn;
	}

	public void setDrawn(boolean drawn) {
		this.drawn = drawn;
	}

	public char[] getDnacontent() {
		return dnacontent;
	}

	public void setDnacontent(char[] dnacontent) {
		this.dnacontent = dnacontent;
	}
	
	public double getRadius() {
		return this.image.getRadius();
	}
	
//	
//	public GraphSeg() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public GraphSeg(Node... arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}

}
