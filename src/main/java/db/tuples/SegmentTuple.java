package db.tuples;

import db.tables.SegmentTable;

/**
 * 
 * Tuple to insert into the Segment table.
 *
 */
public class SegmentTuple extends Tuple {
	
	private int id;
	private String content;
	private int xcoord;
	private int ycoord;
	
	public SegmentTuple(int id, String content) {
		this.id = id;
		this.content = content;
		this.table = new SegmentTable();
		this.xcoord = 0;
		this.ycoord = 0;
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + id + ",\'" + content + "\'," + xcoord + "," + ycoord + ")";
	}

}
