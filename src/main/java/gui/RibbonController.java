package gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;

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
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import parsers.XlsxParser;
import db.DatabaseManager;
import gui.phylogeny.NewickColourMatching;

/**
 * Controller class for the Ribbon screen/tab.
 */
public class RibbonController implements Initializable {
	@FXML private GridPane pane;
	@FXML private ScrollPane scrollPane;
	private ScrollPane otherPane;

	private ScrollPane annotationRibbonPane;
	private ScrollPane annotationGraphPane;

	@FXML private CheckBox checkboxSnp;
	@FXML private CheckBox checkboxInsert;
	
	private DatabaseManager dbm = Launcher.dbm;
	
	private Group innerGroup;
	private Group outerGroup;
	private Group otherGroup;
	private Group annotationRibbonGroup;
	private Group annotationGraphGroup;


	private HashMap<String, String> lineages = updateLineages();
	private ArrayList<Integer> genomeIds = createList();
	private Group collapsedGroup = createCollapsedRibbons();
	private Group normalGroup = createNormalRibbons();
	
    private static final double MAX_SCALE = 1.0d;
    private static final double MIN_SCALE = .003d;
    private static final double COLLAPSE = .2;
    private static final double GRAPH = .8;
	
	private double prevScale = 1;
	
	/**
	 * HashMap containing the lineages of the specimens.
	 */
	
	private static String xlsxpath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TB10" + File.separator + "metadata" + ".xlsx";
	
