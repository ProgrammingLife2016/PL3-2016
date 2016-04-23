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
   
		    //this.dropTable("Article");

		    this.createTable("Article");
		    Reader bodyIn = new FileReader("TB10.gfa");
		    this.insertIntoTable(bodyIn, "Article", 1);
//		    
		    
		    
		   // this.printWholeTable("Article");
		    
//		    

		    
		    
		  //  this.db.executeUpdate("DROP ALL OBJECTS DELETE FILES");
		    
		    
		   //this.printSegment("Article", 1);
		   this.printTable("Article");
		   
		   
		   this.cleanAll();
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
	
	private void printTable(String tableName) throws SQLException {
		ResultSet rs = this.db.executeQuery("SELECT * FROM " + tableName);
		while(rs.next() )
		    {
		        int segmentID = rs.getInt("segmentID");
		        String DNA = rs.getString("DNA");
		        System.out.println("segmentID = " + segmentID);
		        System.out.println("DNA = " + DNA);
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
	
}

