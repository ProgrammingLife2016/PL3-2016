package gui;

import javafx.scene.shape.Line;

import java.util.ArrayList;

import javafx.scene.paint.Paint;

public class Segment extends Line {
	
	private Paint color;
	private Line line;
	private ArrayList<String> genomes;
	
	public Segment(Line l) {
		line = l;
	}
	
	public Segment(Line l, Paint c) {
		line = l;
		color = c;
		line.setStroke(color);
	}
	
	public Segment(double x1, double y1, double x2, double y2, Paint c) {
		line = new Line(x1,y1,x2,y2);
		color = c;
		line.setStroke(color);
	}
	
	public Paint getColor() {
		return color;
	}
	
	public void setColor(Paint c) {
		line.setStroke(c);
	}
	
	public ArrayList<String> getGenomes() {
		return genomes;
	}
	
	public void setGenomes(ArrayList<String> g) {
		genomes = g;
	}
}
