package main;

/**
 * 
 * Tuple to insert into the Link table.
 *
 */
public class LinkTuple extends Tuple {

	private int fromId;
	private int toId;
	
	public LinkTuple(int fromId, int toId) {
		this.fromId = fromId;
		this.toId = toId;
		this.table = new LinkTable();
	}

	@Override
	protected String getInsertValues() {
		return " VALUES (" + fromId + "," + toId + ")";
	}

}
