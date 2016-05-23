package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import coordinates.Coordinate;
import coordinates.CoordinateDetermination;
import gui.SplashController;

/**
 * @author Rob Kapel
 * 
 * Class for pre-processing data in your database.
 */
public class DatabaseProcessor {
	private Statement db;
	private DatabaseReader dbr;
	private int noOfSegments;
	
	public DatabaseProcessor(Statement db, DatabaseReader dbr) {
		this.db = db;
		this.dbr = dbr;
	}
	
	/**
	 * Calculating the coordinates of segments and store them in the database
	 */
	public void updateCoordinates() {
		
		CoordinateDetermination coorddet = new CoordinateDetermination(dbr);
		Coordinate[] coordinates = coorddet.calcCoords();
		SplashController.progressString.set("Saving segment coordinates");
		for (int i = 1; i <= coordinates.length; i++) {
			if ( (i % coordinates.length) / 10 == 0) {
				SplashController.progressString
					.set((i * 100 / coordinates.length) + "% Stored");
			}
			try {
				this.db.executeUpdate("UPDATE SEGMENTS SET "
						+ "XCOORD = " + coordinates[i - 1].getX() 
						+ ", YCOORD = " + coordinates[i - 1].getY()
						+ " WHERE ID = " + i);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void collapseRibbons() {
		List<Integer> bubbleStarts = dbr.getBubbleStarts();
		List<Integer> bubbleEnds = dbr.getBubbleEnds();
		ArrayList<ArrayList<Integer>> links = dbr.getLinks();
		boolean[] visited = new boolean[dbr.countSegments()];
		System.out.println("length " + visited.length);
		int maxCount = 20;

		for (int i = 0; i < bubbleStarts.size() && i < maxCount; i++) {
			System.out.println("Start: " + bubbleStarts.get(i));
		}
		for (int i = 0; i < bubbleEnds.size() && i < maxCount; i++) {
			System.out.println("End: " + bubbleEnds.get(i));
		}
		
		
		int fromBubble = -1;
		int toBubble = -1;
		
//		Stack<Integer> buffer = new Stack<>();
//		buffer.push(bubbleStarts.get(0));
//
//		while (!buffer.isEmpty()) {
//			int id = buffer.pop();
////			visited[id - 1] = true;
//			System.out.println(id);
//			List<Integer> edges = links.get(id - 1);
//			for (int to : edges) {
//				System.out.println("to: " + to);
//				if (!visited[to - 1]) {
//					if (bubbleStarts.contains(to)) {
//						System.out.println("New from: " + to);
//						fromBubble = to;
//					} else if (bubbleEnds.contains(to)) {
//						System.out.println("Bubble: " + fromBubble + ", " + to);
//						break;
//					}
////					
//					buffer.push(to);
//				}
//			}
//		}
		
		Stack<Integer> buffer = new Stack<>();
		buffer.push(bubbleStarts.get(0));
		
		Stack<Integer> startIds = new Stack<>();
		int count = 0;
		while (!buffer.isEmpty() && count++ < 30) {
			int id = buffer.pop();
//			System.out.println(id);
			List<Integer> edges = links.get(id - 1);
			if (edges.size() > 1) {
				startIds.push(id);
				System.out.println("Pushed " + id);
			}
			for (int to : edges) {
				buffer.push(to);
				if (bubbleEnds.contains(to) && startIds.size() > 0) {
					System.out.println("Bubble: " + startIds.pop() + ", " + to);
					break;
				}
			}
			
		}
		
	}
	
	
	/**
	 * Gets the current count of genomes through a specific link from 
	 * the database and increases it by count.
	 * 
	 * @param fromID The start ID of the link
	 * @param toID The end ID of the link
	 * @param count The value to increase the count in the database with
	 */
	public void updateDblinkcount(int fromId, int toId, int count ) {
		try {
			//int currentCount = dbr.getLinkcount(fromId, toId);
			this.db.executeUpdate("INSERT INTO LINKS VALUES "
					+ "(" + fromId + " , " + toId + " , " + count + ")");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Method to determine how much genomes go through each link. 
	 * You allocate space for all links, then you analyze each genome.
	 * At the end, you update the data in the database.
	 */
	
	public void calculateLinkCounts() {
		HashMap<Integer, Integer> hashmap = new HashMap<Integer, Integer>();
		ArrayList<Integer> from = dbr.getAllFromId();
		ArrayList<Integer> to = dbr.getAllToId();
		noOfSegments = to.get(to.size() - 1);
		SplashController.progressString.set("Retrieving link data");
		for (int i = 0; i < from.size(); i++) {
			if ( ((i + 1) % from.size()) / 10 == 0) {
				SplashController.progressString
					.set((i * 100 / from.size() + 1) + "% Retrieved");
			}
			hashmap.put(noOfSegments * (from.get(i) - 1) + to.get(i) - 1, 0);
		}
		SplashController.progressString.set("Starting to analyze genomes");
		for (int i = 1; i <= dbr.countGenomes(); i++) {
			hashmap = analyzeGenome(hashmap, i);
			SplashController.progressString.set(i + "genome(s) analyzed");
		}
		SplashController.progressString.set("Storing link data");
		try {
			this.db.executeUpdate("DELETE FROM LINKS");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < from.size(); i++) {
			if ( ((i + 1) % from.size()) / 10 == 0) {
				SplashController.progressString.set((i * 100 / from.size() + 1) + "% Stored");
			}
			updateDblinkcount(from.get(i), to.get(i), 
					hashmap.get(noOfSegments * (from.get(i) - 1) + to.get(i) - 1));
		}
	}
	
	/**
	 * Go over a genome and store the links the genome uses, by increasing the count by 1.
	 * 
	 * @param map The HashMap where the data about the links is stored
	 * @param genomeID The ID of the genome we're analyzing
	 * @return An updated HashMap where data about the genome we analyzed is also stored
	 */
	public HashMap<Integer, Integer> analyzeGenome(HashMap<Integer, Integer> map, int genomeId) {
		String query = "SELECT * "
				+ "FROM GENOMESEGMENTLINK WHERE GENOMEID = " + genomeId;
		try (ResultSet rs = this.db.executeQuery(query)) {
			if (rs.next()) {
				int first = rs.getInt(1);
				int second;
				while (rs.next()) {
					second = rs.getInt(1);
					int currentcount = map.remove(noOfSegments * (first - 1) + second - 1);
					map.put(noOfSegments * (first - 1) + second - 1, currentcount + 1);
					first = second;
				}
				return map;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return map;
		}
	}
	
}
