package main;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
   
		   //this.dropTable("LINKS");

		    
		    
//		    this.createTable("Article");
//		    Reader bodyIn = new FileReader("TB10.gfa");
//		    this.insertIntoTable(bodyIn, "Article", 1);
//		    
//		    this.createTableTest("LINKS");
//		    this.insertIntoTableTest("LINKS", 1, 2);
//		    this.insertIntoTableTest("LINKS", 1, 3);
		    //this.printTable("LINKS");
		    
		    ArrayList<Integer> testing = this.getlinksToId();
		    for(int x : testing) {
		    	System.out.println(x);
		    }
		   // this.printWholeTable("Article");
		    
//		    

		    
		    
		  //  this.db.executeUpdate("DROP ALL OBJECTS DELETE FILES");
		    
		    
		   //this.printSegment("Article", 1);
		  // this.printTable("Article");
		   
		   
		  // this.cleanAll();
		    db.close();
		    dbConnection.close();
		    //this.printWholeSegment("Article", 1);

		}
		catch( Exception e )
		{
		    System.out.println( e.getMessage() );
		}
	}
	
	private void dropTable(String tableName) throws SQLException {
		this.db.executeUpdate("DROP TABLE " + tableName);
	}
	
	private void cleanAll() throws SQLException {
		this.db.executeUpdate("DROP ALL OBJECTS DELETE FILES");
	}

	private void createTable(String tableName) throws SQLException {
		this.db.executeUpdate("CREATE TABLE " + tableName + " (" + "segmentID INT, " + "DNA CLOB)");
	}
	
	private void createTableTest(String tableName) throws SQLException {
		this.db.executeUpdate("CREATE TABLE " + tableName + " (" + "FROMID INT, " + "TOID INT)");
	}
	
	private void insertIntoTable(Reader something, String tableName, int segmentID) throws SQLException, IOException {
		PreparedStatement ps = this.dbConnection.prepareStatement(
		        "INSERT INTO " + tableName + " (segmentID, DNA) VALUES (?,?)");
	      ps.setInt(1, segmentID);
	      Reader bodyIn = new FileReader("TB10.gfa");
	      ps.setCharacterStream(2, bodyIn);
	      ps.executeUpdate();
	      bodyIn.close();
	      ps.close();	
	}
	private void insertIntoTableTest(String tableName, int fromID, int toID) throws SQLException {
		this.db.executeUpdate("INSERT INTO " + tableName + " VALUES(" + fromID + "," + toID + ")");
	}
	
//	private void printTable(String tableName) throws SQLException {
//		ResultSet rs = this.db.executeQuery("SELECT * FROM " + tableName);
//		while(rs.next() )
//		    {
//		        int segmentID = rs.getInt("segmentID");
//		        String DNA = rs.getString("DNA");
//		        System.out.println("segmentID = " + segmentID);
//		        System.out.println("DNA = " + DNA);
//		    }
//	}
	
	private void printTable(String tableName) throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT * FROM " + tableName);
		ArrayList<Integer> fromIDList = new ArrayList();
		while(rs.next())
		    {
				fromIDList.add(rs.getInt(1));
		        System.out.println(rs.getString(1));
		    }
	}
	
	
	
	private void printSegment(String tableName, int segmentID) throws SQLException {
	      ResultSet res = this.db.executeQuery("SELECT * FROM " + tableName 
	        +" WHERE segmentID = '"+segmentID+"'");
	      //initially positioned BEFORE the first row
	      res.next();
	      System.out.println("segmentID = "+res.getString("segmentID"));
	      System.out.println("DNA = "+res.getString("DNA")); 
	      res.close();
	}
	
//	private Array getGenomeID(int segmentID) throws SQLException {
//		ResultSet rs = this.db.executeQuery(
//				"SELECT NAME FROM GENOMES INNER JOIN GENOMESEGMENTLINK ON SEGMENTID = ID WHERE  SEGMENTID = " + segmentID);
//		return rs.getArray(1);	
//	}
	
	private ArrayList<Integer> getlinksFromId() throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT * FROM LINKS");
		ArrayList<Integer> fromIDList = new ArrayList<Integer>();
		while(rs.next()) {
			fromIDList.add(rs.getInt(1));
		 }
		return fromIDList;
	}
	
	private ArrayList<Integer> getlinksToId() throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT * FROM LINKS");
		ArrayList<Integer> toIDList = new ArrayList<Integer>();
		while(rs.next()) {
			toIDList.add(rs.getInt(2));
		 }
		return toIDList;
	}
	
}

