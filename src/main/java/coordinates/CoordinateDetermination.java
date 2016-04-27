package coordinates;

import java.util.ArrayList;

public class CoordinateDetermination {
	
	private static ArrayList<Integer> fromIDs;
	private static ArrayList<Integer> toIDs;
	
	private static int[] noOfGpS;
	private static Coordinate[] coordinates;
	private static int[] cWeights;
	
	public static Coordinate[] calcCoords() {
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
	
	public static void storeCoord(Coordinate c, int segID, int weight) {
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
	
	
	private static ArrayList<Integer> getTo(int fromID) {
		//return getToIDs(fromID);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < fromIDs.size(); i++) {
			if (fromIDs.get(i) == fromID) {
				list.add(toIDs.get(i));
			}
		}
		return list;
	}
	
	/***
	 * TODO: Uncomment the 2 lines in the getData method and remove the rest
	 */
	
	private static void getData() {

		//fromIDs = getAllFromID();
		//toIDs = getAllToID();
		fromIDs = new ArrayList<Integer>();
		toIDs = new ArrayList<Integer>();
		
		addDummyFromIDData();
		addDummyToIDData();
		addDummyGpSData();
	}
	
	/***
	 * TODO: Remove the 3 dummy methods
	 */
	
	private static void addDummyGpSData() {
		noOfGpS = new int[9];
		noOfGpS[0] = 4;
		noOfGpS[1] = 2;
		noOfGpS[2] = 2;
		noOfGpS[3] = 1;
		noOfGpS[4] = 1;
		noOfGpS[5] = 1;
		noOfGpS[6] = 2;
		noOfGpS[7] = 2;
		noOfGpS[8] = 4;
	}
	
	private static void addDummyFromIDData() {
		fromIDs.add(1);
		fromIDs.add(1);
		fromIDs.add(2);
		fromIDs.add(2);
		fromIDs.add(3);
		fromIDs.add(3);
		fromIDs.add(4);
		fromIDs.add(5);
		fromIDs.add(6);
		fromIDs.add(7);
		fromIDs.add(8);
	}
	
	private static void addDummyToIDData() {
		toIDs.add(2);
		toIDs.add(3);
		toIDs.add(4);
		toIDs.add(5);
		toIDs.add(6);
		toIDs.add(8);
		toIDs.add(7);
		toIDs.add(7);
		toIDs.add(8);
		toIDs.add(9);
		toIDs.add(9);
	}
	
	
	/***
	 * TODO: Add the right database method to the following methods
	 */
	
	public static int countGenomesInLink(int i, int j) {
		if(i == 1 || i == 7 || i == 8) {return 2;}
		else return 1;	
	}
	
	public static int countGenomesInSeg(int segmentID) {
		return noOfGpS[segmentID-1];
	}
	
	public static ArrayList<Integer> getToIDs(int fromID) {
		return getTo(fromID);
	}
	
	
}
