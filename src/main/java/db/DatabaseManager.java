package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import db.tables.Table;
import db.tuples.Tuple;

/**
 * 
 * Provides access methods to the database.
 *
 */
public class DatabaseManager {
	protected Statement db;
	protected Connection dbConnection;
	protected DatabaseReader dbReader;
	
	public DatabaseManager(String dbPath, List<Table> tables) {
		try {
		    dbConnection = DriverManager.getConnection("jdbc:h2:" + dbPath);
			db = dbConnection.createStatement();
			dbReader = new DatabaseReader(db);
			this.createTables(tables);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the connection to the database.
	 * 
	 * @return returns true if the connection is closed successfully, false otherwise.
	 */
	public boolean closeDBConnection() {
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
	
	public DatabaseReader getDBReader() {
		return this.dbReader;
	}
}
