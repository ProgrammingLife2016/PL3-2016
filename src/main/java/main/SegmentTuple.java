package main;

/**
 * 
 * Tuple to insert into the Segment table.
 *
 */
public class SegmentTuple extends Tuple {
	
	private int id;
	private String content;
	
	public SegmentTuple(int id, String content) {
		this.id = id;
		this.content = content;
		this.table = new SegmentTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + id + ",\'" + content + "\')";
	}

}
