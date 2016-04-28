package db.tuples;

import db.tables.GenomeTable;

/**
 * 
 * Tuple to insert into the Genome table.
 *
 */
public class GenomeTuple extends Tuple {

	private int id;
	private String name;
	
	public GenomeTuple(int id, String name) {
		this.id = id;
		this.name = name;
		this.table = new GenomeTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + id + ",\'" + name + "\')";
	}

}
