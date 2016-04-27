package main;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

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
		    
//		    ArrayList<Integer> testing = this.getAllFromId();
//		    for(int x : testing) {
//		    	System.out.println(x);
//		    }
		    
		    
		   // this.printWholeTable("Article");
		    
//		    
		    
		    this.insertIntoTable(null, "test", 1);
		    
		    
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
//		PreparedStatement ps = this.dbConnection.prepareStatement(
//		        "INSERT INTO " + tableName + " (segmentID, DNA) VALUES (?,?)");
//	      ps.setInt(1, segmentID);
	      Reader bodyIn = new FileReader("TB10.gfa");
	      
	      try (Stream<String> lines = Files.lines(Paths.get("TB10.gfa"))) {
	    	  	Iterator<String> it = lines.iterator();
	    	  	//while(it.hasNext()) {
	    	  		System.out.println(it.next());
	    	  		System.out.println(it.next());
	    	  		System.out.println(it.next());
	    	  		System.out.println(it.next());
	    	  	//}
	    	  	//System.out.println(lines.iterator().next());

	    	  	//System.out.println(lines.findFirst().get());
	    	    //String line32 = lines.skip(31).findFirst().get();
	    	    //System.out.println(line32);
	    	}
	     
	      
//	      ps.setCharacterStream(2, bodyIn);
//	      ps.executeUpdate();
	      bodyIn.close();
	      //ps.close();	
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

