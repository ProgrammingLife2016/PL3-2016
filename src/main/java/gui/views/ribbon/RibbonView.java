package gui.views.ribbon;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.scene.Group;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;

import db.DatabaseManager;
import gui.controllers.SplashController;
import gui.views.phylogeny.NewickColourMatching;
import parsers.XlsxParser;

public class RibbonView {
	
	private static String xlsxpath = System.getProperty("user.dir") + File.separator + "Data"
			+ File.separator + "TB10" + File.separator + "metadata" + ".xlsx";
	
	private DatabaseManager dbm;
	private HashMap<String, String> lineages = updateLineages();
	private ArrayList<Integer> genomeIds = createList();
	
	public RibbonView(DatabaseManager dbm) {
		this.dbm = dbm;
	}
	
	private HashMap<String, String> updateLineages() {
		XlsxParser xlsxparser = new XlsxParser();
		xlsxparser.parse(xlsxpath);
		return xlsxparser.getLineages();
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
				line.setStrokeWidth(calculateLineWidth(counts.get(fromId - 1).get(j)));

				line.setStroke(colours.get(fromId - 1).get(j));
		        res.getChildren().add(line);
			}
		}
		System.out.println("Finished normal ribbons");
		return res;
	}

	/**
	 * Determines if a bubble is a snip or not
	 * @param startBubble - the start of the bubble
	 * @param endBubble - the end of the bubble
	 * @param set	A set of snip segments.
	 * @return	returns true if it is a snip and false otherwise.
	 */
	public boolean isSnip(int startBubble, int endBubble, Set<Integer> set) {
		int current = startBubble + 1;
		while (current != endBubble) {
			if (!set.contains(current)) {
				return false;
			}
			++current;
		}
		return true;
	}
	
	/**
	 * Calculates the segments that belong to a snip. Returns that as a set.
	 * @return	returns a set of integers of segments that are in a snip.
	 */
	public Set<Integer> calculateSnipSegments() {
		List<int[]> bubblesList = dbm.getDbReader().getBubbles();
		
		Set<Integer> set = dbm.getDbReader().getSnipMaterial();
		Set<Integer> set2 = new HashSet<Integer>();
		
		for (int i = 0; i < bubblesList.size(); ++i) {
			int startBubble = bubblesList.get(i)[0];
			int endBubble = bubblesList.get(i)[1];
			if (endBubble - startBubble > 2) {
				if (isSnip(startBubble, endBubble, set)) {
					int current = startBubble;
					while (current != endBubble + 1) {
						set2.add(current);
						++current;
					}
				}

			}
		}
		
		System.out.println("DONE SNIP SEGMENTS CALC");
		return set2;
	}
	
	/**
	 * This method makes a set of all InDels
	 * @return	return a set of segment numbers that are in InDels.
	 */
	public Set<Integer> calculateInDelSegments() {
		List<int[]> bubblesList = dbm.getDbReader().getBubbles();
		Set<Integer> set = new HashSet<Integer>();
		
		for (int i = 0; i < bubblesList.size(); ++i) {
			int endBubble = bubblesList.get(i)[1];
			if (i + 1 < bubblesList.size()) {
				int nextStartBubble = bubblesList.get(i + 1)[0];
				if (nextStartBubble - endBubble == 2) {
					int current = endBubble;
					while (current != nextStartBubble + 1) {
						set.add(current);
						++current;
						
					}
				}
			}
		}
		System.out.println("DONE IN DEL SEGMENTS CALC");
		return set;
	}
	
	/**
	 * Creates the snips.
	 * @return
	 */
	public Group createSnips() {
		calculateInDelSegments();
		System.out.println("highLighting snips");
		Group res = new Group();
		ArrayList<ArrayList<Integer>> links = dbm.getDbReader().getLinks(genomeIds);
		ArrayList<ArrayList<Integer>> counts = dbm.getDbReader().getLinkWeights(genomeIds);
		ArrayList<ArrayList<Paint>> colours = calculateColours(links, genomeIds);
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> ycoords = dbm.getDbReader().getAllYCoord();
		Set<Integer> snipSet = calculateSnipSegments();
		
		for (int fromId = 1; fromId <= links.size(); fromId++) {
			for (int j = 0; j < links.get(fromId - 1).size(); j++) {
				int toId = links.get(fromId - 1).get(j);
				Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
						xcoords.get(toId - 1), ycoords.get(toId - 1));
				line.setStrokeWidth(calculateLineWidth(counts.get(fromId - 1).get(j)));
				if (snipSet.contains(fromId) && snipSet.contains(toId)) {
					line.setStroke(colours.get(fromId - 1).get(j));
				} else {
					line.setStroke(NewickColourMatching.getDeactivatedColour());
				}
		        res.getChildren().add(line);
			}
		}
		
		
		System.out.println("Finished snips");
		return res;
		
	}
	
	/**
	 * creates inDel
	 * @return
	 */
	public Group createInDels() {
		System.out.println("highLighting inDel");
		Group res = new Group();
		ArrayList<ArrayList<Integer>> links = dbm.getDbReader().getLinks(genomeIds);
		ArrayList<ArrayList<Integer>> counts = dbm.getDbReader().getLinkWeights(genomeIds);
		ArrayList<ArrayList<Paint>> colours = calculateColours(links, genomeIds);
		ArrayList<Integer> xcoords = dbm.getDbReader().getAllXCoord();
		ArrayList<Integer> ycoords = dbm.getDbReader().getAllYCoord();
		Set<Integer> inDelSet = calculateInDelSegments();
		
		
		for (int fromId = 1; fromId <= links.size(); fromId++) {
			for (int j = 0; j < links.get(fromId - 1).size(); j++) {
				int toId = links.get(fromId - 1).get(j);
				Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
						xcoords.get(toId - 1), ycoords.get(toId - 1));
				line.setStrokeWidth(calculateLineWidth(counts.get(fromId - 1).get(j)));
				if (inDelSet.contains(fromId) && inDelSet.contains(toId)) {
					line.setStroke(colours.get(fromId - 1).get(j));
				} else {
					line.setStroke(NewickColourMatching.getDeactivatedColour());
				}
		        res.getChildren().add(line);
			}
		}
		
		System.out.println("Finished InDel");
		return res;
	}
	
	
	
	/**
	 * Calculates the colors for all of the links.
	 * @param linkIds
	 * @param genomes
	 * @return
	 */
	public Paint getLineColor(int ff, int tt) {
		Paint color = Paint.valueOf("0xff0000ff");
		ArrayList<String> from = dbm.getDbReader().getGenomesThroughSegment(ff, genomeIds);
		ArrayList<String> to = dbm.getDbReader().getGenomesThroughSegment(tt, genomeIds);
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
	
	private ArrayList<ArrayList<Paint>> calculateColours(ArrayList<ArrayList<Integer>> linkIds, 
			ArrayList<Integer> genomes) {
		System.out.println("Starting color creation");
		ArrayList<ArrayList<Paint>> colours = 
				new ArrayList<ArrayList<Paint>>();
		for (int i = 0; i < dbm.getDbReader().countSegments(); i++) {
			colours.add(new ArrayList<Paint>());
		}
		ArrayList<String> genomeNames = dbm.getDbReader().getGenomeNames();
		
		HashMap<Integer, ArrayList<Integer>> hash = dbm.getDbReader().getGenomesPerLink(genomes);
		for (int i = 0; i < linkIds.size(); i++) {
			for (int j = 0; j < linkIds.get(i).size(); j++) {
				ArrayList<Integer> genomeIds = hash.get(100000 * (i + 1) 
						+ linkIds.get(i).get(j));
				int id = genomeIds.get(0);
				Paint colour = Paint.valueOf("0xff0000ff");
				String genome = genomeNames.get(id - 1);
				if (!genome.startsWith("M")) {
					ArrayList<String> a = dbm.getDbReader().getGenomeNames(genomeIds);
					colour = getMajorityColor(a);
				} 
				colours.get(i).add(colour);
			}
		}
		System.out.println("Finished color creation");
		return colours;
	}
	
	/**
	 * Calculates the majority color that is present in all the genomes that are provided.
	 * @param genomeNames
	 * @return
	 */
	private Paint getMajorityColor(ArrayList<String> genomeNames) {
		ArrayList<Paint> colors = getColorList(genomeNames);
		Map<Paint, Long> map = colors.stream()
		        .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

		List<Entry<Paint, Long>> result = map.entrySet().stream()
		        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
		        .limit(1)
		        .collect(Collectors.toList());
		return result.get(0).getKey();
	}
	
	/**
	 * Creates a list of colors from the list of genome names.
	 * @param genomeNames
	 * @return
	 */
	private ArrayList<Paint> getColorList(ArrayList<String> genomeNames) {
		ArrayList<Paint> colors = new ArrayList<Paint>();
		for (String genome : genomeNames) {
			if (!genome.startsWith("M")) {
				colors.add(NewickColourMatching
						.getLineageColour(lineages.get(genome)));
			} 
			else {
				colors.add(Paint.valueOf("0xff000000"));
			}
		}
		return colors;
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
		System.out.println("Enter for loop");
		for (int fromId = 1; fromId <= links.size(); fromId++) {
			System.out.println("i = " + fromId);
			if (fromId == links.size()/4) {
				SplashController.progressNum.set(40);
			}
			else if(fromId == links.size()/4 * 2) {
				SplashController.progressNum.set(50);
			}
			else if(fromId == links.size()/4 * 3) {
				SplashController.progressNum.set(60);
			}
			List<Integer> edges = links.get(fromId - 1);
			
			if (!bubbles.isEmpty() && fromId == bubbles.peek()[0]) {
				System.out.println("a");
				int[] bubble = bubbles.poll();
				System.out.println("b");
				Line line = new Line(xcoords.get(fromId - 1), ycoords.get(fromId - 1), 
						xcoords.get(bubble[1] - 1), ycoords.get(bubble[1] - 1));
				System.out.println("c");
				line.setStrokeWidth(calculateLineWidth(2 * bubble[2]));
				System.out.println("d");
				line.setStroke(getLineColor(fromId, bubble[1]));
				System.out.println("e");
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
						line.setStrokeWidth(
								calculateLineWidth(2 * counts.get(fromId - 1).get(j)));
						line.setStroke(getLineColor(fromId, toId));
						res.getChildren().add(line);
					}
				}
			}
		}
		System.out.println("Finished collapsed ribbons");
		
		return res;
	}
	
	/**
	 * calculates if the width of the line is acceptable.
	 * @param width
	 * @return
	 */
	private double calculateLineWidth(double width) {
		double minimum = 3;
		double maximum = 20;
		if (width < minimum) {
			return minimum;
		} else if (width > maximum) {
			return maximum;
		}
		return width;
	}
}
