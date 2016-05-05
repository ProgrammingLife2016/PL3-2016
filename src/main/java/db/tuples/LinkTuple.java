package db.tuples;

import db.tables.LinkTable;

/**
 * 
 * Tuple to insert into the Link table.
 *
 */
public class LinkTuple extends Tuple {

	private int fromId;
	private int toId;
	private int count;
	
	public LinkTuple(int fromId, int toId) {
		this.fromId = fromId;
		this.toId = toId;
		this.count = 0;
		this.table = new LinkTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + fromId + "," + toId + "," + count + ")";
	}

}
