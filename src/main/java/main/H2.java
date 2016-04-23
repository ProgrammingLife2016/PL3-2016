package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
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
		    //stmt.executeUpdate( "DROP TABLE table1" );
//		    stmt.executeUpdate( "CREATE TABLE table1 ( user varchar(50) )" );
//		    stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Claudio' )" );
//		    stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Bernasconi' )" );
		    ArrayList<String> test = new ArrayList();
		    test.add("something");
		    
		  //  this.dropTable("table1");
		   // this.createTable("table1", test);
//		    this.inserTable("table1", "test");
//		    ResultSet rs = db.executeQuery("SELECT * FROM table1");
//		    while( rs.next() )
//		    {
//		        String name = rs.getString("user");
//		        System.out.println( name );
//		    }
		    
		    //this.dropTable("Article");

		    this.createTable("Article");
		    this.inserTable("test");
//		    
//		    
		    String subject = "Test of setCharacterStream() methods";
		    
		      ResultSet res = db.executeQuery("SELECT * FROM Article" 
		        +" WHERE Subject = '"+subject+"'");
		      res.next();
		      System.out.println("The inserted record: "); 
		      System.out.println("   Subject = "+res.getString("Subject"));
		      System.out.println("   Body = "
		        +res.getString("Body").substring(5000,5050)); 
		      res.close();
		    
		    
		    this.db.executeUpdate("DROP ALL OBJECTS DELETE FILES");
		    
		    
		    db.close();
		    dbConnection.close();
		    

		}
		catch( Exception e )
		{
		    System.out.println( e.getMessage() );
		}
	}
	
	private void dropTable(String tableName) throws SQLException {
		this.db.executeUpdate("DROP TABLE " + tableName);
	}

	
	private void createTable(String tableName) throws SQLException {
//		String dataType = "varchar(255)";
//		String primaryKey = "PRIMARY KEY (ID)";
//		String inputString = "ID int NOT NULL AUTO_INCREMENT" + ", ";
//		for(String x : columnNames) {
//			inputString+= x + " " + dataType + ",";
//		}
		//inputString += primaryKey;
		//this.db.executeUpdate("CREATE TABLE " + tableName + "(" + inputString + ")");
		this.db.executeUpdate("CREATE TABLE " + tableName + "(" + "Subject CLOB, " + "Body CLOB)");
	}
	
	private void inserTable(String tableName) throws SQLException, IOException {
		//String something = new File(".").getAbsolutePath();
		//System.out.println(something);
		PreparedStatement ps = this.dbConnection.prepareStatement(
		        "INSERT INTO Article (Subject, Body) VALUES (?,?)");
	      ps.setString(1, "Test of setCharacterStream() methods");
	      Reader bodyIn = new FileReader("TB10.gfa");
	      ps.setCharacterStream(2, bodyIn);
	      int count = ps.executeUpdate();
	      bodyIn.close();
	      ps.close();
		
//		Clob something = this.dbConnection.createClob();
//		long bla = "aaaaaaa";
//		something.setAsciiStream(pos)
//		this.db.executeUpdate("INSERT INTO " + tableName + " VALUES ( " + value + ")");
	}
	
}

