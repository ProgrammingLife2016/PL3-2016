package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class H2 {
	private Statement db;
	
	H2() {
		try 
		{
		    Class.forName("org.h2.Driver");
		    Connection dbConnection = DriverManager.getConnection("jdbc:h2:~/PL3");
		    Statement db = dbConnection.createStatement();
		    this.db = db;
		    //stmt.executeUpdate( "DROP TABLE table1" );
//		    stmt.executeUpdate( "CREATE TABLE table1 ( user varchar(50) )" );
//		    stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Claudio' )" );
//		    stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Bernasconi' )" );
		    ArrayList<String> test = new ArrayList();
		    test.add("something");
		    
		    this.dropTable("table1");
		    this.createTable("table1", test);
//		    this.inserTable("table1", "test");
//		    ResultSet rs = db.executeQuery("SELECT * FROM table1");
//		    while( rs.next() )
//		    {
//		        String name = rs.getString("user");
//		        System.out.println( name );
//		    }
		    
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

	
	private void createTable(String tableName, ArrayList<String> columnNames) throws SQLException {
		String dataType = "varchar(255)";
		String primaryKey = "PRIMARY KEY (ID)";
		String inputString = "ID int NOT NULL AUTO_INCREMENT" + ", ";
		for(String x : columnNames) {
			inputString+= x + " " + dataType + ",";
		}
		inputString += primaryKey;
		this.db.executeUpdate("CREATE TABLE " + tableName + "(" + inputString + ")");
	}
	
	private void inserTable(String tableName, String value) throws SQLException {
		this.db.executeUpdate("INSERT INTO " + tableName + " VALUES ( " + value + ")");
	}
	
}

