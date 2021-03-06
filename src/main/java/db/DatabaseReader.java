package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class for executing queries to read data out of a database.
 */
public class DatabaseReader {
	private Statement db;
	
	public DatabaseReader(Statement db) {
		this.db = db;
	}
	
	/**
	 * Returns the number of genomes for each segment of the database
	 * @return an ArrayList of the number of genomes for each segment of the database
	 */	
	public ArrayList<Integer> countAllGenomesInSeg() {
		ArrayList<Integer> segments = new ArrayList<Integer>();
		String query = "SELECT SEGMENTID, COUNT(GENOMEID)"
				+ "FROM GENOMESEGMENTLINK GROUP BY SEGMENTID";
		try (ResultSet rs = this.db.executeQuery(query)) {
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
	 * Returns the genome id's that are contained in the given bubble. The
	 * bubble is defined as its from id and to id and two of of the nodes that
	 * are part of the bubble.
	 * 
	 * @param fromId
	 *            The fromid of the bubble
	 * @param toId
	 *            The toid tof the bubble
	 * @param branch1
	 *            The first node of the nodes that are contained within the
	 *            bubble.
	 * @param branch2
	 *            The second node of the nodes that are contained within the
	 *            bubble.
	 * @return A list of the genome id's that are contained in the given bubble.
	 */
	public ArrayList<Integer> getGenomesInBubble(int fromId, int toId, int branch1, int branch2) {
		ArrayList<Integer> genomes = new ArrayList<Integer>();
		String query = "SELECT GENOMEID FROM (SELECT GENOMEID, COUNT(*) AS C FROM LINKS WHERE "
				+ "(FROMID = " + fromId + " AND TOID = " + branch1 + ")"
				+ "OR (FROMID = " + branch1 + " AND TOID = " + toId + ")"
				+ "OR (FROMID = " + fromId + " AND TOID = " + branch2 + ")"
				+ "OR (FROMID = " + branch2 + " AND TOID = " + toId + ")"
				+ "GROUP BY GENOMEID)"
				+ "WHERE C > 1";
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				genomes.add(rs.getInt(1));
			}
			return genomes;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/**
	 * Returns the names of genomes for a segment of the database
	 * @return an ArrayList of the names of genomes for a segment of the database
	 */
	public ArrayList<String> getGenomesThroughSegment(int seg, ArrayList<Integer> genomeIds) {
		ArrayList<Integer> segments = new ArrayList<Integer>();
		String query = "SELECT GENOMEID "
				+ "FROM GENOMESEGMENTLINK WHERE SEGMENTID = " + seg;
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				int k = rs.getInt(1);
				if(genomeIds.contains(k)) {
					segments.add(rs.getInt(1));
				}
			}
			return getGenomesThroughSegmentHelper(segments);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the names of genomes for each segment of the database
	 * @return an ArrayList of the names of genomes for each segment of the database
	 */
	public ArrayList<ArrayList<String>> getGenomesThroughEachSegment(ArrayList<Integer> genomeIds) {
		ArrayList<ArrayList<String>> segments = new ArrayList<ArrayList<String>>();
		int noOfSegments = this.countSegments();
		for (int i = 0; i < noOfSegments; i++) {
			segments.add(new ArrayList<String>());
		}
		if (genomeIds.size() == 0) {
			return segments;
		}
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT A1.SEGMENTID, GENOMES.NAME FROM GENOMES INNER JOIN (SELECT GENOMEID, SEGMENTID FROM GENOMESEGMENTLINK WHERE GENOMESEGMENTLINK.GENOMEID = ");
		sb.append(genomeIds.get(0));
		for(int i = 1; i < genomeIds.size(); i++) {
			sb.append(" OR GENOMESEGMENTLINK.GENOMEID = " + genomeIds.get(i));
		}
		sb.append(") AS A1 ON A1.GENOMEID = GENOMES.ID");
		try (ResultSet rs = this.db.executeQuery(sb.toString())) {
			while (rs.next()) {
				int segmentId = rs.getInt(1);
				String content = rs.getString(2);
				segments.get(segmentId-1).add(content);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return segments;
	}

	/**
	 * Returns the number of genomes for each segment of the database
	 * @return an ArrayList of the number of genomes for each segment of the database
	 */	
	private ArrayList<String> getGenomesThroughSegmentHelper(ArrayList<Integer> ints) {
		ArrayList<String> segments = new ArrayList<String>();
		for (int i : ints) {
			String query = "SELECT NAME "
					+ "FROM GENOMES WHERE ID = " + i;
			try (ResultSet rs = this.db.executeQuery(query)) {
				if (rs.next()) {
					segments.add(rs.getString(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		return segments;
	}
	
	/**
	 * Returns the number of genomes in the database, or -1 if an error occurs
	 * 
	 * @return the number of genomes in the database, or -1 if an error occurs
	 */
	public int countGenomes() {
		String query = "SELECT COUNT(ID) FROM GENOMES";
		try (ResultSet rs = this.db.executeQuery(query)) {
			if (rs.next()) {
				return rs.getInt(1);
			}
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns all genome id's of the given genome names.
	 * @param names Genome names to retreive the ids of.
	 * @return A list containing genome id's of the given genome names.
	 */
	public ArrayList<Integer> getGenomeIds(ArrayList<String> names) {
		ArrayList<Integer> genomeIds = new ArrayList<Integer>();
		String query = "SELECT ID,NAME FROM GENOMES ";
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				String name = rs.getString(2);
				if (names.contains(name)) {
					genomeIds.add(rs.getInt(1));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return genomeIds;
	}
	
	/**
	 * Returns the amount of segments in the database
	 * @return the amount of segments in the database
	 */
	public int countSegments() {
		String query = "SELECT COUNT(ID) FROM SEGMENTS";
		try (ResultSet rs = this.db.executeQuery(query)) {
			if (rs.next()) {
				return rs.getInt(1);
			}
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns a list containing all bubbles. Each bubble is given as an int
	 * array containing the start segment id at position 0 and the end segment
	 * id at position 1.
	 * 
	 * @return a list containing all bubbles, or null if the database could not
	 *         be read.
	 */
	public List<int[]> getAllBubbles() {

		List<int[]> bubbleList = new ArrayList<>();

		String query = "SELECT DISTINCT FROMID, TOID FROM BUBBLES ORDER BY FROMID";
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				bubbleList.add(new int[]{rs.getInt(1),rs.getInt(2)});
			 }
			return bubbleList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns all id's of segmnents witch have only one DNA character in them.
	 * 
	 * @return A list of all id's of segmnents witch have only one DNA character
	 *         in them.
	 */
	public Set<Integer> getSnipMaterial() {
		Set<Integer> set = new LinkedHashSet<Integer>();
		String query = "SELECT ID FROM SEGMENTS WHERE LENGTH(CONTENT) = 1";
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				set.add(rs.getInt(1));
			 }
			return set;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns all bubble start and end id's of the given genome id's.
	 * @param genomes The genome id's.
	 * @return A list containing all bubble start and end id's of the given genome id's.
	 */
	public List<int[]> getBubbles(ArrayList<Integer> genomes) {

		List<int[]> bubbleList = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT DISTINCT FROMID, TOID, COUNT(*) FROM BUBBLES WHERE GENOMEID = "
				+ genomes.get(0));
		for (int i = 1; i < genomes.size(); i++) {
			sb.append(" OR GENOMEID = " + genomes.get(i));
		}
		sb.append(" GROUP BY FROMID, TOID ORDER BY FROMID");

		try (ResultSet rs = this.db.executeQuery(sb.toString())) {
			while (rs.next()) {
				bubbleList.add(new int[]{rs.getInt(1),rs.getInt(2),rs.getInt(3)});
			 }
			return bubbleList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns all id's of the segments that have one or more outgoing links.
	 * them.
	 * 
	 * @return All id's of the segments that have one or more outgoing links.
	 */
	public ArrayList<Integer> getAllFromId() {
		String query = "SELECT DISTINCT FROMID, TOID FROM LINKS";
		ArrayList<Integer> fromIdList = new ArrayList<Integer>();
		try (ResultSet rs = this.db.executeQuery(query)) {
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

		for (int i = 1; i <= this.countGenomes(); i++) {
			String query = "SELECT SEGMENTID FROM GENOMESEGMENTLINK "
					+ "WHERE GENOMEID = " + i + " LIMIT 1";
			try (ResultSet rs = db.executeQuery(query)) {
				if (rs.next()) {
					segmentList.add(rs.getInt(1));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				continue;
			}
		}
		return segmentList;
	}
	
	/**
	 * Returns the starting location in the reference genome of each annotation
	 * @return the starting location in the reference genome of each annotation
	 */
	public ArrayList<Integer> getAllAnnotationStartLocations() {
		String query = "SELECT * FROM ANNOTATION WHERE TYPE = \'CDS\' ORDER BY START";
		try (ResultSet rs = this.db.executeQuery(query)) {
			ArrayList<Integer> startList = new ArrayList<Integer>();
			while (rs.next()) {
				startList.add(rs.getInt(4));
			 }
			return startList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the ending location in the reference genome of each annotation
	 * @return the ending location in the reference genome of each annotation
	 */	
	public ArrayList<Integer> getAllAnnotationEndLocations() {
		String query = "SELECT * FROM ANNOTATION WHERE TYPE = \'CDS\' ORDER BY START";
		try (ResultSet rs = this.db.executeQuery(query)) {
			ArrayList<Integer> endList = new ArrayList<Integer>();
			while (rs.next()) {
				endList.add(rs.getInt(5));
			 }
			return endList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the name of each annotation
	 * @return the name of each annotation
	 */
	public ArrayList<String> getAllAnnotationNames() {
		String query = "SELECT * FROM ANNOTATION WHERE TYPE = \'CDS\' ORDER BY START";
		try (ResultSet rs = this.db.executeQuery(query)) {
			ArrayList<String> nameList = new ArrayList<String>();
			while (rs.next()) {
				nameList.add(rs.getString(10));
			 }
			return nameList;
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
	public ArrayList<Integer> getAllXCoord() {
		String query = "SELECT * FROM SEGMENTS";
		try (ResultSet rs = this.db.executeQuery(query)) {
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
		String query = "SELECT * FROM SEGMENTS";
		try (ResultSet rs = this.db.executeQuery(query)) {
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
	 * Returns the uppermost y coordinate of all segments.
	 * @return The uppermost y coordinate of all segments.
	 */
	public int getMaxYCoord() {
		String query = "SELECT MAX(YCOORD) FROM SEGMENTS";
		try (ResultSet rs = this.db.executeQuery(query)) {
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
		
	/**
	 * Returns all links in the dataset. For each segment, an arraylist is
	 * created in which they can store the segments they link to. All these
	 * arraylists are then put into an arraylist of arraylists.
	 * 
	 * @return Per segment an arraylist of where the respective segments links
	 *         to.
	 */
	public ArrayList<ArrayList<Integer>> getLinks() {

		String query = "SELECT FROMID, TOID, COUNT(*) FROM LINKS GROUP BY FROMID, TOID";
		ArrayList<ArrayList<Integer>> linkList = new ArrayList<ArrayList<Integer>>();
		
		for (int i = 0; i < this.countSegments(); i++) {
			linkList.add(new ArrayList<Integer>());
		}
		
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				linkList.get(rs.getInt(1) - 1).add(rs.getInt(2));
			}
			return linkList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return linkList;
	}
	
	/**
	 * Returns all links containing the given genomes.
	 * @param genomes A list of genome id's.
	 * @return All links containing the given genomes.
	 */
	public ArrayList<ArrayList<Integer>> getLinks(ArrayList<Integer> genomes) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT FROMID, TOID, COUNT(*) FROM LINKS WHERE GENOMEID = "
				+ genomes.get(0));
		for (int i = 1; i < genomes.size(); i++) {
			sb.append(" OR GENOMEID = " + genomes.get(i));
		}
		sb.append(" GROUP BY FROMID, TOID");
		ArrayList<ArrayList<Integer>> linkList = new ArrayList<ArrayList<Integer>>();
		
		for (int i = 0; i < this.countSegments(); i++) {
			linkList.add(new ArrayList<Integer>());
		}
		
		try (ResultSet rs = this.db.executeQuery(sb.toString())) {
			while (rs.next()) {
				linkList.get(rs.getInt(1) - 1).add(rs.getInt(2));
			}
			return linkList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return linkList;
	}
	
	/**
	 * Returns a Hashmap containing all links that contain a genome of the input list.
	 * @param genomes A list of genome id's.
	 * @return a Hashmap containing all links that contain a genome of the input list.
	 */
	public HashMap<Integer, ArrayList<Integer>> getGenomesPerLink(ArrayList<Integer> genomes) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT FROMID, TOID, GENOMEID FROM LINKS WHERE GENOMEID = "
				+ genomes.get(0));
		for (int i = 1; i < genomes.size(); i++) {
			sb.append(" OR GENOMEID = " + genomes.get(i));
		}
		HashMap<Integer, ArrayList<Integer>> hash = new HashMap<Integer, ArrayList<Integer>>();
		
		try (ResultSet rs = this.db.executeQuery(sb.toString())) {
			while (rs.next()) {
				int key = 100000 * rs.getInt(1) + rs.getInt(2);
				ArrayList<Integer> link = hash.get(key);
				if (link == null) {
					link = new ArrayList<Integer>();
				}
				link.add(rs.getInt(3));
				hash.put(key, link);
			}
			return hash;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return hash;
	}
	
	/**
	 * Returns the number of genomes through a link for each link. For each segment, an arraylist 
	 * is created in which they can store per segment how many genomes use that link. All these 
	 * arraylists are then put into an arraylist of arraylists.
	 * @return for each link how many genomes pass through.
	 */
	public ArrayList<ArrayList<Integer>> getLinkWeights() {
		String query = "SELECT FROMID, TOID, COUNT(*) FROM LINKS GROUP BY FROMID, TOID";
		ArrayList<ArrayList<Integer>> linkList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < this.countSegments(); i++) {
			linkList.add(new ArrayList<Integer>());
		}
		
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				linkList.get(rs.getInt(1) - 1).add(rs.getInt(3));
			}
			return linkList;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return linkList;
	}
	
	/**
	 * Returns the number of genomes that run through the links containing a
	 * genome from the input list.
	 * 
	 * @param genomes
	 *            A list of genome id's.
	 * @return The number of genomes that run through the links containing a
	 *         genome from the input list.
	 */
	public ArrayList<ArrayList<Integer>> getLinkWeights(ArrayList<Integer> genomes) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT FROMID, TOID, COUNT(*) FROM LINKS WHERE GENOMEID = "
				+ genomes.get(0));
		for (int i = 1; i < genomes.size(); i++) {
			sb.append(" OR GENOMEID = " + genomes.get(i));
		}
		sb.append(" GROUP BY FROMID, TOID");
		ArrayList<ArrayList<Integer>> linkList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < this.countSegments(); i++) {
			linkList.add(new ArrayList<Integer>());
		}
		
		try (ResultSet rs = this.db.executeQuery(sb.toString())) {
			while (rs.next()) {
				linkList.get(rs.getInt(1) - 1).add(rs.getInt(3));
			}
			return linkList;
		} catch (SQLException e) {
			e.printStackTrace();
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
		String query = "SELECT TOID FROM LINKS WHERE FROMID = " + fromId;
		try (ResultSet rs = this.db.executeQuery(query)) {
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
	 * Returns the names of genomes in the database, or an empty arrayList if an error occurs
	 * 
	 * @return the names of genomes in the database, or an empty arrayList if an error occurs
	 */
	public ArrayList<String> getGenomeNames() {
		String query = "SELECT DISTINCT NAME FROM GENOMES";
		ArrayList<String> genomeNames = new ArrayList<String>();
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				genomeNames.add(rs.getString(1));
			}
			return genomeNames;
		} catch (SQLException e) {
			e.printStackTrace();
			return genomeNames;
		}
	}
	
	/**
	 * Returns the names of the given genomes.
	 * @param genomes List of genome id's.
	 * @return The names of the given genomes.
	 */
	public ArrayList<String> getGenomeNames(ArrayList<Integer> genomes) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT NAME FROM GENOMES WHERE ID = "
				+ genomes.get(0));
		for (int i = 1; i < genomes.size(); i++) {
			sb.append(" OR ID = " + genomes.get(i));
		}

		ArrayList<String> genomeNames = new ArrayList<String>();
		try (ResultSet rs = this.db.executeQuery(sb.toString())) {
			while (rs.next()) {
				genomeNames.add(rs.getString(1));
			}
			return genomeNames;
		} catch (SQLException e) {
			e.printStackTrace();
			return genomeNames;
		}
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
		String query = "SELECT CONTENT FROM SEGMENTS WHERE ID = " + segmentId;
		try (ResultSet rs = this.db.executeQuery(query)) {
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<String> getAllContent() {
		String query = "SELECT CONTENT FROM SEGMENTS";
		List<String> res = new LinkedList<>();
		try (ResultSet rs = this.db.executeQuery(query)) {
			while (rs.next()) {
				res.add(rs.getString(1));
			}
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
