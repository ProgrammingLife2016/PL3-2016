package gui;

import java.util.ArrayList;
import javafx.scene.shape.Line;
import javafx.scene.paint.Paint;

public class Segment extends Line {
	
	private Paint color;
	private Line line;
	private ArrayList<String> genomes;
	
	public Segment(Line line) {
		this.line = line;
	}
	
	public Segment(Line line, Paint color) {
		this.line = line;
		this.color = color;
		line.setStroke(color);
	}
	
	public Segment(double x1, double y1, double x2, double y2, Paint color) {
		line = new Line(x1,y1,x2,y2);
		this.color = color;
		line.setStroke(color);
	}
	
	public Paint getColor() {
		return color;
	}
	
	public void setColor(Paint color) {
		line.setStroke(color);
	}
	
	public ArrayList<String> getGenomes() {
		return genomes;
	}
	
	public void setGenomes(ArrayList<String> genomes) {
		this.genomes = genomes;
	}
}
