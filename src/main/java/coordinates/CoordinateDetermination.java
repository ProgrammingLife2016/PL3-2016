package coordinates;

import db.DatabaseManager;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class CoordinateDetermination {
	/**
	 * List with starting points of each link.
	 * The segment Id's correspond to those in the "toIDs" list (in order).
	 */
	private ArrayList<Integer> fromIDs;
	
	/**
	 * List with ending points of each link.
	 * The segment Id's correspond to those in the "fromIDs" list (in order).
	 */
	private ArrayList<Integer> toIDs;
	
	/**
	 * ?
	 */
	private int[] noOfGpS;
	
	/**
	 * List with coordinates of each segment.
	 */
	private Coordinate[] coordinates;
	
	/**
	 * List with amount of genomes that travel throgh a certain link.
	 */
	private int[] cweights;
	
	/**
	 * Database Object required to extract required data about segments and links.
	 */
	private DatabaseManager dbm;
	
	/**
	 * Constructor. Gets required data from database.
	 * @param dbm
	 */
	public CoordinateDetermination(DatabaseManager dbm) {
		this.dbm = dbm;
		getData();
	}
	
	/**
	 * Calculates coordinates involving all segments to determine a path to be used by ribbons.
	 * 
	 * @return coordinates
	 * 				Array of coordinates of segments.
	 */
	public Coordinate[] calcCoords() {
		getData();
		int noOfSegments = toIDs.get(toIDs.size() - 1);
		
		/***
		 * TODO: Substitute "4" for number of genomes
		 */
		coordinates = new Coordinate[noOfSegments];
		cweights = new int[noOfSegments];
		coordinates[0] = new Coordinate(0,10);
		cweights[0] = 10;
		
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
	 * Stores a specific coordinate in the global coordinates list.
	 * 
	 * @param coord
	 * @param segId
	 * @param weight
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
	
	private ArrayList<Integer> getTo(int fromId) {
		return dbm.getDBReader().getToIDs(fromId);
	}

	private void getData() {
		fromIDs = dbm.getDBReader().getAllFromID();
		toIDs = dbm.getDBReader().getAllToID();
	}
	
	public int countGenomesInLink(int start, int end) {
		return dbm.getDBReader().countGenomesInLink(start, end);
	}
	
	public int countGenomesInSeg(int segmentId) {
		return dbm.getDBReader().countGenomesInSeg(segmentId);
	}
	
	public ArrayList<Integer> getToIDs(int fromId) {
		return dbm.getDBReader().getToIDs(fromId);
	}
}
