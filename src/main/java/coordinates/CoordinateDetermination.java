package coordinates;

import java.util.ArrayList;

import db.DatabaseManager;

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
	 * List with amount of genomes that travel throgh a certain link.
	 */
	protected int[] cweights;
	
	/**
	 * Database Object required to extract required data about segments and links.
	 */
	protected DatabaseManager dbm;
	
	public CoordinateDetermination(DatabaseManager dbm) {
		this.dbm = dbm;
		getData();
	}
	
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
		for(int i = 1; i <= noOfSegments; i++) {
			System.out.println(i);
			int alreadyDrawn = 0;
			int leftToDraw = countGenomesInSeg(i);
			Coordinate c = coordinates[i-1];
			ArrayList<Integer> tos = getToIDs(i);
			
			for(int j = 0; j < tos.size(); j++) {
				leftToDraw = leftToDraw - countGenomesInLink(i, tos.get(j));
				int x = c.getX() + 1;
				int y = c.getY() - leftToDraw + alreadyDrawn;
				storeCoord(new Coordinate(x,y), tos.get(j), countGenomesInLink(i, tos.get(j)));
				alreadyDrawn += countGenomesInLink(i, tos.get(j));
			}
		}
		for (int i = 1; i <= 9; i++) {
			System.out.println("SegID: " + i);
			System.out.println("X: " + coordinates[i-1].getX());
			System.out.println("Y: " + coordinates[i-1].getY());
		}
		return coordinates;
	}
	
	public void storeCoord(Coordinate c, int segID, int weight) {
		if (coordinates[segID-1] == null) {
			coordinates[segID-1] = c;
			cweights[segID-1] = weight;
		}
		else {
			Coordinate oldCoord = coordinates[segID-1];
			int newX = Math.max(c.getX(), oldCoord.getX());
			int newY = (c.getY() * weight + oldCoord.getY() * cweights[segID-1])
					/ (weight + cweights[segID-1]);
			coordinates[segID-1] = new Coordinate(newX, newY);
			cweights[segID-1] += weight;
		}
	}
	
	
	@SuppressWarnings("unused")
	private ArrayList<Integer> getTo(int fromID) {
		return dbm.getDBReader().getToIDs(fromID);
	}

	private void getData() {
		fromIDs = dbm.getDBReader().getAllFromID();
		toIDs = dbm.getDBReader().getAllToID();
	}
	
	public int countGenomesInLink(int i, int j) {
		return dbm.getDBReader().countGenomesInLink(i, j);
	}
	
	public int countGenomesInSeg(int segmentID) {
		return dbm.getDBReader().countGenomesInSeg(segmentID);
	}
	
	public ArrayList<Integer> getToIDs(int fromID) {
		return dbm.getDBReader().getToIDs(fromID);
	}
	
}
