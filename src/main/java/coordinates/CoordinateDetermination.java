package coordinates;

import java.util.ArrayList;

import db.DatabaseManager;

public class CoordinateDetermination {
	
	private ArrayList<Integer> fromIDs;
	private ArrayList<Integer> toIDs;
	
	private int[] noOfGpS;
	private Coordinate[] coordinates;
	private int[] cWeights;
	private DatabaseManager dbm;
	
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
		cWeights = new int[noOfSegments];
		coordinates[0] = new Coordinate(0,4);
		cWeights[0] = 4;
		
		
		for(int i = 1; i <= noOfSegments; i++) {
			int alreadyDrawn = 0;
			int leftToDraw = countGenomesInSeg(i);
			Coordinate c = coordinates[i-1];
			ArrayList<Integer> tos = getToIDs(i);
			
			for(int j = 0; j < tos.size(); j++) {
				leftToDraw = leftToDraw - countGenomesInLink(i, j);
				int x = c.getX() + 1;
				int y = c.getY() - leftToDraw + alreadyDrawn;
				storeCoord(new Coordinate(x,y), tos.get(j), countGenomesInLink(i, j));
				alreadyDrawn += countGenomesInLink(i, j);
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
			cWeights[segID-1] = weight;
		}
		else {
			Coordinate oldCoord = coordinates[segID-1];
			int newX = Math.max(c.getX(), oldCoord.getX());
			int newY = (c.getY() * weight + oldCoord.getY() * cWeights[segID-1]) / (weight + cWeights[segID-1]);
			coordinates[segID-1] = new Coordinate(newX, newY);
			cWeights[segID-1] += weight;
		}
	}
	
	
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
