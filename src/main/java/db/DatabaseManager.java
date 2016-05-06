package db;

import db.tables.Table;
import db.tuples.Tuple;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Provides access methods to the database.
 *
 * @author Björn Ho, Daniël van den Berg
 */
public class DatabaseManager {
	
	/**
	 * Object for connecting with database.
	 */
	private Connection dbConnection;
	
	/**
	 * Object required the the DatabaseReader to function.
	 */
	private Statement db;
	
	/**
	 * Object for executing queries in the H2 database.
	 */
	private DatabaseReader dbReader;
	private DatabaseProcessor dbProcessor;
	
	public DatabaseManager(String dbPath) {
		try {
		    dbConnection = DriverManager.getConnection("jdbc:h2:" + dbPath);
			db = dbConnection.createStatement();
			dbReader = new DatabaseReader(db);
			dbProcessor = new DatabaseProcessor(db, dbReader);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the connection to the database.
	 * 
	 * @return boolean, true if the connection is closed successfully, false otherwise.
	 */
	public boolean closeDbConnection() {
		try {
			this.db.close();
			this.dbConnection.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Creates the given tables in the database.
	 * 
	 * @param tables Tables to create.
	 * @return true if all tables are successfully created, false otherwise.
	 */
	public boolean createTables(List<Table> tables) {
		for (Table table : tables) {
			try {
				db.executeUpdate(table.getCreateQuery());
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Inserts the given tuple into the database.
	 * 
	 * @param tuple 	Tuple to insert into the database.
	 * @return true 	if the tuple is successfully inserted, false otherwise.
	 */
	public boolean insert(Tuple tuple) {
		try {
			this.db.executeUpdate(tuple.getInsertQuery());
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Clears all files in the db directory.
	 */
	public void clearDatabaseFiles() {
		String directoryName = System.getProperty("user.dir") + "/db/";
		File directory = new File(directoryName);
		File[] files = directory.listFiles();
		
		for (File file : files) {
			file.delete();
		}
	}
	
	public DatabaseReader getDbReader() {
		return this.dbReader;
	}
	
	public DatabaseProcessor getDbProcessor() {
		return this.dbProcessor;
	}
}
