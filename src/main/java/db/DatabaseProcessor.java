package db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseProcessor {
	private Statement db;
	private DatabaseReader dbr;
	private int noOfSegments;
	
	public DatabaseProcessor(Statement db, DatabaseReader dbr) {
		this.db = db;
		this.dbr = dbr;
	}
	
	public void updateDBLinkCount(int fromID, int toID, int count ) {
		try {
			int currentCount = dbr.getLinkCount(fromID, toID);
			this.db.executeUpdate("UPDATE LINKS SET COUNT = " + (currentCount + count) + " WHERE FROMID = " + fromID + " AND TOID = " + toID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void calculateLinkCounts() {
		HashMap<Integer, Integer> hashmap = new HashMap<Integer, Integer>();
		ArrayList<Integer> from = dbr.getAllFromId();
		ArrayList<Integer> to = dbr.getAllToId();
		noOfSegments = to.get(to.size() - 1);
		System.out.println("Hashmap");
		for(int i = 0; i < from.size(); i++) {
			System.out.println(i);
			hashmap.put(noOfSegments*(from.get(i)-1) + to.get(i)-1, 0);
		}
		System.out.println("Analyze genome");
		for(int i = 1; i <= dbr.countGenomes(); i++) {
			System.out.println(i);
			hashmap = analyzeGenome(hashmap, i);
		}
		System.out.println("Updating");
		for(int i = 0; i < from.size(); i++) {
			System.out.println(i);
			updateDBLinkCount(from.get(i), to.get(i), hashmap.get(noOfSegments*(from.get(i)-1) + to.get(i)-1));
		}
	}
	
	public HashMap<Integer, Integer> analyzeGenome(HashMap<Integer, Integer> map, int genomeID) {
		try {
			System.out.println(genomeID);
			ResultSet rs = this.db.executeQuery("SELECT * FROM GENOMESEGMENTLINK WHERE GENOMEID = " + genomeID);
			rs.next();
			int first = rs.getInt(1);
			int second;
			while(rs.next()) {
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
