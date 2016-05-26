package db.tuples;

import db.tables.BubbleTable;

/**
 * 
 * Tuple to insert into the Link table.
 *
 */
public class BubbleTuple extends Tuple {

	private int fromId;
	private int toId;
	
	public BubbleTuple(int fromId, int toId) {
		this.fromId = fromId;
		this.toId = toId;
		this.table = new BubbleTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + fromId + "," + toId + ")";
	}

}