package gui;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

@SuppressWarnings("restriction")
public class GraphSegment extends Circle {
	
	private int segmentid;
	private int depth;
	private int[] children;
	

	/**
	 * Creates a new GraphSegment with some standard settings.
	 * (subject to change)
	 */
	public GraphSegment(int segmentid, int childcount) {
		this.setLayout();
	    this.depth = 0;
	    this.segmentid = segmentid;
	    this.children = new int[childcount];
	}
	
	public void setLayout() {
	    this.setRadius(25);
	    this.setFill(Color.DODGERBLUE);
	    this.setStroke(Color.BLACK);
	    this.setStrokeType(StrokeType.INSIDE);
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

//	public GraphSegment(double arg0) {
//		super(arg0);
//		// TODO Auto-generated constructor stub
//	}
//
//	public GraphSegment(double arg0, Paint arg1) {
//		super(arg0, arg1);
//		// TODO Auto-generated constructor stub
//	}
//
//	public GraphSegment(double arg0, double arg1, double arg2) {
//		super(arg0, arg1, arg2);
//		// TODO Auto-generated constructor stub
//	}
//
//	public GraphSegment(double arg0, double arg1, double arg2, Paint arg3) {
//		super(arg0, arg1, arg2, arg3);
//		// TODO Auto-generated constructor stub
//	}

}
