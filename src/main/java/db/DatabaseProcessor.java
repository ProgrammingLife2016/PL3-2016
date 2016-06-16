package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import coordinates.Coordinate;
import coordinates.CoordinateDetermination;
import db.tuples.BubbleTuple;
import gui.controllers.SplashController;

/**
 * @author Rob Kapel
 * 
 * Class for pre-processing data in your database.
 */
public class DatabaseProcessor {
	private Statement db;
	private DatabaseReader dbr;
	
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
			System.out.println(i);
			System.out.println(coordinates[i - 1]);
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
	
	/**
	 * Identifies the innermost bubbles (without inner bubbles) and inserts the
	 * start and end segment id of each bubble that is found into the 'BUBBLES'
	 * table.
	 */
	public void locateBubbles() {

		ArrayList<ArrayList<Integer>> links = dbr.getLinks();
		for (int segmentId = 1; segmentId <= links.size(); segmentId++) {
			System.out.println(segmentId);
			ArrayList<Integer> outgoingEdges = links.get(segmentId - 1);
			System.out.println("a");
			if (outgoingEdges.size() > 1) {
				System.out.println("b");
				int firstChildId = outgoingEdges.get(0);
				System.out.println("c");
				int secondChildId = outgoingEdges.get(1);
				System.out.println("d");
				ArrayList<Integer> firstChildEdges = links.get(firstChildId - 1);
				System.out.println("e");
				ArrayList<Integer> secondChildEdges = links.get(secondChildId - 1);
				System.out.println("f");
				int firstChildEdge;
				int secondChildEdge;
				try {
					System.out.println("g");
					firstChildEdge = firstChildEdges.get(0);
					secondChildEdge = secondChildEdges.get(0);
				} catch (IndexOutOfBoundsException e) {
					System.out.println("Skipping segment: " + segmentId);
					break;
				}
				if (secondChildEdge == firstChildEdge) {
					try {
						System.out.println("h");
						ArrayList<Integer> genomeIds = dbr.getGenomesInBubble(segmentId, 
								firstChildEdge, firstChildId, secondChildId);
						System.out.println("i");
						for (int i = 0; i < genomeIds.size(); i++) {
							System.out.println("i: " + i);
							this.db.executeUpdate(new BubbleTuple(segmentId, 
									firstChildEdge, genomeIds.get(i))
									.getInsertQuery());
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
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
		ArrayList<Integer> from = dbr.getAllFromId();
		
		ArrayList<ArrayList<Integer>> allLinks = new ArrayList<ArrayList<Integer>>();
		
		SplashController.progressString.set("Starting to analyze genomes");
		
		for (int i = 1; i <= dbr.countGenomes(); i++) {
			ArrayList<ArrayList<Integer>> links = analyzeGenome(i);
			allLinks.addAll(links);
			SplashController.progressString.set(i + "genome(s) analyzed");
		}
		
		SplashController.progressString.set("Storing link data");
		
		try {
			this.db.executeUpdate("DELETE FROM LINKS");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < allLinks.size(); i++) {
			if ( ((i + 1) % from.size()) / 10 == 0) {
				SplashController.progressString.set((i * 100 / from.size() + 1) + "% Stored");
			}
			ArrayList<Integer> link = allLinks.get(i);
			updateDblinkcount(link.get(0), link.get(1), link.get(2));
		}
	}
	
	/**
	 * Go over a genome and store the links the genome uses, by increasing the count by 1.
	 * 
	 * @param map The HashMap where the data about the links is stored
	 * @param genomeID The ID of the genome we're analyzing
	 * @return An updated HashMap where data about the genome we analyzed is also stored
	 */
	public ArrayList<ArrayList<Integer>> analyzeGenome(int genomeId) {
		String query = "SELECT * "
				+ "FROM GENOMESEGMENTLINK WHERE GENOMEID = " + genomeId;
		ArrayList<ArrayList<Integer>> links = new ArrayList<ArrayList<Integer>>();
		try (ResultSet rs = this.db.executeQuery(query)) {
			if (rs.next()) {
				int first = rs.getInt(1);
				int second;
				while (rs.next()) {
					ArrayList<Integer> link = new ArrayList<Integer>();
					second = rs.getInt(1);
					link.add(first);
					link.add(second);
					link.add(genomeId);
					links.add(link);
					first = second;
				}
				return links;
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
