package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import coordinates.Coordinate;
import coordinates.CoordinateDetermination;


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
		for (int i = 1; i <= coordinates.length; i++) {
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
	 * Gets the current count of genomes through a specific link from 
	 * the database and increases it by count.
	 * 
	 * @param fromID The start ID of the link
	 * @param toID The end ID of the link
	 * @param count The value to increase the count in the database with
	 */
	public void updateDblinkcount(int fromId, int toId, int count ) {
		try {
			int currentCount = dbr.getLinkcount(fromId, toId);
			this.db.executeUpdate("UPDATE LINKS SET COUNT = " + (currentCount + count) 
					+ " WHERE FROMID = " + fromId + " AND TOID = " + toId);
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
		for (int i = 0; i < from.size(); i++) {
			hashmap.put(noOfSegments * (from.get(i) - 1) + to.get(i) - 1, 0);
		}
		for (int i = 1; i <= dbr.countGenomes(); i++) {
			hashmap = analyzeGenome(hashmap, i);
		}
		for (int i = 0; i < from.size(); i++) {
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
		try {
			System.out.println(genomeId);
			ResultSet rs = this.db.executeQuery("SELECT * "
					+ "FROM GENOMESEGMENTLINK WHERE GENOMEID = " + genomeId);
			rs.next();
			int first = rs.getInt(1);
			int second;
			while (rs.next()) {
				second = rs.getInt(1);
				int currentcount = map.remove(noOfSegments * (first - 1) + second - 1);
				map.put(noOfSegments * (first - 1) + second - 1, currentcount + 1);
				first = second;
			}
			return map;
		} catch (SQLException e) {
			e.printStackTrace();
			return map;
		}
	}
	
}
