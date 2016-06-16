package gui.views.ribbon;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import db.DatabaseManager;
import gui.views.phylogeny.NewickColourMatching;
import parsers.XlsxParser;

public class GraphView {
	
	private DatabaseManager dbm;
	
	/**
	 * Location of metadata.xlsx
	 */
	private static String xlsxpath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TB10" + File.separator + "metadata" + ".xlsx";
	
	/**
	 * HashMap containing the lineages of the specimens.
	 */
	private HashMap<String, String> lineages = updateLineages();
	
	/**
	 * Map of all GraphSegments.
	 */
	private HashMap<Integer, GraphSegment> segments;
	
	/**
	 * 5 lists of required data for constructing GraphSegments.
	 */
	private ArrayList<Integer> from;
	private ArrayList<Integer> to;
	private ArrayList<Integer> graphxcoords;
	private ArrayList<Integer> graphycoords;
	private ArrayList<String> segmentdna;
	
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
		
		from = dbm.getDbReader().getAllFromId();
		to = dbm.getDbReader().getAllToId();
		graphxcoords = dbm.getDbReader().getAllXCoord();
		graphycoords = dbm.getDbReader().getAllYCoord();
		segments = new HashMap<Integer, GraphSegment>((int) Math.ceil(to.size() / 0.75));
		segmentdna = new ArrayList<String>();
		
		for (int i = 1; i <= dbm.getDbReader().countSegments(); i++) {
			segmentdna.add(dbm.getDbReader().getContent(i));
		}
	}
	
	/**
	 * Creates a hash map of all segments, by storing their information in GraphSegment
	 * objects.
	 */
	public void constructSegmentMap() {
		int linkpointer = 1;
		
		for (int i = 1; i <= dbm.getDbReader().countSegments(); i++) {
			int childcount = 0;
			
			while (linkpointer <= from.size() && i == from.get(linkpointer - 1)) {
				childcount++;
				linkpointer++;
			}
			linkpointer -= childcount;
			
			GraphSegment segment = new GraphSegment(i, childcount,
					segmentdna.get(i - 1).toCharArray(), graphxcoords.get(i - 1),
					graphycoords.get(i - 1));
			
			int childindex = 0;
			while (linkpointer <= from.size() && i == from.get(linkpointer - 1)) {
				segment.getSegmentChildren()[childindex] = to.get(linkpointer - 1);
				linkpointer++;
				childindex++;
			}
			segments.put(i, segment);
		}
	}
	
	/**
	 * Uses the created hash map of segments to create a drawable Group
	 * containing a visual representation of the segments, such as where they
	 * are located, how they are related and what their DNA strand is.
	 */
	public Group getGraph() {
		System.out.println("Starting graph calculations");
		Group res = new Group();
		System.out.println("Calculating graph edges");
		res.getChildren().add(getGraphEdges());
		System.out.println("Calculating graph segments");
		res.getChildren().add(getGraphSegments());
		System.out.println("Finished calculating graph");
		return res;
	}
	
	/**
	 * Returns a group containing the edges between all the segment coordinates.
	 */
	private Group getGraphEdges() {
		Group res = new Group();
		ArrayList<Integer> counts = dbm.getDbReader().getAllCounts();
		int countIdx = 0;
		for (int i = 0; i < from.size(); i++) {
			int fromId = from.get(i);
			int toId = to.get(i);
			GraphSegment fromsegment = segments.get(fromId);
			GraphSegment tosegment = segments.get(toId);
			double fromX = fromsegment.getLayoutX() + 2 * Math.log(fromsegment.getContentSize()) 
				+ fromsegment.getRadius();
			double toX = tosegment.getLayoutX() + 2 * Math.log(tosegment.getContentSize()) 
				+ tosegment.getRadius();
			
			Line line = new Line(fromX, fromsegment.getLayoutY() + fromsegment.getRadius(),
					toX, tosegment.getLayoutY() + tosegment.getRadius());

			line.setStrokeWidth(1 + counts.get(countIdx++));
			//line.setStroke(getLineColor(fromId, toId));
	        res.getChildren().add(line);
		}
		System.out.println("Finished creating graph edges");
		return res;
	}
	
	/**
	 * Returns a group containing all GraphSegments on the segment coordinates.
	 */
	private Group getGraphSegments() {
		Group res = new Group();
		for (int i = 1; i <= segments.size(); i++) {
			res.getChildren().add(segments.get(i));
		}
		return res;
	}
	
	/**
	 * Scales and re-uses the x-coordinates calculated for the ribbon visualization.
	 * 
	 * @param xcoords
	 * 			x-coordinates of ribbons.
	 * @return xcoords
	 * 			x-coordinates of graph segments.
	 */
	public ArrayList<Integer> scaleRibbonToGraphCoordsX(ArrayList<Integer> xcoords) {
		for (int i = 0; i < xcoords.size(); i++) {
			int newc = xcoords.remove(i) * 100;
			xcoords.add(i, newc);
		}
		return xcoords;
	}
	
	public Paint getLineColor(int fromId, int toId) {
		Paint color = Paint.valueOf("0xff0000ff");
		ArrayList<String> from = dbm.getDbReader().getGenomesThroughSegment(fromId);
		ArrayList<String> to = dbm.getDbReader().getGenomesThroughSegment(toId);
		if (from.size() > to.size()) {
			for (int i = 0; i < to.size(); i++) {
				String genome = to.get(i);
				if (lineages.containsKey(genome) && from.contains(genome) 
						&& !genome.equals("MT_H37RV_BRD_V5.ref")) {
					return NewickColourMatching.getLineageColour(lineages.get(genome));
				}
			}
		} else if (from.size() < to.size()) {
			for (int i = 0; i < from.size(); i++) {
				String genome = from.get(i);
				if (lineages.containsKey(genome) && to.contains(genome) 
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
	
	/**
	 * Scales and re-uses the x-coordinates calculated for the ribbon visualization.
	 * 
	 * @param ycoords
	 * 			y-coordinates of ribbons.
	 * @return ycoords
	 * 			y-coordinates of graph segments.
	 */
	public ArrayList<Integer> scaleRibbonToGraphCoordsY(ArrayList<Integer> ycoords) {
		for (int i = 0; i < ycoords.size(); i++) {
			int newc = ycoords.remove(i) * 50;
			ycoords.add(i, newc);
		}
		return ycoords;
	}
}
