package main;

/**
 * Helper class to aid the creation of tables in the database. Extending this
 * class and defining the table name, its attributes and (possibly) the primary
 * key makes it possible to retrieve an SQL query to create that table.
 */
public abstract class Table {
	protected String name;
	protected String[] names;
	protected String[] types;
	protected int primaryKeyIdx = -1;
	
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the SQL query that needs to be executed in order to create this
	 * table.
	 * 
	 * @return the SQL query that needs to be executed in order to create this
	 *         table.
	 */
	public String getCreateQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + name + " (");
		for (int i = 0; i < names.length; i++) {
			sb.append(names[i] + " " + types[i] + " " + this.checkPrimaryKey(i) + ",");
		}
		sb.delete(sb.length()-2, sb.length());
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Checks if the attribute with the given index is a primary key.
	 * 
	 * @param idx Index to check.
	 * @return The String "PRIMARY KEY" if the index contains a primary key, an
	 *         empty String otherwise.
	 */
	private String checkPrimaryKey(int idx) {
		if(idx == primaryKeyIdx) {
			return "PRIMARY KEY";
		}
		return "";
	}
	
}
