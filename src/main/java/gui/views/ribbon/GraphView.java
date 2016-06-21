package gui.views.ribbon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import db.DatabaseManager;
import gui.views.phylogeny.NewickColourMatching;
import parsers.XlsxParser;

public class GraphView {
	
	private DatabaseManager dbm;
	private StringProperty selectedContent = new SimpleStringProperty("");
	
	/**
	 * Location of metadata.xlsx
	 */
	private static String xlsxpath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TB10" + File.separator + "metadata" + ".xlsx";
	
	/**
	 * HashMap containing the lineages of the specimens.
	 */
	private HashMap<String, String> lineages = updateLineages();
	

	private Set<Integer> segmentIds = new HashSet<Integer>();
	private ArrayList<Integer> graphxcoords;
	private ArrayList<Integer> graphycoords;
	private ArrayList<String> segmentdna;
	private ArrayList<Integer> genomeIds = createList();
	
	public GraphView(DatabaseManager dbManager) {
		this.dbm = dbManager;
	}
	
	/**
	 * Parse lineages of the specimens.
	 */
	public HashMap<String, String> updateLineages() {
		XlsxParser xlsxparser = new XlsxParser();
		xlsxparser.parse(xlsxpath);
		return xlsxparser.getLineages();
	}
	
	/**
	 * Load in all necessary information from the database.
	 */
	public void loadSegmentData() {
		graphxcoords = dbm.getDbReader().getAllXCoord();
		graphycoords = dbm.getDbReader().getAllYCoord();
		segmentdna = new ArrayList<String>();
		dbm.getDbReader()
			.getAllContent()
			.stream()
			.forEach(str -> segmentdna.add(shortenString(str,5)));
	}
	
	private String shortenString(String in, int maxLength) {
		if (in.length() > maxLength) {
			return in.substring(0, maxLength) + "...";
		}
		return in;
	}
	
	/**
	 * Uses the created hash map of segments to create a drawable Group
	 * containing a visual representation of the segments, such as where they
	 * are located, how they are related and what their DNA strand is.
	 */
	public Group getGraph() {
		System.out.println("Starting graph calculations");
		segmentIds.clear();
		Group res = new Group();
		System.out.println("Calculating graph edges");
		res.getChildren().add(getGraphEdges());
		System.out.println("Calculating graph segments");
		res.getChildren().add(getGraphSegments());
		System.out.println("Finished calculating graph");
		return res;
	}
	
	public void setGenomeIds(ArrayList<Integer> ids) {
		genomeIds = ids;
	}
	
	/**
	 * Returns a group containing the edges between all the segment coordinates.
	 */
	private Group getGraphEdges() {
		Group res = new Group();
		ArrayList<ArrayList<Integer>> links = dbm.getDbReader().getLinks(genomeIds);
		ArrayList<ArrayList<Integer>> counts = dbm.getDbReader().getLinkWeights(genomeIds);
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> ycoords = dbm.getDbReader().getAllYCoord();
		
		for (int fromId = 1; fromId <= links.size(); fromId++) {
			for (int j = 0; j < links.get(fromId - 1).size(); j++) {
				int toId = links.get(fromId - 1).get(j);
				segmentIds.add(fromId);
				segmentIds.add(toId);
				Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
						xcoords.get(toId - 1), ycoords.get(toId - 1));
				line.setStrokeWidth(counts.get(fromId - 1).get(j));
		        res.getChildren().add(line);
			}
		}
		System.out.println("Finished creating graph edges");
		return res;
	}
	
	/**
	 * Returns a group containing all GraphSegments on the segment coordinates.
	 */
	private Group getGraphSegments() {
		double maxY = dbm.getDbReader().getMaxYCoord();
		double ellipseHeigth = 30*maxY/1050;
		System.out.println("MAX Y: " + maxY);
		
		Group res = new Group();
		Iterator<Integer> iterator = segmentIds.iterator();
		while (iterator.hasNext()) {
			int segmentId = iterator.next();
			Group graphSegment = new Group();
			Ellipse ellipse = createEllipse(segmentId,ellipseHeigth);
			graphSegment.getChildren().add(ellipse);
			graphSegment.getChildren().add(visualizeDnaContent(segmentId));
			graphSegment.addEventFilter(MouseEvent.MOUSE_CLICKED, 
					new NodeSelectHandler(segmentId, selectedContent, dbm));
			res.getChildren().add(graphSegment);
		}
		return res;
	}
	
	public Paint getLineColor(int fromId, int toId) {
		Paint color = Paint.valueOf("0xff0000ff");
		ArrayList<String> from = dbm.getDbReader().getGenomesThroughSegment(fromId, genomeIds);
		ArrayList<String> to = dbm.getDbReader().getGenomesThroughSegment(toId, genomeIds);
		if (from.size() > to.size()) {
			for (int i = 0; i < to.size(); i++) {
				String genome = to.get(i);
				if (lineages.containsKey(genome) && from.contains(genome) 
						&& !genome.equals("MT_H37RV_BRD_V5.ref")) {
					return NewickColourMatching.getLineageColour(lineages.get(genome));
				}
			}
		} else {
			for (int i = 0; i < from.size(); i++) {
				String genome = from.get(i);
				if (lineages.containsKey(genome) && to.contains(genome) 
						&& !genome.equals("MT_H37RV_BRD_V5.ref")) {
					return NewickColourMatching.getLineageColour(lineages.get(genome));
				}
			}
		}
		return color;
	}
	
	public ArrayList<Integer> createList() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i <= 330; i++) {
			list.add(i);
		}
		return list;
	}
	
	/**
	 * Returns a visualization of a graph segment
	 */
	public Ellipse createEllipse(int segmentId, double ellipseHeigth) {
		int contentLength = segmentdna.get(segmentId - 1).length();
		double xcoord = graphxcoords.get(segmentId - 1);
		double ycoord = graphycoords.get(segmentId - 1);
		double xradius = 30 + 2 * Math.log(contentLength);
		Ellipse node = new Ellipse(xcoord, ycoord, xradius, ellipseHeigth);
		node.setFill(Color.DODGERBLUE);
		node.setStroke(Color.BLACK);
		node.setStrokeType(StrokeType.INSIDE);
		return node;
	}
	
	/**
	 * Returns a Text object displaying the DNA strand.
	 * DNA strands with more than 5 nucleotides only have the first 5 nucleotides displayed.
	 */
	private Text visualizeDnaContent(int segmentId) {
		String content = segmentdna.get(segmentId - 1);
		Text dnatext = new Text();
		dnatext.setTextAlignment(TextAlignment.CENTER);
		dnatext.setText(content);

		double xcoord = graphxcoords.get(segmentId - 1);
		double ycoord = graphycoords.get(segmentId - 1);
		double width = dnatext.getLayoutBounds().getWidth();
		dnatext.setLayoutX(xcoord - 0.5 * width);
		dnatext.setLayoutY(ycoord + 5);
		return dnatext;
	}
	
	public StringProperty getSelectedContentProperty() {
		return selectedContent;
	}
	

}
