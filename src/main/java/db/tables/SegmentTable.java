package db.tables;

/**
 * 
 * Table that contains the ID's and contents of the DNA segments.
 *
 */
public class SegmentTable extends Table {
	
	public SegmentTable() {
		this.name = "SEGMENTS";
		this.names = new String[]{"ID","CONTENT"};
		this.types = new String[]{"INT","CLOB"};
		this.primaryKeyIdx = 0;
	}

}
