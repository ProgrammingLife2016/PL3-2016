package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author Björn Ho, Daniel van de Berg, Rob Kapel
 *
 * Class for executing queries to read data out of a database.
 */
public class DatabaseReader {
	private Statement db;
	
	public DatabaseReader(Statement db) {
		this.db = db;
	}
	
	/**
	 * Returns the number of genomes that the given segment is part of, or -1 if
	 * no segment with the given id exists.
	 * 
	 * @param segmentID
	 *            Id of the segment.
	 * @return the number of genomes that the given segment is part of, or -1 if
	 *         no segment with the given id exists.
	 */
	public int countGenomesInSeg(int segmentId) {
		try {
			ResultSet rs = this.db.executeQuery("SELECT COUNT(GENOMEID)"
					+ "FROM GENOMESEGMENTLINK WHERE SEGMENTID = "  + segmentId);
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns the number of genomes for each segment of the database
	 * @return an ArrayList of the number of genomes for each segment of the database
	 */
	
	public ArrayList<Integer> countAllGenomesInSeg() {
		try {
			ArrayList<Integer> segments = new ArrayList<Integer>();
			ResultSet rs = this.db.executeQuery("SELECT SEGMENTID, COUNT(GENOMEID)"
					+ "FROM GENOMESEGMENTLINK GROUP BY SEGMENTID");
			while (rs.next()) {
				segments.add(rs.getInt(2));
			}
			return segments;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	/**
	 * Returns the number of genomes in the database, or -1 if an error occurs
	 * 
	 * @return the number of genomes in the database, or -1 if an error occurs
	 */
	public int countGenomes() {
		try {
			ResultSet rs = this.db.executeQuery("SELECT COUNT(ID) FROM GENOMES");
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns the amount of segments in the database
	 * @return the amount of segments in the database
	 */
	
	public int countSegments() {
		try {
			ResultSet rs = this.db.executeQuery("SELECT COUNT(ID) FROM SEGMENTS");
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns the number of genomes in the link between fromID and toID, given
	 * that a link exists between the 2. Returns -1 if no link exists.
	 * 
	 * @param fromID
	 *            Segment id.
	 * @param toID
	 *            Segment id.
	 * @return The number of genomes in the link from fromID to toID, or -1 if
	 *         the link does not exist.
	 */
	public int countGenomesInLink(int fromId, int toId) {
		try {
			ResultSet rs = this.db.executeQuery("SELECT * "
					+ "FROM LINKS WHERE FROMID = " + fromId + " AND TOID = " + toId);
			rs.next();
			return rs.getInt(3);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Return the amount of genomes through a certain link
	 * 
	 * @param fromID Start ID of the link
	 * @param toID End ID of the link
	 * @return the number of genomes through the link, or -1 if the link does not exist
	 */
	
	public int getLinkcount(int fromId, int toId) {
		try {
			ResultSet rs = this.db.executeQuery("SELECT * "
					+ "FROM LINKS WHERE FROMID = " + fromId + " AND TOID = " + toId);
			rs.next();
			return rs.getInt(3);
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns all id's of the segments that have one or more outgoing links.
	 * them.
	 * 
	 * @return All id's of the segments that have one or more outgoing links.
	 */
	public ArrayList<Integer> getAllFromId() {
		try {
			ResultSet rs = this.db.executeQuery("SELECT * FROM LINKS");
			ArrayList<Integer> fromIdList = new ArrayList<Integer>();
			while (rs.next()) {
				fromIdList.add(rs.getInt(1));
			 }
			return fromIdList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the first segment id of each genome in the database
	 * @return the first segment id of each genome in the database
	 */
	
	public ArrayList<Integer> getFirstOfAllGenomes() {
		ArrayList<Integer> segmentList = new ArrayList<Integer>();
		try {
			for (int i = 1; i <= this.countGenomes(); i++) {
				ResultSet rs = db.executeQuery("SELECT SEGMENTID FROM GENOMESEGMENTLINK "
						+ "WHERE GENOMEID = " + i + " LIMIT 1");
				rs.next();
				segmentList.add(rs.getInt(1));
			}
			return segmentList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns all x-coordinates of the segments in the database.
	 * 
	 * @return All x-coordinates of the segments in the database.
	 */
	public ArrayList<Integer> getAllXCoord() {
		try {
			ResultSet rs = this.db.executeQuery("SELECT * FROM SEGMENTS");
			ArrayList<Integer> fromIdList = new ArrayList<Integer>();
			while (rs.next()) {
				fromIdList.add(rs.getInt(3));
			 }
			return fromIdList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns all x-coordinates of the segments in the database.
	 * 
	 * @return All x-coordinates of the segments in the database.
	 */
	public ArrayList<Integer> getAllYCoord() {
		try {
			ResultSet rs = this.db.executeQuery("SELECT * FROM SEGMENTS");
			ArrayList<Integer> fromIdList = new ArrayList<Integer>();
			while (rs.next()) {
				fromIdList.add(rs.getInt(4));
			 }
			return fromIdList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns all id's of the segments that have one or more ingoing links.
	 * 
	 * @return All id's of the segments that have one or more ingoing links.
	 */
	public ArrayList<Integer> getAllToId() {
		try {
			ResultSet rs = this.db.executeQuery("SELECT * FROM LINKS");
			ArrayList<Integer> toIdList = new ArrayList<Integer>();
			while (rs.next()) {
				toIdList.add(rs.getInt(2));
			 }
			return toIdList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the number of genomes through each link in your database.
	 * 
	 * @return the number of genomes through each link in your database.
	 */
	
	public ArrayList<Integer> getAllCounts() {
		try {
			ResultSet rs = this.db.executeQuery("SELECT * FROM LINKS");
			ArrayList<Integer> toIdList = new ArrayList<Integer>();
			while (rs.next()) {
				toIdList.add(rs.getInt(3));
			 }
			return toIdList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/**
	 * Returns all id's of the links entering the segment with the given id, or
	 * null if no segment exists with the given id.
	 * 
	 * @param fromID
	 *            Segment id.
	 * @return All id's of the links entering the segment with the given id, or
	 *         null if no segment exists with the given id.
	 */
	public ArrayList<Integer> getFromIDs(int toId) {
		try {
			ResultSet rs = this.db.executeQuery("SELECT FROMID FROM LINKS WHERE TOID = " + toId);
			ArrayList<Integer> fromIdList = new ArrayList<Integer>();
			while (rs.next()) {
				fromIdList.add(rs.getInt(1));
			 }
			return fromIdList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns all links in the dataset. For each segment, an arraylist is created in which
	 * they can store the segments they link to. All these arraylists are then put into
	 * an arraylist of arraylists.
	 * @return Per segment an arraylist of where the respective segments links to.
	 */
	
	public ArrayList<ArrayList<Integer>> getLinks() {

		ArrayList<ArrayList<Integer>> linkList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i <= this.countSegments(); i++) {
			linkList.add(new ArrayList<Integer>());
		}
		
		ArrayList<Integer> fromIDs = this.getAllFromId();
		ArrayList<Integer> toIDs = this.getAllToId();
		
		for (int i = 1; i <= fromIDs.size(); i++) {
			int fromId = fromIDs.get(i - 1);
			int toId = toIDs.get(i - 1);
			linkList.get(fromId - 1).add(toId);
		}
		return linkList;
	}
	
	/**
	 * Returns the number of genomes through a link for each link. For each segment, an arraylist 
	 * is created in which they can store per segment how many genomes use that link. All these 
	 * arraylists are then put into an arraylist of arraylists.
	 * @return for each link how many genomes pass through.
	 */
	
	public ArrayList<ArrayList<Integer>> getLinkWeights() {

		ArrayList<ArrayList<Integer>> linkList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i <= this.countSegments(); i++) {
			linkList.add(new ArrayList<Integer>());
		}
		
		ArrayList<Integer> fromIDs = this.getAllFromId();
		ArrayList<Integer> weights = this.getAllCounts();
		
		
		for (int i = 1; i <= fromIDs.size(); i++) {
			int fromId = fromIDs.get(i - 1);
			int toId = weights.get(i - 1);
			linkList.get(fromId - 1).add(toId);
		}
		return linkList;
	}
	
	/**
	 * Returns all id's of the links leaving the segment with the given id, or
	 * null if no segment exists with the given id.
	 * 
	 * @param fromID
	 *            Segment id.
	 * @return All id's of the links leaving the segment with the given id, or
	 *         null if no segment exists with the given id.
	 */
	public ArrayList<Integer> getToIDs(int fromId) {
		try {
			ResultSet rs = this.db.executeQuery("SELECT TOID "
					+ "FROM LINKS WHERE FROMID = " + fromId);
			ArrayList<Integer> toIdList = new ArrayList<Integer>();
			while (rs.next()) {
				toIdList.add(rs.getInt(1));
			 }
			return toIdList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the contents of the segment with the given id, or null if no
	 * segment with the given id exists.
	 * 
	 * @param segmentID
	 *            the id of the segment of which to return the contents.
	 * @return The contents of the segment with the given id, or null if no
	 *         segment with the given id exists.
	 */
	public String getContent(int segmentId) {
		try {
			ResultSet rs = this.db.executeQuery("SELECT CONTENT "
					+ "FROM SEGMENTS WHERE ID = " + segmentId);
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
