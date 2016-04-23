package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class H2 {
	private Statement db;
	
	
	H2() {
		try 
		{
		    Class.forName("org.h2.Driver");
		    Connection dbConnection = DriverManager.getConnection("jdbc:h2:~/PL3");
		    Statement db = dbConnection.createStatement();
		    this.db = db;
		    db.close();
		    dbConnection.close();
		}
		catch( Exception e )
		{
		    System.out.println( e.getMessage() );
		}
	}
	
}

