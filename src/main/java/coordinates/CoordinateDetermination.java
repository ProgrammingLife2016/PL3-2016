package coordinates;

import db.DatabaseManager;
import db.DatabaseReader;

import java.util.ArrayList;

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
		getData();
		int noOfSegments = toIDs.get(toIDs.size() - 1);
		coordinates = new Coordinate[noOfSegments];
		cweights = new int[noOfSegments];
		int genomeCount = dbr.countGenomes();
		coordinates[0] = new Coordinate(0, genomeCount);
		cweights[0] = genomeCount;
		
		System.out.println(noOfSegments);
		for (int i = 1; i <= noOfSegments; i++) {
			System.out.println(i);
			int alreadyDrawn = 0;
			int leftToDraw = countGenomesInSeg(i);
			Coordinate coords = coordinates[i - 1];
			ArrayList<Integer> outgoingedges = getToIDs(i);
			
			for (int j = 0; j < outgoingedges.size(); j++) {
				leftToDraw = leftToDraw - countGenomesInLink(i, outgoingedges.get(j));
				int xc = coords.getX() + 1;
				int yc = coords.getY() - leftToDraw + alreadyDrawn;
				storeCoord(new Coordinate(xc,yc), outgoingedges.get(j),
						countGenomesInLink(i, outgoingedges.get(j)));
				alreadyDrawn += countGenomesInLink(i, outgoingedges.get(j));
			}
		}
		for (int i = 1; i <= 9; i++) {
			System.out.println("SegID: " + i);
			System.out.println("X: " + coordinates[i - 1].getX());
			System.out.println("Y: " + coordinates[i - 1].getY());
		}
		return coordinates;
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
	
	
	@SuppressWarnings("unused")
	private ArrayList<Integer> getTo(int fromId) {
		return dbr.getToIDs(fromId);
	}

	private void getData() {
		fromIDs = dbr.getAllFromId();
		toIDs = dbr.getAllToId();
		System.out.println(fromIDs.size());
	}
	
	public int countGenomesInLink(int from, int to) {
		return dbr.countGenomesInLink(from, to);
	}
	
	public int countGenomesInSeg(int segmentId) {
		return dbr.countGenomesInSeg(segmentId);
	}
	
	public ArrayList<Integer> getToIDs(int fromId) {
		return dbr.getToIDs(fromId);
	}
	
}
