package gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import db.DatabaseManager;
import gui.phylogeny.NewickColourMatching;
import parsers.XlsxParser;

/**
 * @author hugokooijman
 *
 * Class containing all the logic involving the graph view. It reads out database tables,
 * stores required information in memory and adds the required to the Group "graphpane",
 * which is defined in the GraphLevel.fxml file. This will result in a visual representation
 * of all segments that are loaded into it.
 */
public class GraphController implements Initializable {
	
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	private ScrollPane otherPane;

	@FXML private CheckBox checkboxSnp;
	@FXML private CheckBox checkboxInsert;
	
	private Group innerGroup;
	private Group outerGroup;
	private Group otherGroup;
	
	private static final double MAX_SCALE = 1.0d;
    private static final double MIN_SCALE = .003d;
    
	private DatabaseManager dbm;
	
	/**
	 * Map of all GraphSegments.
	 */
	private HashMap<Integer, GraphSegment> segments;
	
	/**
	 * 6 lists of required data for constructing GraphSegments.
	 */
	private ArrayList<Integer> from;
	private ArrayList<Integer> to;
	private ArrayList<Integer> graphxcoords;
	private ArrayList<Integer> graphycoords;
	private ArrayList<String> segmentdna;
	private ArrayList<Integer> genomeIds = createList();
	
	Set<Integer> segmentIds = new HashSet<Integer>();
    
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
	 * Handles the scroll wheel event handler for zooming in and zooming out.
	 */
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			event.consume();
			if (event.isControlDown()) {
				double deltaY = event.getDeltaY();
				double delta = 1.2;
				double scale = innerGroup.getScaleX();

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}
				
				double barValue = scrollPane.getHvalue();
				innerGroup.setScaleX(scale);
				otherGroup.setScaleX(scale);
				scrollPane.setHvalue(barValue);
				otherPane.setHvalue(barValue);
				return;
			}

			double deltaY = event.getDeltaY();
			double deltaX = event.getDeltaX();

			if (deltaY < 0) {
				scrollPane.setHvalue(Math.min(1, scrollPane.getHvalue() + 0.0007));
			} else if (deltaY > 0) {
				scrollPane.setHvalue(Math.max(0, scrollPane.getHvalue() - 0.0007));
			}
			if (deltaX < 0) {
				scrollPane.setVvalue(Math.min(1, scrollPane.getVvalue() + 0.05));
			} else if (deltaX > 0) {
				scrollPane.setVvalue(Math.max(0, scrollPane.getVvalue() - 0.05));
			}
			
		}
	};
	
	/**
	 * Event handler for zooming in and out using the keyboard instead of the scroll wheel.
	 */
	private final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
		
		@Override
		public void handle(KeyEvent event) {
			String character = event.getCharacter();
			if (!event.isControlDown()) {
				return;
			}
			
			double delta = 1.2;
			double scale = innerGroup.getScaleY();

			// Zoom in when ctrl and the "+" or "+/=" key is pressed.
			if (character.equals("+") || character.equals("=")) {
				scale *= delta;

				// Cut off the scale if it is bigger than the maximum
				// allowed scale
				scale = scale > MAX_SCALE ? MAX_SCALE : scale;
			} else if (character.equals("-")) {
				scale /= delta;
				// Cut off the scale if it is bigger than the minimum
				// allowed scale
				scale = scale < MIN_SCALE ? MIN_SCALE : scale;
			} else {
				return;
			}
			innerGroup.setScaleY(scale);
			innerGroup.setScaleX(MIN_SCALE);
		}
	};


	
	/**
	 * Parse lineages of the specimens.
	 */
	public HashMap<String, String> updateLineages() {
		XlsxParser xlsxparser = new XlsxParser();
		xlsxparser.parse(xlsxpath);
		return xlsxparser.getLineages();
	}
	
	/**
	 * Initialize fxml file.
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.dbm = Launcher.dbm;
		loadSegmentData();
		constructSegmentMap();
		updateView();	
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		scrollPane.addEventFilter(KeyEvent.KEY_TYPED, keyEventHandler);
		pane.boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
		    scrollPane.setPrefWidth(newValue.getWidth());
		    scrollPane.setPrefHeight(newValue.getHeight());
		});
		scrollPane.setHvalue(0);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, 
					Number oldValue, Number newValue) {
				otherPane.setHvalue(newValue.doubleValue());
			}
		});
		
		checkboxInsert.selectedProperty().addListener(
			(ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
			//For now, we just print a line. Should be toggling the insertions
			System.out.println("You pressed the insert checkbox")
		);
		
		checkboxSnp.selectedProperty().addListener(
			(ChangeListener<Boolean>) (observable, oldValue, newValue) -> 
			//For now, we just print a line. Should be toggling the SNPs
			System.out.println("You pressed the SNP checkbox")
		);
		
		double maxY = dbm.getDbReader().getMaxYCoord();
		System.out.println("MaxY in the graph controller = " + maxY);
		innerGroup.setScaleY(1020.0 / maxY);
		innerGroup.setScaleX(0.4);
	}
	
	/**
	 * Updates the view. Used when changing database files so the graph
	 * will have to adjust to the new file.
	 */
	public void updateView() {
		innerGroup = getGraph();
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
		System.out.println("Number of genomes: " + genomeIds.size());
	}
	
	/**
	 * Load in all necessary information from the database.
	 */
	private void loadSegmentData() {
		
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
		segmentIds.clear();
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
		Group res = new Group();
		Iterator<Integer> iterator = segmentIds.iterator();
		while(iterator.hasNext()) {
			int segmentId = iterator.next();
			res.getChildren().add(createEllipse(segmentId));
			res.getChildren().add(visualizeDnaContent(segmentId));
		}
		return res;
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
	
	public void redraw() {
		innerGroup.getChildren().clear();
		innerGroup.getChildren().add(getGraph());
		updateView();
		double maxY = dbm.getDbReader().getMaxYCoord();
		innerGroup.setScaleY(720.0 / maxY);
		innerGroup.setScaleX(MIN_SCALE);
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public Group getInnerGroup() {
		return innerGroup;
	}
	
	public void setRibbonPane(ScrollPane scroll) {
		otherPane = scroll;
	}
	
	public void setRibbonGroup(Group group) {
		otherGroup = group;
	}
	
	public void setGenomeIds(ArrayList<Integer> ids) {
		genomeIds = ids;
	}
	
	public ArrayList<Integer> createList() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			list.add(i);
		}
		return list;
	}
	
	/**
	 * Returns a visualization of a graph segment
	 */
	
	public Ellipse createEllipse(int segmentId) {
		String content = segmentdna.get(segmentId - 1);
		double xcoord = graphxcoords.get(segmentId - 1);
		double ycoord = graphycoords.get(segmentId - 1);
		double xradius = 30 + 2 * Math.log(content.length());
		Ellipse node = new Ellipse(xcoord, ycoord, xradius, 30);
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
		double xcoord = graphxcoords.get(segmentId - 1);
		double ycoord = graphycoords.get(segmentId - 1);
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < content.length() && j <= 4; j++) {
			sb.append(content.substring(j, j + 1));
		}
		if ( content.length() > 5) {
			sb.append("...");
		}
		Text dnatext = new Text();
		dnatext.setTextAlignment(TextAlignment.CENTER);
		dnatext.setText(sb.toString());
		double width = dnatext.getLayoutBounds().getWidth();
		dnatext.setLayoutX(xcoord - 0.5 * width);
		dnatext.setLayoutY(ycoord + 5);
		return dnatext;
	}
}
