package db.tables;

/**
 * 
 * Table that contains the ID's and contents of the DNA segments.
 *
 */
public class SegmentTable extends Table {
	
	public SegmentTable() {
		this.name = "SEGMENTS";
		this.names = new String[]{"ID","CONTENT", "XCOORD", "YCOORD"};
		this.types = new String[]{"INT","CLOB", "INT", "INT"};
		this.primaryKeyIdx = 0;
	}

}