	/**
	 * Handles the scroll wheel event for the ribbon view.
	 */
	private final EventHandler<ScrollEvent> scrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			event.consume();
			if (event.isControlDown()) {
				double deltaY = event.getDeltaY();
				double delta = 1.2;
				double scale = innerGroup.getScaleX();
				double oldBarValue = scrollPane.getHvalue();

				if (deltaY < 0) {
					scale /= Math.pow(delta, -event.getDeltaY() / 20);
					scale = scale < MIN_SCALE ? MIN_SCALE : scale;
				} else if (deltaY > 0) {
					scale *= Math.pow(delta, event.getDeltaY() / 20);
					scale = scale > MAX_SCALE ? MAX_SCALE : scale;
				}
				if (prevScale > COLLAPSE && scale <= COLLAPSE) {
					innerGroup.getChildren().clear();
					Group temp = new Group(collapsedGroup);
					innerGroup.getChildren().addAll(temp.getChildren());
					scrollPane.setHvalue(oldBarValue);
				} else if (prevScale < COLLAPSE && scale >= COLLAPSE 
						|| prevScale > GRAPH && scale <= GRAPH) {
					innerGroup.getChildren().clear();
					Group temp = new Group(normalGroup);
					innerGroup.getChildren().addAll(temp.getChildren());
					scrollPane.setHvalue(oldBarValue);
				} else if (prevScale < GRAPH && scale >= GRAPH) {
					innerGroup.getChildren().clear();
					Group temp = new Group(otherGroup);
					innerGroup.getChildren().addAll(temp.getChildren());
					scrollPane.setHvalue(oldBarValue);
				}
				
				double barValue = scrollPane.getHvalue();
				innerGroup.setScaleX(scale);
				scrollPane.setHvalue(barValue);
				otherGroup.setScaleX(scale);
				otherPane.setHvalue(barValue);
				
				annotationRibbonGroup.setScaleX(scale);
				annotationGraphGroup.setScaleX(scale);
				annotationRibbonPane.setHvalue(barValue);
				annotationGraphPane.setHvalue(barValue);
				prevScale = scale;
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
	 * Event handler for keyboard events with the ribbon view.
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

			if (character.equals("+") || character.equals("=")) {
				scale *= delta;

				scale = scale > MAX_SCALE ? MAX_SCALE : scale;
			} else if (character.equals("-")) {
				scale /= delta;
				scale = scale < MIN_SCALE ? MIN_SCALE : scale;
			} else {
				return;
			}
			
			double barValue = scrollPane.getHvalue();
			innerGroup.setScaleX(scale);
			otherGroup.setScaleX(scale);
			scrollPane.setHvalue(barValue);
			otherPane.setHvalue(barValue);
			annotationRibbonGroup.setScaleX(scale);
			annotationRibbonPane.setPrefWidth(scrollPane.getPrefWidth());
			annotationGraphGroup.setScaleX(scale);
			annotationRibbonPane.setPrefWidth(scrollPane.getPrefWidth());

		}
	};
    
	
	/**
	 * function that gets executed when the matching fxml file is loaded.
	 * 
	 * The group is from within the FXML file. We use that group to add events and the pannable 
	 * canvas on which the drawing of the ribbon takes place.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateView();
		scrollPane.addEventFilter(ScrollEvent.ANY, scrollEventHandler);
		scrollPane.addEventFilter(KeyEvent.ANY, keyEventHandler);
		
		// Resize the scrollpane along with the window.
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
				annotationRibbonPane.setHvalue(newValue.doubleValue());
				annotationGraphPane.setHvalue(newValue.doubleValue());
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
		innerGroup.setScaleY(720.0 / maxY);
		innerGroup.setScaleX(MIN_SCALE);
		
	}
	
	private HashMap<String, String> updateLineages() {
		XlsxParser xlsxparser = new XlsxParser();
		xlsxparser.parse(xlsxpath);
		return xlsxparser.getLineages();
	}
	
	/**
	 * Updates the view. Used when changing database files so the graph
	 * will have to adjust to the new file.
	 */
	public void updateView() {
		// Inner group and outer group according to the ScrollPane JavaDoc.
		innerGroup = new Group(collapsedGroup);
		outerGroup = new Group(innerGroup);
		scrollPane.setContent(outerGroup);
	}
	

	/**
	 * Creates all paths that make up the ribbons and returns a {@link Group}
	 * containing those paths.
	 * 
	 * @return A group containing the ribbons.
	 */
	public Group createNormalRibbons() {
		System.out.println("Creating normal ribbons");
		Group res = new Group();
		ArrayList<ArrayList<Integer>> links = dbm.getDbReader().getLinks(genomeIds);
		ArrayList<ArrayList<Integer>> counts = dbm.getDbReader().getLinkWeights(genomeIds);
		ArrayList<ArrayList<Paint>> colours = calculateColours(links, genomeIds);
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> ycoords = dbm.getDbReader().getAllYCoord();
		
		for (int fromId = 1; fromId <= links.size(); fromId++) {
			for (int j = 0; j < links.get(fromId - 1).size(); j++) {
				int toId = links.get(fromId - 1).get(j);
				Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
						xcoords.get(toId - 1), ycoords.get(toId - 1));
				line.setStrokeWidth(counts.get(fromId - 1).get(j));
				line.setStroke(colours.get(fromId - 1).get(j));
		        res.getChildren().add(line);
			}
		}
		System.out.println("Finished normal ribbons");
		return res;
	}
	
	public Paint getLineColor(int ff, int tt) {
		Paint color = Paint.valueOf("0xff0000ff");
		ArrayList<String> from = dbm.getDbReader().getGenomesThroughSegment(ff);
		ArrayList<String> to = dbm.getDbReader().getGenomesThroughSegment(tt);
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
	
	private ArrayList<ArrayList<Paint>> calculateColours(ArrayList<ArrayList<Integer>> linkIds, 
			ArrayList<Integer> genomes) {
		ArrayList<ArrayList<Paint>> colours = 
				new ArrayList<ArrayList<Paint>>();
		for (int i = 0; i < dbm.getDbReader().countSegments(); i++) {
			colours.add(new ArrayList<Paint>());
		}
		ArrayList<String> genomeNames = dbm.getDbReader().getGenomeNames(genomes);
		
		HashMap<Integer, ArrayList<Integer>> hash = dbm.getDbReader().getGenomesPerLink(genomes);
		for (int i = 0; i < linkIds.size(); i++) {
			for (int j = 0; j < linkIds.get(i).size(); j++) {
				ArrayList<Integer> genomeIds = hash.get(100000 * (i + 1) 
						+ linkIds.get(i).get(j));
				int id = genomeIds.get(0);
				Paint colour = Paint.valueOf("0xff0000ff");
				String genome = genomeNames.get(id - 1);
				if (!genome.startsWith("M")) {
					colour = NewickColourMatching
							.getLineageColour(lineages.get(genome));
				} 
				colours.get(i).add(colour);
			}
		}
		return colours;
	}
	
	/**
	 * Creates all paths that make up the collapsed ribbons and returns a
	 * {@link Group} containing those paths.
	 * 
	 * @return A group containing the ribbons.
	 */
	public Group createCollapsedRibbons() {
		System.out.println("Creating collapsed ribbons");
		Group res = new Group();
		ArrayList<ArrayList<Integer>> links = dbm.getDbReader().getLinks(genomeIds);
		ArrayList<ArrayList<Integer>> counts = dbm.getDbReader().getLinkWeights(genomeIds);
		ArrayList<ArrayList<Paint>> colours = calculateColours(links, genomeIds);
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> ycoords = dbm.getDbReader().getAllYCoord();
		Queue<int[]> bubbles = new LinkedList<>(dbm.getDbReader().getBubbles(genomeIds));
		
		List<Integer> ignore = new LinkedList<>();
		
		for (int fromId = 1; fromId <= links.size(); fromId++) {
			List<Integer> edges = links.get(fromId - 1);
			
			if (!bubbles.isEmpty() && fromId == bubbles.peek()[0]) {
				int[] bubble = bubbles.poll();
				Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
						xcoords.get(bubble[1] - 1), ycoords.get(bubble[1] - 1));
				double width = bubble[2];
				line.setStrokeWidth(2 * width);
				line.setStroke(colours.get(fromId - 1).get(0));
		        res.getChildren().add(line);
		        ignore.addAll(edges);
			} else {
				if (ignore.contains(fromId)) {
					continue;
				}
				for (int toId : edges) {
					if (!bubbles.isEmpty() && toId == bubbles.peek()[1]) {
						break;
					}

					for (int j = 0; j < links.get(fromId - 1).size(); j++) {
						Line line = new Line(xcoords.get(fromId - 1), 
								ycoords.get(fromId - 1), 
								xcoords.get(toId - 1), 
								ycoords.get(toId - 1));
						line.setStrokeWidth(2 * counts.get(fromId - 1).get(j));
						line.setStroke(colours.get(fromId - 1).get(j));
						res.getChildren().add(line);
					}
				}
			}
		}
		System.out.println("Finished collapsed ribbons");
		
		return res;
	}

	public void redraw() {
		collapsedGroup = createCollapsedRibbons();
		normalGroup = createNormalRibbons();
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
	
	public void setGraphPane(ScrollPane scroll) {
		otherPane = scroll;
	}
	
	public void setGraphGroup(Group group) {
		otherGroup = group;
	}
	
	public void setAnnotationRibbonGroup(Group group) {
		annotationRibbonGroup = group;
	}
	
	public void setAnnotationRibbonPane(ScrollPane scrollpane) {
		annotationRibbonPane = scrollpane;
	}
	
	public void setAnnotationGraphGroup(Group group) {
		annotationGraphGroup = group;
	}
	
	public void setAnnotationGraphPane(ScrollPane scrollpane) {
		annotationGraphPane = scrollpane;
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
}
