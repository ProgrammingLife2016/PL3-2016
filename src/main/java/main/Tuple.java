package main;

/**
 * 
 * Helper class to aid the insertion of tuples in the database. Extending this
 * class and defining the tuple values makes it possible to retrieve an SQL
 * query to insert that tuple into the database.
 *
 */
public abstract class Tuple {
	
	protected Table table;
	
	/**
	 * Returns an SQL INSERT query which can be used to insert the tuple in a
	 * database.
	 * 
	 * @return an SQL INSERT query which can be used to insert the tuple in a
	 *         database.
	 */
	public String getInsertQuery() {
		return "INSERT INTO " + table.name + this.getInsertValues();
	}
	
	/**
	 * Returns the tuple values in SQL format ("VALUES (VAL1,VAL2,...)).
	 * 
	 * @return The tuple values in SQL format ("VALUES (VAL1,VAL2,...)).
	 */
	protected abstract String getInsertValues();
}
