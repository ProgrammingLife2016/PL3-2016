package coordinates;

import java.util.ArrayList;

import db.DatabaseReader;
import gui.SplashController;

/**
 * @author Rob Kapel
 * 
 * Class for determining the coordinates of the path that a ribbon should travel.
 */
public class CoordinateDetermination {
	
	/**
	 * List with starting points of each link.
	 * The segment Id's correspond to those in the "toIDs" list (in order).
	 */
	protected ArrayList<Integer> fromIDs;
	
	/**
	 * List with ending points of each link.
	 * The segment Id's correspond to those in the "fromIDs" list (in order).
	 */
	protected ArrayList<Integer> toIDs;
	
	/**
	 * List for each link per segment.
	 */
	
	protected ArrayList<ArrayList<Integer>> links;
	
	/**
	 * List for each weight per segment
	 */
	
	protected ArrayList<ArrayList<Integer>> counts;
	
	/**
	 * List of number of genomes through each segment
	 */
	
	protected ArrayList<Integer> segmentWeights;
	
	/**
	 * ?
	 */
	protected int[] noOfGpS;
	
	/**
	 * List with coordinates of each segment.
	 */
	protected Coordinate[] coordinates;
	
	/**
	 * List with amount of genomes that travel through a certain link.
	 */
	protected int[] cweights;
	
	/**
	 * Database Object required to extract required data about segments and links.
	 */
	protected DatabaseReader dbr;
	
	/**
	 * Takes in a DatabaseReader and determines the coordinates.
	 * @param dbr - the DatabaseReader
	 */
	public CoordinateDetermination(DatabaseReader dbr) {
		this.dbr = dbr;
		getData();
	}
	
	/**
	 * Calculates coordinates for segments using parsed arrays from the database.
	 * 
	 * @return list of coordinates of segments.
	 */
	public Coordinate[] calcCoords() {
		int noOfSegments = dbr.countSegments();
		coordinates = new Coordinate[noOfSegments];
		cweights = new int[noOfSegments];
		calcStartCoords();
		SplashController.progressString.set("Calculating segment coordinates");

		for (int i = 1; i <= noOfSegments; i++) {
			if ( (i % noOfSegments) / 10 == 0) {
				SplashController.progressString.set((i * 100 / noOfSegments) 
						+ 1 + "% Calculated");
			}
			int alreadyDrawn = 0;
			int leftToDraw = countGenomesInSeg(i);
			Coordinate coords = coordinates[i - 1];
			
			ArrayList<Integer> outgoingedges = links.get(i - 1);
			
			for (int j = 0; j < outgoingedges.size(); j++) {
				leftToDraw = leftToDraw - countGenomesInLink(i, outgoingedges.get(j));
				int xc = coords.getX() + 1;
				int yc = coords.getY() - leftToDraw + alreadyDrawn;
				storeCoord(new Coordinate(xc,yc), outgoingedges.get(j),
						countGenomesInLink(i, outgoingedges.get(j)));
				alreadyDrawn += countGenomesInLink(i, outgoingedges.get(j));
			}
		}
		return coordinates;
	}
	
	/**
	 * Calculates the beginning coordinates of the segments. This method
	 * gets the data from the database.
	 */
	private void calcStartCoords() {
		ArrayList<Integer> segIds = new ArrayList<Integer>();
		ArrayList<Integer> segWeights = new ArrayList<Integer>();
		ArrayList<Integer> firstSegments = dbr.getFirstOfAllGenomes();
		
		SplashController.progressString.set("Calculating start coordinates");
		for (int i = 1; i <= firstSegments.size(); i++) {
			int segId = firstSegments.get(i - 1);
			int index = segIds.indexOf(segId);
			if (index == -1) {
				segIds.add(segId);
				segWeights.add(1);
			} else {
				int currentWeight = segWeights.get(index);
				segWeights.set(index, currentWeight + 1);
			}
		}
		int genomesDrawn = 0;
		for (int i = 0; i < segIds.size(); i++) {
			int index = segIds.get(i);
			int weight = segWeights.get(i);
			coordinates[index - 1] = new Coordinate(0, 2 * genomesDrawn + weight);
			cweights[index - 1] = weight;
			genomesDrawn += weight;
		}
		
	}
	
	/**
	 * Stores an x,y coordinate of where a segment should appear.
	 * 
	 * @param coord
	 * 			Coordinate to be stored.
	 * @param segId
	 * 			Id of segment of which coordinate is to be stored.
	 * @param weight
	 * 			Amount of genomes that pass through a segment.
	 */
	public void storeCoord(Coordinate coord, int segId, int weight) {
		if (coordinates[segId - 1] == null) {
			coordinates[segId - 1] = coord;
			cweights[segId - 1] = weight;
		} else {
			Coordinate oldCoord = coordinates[segId - 1];
			int newX = Math.max(coord.getX(), oldCoord.getX());
			int newY = (coord.getY() * weight + oldCoord.getY() * cweights[segId - 1])
					/ (weight + cweights[segId - 1]);
			coordinates[segId - 1] = new Coordinate(newX, newY);
			cweights[segId - 1] += weight;
		}
	}
	
	
	/**
	 * Gets the id of all nodes that are going to the given node.
	 * @param fromId - the given node.
	 * @return
	 */
	private ArrayList<Integer> getTo(int fromId) {
		return dbr.getToIDs(fromId);
	}
	
	/**
	 * Runs the queries on the database to get the required data.
	 */
	private void getData() {
		fromIDs = dbr.getAllFromId();
		toIDs = dbr.getAllToId();
		links = dbr.getLinks();
		counts = dbr.getLinkWeights();
		segmentWeights = dbr.countAllGenomesInSeg();
		SplashController.progressString.set("From ID's size is: " + fromIDs.size());
	}
	
	/**
	 * Counts the number of genomes in the link between the two given nodes.
	 * @param from
	 * @param to
	 * @return
	 */
	public int countGenomesInLink(int from, int to) {
		int linkTo = links.get(from - 1).indexOf(to);
		return counts.get(from - 1).get(linkTo);
	}
	
	/**
	 * Counts the number of genomes that run through a given segment.
	 * @param segmentId
	 * @return
	 */
	public int countGenomesInSeg(int segmentId) {
		return segmentWeights.get(segmentId - 1);
	}

}
