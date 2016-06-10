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
	private int genomeId;
	
	public BubbleTuple(int fromId, int toId, int genomeId) {
		this.fromId = fromId;
		this.toId = toId;
		this.genomeId = genomeId;
		this.table = new BubbleTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + fromId + "," + toId + "," + genomeId + ")";
	}

}