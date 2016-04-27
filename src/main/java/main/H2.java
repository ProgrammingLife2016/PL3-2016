package main;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class H2 {
	private Statement db;
	private Connection dbConnection;
	
	H2() {
		try 
		{
		    Class.forName("org.h2.Driver");
		    Connection dbConnection = DriverManager.getConnection("jdbc:h2:~/PL3");
		    Statement db = dbConnection.createStatement();
		    this.db = db;
		    this.dbConnection = dbConnection;
		}
		catch( Exception e )
		{
		    System.out.println( e.getMessage() );
		}
	}
	
	private void closeDBConnection() throws SQLException {
		this.db.close();
		this.dbConnection.close();
	}
	private void dropTable(String tableName) throws SQLException {
		this.db.executeUpdate("DROP TABLE " + tableName);
	}
	
	private void cleanAll() throws SQLException {
		this.db.executeUpdate("DROP ALL OBJECTS DELETE FILES");
	}

	private void printTable(String tableName) throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT * FROM " + tableName);
		while(rs.next())
		    {
		        System.out.println(rs.getString(1) + " " + rs.getString(2));
		    }
	}
	
	private int countGenomesInSeg(int segmentID) throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT COUNT(GENOMEID) FROM GENOMESEGMENTLINK WHERE SEGMENTID = "  + segmentID);
		rs.next();
		return rs.getInt(1);
	}
	
	private int countGenomesInLink(int fromID, int toID) throws SQLException {
		String query = new StringBuilder()
				.append("SELECT COUNT(G1) ")
				.append("FROM (")
				.append("SELECT * FROM (")
				.append("SELECT FROMID, TOID ")
				.append("FROM LINKS ")
				.append("WHERE FROMID = " + fromID +" AND TOID = " + toID + " ")
				.append(") AS T1 ")
				.append("LEFT OUTER JOIN (SELECT SEGMENTID AS S1, GENOMEID AS G1 FROM GENOMESEGMENTLINK) AS GSL ")
				.append("ON T1.FROMID = GSL.S1 ")
				.append("LEFT OUTER JOIN (SELECT SEGMENTID AS S2, GENOMEID AS G2 FROM GENOMESEGMENTLINK) AS GSL2 ")
				.append("ON T1.TOID = GSL2.S2")
				.append(") ")
				.append("WHERE G1 = G2")
				.toString();
		ResultSet rs = this.db.executeQuery(query);
		rs.next();
		return rs.getInt(1);
	}
	
	private ArrayList<Integer> getAllFromID() throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT * FROM LINKS");
		ArrayList<Integer> fromIDList = new ArrayList<Integer>();
		while(rs.next()) {
			fromIDList.add(rs.getInt(1));
		 }
		return fromIDList;
	}
	
	private ArrayList<Integer> getAllToID() throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT * FROM LINKS");
		ArrayList<Integer> toIDList = new ArrayList<Integer>();
		while(rs.next()) {
			toIDList.add(rs.getInt(2));
		 }
		return toIDList;
	}
	
	private ArrayList<Integer> getFromIDs(int toID) throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT FROMID FROM LINKS WHERE TOID = " + toID);
		ArrayList<Integer> fromIDList = new ArrayList<Integer>();
		while(rs.next()) {
			fromIDList.add(rs.getInt(1));
		 }
		return fromIDList;
	}
	
	private ArrayList<Integer> getToIDs(int fromID) throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT TOID FROM LINKS WHERE FROMID = " + fromID);
		ArrayList<Integer> toIDList = new ArrayList<Integer>();
		while(rs.next()) {
			toIDList.add(rs.getInt(1));
		 }
		return toIDList;
	}
	
	private String getContent(int segmentID) throws SQLException, IOException {
		ResultSet rs = this.db.executeQuery("SELECT CONTENT FROM SEGMENTS WHERE ID = " + segmentID);
		rs.next();
		return rs.getString(1);
	}
	
}

